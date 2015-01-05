package com.sanjay900.wonderland.player;

import java.util.ArrayList;
import java.util.UUID;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sanjay900.wonderland.hologram.Button;
import com.sanjay900.wonderland.hologram.ItemHologram;
import com.sanjay900.wonderland.plots.Plot;
import com.sanjay900.wonderland.Wonderland;

public class WonderlandPlayer {


	private UUID player;
	private Wonderland plugin;
	public ArrayList<Button> toggledButtons = new ArrayList<>();
	private Location prevLocation;
	BukkitTask task;
	private @Getter int playerN;
	public WonderlandPlayer(Player p, Wonderland plugin, int playerN) {
		this.prevLocation = p.getLocation();
		this.setPlayer(p);
		this.playerN = playerN;
		this.plugin = plugin;
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable(){

			@Override
			public void run() {
				updateScoreboard();
			}
		}, 1l, 1l);

	}
	private void updateScoreboard() {
		// TODO Auto-generated method stub

	}

	public void stopGame() {
		task.cancel();
		getPlayer().teleport(prevLocation);
	}


	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}
	public void reSpawn() {
		plugin.plotManager.getPlot(this).respawn();
	}
	public void quit() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF("Lobby");
		getPlayer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}
	public void setPlayer(Player player) {
		this.player = player.getUniqueId();
	}
	public void map() {
		//map
	}
	public boolean isTp() {
		return getPlayer().getWalkSpeed()==0;
	}
	public void collectItem(ItemHologram h) {
		if (h.itemId == 264) {
			Plot p = plugin.plotManager.getPlot(this);
			p.setCollectedKeys(p.getCollectedKeys()+1);
		}
		if (h.itemId == 266) {
			Plot p = plugin.plotManager.getPlot(this);
			p.setCollectedBonuses(p.getCollectedBonuses()+1);
		}
	}

}
