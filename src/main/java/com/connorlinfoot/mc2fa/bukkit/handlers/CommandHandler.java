package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import org.bukkit.Bukkit;
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

        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "No debug arg");
            } else {
                switch (args[1].toLowerCase()) {
                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid debug arg");
                        break;
                    case "state":
                        Player player = null;
                        if (args.length > 2) {
                            String playerName = args[2];
                            player = Bukkit.getPlayer(playerName);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + "Player not found");
                                return true;
                            }
                        }
                        player = (Player) sender;
                        sender.sendMessage(String.valueOf(mc2FA.getAuthHandler().getState(player.getUniqueId())));
                        break;
                }
            }
            return true;
        }

        Player player = (Player) sender;
        if (mc2FA.getAuthHandler().isPending(player.getUniqueId())) {
            if (args.length == 0) {
                player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Please validate your two-factor authentication key with /" + string + " <key>");
            } else {
                Integer key;
                try {
                    key = Integer.valueOf(args[0]);
                } catch (Exception e) {
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Invalid key entered");
                    return false;
                }

                boolean approved = mc2FA.getAuthHandler().approveKey(player.getUniqueId(), key);
                if (approved) {
                    player.sendMessage(messageHandler.getMessage("Setup Success"));
                    mc2FA.getAuthHandler().giveItemsBack(player);
//					player.getInventory().forEach(itemStack -> {
//						if (itemStack != null && itemStack.getType() == Material.MAP && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "QR Code")) {
//							player.getInventory().remove(itemStack);
//						}
//					});
                } else {
                    player.sendMessage(messageHandler.getMessage("Invalid Key"));
                }
            }
        } else if (!mc2FA.getAuthHandler().isEnabled(player.getUniqueId())) {
            mc2FA.getAuthHandler().createKey(player.getUniqueId());
            mc2FA.getAuthHandler().giveQRItem(mc2FA, player);
        } else {
            if (args.length > 0) {
                boolean isValid = mc2FA.getAuthHandler().validateKey(player.getUniqueId(), Integer.valueOf(args[0]));
                player.sendMessage(String.valueOf(isValid));
            }
        }

        return false;
    }

}
