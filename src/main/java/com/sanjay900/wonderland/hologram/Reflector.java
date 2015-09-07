package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;
import com.sanjay900.wonderland.Wonderland;

public class Reflector extends Hologram{
	public ReflectorType type;
	public Reflector(Wonderland plugin, Location location, ReflectorType type) {
		super(location,95, getData(type), HologramType.Reflector);
		this.type = type;
	}
	
	private static byte getData(ReflectorType type) {
		switch (type) {
		case LEFT:
			return 7;
		case RIGHT:
			return 8;
		case PRISM:
			return 3;
		default:
			return 0;
		}
	}
	public static ReflectorType getType(byte data) {
		switch (data) {
		case 7:
			return ReflectorType.LEFT;
		case 8:
			return ReflectorType.RIGHT;
		case 3:
			return ReflectorType.PRISM;
		default:
			return null;
		}
	}
	public enum ReflectorType {
		LEFT,RIGHT,PRISM
	}

}
