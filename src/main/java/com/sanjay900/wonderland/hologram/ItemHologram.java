package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;

import com.sanjay900.wonderland.Wonderland;

public class ItemHologram extends BlockHologram{
	public int itemId;
	public byte itemData;
	public ItemHologram(Wonderland plugin, Location location, int id, byte data) {
		super(plugin, location, "166");
		// TODO Auto-generated constructor stub
	}

}
