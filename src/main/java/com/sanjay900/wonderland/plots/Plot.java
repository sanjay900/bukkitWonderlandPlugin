package com.sanjay900.wonderland.plots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.Scoreboard;

import com.sanjay900.puzzleapi.api.AbstractPlayer;
import com.sanjay900.puzzleapi.api.PlotObject;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.hologram.ItemHologram;
import com.sanjay900.wonderland.hologram.Star;
import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
@Getter
@Setter
public class Plot extends com.sanjay900.puzzleapi.api.Plot{
	public Plot(int coordX, int coordZ, World world) {
		super(coordX, coordZ, world);
	}
	public Plot(Location loc) {
		super(loc);
	}
	private Wonderland plugin = Wonderland.getInstance();
	private int collectedKeys;
	private int collectedBonuses;
	private Scoreboard board;
	public HashMap<Fireball, Block> fireballs = new HashMap<>();
	
	public void setPlayerCount() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		Location corner = new Location(world,coordX*getWidth()*16+10,3,coordX*getWidth()*16+10);
		for (int i = 1; i <=4;i++) {
			if (i <= playerCount) {
			this.startLoc[i-1]=corner.clone();
			NPC wolf = registry.createNPC(EntityType.PLAYER, "Player "+i+" Spawn");
			wolf.spawn(corner);
			npcs[i-1] = wolf;
			} else {
				this.startLoc[i-1] = null;
				this.npcs[i-1] = null;
			}
		}
	}
	public void fireCannons() {
		CuboidRegion r = new CuboidRegion(new Vector((getChunkX()*16)+7,3,(getChunkZ()*16)+7),new Vector((getChunkX()*16)+getGenerator().size-1,getGenerator().height-1,(getChunkZ()*16)+getGenerator().size-1));
		for (int x =r.getMinimumPoint().getBlockX(); x< r.getMaximumPoint().getBlockX(); x++) {
			for (int y =r.getMinimumPoint().getBlockY(); y< r.getMaximumPoint().getBlockY(); y++) {
				for (int z =r.getMinimumPoint().getBlockZ(); z< r.getMaximumPoint().getBlockZ(); z++) {
					Block b = world.getBlockAt(x, y, z);
					if (b.getType() == Material.DISPENSER) {
						fireballs.put(plugin.entityManager.fireCannon(b),b);
					}
				}
			}
		}

	}
	public void setPlayerCount(int playerCount) {
		startLoc = new Location[playerCount];
		npcs = new NPC[playerCount];
		Location corner = new Location(world,coordX*getWidth()*16+10,3,coordX*getWidth()*16+10);
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		for (int i = 0; i <playerCount;i++) {
			this.startLoc[i]=corner.clone();
			NPC wolf = registry.createNPC(EntityType.PLAYER, "Player "+(i+1)+" Spawn");
			wolf.spawn(corner);
			npcs[i] = wolf;
		}
		this.playerCount = playerCount;
	}
	public int getKeys() {
		return objects.stream().filter(h -> h instanceof ItemHologram).filter(h -> ((ItemHologram)h).itemId == 264).collect(Collectors.toList()).size();
	}
	public int getBonuses() {
		return objects.stream().filter(h -> h instanceof ItemHologram).filter(h -> ((ItemHologram)h).itemId == 266).collect(Collectors.toList()).size();
	}
	public void setStar(String s, Location loc) {
		if (end != null) {
			((Star)end).remove();
		}
		this.end = new Star(plugin,loc,s);
	}

	
	@SuppressWarnings("deprecation")
	public void replaceBlocks(MaterialData orig, MaterialData replace) {
		EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), 4096);
		CuboidRegion r = new CuboidRegion(new Vector((getChunkX()*16)+7,3,(getChunkZ()*16)+7),new Vector((getChunkX()*16)+getGenerator().size-1,getGenerator().height-1,(getChunkZ()*16)+getGenerator().size-1));
		HashSet<BaseBlock> mask = new HashSet<>();
		mask.add(new BaseBlock(orig.getItemTypeId(),orig.getData()));
		try {
			es.replaceBlocks(r, mask, new BaseBlock(replace.getItemTypeId(),replace.getData()));
		} catch (MaxChangedBlocksException e) {
		}

	}
	
	@Override
	public AbstractPlayer createPlayer(Player pl, int i) {
		return new WonderlandPlayer(pl,i);
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}


}
