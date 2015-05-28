package com.sanjay900.wonderland.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;

import com.sanjay900.nmsUtil.util.FaceUtil;
import com.sanjay900.wonderland.entities.Bomb;
import com.sanjay900.wonderland.entities.Chomper;
import com.sanjay900.wonderland.entities.Coily;
import com.sanjay900.wonderland.entities.WonderlandEntity;
import com.sanjay900.wonderland.plots.WonderlandChunkGen;

public class EntityManager extends ConfigManager{
	
	
	public HashMap<String,WonderlandEntity> entities = new HashMap<>();
	public EntityManager() {
		super("entities.yml");
	}
	public WonderlandEntity getEntity(int entityID) {
			for (WonderlandEntity en: entities.values()) {
				if (en.getNpc().getEntity().getEntityId() == entityID) {
					return en;
				}
			}
		return null;
	}
	
	public void loadConfig() {
		Iterator<WonderlandEntity> it = entities.values().iterator();
		while (it.hasNext()) {
			it.next().despawn();
			it.remove();
		}
		

		
		if (this.config.contains("entities")) {
			for (String key: config.getConfigurationSection("entities").getKeys(false)) {
				String path = "entities."+key;
				String world = config.getString(path+"."+"world");
				if (Bukkit.getWorld(world) == null || !(Bukkit.getWorld(world).getGenerator() instanceof WonderlandChunkGen)) {
					continue;
				}
				Location loc = config.getVector(path+"."+"loc").toLocation(Bukkit.getWorld(world));

				String type = config.getString(path+"."+"type");
				WonderlandEntity h = null;
				switch (type) {
				case "chomper":
					EntityType et = EntityType.valueOf(config.getString(path+"."+"chompertype"));
					h = new Chomper(plugin,loc,et);
					break;
				case "bomb":
					h = new Bomb(plugin,loc,BlockFace.valueOf(config.getString(path+"."+"direction")));
					break;
				case "coily":
					h = new Coily(plugin,loc);
					break;
				}
				entities.put(key,h);
			}
		}

	}

	public void removeEntity(WonderlandEntity entity) {
		String id = null;
		for (Entry<String, WonderlandEntity> h : entities.entrySet()) {
			if (h.getValue() == entity) {
				id = h.getKey();
			}
		}
		if (id != null) {
			this.config.set("entities"+"."+id, null);
			entities.remove(id);
		}
		entity.remove();
		saveConfig();
	}
	public void updateEntity(WonderlandEntity entity) {
		String id = null;
		for (Entry<String, WonderlandEntity> h : entities.entrySet()) {
			if (h.getValue() == entity) {
				id = h.getKey();
			}
		}
		if (id == null) {
			return;
		}
		saveEntity(entity,id);
	}
	public void addEntity(WonderlandEntity entity) {

		int id = -1;
		for (Entry<String, WonderlandEntity> h : entities.entrySet()) {
			if (StringUtils.isNumeric(h.getKey())) {
				if (Integer.parseInt(h.getKey()) > id) {
					id = Integer.parseInt(h.getKey());
				}
			}	
		}
		id++;

		saveEntity(entity,String.valueOf(id));
	}
	public void saveEntity(WonderlandEntity entity, String id) {
		String path = "entities."+id;
		config.set(path+"."+"loc", entity.getSpawnLoc().toVector());
		config.set(path+"."+"world", entity.getLoc().getWorld().getName());
		if (entity instanceof Chomper) {
			config.set(path+"."+"type", "chomper");
			config.set(path+"."+"chompertype", ((Chomper)entity).type.name());
		}
		if (entity instanceof Coily) {
			config.set(path+"."+"type", "coily");
		}
		if (entity instanceof Bomb) {
			config.set(path+"."+"type", "bomb");
			config.set(path+"."+"direction", ((Bomb)entity).getStartDir().name());
		}
		saveConfig();
		entities.put(id, entity);
	}
	public Fireball fireCannon(Block b) {
		BlockState bs = b.getState();
		org.bukkit.material.Dispenser disp = (org.bukkit.material.Dispenser) bs
				.getData();

		BlockFace direction = disp
				.getFacing();
		return fireCannon(direction,b.getRelative(direction).getLocation());
		
	}
	public Fireball fireCannon(BlockFace face, Location location) {
		location.getWorld().playEffect(location, Effect.GHAST_SHOOT, 1);
		Fireball f = plugin.nmsutils.createFireball(location.add(0.5,0.5,0.5), FaceUtil.faceToVector(face));
		return f;
		
	}



}
