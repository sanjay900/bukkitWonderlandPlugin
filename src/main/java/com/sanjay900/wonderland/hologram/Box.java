package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;

import com.sanjay900.wonderland.Wonderland;

public class Box extends Hologram{
	public BoxType type;
	public Box(Wonderland plugin, Location location, BoxType type) {
		super(plugin, location, type==BoxType.WOODEN?5:42,3, HologramType.Box);
		this.type = type;
	}
	public enum BoxType {
		IRON,WOODEN
	}

}
