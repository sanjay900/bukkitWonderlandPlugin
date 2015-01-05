package com.sanjay900.wonderland.hologram;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import com.sanjay900.wonderland.Wonderland;


public class ElectroConversation {
	private ConversationFactory conversationFactory;
	public ElectroConversation(Wonderland plugin) {
		this.conversationFactory = new ConversationFactory(plugin)
		.withModality(true)
		.withFirstPrompt(new SetTimerPrompt())
		.thatExcludesNonPlayersWithMessage("This command requires a player");
	}
	public void startConversation(CommandSender commandSender, Electro electro) {
		if (commandSender instanceof Conversable) {
			Conversation convo = conversationFactory.buildConversation((Conversable)commandSender);
			convo.getContext().setSessionData("electro", electro);
			convo.begin();

		} 
	}

	private class SetTimerPrompt extends StringPrompt {
		public String getPromptText(ConversationContext context) {

			context.getForWhom().sendRawMessage("Electricity blocks turn on and off in time to 5 periods.");
			context.getForWhom().sendRawMessage("Type the periods, seperated by a , that this block should turn on and off");
			return "For example, for a block that stays on forever, type 1,2,3,4,5";
		}
		@Override
		public Prompt acceptInput(ConversationContext context, String s) {
			String fin = null;
			ArrayList<Integer> periods = new ArrayList<>();
			if (!s.contains(",")) {
				try {
					int i = Integer.parseInt(s);
					periods.add(i);
					fin = i+"";
					
				} catch (NumberFormatException ex){
					context.getForWhom().sendRawMessage("Incorrect arguments were typed. Exiting conversation.");
					return Prompt.END_OF_CONVERSATION;
				}
			}
			else {
				for (String split: s.split(",")) {
					try {
						int i = Integer.parseInt(split);
						periods.add(i);
						if (fin == null) 
						{
							fin = i+"";
						} else {
							fin += ","+i;
						}
					} catch (NumberFormatException ex){
						context.getForWhom().sendRawMessage("Incorrect arguments were typed. Exiting conversation.");
						return Prompt.END_OF_CONVERSATION;
					}
				}
			}
			context.getForWhom().sendRawMessage("Periods set to: "+ fin);
			((Electro)context.getSessionData("electro")).setTime(periods);
			return Prompt.END_OF_CONVERSATION;
			
		}
	}

}
