package com.sanjay900.wonderland.listeners.listenerChck;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.sanjay900.nmsUtil.util.V10BlockLocation;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.utils.FaceUtil;
import com.sanjay900.wonderland.utils.Utils;

public class ConveyorSnowChecker {
	private BukkitTask task;
	private Wonderland plugin = Wonderland.getInstance();

	public ConveyorSnowChecker() {
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable(){

			@Override
			public void run() {
				for (World w:Bukkit.getWorlds()) {
					for (Entity en: w.getEntities()){
						Vector v = checkConveyorAndSnow(en);
						if (v != null) {
							en.setVelocity(v.multiply(0.3));
							if (en instanceof Player) {
								((Player) en).setWalkSpeed(0);
							}
						} else {
							if (en instanceof Player) {
								((Player) en).setWalkSpeed((float) 0.2);
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
			v = plugin.nmsutils.calcPlayerVelocity((Player) en);
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
		if (l != null) {
			return l;
		}
		if (en instanceof Player && (!flipped.containsKey(en) || !flipped.get(en).equals(under))) {
			if (flipped.containsKey(en)) flipped.remove(en);
			if (Utils.getConveyor(under.getHandle().getBlock()) !=null || Utils.getStillConveyor(under.getHandle().getBlock())) {
				Utils.flipConveyor(under.getHandle().getBlock(),plugin);
				flipped.put(en, under);
			}	
		}

		return checkSnow(en);
	}
}
