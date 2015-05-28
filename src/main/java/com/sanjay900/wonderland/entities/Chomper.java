package com.sanjay900.wonderland.entities;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SlimeSize;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.sanjay900.nmsUtil.util.FaceUtil;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.player.WonderlandPlayer;

public class Chomper extends WonderlandEntity {
	
	public EntityType type;
	
	public Chomper(Wonderland plugin, Location loc, EntityType type) {
		super(plugin,loc);
		this.type = type;
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		this.npc = registry.createNPC(type, "");
		npc.spawn(loc);
		npc.getTrait(SlimeSize.class).setSize(2);
		npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
	}
	@Override
	public void tickEntity() {
		for (WonderlandPlayer player: plot.getPlayers()) {
			
			if (type == EntityType.MAGMA_CUBE) {
				if (player.getPlayerN() > 1) {
					continue;
				}
			} else {
				if (player.getPlayerN() == 1) {
					continue;
				}
			}
			Vector v = new Vector(0,0,0);
			int slimeX = loc.getBlockX();
			int slimeZ = loc.getBlockZ();
			int playerX = player.getPlayer().getLocation().getBlockX();
			int playerZ = player.getPlayer().getLocation().getBlockZ();
			loc = loc.getBlock().getLocation();
			v.setY(0);
			if (slimeX != playerX) {
				v.setX(playerX - slimeX);
				
				if (loc.clone().add(FaceUtil.faceToVector(FaceUtil.getDirection(v,false))).getBlock().getType()==Material.AIR && moveEvent(loc.getBlock().getLocation().add(FaceUtil.faceToVector(FaceUtil.getDirection(v,false))).getBlock().getLocation()))  {
					moveNPC(FaceUtil.faceToVector(FaceUtil.getDirection(v,false)));

				}else if (slimeZ != playerZ){
					v.setX(0);
					v.setZ(playerZ - slimeZ);
					if (loc.clone().add(FaceUtil.faceToVector(FaceUtil.getDirection(v,false))).getBlock().getType()==Material.AIR)  {
						if (moveEvent(loc.getBlock().getLocation().add(FaceUtil.faceToVector(FaceUtil.getDirection(v,false))).getBlock().getLocation())) {
							moveNPC(FaceUtil.faceToVector(FaceUtil.getDirection(v,false)));
						}
					}
				}

			} else if (slimeZ != playerZ){
				v.setZ(playerZ - slimeZ);
				if (loc.clone().add(FaceUtil.faceToVector(FaceUtil.getDirection(v,false))).getBlock().getType()==Material.AIR)  {
					if (moveEvent(loc.getBlock().getLocation().add(FaceUtil.faceToVector(FaceUtil.getDirection(v,false))).getBlock().getLocation())) {
						moveNPC(FaceUtil.faceToVector(FaceUtil.getDirection(v,false)));
					}
				}
			} else {
				if (!player.isTp()) {
					player.reSpawn();
				}
			}
			break;
		}
	}


}
