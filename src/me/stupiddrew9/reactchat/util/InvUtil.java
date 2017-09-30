package me.stupiddrew9.reactchat.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.stupiddrew9.reactchat.React;
import me.stupiddrew9.reactchat.util.SkullUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class InvUtil {
	
	public static ItemStack[] items = new ItemStack[18]; 
	public static ItemMeta[] itemMeta = new ItemMeta[18];
	
	/**
	 * Shortens length of msg for setting as inventory name
	 * @param namemsg
	 * @param msg
	 * @return msg
	 */
	public static String invName(String namemsg, String msg) {

		String fullmsg = (namemsg + msg);
		
		if (fullmsg.length() > 29) {
			
			if (msg.length() < 13) {
				return msg;
			}
			
			return (msg.substring(0, 12) + "...");
			
		} else {
			
			return msg;
			
		}

	}

	/**
	 * Sets default contents of the reaction window
	 * @param invArray
	 * @param inventory
	 * @param player
	 */
	public static void defineInvContents(ArrayList<ArrayList<String>> invArray, Inventory inventory, Player player) {

		ItemStack blackGlassItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		ItemMeta blackGlassItemMeta = blackGlassItem.getItemMeta();
		blackGlassItemMeta.setDisplayName(ChatColor.BLACK + " ");
		blackGlassItem.setItemMeta(blackGlassItemMeta);
		
		ItemStack moreItem = new ItemStack(Material.TOTEM, 1);
		ItemMeta moreItemMeta = moreItem.getItemMeta();
		moreItemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Add a Reaction");
		moreItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		moreItem.setItemMeta(moreItemMeta);
		
		inventory.setItem(18, blackGlassItem);
		inventory.setItem(19, blackGlassItem);
		inventory.setItem(20, blackGlassItem);
		inventory.setItem(21, blackGlassItem);
		inventory.setItem(22, moreItem);
		inventory.setItem(23, blackGlassItem);
		inventory.setItem(24, blackGlassItem);
		inventory.setItem(25, blackGlassItem);
		inventory.setItem(26, blackGlassItem);
		
	}
	
	/**
	 * Sets default contents of the reaction menu window
	 * @param invArray
	 * @param inventory
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public static void defineInvMenuContents(ArrayList<ArrayList<String>> invArray, Inventory inventory, Player player) {

		ItemStack blackGlassItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		ItemMeta blackGlassItemMeta = blackGlassItem.getItemMeta();
		blackGlassItemMeta.setDisplayName(ChatColor.BLACK + " ");
		blackGlassItem.setItemMeta(blackGlassItemMeta);
		
		ItemStack moreItem = new ItemStack(Material.TOTEM, 1);
		ItemMeta moreItemMeta = moreItem.getItemMeta();
		moreItemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Hide Menu");
		moreItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		moreItem.setItemMeta(moreItemMeta);

		inventory.setItem(18, blackGlassItem);
		inventory.setItem(19, blackGlassItem);
		inventory.setItem(20, blackGlassItem);
		inventory.setItem(21, blackGlassItem);
		inventory.setItem(22, moreItem);
		inventory.setItem(23, blackGlassItem);
		inventory.setItem(24, blackGlassItem);
		inventory.setItem(25, blackGlassItem);
		inventory.setItem(26, blackGlassItem);

		for (int i = 0; i < 18; i++) {
			
			ArrayList<String> eachItem = new ArrayList<String>(18);
			invArray.add(eachItem);
			
			if (React.reactNames[i] == null) {
				continue;
			}
			
			String username = player.getName();
			
			if (!(React.skullIDs[i] == null)) {
				
				items[i] = new ItemStack(397, 1, (short) 3);
				itemMeta[i] = items[i].getItemMeta();
				itemMeta[i].setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + React.reactNames[i]);
				itemMeta[i].setLore(Arrays.asList(ChatColor.GOLD + "React to " + ChatColor.YELLOW + username 
				                                + ChatColor.GOLD + "'s message"));
				items[i].setItemMeta(itemMeta[i]);
				SkullUtil.getCustomSkull(React.skullIDs[i], items[i]);
				inventory.setItem(i + 27, items[i]);
				
				continue;
				
			}
			
			items[i] = new ItemStack(React.itemIDs[i], 1);
			itemMeta[i] = items[i].getItemMeta();
			itemMeta[i].setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + React.reactNames[i]);
			itemMeta[i].setLore(Arrays.asList(ChatColor.GOLD + "React to " + ChatColor.YELLOW + username 
			                                + ChatColor.GOLD + "'s message"));
			items[i].setItemMeta(itemMeta[i]);
			inventory.setItem(i + 27, items[i]);
			
		}
		
	}
	
	public static void helpCommand(Player player) {
		
		player.sendMessage(ChatColor.GOLD + "Click on a message to add a reaction. If a message is " + React.getMessageMax() + " messages old, you can't react to it!");
		
	}
	
	/**
	 * Sends an alert to the reactor and reactee when a reaction is added
	 * @param player
	 * @param invOwner
	 * @param originalMsg
	 * @param itemName
	 */
	public static void sendAlert(Player player, Player invOwner, String originalMsg, String itemName) {
		
		TextComponent playerMsg = new TextComponent();
		TextComponent invOwnerMsg = new TextComponent();
		
		invOwnerMsg.setText(
				ChatColor.GOLD + "Received reaction " + ChatColor.YELLOW + itemName + ChatColor.GOLD
						+ " from " + ChatColor.YELLOW + player.getName());
		playerMsg.setText(
				ChatColor.GOLD + "Reacted with " + ChatColor.YELLOW + itemName + ChatColor.GOLD + " to "
						+ ChatColor.YELLOW + invOwner.getName());
		
		playerMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(originalMsg).create()));
		invOwnerMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(originalMsg).create()));
		
		player.spigot().sendMessage(playerMsg);
		invOwner.spigot().sendMessage(invOwnerMsg);
		
	}
	
	/**
	 * Sets reactions when item is clicked
	 * @param inventory
	 * @param inventoryMenu
	 * @param invOwner
	 * @param players
	 */
	public static void setReactions(Inventory inventory, 
			Inventory inventoryMenu, Player invOwner, LinkedHashMap<ItemStack, List<String>> players) {
		
		ItemStack air = new ItemStack(Material.AIR);
		
		for (int i = 0; i < 18; i++) {
			inventory.setItem(i, air);
			inventoryMenu.setItem(i, air);
		}
		
		int i = 0;
		
		for (Entry<ItemStack, List<String>> item : players.entrySet()) {
			
			if (item.getValue().isEmpty()) {
				continue;
			}
			
			String usernames = String.join((CharSequence) ", ", (Iterable<? extends CharSequence>) item.getValue());
			item.getKey().setAmount(item.getValue().size());
			
			ItemMeta newItemMetas = item.getKey().getItemMeta();
			newItemMetas.setLore(Arrays.asList(ChatColor.GOLD + "React to " + 
			        ChatColor.YELLOW + invOwner.getName() + ChatColor.GOLD + "'s message",
			        ChatColor.GOLD + "Reactions:",
			        ChatColor.YELLOW + "" + ChatColor.ITALIC + usernames));
			
			item.getKey().setItemMeta(newItemMetas);
			
			inventory.setItem(i, item.getKey());
			inventoryMenu.setItem(i, item.getKey());
			i++;
			
		}
		
	}
	
}
