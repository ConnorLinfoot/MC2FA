package com.connorlinfoot.mc2fa.bungee;

import com.connorlinfoot.mc2fa.bungee.bungeeutils.PacketHandler;
import com.connorlinfoot.mc2fa.bungee.handlers.AuthHandler;
import com.connorlinfoot.mc2fa.bungee.handlers.ConfigHandler;
import com.connorlinfoot.mc2fa.bungee.handlers.MessageHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

public class MC2FA extends Plugin {
    private ConfigHandler configHandler;
    private AuthHandler authHandler;
    private MessageHandler messageHandler;

    public void onEnable() {
        configHandler = new ConfigHandler(this);

        // Check if BungeeUtils exists, if so use the correct auth handler etc
        Plugin bungeeUtils = getProxy().getPluginManager().getPlugin("BungeeUtils");
        if (bungeeUtils != null) {
            authHandler = new com.connorlinfoot.mc2fa.bungee.bungeeutils.AuthHandler(this);
            new PacketHandler(this);
        } else {
            authHandler = new AuthHandler(this);
        }

        messageHandler = new MessageHandler(this);

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
