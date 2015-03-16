package com.sanjay900.wonderland.plots;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.sanjay900.nmsUtil.EntityFireballImpl;
import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.entities.WonderlandEntity;
import com.sanjay900.wonderland.hologram.Hologram;
import com.sanjay900.wonderland.hologram.ItemHologram;
import com.sanjay900.wonderland.hologram.Star;
import com.sanjay900.wonderland.hologram.Tunnel;
import com.sanjay900.wonderland.player.WonderlandPlayer;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
@Getter
@Setter
public class Plot{
	private UUID owner;
	private String title;
	private String subTitle;
	private int coordX;
	private int coordZ;
	private World world;
	private PlotType type;
	private Location[] startLoc = new Location[4];
	private ArrayList<WonderlandPlayer> players = new ArrayList<>();
	private Wonderland plugin;
	private ArrayList<WonderlandEntity> entities = new ArrayList<>();
	private List<UUID> helpers = new ArrayList<>();
	private ArrayList<WonderlandEntity> spawnedEntities = new ArrayList<>();
	private ArrayList<Hologram> holograms = new ArrayList<>();
	private PlotType lastType;
	private NPC[] npcs = new NPC[4];
	private PlotStatus status = PlotStatus.STOPPED;
	private Star star = null;
	private int collectedKeys;
	private int collectedBonuses;
	private int playerCount = 4;
	private Scoreboard board;
	public HashMap<EntityFireballImpl, Block> fireballs = new HashMap<>();
	public Plot(int coordX, int coordZ, World world) {
		if (!(world.getGenerator() instanceof WonderlandChunkGen)) return;
		this.world = world;
		plugin = ((WonderlandChunkGen)world.getGenerator()).plugin;
		owner = null;
		title = "Change Me";
		subTitle = "Change Me";
		this.coordX = coordX;
		this.coordZ = coordZ;
		this.type = this.lastType = PlotType.EMPTY;
		Location corner = new Location(world,coordX*getWidth()*16+10,3,coordX*getWidth()*16+10);
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		for (int i = 1; i <=playerCount;i++) {
			this.startLoc[i-1]=corner.clone();
			NPC wolf = registry.createNPC(EntityType.PLAYER, "Player "+i+" Spawn");
			wolf.spawn(corner);
			npcs[i-1] = wolf;
		}
		this.star = new Star(plugin,corner,"0,0");

	}
	public void setPlayerCount() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		Location corner = new Location(world,coordX*getWidth()*16+10,3,coordX*getWidth()*16+10);
		for (int i = 1; i <=4;i++) {
			if (i <= playerCount) {
			this.startLoc[i-1]=corner.clone();
			NPC wolf = registry.createNPC(EntityType.PLAYER, "Player "+i+" Spawn");
			wolf.spawn(corner);
			npcs[i-1] = wolf;
			} else {
				this.startLoc[i-1] = null;
				this.npcs[i-1] = null;
			}
		}
	}
	public void fireCannons() {
		CuboidRegion r = new CuboidRegion(new Vector((getChunkX()*16)+7,3,(getChunkZ()*16)+7),new Vector((getChunkX()*16)+getGenerator().size-1,getGenerator().height-1,(getChunkZ()*16)+getGenerator().size-1));
		for (int x =r.getMinimumPoint().getBlockX(); x< r.getMaximumPoint().getBlockX(); x++) {
			for (int y =r.getMinimumPoint().getBlockY(); y< r.getMaximumPoint().getBlockY(); y++) {
				for (int z =r.getMinimumPoint().getBlockZ(); z< r.getMaximumPoint().getBlockZ(); z++) {
					Block b = world.getBlockAt(x, y, z);
					if (b.getType() == Material.DISPENSER) {
						fireballs.put(plugin.entityManager.fireCannon(b),b);
					}
				}
			}
		}

	}
	public void setPlayerCount(int playerCount) {
		startLoc = new Location[playerCount];
		npcs = new NPC[playerCount];
		Location corner = new Location(world,coordX*getWidth()*16+10,3,coordX*getWidth()*16+10);
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		for (int i = 0; i <playerCount;i++) {
			this.startLoc[i]=corner.clone();
			NPC wolf = registry.createNPC(EntityType.PLAYER, "Player "+(i+1)+" Spawn");
			wolf.spawn(corner);
			npcs[i] = wolf;
		}
		this.playerCount = playerCount;
	}
	public int getKeys() {
		int keys = 0;
		for (Hologram h: holograms) {
			if (h instanceof ItemHologram) {
				if (((ItemHologram)h).itemId == 264) {
					keys++;
				}
			}
		}
		return keys;
	}
	public int getBonuses() {
		int bonuses = 0;
		for (Hologram h: holograms) {
			if (h instanceof ItemHologram) {
				if (((ItemHologram)h).itemId == 266) {
					bonuses++;
				}
			}
		}
		return bonuses;
	}
	public Plot(Location loc) {

		this(((int) Math.ceil((double)loc.getBlockX()/16d/(double)(((WonderlandChunkGen)loc.getWorld().getGenerator()).size/ 16)))-1,
				((int) Math.ceil((double)loc.getBlockZ()/16d/(double)(((WonderlandChunkGen)loc.getWorld().getGenerator()).size/ 16)))-1,
				loc.getWorld()
				);
	}
	public void setStar(String s, Location loc) {
		if (star != null) {
			star.remove();
		}
		this.star = new Star(plugin,loc,s);
	}

	public void save() {
		//plugin.pl.updateScoreboards(this);
		plugin.plotManager.savePlot(this);
	}
	public boolean setType(final PlotType type) {
		if (this.type != type) {
			this.lastType = this.type;
			this.type = type;
			Bukkit.getScheduler().runTask(getPlugin(), new Runnable(){
				@Override
				public void run() {
					for (int x = getChunkX(); x < getChunkX()+getWidth(); x++) {
						for (int z = getChunkZ(); z < getChunkZ()+getWidth(); z++) {
							getGenerator().makeFloor(world, null, type.getFloor(), x, z);
							getGenerator().makeRoof(world, null, type.getRoof(), x, z);
							getGenerator().makeWall(null, x, z, type.getOuterwall(), type.getInnerwall(), world);
						}
					}
					for (int x = getChunkX(); x < getChunkX()+getWidth(); x++) {
						getGenerator().makeWall(null, x,getChunkZ()+getWidth(), type.getOuterwall(), type.getInnerwall(), world, true, false);
					}
					for (int z = getChunkZ(); z < getChunkZ()+getWidth(); z++) {
						getGenerator().makeWall(null, getChunkX()+getWidth(),z, type.getOuterwall(), type.getInnerwall(), world, false, true);
					}
					if (lastType == PlotType.EMPTY) return;
					for (Hologram h: holograms) {
						if (h instanceof Tunnel) {
							((Tunnel) h).setType(type);
						}
					}

					replaceBlocks(WallType.valueOf(type.name()),WallType.valueOf(lastType.name()));

				}

				@SuppressWarnings("deprecation")
				private void replaceBlocks(WallType type,
						WallType lasttype) {
					EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), 4096);
					CuboidRegion r = new CuboidRegion(new Vector((getChunkX()*16)+7,3,(getChunkZ()*16)+7),new Vector((getChunkX()*16)+getGenerator().size-1,getGenerator().height-1,(getChunkZ()*16)+getGenerator().size-1));
					for (int i = 0; i < type.getBlocks().length; i++) {
						HashSet<BaseBlock> mask = new HashSet<>();
						mask.add(new BaseBlock(lasttype.getBlocks()[i].getItemTypeId(),lasttype.getBlocks()[i].getData()));
						try {
							es.replaceBlocks(r, mask, new BaseBlock(type.getBlocks()[i].getItemTypeId(),type.getBlocks()[i].getData()));
						} catch (MaxChangedBlocksException e) {
						}
					}
				}


			});
			return true;
		}
		return false;
	}
	public int getWidth() {
		return getGenerator().size/ 16;
	}
	public WonderlandChunkGen getGenerator() {
		return ((WonderlandChunkGen)world.getGenerator());
	}
	public int getChunkX() {
		return coordX*getWidth();
	}
	public int getChunkZ() {
		return coordZ*getWidth();
	}
	@SuppressWarnings("deprecation")
	public void replaceBlocks(MaterialData orig, MaterialData replace) {
		EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), 4096);
		CuboidRegion r = new CuboidRegion(new Vector((getChunkX()*16)+7,3,(getChunkZ()*16)+7),new Vector((getChunkX()*16)+getGenerator().size-1,getGenerator().height-1,(getChunkZ()*16)+getGenerator().size-1));
		HashSet<BaseBlock> mask = new HashSet<>();
		mask.add(new BaseBlock(orig.getItemTypeId(),orig.getData()));
		try {
			es.replaceBlocks(r, mask, new BaseBlock(replace.getItemTypeId(),replace.getData()));
		} catch (MaxChangedBlocksException e) {
		}

	}
	public PlotLocation hasLocation(Location loc) {
		int coordX = ((int) Math.ceil((double)loc.getBlockX()/16d/(double)getWidth()))-1;
		int coordZ = ((int) Math.ceil((double)loc.getBlockZ()/16d/(double)getWidth()))-1;
		int locRelX = loc.getBlockX()-coordX*16*getWidth();
		int locRelZ = loc.getBlockZ()-coordZ*16*getWidth();
		if (coordX == this.coordX && coordZ == this.coordZ) {
			if (locRelX < getGenerator().size && locRelZ < getGenerator().size && locRelX > 6 && locRelZ > 6 ) {
				return PlotLocation.INPLOT;
			}
			if (locRelX == getGenerator().size || locRelZ == getGenerator().size || locRelX > 4 || locRelZ > 4 ) {
				return PlotLocation.WALL;
			}
			return PlotLocation.PATH;
		}
		return PlotLocation.NONE;
	}
	public void printInformationId(CommandSender sender,int id) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
				"tellraw {player} {\"text\":\"\",\"extra\":[{\"text\":\"Plot {id} - Coords: {coords}\",\"color\":\"dark_green\",\"bold\":\"true\",\"underlined\":\"true\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/plot warp {id}\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click to warp to plot {coords}\",\"color\":\"yellow\"}]}}}]}"
				.replace("{player}",sender.getName())
				.replace("{id}",id+"")
				.replace("{coords}","["+coordX+","+coordZ+"]")
				.replace("{title}",getTitle())
				.replace("{subtitle}",getSubTitle())
				.replace("{type}",type == PlotType.EMPTY?"None":getType().name())
				);
		
	}
	public void printInformation(CommandSender sender) {
		sender.sendMessage("==============="+ChatColor.YELLOW+"Plot Information:"+ChatColor.RESET+"===============");
		sender.sendMessage("Plot: ["+coordX+","+coordZ+"]");
		if (type == PlotType.EMPTY) {
			sender.sendMessage("Type: None");
		} else {
			sender.sendMessage("Type: "+type.name().toLowerCase());
		}
		if (owner != null)
			sender.sendMessage("Owner: "+Bukkit.getOfflinePlayer(owner).getName());
		else
			sender.sendMessage("Owner: Nobody");
		sender.sendMessage("Title: "+title);
		sender.sendMessage("Sub Title: "+subTitle);
	}
	public void printInformation(Conversable sender) {
		sender.sendRawMessage("==============="+ChatColor.YELLOW+"Plot Information:"+ChatColor.RESET+"===============");
		sender.sendRawMessage("Plot: ["+coordX+","+coordZ+"]");
		if (type == PlotType.EMPTY) {
			sender.sendRawMessage("Type: None");
		} else {
			sender.sendRawMessage("Type: "+type.name().toLowerCase());
		}
		if (owner != null)
			sender.sendRawMessage("Owner: "+Bukkit.getOfflinePlayer(owner).getName());
		else
			sender.sendRawMessage("Owner: Nobody");
		sender.sendRawMessage("Title: "+title);
		sender.sendRawMessage("Sub Title: "+subTitle);
	}
	public void startGame(final Player... pls) {
		if (!status.equals(PlotStatus.STOPPED)) return;
		status = PlotStatus.STARTING;
		if (players.isEmpty()) {
			for (NPC npc : npcs) {
				if (npc != null) {
					npc.despawn();
				}
			}
			WrappedChatComponent titlec = WrappedChatComponent.fromChatMessage(title)[0];
			WrappedChatComponent subtitlec = WrappedChatComponent.fromChatMessage(subTitle)[0];
			PacketContainer title = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TITLE);
			PacketContainer subtitle = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TITLE);
			title.getTitleActions().write(0, TitleAction.TITLE);
			subtitle.getTitleActions().write(0, TitleAction.SUBTITLE);
			title.getChatComponents().write(0, titlec);
			subtitle.getChatComponents().write(0, subtitlec);
			for (int i = 0; i < pls.length && i <= playerCount; i++) {
				Player p = pls[i];
				p.teleport(startLoc[i]);
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(p, title);
					ProtocolLibrary.getProtocolManager().sendServerPacket(p, subtitle);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100 ,1));
				p.setWalkSpeed(0);
			}

			for (Hologram h :holograms) {
				h.reset();
			}
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

				@Override
				public void run() {
					status = PlotStatus.STARTED;
					for (Player p: pls) {
						p.removePotionEffect(PotionEffectType.BLINDNESS);
						p.setWalkSpeed(0.2f);
					}
					for (WonderlandEntity e: entities) {
						e.spawn();
					}
					for (int i = 0; i < pls.length && i <= playerCount; i++) {
						WonderlandPlayer player = new WonderlandPlayer(pls[i],plugin,i);
						plugin.playerManager.players.add(player);
						players.add(player);
					}
					
					fireCannons();
				}}, 100L);

		}
	}

	public void stopGame() {
		status = PlotStatus.STOPPED;
		for (Iterator<Entry<EntityFireballImpl, Block>> it = fireballs.entrySet().iterator();it.hasNext();) {
			it.next().getKey().getBukkitEntity().remove();
			it.remove();
		}
		for (WonderlandPlayer pl: players) {
			pl.stopGame();
			plugin.playerManager.players.remove(pl);
		}
		players.clear();
		for (Hologram h: holograms) {
			h.reset();
		}
		ListIterator<WonderlandEntity> it = spawnedEntities.listIterator();
		while (it.hasNext()) {
			WonderlandEntity en = it.next();
			en.editorMode();
			it.remove();
		}
		for (NPC npc : npcs) {
			if (npc != null) {
				npc.spawn(npc.getStoredLocation());
			}
		}
	}
	public enum PlotLocation {
		INPLOT,WALL,PATH,NONE
	}
	public enum PlotStatus {
		STOPPED,STARTING,STARTED
	}
	@Getter
	@SuppressWarnings("deprecation")
	public enum PlotType {
		GARDEN(new MaterialData(Material.LEAVES,(byte) 0),new MaterialData(Material.EMERALD_BLOCK.getId(),(byte) 0),
				new MaterialData(0,(byte) 0),new MaterialData(Material.GRASS,(byte) 0)),
				SPOOKY(new MaterialData(98,(byte) 1),new MaterialData(1,(byte) 6)),
				AZTEC(new MaterialData(24,(byte) 2),new MaterialData(179,(byte) 0)),
				WOOD(new MaterialData(5,(byte) 2),new MaterialData(5,(byte) 2),new MaterialData(0,(byte) 0),new MaterialData(126,(byte) 12)),
				CAVE(new MaterialData(Material.NETHERRACK,(byte) 0),new MaterialData(44,(byte) 14)),
				EMPTY(new MaterialData(5,(byte) 2),new MaterialData(5,(byte) 2),new MaterialData(0,(byte) 0),new MaterialData(126,(byte) 12));

		private MaterialData innerwall;
		private MaterialData outerwall;
		private MaterialData roof;
		private MaterialData floor;
		PlotType(MaterialData wall, MaterialData floor) {
			this(wall,wall,wall,floor);
		}
		PlotType(MaterialData innerwall, MaterialData outerwall, MaterialData roof, MaterialData floor) {
			this.innerwall=innerwall;
			this.outerwall=outerwall;
			this.roof=roof;
			this.floor=floor;
		}
	}
	public void respawn() {
		if (status != PlotStatus.STARTED) return;
		status = PlotStatus.STARTING;
		for (Iterator<Entry<EntityFireballImpl, Block>> it = fireballs.entrySet().iterator();it.hasNext();) {
			it.next().getKey().getBukkitEntity().remove();
			it.remove();
		}
		WrappedChatComponent titlec = WrappedChatComponent.fromChatMessage(title)[0];
		WrappedChatComponent subtitlec = WrappedChatComponent.fromChatMessage(subTitle)[0];
		PacketContainer title = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TITLE);
		PacketContainer subtitle = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TITLE);
		title.getTitleActions().write(0, TitleAction.TITLE);
		subtitle.getTitleActions().write(0, TitleAction.SUBTITLE);
		title.getChatComponents().write(0, titlec);
		subtitle.getChatComponents().write(0, subtitlec);
		for (int i = 0; i < players.size(); i++) {
			players.get(i).getPlayer().teleport(startLoc[i]);
			Player p = players.get(i).getPlayer();
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(p, title);
				ProtocolLibrary.getProtocolManager().sendServerPacket(p, subtitle);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1));
			p.setWalkSpeed(0);
		}
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

			@Override
			public void run() {
				status = PlotStatus.STARTED;
				for (WonderlandPlayer p: players) {
					p.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
					p.getPlayer().setWalkSpeed(1);
				}
				for (WonderlandEntity e: entities) {
					e.spawn();
				}
				fireCannons();
			}}, 100L);

		for (WonderlandEntity e: entities) {
			e.despawn();
		}
		for (Hologram h: holograms) {
			h.reset();
		}
		
	}

	public void setStartLoc(Location location, int which) {
		if (which > playerCount) return;
		location = location.getBlock().getLocation().add(0.5,0,0.5);
		startLoc[which-1] = location;
		npcs[which-1].teleport(location, TeleportCause.PLUGIN);

	}
	public void setHelpers(List<String> helperlist) {
		for (String helper: helperlist) {
			helpers.add(UUID.fromString(helper));
		}
	}
	public void addHelper(UUID uuid) {
			helpers.add(uuid);
			save();
	}
	public void removeHelper(UUID uuid) {
		if (!helpers.contains(uuid)) return;
		helpers.remove(uuid);
		save();
}
	public List<String> getHelpersString() {
		List<String> helperlist = new ArrayList<>();
		for (UUID helper: helpers) {
			helperlist.add(helper.toString());
		}
		return helperlist;
	}
	private List<UUID> requestPlayers = new ArrayList<>();
	private List<UUID> acceptPlayers = new ArrayList<>();
	public void requestGame(Player sender, ArrayList<UUID> players) {
		cancelRequest(sender);
		requestPlayers = players;
		sender.sendMessage("Asking players if they want to play. Wait for them to respond.");
		for (UUID u: requestPlayers) {
			if (Bukkit.getPlayer(u) == null){
				cancelRequest(sender);
				return;
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
					"tellraw {player} {\"text\":\"\",\"extra\":[{\"text\":\"{sender}\",\"color\":\"yellow\"},{\"text\":\" would like to play a game of \"},{\"text\":\"Wonderland\",\"color\":\"yellow\",\"bold\":\"true\"},{\"text\":\" With you! \"},{\"text\":\"Click to accept\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/plot accept\"}},{\"text\":\" or \"},{\"text\":\"deny.\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/plot deny\"}}]}"
					.replace("{player}", Bukkit.getPlayer(u).getDisplayName())
					.replace("{sender}", sender.getDisplayName())
					);
		}
	}
	public void cancelRequest(Player sender) {
		for (UUID p : acceptPlayers) {
			Bukkit.getPlayer(p).sendMessage(sender.getDisplayName()+" Has cancelled their request to play a game of wonderland.");
		}
		requestPlayers.clear();
		acceptPlayers.clear();
		return;
	}
	public boolean denyRequest(Player sender) {
		if (!requestPlayers.contains(sender.getUniqueId())) return false;
		for (UUID p : acceptPlayers) {
			Bukkit.getPlayer(p).sendMessage(sender.getDisplayName()+" denied their request to start this game, and so it can't start.");		
		}
		Bukkit.getPlayer(owner).sendMessage(sender.getDisplayName()+" denied their request to start this game, and so it can't start.");		
		requestPlayers.clear();
		acceptPlayers.clear();
		return true;
	}
	public boolean acceptRequest(Player sender) {
		if (!requestPlayers.contains(sender.getUniqueId())) return false;
		acceptPlayers.add(sender.getUniqueId());
		if (requestPlayers.size() == acceptPlayers.size()) {
			Player[] players = new Player[acceptPlayers.size()+1];
			int i = 0;
			for (UUID p : acceptPlayers) {
				Bukkit.getPlayer(p).sendMessage("All players have accepted. Starting game.");
				players[i++]=Bukkit.getPlayer(p);
				}
			players[i++]=Bukkit.getPlayer(owner);
			Bukkit.getPlayer(owner).sendMessage("All players have accepted. Starting game.");
			startGame(players);
		} else {
			for (UUID p : acceptPlayers) {
				Bukkit.getPlayer(p).sendMessage(acceptPlayers.size() +" out of "+requestPlayers.size()+" have joined the game!");		
			}
			Bukkit.getPlayer(owner).sendMessage(acceptPlayers.size() +" out of "+requestPlayers.size()+" have joined the game!");	
		}
		
		return true;
	}


}
