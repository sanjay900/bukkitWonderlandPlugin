package com.sanjay900.wonderland.entities;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.sanjay900.puzzleapi.api.Plot.PlotStatus;
import com.sanjay900.puzzleapi.api.PlotObject;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.hologram.Barrel;
import com.sanjay900.wonderland.hologram.BlockHologram;
import com.sanjay900.wonderland.hologram.Boulder;
import com.sanjay900.wonderland.hologram.Box;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.hologram.Reflector;
import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sanjay900.wonderland.plots.Plot;
import com.sanjay900.wonderland.utils.Utils;

import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

@Getter
public abstract class WonderlandEntity extends PlotObject implements Runnable {
	protected Wonderland plugin;
	protected Location loc;
	protected Location spawnLoc;
	protected NPC npc;
	protected Plot plot;
	private BukkitTask task;
	public WonderlandEntity(Wonderland plugin, Location loc) {
		super(true);
		this.plugin = plugin;
		this.loc = loc.clone();
		this.spawnLoc = loc.clone();
		plot = plugin.plotManager.getPlot(loc);
		plot.addObject(this);
	}

	public void spawn(){
		npc.spawn(spawnLoc);
		loc = spawnLoc.clone();
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,this, 0l, 5l);
	}
	public void respawn() {
		if (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
			Bukkit.getScheduler().cancelTask(task.getTaskId());
		}
		spawn();
	}
	public void editorMode() {
		Bukkit.getScheduler().cancelTask(task.getTaskId());
		npc.teleport(spawnLoc, TeleportCause.PLUGIN);
		loc = spawnLoc.clone();
	}
	public void despawn() {
		Bukkit.getScheduler().cancelTask(task.getTaskId());
		npc.teleport(spawnLoc, TeleportCause.PLUGIN);
		npc.despawn();
		loc = spawnLoc.clone();

	}
	public Hologram getHologram(Block oldBlock) {
		for (Hologram h: plugin.hologramManager.holograms.values()) {
			if (!(h instanceof BlockHologram) && Utils.compareLocation(h.location.getBlock().getLocation(), oldBlock.getLocation())) {
				return h;
			}
		}
		return null;
	}
	public boolean checkItem(Location loc) {
		for (Entity e: loc.getChunk().getEntities()) {
			if (e instanceof Item && e.getLocation().getBlockX() == loc.getBlockX() && e.getLocation().getBlockY() == loc.getBlockY() && e.getLocation().getBlockZ() == loc.getBlockZ())
				return true;
		}
		return false;
	}
	public abstract void tickEntity();

	@Override
	public void run() {
		if (plot.getStatus() == PlotStatus.STARTED && npc.isSpawned()) {
			tickEntity();
		}
	}

	public void moveNPC(Vector v) {
		Iterator<PlotObject> it = plot.getObjects().iterator();
		while (it.hasNext()) {
			PlotObject c = it.next();
			if (c!=this && Utils.compareLocation(c.getLocation().getBlock().getLocation(), loc.getBlock().getLocation().add(v))){
				return;
			}
		}
		for (WonderlandPlayer p : plugin.playerManager.players) {
			if (Utils.compareLocation(p.getPlayer().getLocation().getBlock().getLocation(), loc.getBlock().getLocation().add(v))){
				p.reSpawn();
				return;
			}
		}
		loc = loc.getBlock().getLocation().add(v).getBlock().getLocation();
		loc.setDirection(v);
		npc.faceLocation(loc.clone().add(v));
		npc.getNavigator().cancelNavigation();
		npc.getNavigator().setTarget(loc.clone().getBlock().getLocation().add(0.5,0,0.5));
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

			@Override
			public void run() {
				if (npc.isSpawned())
					npc.getEntity().teleport(loc.clone().getBlock().getLocation().add(0.5,0,0.5));
			}}, 3L);
	}

	public boolean moveEvent(Location l) {
		final Block under = l.getBlock().getRelative(BlockFace.DOWN);
		if (Utils.getConveyor(under) != null) return false;
		if (Utils.getStillConveyor(under)) return false;
		if (Utils.snow(under, BlockFace.NORTH) != null) return false;
		Boolean isTeleport = false;
		Block teleport = under.getRelative(BlockFace.DOWN);
		isTeleport = teleport.getType().name().contains("SIGN");
		if (!isTeleport) {
			teleport = teleport.getRelative(BlockFace.DOWN);
			isTeleport = teleport.getType().name().contains("SIGN");
		}

		if (isTeleport) {
			Sign s = (Sign) teleport.getState();
			if (s.getLine(0).equals("teleport")||s.getLine(0).equals("bridge")) {
				return false;
			}
		}
		Block oldBlock = l.getBlock();
		final Hologram h = getHologram(oldBlock);

		if ((h!=null&&!(oldBlock.getType() == Material.WOODEN_DOOR))) {
			if (oldBlock.getType() == Material.AIR) {

				if (h instanceof Boulder||h instanceof Box||h instanceof Reflector||h instanceof Barrel) {
					return false;	

				}  else {
					return true;
				}
			}else {
				return true;
			}
		}
		switch (under.getType()) {
		case WATER:
		case STATIONARY_WATER:
		case LAVA:
		case STATIONARY_LAVA:
			return false;
		default:
			break;
		}

		if (l.getBlock().getType().isSolid()) return false;
		return true;
	}

	public void remove() {
		despawn();
		List<PlotObject> entities = plot.getObjects();
		entities.remove(this);
		plot.setObjects(entities);
		npc.destroy();

	}



}
