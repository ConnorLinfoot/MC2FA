package com.connorlinfoot.mc2fa.bungee;

import com.connorlinfoot.mc2fa.bungee.bungeeutils.PacketHandler;
import com.connorlinfoot.mc2fa.bungee.handlers.AuthHandler;
import com.connorlinfoot.mc2fa.bungee.handlers.CommandHandler;
import com.connorlinfoot.mc2fa.bungee.handlers.ConfigHandler;
import com.connorlinfoot.mc2fa.bungee.handlers.MessageHandler;
import com.connorlinfoot.mc2fa.bungee.listeners.PlayerListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

public class MC2FA extends Plugin {
    private ConfigHandler configHandler;
    private AuthHandler authHandler;
    private MessageHandler messageHandler;

    public void onEnable() {
        configHandler = new ConfigHandler(this);

        if (getConfigHandler().getMode() == com.connorlinfoot.mc2fa.shared.ConfigHandler.Mode.UNKNOWN) {
            getLogger().severe("No mode has been set! MC2FA has not been enabled!");
            return;
        }

        // Check if BungeeUtils exists, if so use the correct auth handler etc
        try {
            Class.forName("dev.wolveringer.bungeeutil.player.Player");
            getLogger().info("Found BungeeUtils, hooking in!");
            authHandler = new com.connorlinfoot.mc2fa.bungee.bungeeutils.AuthHandler(this);
            new PacketHandler(this);
        } catch (ClassNotFoundException e) {
            authHandler = new AuthHandler(this);
        }

        messageHandler = new MessageHandler(this);

        getProxy().getPluginManager().registerCommand(this, new CommandHandler(this));
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
        getProxy().getConsole().sendMessage(ChatColor.AQUA + "MC2FA v" + getDescription().getVersion() + " (BungeeCord) has been enabled");
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
