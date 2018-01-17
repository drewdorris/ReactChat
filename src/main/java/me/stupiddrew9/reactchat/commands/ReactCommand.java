package me.stupiddrew9.reactchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.stupiddrew9.reactchat.React;

public class ReactCommand implements CommandExecutor {
	
	private YamlConfiguration messages = React.getInstance().getMessages();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			for (String line : messages.getStringList("help-message")) {
				sender.sendMessage(React.getUtil().getPrefix("help-message") + 
						React.colour(line.replace("{max}", React.getMessageMax().toString())
								.replace("{limit}", React.getReactLimit().toString())
										.replace("{player}", "CONSOLE")));
			}
			return false;
		}
		
		Player player = (Player) sender;
		int limit = React.getMessageMax();
		
		if (!(player.hasPermission("reactchat.react"))) {
			
			// NO_PERMISSION
			for (String line : messages.getStringList("no-permission")) {
				player.sendMessage(React.getUtil().getPrefix("no-permission") + React.colour(line));
			}
			return false;
		}

		if (args.length < 1) {
			React.getUtil().helpCommand(player);
			return false;
		}

		if (args.length >= 1) {

			try {
				Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				
				// INTEGER_PLS
				for (String line : messages.getStringList("integer-pls")) {
					player.sendMessage(React.getUtil().getPrefix("integer-pls") + React.colour(line));
				}
				return false;
			}
			
			int argsInt = (Integer.parseInt(args[0]));
			int totalArgsInt = (argsInt / limit);
			int changeToInv = (argsInt - (limit * totalArgsInt));

			if (argsInt <= (React.getTotalMsgs() - (limit + 1)) || argsInt < 0) {
				
				// MESSAGE_EXPIRED
				for (String line : messages.getStringList("message-expired")) {
					player.sendMessage(React.getUtil().getPrefix("message-expired") + React.colour(line));
				}
				return false;
			}

			if (argsInt >= (React.getTotalMsgs())) {
				
				// MESSAGE_NOT_EXIST
				for (String line : messages.getStringList("message-not-exist")) {
					player.sendMessage(React.getUtil().getPrefix("message-not-exist") + React.colour(line));
				}
				return false;
			}

			player.openInventory(React.getInventories().get(changeToInv));

		}

		else {

			// ERROR
			for (String line : messages.getStringList("error")) {
				player.sendMessage(React.getUtil().getPrefix("error") + React.colour(line));
			}

		}

		return true;

	}

}
