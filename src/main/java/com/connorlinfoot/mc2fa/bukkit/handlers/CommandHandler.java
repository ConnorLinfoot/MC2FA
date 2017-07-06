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
            sender.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "This command must be ran as a player");
            return false;
        }
        Player player = (Player) sender;
        Player target;

        if (mc2FA.getAuthHandler().getState(player.getUniqueId()).equals(AuthHandler.AuthState.PENDING_LOGIN)) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Correct usage: /2fa <key>");
            } else {
                try {
                    boolean isValid = mc2FA.getAuthHandler().validateKey(player.getUniqueId(), Integer.valueOf(args[0]));
                    if (isValid) {
                        player.sendMessage(ChatColor.GREEN + "You have successfully authenticated");
                    } else {
                        player.sendMessage(ChatColor.RED + "Incorrect key, please try again");
                    }
                } catch (Exception e) {
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Invalid key entered");
                    return true;
                }
            }
        } else if (mc2FA.getAuthHandler().getState(player.getUniqueId()).equals(AuthHandler.AuthState.PENDING_SETUP)) {
            if (args.length == 0) {
                player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Please validate your two-factor authentication key with /" + string + " <key>");
            } else {
                Integer key;
                try {
                    key = Integer.valueOf(args[0]);
                } catch (Exception e) {
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Invalid key entered");
                    return true;
                }

                boolean approved = mc2FA.getAuthHandler().approveKey(player.getUniqueId(), key);
                if (approved) {
                    player.sendMessage(messageHandler.getMessage("Setup Success"));
                    player.getInventory().forEach(itemStack -> {
                        if (itemStack != null && itemStack.getType() == Material.MAP && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "QR Code")) {
                            player.getInventory().remove(itemStack);
                        }
                    });
                } else {
                    player.sendMessage(messageHandler.getMessage("Invalid Key"));
                }
            }
        } else {

            if (args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("help"))) {
                sender.sendMessage(ChatColor.AQUA + "---------- " + ChatColor.GOLD + "MC2FA" + ChatColor.AQUA + " ----------");
                sender.sendMessage(ChatColor.YELLOW + "// TODO");
            } else {
                switch (args[0].toLowerCase()) {
                    default:
                        sender.sendMessage(ChatColor.RED + "Unknown argument");
                        break;
                    case "enable":
                    case "on":
                    case "true":
                        if (mc2FA.getAuthHandler().getState(player.getUniqueId()).equals(AuthHandler.AuthState.DISABLED)) {
                            mc2FA.getAuthHandler().createKey(player.getUniqueId());
                            mc2FA.getAuthHandler().giveQRItem(mc2FA, player);
                        } else {
                            player.sendMessage(ChatColor.RED + "You are already setup with 2FA");
                        }
                        break;
                    case "reset":
                        if (mc2FA.getAuthHandler().isEnabled(player.getUniqueId())) {
                            mc2FA.getAuthHandler().reset(player.getUniqueId());
                            player.sendMessage(ChatColor.GREEN + "Your 2FA has been reset");
                        } else {
                            player.sendMessage(ChatColor.RED + "You are not setup with 2FA");
                        }
                        break;
                    case "debug":
                        if (!sender.isOp()) {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to run this command");
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

//            Player player = (Player) sender;
//            if (mc2FA.getAuthHandler().isPendingSetup(player.getUniqueId())) {
//                if (args.length == 0) {
//                    player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Please validate your two-factor authentication key with /" + string + " <key>");
//                } else {
//                    Integer key;
//                    try {
//                        key = Integer.valueOf(args[0]);
//                    } catch (Exception e) {
//                        player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Invalid key entered");
//                        return false;
//                    }
//
//                    boolean approved = mc2FA.getAuthHandler().approveKey(player.getUniqueId(), key);
//                    if (approved) {
//                        player.sendMessage(messageHandler.getMessage("Setup Success"));
//                        player.getInventory().forEach(itemStack -> {
//                            if (itemStack != null && itemStack.getType() == Material.MAP && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "QR Code")) {
//                                player.getInventory().remove(itemStack);
//                            }
//                        });
//                    } else {
//                        player.sendMessage(messageHandler.getMessage("Invalid Key"));
//                    }
//                }
//            } else if (!mc2FA.getAuthHandler().isEnabled(player.getUniqueId())) {
//                mc2FA.getAuthHandler().createKey(player.getUniqueId());
//                mc2FA.getAuthHandler().giveQRItem(mc2FA, player);
//            } else {
//                if (args.length > 0) {
//                    boolean isValid = mc2FA.getAuthHandler().validateKey(player.getUniqueId(), Integer.valueOf(args[0]));
//                    player.sendMessage(String.valueOf(isValid));
//                }
//            }

        }

        return false;
    }

}
