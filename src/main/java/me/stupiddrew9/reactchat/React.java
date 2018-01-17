package me.stupiddrew9.reactchat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteStreams;

import me.stupiddrew9.reactchat.commands.ReactCommand;
import me.stupiddrew9.reactchat.listeners.ChatListener;
import me.stupiddrew9.reactchat.listeners.InvListener;
import me.stupiddrew9.reactchat.util.InvUtil;

public class React extends JavaPlugin {
	
	private static React instance;
	private static InvUtil util;
	private YamlConfiguration messages;

	public static React getInstance() {
		
		return instance;
		
	}

	@Override
	public void onEnable() {
		
		instance = this;

		loadFiles();
		
		util = new InvUtil();
		
		defineNames();
		
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new InvListener(), this);
		getCommand("reactchat").setExecutor(new ReactCommand());
		
	}
	
	public void loadFiles() {
		
		File file = new File(getDataFolder(), "config.yml");
		File msgs = new File(getDataFolder(), "messages.yml");
		if (!file.exists()) {
		    getLogger().info("config.yml not found, creating!");
		    saveDefaultConfig();
		}
		if (!msgs.exists()) {
		    getLogger().info("messages.yml not found, creating!");
            msgs.getParentFile().mkdirs();
            saveResource("messages.yml", false);
		}
		
		messages = new YamlConfiguration();
		try {
			messages.load(msgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onDisable() {
		
		instance = null;
		
	}
	
	public static InvUtil getUtil() {
		
		return util;
		
	}
	
	public YamlConfiguration getMessages() {
		
		return messages;
		
	}
	
	
	public static String[] reactNames = new String[27];
	public static Integer[] itemIDs = new Integer[27];
	public static String[] skullIDs = new String[27];
	
	private static int reactLimit;
	private static int messageMax;
	private static boolean allowSelfReact;
	
	// get names of reactions from config
	public void defineNames() {
		
		reactLimit = getConfig().getInt("reactionLimit");
		messageMax = getConfig().getInt("maxMessages");
		allowSelfReact = getConfig().getBoolean("allowSelfReacting");
		
		if (getConfig().getInt("reactionLimit") > 18 || getConfig().getInt("reactionLimit") < 0 || !(getConfig().isInt("reactionLimit"))) {
			getLogger().info("[ReactChat] Value of reactionLimit invalid. Set to default value");
			reactLimit = 3;
		}
		
	    for (String s : getConfig().getConfigurationSection("reactions").getKeys(false)) {
	    	
	    	String reactionName = getConfig().getString("reactions." + s + ".friendlyname");
	    	int position = getConfig().getInt("reactions." + s + ".position");
	    	int id = getConfig().getInt("reactions." + s + ".id");
	    	
	    	if (position > 26) {
	    		getLogger().warning("[ReactChat] Incorrect position for reaction " + reactionName + " in config.yml");
	    		continue;
	    	}
	    	
	    	if (getConfig().contains("reactions." + s + ".skullid") == true) {
	    		
	    		String skullid = getConfig().getString("reactions." + s + ".skullid");
	    		
	    		if (skullid == null) {
		    		getLogger().warning("[ReactChat] Missing skull data for" + reactionName);
		    		return;
	    		}
	    		
	    		skullIDs[position] = skullid;
	    		
	    	}
	    	
	    	reactNames[position] = reactionName;
	    	itemIDs[position] = id;
	    	
	    }
		
	}
	
	private InputStream in;
	private OutputStream out;
	
	public File loadResource(String resource) {
		
		File folder = getDataFolder();
		if (!folder.exists()) folder.mkdir();

		File file = new File(folder, resource);

		try {
			
			if (!file.exists()) {
				
				file.createNewFile();

				try {
					in = getResource(resource); 
					out = new FileOutputStream(file);
				} finally {
					ByteStreams.copy(in, out);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}
	
	public static String colour(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	
	public static ItemStack[] items = new ItemStack[18]; 
	public static ItemMeta[] itemMeta = new ItemMeta[18];
	
	/**
	 * Maximum amount of reactions per message per player
	 * Defined in config
	 * @return limit
	 */
	public static Integer getReactLimit() {
		return reactLimit;
	}
	
	/**
	 * Amount of messages sent before a message expires
	 * Defined in config
	 * @return messageMax
	 */
	public static Integer getMessageMax() {
		return messageMax;
	}
	
	/**
	 * Whether or not a user can react to their own message
	 * @return allowSelfReact
	 */
	public static Boolean getSelfReact() {
		return allowSelfReact;
	}
	
	private static ArrayList<Inventory> inventories = new ArrayList<Inventory>(reactLimit);
	
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
	private static ArrayList<Inventory> inventoriesMenu = new ArrayList<Inventory>(reactLimit);
	
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
	
	private static ArrayList<HashMap<String, Integer>> reactCount = new ArrayList<HashMap<String, Integer>>(reactLimit);
	
	/**
	 * Counts amnt of reactions in an inv per user
	 * @return reactCount
	 */
	public static ArrayList<HashMap<String, Integer>> getReactCount() {
		return reactCount;
	}

	private static ArrayList<LinkedHashMap<ItemStack, List<String>>> addedReactions = new ArrayList<LinkedHashMap<ItemStack, List<String>>>(reactLimit);
	
	/**
	 * Counts amnt of reactions in an inv per added reaction
	 * @return addedReactions
	 */
	public static ArrayList<LinkedHashMap<ItemStack, List<String>>> getAddedReactions() {
		return addedReactions;
	}
	
	// all problems can be fixed with more lists and maps
	// - every pro dev
	
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
