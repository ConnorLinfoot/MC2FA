package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class MessageHandler extends com.connorlinfoot.mc2fa.shared.MessageHandler {
    private MC2FA mc2FA;
    private YamlConfiguration messagesConfig;

    public MessageHandler(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
        loadConfiguration();
    }

    public String getPrefix() {
        String prefix = super.getPrefix();
        if (messagesConfig.isSet("Prefix")) {
            prefix = messagesConfig.getString("Prefix") + "&r ";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getMessage(String message) {
        message = messagesConfig.getString(message, message);
        if (message.isEmpty()) {
            return "";
        }
        return getPrefix() + ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(Player player, String message) {
        message = getMessage(message);
        if (message.isEmpty())
            return;
        player.sendMessage(message);
    }

    public void loadConfiguration() {
        if (!mc2FA.getDataFolder().exists()) {
            mc2FA.getDataFolder().mkdirs();
        }
        File messagesFile = new File(mc2FA.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try {
                messagesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        if (!messagesConfig.isSet("Prefix")) {
            messagesConfig.set("Prefix", "&7[&bMC2FA&7]");
        }

        for (String message : getDefaults()) {
            if (!messagesConfig.isSet(message)) {
                messagesConfig.set(message, message);
            }
        }

        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
