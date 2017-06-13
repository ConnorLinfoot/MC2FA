package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import com.connorlinfoot.mc2fa.bukkit.utils.ImageRenderer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

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
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "You have successfully setup two-factor authentication");
                } else {
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.RED + "The key you entered was not valid, please try again");
                }
            }
        } else if (!mc2FA.getAuthHandler().isEnabled(player.getUniqueId())) {
            mc2FA.getAuthHandler().createKey(player.getUniqueId());
            String url = mc2FA.getAuthHandler().getQRCodeURL(player.getUniqueId());
            give_qr_map(url, player);
        } else {
            if (args.length > 0) {
                boolean isValid = mc2FA.getAuthHandler().validateKey(player.getUniqueId(), Integer.valueOf(args[0]));
                player.sendMessage(String.valueOf(isValid));
            }
        }

        return false;
    }

    private void give_qr_map(final String urlStr, final Player player) {
        final MessageHandler messageHandler = mc2FA.getMessageHandler();

        new BukkitRunnable() {
            @Override
            public void run() {
                MapView view = Bukkit.createMap(player.getWorld());
                for (MapRenderer mapRenderer : view.getRenderers()) {
                    view.removeRenderer(mapRenderer);
                }
                try {
                    ImageRenderer renderer = new ImageRenderer(urlStr);
                    view.addRenderer(renderer);
                    player.getInventory().addItem(new ItemStack(Material.MAP, 1, view.getId()));
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "Please use the QR code given to setup two-factor authentication");
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "Please validate by entering your key: /2fa <key>");
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                }
            }
        }.runTaskAsynchronously(mc2FA);
    }

}
