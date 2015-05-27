package com.sanjay900.wonderland.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.sanjay900.wonderland.Wonderland;

public class ItemHologram extends BlockHologram{
	public int itemId = 1;
	public byte itemData = 0;
	public Item item;
	public ItemHologram(Wonderland plugin, Location location, int id2, byte data2) {
		super(plugin, location, "166", HologramType.ItemHologram);
		this.itemId = id2;
		this.itemData = data2;
		this.despawn();
		this.spawn();
	}
	@Override
	public void spawn() {
		super.spawn();
		item = location.getWorld().dropItem(location, getItemStack());
		item.setMetadata("hologramType", new FixedMetadataValue(plugin,HologramType.ItemHologram));
		hologram.setPassenger(item);
	}
	@Override
	public void remove() {
		item.remove();
		super.remove();
	}
	@Override 
	public void despawn() {
		super.despawn();
		item.remove();
	}
	@SuppressWarnings("deprecation")
	private ItemStack getItemStack() {
		if (itemId == 0) itemId = 1;
		return new ItemStack(itemId,1,itemData);
	}

}
