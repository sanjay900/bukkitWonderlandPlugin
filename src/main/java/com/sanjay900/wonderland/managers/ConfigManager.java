package com.sanjay900.wonderland.managers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sanjay900.wonderland.Wonderland;

public abstract class ConfigManager {
	protected Wonderland plugin = Wonderland.getInstance();
	private String fileName;
	protected FileConfiguration config;
	protected File configFile = null;
	public ConfigManager(String fileName) {
		this.fileName = fileName;
	}
	public void reloadConfig() {
		if (config == null) {
			configFile = new File(plugin.getDataFolder(), fileName);
		}
		try {
			configFile.createNewFile();
		} catch (IOException e) {
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		loadConfig();
	}
	public abstract void loadConfig();
	public FileConfiguration getConfig() {
		if (config == null) {
			reloadConfig();
		}
		return config;
	}
	public void saveConfig() {
		if (config == null || configFile == null) {
			return;
		}
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
		}
	}
}
