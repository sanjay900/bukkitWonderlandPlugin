package com.sanjay900.wonderland.listeners;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.material.MonsterEggs;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.sanjay900.nmsUtil.fallingblocks.FrozenSand;
import com.sanjay900.wonderland.utils.FaceUtil;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.entities.Bomb;
import com.sanjay900.wonderland.entities.Chomper;
import com.sanjay900.wonderland.entities.Coily;
import com.sanjay900.wonderland.entities.WonderlandEntity;
import com.sanjay900.wonderland.hologram.Barrel;
import com.sanjay900.wonderland.hologram.BlockHologram;
import com.sanjay900.wonderland.hologram.Boulder;
import com.sanjay900.wonderland.hologram.Box;
import com.sanjay900.wonderland.hologram.Button;
import com.sanjay900.wonderland.hologram.Electro;
import com.sanjay900.wonderland.hologram.ElectroConversation;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.hologram.ItemHologram;
import com.sanjay900.wonderland.hologram.Reflector;
import com.sanjay900.wonderland.hologram.Spike;
import com.sanjay900.wonderland.hologram.Tunnel;
import com.sanjay900.wonderland.hologram.Button.ButtonColour;
import com.sanjay900.wonderland.hologram.Button.ButtonType;
import com.sanjay900.wonderland.hologram.Button.StarCountdown;
import com.sanjay900.wonderland.hologram.Reflector.ReflectorType;
import com.sanjay900.wonderland.listeners.listenerChck.BridgeChecker;
import com.sanjay900.wonderland.listeners.listenerChck.ConveyorSnowChecker;
import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sanjay900.wonderland.plots.Plot;
import com.sanjay900.wonderland.plots.Plot.PlotStatus;
import com.sanjay900.wonderland.plots.Plot.PlotType;
import com.sanjay900.wonderland.plots.WonderlandChunkGen;
import com.sanjay900.wonderland.utils.Cooldown;
import com.sanjay900.wonderland.utils.Utils;

@SuppressWarnings("unused")
public class PlayerListener extends PacketAdapter implements Listener {
	private Wonderland plugin;
	public ConveyorSnowChecker conveyorSnowCheker;
	private BridgeChecker bridgeChecker;
	private ArrayList<Player> hasScoreboard = new ArrayList<>();

	ElectroConversation eco;
	public PlayerListener(Wonderland plugin) {
		super(plugin,
				ListenerPriority.NORMAL, 
				PacketType.Play.Client.USE_ENTITY);
		this.plugin = plugin;

		this.conveyorSnowCheker = new ConveyorSnowChecker();
		this.bridgeChecker = new BridgeChecker(plugin);
		eco = new ElectroConversation(plugin);



	}

