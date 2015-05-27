package com.sanjay900.wonderland.packets;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.utils.Cooldown;

public class SoundPacketHandler extends PacketAdapter{
	
	public SoundPacketHandler() {
		super(Wonderland.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_EVENT);
		
	}
	 @Override
     public void onPacketSending(PacketEvent event) {
         PacketContainer packet = event.getPacket();
         int effectID = packet.getIntegers().read(0);
         
         // Sound: random.click
         if (effectID == 1000) {
             int x = packet.getIntegers().read(2);
             int y = packet.getIntegers().read(3);
             int z = packet.getIntegers().read(4);
             Block block = event.getPlayer().getWorld().getBlockAt(x, y, z);

             // Cancel all dispenser clicks
             if (block.getType() == Material.DISPENSER) {
                 event.setCancelled(true);
             }
         }
         if (effectID == 1008) {
             if (Cooldown.tryCooldown(event.getPlayer(), "sound", 100)) {
                 event.setCancelled(true);
             }
         }
     }

}
