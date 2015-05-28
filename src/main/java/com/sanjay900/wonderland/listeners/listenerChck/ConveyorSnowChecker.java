package com.sanjay900.wonderland.listeners.listenerChck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.sanjay900.nmsUtil.util.FaceUtil;
import com.sanjay900.nmsUtil.util.V10BlockLocation;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.utils.Utils;

import net.citizensnpcs.api.CitizensAPI;

public class ConveyorSnowChecker {
	private BukkitTask task;
	private Wonderland plugin = Wonderland.getInstance();
	private ArrayList<UUID> entities = new ArrayList<>();
	public ConveyorSnowChecker() {
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable(){

			@Override
			public void run() {
				for (World w:Bukkit.getWorlds()) {
					for (final Entity en: w.getEntities()){

						if (CitizensAPI.getNPCRegistry().isNPC(en)) continue;
						Vector v = checkConveyorAndSnow(en);
						if (v != null) {
							entities.add(en.getUniqueId());
							//Players + velocity = hell.
							if (en instanceof Player) {
								plugin.pl.setVelocity((Player) en,v);
								en.teleport(en.getLocation().getBlock().getRelative(FaceUtil.getDirection(v,false)).getLocation().add(0.5, 0, 0.5).setDirection(en.getLocation().getDirection()));
								//TODO: check if item at location and pick up
							}
							else
							{
								en.setVelocity(v.add(FaceUtil.centerExcludeFace(en.getLocation(), FaceUtil.getDirection(v)).multiply(0.5)));
							}
						} else {
							if (entities.contains(en.getUniqueId())) {
								entities.remove(en.getUniqueId());
								en.setVelocity(new Vector(0,0,0).zero());
							}
						}
					}
				}
			}}, 1l, 1l);
	}
	public HashMap<Entity,V10BlockLocation> flipped= new HashMap<>();
	public Vector checkConveyor(Entity en) {
		Block under = en.getLocation().getBlock().getRelative(BlockFace.DOWN);
		final BlockFace Conveyor = Utils.getConveyor(under);
		if (Conveyor != null) {
			return FaceUtil.faceToVector(Conveyor);
		} 
		return null;
	}
	public Vector checkSnow(Entity en) {
		Block under = en.getLocation().getBlock().getRelative(BlockFace.DOWN);
		Vector v = en.getVelocity();
		if (en instanceof Player) {
			v = plugin.pl.getVelocity(en);
			if (v == null)v = en.getVelocity();
		}
		BlockFace snow = Utils.snow(under, FaceUtil.getDirection(v));
		if (snow != null) {
			return FaceUtil.faceToVector(snow);
		}
		return null;
	}
	public Vector checkConveyorAndSnow(Entity en){

		Vector l = checkConveyor(en);
		V10BlockLocation under = new V10BlockLocation(en.getLocation().getBlock().getRelative(BlockFace.DOWN));

		if (en instanceof Player && (!flipped.containsKey(en) || !flipped.get(en).equals(under))) {
			if (flipped.containsKey(en)) flipped.remove(en);
			if (Utils.getConveyor(under.getHandle().getBlock()) !=null || Utils.getStillConveyor(under.getHandle().getBlock())) {
				Utils.flipConveyor(under.getHandle().getBlock());
				flipped.put(en, under);
			}	
		}
		if (l != null) {
			return l;
		}
		return checkSnow(en);
	}
}
