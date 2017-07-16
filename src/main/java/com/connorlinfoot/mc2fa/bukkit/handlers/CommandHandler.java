package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import com.connorlinfoot.mc2fa.shared.AuthHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
            sender.sendMessage(messageHandler.getMessage("&cThis command must be ran as a player"));
            return false;
        }
        Player player = (Player) sender;
        Player target;

        if (mc2FA.getAuthHandler().getState(player.getUniqueId()).equals(AuthHandler.AuthState.PENDING_LOGIN)) {
            if (args.length == 0) {
                messageHandler.sendMessage(player, "&cCorrect usage: /2fa <key>");
            } else {
                try {
                    boolean isValid = mc2FA.getAuthHandler().validateKey(player.getUniqueId(), Integer.valueOf(args[0]));
                    if (isValid) {
                        messageHandler.sendMessage(player, "&aYou have successfully authenticated");
                    } else {
                        messageHandler.sendMessage(player, "&cIncorrect key, please try again");
                    }
                } catch (Exception e) {
                    messageHandler.sendMessage(player, "&cInvalid key entered");
                    return true;
                }
            }
        } else if (mc2FA.getAuthHandler().getState(player.getUniqueId()).equals(AuthHandler.AuthState.PENDING_SETUP)) {
            if (args.length == 0) {
                messageHandler.sendMessage(player, "&cPlease validate your two-factor authentication key with /2fa <key>");
            } else {
                Integer key;
                try {
                    key = Integer.valueOf(args[0]);
                } catch (Exception e) {
                    messageHandler.sendMessage(player, "&cInvalid key entered");
                    return true;
                }

                boolean approved = mc2FA.getAuthHandler().approveKey(player.getUniqueId(), key);
                if (approved) {
                    messageHandler.sendMessage(player, "&aYou have successfully setup two-factor authentication");
                    player.getInventory().forEach(itemStack -> {
                        if (itemStack != null && itemStack.getType() == Material.MAP && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "QR Code")) {
                            player.getInventory().remove(itemStack);
                        }
                    });
                } else {
                    messageHandler.sendMessage(player, "&cThe key you entered was not valid, please try again");
                }
            }
        } else {

            if (args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("help"))) {
                sender.sendMessage(ChatColor.AQUA + "--------------- " + ChatColor.GOLD + "MC2FA" + ChatColor.AQUA + " ---------------");
                if (mc2FA.getAuthHandler().isEnabled(((Player) sender).getUniqueId())) {
                    sender.sendMessage(ChatColor.GOLD + "/2fa reset <key> " + ChatColor.YELLOW + "Disables two-factor authentication");
                } else {
                    sender.sendMessage(ChatColor.GOLD + "/2fa enable " + ChatColor.YELLOW + "Enables two-factor authentication");
                }
                if (sender.isOp()) {
                    sender.sendMessage(ChatColor.GOLD + "/2fa debug " + ChatColor.YELLOW + "Debug two-factor authentication");
                }
            } else {
                switch (args[0].toLowerCase()) {
                    default:
                        messageHandler.sendMessage(player, "&cUnknown argument");
                        break;
                    case "enable":
                    case "on":
                    case "true":
                    case "activate":
                        if (mc2FA.getAuthHandler().getState(player.getUniqueId()).equals(AuthHandler.AuthState.DISABLED)) {
                            mc2FA.getAuthHandler().createKey(player.getUniqueId());
                            mc2FA.getAuthHandler().giveQRItem(mc2FA, player);
                        } else {
                            messageHandler.sendMessage(player, "&cYou are already setup with 2FA");
                        }
                        break;
                    case "deactivate":
                    case "off":
                    case "false":
                    case "disable":
                    case "reset":
                        if (true) {
                            sender.sendMessage(">.>");
                            return true;
                        }
                        if (mc2FA.getAuthHandler().isEnabled(player.getUniqueId())) {
                            mc2FA.getAuthHandler().reset(player.getUniqueId());
                            messageHandler.sendMessage(player, "&aYour 2FA has been reset");
                        } else {
                            messageHandler.sendMessage(player, "&cYou are not setup with 2FA");
                        }
                        break;
                    case "debug":
                        if (!sender.isOp()) {
                            messageHandler.sendMessage(player, "&cYou do not have permission to run this command");
                            return true;
                        }
                        if (args.length == 1) {
                            sender.sendMessage(ChatColor.RED + "No debug arg");
                        } else {
                            if (args.length > 2) {
                                String playerName = args[2];
                                target = Bukkit.getPlayer(playerName);
                                if (target == null) {
                                    sender.sendMessage(ChatColor.RED + "Player not found");
                                    return true;
                                }
                            }
                            target = (Player) sender;
                            switch (args[1].toLowerCase()) {
                                default:
                                    sender.sendMessage(ChatColor.RED + "Invalid debug arg");
                                    break;
                                case "auth":
                                case "authstate":
                                case "state":
                                    sender.sendMessage("AuthState: " + String.valueOf(mc2FA.getAuthHandler().getState(target.getUniqueId())));
                                    break;
                                case "key":
                                    sender.sendMessage("Key: " + String.valueOf(mc2FA.getAuthHandler().getStorageHandler().getKey(target.getUniqueId())));
                                    break;
                            }
                        }
                        break;
                }
            }

        }

        return false;
    }

}
