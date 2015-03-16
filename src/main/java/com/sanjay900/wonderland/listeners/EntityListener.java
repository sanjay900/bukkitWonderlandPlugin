package com.sanjay900.wonderland.listeners;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.sanjay900.nmsUtil.EntityCubeImpl;
import com.sanjay900.nmsUtil.EntityFireballImpl;
import com.sanjay900.nmsUtil.EntityImpl;
import com.sanjay900.nmsUtil.events.CubeGroundTickEvent;
import com.sanjay900.nmsUtil.events.EntityCollidedWithEntityImplEvent;
import com.sanjay900.nmsUtil.events.EntityImplCollideBlockEvent;
import com.sanjay900.nmsUtil.events.EntityImplCollideEntityImplEvent;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.hologram.Barrel;
import com.sanjay900.wonderland.hologram.BlockHologram;
import com.sanjay900.wonderland.hologram.Button;
import com.sanjay900.wonderland.hologram.Button.ButtonType;
import com.sanjay900.wonderland.hologram.Button.StarCountdown;
import com.sanjay900.wonderland.hologram.Electro;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.hologram.Hologram.HologramType;
import com.sanjay900.wonderland.hologram.Reflector;
import com.sanjay900.wonderland.hologram.Spike;
import com.sanjay900.wonderland.plots.Plot;
import com.sanjay900.wonderland.plots.Plot.PlotStatus;
import com.sanjay900.wonderland.utils.FaceUtil;

public class EntityListener implements Listener{
	private Wonderland plugin;

