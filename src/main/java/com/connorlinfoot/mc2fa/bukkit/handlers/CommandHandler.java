package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	private MC2FA mc2FA;

	public CommandHandler(MC2FA mc2FA) {
		this.mc2FA = mc2FA;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
		MessageHandler messageHandler = mc2FA.getMessageHandler();
		if (!(sender instanceof Player)) {
			sender.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "This command must be ran as a player");
			return false;
		}

		Player player = (Player) sender;
		if( mc2FA.getAuthHandler().isPending(player.getUniqueId())) {
			if( args.length == 0 ) {
				player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Please validate your two-factor authentication key with /" + string + " <key>");
			} else {
				Integer key;
				try {
					key = Integer.valueOf(args[0]);
				} catch(Exception e) {
					player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Invalid key entered");
					return false;
				}

				boolean approved = mc2FA.getAuthHandler().approveKey(player.getUniqueId(), key);
				if( approved ) {
					player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "You have successfully setup two-factor authentication");
				} else {
					player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "The key you entered was not valid, please try again");
				}
			}
		} else if( ! mc2FA.getAuthHandler().isEnabled(player.getUniqueId())) {
			mc2FA.getAuthHandler().createKey(player.getUniqueId());
			player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "Please follow the link below to setup two-factor authentication");
			player.sendMessage(messageHandler.getPrefix() + mc2FA.getAuthHandler().getQRCodeURL(player.getUniqueId()));
			player.sendMessage("");
			player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "Please validate by entering your key: /" + string + " <key>");
		} else {
			if( args.length > 0 ) {
				boolean isValid = mc2FA.getAuthHandler().validateKey(player.getUniqueId(), Integer.valueOf(args[0]));
				player.sendMessage(String.valueOf(isValid));
			}
		}

		return false;
	}

}
