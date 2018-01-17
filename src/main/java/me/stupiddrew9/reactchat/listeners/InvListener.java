package me.stupiddrew9.reactchat.listeners;

import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.stupiddrew9.reactchat.React;

public class InvListener implements Listener {
	
	private int currentInv = 0;
	private Inventory inventory = null;
	private Inventory inventoryMenu = null;
	private ItemStack menuItem = null;
	private YamlConfiguration messages = React.getUtil().getMessages();
	
	HashMap<Player, Boolean> ifMenuOpen = new HashMap<Player, Boolean>();
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent clicc) {

		Player player = (Player) clicc.getWhoClicked();
		String playerName = player.getName();
		Inventory cliccInv = clicc.getInventory();
		
		// check if the inventory menu is open
		if (!(ifMenuOpen.containsKey(player))) {
		    ifMenuOpen.put(player, false);
		}
		
		Player invOwner = null;
		String originalMsg = null;
		
		// set data equivalent to the data for the inventories created by the plugin
		for (Inventory inv : React.getInvIdentity().keySet()) {
			
			if (inv.getTitle() == cliccInv.getTitle()) {
				
				inventory = inv;
				invOwner = React.getPlayersHash().get(inv);
				originalMsg = React.getMsgHash().get(inv);
				currentInv = React.getInvIdentity().get(inv);
				inventoryMenu = React.getInventoriesMenu().get(currentInv);
				
				break;
				
			} else {
				
				continue;
				
			}
			
		}
		
		// set data equivalent to the data for the inventory menus created by the plugin
		for (Inventory inv : React.getInvMenuIdentity().keySet()) {
			
			if (inv.getTitle() == cliccInv.getTitle()) {
				
				inventory = React.getInventories().get(React.getInvMenuIdentity().get(inv).intValue());
				invOwner = React.getPlayersHash().get(inventory);
				originalMsg = React.getMsgHash().get(inventory);
				currentInv = React.getInventories().indexOf(inventory);
				inventoryMenu = React.getInventoriesMenu().get(currentInv);
				
				break;
				
			} else {
				
				continue;
				
			}
			
		}
		
		// return if inventory was not found to match with a plugin-made inventory
		if ((invOwner == null) || (inventory == null)) {
			return;
		}

		String invOwnerName = invOwner.getName();

		ItemStack item = clicc.getCurrentItem();
		
		clicc.setCancelled(true);

		if (clicc.getClick().isShiftClick()) {
			return;
		}
		
		// click out of inventory easily
		if (!(clicc.getSlotType() == SlotType.CONTAINER)) {
			player.closeInventory();
			return;
		}
		
		// do nothing if the item is not a reaction
		if (clicc.getCurrentItem().getType() == Material.STAINED_GLASS_PANE ||
				  clicc.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		// "add reaction" item
		if (clicc.getRawSlot() == 22) {
			
			if (ifMenuOpen.get(player) == true) {
				player.openInventory(React.getInventories().get(currentInv));
				ifMenuOpen.put(player, false);
			    
				return;
			    
			}
			
			player.openInventory(React.getInventoriesMenu().get(currentInv));
			ifMenuOpen.put(player, true);
		    
			return;
			
		}
		
		// if self-reacting is not allowed & message is their own, return
		if (React.getSelfReact() == false) {
			
			if (playerName == invOwnerName) {
				
				// NO_SELF_REACT 
				for (String line : messages.getStringList("no-self-react")) {
					player.sendMessage(React.getUtil().getPrefix("no-self-react") + 
							React.colour(line.replace("{username}", invOwnerName)));
				}
				player.closeInventory();
				
				return;
				
			}
			
		}
		
		int slot = 0;
		
		// get "slot" equivalent to the slot of the reaction in the menu
		for (int j = 27; j <= inventoryMenu.getSize(); j++) {
			
			menuItem = inventoryMenu.getItem(j);
			if (menuItem == null) {
				continue;
			}
			if (item.getItemMeta().getDisplayName() == menuItem.getItemMeta().getDisplayName()) {
				slot = j;
				break;
			}
		    
		} 
		
		// if getting the slot fails, return
		if (!(item.getItemMeta().getDisplayName() == menuItem.getItemMeta().getDisplayName())) {
			return;
		}

		// reactions per player in the message's reactions
		int reactAmnt = 0;
		
		// put reactAmnt in the getReactCount list if not already there
		if (React.getReactCount().get(currentInv).get(playerName) == null) {
			reactAmnt = 0;
			React.getReactCount().get(currentInv).put(playerName, reactAmnt);
		}
		
		reactAmnt = React.getReactCount().get(currentInv).get(playerName).intValue();
		
		// new item for the added reactions section
		ItemStack newReaction = new ItemStack(item);
		
		// reactors
		List<String> reactors = new ArrayList<String>();
		
		// counts amnt of reactions
		int reactionsAmnt = 0;
		
		if (clicc.getRawSlot() < 18) {
			
			// iterates through each reacted reaction in that inventory
			for (Entry<ItemStack, List<String>> reaction : React.getAddedReactions().get(currentInv).entrySet()) {
				
				reactionsAmnt++;
				
				// if reaction is in the hash of reactions
				if (reaction.getKey().getItemMeta().getDisplayName() == item.getItemMeta().getDisplayName()) {
					
					// if the player is already a reactor of the reaction
					if (reaction.getValue().contains(playerName)) {
						
						// remove the player from the list of reactors
						reaction.getValue().remove(playerName);
						React.getReactCount().get(currentInv).put(playerName, reactAmnt - 1);
						// if there are no reactors, remove the item from reactions
						if (reaction.getValue().isEmpty()) {
							React.getAddedReactions().get(currentInv).remove(reaction);
						}
						
						React.getUtil().setReactions(inventory, inventoryMenu, invOwner, 
					             React.getAddedReactions().get(currentInv));
						
						return;
						
					}
					
					if (reactAmnt >= React.getReactLimit()) {
						
						// USER_LIMIT
						for (String line : messages.getStringList("user-limit")) {
							player.sendMessage(React.getUtil().getPrefix("user-limit")
									+ React.colour(line.replace("{username}", invOwnerName)
										.replace("{limit}", React.getReactLimit().toString())));
						}
						player.closeInventory();
						
						return;
						
					}
					
					React.getReactCount().get(currentInv).put(playerName, reactAmnt + 1);
					reaction.getValue().add(playerName);
					
					React.getUtil().setReactions(inventory, inventoryMenu, invOwner, 
				             React.getAddedReactions().get(currentInv));
					React.getUtil().sendAlert(player, invOwner, originalMsg, React.reactNames[slot - 27]);
					
					return;
					
				}
				
				// if reaction is not in the array (no prior reactions)
				// cantdew since only checking in the added reactions (0-17 inv slots)
				if (reactionsAmnt == React.getAddedReactions().get(currentInv).size()) {
					// ERROR
					for (String line : messages.getStringList("error")) {
						player.sendMessage(React.getUtil().getPrefix("error") + React.colour(line));
					}
				}
				
			}
			
		}
		
		if (clicc.getRawSlot() > 26) {
			
			// look in all reactions
			// look in menu inv
			for (Entry<ItemStack, List<String>> reaction : React.getAddedReactions().get(currentInv).entrySet()) {
				
				reactionsAmnt++;
				
				// if reaction is in the hash of reactions
				if (reaction.getKey().getItemMeta().getDisplayName() == item.getItemMeta().getDisplayName()) {
					
					if (React.getAddedReactions().get(currentInv) == null) {
						// ERROR
						for (String line : messages.getStringList("error")) {
							player.sendMessage(React.getUtil().getPrefix("error") + React.colour(line));
						}
					}
					
					if (reaction.getValue() == null) {
						// ERROR
						for (String line : messages.getStringList("error")) {
							player.sendMessage(React.getUtil().getPrefix("error") + React.colour(line));
						}
						return;
					}
					
					// if the player is already a reactor of the reaction
					if (reaction.getValue().contains(playerName)) {
						
						// remove the player from the list of reactors
						reaction.getValue().remove(playerName);
						React.getReactCount().get(currentInv).put(playerName, reactAmnt - 1);
						
						// if there are no reactors, remove the item from reactions
						if (reaction.getValue().isEmpty()) {
							React.getAddedReactions().get(currentInv).remove(reaction, reaction.getValue());
						}
						
						React.getUtil().setReactions(inventory, inventoryMenu, invOwner, 
					             React.getAddedReactions().get(currentInv));
						
						return;
						
					}
					
					if (reactAmnt >= React.getReactLimit()) {
						
						// USER_LIMIT
						for (String line : messages.getStringList("user-limit")) {
							player.sendMessage(React.getUtil().getPrefix("user-limit") + React.colour(line.replace("{username}", invOwnerName)
									.replace("{limit}", React.getReactLimit().toString())));
						}
						player.closeInventory();
						
						return;
						
					}
					
					React.getReactCount().get(currentInv).put(playerName, reactAmnt + 1);
					reaction.getValue().add(playerName);
					
					// update the reactions list
					React.getUtil().setReactions(inventory, inventoryMenu, invOwner, 
				             React.getAddedReactions().get(currentInv));
					
					// alert users of reaction event
					React.getUtil().sendAlert(player, invOwner, originalMsg, React.reactNames[slot - 27]);
					
					return;
					
				}
				
				// if reaction is not in the array (no prior reactions)
				if (reactionsAmnt == React.getAddedReactions().get(currentInv).size()) {
					
					if (reactAmnt >= React.getReactLimit()) {
						
						// USER_LIMIT
						for (String line : messages.getStringList("user-limit")) {
							player.sendMessage(React.getUtil().getPrefix("user-limit") + 
									React.colour(line.replace("{username}", invOwnerName)
											.replace("{limit}", React.getReactLimit().toString())));
						}
						player.closeInventory();
						return;
					}
					
					// check if reaction limit is reached
					if (React.getAddedReactions().get(currentInv).size() == 18) {
						
						// REACTION_MAX
						for (String line : messages.getStringList("reaction-max")) {
							player.sendMessage(React.getUtil().getPrefix("reaction-max") + 
									React.colour(line.replace("{username}", invOwnerName)));
						}
						return;
					}
					
					// add the reaction to the list of reactions
				 	reactors.add(playerName);
				 	React.getAddedReactions().get(currentInv).put(newReaction, reactors);
				 	
				 	React.getReactCount().get(currentInv).put(playerName, reactAmnt + 1);
				 	
					// update the reactions list
				 	React.getUtil().setReactions(inventory, inventoryMenu, invOwner, 
				             React.getAddedReactions().get(currentInv));
					
					// alert users of reaction event
				 	React.getUtil().sendAlert(player, invOwner, originalMsg, React.reactNames[slot - 27]);
					
					return;
					
				}
				
			}
			
			// check if reaction limit is reached
			if (React.getAddedReactions().get(currentInv).size() == 18) {
				
				// REACTION_MAX
				for (String line : messages.getStringList("reaction-max")) {
					player.sendMessage(React.getUtil().getPrefix("reaction-max") + 
							React.colour(line.replace("{username}", invOwnerName)));
				}
				return;
			}
			
			// add the reaction to the list of reactions
		 	reactors.add(playerName);
		 	React.getAddedReactions().get(currentInv).put(newReaction, reactors);
		 	
		 	React.getReactCount().get(currentInv).put(playerName, reactAmnt + 1);
		 	
			// update the reactions list
		 	React.getUtil().setReactions(inventory, inventoryMenu, invOwner, 
		             React.getAddedReactions().get(currentInv));
			
			// alert users of reaction event
		 	React.getUtil().sendAlert(player, invOwner, originalMsg, React.reactNames[slot - 27]);
			
		}

		currentInv = 0;
		inventory = null;
		inventoryMenu = null;
		menuItem = null;
		reactors = null;

	}	
	
	// change value of menu being open when player closes inventory
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryExit(InventoryCloseEvent event) {
		
		Player player = (Player) event.getPlayer();
		ifMenuOpen.put(player, false);
	    
	}

}
