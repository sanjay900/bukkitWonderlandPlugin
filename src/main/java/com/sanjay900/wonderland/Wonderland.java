package com.sanjay900.wonderland;

import java.util.ArrayList;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.sanjay900.nmsUtil.NMSUtil;
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
import com.sanjay900.wonderland.plots.WonderlandChunkGen;

public class Wonderland extends JavaPlugin {

	public PlayerListener pl;
	public EntityListener el;
	public PlayerManager playerManager;
	public HologramManager hologramManager;
	public EntityManager entityManager;
	public PlotManager plotManager;
	public NMSUtil nmsutils;
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
	return new WonderlandChunkGen(worldName,id,this);
	}

    @Override
    public void onEnable() {
    	
    	getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
        		nmsutils = (NMSUtil) Bukkit.getPluginManager().getPlugin("nmsUtils");
            	NPCRegistry registry = CitizensAPI.getNPCRegistry();
        		registry.deregisterAll();
        		hologramManager = new HologramManager(Wonderland.this);
            	playerManager = new PlayerManager();
            	
            	pl = new PlayerListener(Wonderland.this);
                Bukkit.getPluginManager().registerEvents(pl, Wonderland.this);
                el = new EntityListener(Wonderland.this);
                Bukkit.getPluginManager().registerEvents(el, Wonderland.this);
                Wonderland.this.getServer().getMessenger().registerOutgoingPluginChannel(Wonderland.this, "BungeeCord");
    	plotManager = new PlotManager(Wonderland.this);
    	plotManager.reloadConfig();
    	entityManager = new EntityManager(Wonderland.this);
    	entityManager.reloadConfig();
    	hologramManager.reloadConfig();
        getCommand("wl").setExecutor(new WonderCommand(Wonderland.this));
        getCommand("plot").setExecutor(new PlotCommand(Wonderland.this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new SoundPacketHandler(Wonderland.this));
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
    		for (Hologram h : p.getHolograms()) {
    			hs.add(h);
    		}
    	}
    	for (Hologram h : hs) {
    		h.despawn();
    	}
    	nmsutils.deregisterAll();
    }


}

