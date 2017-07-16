package com.connorlinfoot.mc2fa.bukkit;

import com.connorlinfoot.mc2fa.bukkit.handlers.AuthHandler;
import com.connorlinfoot.mc2fa.bukkit.handlers.CommandHandler;
import com.connorlinfoot.mc2fa.bukkit.handlers.ConfigHandler;
import com.connorlinfoot.mc2fa.bukkit.handlers.MessageHandler;
import com.connorlinfoot.mc2fa.bukkit.listeners.PlayerListener;
import com.connorlinfoot.mc2fa.bukkit.utils.MCStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MC2FA extends JavaPlugin {
    private ConfigHandler configHandler;
    private AuthHandler authHandler;
    private MessageHandler messageHandler;

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        configHandler = new ConfigHandler(this);
        authHandler = new AuthHandler(this);
        messageHandler = new MessageHandler(this);

        if (getConfig().getBoolean("MCStats", true)) {
            try {
                MCStats mcstats = new MCStats(this);
                mcstats.start();
            } catch (IOException e) {
                // Failed to submit the stats :-(
            }
        }

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                getAuthHandler().playerJoin(player.getUniqueId());
                player.getInventory().forEach(itemStack -> {
                    if (getAuthHandler().isQRCodeItem(itemStack))
                        player.getInventory().remove(itemStack);
                });
            }
        }

        getServer().getPluginCommand("2fa").setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "MC2FA v" + getDescription().getVersion() + " has been enabled");
    }

    public void onDisable() {

    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public AuthHandler getAuthHandler() {
        return authHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
