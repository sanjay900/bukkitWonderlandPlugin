package com.sanjay900.wonderland.managers;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.sanjay900.wonderland.player.WonderlandPlayer;

public class PlayerManager {
	public ArrayList<WonderlandPlayer> players = new ArrayList<>();
	 public WonderlandPlayer getPlayer(Player player) {
	        for (WonderlandPlayer p : players) {
	        	
	            if (p.getPlayer().getName().equalsIgnoreCase(player.getName())) {
	                return p;
	            }
	        }
	        return null;
	    }
	public void stopAll() {
		for (WonderlandPlayer p : players) 
    	{
    		p.stopGame();
    	}
	}
}
