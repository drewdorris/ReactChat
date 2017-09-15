package me.stupiddrew9.reactchat.listeners;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.stupiddrew9.reactchat.React;
import me.stupiddrew9.reactchat.util.InvUtil;
import net.md_5.bungee.api.ChatColor;

public class InvListener implements Listener {
	
	int currentInv = 0;
	Inventory inventory = null;
	Inventory inventoryMenu = null;
	ItemStack menuItem = null;
	
	HashMap<Player, Boolean> ifMenuOpen = new HashMap<Player, Boolean>();
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent clicc) {

		Player player = (Player) clicc.getWhoClicked();
		String playerName = player.getName();
		Inventory cliccInv = clicc.getInventory();
		
		if (!(ifMenuOpen.containsKey(player))) {
		    ifMenuOpen.put(player, false);
		}
		
		Player invOwner = null;
		String originalMsg = null;
		
		for (Inventory inv : React.getInvIdentity().keySet()) {
			if (inv.getTitle() == cliccInv.getTitle()) {
				inventory = inv;
				invOwner = React.getPlayersHash().get(inv);
				originalMsg = React.getMsgHash().get(inv);
				currentInv = React.getInventories().indexOf(inv);
				inventoryMenu = React.getInventoriesMenu().get(currentInv);
				break;
			} else {
				continue;
			}
		}
		
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
		
		if ((invOwner == null) || (inventory == null)) {
			player.sendMessage(ChatColor.GOLD + "Wrong!");
			return;
		}

		String invOwnerName = invOwner.getName();


		
			ItemStack item = clicc.getCurrentItem();
			
			clicc.setCancelled(true);

			if (clicc.getClick().isShiftClick()) {
				return;
			}
			
			if (!(clicc.getSlotType() == SlotType.CONTAINER)) {
				player.closeInventory();
				return;
			}
			
			if (clicc.getCurrentItem().getType() == Material.STAINED_GLASS_PANE ||
					  clicc.getCurrentItem().getType() == Material.AIR) {
				return;
			}
			
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
			
			if (playerName == invOwnerName) {
				player.sendMessage(ChatColor.GOLD + "You can't react to your own message!");
				player.closeInventory();
				return;
			}
			
			int slot = -1;
			
			for (int i = 0; i < 18; i++) {
				slot++;
				
				if (slot == clicc.getRawSlot()) {
				
					if ((React.getReactCount().get(currentInv).containsKey(player) == false)) {

						React.getReactCount().get(currentInv).put(player, 0);
					
					}
					
					for (int j = 0; j <= inventoryMenu.getSize(); j++) {
					    menuItem = inventoryMenu.getItem(j);
					    if (item.getItemMeta().getDisplayName() == menuItem.getItemMeta().getDisplayName()) {
							player.sendMessage(ChatColor.GOLD + "bvgjkvfkjdv");
					    	break;
					    }
					}
					
					// follow the txt
					
					ItemStack placeholder;
					for (ItemStack reaction : React.getAddedReactions().get(currentInv).keySet()) {
						// placeholder
						placeholder = reaction;
						placeholder.setItemMeta(null);
						// do this 
					}
				
					// reactions per player
					int reactAmnt = React.getReactCount().get(currentInv).get(player).intValue();
					
					if (React.getClickedPlayersInv().get(currentInv).get(i).contains(playerName)) {
						React.getClickedPlayersInv().get(currentInv).get(i).remove(playerName);
						
						React.getReactCount().get(currentInv).put(player, reactAmnt - 1);
						React.getPlayersWithReaction().get(currentInv).put(menuItem, React.getClickedPlayersInv().get(currentInv).get(i));
						
						if (React.getClickedPlayersInv().get(currentInv).get(i).size() == 0) {
							React.getAddedReactions().get(currentInv).remove(menuItem);
							ItemMeta itemMeta = menuItem.getItemMeta();
							itemMeta.setLore(Arrays.asList(ChatColor.GOLD + "React to " + ChatColor.YELLOW + 
									                   invOwner.getName() + ChatColor.GOLD + "'s message"));
							React.getPlayersWithReaction().get(currentInv).remove(menuItem, React.getClickedPlayersInv().get(currentInv).get(i));
							InvUtil.setReactions(inventory, inventoryMenu, invOwner, 
									             React.getPlayersWithReaction().get(currentInv));
							menuItem.setItemMeta(itemMeta);
						
							return;
						}
						InvUtil.setReactions(inventory, inventoryMenu, invOwner, 
					             React.getPlayersWithReaction().get(currentInv));
						return;
					}
					
					if (reactAmnt >= 3) {
						
						player.sendMessage(ChatColor.GOLD + "You can't react to more than three messages!");
						player.closeInventory();
						return;
						
					}
					
					React.getClickedPlayersInv().get(currentInv).get(i).add(playerName);
					React.getReactCount().get(currentInv).put(player, reactAmnt + 1);

					React.getPlayersWithReaction().get(currentInv).put(menuItem, React.getClickedPlayersInv().get(currentInv).get(i));
					InvUtil.setReactions(inventory, inventoryMenu, invOwner, 
				             React.getPlayersWithReaction().get(currentInv));
					InvUtil.sendAlert(inventory, clicc, player, invOwner, originalMsg, React.reactNames[i]);
					
			    }
				
		    }
			
			int menuSlot = 26;
			
			for (int i = 0; i < 18; i++) {
				menuSlot++;
				
				if (menuSlot == clicc.getRawSlot()) {
				
					if ((React.getReactCount().get(currentInv).containsKey(player) == false)) {

						React.getReactCount().get(currentInv).put(player, 0);
					
					}
					
					for (ItemStack reaction : React.getAddedReactions().get(currentInv).keySet()) {
						if (clicc.getCurrentItem().getItemMeta().getDisplayName() == 
								reaction.getItemMeta().getDisplayName()) {
							if (React.getAddedReactions().get(currentInv).get(reaction).contains(playerName)) {
								React.getAddedReactions().get(currentInv).get(reaction).remove(playerName);
								if (React.getAddedReactions().get(currentInv).get(reaction).size() == 0) {
									// remove the item
								}
							}
							if (!(React.getAddedReactions().get(currentInv).get(reaction).contains(playerName))) {
								React.getAddedReactions().get(currentInv).get(reaction).add(playerName);
							}
						}
					}
				
					// reactions per player
					int reactAmnt = React.getReactCount().get(currentInv).get(player).intValue();
					
					if (React.getClickedPlayersInv().get(currentInv).get(i).contains(playerName)) {
						React.getClickedPlayersInv().get(currentInv).get(i).remove(playerName);
						
						React.getReactCount().get(currentInv).put(player, reactAmnt - 1);
						React.getPlayersWithReaction().get(currentInv).put(item, React.getClickedPlayersInv().get(currentInv).get(i));
						
						if (React.getClickedPlayersInv().get(currentInv).get(i).size() == 0) {
							ItemMeta itemMeta = item.getItemMeta();
							itemMeta.setLore(Arrays.asList(ChatColor.GOLD + "React to " + ChatColor.YELLOW + 
									                   invOwner.getName() + ChatColor.GOLD + "'s message"));
							React.getPlayersWithReaction().get(currentInv).remove(item, React.getClickedPlayersInv().get(currentInv).get(i));
							item.setItemMeta(itemMeta);
							InvUtil.setReactions(inventory, inventoryMenu, invOwner, 
						             React.getPlayersWithReaction().get(currentInv));
						
							return;
						}
						
						InvUtil.setReactions(inventory, inventoryMenu, invOwner, 
					             React.getPlayersWithReaction().get(currentInv));
						return;
					}
					
					if (reactAmnt >= 3) {
						
						player.sendMessage(ChatColor.GOLD + "You can't react to more than three messages!");
						player.closeInventory();
						return;
						
					}
					
					React.getClickedPlayersInv().get(currentInv).get(i).add(playerName);
					React.getReactCount().get(currentInv).put(player, reactAmnt + 1);
					
					React.getPlayersWithReaction().get(currentInv).put(item, React.getClickedPlayersInv().get(currentInv).get(i));
					InvUtil.setReactions(inventory, inventoryMenu, invOwner, 
				             React.getPlayersWithReaction().get(currentInv));
					InvUtil.sendAlert(inventory, clicc, player, invOwner, originalMsg, React.reactNames[i]);
					
			    }
				
		    }

		currentInv = 0;
		
		inventory = null;
		inventoryMenu = null;
		menuItem = null;

	}	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryExit(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
	    ifMenuOpen.put(player, false);
	}

}
