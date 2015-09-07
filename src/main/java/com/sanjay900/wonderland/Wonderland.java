package com.sanjay900.wonderland;

import java.util.ArrayList;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.sanjay900.nmsUtil.NMSUtil;
import com.sanjay900.puzzleapi.PuzzleAPI;
import com.sanjay900.puzzleapi.api.PlotObject;
import com.sanjay900.puzzleapi.api.PlotTypeRegistry;
import com.sanjay900.puzzleapi.worldgen.PlotChunkGenerator;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.listeners.EntityListener;
import com.sanjay900.wonderland.listeners.PlayerListener;
import com.sanjay900.wonderland.listeners.PlotCommand;
import com.sanjay900.wonderland.listeners.WonderCommand;
import com.sanjay900.wonderland.managers.EntityManager;
import com.sanjay900.wonderland.managers.HologramManager;
import com.sanjay900.wonderland.managers.PlayerManager;
import com.sanjay900.wonderland.managers.PlotManager;
import com.sanjay900.wonderland.packets.SoundPacketHandler;
import com.sanjay900.wonderland.plots.Plot;
import com.sanjay900.wonderland.plots.PlotType;

public class Wonderland extends JavaPlugin {

	public PlayerListener pl;
	public EntityListener el;
	public PlayerManager playerManager;
	public HologramManager hologramManager;
	public EntityManager entityManager;
	public PlotManager plotManager;
	public NMSUtil nmsutils;
	@Getter
	public static Wonderland instance;
	
	@Override
	public void onEnable() {
		instance = this;
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				PlotTypeRegistry.registerPlotTypes(PlotType.class);
				nmsutils = (NMSUtil) Bukkit.getPluginManager().getPlugin("nmsUtils");
				NPCRegistry registry = CitizensAPI.getNPCRegistry();
				registry.deregisterAll();
				hologramManager = new HologramManager();
				playerManager = new PlayerManager();
				pl = new PlayerListener();
				Bukkit.getPluginManager().registerEvents(pl, Wonderland.this);
				el = new EntityListener();
				Bukkit.getPluginManager().registerEvents(el, Wonderland.this);
				Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Wonderland.this, "BungeeCord");
				plotManager = new PlotManager();
				plotManager.reloadConfig();
				entityManager = new EntityManager();
				entityManager.reloadConfig();
				hologramManager.reloadConfig();
				getCommand("wl").setExecutor(new WonderCommand());
				getCommand("plot").setExecutor(new PlotCommand());
				ProtocolLibrary.getProtocolManager().addPacketListener(new SoundPacketHandler());
				ProtocolLibrary.getProtocolManager().addPacketListener(pl);
			}
		},20);
	}


	@Override
	public void onDisable() {
		playerManager.stopAll();
		//grass.unhook();

		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
		ArrayList<Hologram> hs = new ArrayList<>();
		for (Plot p : plotManager.plots) {
			for (PlotObject h : p.getObjects()) {
				if (h instanceof Hologram)
				hs.add((Hologram) h);
			}
		}
		for (Hologram h : hs) {
			h.despawn();
		}
	}


}

