package com.sanjay900.wonderland.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

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

	
	public static BlockFace getConveyor(Block b) {
		switch (b.getTypeId()) {
		case 5:
			if (b.getData() == (byte)2) return BlockFace.EAST;
			return null;
		case 162:
			if (b.getData() == (byte)1) return BlockFace.WEST;
			if (b.getData() == (byte)0) return BlockFace.EAST;
			return null;
		case 17:
			if (b.getData() == (byte)1) return BlockFace.SOUTH;
			if (b.getData() == (byte)0) return BlockFace.NORTH;
			if (b.getData() == (byte)3) return BlockFace.NORTH;
			if (b.getData() == (byte)2) return BlockFace.NORTH;
			return null;
		case 16:
			return BlockFace.WEST;
		case 21:
			return BlockFace.SOUTH;
		default:
			return null;

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
	@SuppressWarnings("deprecation")
	public static void flipConveyor(final Block b, final Wonderland plugin) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			public void run() {
		switch (b.getTypeId()) {
		case 5:
			if (b.getData() == (byte)2) {
						b.setTypeId(16);				
				return;
			}
			break;
		case 16:
			b.setTypeId(5);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					b.setData((byte) 2);
				}
			});
			return;
		case 21:
			b.setTypeId(17);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					b.setData((byte) 3);
				}
			});
			return;
		case 17:
			if (b.getData() == (byte)3)  {
						b.setTypeId(21);
				return;
			}
			if (b.getData() == (byte)11) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 9);
					}
				});
				return;


			}

			if (b.getData() == (byte)9) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 11);
					}
				});
				return;
			}  
			
			if (b.getData() == (byte)7) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 5);
					}
				});
				return;
			}  
			if (b.getData() == (byte)5) {

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 7);


					}
				});
				return;


			}
			return;

		}
		
		}});
		return;
		
	}


	@SuppressWarnings("deprecation")
	public static void switchConveyor(final Block b, final Wonderland plugin) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			public void run() {
		switch (b.getTypeId()) {
		case 5:
			if (b.getData() == (byte)2) {				
				b.setType(Material.LOG);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 6);
					}
				});
				return;
			}
			break;
		case 17:
			if (b.getData() == (byte)3)  {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 10);
					}
				});
				return;
			}
			if (b.getData() == (byte)11) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 9);
					}
				});
				return;


			}

			if (b.getData() == (byte)9) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 11);
					}
				});
				return;
			}  
			if (b.getData() == (byte)6) {
				b.setTypeId(5);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 2);
					}
				});
				return;


			}

			if (b.getData() == (byte)10) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 3);
					}
				});
				return;
			}
			if (b.getData() == (byte)7) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 5);
					}
				});
				return;
			}  
			if (b.getData() == (byte)5) {

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 7);


					}
				});
				return;


			}
			return;

		}
		return;
			}});
	}
	@SuppressWarnings("deprecation")
	public static void statictoAnimConveyor(final Block b, final Wonderland plugin) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			public void run() {
		switch (b.getTypeId()) {
		case 162:
			if (b.getData() == (byte)9) {
				b.setTypeId(17);
				b.setData((byte) 2);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setTypeId(17);
						b.setData((byte) 2);
					}
				},1L);
				return;
			}
			if (b.getData() == (byte)5)  {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 1);
					}
				});
				return;
			}
			if (b.getData() == (byte)8) {
				b.setTypeId(17);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 1);
					}
				});
				return;
			}  
			if (b.getData() == (byte)4) {

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 0);


					}
				});
				return;


			}
			return;
		
		case 17:
			if (b.getData() == (byte)5) {
				Bukkit.getScheduler().runTask(plugin, new Runnable() {
					public void run() {
				b.setTypeId(5);
					}});
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 2);
					}
				},1L);
				return;
			}
			if (b.getData() == (byte)9)  {
				b.setTypeId(21);
				return;
			}
			if (b.getData() == (byte)7) {
				b.setTypeId(16);
				return;
			}  
			if (b.getData() == (byte)11) {

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 3);


					}
				});
				return;


			}
			return;

		}
			}});
		return;
			
	}
	@SuppressWarnings("deprecation")
	public static void animToStaticConveyor(final Block b, final Wonderland plugin) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			public void run() {
		switch (b.getTypeId()) {
		case 5:
			if (b.getData() == (byte)2) {
				b.setTypeId(17);
				b.setData((byte) 5);	
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {	
						b.setTypeId(17);
						b.setData((byte) 5);	
					}
				},1L);
				return;
			}
			break;
		case 21:
				b.setTypeId(17);
				b.setData((byte) 9);	
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {	
						b.setTypeId(17);
						b.setData((byte) 9);	
					}
				},1L);
				return;
		case 16:
			b.setTypeId(17);
			b.setData((byte) 7);	
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {	
					b.setTypeId(17);
					b.setData((byte) 7);	
				}
			},1L);
			return;
		case 162:
			
			if (b.getData() == (byte)1)  {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 5);
					}
				});
				return;
			}
			
			
			if (b.getData() == (byte)0) {

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 4);


					}
				});
				return;


			}
			return;
		case 17:
			
			if (b.getData() == (byte)1) {
				b.setTypeId(162);
					
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 8);
					}
				},1L);
				return;
			}
			if (b.getData() == (byte)2) {
				b.setTypeId(162);
				b.setData((byte) 9);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setTypeId(162);
						b.setData((byte) 9);
					}
				});
				return;
			}  
			if (b.getData() == (byte)3) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 11);
					}
				});
				return;


			}
			
			if (b.getData() == (byte)6) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 7);
					}
				});
				return;


			}


			if (b.getData() == (byte)10) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						b.setData((byte) 11);
					}
				});
				return;
			}  
			
			return;

		}
			}});
		return;
	}
	@SuppressWarnings("deprecation")
	public static Boolean getStillConveyor(Block b) {
		switch (b.getTypeId()) {

		case 162:
			if (b.getData() == (byte)8) return true;
			if (b.getData() == (byte)4) return true;
			if (b.getData() == (byte)9) return true;
			if (b.getData() == (byte)5) return true;
			return false;
		case 17:
			if (b.getData() == (byte)11) return true;
			if (b.getData() == (byte)5) return true;
			if (b.getData() == (byte)9) return true;
			if (b.getData() == (byte)7) return true;
			return false;
		default:
			return false;

		}
	}
}

