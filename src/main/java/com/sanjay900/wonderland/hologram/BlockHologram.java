package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;	
import org.bukkit.Material;

import com.sanjay900.wonderland.Wonderland;

public class BlockHologram extends Hologram{
	public BlockHologram(Wonderland plugin, Location location, String id) {
		this(plugin, location, id, HologramType.BlockHologram);
	}
	public BlockHologram(Wonderland plugin, Location location, String id,
			HologramType type) {
		super(location, Integer.valueOf(id.split(":")[0]), id.split(":").length>1?Integer.valueOf(id.split(":")[1]):0, type);
	}
	@SuppressWarnings("deprecation")
	public void setMaterial(Material mt) {
		setItemID(mt.getId());
	}
	public void setID(String text) {
		id = Integer.valueOf(text.split(":")[0]);
		data = text.split(":").length>1?Integer.valueOf(text.split(":")[1]):0;
		super.despawn();
		super.spawn();
		plugin.hologramManager.updateHologram(this);
	}
	public void setItemID(int blockId) {
		id = blockId;
		super.despawn();
		super.spawn();
		plugin.hologramManager.updateHologram(this);
	}
	public void setData(byte data) {
		this.data = data;
		super.despawn();
		super.spawn();
		plugin.hologramManager.updateHologram(this);
	}
}
