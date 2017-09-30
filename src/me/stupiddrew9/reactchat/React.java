package me.stupiddrew9.reactchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.stupiddrew9.reactchat.commands.ReactCommand;
import me.stupiddrew9.reactchat.listeners.ChatListener;
import me.stupiddrew9.reactchat.listeners.InvListener;

public class React extends JavaPlugin {
	
	/*
	 * TODO:
	 * clean up code / tabs / blablabla
	 * set name as clickable if msg contains a link
	 * timer to prevent spam
	 * multiple pages of reactions
	 * 
	 * 
	 * fix things
	 */
	
	// all problems can be fixed with more arraylists
	
	private static React instance;

	public static React getInstance() {
		return instance;
	}
	
	public FileConfiguration config = getConfig();

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		defineNames();
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new InvListener(), this);
		getCommand("reactchat").setExecutor(new ReactCommand());
	    config.options().copyDefaults(true);
	    saveConfig();
		
	}

	@Override
	public void onDisable() {
		instance = null;
	}
	
	public static String[] reactNames = new String[18];
	public static Integer[] itemIDs = new Integer[18];
	public static String[] skullIDs = new String[18];
	
	private static int limit;
	private static int messageMax;
	
	// get names of reactions from config
	public void defineNames() {
		
		limit = config.getInt("reactionLimit");
		messageMax = config.getInt("maxMessages");
		
		if (config.getInt("reactionLimit") > 18 || config.getInt("reactionLimit") < 0 || !(config.isInt("reactionLimit"))) {
			System.out.println("[ReactChat] Value of reactionLimit invalid. Set to default value");
			limit = 3;
		}
		
	    for (String s : config.getConfigurationSection("reactions").getKeys(false)) {
	    	
	    	String reactionName = getConfig().getString("reactions." + s + ".friendlyname");
	    	int position = getConfig().getInt("reactions." + s + ".position");
	    	int id = getConfig().getInt("reactions." + s + ".id");
	    	if (position > 18) {
	    		instance.getServer().getLogger().info("Incorrect position for " + reactionName + " in config.yml");
	    		continue;
	    	}
	    	if (getConfig().contains("reactions." + s + ".skullid") == true) {
	    		String skullid = getConfig().getString("reactions." + s + ".skullid");
	    		if (skullid == null) {
		    		instance.getServer().getLogger().info("Missing skull data for" + reactionName);
		    		return;
	    		}
	    		skullIDs[position] = skullid;
	    	}
	    	reactNames[position] = reactionName;
	    	itemIDs[position] = id;
	    }
		
	}
	
	public static Integer getLimit() {
		return limit;
	}
	public static Integer getMessageMax() {
		return messageMax;
	}
	
	public static ItemStack[] items = new ItemStack[18]; 
	public static ItemMeta[] itemMeta = new ItemMeta[18];
	
	private static ArrayList<Inventory> inventories = new ArrayList<Inventory>(limit);
	
	/**
	 * Array of each inv created for limit amnt of messages
	 * @return inventories
	 */
	public static ArrayList<Inventory> getInventories() {
		return inventories;
	}
	
	/**
	 * Array of each menuinv created for limit amnt of messages
	 * @return inventoriesMenu
	 */
	private static ArrayList<Inventory> inventoriesMenu = new ArrayList<Inventory>(limit);
	
	public static ArrayList<Inventory> getInventoriesMenu() {
		return inventoriesMenu;
	}
	
	private static HashMap<Inventory, Player> hash = new HashMap<Inventory, Player>();

	/**
	 * Assigns an inventory with a player
	 * @return hash
	 */
	public static HashMap<Inventory, Player> getPlayersHash() {
		return hash;
	}

	private static HashMap<Inventory, String> msgHash = new HashMap<Inventory, String>();
	
	/**
	 * Assigns an inventory with a message
	 * @return msgHash
	 */
	public static HashMap<Inventory, String> getMsgHash() {
		return msgHash;
	}

	private static HashMap<Inventory, Integer> invIdentity = new HashMap<Inventory, Integer>();
	
	/**
	 *  Assigns inventory with the currentInv
	 *  @return invIdentity
	 */
	public static HashMap<Inventory, Integer> getInvIdentity() {
		return invIdentity;
	}
	
	private static HashMap<Inventory, Integer> invMenuIdentity = new HashMap<Inventory, Integer>();
	
	/**
	 *  Assigns menu inventory with the currentInv
	 *  @return invMenuIdentity
	 */
	public static HashMap<Inventory, Integer> getInvMenuIdentity() {
		return invMenuIdentity;
	}
	
	private static ArrayList<HashMap<String, Integer>> reactCount = new ArrayList<HashMap<String, Integer>>(limit);
	
	/**
	 * Counts amnt of reactions in an inv per user
	 * @return reactCount
	 */
	public static ArrayList<HashMap<String, Integer>> getReactCount() {
		return reactCount;
	}

	private static ArrayList<LinkedHashMap<ItemStack, List<String>>> addedReactions = new ArrayList<LinkedHashMap<ItemStack, List<String>>>(limit);
	
	/**
	 * Counts amnt of reactions in an inv per added reaction
	 * @return addedReactions
	 */
	public static ArrayList<LinkedHashMap<ItemStack, List<String>>> getAddedReactions() {
		return addedReactions;
	}
	
	public static int currentInv = 0;
	public static int totalMsgs = 0;
	public static int invMult = 0;

	/**
	 * Gets current inventory (set for each msg)
	 * @return currentInv
	 */
	public static int getCurrentInv() {
		return currentInv;
	}

	/**
	 * Gets amount of total messages
	 * @return totalMsgs
	 */
	public static int getTotalMsgs() {
		return totalMsgs;
	}

}
