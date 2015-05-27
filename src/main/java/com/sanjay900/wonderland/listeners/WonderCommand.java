package com.sanjay900.wonderland.listeners;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.hologram.BlockHologram;


public class WonderCommand implements CommandExecutor {
	Wonderland plugin = Wonderland.getInstance();
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("wl")) {
				if (sender.isOp()) {
					Location l = ((Player) sender).getLocation().getBlock().getLocation();
					switch (args[0]) {

					case "save":
						plugin.hologramManager.saveConfig();
						break;
					case "load":
						plugin.hologramManager.reloadConfig();
						plugin.plotManager.reloadConfig();
						plugin.entityManager.reloadConfig();
						break;
					case "fake":
						plugin.hologramManager.addHologram(new BlockHologram(plugin, l, args[1]));
						break;
					}
				}

			}
			
		}
		return true;
	}
}
