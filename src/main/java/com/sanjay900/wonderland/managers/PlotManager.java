package com.sanjay900.wonderland.managers;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sanjay900.wonderland.plots.Plot;
import com.sanjay900.wonderland.plots.Plot.PlotLocation;
import com.sanjay900.wonderland.plots.WonderlandChunkGen;
import com.sanjay900.wonderland.plots.Plot.PlotType;

public class PlotManager extends ConfigManager{
	public ArrayList<Plot> plots = new ArrayList<>();
	public PlotManager(Wonderland plugin) {
		super("plots.yml",plugin);
	}
	public ArrayList<Plot> getOwnedPlots(Player pl) {
		ArrayList<Plot> plots2 = new ArrayList<>();
		for (Plot p : plots) {
			if (p.getOwner() != null && p.getOwner().compareTo(pl.getUniqueId()) ==0) {
				plots2.add(p);
			}
		}
		return plots2;
	}
	public ArrayList<Plot> getHelperPlots(Player pl) {
		ArrayList<Plot> plots2 = new ArrayList<>();
		for (Plot p : plots) {
			if (p.getHelpers().contains(pl.getUniqueId())) {
				plots2.add(p);
			}
		}
		return plots2;
	}
	public Plot getPlot(WonderlandPlayer pl) {
		for (Plot p : plots) {
			if (p.getPlayers().contains(pl)) return p;
		}
		return null;
	}
	public Plot getPlot(int coordX, int coordZ, World w) {
		for (Plot p : plots) {
			if (p.getCoordX() == coordX && p.getCoordZ() == coordZ) {
				return p;
			}
		}
		Plot p = new Plot(coordX, coordZ, w);
		addPlot(p);
		return p;
	}
	public Plot getPlot(Location loc) {
		if (!(loc.getWorld().getGenerator() instanceof WonderlandChunkGen)) return null;
		for (Plot p : plots) {
			if (p.hasLocation(loc) != PlotLocation.NONE) {
				return p;
			}
		}
		Plot p = new Plot(loc);
		addPlot(p);
		return p;
	}
	public Plot getPlotWall(Location loc) {
		if (!(loc.getWorld().getGenerator() instanceof WonderlandChunkGen)) return null;
		if (getPlot(loc).hasLocation(loc)==PlotLocation.WALL) {
			return getPlot(loc);
		}
			
		return null;
	}
	public Plot getPlotInside(Location loc) {
		if (!(loc.getWorld().getGenerator() instanceof WonderlandChunkGen)) return null;
		if (getPlot(loc).hasLocation(loc)==PlotLocation.INPLOT) {
			return getPlot(loc);
		}
			
		return null;
	}
	public void addPlot(Plot p) {
		for (Plot p2 : plots) {
			if (p2.getCoordX() == p.getCoordX() && p2.getCoordZ() == p.getCoordZ()) {
				return;
			}
		}
			plots.add(p);
		
	}

	
	
	public void loadConfig() {
		if (!config.contains("plot")) return;
		for (String world: config.getConfigurationSection("plot").getKeys(false)) {
			if (Bukkit.getWorld(world) == null || !(Bukkit.getWorld(world).getGenerator() instanceof WonderlandChunkGen)) {
				System.out.println(world);
				continue;
			}
			for (String id: config.getConfigurationSection("plot."+world).getKeys(false)) {
				Plot p = getPlot(Integer.parseInt(id.split(",")[0]),Integer.parseInt(id.split(",")[1]),Bukkit.getWorld(world));
				String path = "plot."+world+"."+id;
				p.setOwner(UUID.fromString(config.getString(path+"."+"owner")));
				p.setPlayerCount(config.getInt(path+"."+"players"));
				p.setStartLoc(config.getVector(path+"."+"startLoc1").toLocation(Bukkit.getWorld(world)),1);
				p.setStartLoc(config.getVector(path+"."+"startLoc2").toLocation(Bukkit.getWorld(world)),2);
				p.setStartLoc(config.getVector(path+"."+"startLoc3").toLocation(Bukkit.getWorld(world)),3);
				p.setStartLoc(config.getVector(path+"."+"startLoc4").toLocation(Bukkit.getWorld(world)),4);
				p.setType(PlotType.valueOf(config.getString(path+"."+"type")));
				p.setTitle(config.getString(path+"."+"title"));
				p.setSubTitle(config.getString(path+"."+"subtitle"));
				p.setStar(config.getString(path+"."+"stardest"), config.getVector(path+"."+"starloc").toLocation(Bukkit.getWorld(world)));
				if (config.contains(path+"."+"helpers")) {
					p.setHelpers(config.getStringList(path+"."+"helpers"));
				}
			}
		}
	}
	public void savePlot(Plot plot) {
		String world = plot.getWorld().getName();
		String id = plot.getCoordX()+","+plot.getCoordZ();
		String path = "plot."+world+"."+id;
		config.set(path+"."+"players", plot.getPlayerCount());
		config.set(path+"."+"owner", plot.getOwner().toString());
		config.set(path+"."+"startLoc1", plot.getStartLoc()[0].toVector());
		config.set(path+"."+"startLoc2", plot.getPlayerCount() >=2?plot.getStartLoc()[1].toVector():plot.getStartLoc()[0].toVector());
		config.set(path+"."+"startLoc3", plot.getPlayerCount() >=3?plot.getStartLoc()[2].toVector():plot.getStartLoc()[0].toVector());
		config.set(path+"."+"startLoc4", plot.getPlayerCount() ==4?plot.getStartLoc()[3].toVector():plot.getStartLoc()[0].toVector());
		config.set(path+"."+"type", plot.getType().name());
		config.set(path+"."+"title", plot.getTitle());
		config.set(path+"."+"subtitle", plot.getSubTitle());
		config.set(path+"."+"stardest", plot.getStar().destination);
		config.set(path+"."+"starloc", plot.getStar().location.toVector());
		config.set(path+"."+"helpers", plot.getHelpersString());
		saveConfig();
	}
	


}
