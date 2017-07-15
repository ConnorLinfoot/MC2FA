package com.connorlinfoot.mc2fa.bungee.handlers;

import com.connorlinfoot.mc2fa.bungee.MC2FA;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MessageHandler extends com.connorlinfoot.mc2fa.shared.MessageHandler {
    private MC2FA mc2FA;
//    private YamlConfiguration messagesConfig;

    public MessageHandler(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
        loadConfiguration();
    }

    public String getPrefix() {
//        String prefix = super.getPrefix();
//        if (messagesConfig.isSet("Prefix")) {
//            prefix = messagesConfig.getString("Prefix") + "&r ";
//        }
//        return ChatColor.translateAlternateColorCodes('&', prefix);
        return "";
    }

    public String getMessage(String message) {
//        return getPrefix() + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(message, message));
        return message;
    }

    public void sendMessage(ProxiedPlayer player, String message) {
        player.sendMessage(getMessage(message));
    }

    public void loadConfiguration() {
//        File messagesFile = new File(mc2FA.getDataFolder(), "messages.yml");
//        if (!messagesFile.exists()) {
//            try {
//                messagesFile.createNewFile();
//                InputStream inputStream = mc2FA.getResource("messages.yml");
//                OutputStream outputStream = new FileOutputStream(messagesFile);
//                ByteStreams.copy(inputStream, outputStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
//
//        if (!messagesConfig.isSet("Prefix"))
//            messagesConfig.set("Prefix", "&7[&bMC2FA&7]");
//
//        if (!messagesConfig.isSet("Validate"))
//            messagesConfig.set("Validate", "&cPlease validate your account with two-factor authentication");
//
//        if (!messagesConfig.isSet("Invalid Key"))
//            messagesConfig.set("Invalid Key", "&cThe key you entered was not valid, please try again");
//
//        if (!messagesConfig.isSet("Setup Success"))
//            messagesConfig.set("Setup Success", "&aYou have successfully setup two-factor authentication");
//
//        try {
//            messagesConfig.save(messagesFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
