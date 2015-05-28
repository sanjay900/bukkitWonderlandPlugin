package com.sanjay900.wonderland.entities;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;

import com.sanjay900.nmsUtil.util.FaceUtil;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.hologram.Barrel;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sanjay900.wonderland.utils.Utils;

public class Bomb extends WonderlandEntity {
	BlockFace direction;
	private BlockFace startDir;
	public Bomb(Wonderland plugin, Location loc, BlockFace direction) {
		super(plugin,loc);
		this.direction = this.setStartDir(direction);
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		this.npc = registry.createNPC(EntityType.CREEPER, "");
		npc.spawn(loc);
		npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
	}
	public Bomb(Wonderland plugin, Location loc) {
		super(plugin,loc);
		this.direction = BlockFace.NORTH;
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		this.npc = registry.createNPC(EntityType.CREEPER, "");
		npc.spawn(loc);
		npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
	}
	@Override
	public void tickEntity() {
		//Explode if surrounded
		BlockFace[] faces = new BlockFace[]{BlockFace.NORTH,BlockFace.SOUTH,BlockFace.EAST,BlockFace.WEST};
		boolean isSolid = true;
		for (BlockFace face : faces) {
			if (!loc.getBlock().getRelative(face).getType().isSolid()) {
				isSolid = false;
			}
		}
		if (isSolid) {
			for (BlockFace face : faces) {
				checkExplode(loc.getBlock().getRelative(face).getLocation());
			}
			despawn();
			return;
		}
		Location to = loc.getBlock().getRelative(direction).getLocation();
		if (moveEvent(to)&&!checkExplode(to)) {
			moveNPC(FaceUtil.faceToVector(direction));
		}else {
			to = loc.getBlock().getLocation().add(FaceUtil.faceToVector(FaceUtil.rotate(direction,2))).getBlock().getLocation();
			if (moveEvent(to)&&!checkExplode(to)) {
				for (WonderlandPlayer p: plot.getPlayers()) {
					if (Utils.compareLocation(p.getPlayer().getLocation().getBlock().getLocation(),to)) {
						p.reSpawn();
					}
				}
				moveNPC(FaceUtil.faceToVector(FaceUtil.rotate(direction,2)));
			}
		}
	}
	private boolean checkExplode(Location to) {
		Boolean explode = false;
		Hologram h = getHologram(to.getBlock());
		if (h!= null && h instanceof Barrel) {
			((Barrel)h).detonate();
			despawn();
			explode = true;
		}
		for (WonderlandPlayer p: plot.getPlayers()) {
			if (Utils.compareLocation(p.getPlayer().getLocation().getBlock().getLocation(),to)) {
				p.reSpawn();
				explode = true;
			}
		}
		return explode;
	}
	public BlockFace getStartDir() {
		return startDir;
	}
	public BlockFace setStartDir(BlockFace startDir) {
		this.startDir = startDir;
		return startDir;
	}

}
