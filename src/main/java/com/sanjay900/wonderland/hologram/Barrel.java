package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.sanjay900.wonderland.Wonderland;

public class Barrel extends Hologram{

	public Barrel(Wonderland plugin, Location location) {
		super(location, 46,0, HologramType.Barrel);
		}

	public void detonate() {
		this.despawn();
		location.getWorld().createExplosion(location, 0.2f);
		BlockFace[] faces = new BlockFace[]{BlockFace.NORTH,BlockFace.SOUTH,BlockFace.EAST,BlockFace.WEST};
		for (BlockFace face : faces) {
			Block relative = location.getBlock().getRelative(face);
			Hologram h = getHologram(relative);
			if (h!= null && h instanceof Barrel) {
				((Barrel)h).detonate();
			} else if(h != null){
				if (h instanceof Box || h instanceof Boulder || h instanceof Reflector) {
					h.despawn();
					location.getWorld().createExplosion(relative.getLocation(), 0.2f);
				}
			}
		}
	}

}
