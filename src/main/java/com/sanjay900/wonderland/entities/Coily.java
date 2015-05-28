package com.sanjay900.wonderland.entities;

import java.util.Random;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.RabbitType;
import net.citizensnpcs.trait.RabbitType.RabbitTypes;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;

import com.sanjay900.nmsUtil.util.FaceUtil;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sanjay900.wonderland.utils.Utils;

public class Coily extends WonderlandEntity {
	Random r;
	public Coily(Wonderland plugin, Location loc) {
		super(plugin,loc);
		r = new Random();
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		this.npc = registry.createNPC(EntityType.RABBIT, "");
		npc.getTrait(RabbitType.class).setType(RabbitTypes.KILLER);
		npc.spawn(loc);
		npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
	}
	@Override
	public void tickEntity() {
		BlockFace[] faces = new BlockFace[]{BlockFace.NORTH,BlockFace.SOUTH,BlockFace.EAST,BlockFace.WEST};
		BlockFace face = faces[r.nextInt(faces.length-1)];
		Location to = loc.getBlock().getLocation().add(FaceUtil.faceToVector(face)).getBlock().getLocation();
		if (moveEvent(to)) {
			for (WonderlandPlayer p: plot.getPlayers()) {
				if (Utils.compareLocation(p.getPlayer().getLocation().getBlock().getLocation(),to)) {
					p.reSpawn();
					return;
				}
			}
			moveNPC(FaceUtil.faceToVector(face));
		}
	}

}
