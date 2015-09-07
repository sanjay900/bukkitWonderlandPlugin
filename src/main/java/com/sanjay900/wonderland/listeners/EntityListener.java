package com.sanjay900.wonderland.listeners;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.sanjay900.nmsUtil.events.ArmorStandCollideEvent;
import com.sanjay900.nmsUtil.events.ArmorStandTickEvent;
import com.sanjay900.nmsUtil.events.FireballCollideEvent;
import com.sanjay900.nmsUtil.util.FaceUtil;
import com.sanjay900.puzzleapi.api.Plot.PlotStatus;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.hologram.Barrel;
import com.sanjay900.wonderland.hologram.BlockHologram;
import com.sanjay900.wonderland.hologram.Button;
import com.sanjay900.wonderland.hologram.Button.StarCountdown;
import com.sanjay900.wonderland.hologram.Electro;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.hologram.Hologram.HologramType;
import com.sanjay900.wonderland.hologram.Reflector;
import com.sanjay900.wonderland.hologram.Spike;
import com.sanjay900.wonderland.plots.Plot;

public class EntityListener implements Listener{
	private Wonderland plugin = Wonderland.getInstance();
	@EventHandler
	public void fireballCollide(FireballCollideEvent evt) {
		Fireball fireball = evt.getFireball();
		Entity collider = evt.getCollider();
		if (collider.getType() == EntityType.ARMOR_STAND) {
			ArmorStand cube = (ArmorStand) collider;
			evt.setCancelled(!fireballCollision((Fireball)fireball,cube));
		}

	}
	@EventHandler
	public void collideEvent(ArmorStandCollideEvent evt) {
		Entity collider = evt.getCollider();
		if (collider instanceof Player) {
			Player p = (Player)collider;
			if (plugin.playerManager.getPlayer(p)== null) {
				//evt.setCancelled(true);
				//return;
			}

		}
		ArmorStand cube = evt.getStand();

		if (collider.getType() == EntityType.PLAYER) {
			evt.setCancelled(true);
			if (!playerCollision((Player)collider,cube)) return;
			Entity en = evt.getStand();
			Vector v = en.getLocation().toVector().subtract(evt.getCollider().getLocation().toVector());
			Location l = en.getLocation();
			en.teleport(l);
			en.setVelocity(v);

		}
		if (collider.getType() == EntityType.ARMOR_STAND && collider.hasMetadata("hologramType")) {
			cubecollide((ArmorStand) collider,cube);
			evt.setCancelled(true);
		}
	}
	private boolean playerCollision(Player collider, ArmorStand cube) {
		switch ((HologramType)cube.getMetadata("hologramType").get(0).value()) {
		case Boulder:
		case Barrel:		
		case Box:
		case Reflector:

			return true;
		case ItemHologram:	
		case Star:
			collider.sendMessage("COLLECT");
			//Collect here

		case Button:
		case BlockHologram:
		case Electro:
		case Spike:
		case Tunnel:
			return false;
		}
		return false;
	}
	public Hologram getHologram(Vector vec,Location l) {
		for (Hologram h: plugin.hologramManager.holograms.values()) {
			//System.out.print(h.hologram.getLocation().distanceSquared(l.add(vec)));
			if (!(h instanceof BlockHologram) && h.hologram.getLocation().distanceSquared(l.add(vec)) < 0.8) {
				return h;
			}
		}
		return null;
	}
	public Hologram getHologram(ArmorStand cube) {
		for (Hologram h : plugin.hologramManager.holograms.values()) {
			if (h.hologram == cube) {
				return h;
			}
		}
		return null;
	}
	public boolean fireballCollision(Fireball fireball, ArmorStand cube) {
		switch ((HologramType)cube.getMetadata("hologramType").get(0).value()) {
		case Barrel:
			((Barrel)getHologram(cube)).explode();
			return true;

		case Boulder:
		case Box:
			getHologram(cube).remove();
			return true;
		case Reflector:
			Reflector r = (Reflector)getHologram(cube);
			BlockFace face2;
			BlockFace face = FaceUtil.getDirection(fireball.getVelocity(),false);
			switch (face) {
			case SOUTH:
				face2 = BlockFace.WEST;
				break;
			case WEST:
				face2 = BlockFace.SOUTH;
				break;
			case EAST:
				face2 = BlockFace.NORTH;
				break;
			case NORTH:
				face2 = BlockFace.EAST;
				break;
			default:
				face2 = FaceUtil.getDirection(fireball.getVelocity(),false);
			}

			switch(r.type) {
			case LEFT:
				fireball.teleport(r.location.getBlock().getRelative(face2).getLocation());
				fireball.setVelocity(FaceUtil.faceToVector(face2));
				fireball.setDirection(fireball.getVelocity());
				break;
			case PRISM:
				Plot p = plugin.plotManager.getPlot(fireball.getLocation());
				if (p.getStatus() == PlotStatus.STARTED) {
					cube.getWorld().playSound(cube.getLocation(), Sound.GLASS, 1f, 1f);
					cube.remove();
				}

				int[] faces = new int[]{2,4};
				for (int i : faces) {
					BlockFace rel = FaceUtil.rotate(face, i);
					plugin.entityManager.fireCannon(rel,r.location.getBlock().getRelative(rel).getLocation());
				}
				return false;
			case RIGHT:
				fireball.teleport(r.location.getBlock().getRelative(face2.getOppositeFace()).getLocation());
				fireball.setVelocity(FaceUtil.faceToVector(face2.getOppositeFace()));
				fireball.setDirection(fireball.getVelocity());
				break;

			}
			return true;
		case Button:
		case Electro:
		case ItemHologram:
		case Spike:
		case Star:
		case Tunnel:
		default:
			return false;

		}
	}
	public void cubecollide(ArmorStand collider, ArmorStand collidecube) {
		switch ((HologramType)collider.getMetadata("hologramType").get(0).value()) {
		case Barrel:
		case Boulder:
		case Box:
		case Reflector:
			switch ((HologramType)collidecube.getMetadata("hologramType").get(0).value()) {
			case Barrel:
			case Boulder:
			case Box:
			case ItemHologram:
			case Reflector:
			case Star:
				collidecube.setVelocity(new Vector(0,0,0));
				collider.setVelocity(new Vector(0,0,0));
				break;

			case BlockHologram:
			case Button:
			case Electro:
			case Spike:
			case Tunnel:
				break;

			}
			break;
		case Electro:
			if (((Electro)getHologram(collider)).isSpawned()) {
				if (getHologram(collidecube) instanceof Barrel) {
					((Barrel)getHologram(collidecube)).explode();
				} else {
					collider.remove();
				}
			}
			return;
		case Spike:
			Spike s = (Spike)getHologram(collidecube);
			if (s.getPos() > 0.2 && s.getDir() == 1) {
				if (((Electro)getHologram(collidecube)).isSpawned()) {
					if (getHologram(collider) instanceof Barrel) {
						((Barrel)getHologram(collider)).explode();
					} else {
						collider.remove();
					}
				}
			}
		default:
			return;
		}
	}
	@EventHandler
	public void onGroundTick(ArmorStandTickEvent evt)
	{
		switch ((HologramType)evt.getArmorStand().getMetadata("hologramType").get(0).value()) {
		case Barrel:
		case Box:
		case Reflector:
			evt.getArmorStand().setVelocity(evt.getArmorStand().getVelocity().multiply(0.5));
			break;

		case Boulder:
			evt.getArmorStand().setVelocity(evt.getArmorStand().getVelocity());
			break;
		case Star:
		case ItemHologram:
		case BlockHologram:
		case Button:
		case Electro:
		case Spike:
		case Tunnel:
		}
		if ((HologramType)evt.getArmorStand().getMetadata("hologramType").get(0).value() == HologramType.Button) {
			Button bt = (Button)getHologram(evt.getArmorStand());
			if (bt == null) {
				evt.getArmorStand().remove();
				return;
			}
			boolean collision = false;
			for (Entity en: evt.getArmorStand().getNearbyEntities(0.5, 1, 0.5)) {
				if (collision == true) {
					break;
				}
				if (en instanceof Player) {
					Player p = (Player)en;
					if (plugin.playerManager.getPlayer(p)!= null) continue;
				}
				if (!en.hasMetadata("hologramType")) {
					collision = true;
					break;
				}
				switch ((HologramType)en.getMetadata("hologramType").get(0).value()) {
				case Barrel:
				case Boulder:
				case Box:
				case Reflector:
					collision = true;
					break;
				case BlockHologram:
				case Button:
				case Electro:
				case ItemHologram:
				case Spike:
				case Star:
				case Tunnel:
					break;
				default:
					break;


				}
			}
			switch (bt.type) {
			case ROUND:
				if (!bt.isDisabled() && collision) {
					bt.setPushed(true);
				}
				if (bt.isDisabled() && !collision) {
					bt.setPushed(false);
				}
				break;
			case SQUARE:
				if (collision) {
					bt.setPushed(true);
				}
				break;
			case STAR:
				if (collision) {
					if (plugin.pl.buttonsTimers.containsKey(bt)) {
						plugin.pl.buttonsTimers.get(bt).cancel();
					}
					StarCountdown st = bt.new StarCountdown();
					st.start(10, plugin);
					
				}
				break;

			}
		}
	}
}
