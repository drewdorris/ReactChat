package me.stupiddrew9.reactchat.listeners;

import java.util.ArrayList;
import java.util.HashMap;

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

	private static ArrayList<String> msgs = new ArrayList<String>(50);
	private static HashMap<String, Integer> amntDupeMsgs = new HashMap<String, Integer>(50);
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		Inventory inv = null;
		Inventory invMenu = null;
		Player user = event.getPlayer();
		String username = event.getPlayer().getName();
		String sentMessage = event.getMessage();
		String sentMessageWName = String.format(event.getFormat(), event.getPlayer().getDisplayName(), "");
		String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
		String invTitle = (sentMessageWName + ChatColor.DARK_GRAY + InvUtil.invName(sentMessageWName, sentMessage));
		
		TextComponent newMessage = new TextComponent(message);

		newMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(ChatColor.GOLD + "Click to react to " + ChatColor.YELLOW + username).create()));
		newMessage.setClickEvent(
				new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/reactchat " + React.totalMsgs)));

		event.setCancelled(true);

		if (React.getInventories().size() < 50) {
			if (msgs.contains(invTitle)) {
				int j = amntDupeMsgs.get(invTitle);
				amntDupeMsgs.put(invTitle, j + 1);
				String numDupeString = (ChatColor.WHITE + "(" + Integer.toString(j) + ") ");
				inv = Bukkit.createInventory(null, 27, (numDupeString + invTitle));	
				invMenu = Bukkit.createInventory(null, 54, ("M " + numDupeString + invTitle));	
			} else {		
				inv = Bukkit.createInventory(null, 27, invTitle);
				invMenu = Bukkit.createInventory(null, 54, "M " + invTitle);	
				amntDupeMsgs.put(invTitle, 0);
			}
			// add to check for the if statement 
			// array of item names for reactions list
			ArrayList<ArrayList<String>> invArray = new ArrayList<ArrayList<String>>(27);
			// set items in the inv
			InvUtil.defineInvContents(invArray, inv, user);
			InvUtil.defineInvMenuContents(invArray, invMenu, user);
			// array of invArrays, set in position of currentInv
			React.getClickedPlayersInv().add(React.getCurrentInv(), invArray);
			// store reaction count for each player
			HashMap<Player, Integer> reactCount = new HashMap<Player, Integer>();
			// array of reactCounts, set in position of currentInv
			React.getReactCount().add(React.getCurrentInv(), reactCount);
			// reactions added to the msg + amnt
			HashMap<ItemStack, ArrayList<String>> addedReactions = new HashMap<ItemStack, ArrayList<String>>(27);
			React.getAddedReactions().add(React.getCurrentInv(), addedReactions);
			// array of inventories
			React.getInventories().add(React.getCurrentInv(), inv);
			React.getInventoriesMenu().add(React.getCurrentInv(), invMenu);
			// stores players list w/ item
			HashMap<ItemStack, ArrayList<String>> playersAndItem = new HashMap<ItemStack, ArrayList<String>>(27);
			React.getPlayersWithReaction().add(playersAndItem);
			// identify an inventory w the currentInv
			React.getInvIdentity().put(inv, React.getCurrentInv());
			React.getInvMenuIdentity().put(invMenu, React.getCurrentInv());
			// assign an inv with the msg creator
			React.getPlayersHash().put(inv, user);
			// assign an inv with the msg created
			React.getMsgHash().put(inv, message);
		}

		if (React.getInventories().size() == 50) {
			React.getInventories().get(React.getCurrentInv()).equals(null);
			if (msgs.contains(invTitle)) {
				int j = amntDupeMsgs.get(invTitle);
				amntDupeMsgs.put(invTitle, j + 1);
				String numDupeString = (ChatColor.WHITE + "(" + Integer.toString(j) + ") ");
				inv = Bukkit.createInventory(null, 27, (numDupeString + invTitle));
				invMenu = Bukkit.createInventory(null, 54, ("M " + numDupeString + invTitle));	
			} else {
				inv = Bukkit.createInventory(null, 27, invTitle);
				invMenu = Bukkit.createInventory(null, 54, "M " + invTitle);	
				amntDupeMsgs.put(invTitle, 0);
			}
			ArrayList<ArrayList<String>> invArray = new ArrayList<ArrayList<String>>(27);
			InvUtil.defineInvContents(invArray, inv, user);
			InvUtil.defineInvMenuContents(invArray, invMenu, user);
			React.getClickedPlayersInv().set(React.getCurrentInv(), invArray);
			HashMap<Player, Integer> reactCount = new HashMap<Player, Integer>();
			React.getReactCount().set(React.getCurrentInv(), reactCount);
			HashMap<ItemStack, ArrayList<String>> addedReactions = new HashMap<ItemStack, ArrayList<String>>(27);
			React.getAddedReactions().set(React.getCurrentInv(), addedReactions);
			React.getInventories().set(React.getCurrentInv(), inv);
			HashMap<ItemStack, ArrayList<String>> playersAndItem = new HashMap<ItemStack, ArrayList<String>>(27);
			React.getPlayersWithReaction().set(React.getCurrentInv(), playersAndItem);
			React.getInventoriesMenu().set(React.getCurrentInv(), invMenu);
			React.getInvIdentity().put(inv, React.getCurrentInv());
			React.getInvMenuIdentity().put(invMenu, React.getCurrentInv());
			React.getPlayersHash().put(inv, user);
			React.getMsgHash().put(inv, message);
		}

		React.totalMsgs++;
		React.currentInv++;
		if (React.currentInv >= 50) {
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
