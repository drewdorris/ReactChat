package me.stupiddrew9.reactchat.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
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
	
	public static ItemStack[] items; 
	public static ItemMeta[] itemMeta;
	private YamlConfiguration messages = React.getInstance().getMessages();
	private final String prefix = messages.getString("prefix");
	
	
	public InvUtil() {
		items = new ItemStack[27];
		itemMeta = new ItemMeta[27];
	}
	
	/**
	 * Get message prefix set in message yaml
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Get message yaml
	 * @return messages
	 */
	public YamlConfiguration getMessages() {
		return messages;
	}
	
	/**
	 * Get if prefix is enabled for this message
	 * @param path
	 * @return string
	 */
	public String getPrefix(String path) {
		
		if ((messages.getBoolean(path + "-prefix"))) {
			return React.colour(prefix);
		} else {
			return "";
		}
		
	}
	
	/**
	 * Shortens length of msg for setting as inventory name
	 * @param namemsg
	 * @param msg
	 * @return msg
	 */
	public static String invName(String namemsg, String msg) {

		String fullmsg = (namemsg + msg);
		fullmsg = ChatColor.stripColor(fullmsg);
		int name = namemsg.length() - msg.length();
		
		if (name > 20 && name < 27) {
			if (msg.length() < 13) {
				return msg;
			}
			return (msg.substring(0, 12) + "...");
		} else if (name >= 27 && msg.length() > 5) {
			return (msg.substring(0, 4) + "..."); 
		} else if (fullmsg.length() > 26) {
			return (msg.substring(0, 4) + "..."); 
		} else {
			return msg;
		}
		
	}
	
	public void helpCommand(Player player) {
		
		for (String line : messages.getStringList("help-message")) {
			player.sendMessage(getPrefix("help-message") + 
					React.colour(line.replace("{max}", React.getMessageMax().toString())
							.replace("{limit}", React.getReactLimit().toString())
									.replace("{player}", player.getName())));
		}
		
	}

	/**
	 * Sets default contents of the reaction window
	 * @param invArray
	 * @param inventory
	 * @param player
	 */
	public void defineInvContents(ArrayList<ArrayList<String>> invArray, Inventory inventory, Player player) {
		
		ItemStack moreItem = new ItemStack(Material.getMaterial(React.getInstance().getConfig().getString("menu-item")), 1);
		ItemMeta moreItemMeta = moreItem.getItemMeta();
		// SEE_REACTIONS
		moreItemMeta.setDisplayName(React.colour(messages.getString("see-reactions")));
		moreItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		moreItem.setItemMeta(moreItemMeta);
		inventory.setItem(22, moreItem);
		
		setBorder(inventory);
		
	}
	
	/**
	 * Sets default contents of the reaction menu window
	 * @param invArray
	 * @param inventory
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public void defineInvMenuContents(ArrayList<ArrayList<String>> invArray, Inventory inventory, Player player) {
		
		ItemStack moreItem = new ItemStack(Material.getMaterial(React.getInstance().getConfig().getString("menu-item")), 1);
		ItemMeta moreItemMeta = moreItem.getItemMeta();
		// HIDE_REACTIONS
		moreItemMeta.setDisplayName(React.colour(messages.getString("hide-reactions")));
		moreItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		moreItem.setItemMeta(moreItemMeta);
		inventory.setItem(22, moreItem);
		
		setBorder(inventory);

		for (int i = 0; i < 27; i++) {
			
			ArrayList<String> eachItem = new ArrayList<String>(27);
			invArray.add(eachItem);
			
			if (React.reactNames[i] == null) {
				continue;
			}
			
			String username = player.getName();
			
			if (!(React.skullIDs[i] == null)) {
				
				items[i] = new ItemStack(397, 1, (short) 3);
				itemMeta[i] = items[i].getItemMeta();
				
				// REACTION_NAME
				itemMeta[i].setDisplayName(React.colour(messages.getString("reaction-name"))
						.replace("{reaction}", React.reactNames[i]).replace("{username}", username));
				
				List<String> lore = new ArrayList<String>();
				
				// REACTION_LORE (list)
				for (String line : messages.getStringList("reaction-lore")) {
					lore.add(React.colour(line).replace("{username}", username));
				}
				itemMeta[i].setLore(lore);
				items[i].setItemMeta(itemMeta[i]);
				SkullUtil.getCustomSkull(React.skullIDs[i], items[i]);
				inventory.setItem(i + 27, items[i]);
				
				continue;
				
			}
			
			items[i] = new ItemStack(React.itemIDs[i], 1);
			itemMeta[i] = items[i].getItemMeta();
			
			// REACTION_NAME
			itemMeta[i].setDisplayName(React.colour(messages.getString("reaction-name"))
					.replace("{reaction}", React.reactNames[i]));
			
			List<String> lore = new ArrayList<String>();
			
			// REACTION_LORE (list)
			for (String line : messages.getStringList("reaction-lore")) {
				lore.add(React.colour(line).replace("{username}", username));
			}
			
			itemMeta[i].setLore(lore);
			items[i].setItemMeta(itemMeta[i]);
			inventory.setItem(i + 27, items[i]);
			
		}
		
	}
	
	public Inventory setBorder(Inventory inventory) {
		
		// configurable int for color
		ItemStack borderItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		ItemMeta borderMeta = borderItem.getItemMeta();
		borderMeta.setDisplayName(ChatColor.BLACK + " ");
		borderItem.setItemMeta(borderMeta);
		
		inventory.setItem(18, borderItem);
		inventory.setItem(19, borderItem);
		inventory.setItem(20, borderItem);
		inventory.setItem(21, borderItem);
		inventory.setItem(23, borderItem);
		inventory.setItem(24, borderItem);
		inventory.setItem(25, borderItem);
		inventory.setItem(26, borderItem);
		
		return inventory;
		
	}
	
	public void setAlertText(String direc, TextComponent msg, String owner, String itemName) {
		
		int i = 0;
		
		for (String line : messages.getStringList(direc)) {
			
			if (i == 0) {
				msg.addExtra(getPrefix("send-reaction") + 
						React.colour(line.replace("{username}", owner)
								.replace("{reactor}", owner).replace("{reaction}", itemName)));
			} else {
				msg.addExtra("\n" + getPrefix("send-reaction") + 
						React.colour(line.replace("{username}", owner)
								.replace("{reactor}", owner).replace("{reaction}", itemName)));
			}
			
			i++;
			
		}
		
	}
	
	/**
	 * Sends an alert to the reactor and reactee when a reaction is added
	 * @param player
	 * @param invOwner
	 * @param originalMsg
	 * @param itemName
	 */
	public void sendAlert(Player player, Player invOwner, String originalMsg, String itemName) {
		
		TextComponent playerMsg = new TextComponent();
		TextComponent invOwnerMsg = new TextComponent();
		String playerName = player.getName();
		String invOwnerName = invOwner.getName();
		
		// RECEIVE_REACTION
		setAlertText("receive-reaction", invOwnerMsg, playerName, itemName);
		
		// SEND_REACTION
		setAlertText("send-reaction", playerMsg, invOwnerName, itemName);
		
		if (player.getName() == invOwner.getName()) {
			// SELF_REACTION
			playerMsg.setText("");
			setAlertText("self-reaction", invOwnerMsg, invOwnerName, itemName);
		}
		
		playerMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(originalMsg).create()));
		invOwnerMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(originalMsg).create()));
		
		player.spigot().sendMessage(playerMsg);
		
		// check if self-reacting is allowed
		if (React.getSelfReact() == true) {
			
			// if it is, check if they are reacting on their own message
			// if they are, don't send them another unnecessary message
			if (player.getName() == invOwner.getName()) { 
				return;
			}
			
		}
		
		invOwner.spigot().sendMessage(invOwnerMsg);
		
	}
	
	/**
	 * Sets reactions when item is clicked
	 * @param inventory
	 * @param inventoryMenu
	 * @param invOwner
	 * @param players
	 */
	public void setReactions(Inventory inventory, 
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
			List<String> lore = new ArrayList<String>();
			
			// REACTION_LORE (list)
			for (String line : messages.getStringList("reaction-lore")) {
				lore.add(React.colour(line.replace("{username}", invOwner.getName())
						.replace("{users}", usernames)));
			}
			
			// REACTION_LIST_LORE (list)
			for (String line : messages.getStringList("reaction-list-lore")) {
				lore.add(React.colour(line.replace("{username}", invOwner.getName())
						.replace("{users}", usernames)));
			}
			
			
			newItemMetas.setLore(lore);
			item.getKey().setItemMeta(newItemMetas);
			
			inventory.setItem(i, item.getKey());
			inventoryMenu.setItem(i, item.getKey());
			i++;
			
		}
		
	}
	
}
