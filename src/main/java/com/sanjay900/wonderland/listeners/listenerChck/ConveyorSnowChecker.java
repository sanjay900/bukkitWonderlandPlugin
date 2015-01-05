package com.sanjay900.wonderland.listeners.listenerChck;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sanjay900.wonderland.utils.FaceUtil;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.utils.Utils;

public class ConveyorSnowChecker {
	public HashMap<WonderlandPlayer,Block> flipped= new HashMap<>();
	public Location checkConveyor(final Block under, final WonderlandPlayer p, final Wonderland plugin) {
		final BlockFace Conveyor = Utils.getConveyor(under);
		if (Conveyor != null) {
			Utils.flipConveyor(under,plugin);
			Location l = p.getPlayer().getLocation();
			l.setX(under.getRelative(Conveyor).getRelative(BlockFace.UP).getX()+0.5);
			l.setZ(under.getRelative(Conveyor).getRelative(BlockFace.UP).getZ()+0.5);
			l.setDirection(FaceUtil.faceToVector(Conveyor));
			return l;
			
		} 
		return null;
	}
	public Location checkSnow(final Block under, final WonderlandPlayer p, Vector v) {
		BlockFace snow = Utils.snow(under, FaceUtil.getDirection(v));
		if (snow != null) {
			
			Location l = under.getRelative(snow).getRelative(BlockFace.UP).getLocation();
			l.setDirection(FaceUtil.faceToVector(snow));
			if ((l.getBlock().getType().isSolid()&&!l.getBlock().getType().name().toLowerCase().contains("stair")&&!l.getBlock().getType().name().contains("SIGN"))||l.getBlock().getType()==Material.FLOWER_POT||l.getBlock().getType()==Material.LADDER)  {
				l = p.getPlayer().getLocation();
				l.setDirection(FaceUtil.faceToVector(FaceUtil.getDirection(v).getOppositeFace()));
				return l;
			}
			l = l.add(0, l.getBlock().getType().name().toLowerCase().contains("stair")?1:0, 0);
			l.setDirection(FaceUtil.faceToVector(snow));
				return l;
				}
				
		return null;
			
		
	}
	public Location checkConveyorAndSnow(Block under, WonderlandPlayer p, Vector v, Wonderland plugin){
		Location l = checkConveyor(under, p,plugin );
		if (l != null) {
			return l;
		
		}
			if (!flipped.containsKey(p) || !Utils.compareLocation(flipped.get(p).getLocation(), under.getLocation())) {

				if (flipped.containsKey(p)) flipped.remove(p);
				if (Utils.getConveyor(under) !=null || Utils.getStillConveyor(under)) {
					Utils.flipConveyor(under,plugin);
					flipped.put(p, under);
				}	
			}
			
			return checkSnow(under, p, v);
	}
}