	public HashMap<Block, Byte> panes = new HashMap<>();
	public HashMap<Button, StarCountdown> buttonsTimers = new HashMap<>();
	boolean checked = false;
	public boolean stop;
	@EventHandler
	public void worldLoad(WorldLoadEvent evt) {
		plugin.hologramManager.reloadConfig();
		plugin.plotManager.reloadConfig();
		plugin.entityManager.reloadConfig();
		plugin.nmsutils.registerWorld(evt.getWorld());
	}
	@EventHandler
	public void worldUnLoad(WorldUnloadEvent evt) {
		plugin.nmsutils.deregisterWorld(evt.getWorld().getUID());
	}
	@EventHandler
	public void blockPlace(final BlockPlaceEvent event) {
		Plot plot = plugin.plotManager.getPlotInside(event.getBlock().getLocation());
		if (!(event.getBlockPlaced().getWorld().getGenerator() instanceof WonderlandChunkGen)) {
			return;
		}
		if (plot == null || event.getBlockPlaced().getY() >= 25) {
			event.getPlayer().sendMessage("You are not allowed to build on the path or the walls or build past the roof.");
			event.setCancelled(true);
			return;
		}
		if (plot.getOwner() == null) {
			event.getPlayer().sendMessage("This plot doesn't belong to anybody. ./plot claim to claim it.");
			event.setBuild(false);
			return;
		}
		if (plot.getOwner().compareTo(event.getPlayer().getUniqueId()) !=0 && !plot.getHelpers().contains(event.getPlayer().getUniqueId())) {
			event.setBuild(false);
			event.getPlayer().sendMessage("This isnt your plot");
			return;
		}
		if (event.getBlock().getType() == Material.CARPET && event.getBlock().getData() == (byte)3) {
			plugin.hologramManager.addHologram(new Electro(plugin, event.getBlock().getLocation(), 1));
			event.setCancelled(true);
			return;	
		}
		PlotType p = Tunnel.getType(new MaterialData(event.getBlock().getType(),event.getBlock().getData()));
		if (p != null) {
			plugin.hologramManager.addHologram(new Tunnel(plugin, event.getBlock().getLocation(), p));
			event.setCancelled(true);
			return;	
		}
		if (event.getBlock().getType() == Material.WOOL || event.getBlock().getType() == Material.HARD_CLAY ||event.getBlock().getType() == Material.STAINED_CLAY) {
			ButtonType bt = Button.getBtType(event.getBlock().getType(), DyeColor.getByWoolData(event.getBlock().getData()));
			ButtonColour bc = Button.getBtColour(bt, DyeColor.getByWoolData(event.getBlock().getData()), event.getBlock().getType());
			plugin.hologramManager.addHologram(new Button(plugin, bt, bc,event.getBlock().getLocation()));
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getType().name().contains("MUSHROOM")) {
			plugin.hologramManager.addHologram(new BlockHologram(plugin, event.getBlock().getLocation(),"100:0"));
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getType() == Material.WOOD &&event.getBlock().getData() == (byte)3) {
			plugin.hologramManager.addHologram(new Box(plugin, event.getBlock().getLocation(),Box.BoxType.WOODEN));
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getType() == Material.BEDROCK) {
			plugin.hologramManager.addHologram(new Spike(plugin, event.getBlock().getLocation(),1L));
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getType() == Material.IRON_BLOCK) {
			plugin.hologramManager.addHologram(new Box(plugin, event.getBlock().getLocation(),Box.BoxType.IRON));
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getType() == Material.STAINED_GLASS) {
			ReflectorType rt = Reflector.getType(event.getBlock().getData());
			if (rt != null) {
				plugin.hologramManager.addHologram(new Reflector(plugin, event.getBlock().getLocation(),rt));
				event.setCancelled(true);
				return;
			}
		}
		if (event.getBlock().getType() == Material.TNT) {
			plugin.hologramManager.addHologram(new Barrel(plugin, event.getBlock().getLocation()));
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getType().equals(Material.COBBLESTONE)) {
			plugin.hologramManager.addHologram(new Boulder(plugin, event.getBlock().getLocation()));
			event.setCancelled(true);
			return;
		}

		if (!isDirectional(event.getBlock().getType()) && event.getBlock().getData() != event.getItemInHand().getData().getData()) {
			event.setCancelled(true);
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

				@Override
				public void run() {
					event.getBlockPlaced().setType(event.getItemInHand().getType());
					event.getBlockPlaced().setData(event.getItemInHand().getData().getData());
				}}, 1L);

		}


	}
	private boolean isDirectional(Material type) {
		if (type.name().contains("PISTON")||type.name().contains("CHEST")||type.name().contains("DIODE")||type.name().contains("REDSTONE")||type.name().contains("DOOR")){
			return true;
		}
		switch (type) {
		case ANVIL:
		case DISPENSER:
		case DROPPER:
		case FURNACE:
			return true;
		default:
			return false;
		}
	}
	@EventHandler
	public void itemDrop(PlayerDropItemEvent evt) {
		if (plugin.playerManager.getPlayer(evt.getPlayer()) != null) {
			evt.setCancelled(true);
		}
	}

	@EventHandler 
	public void playerMove(PlayerMoveEvent evt) {

		if (evt.getTo().getWorld().getGenerator() instanceof WonderlandChunkGen&&plugin.playerManager.getPlayer(evt.getPlayer())== null) {
			Plot plot = plugin.plotManager.getPlotInside(evt.getTo());

			if (plot != null && plot.getType().getRoof() != null) {
				evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,1));
			} else {
				evt.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
			}
			if (hasScoreboard.contains(evt.getPlayer())) {
				Scoreboard board = evt.getPlayer().getScoreboard();
				String owner;
				String type;
				String coords;
				String title;
				String helper;
				if (plot != null) {
					owner = plot==null?"":(plot.getOwner()==null?"Nobody":Bukkit.getPlayer(plot.getOwner())!=null?Bukkit.getPlayer(plot.getOwner()).getName():"Unknown");
					type = plot.getType().name().toLowerCase();
					coords = plot.getCoordX()+","+plot.getCoordZ();
					title = plot.getTitle().length()>16?plot.getTitle().substring(0, 16):plot.getTitle();
					helper = plot.getHelpers().contains(evt.getPlayer().getUniqueId())+"";
				} else {
					owner = "";
					type = "";
					coords ="";
					title = "";
					helper = "";
				}
				Team team = board.getTeam("owner");
				if (team.getSuffix() != null && !owner.equals(team.getSuffix()))
					team.setSuffix(owner);
				team = board.getTeam("type");
				if (team.getSuffix() != null && !type.equals(team.getSuffix()))
					team.setSuffix(type);
				team = board.getTeam("coords");
				if (team.getSuffix() != null && !coords.equals(team.getSuffix()))
					team.setSuffix(coords);
				team = board.getTeam("title");
				if (team.getSuffix() != null && !title.equals(team.getSuffix()))
					team.setSuffix(title);
				team = board.getTeam("helper");
				if (team.getSuffix() != null && !helper.equals(team.getSuffix()))
					team.setSuffix(helper);

			} else {
				Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
				Objective objective = board.registerNewObjective("sidebar", "dummy");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				objective.setDisplayName(ChatColor.YELLOW+"Plot Info:");		
				objective.getScore("Owner: ").setScore(1);
				objective.getScore("Type: ").setScore(2);
				objective.getScore("Coordinates: ").setScore(3);
				objective.getScore("Title: ").setScore(4);
				objective.getScore("Helper: ").setScore(0);
				Team team = board.registerNewTeam("owner");
				team.addEntry("Owner: ");
				team.setSuffix(plot==null?"":(plot.getOwner()==null?"Nobody":Bukkit.getPlayer(plot.getOwner())!=null?Bukkit.getPlayer(plot.getOwner()).getName():"Unknown"));
				team = board.registerNewTeam("type");
				team.addEntry("Type: ");
				team.setSuffix(plot==null?"":plot.getType().name().toLowerCase());
				team = board.registerNewTeam("coords");
				team.addEntry("Coordinates: ");
				team.setSuffix(plot==null?"":plot.getCoordX()+","+plot.getCoordZ());
				team = board.registerNewTeam("title");
				team.addEntry("Title: ");
				team.setSuffix(plot==null?"":plot.getTitle().length()>16?plot.getTitle().substring(0, 16):plot.getTitle());
				team = board.registerNewTeam("helper");
				team.addEntry("Helper: ");
				team.setSuffix(plot==null?"":(plot.getHelpers().contains(evt.getPlayer().getUniqueId())+""));
				evt.getPlayer().setScoreboard(board);
				hasScoreboard.add(evt.getPlayer());
			}
		} else {
			if (hasScoreboard.contains(evt.getPlayer())) {
				evt.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				hasScoreboard.remove(evt.getPlayer());
			}
		}
	}
	@EventHandler
	public void despawn(ItemDespawnEvent evt ) {
		if (evt.getEntity().getItemStack().hasItemMeta() && evt.getEntity().getItemStack().getItemMeta().hasDisplayName()) {
			evt.setCancelled(true);
		}
	}

	@EventHandler
	public void leave(PlayerQuitEvent evt) {
		if (plugin.playerManager.getPlayer(evt.getPlayer())!=null) {
			plugin.playerManager.getPlayer(evt.getPlayer()).stopGame();
			plugin.playerManager.players.remove(plugin.playerManager.getPlayer(evt.getPlayer()));  
		}
		HashMap<FrozenSand, UUID[]> holostoedit = new HashMap<>();
		for (Entry<FrozenSand, UUID[]> uuids: plugin.hologramManager.showToPlayer.entrySet()) {
			ArrayList<UUID> players = new ArrayList<>();
			players.addAll(Arrays.asList(uuids.getValue()));
			if (players.contains(evt.getPlayer().getUniqueId())) {
				players.remove(evt.getPlayer().getUniqueId());
			}
			holostoedit.put(uuids.getKey(), players.toArray(new UUID[players.size()]));
		}
		plugin.hologramManager.showToPlayer.putAll(holostoedit);
		for (Plot p: plugin.plotManager.getOwnedPlots(evt.getPlayer())) {
			p.cancelRequest(evt.getPlayer());
		}
		for (Plot p: plugin.plotManager.plots) {
			if (p.getRequestPlayers().contains(evt.getPlayer().getUniqueId())) {
				p.denyRequest(evt.getPlayer());
			}
		}
	}
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent evt) {
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (!checked) {
			checked = true;
			Iterator<Entry<Block, Byte>> iter = panes.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Block, Byte> entry = iter.next();
				Block blk = entry.getKey();
				if (!(blk.isBlockPowered() || blk.isBlockIndirectlyPowered())) {
					for (WonderlandPlayer p : plugin.playerManager.players) {
						if (Utils.compareLocation(p.getPlayer().getLocation().getBlock().getLocation(), blk.getLocation())){
							p.reSpawn();
						}
					}
					if (blk.getType() == Material.WOOD || blk.getType() == Material.COBBLESTONE) {
						blk.getWorld().createExplosion(blk.getLocation().add(0.5, 0.5, 0.5), 0.0f, false);
					}
					blk.setType(Material.STAINED_GLASS_PANE);
					blk.setData(entry.getValue());
					iter.remove();

				}
			}
			if (event.getChangedType().name().toLowerCase().contains("redstone")) {
				Block block = event.getBlock();
				for (final Block b : SurroundingBlocks(block)){
					if (CheckBlock(b)){
						if (b.isBlockIndirectlyPowered()||b.isBlockPowered()){

							if (Utils.getStillConveyor(b)) {
								Utils.statictoAnimConveyor(b, plugin);
								if (getHologram(b.getRelative(BlockFace.UP))!=null) {
									//TODO: move entity around on conveyor start
									//pushableChecker.checkPushable(getHologram(b.getRelative(BlockFace.UP)), Utils.getConveyor(b));
								}
							}


						}
						else{

							Utils.animToStaticConveyor(b, plugin);

						}
					}
				}
			}
			for (Block blk : Utils.getNearbyBlocks(event.getBlock().getLocation(), 2)) {

				if (blk.getType() == Material.STAINED_GLASS_PANE
						&& (blk.isBlockPowered() || blk
								.isBlockIndirectlyPowered())) {
					panes.put(blk, blk.getData());
					blk.setType(Material.AIR);
				}
			}
			checked = false;
		}

	}
	@EventHandler
	public void BlockBreak(BlockBreakEvent evt) {
		Plot plot = plugin.plotManager.getPlotInside(evt.getBlock().getLocation());
		if (!(evt.getBlock().getWorld().getGenerator() instanceof WonderlandChunkGen)) {
			return;
		}
		if (plot == null || evt.getBlock().getLocation().getBlockY() >= 25 || evt.getBlock().getLocation().getBlockY() < 2) {
			evt.getPlayer().sendMessage("You are not allowed to break the path or the walls or break the roof or past the floor.");
			evt.setCancelled(true);
			return;
		}
		if (plot.getOwner() == null) {
			evt.getPlayer().sendMessage("This plot doesn't belong to anybody. ./plot claim to claim it.");
			evt.setCancelled(true);
			return;
		}
		if (plot.getOwner().compareTo(evt.getPlayer().getUniqueId()) !=0&& !plot.getHelpers().contains(evt.getPlayer().getUniqueId())) {
			evt.setCancelled(true);
			return;
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPickUp(InventoryCreativeEvent event)
	{
		Player player = (Player)event.getInventory().getHolder();

		ItemStack item = event.getCursor();
		Block block = player.getTargetBlock(null, 5);
		if ((player.getGameMode().equals(GameMode.CREATIVE)) && (item.getType() != Material.AIR)) {
			if ((item.getType().isBlock()) && (item.getType() == block.getType()))
			{
				ItemStack it = createItem(event.getCursor(), (block.isEmpty()) || (block.getData() == 0) ? item.getDurability() : block.getData());
				int slot = getSlot(it, player);
				if (slot != -1) {
					event.setCancelled(true);
					player.getInventory().setHeldItemSlot(slot);
				} else {
					event.setCursor(it);
				}
			}

		}
	}
	private int getSlot(ItemStack it, Player pl) {
		for (int i = 0; i < 9; i++) {
			ItemStack it2 = pl.getInventory().getItem(i);
			if (it2 != null && it2.getType() == it.getType() && it2.getData().getData()== it.getData().getData()) {
				return i;
			}
		}
		return -1;
	}
	private static int maxDurability(ItemStack item)
	{
		if (item.getType() != Material.PAINTING) {
			return item.getType().getMaxDurability() == 0 ? 16 : item.getType().getMaxDurability();
		}
		return item.getType().getMaxDurability() == 0 ? 26 : item.getType().getMaxDurability();
	}
	public static ItemStack createItem(ItemStack item, int data)
	{
		data = (data = (data + maxDurability(item)) % maxDurability(item)) < 0 ? 0 : data;
		ItemStack buff;
		if (data == 0) {
			buff = new ItemStack(item.getType());
		} else {
			buff = new ItemStack(item.getType(), data);
		}
		if ((item.getType() != Material.DOUBLE_PLANT) || (data <= 5))
		{
			buff.setDurability((short)data);
			buff.getData().setData((byte)data);
		}	    
		return buff;
	}


	@EventHandler
	public void interact(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK&& !e.hasItem()) {
			if (plugin.playerManager.getPlayer(e.getPlayer())== null) {
				Plot p = plugin.plotManager.getPlotWall(e.getClickedBlock().getLocation());
				if (p != null || e.getClickedBlock().getLocation().getBlockY() == 25) {
					BlockFace face = FaceUtil.getDirection(e.getClickedBlock().getLocation().toVector().subtract(e.getPlayer().getLocation().toVector()),false);
					Block b = e.getClickedBlock().getRelative(face);
					while (b.getType().isSolid()) {
						b = b.getRelative(face);
					}
					b = b.getRelative(face);
					e.getPlayer().teleport(b.getLocation().setDirection(e.getPlayer().getLocation().getDirection()));
					e.setCancelled(true);
					return;
				}
			}

		}
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null) {
			if (e.getItem().getType()== Material.MONSTER_EGG) {
				Plot p = plugin.plotManager.getPlotInside(e.getPlayer().getLocation());
				if (p==null || p.getOwner()==null) return;
				Location clicked = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().add(0.5,0,0.5);
				if (p.getOwner().compareTo(e.getPlayer().getUniqueId()) !=0||p.getHelpers().contains(e.getPlayer().getUniqueId())) {
					switch (e.getItem().getData().getData()) {
					case 55:
						plugin.entityManager.addEntity(new Chomper(plugin,clicked,EntityType.SLIME));
						break;
					case 62:
						plugin.entityManager.addEntity(new Chomper(plugin,clicked,EntityType.MAGMA_CUBE));
						break;
					case 50:
						plugin.entityManager.addEntity(new Bomb(plugin,clicked,FaceUtil.getDirection(e.getPlayer().getLocation().getDirection(), false)));
						break;
					case 101:
						plugin.entityManager.addEntity(new Coily(plugin,clicked));
						break;
					}
				}
			}

			Location clicked = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().add(0.5,1,0.5);
			Plot p = plugin.plotManager.getPlotInside(clicked);
			if (e.hasItem() && e.getItem().getType()== Material.DIAMOND) {

				if ((p.getOwner() != null &&p.getOwner().compareTo(e.getPlayer().getUniqueId()) ==0)||p.getHelpers().contains(e.getPlayer().getUniqueId())) {
					plugin.hologramManager.addHologram(new ItemHologram(plugin,clicked,264,(byte)0));
					e.getPlayer().sendMessage("Keys created:"+p.getKeys()+"");
				} else {
					e.getPlayer().sendMessage("You need to be the owner or a helper to place keys.");
				}

			}
			if (e.hasItem() && e.getItem().getType()== Material.GOLD_INGOT) {
				if ((p.getOwner() != null &&p.getOwner().compareTo(e.getPlayer().getUniqueId()) ==0)||p.getHelpers().contains(e.getPlayer().getUniqueId())) {
					plugin.hologramManager.addHologram(new ItemHologram(plugin,clicked,266,(byte)0));
					e.getPlayer().sendMessage("Bonuses created:"+p.getBonuses()+"");
				}else {
					e.getPlayer().sendMessage("You need to be the owner or a helper to place bonuses.");
				}
			}
		}
		if (Cooldown.tryCooldown(e.getPlayer(), "freecam", 1L)&&plugin.playerManager.getPlayer(e.getPlayer())!=null&&e.hasItem()&&e.getItem().getType()==Material.ITEM_FRAME&&e.getItem().hasItemMeta()&&e.getItem().getItemMeta().hasDisplayName()) {
			switch (ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).toLowerCase()) {
			case "toggle freecam": 
				//plugin.playerManager.getPlayer(e.getPlayer()).toggleFreeCam();
				e.setCancelled(true);

			}
		} else if (Cooldown.tryCooldown(e.getPlayer(), "restart", 1L)&&plugin.playerManager.getPlayer(e.getPlayer())!=null&&e.hasItem()&&e.getItem().hasItemMeta()&&e.getItem().getItemMeta().hasDisplayName()) {
			switch (ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).toLowerCase()) {
			case "restart level": 
				plugin.playerManager.getPlayer(e.getPlayer()).reSpawn();
				e.setCancelled(true);
				break;		

			case "return to map": 
				plugin.playerManager.getPlayer(e.getPlayer()).map();
				e.setCancelled(true);
				break;
			case "exit game": 
				plugin.playerManager.getPlayer(e.getPlayer()).quit();
				e.setCancelled(true);
			}
		}
	}



	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event){

		Block block = event.getBlock();
		for (Block b : SurroundingBlocks(block)){
			if (CheckBlock(b)){
				if (b.isBlockIndirectlyPowered()||b.isBlockPowered()){

					Utils.animToStaticConveyor(b, plugin);


				}
				else{

					Utils.animToStaticConveyor(b, plugin);

				}}
		}
	}
	public boolean CheckBlock(Block block){
		return (Utils.getConveyor(block) != null || Utils.getStillConveyor(block));
	}
	public ArrayList<Block> SurroundingBlocks(Block block){
		ArrayList<Block> Blocks = new ArrayList<Block>();
		for (BlockFace face : BlockFace.values()){
			if (face == BlockFace.UP){
				Block above = block.getRelative(BlockFace.UP);
				Block above2 = above.getRelative(BlockFace.UP);
				Blocks.add(above);
				Blocks.add(above2);}
			Blocks.add(block.getRelative(face));
		}
		return Blocks;
	}
	@EventHandler
	public void infiniteDispenser(BlockDispenseEvent event) {
		BlockState bs = event.getBlock().getState();
		if (!(bs instanceof Dispenser))
			return;
		Dispenser d = (Dispenser) bs;
		org.bukkit.material.Dispenser disp = (org.bukkit.material.Dispenser) bs
				.getData();

		ItemStack is = d.getInventory().getItem(4);
		if (is == null)
			return;
		BlockFace direction = disp.getFacing();
		if (is != null && is.getType() == Material.FIREBALL) {
			if (plugin.plotManager.getPlot(event.getBlock().getLocation()).getStatus()==PlotStatus.STOPPED)
				plugin.entityManager.fireCannon(event.getBlock());
		}

	}
	/*
	public boolean playerMoveEvent(final Location to, final WonderlandPlayer p, final Vector direction) {

		for (WonderlandEntity c : plugin.plotManager.getPlot(p).getEntities()) {
			if (c.npc.isSpawned() && Utils.compareLocation(c.loc.getBlock().getLocation(), to.getBlock().getLocation())){
				p.reSpawn();
				return false;
			}
		}
		for (Hologram h:plugin.plotManager.getPlot(p).getHolograms()) {
			if(h instanceof Spike) {
				if (Utils.compareLocation(to.getBlock().getLocation(),h.location.getBlock().getLocation())) {
					p.reSpawn();
				}
			}
			if (h instanceof Electro) {
				Electro elec = (Electro) h;
				if (elec.isSpawned()) {
					p.reSpawn();
				}
			}
			if (h instanceof ItemHologram) {
				h.despawn();
				p.collectItem((ItemHologram)h);
			}
		}

		final Block oldBlock = to.getBlock();
		if (oldBlock.getType().name().toLowerCase().contains("stair")) {

			p.setTp(false);
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

				@Override
				public void run() {
					p.setTp(true);

				}}, 4L);
			p.teleport(to.add(0,1,0), direction);	
			return true;
		}
		if (oldBlock.getType()==Material.FLOWER_POT||oldBlock.getType()==Material.LADDER) {
			return true;
		}

		final Block under = to.getBlock().getRelative(BlockFace.DOWN);
		if (under.getType().name().contains("WATER")) {
			return true;
		}
		if (under.getRelative(BlockFace.DOWN).getType().name().contains("WATER")) {
			return true;
		}

		final BlockFace face = FaceUtil.getDirection(direction);
		final Hologram h = getHologram(oldBlock);
		if (!p.toggledButtons.isEmpty()) {
			Iterator<Button> it = p.toggledButtons.iterator();
			while (it.hasNext()) {
				Button b = it.next();
				if (b.type == Button.ButtonType.ROUND) {
					b.toggle();
					it.remove();
				}
			}
		}

		if ((h!=null&&!(oldBlock.getType() == Material.WOODEN_DOOR))) {
			if (oldBlock.getRelative(face).getType() == Material.AIR) {

				if (h instanceof Boulder||h instanceof Box||h instanceof Reflector||h instanceof Barrel) {
					PushableStatus status = pushableChecker.checkPushable(h, face);
					switch (status.type) {
					case PUSHABLE:
						pushableChecker.movePushable(h, face, status);
					case FALLEN:
						return true;
					case STATIONARY:
						return false;
					}
				}  else
					if (h instanceof Button) {
						final Button bt = (Button)h;
						if (bt.type == ButtonType.SQUARE && !bt.isDisabled()) {
							Bukkit.getScheduler().runTask(plugin, new Runnable(){

								@Override
								public void run() {
									bt.toggle();
								}});
						}
						if (bt.type == Button.ButtonType.STAR) {
							if (!bt.isDisabled()) {
								bt.toggle();
							}
							StarCountdown st = bt.new StarCountdown();
							buttonsTimers.put(bt, st);
							st.start(10, bt, plugin, p);
							p.getToggledButtons().add(bt);
						}
						if (bt.type == Button.ButtonType.ROUND) {


							Bukkit.getScheduler().runTask(plugin, new Runnable(){

								@Override
								public void run() {
									if (!bt.isDisabled()) {
										bt.toggle();
									}
									p.toggledButtons.add(bt);

								}});


							return false;



						} 

					}
					else {
						return true;
					}
			}else {
				return true;
			}
		}
		final Location cl =conveyorSnowCheker.checkConveyorAndSnow(under, p, direction, plugin);
		if (!(cl == null)) {
			p.setTp(false);
			p.teleportNoEvt(to,cl.getDirection());
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

				@Override
				public void run() {
					p.setTp(true);
					p.teleportNoChk(cl,cl.getDirection());
				}
			}, 2L);
			return true;
		} 
		bridgeChecker.checkBridge(under, p);

		Boolean isTeleport = false;
		Block teleport = under.getRelative(BlockFace.DOWN);
		isTeleport = teleport.getType().name().contains("SIGN");
		if (!isTeleport) {
			teleport = teleport.getRelative(BlockFace.DOWN);
			isTeleport = teleport.getType().name().contains("SIGN");
		}

		if (isTeleport) {
			final Sign s = (Sign) teleport.getState();
			if (s.getLine(0).equals("teleport")) {
				String[] lS = s.getLine(1).split(",");
				final Location l = new Location(s.getWorld(),Integer.parseInt(lS[0])+0.5,Integer.parseInt(lS[1]),Integer.parseInt(lS[2])+0.5).getBlock().getRelative(FaceUtil.getDirection(direction,false)).getLocation();

				l.setX(l.getBlockX()+0.5);
				l.setZ(l.getBlockZ()+0.5);




				if(p.chkteleport(l,direction)) {
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
						@Override
						public void run() {
							p.teleportNoEvt(l,direction);
						}
					}, 3L);
					l.getWorld().playSound(to, Sound.ENDERMAN_TELEPORT, 1f, 1f);
					l.getWorld().playSound(l, Sound.ENDERMAN_TELEPORT, 1f, 1f);
					return true;
				} else {
					to.getWorld().playSound(to, Sound.SHEEP_WALK, 1f, 1f);	
				}





				return false;

			} else {
				if (s.getLine(0).equals("bridge")) {
					bridgeChecker.updateBridge(p, under, s.getBlock(), s.getLines());
					return false;
				}
			}
		}



		return false;



	}
	 */

	private Hologram getHologram(Block oldBlock) {
		for (Hologram h: plugin.hologramManager.holograms.values()) {
			if (!(h instanceof BlockHologram) && Utils.compareLocation(h.location.getBlock().getLocation(), oldBlock.getLocation())) {
				return h;
			}
		}
		return null;
	}

	public boolean checkItem(Location loc) {
		for (Entity e: loc.getChunk().getEntities()) {
			if (e instanceof Item && e.getLocation().getBlockX() == loc.getBlockX() && e.getLocation().getBlockY() == loc.getBlockY() && e.getLocation().getBlockZ() == loc.getBlockZ())
				return true;
		}
		return false;
	}

	public void updateScoreboards(Plot plot) {
		for (Player p : hasScoreboard) {
			if (plugin.playerManager.getPlayer(p)!= null) return;
			Scoreboard board = p.getScoreboard();
			String owner = plot.getOwner()==null?"Nobody":Bukkit.getPlayer(plot.getOwner()).getName();
			String type = plot.getType().name().toLowerCase();
			String coords = plot.getCoordX()+","+plot.getCoordZ();
			String title = plot.getTitle().length()>16?plot.getTitle().substring(0, 16):plot.getTitle();
			Team team = board.getTeam("coords");
			if (!coords.equals(team.getSuffix()))
				return;
			team = board.getTeam("owner");
			if (!owner.equals(team.getSuffix()))
				team.setSuffix(owner);
			team = board.getTeam("type");
			if (!type.equals(team.getSuffix()))
				team.setSuffix(type);
			team = board.getTeam("title");
			if (!title.equals(team.getSuffix()))
				team.setSuffix(title);
		}
	}
	public void onPacketReceiving(final PacketEvent event) {
		final PacketContainer packet = event.getPacket();
		final int entityID = packet.getIntegers().read(0);	
		final EntityUseAction action ;
		try {
			action = packet.getEntityUseActions().read(0);
		} catch (Exception ex) {
			return;
		}
		if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE)) return;

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

			@Override
			public void run() {
				Hologram toDestroy = null;
				for (Hologram h :plugin.hologramManager.holograms.values()) {
					if (h.hologram.getBukkitEntity().getEntityId() == entityID) {
						switch (action) {
						case ATTACK:
							if (isOwned(event.getPlayer(),h)) {
								toDestroy = h;
							}
							break;
						case INTERACT:
							if (h instanceof Spike && event.getPlayer().getInventory().getItemInHand().getType() == Material.BEDROCK) {
								Spike spike = (Spike) h;
								if (spike.getTime()<5)
									spike.setTime(spike.getTime()+1);
								else
									spike.setTime(1L);
								event.getPlayer().sendMessage(ChatColor.YELLOW+"Spike timer set to "+spike.getTime());
							}
							if (h instanceof Electro && event.getPlayer().getInventory().getItemInHand().getType() == Material.CARPET && event.getPlayer().getInventory().getItemInHand().getDurability()==(short)3) {
								Electro electro = (Electro) h;
								eco.startConversation(event.getPlayer(), electro);
							}
							FallingBlock fb = (FallingBlock)h.hologram.getBukkitEntity();
							ItemStack it = new ItemStack(fb.getMaterial(),1,(short)fb.getBlockData());
							if (!event.getPlayer().getInventory().contains(it))
								event.getPlayer().getInventory().addItem(it);
							for(int i = 0; i < 9; i++) {
								ItemStack item = event.getPlayer().getInventory().getItem(i);
								if (item != null && item.isSimilar(it)) {
									event.getPlayer().getInventory().setHeldItemSlot(i);
								}
							}
							break;
						}
					}

				} 
				if (toDestroy != null) toDestroy.remove();
				WonderlandEntity en = plugin.entityManager.getEntity(entityID);
				if (en != null && action == EntityUseAction.ATTACK) {
					plugin.entityManager.removeEntity(en);
				}


			}
		}
				);
	}
	private boolean isOwned(Player p, Hologram h) {
		return (plugin.plotManager.getPlot(h.location).getOwner() != null && plugin.plotManager.getPlot(h.location).getOwner().compareTo(p.getUniqueId())==0);
	}





}