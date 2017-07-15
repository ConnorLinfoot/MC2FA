package com.connorlinfoot.mc2fa.bungee.handlers;

import com.connorlinfoot.mc2fa.bungee.MC2FA;

public class ConfigHandler extends com.connorlinfoot.mc2fa.shared.ConfigHandler {

    public ConfigHandler(MC2FA mc2FA) {
        super();
//        FileConfiguration config = mc2FA.getConfig();
//
//        if (config.isSet("Debug"))
//            debug = config.getBoolean("Debug");
//
//        if (config.isSet("Enabled"))
//            enabled = config.getBoolean("Enabled");
//
//        if (config.isSet("Disable Commands"))
//            commandsDisabled = config.getBoolean("Disable Commands");
//
//        if (config.isSet("Whitelisted Commands"))
//            whitelistedCommands = config.getStringList("Whitelisted Commands");
//        whitelistedCommands.add("2fa");
//        whitelistedCommands.add("twofactorauth");
//        whitelistedCommands.add("twofactorauthentication");
//        whitelistedCommands.add("twofa");
//        whitelistedCommands.add("tfa");
//
//        if (config.isSet("Blacklisted Commands"))
//            blacklistedCommands = config.getStringList("Blacklisted Commands");
//
//        if (config.isSet("Key Storage")) {
//            try {
//                keyStorage = KeyStorage.valueOf(config.getString("Key Storage").toUpperCase());
//            } catch (Exception ignored) {
//            }
//        }
//
//        if (keyStorage == KeyStorage.MYSQL) {
//            mc2FA.getLogger().warning("MySQL storage is not yet supported, reverting to flat file storage");
//            keyStorage = KeyStorage.FLAT;
//        }
//
//        if (config.isSet("Forced")) {
//            try {
//                forced = Forced.valueOf(config.getString("Forced").toUpperCase());
//            } catch (Exception ignored) {
//            }
//        }
    }

}
