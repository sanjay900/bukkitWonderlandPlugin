package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;

import com.sanjay900.wonderland.Wonderland;

public class Boulder extends Hologram{

	public Boulder(Wonderland plugin, Location location) {
		super(plugin, location, 79, 0, HologramType.Boulder);
	}
	@Override
	public void spawn() {
		super.spawn();
		hologram.setStoredData("friction", false);
	}
}
