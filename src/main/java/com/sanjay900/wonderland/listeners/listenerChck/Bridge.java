package com.sanjay900.wonderland.listeners.listenerChck;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Bridge {
	int decay;
	String type;
	String BType;
	Block Sign;
	Block bridgeBlock;
	public Bridge(Block under, Block sign, String... lines) {
		decay = Integer.parseInt(lines[1]);
		bridgeBlock = under;
		type = lines[2];
		BType = lines[3];
		this.Sign = sign;
	}
	
	public Location getLocation() {
		return this.bridgeBlock.getLocation();
	}

}