	public EntityListener(Wonderland plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void collideEvent(EntityCollidedWithEntityImplEvent evt) {
		Entity collider = evt.getCollisionEntity();
		if (collider instanceof Player) {
			Player p = (Player)collider;
			if (plugin.playerManager.getPlayer(p)== null) {
				evt.setCancelled(true);
				return;
			}

		}
		EntityImpl impl = evt.getImplementedEntity();
		if (impl instanceof EntityFireballImpl) {
			EntityFireballImpl fireball = (EntityFireballImpl)impl;
			if (collider.getType() == EntityType.FALLING_BLOCK) {
				if (plugin.nmsutils.getCube(collider) != null) {
					EntityCubeImpl cube = plugin.nmsutils.getCube(collider);
					evt.setCancelled(!fireballCollision((Fireball)fireball.getBukkitEntity(),cube));
				}

			}
		}
		if (impl instanceof EntityCubeImpl) {
			EntityCubeImpl cube = (EntityCubeImpl)impl;

			if (collider.getType() == EntityType.PLAYER) {
				evt.setCancelled(!playerCollision((Player)collider,cube));

			}
			if (collider.getType() == EntityType.FALLING_BLOCK) {
				if (plugin.nmsutils.getCube(collider) != null) {
					cubecollide(plugin.nmsutils.getCube(collider),cube);
					evt.setCancelled(true);
				}
			}

		}

	}

	private boolean playerCollision(Player collider, EntityCubeImpl cube) {
		switch (cube.<HologramType>getStored("hologramType")) {
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
			//System.out.print(h.hologram.getBukkitEntity().getLocation().distanceSquared(l.add(vec)));
			if (!(h instanceof BlockHologram) && h.hologram.getBukkitEntity().getLocation().distanceSquared(l.add(vec)) < 0.8) {
				return h;
			}
		}
		return null;
	}
	public Hologram getHologram(EntityCubeImpl cube) {
		for (Hologram h : plugin.hologramManager.holograms.values()) {
			if (h.hologram == cube) {
				return h;
			}
		}
		return null;
	}
	public boolean fireballCollision(Fireball fireball, EntityCubeImpl cube) {
		switch (cube.<HologramType>getStored("hologramType")) {
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
					cube.getBukkitEntity().getWorld().playSound(cube.getBukkitEntity().getLocation(), Sound.GLASS, 1f, 1f);
					cube.getBukkitEntity().remove();
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
	public void cubecollide(EntityCubeImpl cube, EntityCubeImpl collidecube) {
		switch (cube.<HologramType>getStored("hologramType")) {
		case Barrel:
		case Boulder:
		case Box:
		case Reflector:
			switch (collidecube.<HologramType>getStored("hologramType")) {
			case Barrel:
			case Boulder:
			case Box:
			case ItemHologram:
			case Reflector:
			case Star:
				collidecube.getBukkitEntity().setVelocity(new Vector(0,0,0));
				cube.getBukkitEntity().setVelocity(new Vector(0,0,0));
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
			if (((Electro)getHologram(cube)).isSpawned()) {
				if (getHologram(collidecube) instanceof Barrel) {
					((Barrel)getHologram(collidecube)).explode();
				} else {
					cube.getBukkitEntity().remove();
				}
			}
			return;
		case Spike:
			Spike s = (Spike)getHologram(collidecube);
			if (s.getPos() > 0.2 && s.getDir() == 1) {
				if (((Electro)getHologram(collidecube)).isSpawned()) {
					if (getHologram(cube) instanceof Barrel) {
						((Barrel)getHologram(cube)).explode();
					} else {
						cube.getBukkitEntity().remove();
					}
				}
			}
		default:
			return;
		}
	}
	@EventHandler
	public void onGroundTick(CubeGroundTickEvent evt)
	{
		switch (evt.getCube().<HologramType>getStored("hologramType")) {
		case Barrel:
		case Box:
		case Reflector:
			evt.getCube().getBukkitEntity().setVelocity(evt.getCube().getBukkitEntity().getVelocity().multiply(0.5));
			break;

		case Boulder:
			evt.getCube().getBukkitEntity().setVelocity(evt.getCube().getBukkitEntity().getVelocity());
			break;
		case Star:
		case ItemHologram:
		case BlockHologram:
		case Button:
		case Electro:
		case Spike:
		case Tunnel:
		}
		switch (evt.getCube().<HologramType>getStored("hologramType")) {
		case Barrel:
		case Box:
		case Reflector:
		case Boulder:
			
			break;
		default:
			break;
		}
		if (evt.getCube().getBukkitEntity().getLocation().add(evt.getCube().getBukkitEntity().getVelocity()).getBlock().getType().isSolid()) {
			evt.getCube().getBukkitEntity().setVelocity(new Vector(0,0,0));
		}
		if (evt.getCube().<HologramType>getStored("hologramType") == HologramType.Button) {
			Button bt = (Button)getHologram(evt.getCube());
			if (bt == null) {
				evt.getCube().getBukkitEntity().remove();
				return;
			}
			boolean collision = false;
			for (Entity en: evt.getCube().getCollidedEntities(bt.type==ButtonType.SQUARE?-0.20000000298023224:-0.3)) {
				if (collision == true) {
					break;
				}
				if (en instanceof Player) {
					Player p = (Player)en;
					if (plugin.playerManager.getPlayer(p)!= null) continue;
				}
				EntityImpl eni = plugin.nmsutils.getEntity(en);
				if (eni == null) {
					collision = true;
					break;
				}
				if (eni instanceof EntityFireballImpl) {
					continue;
				}
				if (eni instanceof EntityCubeImpl) {
					EntityCubeImpl cube = (EntityCubeImpl) eni;
					switch (cube.<HologramType>getStored("hologramType")) {
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
					StarCountdown st = bt.new StarCountdown();
					st.start(10, plugin);
				}
				break;

			}
		}
	}
	public void collideImpl(EntityImplCollideEntityImplEvent evt) {
		if (evt.getImplementedCollider() instanceof EntityCubeImpl && evt.getImplementedEntity() instanceof EntityCubeImpl) {
			EntityCubeImpl cube = (EntityCubeImpl) evt.getImplementedCollider();
			EntityCubeImpl collidecube = (EntityCubeImpl) evt.getImplementedEntity();
			evt.setCancelled(true);
			cubecollide(cube,collidecube);

		}
		if (evt.getImplementedCollider() instanceof EntityFireballImpl && evt.getImplementedEntity() instanceof EntityCubeImpl) {
			System.out.println("TEST");
		}
		if (evt.getImplementedCollider() instanceof EntityCubeImpl && evt.getImplementedEntity() instanceof EntityFireballImpl) {
			System.out.println("TEST");
		}
	}
	@EventHandler
	public void blockCollide(EntityImplCollideBlockEvent evt) {
		evt.getCube().getBukkitEntity().setVelocity(new Vector(0,0,0));
	}
}
