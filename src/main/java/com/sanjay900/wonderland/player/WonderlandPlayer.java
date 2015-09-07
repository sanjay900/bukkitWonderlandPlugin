package com.sanjay900.wonderland.player;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sanjay900.puzzleapi.api.AbstractPlayer;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.hologram.Button;
import com.sanjay900.wonderland.hologram.ItemHologram;
import com.sanjay900.wonderland.plots.Plot;

import lombok.Getter;

public class WonderlandPlayer extends AbstractPlayer{
	private Wonderland plugin = Wonderland.getInstance();
	public ArrayList<Button> toggledButtons = new ArrayList<>();
	private Location prevLocation;
	BukkitTask task; 
	@Getter
	private int playerN;
	public WonderlandPlayer(Player p, int playerN) {
		super(p);
		this.prevLocation = p.getLocation();
		p.setWalkSpeed(0.01f);
		this.playerN = playerN;
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable(){

			@Override
			public void run() {
				updateScoreboard();
			}
		}, 1l, 1l);

	}
	private void updateScoreboard() {


	}

	public void stopGame() {
		getPlayer().setWalkSpeed(0.2f);
		task.cancel();
		getPlayer().teleport(prevLocation);
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
