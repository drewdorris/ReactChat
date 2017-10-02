package me.stupiddrew9.reactchat.listeners;

import java.util.List;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.stupiddrew9.reactchat.React;
import me.stupiddrew9.reactchat.util.InvUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatListener implements Listener {

	private static int limit = React.getMessageMax();
	private static ArrayList<String> msgs = new ArrayList<String>(limit);
	private static HashMap<String, Integer> amntDupeMsgs = new HashMap<String, Integer>(limit);
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		Inventory inv = null;
		Inventory invMenu = null;
		Player user = event.getPlayer();
		String username = event.getPlayer().getName();
		String sentMessage = event.getMessage();
		String messageWName = String.format(event.getFormat(), event.getPlayer().getDisplayName(), "");
		String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
		String invTitle = (messageWName + ChatColor.DARK_GRAY + InvUtil.invName(messageWName, sentMessage));
		
		TextComponent newMessage = new TextComponent(message);
		
		if (!(user.hasPermission("reactchat.receive"))) {
			return;
		}
		
		if (Pattern.compile(".*[a-z].*" + ".*[a-z].*" + "[.]" + ".*[a-z].*" + ".*[a-z].*").matcher(sentMessage).find()) {
			
			return;
			
		} else {
			
			newMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(ChatColor.GOLD + "Click to react to " + ChatColor.YELLOW + username).create()));
			newMessage.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/reactchat " + React.getTotalMsgs())));
			newMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(ChatColor.GOLD + "Click to react to " + ChatColor.YELLOW + username).create()));
			newMessage.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/reactchat " + React.getTotalMsgs())));
			
		}
		
		event.setCancelled(true);

		if (React.getInventories().size() < limit) {
			
			if (msgs.contains(invTitle)) {
				
				int j = amntDupeMsgs.get(invTitle);
				amntDupeMsgs.put(invTitle, j + 1);
				String numDupeString = (ChatColor.WHITE + "(" + Integer.toString(j) + ") ");
				inv = Bukkit.createInventory(null, 27, (numDupeString + invTitle));	
				invMenu = Bukkit.createInventory(null, 54, (numDupeString + invTitle));	
				
			} else {		
				
				inv = Bukkit.createInventory(null, 27, invTitle);
				invMenu = Bukkit.createInventory(null, 54, invTitle);	
				amntDupeMsgs.put(invTitle, 0);
				
			}
			
			// add to check for the if statement 
			// array of item names for reactions list
			ArrayList<ArrayList<String>> invArray = new ArrayList<ArrayList<String>>(27);
			// set items in the inv
			InvUtil.defineInvContents(invArray, inv, user);
			InvUtil.defineInvMenuContents(invArray, invMenu, user);
			// store reaction count for each player
			HashMap<String, Integer> reactCount = new HashMap<String, Integer>();
			// array of reactCounts, set in position of currentInv
			React.getReactCount().add(React.getCurrentInv(), reactCount);
			// reactions added to the msg + amnt
			LinkedHashMap<ItemStack, List<String>> addedReactions = new LinkedHashMap<ItemStack, List<String>>(27);
			React.getAddedReactions().add(React.getCurrentInv(), addedReactions);
			// array of inventories
			React.getInventories().add(React.getCurrentInv(), inv);
			React.getInventoriesMenu().add(React.getCurrentInv(), invMenu);
			// identify an inventory w the currentInv
			React.getInvIdentity().put(inv, React.getCurrentInv());
			React.getInvMenuIdentity().put(invMenu, React.getCurrentInv());
			// assign an inv with the msg creator
			React.getPlayersHash().put(inv, user);
			// assign an inv with the msg created
			React.getMsgHash().put(inv, message);
			
		}

		if (React.getInventories().size() == limit) {
			
			React.getInventories().get(React.getCurrentInv()).equals(null);
			
			if (msgs.contains(invTitle)) {
				
				int j = amntDupeMsgs.get(invTitle);
				amntDupeMsgs.put(invTitle, j + 1);
				String numDupeString = (ChatColor.WHITE + "(" + Integer.toString(j) + ") ");
				inv = Bukkit.createInventory(null, 27, (numDupeString + invTitle));
				invMenu = Bukkit.createInventory(null, 54, (numDupeString + invTitle));	
				
			} else {
				
				inv = Bukkit.createInventory(null, 27, invTitle);
				invMenu = Bukkit.createInventory(null, 54, invTitle);	
				amntDupeMsgs.put(invTitle, 0);
				
			}
			
			ArrayList<ArrayList<String>> invArray = new ArrayList<ArrayList<String>>(27);
			InvUtil.defineInvContents(invArray, inv, user);
			InvUtil.defineInvMenuContents(invArray, invMenu, user);
			HashMap<String, Integer> reactCount = new HashMap<String, Integer>();
			React.getReactCount().set(React.getCurrentInv(), reactCount);
			LinkedHashMap<ItemStack, List<String>> addedReactions = new LinkedHashMap<ItemStack, List<String>>(27);
			React.getAddedReactions().set(React.getCurrentInv(), addedReactions);
			React.getInventories().set(React.getCurrentInv(), inv);
			React.getInventoriesMenu().set(React.getCurrentInv(), invMenu);
			React.getInvIdentity().put(inv, React.getCurrentInv());
			React.getInvMenuIdentity().put(invMenu, React.getCurrentInv());
			React.getPlayersHash().put(inv, user);
			React.getMsgHash().put(inv, message);
			
		}

		React.totalMsgs++;
		React.currentInv++;
		
		if (React.currentInv >= limit) {
			
			int goBackToZero = 0;
			React.currentInv = goBackToZero;
			React.invMult++;
			
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(newMessage);
		}

		Bukkit.getServer().getConsoleSender().sendMessage(message);

	}

}
