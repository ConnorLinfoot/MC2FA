package com.connorlinfoot.mc2fa.bukkit;

import com.connorlinfoot.mc2fa.bukkit.handlers.AuthHandler;
import com.connorlinfoot.mc2fa.bukkit.handlers.CommandHandler;
import com.connorlinfoot.mc2fa.bukkit.handlers.ConfigHandler;
import com.connorlinfoot.mc2fa.bukkit.handlers.MessageHandler;
import com.connorlinfoot.mc2fa.bukkit.listeners.PlayerListener;
import com.connorlinfoot.mc2fa.bukkit.utils.MCStats;
import com.connorlinfoot.mc2fa.shared.UpdateHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MC2FA extends JavaPlugin {
    private boolean updateAvailable = false;
    private String updateMessage = "";
    private String pluginMessage = null;
    private UpdateHandler updateHandler;
    private ConfigHandler configHandler;
    private AuthHandler authHandler;
    private MessageHandler messageHandler;

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        updateHandler = new UpdateHandler(getConfig().getBoolean("Update Checks", true), getDescription().getVersion());
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
        checkUpdates();
    }

    private void checkUpdates() {
        if (updateHandler.getUpdateResult() != UpdateHandler.UpdateResult.DISABLED) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                getUpdateHandler().checkForUpdate();
                final UpdateHandler.UpdateResult result = getUpdateHandler().getUpdateResult();
                switch (result) {
                    default:
                    case FAIL_HTTP:
                        updateAvailable = false;
                        updateMessage = getMessageHandler().getPrefix() + "Failed to check for updates.";
                        break;
                    case NO_UPDATE:
                        updateAvailable = false;
                        updateMessage = getMessageHandler().getPrefix() + "No update was found, you are running the latest version. Will check again later.";
                        break;
                    case DISABLED:
                        updateAvailable = false;
                        updateMessage = getMessageHandler().getPrefix() + "You currently have update checks disabled.";
                        break;
                    case UPDATE_AVAILABLE:
                        updateAvailable = true;
                        updateMessage = getMessageHandler().getPrefix() + "An update for MC2FA is available, new version is " + getUpdateHandler().getNewestVersion() + ". Your installed version is " + getDescription().getVersion() + ".\nPlease update to the latest version :)";
                        break;
                }
                Bukkit.getConsoleSender().sendMessage(updateMessage);

                if (getUpdateHandler().getMessage() != null) {
                    pluginMessage = ChatColor.translateAlternateColorCodes('&', getUpdateHandler().getMessage());
                    Bukkit.getConsoleSender().sendMessage(pluginMessage);
                }
            });
        }
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String getPluginMessage() {
        return pluginMessage;
    }

    public void onDisable() {

    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
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
