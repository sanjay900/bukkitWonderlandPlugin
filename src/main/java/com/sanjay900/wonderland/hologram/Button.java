package com.sanjay900.wonderland.hologram;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import com.sanjay900.wonderland.Wonderland;

public class Button extends Hologram{
	public ButtonType type;
	public ButtonColour colour;
	public ButtonColour stColour;
	
	public Button(Wonderland plugin, ButtonType type, ButtonColour colour, Location location) {
		super(location, getMaterial(type, colour).getId(),getColour(type, colour).getData(), HologramType.Button);
		this.type = type;
		this.colour = this.stColour = colour;
	}
	public Boolean isDisabled() {
		return this.colour != this.stColour;
	}
	public void setPushed(Boolean pushed) {
		if (isDisabled() == pushed) return;
		if (pushed) {
			this.colour = ButtonColour.DISABLED;
		} else {
			this.colour = this.stColour;
		}
		this.id = getMaterial().getId();
		this.data = getColour().getData();
		hologram.setHelmet(new ItemStack(id,0,(short) data));
	}
	public void toggle() {
		setPushed(this.colour == this.stColour);
	}
	public DyeColor getColour() {
		return getColour(type,colour);
	}
	public Material getMaterial() {
		return getMaterial(type, colour);
	}
	public static DyeColor getColour(ButtonType type, ButtonColour colour) {
		DyeColor dyecolour = DyeColor.BLUE;
		if (type == ButtonType.SQUARE || type == ButtonType.ROUND) {
			switch (colour) {

			case DISABLED:
				dyecolour = DyeColor.GRAY;
				break;
			case MAGENTA:
			case CYAN:
			case LIME:
			case RED:
			case WHITE:
			case BLUE:
			case YELLOW:
				dyecolour = DyeColor.valueOf(colour.name());
				break;
			case WOOD:
				dyecolour = DyeColor.BROWN;
				break;

			}
		}
		
		if (type == ButtonType.STAR) {
			switch (colour) {

			case WOOD:
			case DISABLED:
				dyecolour = DyeColor.SILVER;
				break;
			case MAGENTA:
				dyecolour = DyeColor.PURPLE;
				break;
			case CYAN:
				dyecolour = DyeColor.LIGHT_BLUE;
				break;
			case LIME:
				dyecolour = DyeColor.GREEN;
				break;
			case RED:
				dyecolour = DyeColor.PINK;
				break;
			case WHITE:
				dyecolour = DyeColor.WHITE;
				break;
			case BLUE:
				dyecolour = DyeColor.BLACK;
				break;
			case YELLOW:
				dyecolour = DyeColor.ORANGE;
				break;

			}
		}
		return dyecolour;
	}
	public static Material getMaterial(ButtonType type, ButtonColour colour) {
		Material mt = Material.STAINED_CLAY;
		switch (type) {
		case ROUND:
			mt = Material.STAINED_CLAY;
			break;
		case SQUARE:
			mt = Material.WOOL;
			break;
		case STAR:
			switch (colour) {

			case DISABLED:
				mt = Material.WOOL;
				break;
			case WOOD:
			case MAGENTA:
			case CYAN:
			case LIME:
			case RED:
			case BLUE:
			case YELLOW:
				mt = Material.STAINED_CLAY;
				break;
			case WHITE:
				mt = Material.HARD_CLAY;
				break;
			}	
			break;
		}
		return mt;
	}
	public static ButtonColour getBtColour(ButtonType type, DyeColor dyecolour, Material mt) {
		ButtonColour btcolour = ButtonColour.BLUE;
			switch (dyecolour) {
			case SILVER:
				if (mt == Material.WOOL)
					btcolour = ButtonColour.DISABLED;
				else 
					btcolour = ButtonColour.WOOD;
				break;
			case PURPLE:
				btcolour = ButtonColour.MAGENTA;
				break;
			case LIGHT_BLUE:
				btcolour = ButtonColour.CYAN;
				break;
			case GREEN:
				btcolour = ButtonColour.LIME;
				break;
			case PINK:
				btcolour = ButtonColour.RED;
				break;
			case BLACK:
				btcolour = ButtonColour.BLUE;
				break;
			case ORANGE:
				btcolour = ButtonColour.YELLOW;
				break;
			case GRAY:
				btcolour = ButtonColour.DISABLED;
				break;
			case MAGENTA:
			case CYAN:
			case LIME:
			case RED:
			case WHITE:
			case BLUE:
			case YELLOW:
				btcolour = ButtonColour.valueOf(dyecolour.name());
				break;
			case BROWN:
				btcolour = ButtonColour.WOOD;
				break;
			default:
				break;

			}
			
		
		return btcolour;
	}
	public static ButtonType getBtType(Material mt, DyeColor bc) {
		ButtonType bt = ButtonType.SQUARE;
		if (mt == Material.WOOL &&bc == DyeColor.SILVER) bt = ButtonType.STAR;
		if (mt == Material.HARD_CLAY) bt = ButtonType.STAR;
		if (mt == Material.STAINED_CLAY) {
			switch (bc) {
			case GRAY:
			case MAGENTA:
			case CYAN:
			case LIME:
			case RED:
			case WHITE:
			case BLUE:
			case YELLOW:
			case BROWN:
				bt = ButtonType.ROUND;
				break;
			case SILVER:
			case PURPLE:
			case LIGHT_BLUE:
			case GREEN:
			case PINK:
			case BLACK:
			case ORANGE:
				bt = ButtonType.STAR;
				break;
			}
		}
		
		return bt;
	}
	public enum ButtonType {
		SQUARE,ROUND,STAR
	}
	public enum ButtonColour {
		WHITE,BLUE,LIME,CYAN,YELLOW,RED,MAGENTA,WOOD,DISABLED
	}
	public class StarCountdown
	{
		public BukkitTask bt;
		public BukkitTask bt2;
		private Wonderland plugin;
		public void start(final int time, Wonderland plugin) {
			this.plugin = plugin;
			Button.this.setPushed(true);
			plugin.pl.buttonsTimers.put(Button.this,this);
			this.bt = Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
			{
				int i = time;
				public void run()
				{	
					//TODO: toggle ALL gates.
					this.i--;
					if (this.i < -1)
					{
						cancel();
					}
				}
			}
			, 0L, 20L);
			this.bt2 = Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
			{
				double interval = Math.toRadians(360d/20d/(double)time);
				public void run()
				{	
					EulerAngle ea = Button.this.hologram.getHeadPose();
					ea = ea.setY(ea.getY()-interval);
					Button.this.hologram.setHeadPose(ea);
				}
			}
			, 0L, 1L);
		}
		
		public void cancel()
		{
			if (plugin.pl.buttonsTimers.containsValue(this)) {
				//TODO: toggle ALL gates.
				plugin.pl.buttonsTimers.remove(Button.this);
				Button.this.setPushed(false);
				Button.this.hologram.setHeadPose(new EulerAngle(0,0,0));
				bt.cancel();
				bt2.cancel();
			}
		}
	}
}
