package com.sanjay900.wonderland.hologram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Location;

import com.sanjay900.wonderland.Wonderland;

public class Electro extends Hologram{
	@Getter @Setter
	private List<Integer> time = new ArrayList<>();
	@Getter @Setter
	private boolean spawned = true;
	public Electro(Wonderland plugin, Location location, Integer... list) {
		super(plugin, location, 171,3, HologramType.Electro);
		this.time.addAll(Arrays.asList(list));
	}
	public Electro(Wonderland plugin, Location location, List<Integer> list) {
		super(plugin, location, 171,3, HologramType.Electro);
		this.time = list;
	}
}
