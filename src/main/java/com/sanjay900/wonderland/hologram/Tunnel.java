package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;
import org.bukkit.material.MaterialData;

import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.plots.Plot.PlotType;

public class Tunnel extends BlockHologram{

	public PlotType type;
	public Tunnel(Wonderland plugin, Location location, PlotType type) {
		super(plugin, location, getId(type), HologramType.Tunnel);
		this.type = type;
	}
	public void setType(PlotType type) {
		this.type = type;
		this.setID(getId(type));
	}
	public static String getId(PlotType type) {
		switch (type) {
		case AZTEC:
			return "24:1";
		case CAVE:
			return "112:0";
		case GARDEN:
			return "18:1";
		case SPOOKY:
			return "98:0";
		case WOOD:
			return "5:5";
		default: //Shouldn't happen
			return null;
		}
	}
	public static PlotType getType(MaterialData md) {
		if (md.getItemTypeId() == 24 && md.getData() == (byte)1) return PlotType.AZTEC;
		if (md.getItemTypeId() == 112 && md.getData() == (byte)0) return PlotType.CAVE;
		if (md.getItemTypeId() == 18 && md.getData() == (byte)1) return PlotType.GARDEN;
		if (md.getItemTypeId() == 98 && md.getData() == (byte)0) return PlotType.SPOOKY;
		if (md.getItemTypeId() == 5 && md.getData() == (byte)5) return PlotType.WOOD;
		return null;
		
	}
}
