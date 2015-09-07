package com.sanjay900.wonderland.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Diode;

import com.sanjay900.wonderland.Wonderland;

public class Utils
{	

	public static List<Block> getNearbyLiquids(Location location, int Radius) {
		List<Block> Blocks = new ArrayList<Block>();
		List<Block> nearby = getNearbyBlocks(location,Radius);
		for (Block b : nearby) {
			if (b.isLiquid()) {
				Blocks.add(b);
			}
		}

		return Blocks;
	}
	public static List<Block> getNearbyBlocks(Location location, int Radius) {
		List<Block> Blocks = new ArrayList<Block>();

		for (int X = location.getBlockX() - Radius; X <= location.getBlockX()
				+ Radius; X++) {
			for (int Y = location.getBlockY() - Radius; Y <= location
					.getBlockY() + Radius; Y++) {
				for (int Z = location.getBlockZ() - Radius; Z <= location
						.getBlockZ() + Radius; Z++) {
					Block block = location.getWorld().getBlockAt(X, Y, Z);
					if (!block.isEmpty()) {
						Blocks.add(block);
					}
				}
			}
		}

		return Blocks;
	}
	public static boolean compareLocation(Location l, Location l2) {
		return (l.getX() == l2.getX())
				&& (l.getY() == l2.getY())
				&& (l.getZ() == l2.getZ());

	}
	public static boolean isConvSnow(Block b) {
		if (getConveyor(b) != null) return true;
		return snow(b);
	}

	public static BlockFace getConveyor(Block b) {
		//Snow is under, most the code revolves around snow.
		b = b.getRelative(BlockFace.UP);
		if (b.getType() == Material.DIODE_BLOCK_OFF||b.getType() == Material.DIODE_BLOCK_ON) {
			Diode d = (Diode) b.getState().getData();
			if (d.getDelay() == 2 || d.getDelay() == 4)
			return d.getFacing();
		}
		return null;
	}
	public static boolean snow(final Block b) {
		switch (b.getTypeId()) {
		case 80:
		case 15: 
		case 56: 
		case 14: 
		case 129: 
			return true;
		default: return false;
		}

	}
	public static BlockFace snow(final Block b, BlockFace initDir) {
		switch (b.getTypeId()) {
		case 80: return initDir;
		case 15: 
			if (initDir == BlockFace.WEST) return BlockFace.NORTH;
			if (initDir == BlockFace.SOUTH) return BlockFace.EAST;
			return initDir;
		case 56: 
			if (initDir == BlockFace.WEST) return BlockFace.SOUTH;
			if (initDir == BlockFace.NORTH) return BlockFace.EAST;
			return initDir;
		case 14: 
			if (initDir == BlockFace.SOUTH) return BlockFace.WEST;
			if (initDir == BlockFace.EAST) return BlockFace.NORTH;
			return initDir;
		case 129: 
			if (initDir == BlockFace.EAST) return BlockFace.SOUTH;
			if (initDir == BlockFace.NORTH) return BlockFace.WEST;
			return initDir;
		default: return null;
		}

	}
	public static void flipConveyor(Block b) {
		b = b.getRelative(BlockFace.UP);
		if (b.getType() == Material.DIODE_BLOCK_OFF||b.getType() == Material.DIODE_BLOCK_ON) {
			Diode d = (Diode) b.getState().getData();
			if (d.getDelay() < 3) return;
			d.setFacingDirection(d.getFacing().getOppositeFace());
			BlockState bs = b.getState();
			bs.setData(d);
			bs.update();
			b.setData(bs.getRawData());
		}
	}
	public static void statictoAnimConveyor(Block b) {
		b = b.getRelative(BlockFace.UP);
		if (b.getType() == Material.DIODE_BLOCK_OFF||b.getType() == Material.DIODE_BLOCK_ON) {
			Diode d = (Diode) b.getState().getData();
			switch (d.getDelay()) {
			case 1:
				d.setDelay(2);
				break;
			case 3:
				d.setDelay(4);
				break;
			}
			BlockState bs = b.getState();
			bs.setData(d);
			bs.update();
			b.setData(bs.getRawData());
		}

	}
	public static void animToStaticConveyor(Block b) {
		b = b.getRelative(BlockFace.UP);
		if (b.getType() == Material.DIODE_BLOCK_OFF||b.getType() == Material.DIODE_BLOCK_ON) {
			Diode d = (Diode) b.getState().getData();
			switch (d.getDelay()) {
			case 2:
				d.setDelay(1);
				break;
			case 4:
				d.setDelay(3);
				break;
			}
			BlockState bs = b.getState();
			bs.setData(d);
			bs.update();
			b.setData(bs.getRawData());
		}
	}
	public static Boolean getStillConveyor(Block b) {
		b = b.getRelative(BlockFace.UP);
		if (b.getType() == Material.DIODE_BLOCK_OFF||b.getType() == Material.DIODE_BLOCK_ON) {
			Diode d = (Diode) b.getState().getData();
			switch (d.getDelay()) {
			case 1:
			case 3:
				return true;
			}
		}
		return false;
	}
}

