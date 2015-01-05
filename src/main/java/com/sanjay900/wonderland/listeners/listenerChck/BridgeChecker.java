package com.sanjay900.wonderland.listeners.listenerChck;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sanjay900.wonderland.utils.Utils;

public class BridgeChecker {
	private Wonderland plugin;
	public BridgeChecker(Wonderland plugin) {
		this.plugin = plugin;
	}
	HashMap<Bridge, WonderlandPlayer> bridges = new HashMap<>();
	public void updateBridge(final WonderlandPlayer p, final Block under, final Block sign, final String... lines) {
		checkBridge(under, p);
		Bukkit.getServer().getScheduler()
		.runTaskLater(plugin, new Runnable() {
			public void run() {
		bridges.put(new Bridge(under, sign, lines), p);
			}
		},3L);
	}
	@SuppressWarnings("deprecation")
	public boolean checkBridge(final Block under, final WonderlandPlayer p) {
		if (bridges.containsValue(p)) {
			Bukkit.getServer().getScheduler()
			.runTaskLater(plugin, new Runnable() {
				public void run() {
			Bridge bridge = null;
			for (Entry<Bridge, WonderlandPlayer> entry : bridges.entrySet()) {
				if (entry.getValue() == p) {
					bridge = entry.getKey();
				}
			}
			if (bridge != null
					&& !Utils.compareLocation(under.getLocation(),
							bridge.getLocation())) {
				
				int decay = bridge.decay;
				decay = decay - 1;
				if (decay < 1) {
					String[] typeA = bridge.type.split(":");
					int type = Integer.parseInt(typeA[0]);
					int data = Integer.parseInt(typeA[1]);
					String[] BtypeA = bridge.BType.split(":");
					int Btype = Integer.parseInt(BtypeA[0]);
					int Bdata = Integer.parseInt(BtypeA[1]);
					
					bridge.bridgeBlock.setTypeIdAndData(Btype, (byte) Bdata, true);
					bridge.bridgeBlock.getRelative(BlockFace.DOWN).setTypeIdAndData(Btype,
							(byte) Bdata, true);
					bridge.Sign.setTypeIdAndData(type, (byte) data,
							true);

				} else {
					Sign s = (Sign) bridge.Sign.getState();
					s.setLine(1, String.valueOf(decay));
					s.update();
				}
				bridges.remove(bridge);
				
			}
				}},1L);
			return true;
		}
		return false;
	}
}
