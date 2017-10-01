package me.stupiddrew9.reactchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.stupiddrew9.reactchat.React;
import me.stupiddrew9.reactchat.util.InvUtil;
import net.md_5.bungee.api.ChatColor;

public class ReactCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;
		int limit = React.getMessageMax();

		if (!(sender instanceof Player)) {
			return false;
		}

		if (args.length < 1) {
			InvUtil.helpCommand(player);
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
			int totalArgsInt = (argsInt / limit);
			int changeToInv = (argsInt - (limit * totalArgsInt));

			if (argsInt <= (React.getTotalMsgs() - (limit + 1)) || argsInt < 0) {
				player.sendMessage(ChatColor.GOLD + "Message reaction expired.");
				return false;
			}

			if (argsInt >= (React.getTotalMsgs())) {
				player.sendMessage(ChatColor.GOLD + "Message does not exist.");
				return false;
			}

			player.openInventory(React.getInventories().get(changeToInv));

		}

		else {

			player.sendMessage(ChatColor.GOLD + "Error encountered.");

		}

		return true;

	}

}
