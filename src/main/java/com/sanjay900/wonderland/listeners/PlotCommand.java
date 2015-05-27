package com.sanjay900.wonderland.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.sanjay900.wonderland.Wonderland;
import com.sanjay900.wonderland.plots.Plot;
import com.sanjay900.wonderland.plots.Plot.PlotType;
import com.sanjay900.wonderland.plots.WonderlandChunkGen;

public class PlotCommand implements CommandExecutor {
	Wonderland plugin = Wonderland.getInstance();
	private ConversationFactory conversationFactory;
	public PlotCommand() {
		this.conversationFactory = new ConversationFactory(plugin)
		.withModality(true)
		.withPrefix(new SummoningConversationPrefix())
		.withFirstPrompt(new WhichPlotPrompt())
		.thatExcludesNonPlayersWithMessage("This command requires a player");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandStr,
			String[] args) {
		Plot plot = plugin.plotManager.getPlotInside(((Player) sender).getLocation());
		World world = ((Player) sender).getWorld();
		if (!(world.getGenerator() instanceof WonderlandChunkGen)) {
			sender.sendMessage("The "+ChatColor.BLUE+"plot"+ChatColor.RESET+" command can only be used on a plot world.");
			return true;
		}
		if ((args.length == 1 && args[0].equalsIgnoreCase("help"))||(args.length == 0 && plot == null)) {
			sender.sendMessage("==============="+ChatColor.YELLOW+"Plot Help:"+ChatColor.RESET+"===============");
			sender.sendMessage("If a plot has a roof, right click it to enter it!");
			sender.sendMessage(ChatColor.BLUE+"/plot warp [id]"+ChatColor.RESET+" - Warp to a plot you own by id (default id 0)");
			sender.sendMessage(ChatColor.BLUE+"/plot warp <coordinates>"+ChatColor.RESET+" - Warp to a plot by coordinates.");
			sender.sendMessage(ChatColor.BLUE+"/plot list "+ChatColor.RESET+" - List all the plots you own");
			sender.sendMessage(ChatColor.BLUE+"/plot accept "+ChatColor.RESET+" - Accept a request to play wonderland!");
			sender.sendMessage(ChatColor.BLUE+"/plot deny "+ChatColor.RESET+" - Deny a request to play wonderland!");
			if (plot == null) {
				return true;
			}
		}
		if (args.length == 1 && args[0].equals("accept")) {
			for (Plot p: plugin.plotManager.plots) {
				if (p.getRequestPlayers().contains(((Player)sender).getUniqueId())) {
					p.acceptRequest((Player)sender);
					return true;
				}
			}
			sender.sendMessage("You don't have any requests at the moment.");
			return true;
		}
		if (args.length == 1 && args[0].equals("deny")) {
			for (Plot p: plugin.plotManager.plots) {
				if (p.getRequestPlayers().contains(((Player)sender).getUniqueId())) {
					p.denyRequest((Player)sender);
					return true;
				}
			}
			sender.sendMessage("You don't have any requests at the moment.");
			return true;
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			sender.sendMessage("==============="+ChatColor.YELLOW+"Plot List:"+ChatColor.RESET+"===============");
			sender.sendMessage("You have claimed "+plugin.plotManager.getOwnedPlots((Player) sender).size()+" plots out of "+(sender.isOp()?"infinite":"5")+" plots.");
			if (!plugin.plotManager.getOwnedPlots((Player) sender).isEmpty())
				sender.sendMessage("Click on a plot to warp to it.");
			int i = 0;
			for (Plot p:plugin.plotManager.getOwnedPlots((Player) sender)) {
				p.printInformationId(sender,i++);
			}
			sender.sendMessage("==============="+ChatColor.YELLOW+"Helper Plots:"+ChatColor.RESET+"===============");
			i = 0;
			for (Plot p:plugin.plotManager.getHelperPlots((Player) sender)) {
				p.printInformationId(sender,i++);
			}
			return true;
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("warp")) {
			if (plugin.plotManager.getOwnedPlots((Player) sender).isEmpty()) {
				sender.sendMessage("You currently don't own any plots. Claim one before attempting to warp to it.");
				return true;
			} else {
				Plot p = plugin.plotManager.getOwnedPlots((Player) sender).get(0);
				((Player) sender).teleport(p.getStartLoc()[0]);
			}
			return true;
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("warp")) {
			Plot p;
			try {
				int i = Integer.parseInt(args[1]);
				if (i+1 > plugin.plotManager.getOwnedPlots((Player) sender).size()) {
					sender.sendMessage("You do not own a plot with the id "+i+". Check ./plot list for a list of plots.");
					return true;
				}
				p = plugin.plotManager.getOwnedPlots((Player) sender).get(i);
				((Player) sender).sendMessage("Teleporting to plot: ["+p.getCoordX()+","+p.getCoordZ()+"]");
				((Player) sender).teleport(p.getStartLoc()[0]);
				return true;
			} catch (NumberFormatException ex) {
				if (args[1].split(",").length == 2) {
					try {
						int x = Integer.parseInt(args[1].split(",")[0]);
						int z= Integer.parseInt(args[1].split(",")[1]);
						p = plugin.plotManager.getPlot(x, z, ((Player) sender).getWorld());
					} catch (NumberFormatException ex2) {
						sender.sendMessage(args[1]+" is not a valid id or coordinate");
						return true;
					}
				} else {
					sender.sendMessage(args[1]+" is not a valid id or coordinate");
					return true;
				}

				((Player) sender).sendMessage("Teleporting to plot: ["+p.getCoordX()+","+p.getCoordZ()+"]");
				((Player) sender).teleport(p.getStartLoc()[0]);
			}
			return true;
		}

		if (plot == null) {
			sender.sendMessage("The "+ChatColor.BLUE+"plot"+ChatColor.RESET+" command can only be used while in a plot.");
			return true;
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(ChatColor.BLUE+"/plot"+ChatColor.RESET+" - Get information about the plot you are standing in.");
		}

		if (args.length == 0) {
			plot.printInformation(sender);
			sender.sendMessage("Type "+ChatColor.BLUE+"/plot help"+ChatColor.RESET+" for a list of plot related commands.");
			return true;
		}

		if (plot.getOwner() == null) {
			if (!sender.hasPermission("wonderland.claim")) {
				sender.sendMessage("You don't have permission to run this command");
				return true;
			}
			if (sender.isOp() || plugin.plotManager.getOwnedPlots((Player) sender).size() < 5) {
				if (args.length == 1 && args[0].equalsIgnoreCase("claim")) {
					sender.sendMessage("You claimed the plot ["+plot.getCoordX()+","+plot.getCoordZ()+"]");
					plot.setOwner(((Player) sender).getUniqueId());
					sender.sendMessage("We will now configure your plot");
					startConversation(sender,plot);
				} else {
					if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
						sender.sendMessage(ChatColor.BLUE+"/plot claim"+ChatColor.RESET+" - Claim the plot you are standing in and configure it.");
					}
					else {
						sender.sendMessage("This plot does not belong to anybody. "+ChatColor.BLUE+"/plot claim"+ChatColor.RESET+" it to edit it's settings and build in it.");
					}
				}
			}
			else
				sender.sendMessage("You currently own too many plots. You can not claim any more plots.");
			return true;
		}
		if (plot.getOwner().compareTo(((Player) sender).getUniqueId()) !=0) {
			if (sender.isOp()) {
				sender.sendMessage("This plot belongs to somebody else. As you are an op, you can still edit it.");
			} else {
				sender.sendMessage("This plot belongs to somebody else. You may not edit it.");
				plot.printInformation(sender);
				return true;
			}
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(ChatColor.BLUE+"/plot players <playeramount>"+ChatColor.RESET+" - Set the amount of players this level requires");
			sender.sendMessage(ChatColor.BLUE+"/plot play"+ChatColor.RESET+" - Start a game of wonderland on your own");
			sender.sendMessage(ChatColor.BLUE+"/plot play <player2> [player3] [player4]"+ChatColor.RESET+" - Start a game of wonderland with other players");
			sender.sendMessage(ChatColor.BLUE+"/plot leave"+ChatColor.RESET+" - Stop playing a game of wonderland");
			sender.sendMessage(ChatColor.BLUE+"/plot title"+ChatColor.RESET+" - Set your plot's title");
			sender.sendMessage(ChatColor.BLUE+"/plot subtitle"+ChatColor.RESET+" - Set your plot's subtitle");
			sender.sendMessage(ChatColor.BLUE+"/plot type ("+getPlotTypes()+")"+ChatColor.RESET+" - Set your plot's type");
			sender.sendMessage(ChatColor.BLUE+"/plot configure"+ChatColor.RESET+" - run through the configuration menu again");
			sender.sendMessage(ChatColor.BLUE+"/plot pos1"+ChatColor.RESET+" - set player one's start position");
			sender.sendMessage(ChatColor.BLUE+"/plot pos2"+ChatColor.RESET+" - set player two's start position");
			sender.sendMessage(ChatColor.BLUE+"/plot pos3"+ChatColor.RESET+" - set player three's start position");
			sender.sendMessage(ChatColor.BLUE+"/plot pos4"+ChatColor.RESET+" - set player four's start position");
			sender.sendMessage(ChatColor.BLUE+"/plot star <levelCoordX,LevelCoordZ>"+ChatColor.RESET+" - Set the location of the star and the level it points towards");
			sender.sendMessage(ChatColor.BLUE+"/plot helper"+ChatColor.RESET+" - Show all helpers");
			sender.sendMessage(ChatColor.BLUE+"/plot helper add <playername>"+ChatColor.RESET+" - Add a player as a helper");
			sender.sendMessage(ChatColor.BLUE+"/plot helper remove <playername>"+ChatColor.RESET+" - remove a player from your helper list");
			return true;
		}
		if (args[0].equalsIgnoreCase("play")) {
			if (args.length == 1) {
				plot.startGame((Player)sender);
			} else {
				ArrayList<UUID> players = new ArrayList<>();
				for (int i = 1; i < args.length; i++) {
					if (Bukkit.getPlayer(args[i]) != null) {
						players.add(Bukkit.getPlayer(args[i]).getUniqueId());	
					} else {
						sender.sendMessage("The player "+args[i]+" does not appear to be online.");
					}

				}
				if (players.size() +1 != plot.getPlayerCount()) {
					sender.sendMessage("You did not supply enough players. Supplied "+players.size()+", expected "+plot.getPlayerCount()+".");
					return true;
				}
				plot.requestGame((Player)sender,players);
				
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("helper")) {
			if (args.length == 1) {
				sender.sendMessage("==============="+ChatColor.YELLOW+"Helper List:"+ChatColor.RESET+"===============");
				for (UUID u: plot.getHelpers()) {
					sender.sendMessage(Bukkit.getOfflinePlayer(u).getName());
				}
				return true;
			} 
			if (args.length == 3) {
				if (args[1].equalsIgnoreCase("add")) {
					Player pl = Bukkit.getPlayer(args[2]);
					if (pl != null) {
						if (plot.getHelpers().contains(pl.getUniqueId())) {
							plot.addHelper(pl.getUniqueId());
							pl.sendMessage("The player "+sender.getName()+" has added you to their plot as a helper!");
						} else {
							sender.sendMessage("The player "+args[2]+" is already a helper on your plot!");
						}
					} else {
						sender.sendMessage("The player "+args[2]+" could not be found. Are they online?");
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("remove")) {
					Iterator<UUID> it = plot.getHelpers().iterator();

					while (it.hasNext()) {
						UUID u = it.next();
						OfflinePlayer pl  = Bukkit.getOfflinePlayer(u);
						if (pl.getName().equals(args[2])) {
							if (Bukkit.getPlayer(u) != null) {
								Bukkit.getPlayer(u).sendMessage("The player "+sender.getName()+" has removed you from their plot!");
							}
							it.remove();
							plot.save();
							return true;
						}
					}
					sender.sendMessage("The player "+args[2]+" is not a helper on your plot!");

					return true;
				}
			}
		}
		if (args[0].equalsIgnoreCase("leave")) {
			plot.stopGame();
			return true;
		}
		if (args[0].equalsIgnoreCase("pos1")) {
			plot.setStartLoc(((Player)sender).getLocation(),1);
			plot.save();
			return true;
		}
		if (args[0].equalsIgnoreCase("star")) {
			if (args.length == 2&&args[1].matches("\\d*,\\d*")) {
				if (plot.getCoordX() == Integer.parseInt(args[1].split(",")[0]) && plot.getCoordX() == Integer.parseInt(args[1].split(",")[1])){
					sender.sendMessage("You have attemped to set the next level to the current level. You are not allowed to do this.");
					return true;
				}
				plot.setStar(args[1],((Player)sender).getLocation());
				plot.save();
			} else if (args.length == 2&&args[1].equalsIgnoreCase("help")){
				sender.sendMessage(ChatColor.BLUE+"/plot star <levelCoordX,LevelCoordZ>"+ChatColor.RESET+" - Set the location of the star and the level it points towards");
				sender.sendMessage("Where <levelCoordX,LevelCoordZ> is the coordinate of the level you want this level to go to after it is finished.");
				sender.sendMessage("For example, 0,0 is the coordinates for the ending level");
				sender.sendMessage("Make sure you stand where you want your star to be.");
				sender.sendMessage("Chunk coordinates are listed in the sidebar when you fly over a plot.");
			} else {
				sender.sendMessage("Type " +ChatColor.BLUE+"/plot star help"+ChatColor.RESET+" for information on how to use this command.");
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("pos2")) {
			plot.setStartLoc(((Player)sender).getLocation(),2);
			plot.save();
			return true;
		}
		if (args[0].equalsIgnoreCase("pos3")) {
			plot.setStartLoc(((Player)sender).getLocation(),3);
			plot.save();
			return true;
		}
		if (args[0].equalsIgnoreCase("pos4")) {
			plot.setStartLoc(((Player)sender).getLocation(),4);
			plot.save();
			return true;
		}
		if (args[0].equalsIgnoreCase("leave")) {
			plot.stopGame();
			return true;
		}
		if (args[0].equalsIgnoreCase("title")) {
			if (args.length == 1) {
				sender.sendMessage("The command "+ChatColor.BLUE+"/plot title <title>"+ChatColor.RESET+" requires a title");
				return true;
			}
			plot.setTitle(args[1]);
			plot.save();
			return true;
		}
		if (args[0].equalsIgnoreCase("players")) {
			if (args.length == 1) {
				sender.sendMessage("The command "+ChatColor.BLUE+"/plot players <playeramount>"+ChatColor.RESET+" requires an amount of players!");
				return true;
			}
			try {
				plot.setPlayerCount(Integer.parseInt(args[1]));
			} catch (NumberFormatException ex) {
				sender.sendMessage("The command "+ChatColor.BLUE+"/plot players <playeramount>"+ChatColor.RESET+" expects an amount of players!");
			}
			plot.save();
			return true;
		}
		if (args[0].equalsIgnoreCase("subtitle")) {
			if (args.length == 1) {
				sender.sendMessage("The command "+ChatColor.BLUE+"/plot title <title>"+ChatColor.RESET+" requires a title");
				return true;
			}
			plot.setSubTitle(args[1]);
			plot.save();
			return true;
		}
		if (args[0].equalsIgnoreCase("configure")) {
			startConversation(sender,plot);
			return true;
		}

		if (args[0].equalsIgnoreCase("type")) {
			if (args.length == 1) {
				sender.sendMessage("The command "+ChatColor.BLUE+"/plot type <type>"+ChatColor.RESET+" requires a type");
				String plottypes = "Please try one of the following: ["+getPlotTypes()+"].";
				sender.sendMessage(plottypes);
				return true;
			}
			PlotType type;
			try {
				type = PlotType.valueOf(args[1].toUpperCase());
				if (type == PlotType.EMPTY) {
					String plottypes = "The plot type "+args[1]+" was not recognised. Please try one of the following: ["+getPlotTypes()+"]";
					sender.sendMessage(plottypes);
					return true;
				}
			} catch (IllegalArgumentException ex) {
				String plottypes = "The plot type "+args[1]+" was not recognised. Please try one of the following: ["+getPlotTypes()+"]";
				sender.sendMessage(plottypes);
				return true;
			}


			if (plot.setType(type)) {
				plot.save();
				sender.sendMessage("Setting plot ["+plot.getCoordX()+","+plot.getCoordZ()+"] to "+type.name().toLowerCase());
			} else {
				sender.sendMessage("The plot ["+plot.getCoordX()+","+plot.getCoordZ()+"] already has the plot type of "+type.name().toLowerCase());
			}
			return true;

		}


		return false;
	}
	private String getPlotTypes() {
		String plottypes = "";
		for (PlotType p : PlotType.values()) {
			if (p == PlotType.EMPTY) continue;
			if (p.ordinal() == 0) {
				plottypes += p.name().toLowerCase();
			} else {
				plottypes += ","+p.name().toLowerCase();
			}
		}
		return plottypes;
	}
	private ArrayList<String> getPlotArray() {
		ArrayList<String> plottypes = new ArrayList<>();
		for (PlotType p : PlotType.values()) {
			if (p == PlotType.EMPTY) continue;
			plottypes.add(p.name().toLowerCase());
		}
		return plottypes;
	}

	public void startConversation(CommandSender commandSender, Plot plot) {
		if (commandSender instanceof Conversable) {
			Conversation convo = conversationFactory.buildConversation((Conversable)commandSender);
			convo.getContext().setSessionData("plot", plot);
			convo.begin();

		} 
	}
	private class WhichPlotPrompt extends FixedSetPrompt {
		public WhichPlotPrompt() {
			super(getPlotArray().toArray(new String[getPlotArray().size()]));
		}
		public String getPromptText(ConversationContext context) {
			return "What type of Level are you creating? Pick from " + formatFixedSet()+":";
		}
		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, String s) {
			Plot plot = (Plot) context.getSessionData("plot");
			plot.setType(PlotType.valueOf(s.toUpperCase()));
			return new TitlePrompt();
		}
	}
	private class TitlePrompt extends StringPrompt {
		public String getPromptText(ConversationContext context) {
			return "What is the title of your level (shown via minecraft's title feature)";
		}
		@Override
		public Prompt acceptInput(ConversationContext context, String s) {
			Plot plot = (Plot) context.getSessionData("plot");
			plot.setTitle(s);
			return new SubTitlePrompt();
		}
	}
	private class SubTitlePrompt extends StringPrompt {
		public String getPromptText(ConversationContext context) {
			return "What is the subtitle of your level";
		}
		@Override
		public Prompt acceptInput(ConversationContext context, String s) {
			Plot plot = (Plot) context.getSessionData("plot");
			plot.setTitle(s);
			return new PlayersPrompt();
		}
	}
	private class PlayersPrompt extends NumericPrompt {
		public String getPromptText(ConversationContext context) {
			return "How many players does your level support? [1-4]";
		}
		@Override
		protected Prompt acceptValidatedInput(ConversationContext context,
				Number arg1) {
			Plot plot = (Plot) context.getSessionData("plot");
			plot.setPlayerCount((int) arg1);
			context.getForWhom().sendRawMessage("Your plot has been configured!");
			plot.printInformation(context.getForWhom());
			context.getForWhom().sendRawMessage("If anything is wrong, use the commands in "+ChatColor.BLUE+"/plot help"+ChatColor.RESET+" to correct problems.");
			context.getForWhom().sendRawMessage("Check ./plot help for more settings you can change.");
			plot.save();
			return Prompt.END_OF_CONVERSATION;
		}
	}

	private class SummoningConversationPrefix implements ConversationPrefix {
		public String getPrefix(ConversationContext context) {
			return ChatColor.YELLOW + "Plot Configuration: " + ChatColor.WHITE;
		}
	}

}
