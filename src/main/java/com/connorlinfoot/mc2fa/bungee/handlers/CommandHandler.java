package com.connorlinfoot.mc2fa.bungee.handlers;

import com.connorlinfoot.mc2fa.bungee.MC2FA;
import com.connorlinfoot.mc2fa.shared.AuthHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandHandler extends Command {
    private MC2FA mc2FA;

    public CommandHandler(MC2FA mc2FA) {
        super("2fa", "", "twofactorauth", "twofactorauthentication", "twofa", "tfa");
        this.mc2FA = mc2FA;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageHandler messageHandler = mc2FA.getMessageHandler();
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "This command must be ran as a player");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        ProxiedPlayer target;

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
                }
            }
        } else if (mc2FA.getAuthHandler().getState(player.getUniqueId()).equals(AuthHandler.AuthState.PENDING_SETUP)) {
            if (args.length == 0) {
                player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Please validate your two-factor authentication key with /2fa <key>");
            } else {
                Integer key;
                try {
                    key = Integer.valueOf(args[0]);
                } catch (Exception e) {
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "Invalid key entered");
                    return;
                }

                boolean approved = mc2FA.getAuthHandler().approveKey(player.getUniqueId(), key);
                if (approved) {
                    player.sendMessage(messageHandler.getMessage("Setup Success"));
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
                            player.sendMessage(mc2FA.getAuthHandler().getQRCodeURL(mc2FA.getConfigHandler().getQrCodeURL(), player.getUniqueId()));
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
                        if (!sender.hasPermission("mc2fa.admin")) {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to run this command");
                            return;
                        }
                        if (args.length == 1) {
                            sender.sendMessage(ChatColor.RED + "No debug arg");
                        } else {
                            if (args.length > 2) {
                                String playerName = args[2];
                                target = ProxyServer.getInstance().getPlayer(playerName);
                                if (target == null) {
                                    sender.sendMessage(ChatColor.RED + "Player not found");
                                    return;
                                }
                            }
                            target = (ProxiedPlayer) sender;
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
    }

}
