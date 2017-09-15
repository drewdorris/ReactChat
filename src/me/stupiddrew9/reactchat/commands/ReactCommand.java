package me.stupiddrew9.reactchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.stupiddrew9.reactchat.React;
import net.md_5.bungee.api.ChatColor;

public class ReactCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;

		if (!(sender instanceof Player)) {
			return false;
		}

		if (args.length < 1) {
			helpCommand(player);
			return false;
		}

		if (args.length >= 1) {

			try {
				Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.GOLD + "Please type an integer.");
				return false;
			}
			int argsInt = (Integer.parseInt(args[0]));
			int totalArgsInt = (argsInt / 50);
			int changeToInv = (argsInt - (50 * totalArgsInt));

			if (argsInt <= (React.totalMsgs - 51) || argsInt < 0) {
				try {
					player.openInventory(React.getInventories().get(200));
					player.sendMessage(ChatColor.GOLD + "Error encountered.");
				} catch (IndexOutOfBoundsException o) {
					player.sendMessage(ChatColor.GOLD + "Message reaction expired.");
				}
				return false;
			}

			if (argsInt >= (React.totalMsgs)) {
				try {
					player.openInventory(React.getInventories().get(200));
					player.sendMessage(ChatColor.GOLD + "Error encountered.");
				} catch (IndexOutOfBoundsException i) {
					player.sendMessage(ChatColor.GOLD + "Message does not exist.");
				}
				return false;
			}

			player.openInventory(React.getInventories().get(changeToInv));

		}

		else {

			player.sendMessage(ChatColor.GOLD + "Error encountered.");

		}

		return true;

	}

	private void helpCommand(Player player) {

		player.sendMessage(ChatColor.GOLD + "Click on a message to add a reaction. If the message is 100+ messages old, you can't react to it!");

	}

}
