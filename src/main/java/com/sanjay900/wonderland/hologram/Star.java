package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;

import com.sanjay900.puzzleapi.api.PlotLevelEnd;
import com.sanjay900.wonderland.Wonderland;

public class Star extends ItemHologram implements PlotLevelEnd {
	public String destination = "";
	public Star(Wonderland plugin, Location location, String destination) {
		super(plugin, location, 399,(byte) 0);
		this.destination = destination;
	}

}
