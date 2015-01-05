package com.sanjay900.wonderland.hologram;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.sanjay900.nmsUtil.EntityCubeImpl;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.utils.Utils;

public class Hologram {
	
	public Location location;
	public Location slocation;
	public EntityCubeImpl hologram;
	public Wonderland plugin;
	public int id;
	protected int data;
	protected HologramType type;
	public Hologram (Wonderland plugin, Location location, int id, int data, HologramType type) {
		this.type = type;
		this.id = id;
		this.data = data;
		this.plugin = plugin;
		location=location.getBlock().getLocation().add(0.5, 0, 0.5);
		this.location = location;
		this.slocation = location.clone();
		spawn();
	}
	public void despawn() {
		hologram.getBukkitEntity().remove();
	}
	public void remove() {
		hologram.getBukkitEntity().remove();
		plugin.hologramManager.removeHologram(this);
	}
	public void move(Vector vector) {
		location = location.add(vector);
		hologram.getBukkitEntity().teleport(location);
		
	}
	
	public void setVelocity(final Vector multiply) {
		hologram.getBukkitEntity().setVelocity(multiply);
	}
	public Hologram getHologram(Block oldBlock) {
		for (Hologram h: plugin.hologramManager.holograms.values()) {
			if (!(h instanceof BlockHologram) && Utils.compareLocation(h.location.getBlock().getLocation(), oldBlock.getLocation())) {
				return h;
			}
		}
		return null;
	}
	public void teleport(Location location2) {
		location = location2.clone();
		hologram.getBukkitEntity().teleport(location);
		
	}
	public void reset() {
		this.despawn();
		location = slocation.clone();
			this.spawn();
		if (this instanceof Button) {
			((Button)this).setPushed(false);
		}
	}
	public void spawn() {
		HashMap<String,Object> storedData = new HashMap<>();
		storedData.put("hologramType", this.type);
		storedData.put("hologram", this);
		hologram = plugin.nmsutils.createCube(location, id, data, storedData);
	}
	public void explode() {
		if (this instanceof Barrel) {
			((Barrel)this).detonate();
		} else {
			this.despawn();
			location.getWorld().createExplosion(location, 0.2f);
		}
	}
	public int getId() {
		return id;
	}
	public static enum HologramType {
		Barrel,BlockHologram,Boulder,Box,Button,Electro,ItemHologram,Reflector,Spike,Star,Tunnel
	}

}
