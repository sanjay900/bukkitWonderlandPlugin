package com.sanjay900.wonderland.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import com.sanjay900.nmsUtil.fallingblocks.FrozenSand;
import com.sanjay900.wonderland.hologram.Barrel;
import com.sanjay900.wonderland.hologram.BlockHologram;
import com.sanjay900.wonderland.hologram.Boulder;
import com.sanjay900.wonderland.hologram.Box;
import com.sanjay900.wonderland.hologram.Button;
import com.sanjay900.wonderland.hologram.Button.ButtonColour;
import com.sanjay900.wonderland.hologram.Button.ButtonType;
import com.sanjay900.wonderland.hologram.Electro;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.hologram.Reflector;
import com.sanjay900.wonderland.hologram.Spike;
import com.sanjay900.wonderland.hologram.Tunnel;
import com.sanjay900.wonderland.plots.Plot;
import com.sanjay900.wonderland.plots.Plot.PlotType;
import com.sanjay900.wonderland.plots.WonderlandChunkGen;

public class HologramManager extends ConfigManager{
	public HashMap<String,Hologram> holograms = new HashMap<>();
	public HashMap<FrozenSand,UUID[]> showToPlayer = new HashMap<>();
	private BukkitTask electrotask;
	private int electrocounter = 1;
	private volatile int SHARED_ID = Short.MAX_VALUE;
	public int nextId() {
		int firstId = ++SHARED_ID;
		SHARED_ID += 4;
		return firstId;
	}
	public HologramManager() {
		super("holograms.yml");
		electrotask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable(){

			@Override
			public void run() {

				for (Hologram h: holograms.values()) {
					if (h instanceof Spike) {
						if (electrocounter == 1) {
							if (((Spike)h).getDir() == 2) {
								((Spike)h).setDir(1);
							}
						}
					}
					if (h instanceof Electro) {
						Electro el = (Electro) h;
						if (el.getTime().contains(electrocounter)) {
							el.teleport(el.location);
						} else {
							el.teleport(el.location.clone().add(0, -1, 0));
							el.setSpawned(false); 
						}
					}
				}
				if (electrocounter < 5)
					electrocounter++;
				else
					electrocounter = 1;
			}
		}, 1, 40); 




	}

	public void loadConfig() {

		ArrayList<Hologram> hologramstodel = new ArrayList<>();
		hologramstodel.addAll(holograms.values());
		for (Hologram h: hologramstodel) {
			h.despawn();
		}
		if (this.config.contains("holograms")) {
			for (String key: config.getConfigurationSection("holograms").getKeys(false)) {
				String path = "holograms."+key;
				String world = config.getString(path+"."+"world");
				Location loc = config.getVector(path+"."+"loc").toLocation(Bukkit.getWorld(world));
				if (Bukkit.getWorld(world) == null || !(Bukkit.getWorld(world).getGenerator() instanceof WonderlandChunkGen)) {
					continue;
				}
				String type = config.getString(path+"."+"type");
				Hologram h = null;
				switch (type) {
				case "button":
					ButtonType bt = ButtonType.valueOf(config.getString(path+"."+"shape").toUpperCase());
					ButtonColour bc = ButtonColour.valueOf(config.getString(path+"."+"colour").toUpperCase());
					h = new Button(plugin,bt,bc,loc);
					break;
				case "box":
					h = new Box(plugin,loc,Box.BoxType.valueOf(config.getString(path+"."+"boxtype").toUpperCase()));
					break;
				case "reflector":
					h = new Reflector(plugin,loc,Reflector.ReflectorType.valueOf(config.getString(path+"."+"reflectortype").toUpperCase()));
					break;
				case "boulder":
					h = new Boulder(plugin,loc);
					break;
				case "barrel":
					h = new Barrel(plugin,loc);
					break;	
				case "block":
					h = new BlockHologram(plugin,loc,config.getString(path+"."+"id"));
					break;
				case "tunnel":
					h = new Tunnel(plugin,loc,PlotType.valueOf(config.getString(path+"."+"plotType")));
					break;
				case "spike":
					h = new Spike(plugin,loc,config.getLong(path+"."+"time"));
					break;
				case "electro":
					h = new Electro(plugin,loc,config.getIntegerList(path+"."+"time"));
					break;
				}
				holograms.put(key,h);
				Plot p = plugin.plotManager.getPlot(loc);
				if (p != null) {
					ArrayList<Hologram> holos = p.getHolograms();
					holos.add(h);
					p.setHolograms(holos);
				}
			}
		}
	}

	public void removeHologram(Hologram hologram) {
		String id = null;
		for (Entry<String, Hologram> h : holograms.entrySet()) {
			if (h.getValue() == hologram) {
				id = h.getKey();
			}
		}
		if (id != null) {
			this.config.set("holograms"+"."+id, null);
			holograms.remove(id);
		}
		saveConfig();
		if (hologram instanceof Spike) {
			if (((Spike)hologram).task != null)
			((Spike)hologram).task.cancel();
		}
	}
	public void updateHologram(Hologram hologram) {
		String id = null;
		for (Entry<String, Hologram> h : holograms.entrySet()) {
			if (h.getValue() == hologram) {
				id = h.getKey();
			}
		}
		if (id == null) {
			return;
		}
		saveHologram(hologram,id);
	}
	public void addHologram(Hologram hologram) {

		int id = -1;
		for (Entry<String, Hologram> h : holograms.entrySet()) {
			if (StringUtils.isNumeric(h.getKey())) {
				if (Integer.parseInt(h.getKey()) > id) {
					id = Integer.parseInt(h.getKey());
				}
			}	
		}
		id++;

		saveHologram(hologram,String.valueOf(id));
		holograms.put(String.valueOf(id), hologram);
		ArrayList<Hologram> holos = plugin.plotManager.getPlot(hologram.location).getHolograms();
		holos.add(hologram);
		plugin.plotManager.getPlot(hologram.location).setHolograms(holos);
	}
	public void saveHologram(Hologram hologram, String id) {
		String path = "holograms."+id;
		config.set(path+"."+"loc", hologram.slocation.toVector());
		config.set(path+"."+"world", hologram.slocation.getWorld().getName());
		if (hologram instanceof Barrel) {
			config.set(path+"."+"type", "barrel");
		}
		if (hologram instanceof Reflector) {
			config.set(path+"."+"type", "reflector");
			config.set(path+"."+"reflectortype", ((Reflector)hologram).type.name());
		}
		if (hologram instanceof Box) {
			config.set(path+"."+"type", "box");
			config.set(path+"."+"boxtype", ((Box)hologram).type.name());
		}
		if (hologram instanceof Boulder) {
			config.set(path+"."+"type", "boulder");
		}
		if (hologram instanceof Button) {
			Button bt = (Button)hologram;
			config.set(path+"."+"type", "button");
			config.set(path+"."+"shape", bt.type.name());
			config.set(path+"."+"colour", bt.colour.name());
		}
		if (hologram instanceof Tunnel) {
			config.set(path+"."+"type", "tunnel");
			config.set(path+"."+"plotType", ((Tunnel)hologram).type.name());
		} else
			if (hologram instanceof BlockHologram) {
				config.set(path+"."+"type", "block");
				config.set(path+"."+"id", hologram.getId());
			}
		if (hologram instanceof Spike) {
			config.set(path+"."+"type", "spike");
			config.set(path+"."+"time", ((Spike)hologram).getTime());
		}
		if (hologram instanceof Electro) {
			config.set(path+"."+"type", "electro");
			config.set(path+"."+"time", ((Electro)hologram).getTime());
		}
		saveConfig();

	}



}
