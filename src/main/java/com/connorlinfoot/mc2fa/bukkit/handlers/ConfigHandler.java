package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConfigHandler extends com.connorlinfoot.mc2fa.shared.ConfigHandler {

    public ConfigHandler(MC2FA mc2FA) {
        FileConfiguration config = mc2FA.getConfig();

        if (config.isSet("Debug"))
            debug = config.getBoolean("Debug");

        if (config.isSet("Mode")) {
            try {
                mode = Mode.valueOf(config.getString("Mode").toUpperCase());
            } catch (Exception ignored) {
            }
        }

        if (mode == Mode.DEFAULT || mode == Mode.BUNGEE) {
            mode = Mode.BUKKIT; // Force Bukkit if default or Bungee
        }

        if (config.isSet("Disable Commands"))
            commandsDisabled = config.getBoolean("Disable Commands");

        if (config.isSet("Advise"))
            advise = config.getBoolean("Advise");

        if (config.isSet("GUI Keypad"))
            guiKeypad = config.getBoolean("GUI Keypad");

        if (config.isSet("Whitelisted Commands"))
            whitelistedCommands = config.getStringList("Whitelisted Commands");
        whitelistedCommands.add("2fa");
        whitelistedCommands.add("twofactorauth");
        whitelistedCommands.add("twofactorauthentication");
        whitelistedCommands.add("twofa");
        whitelistedCommands.add("tfa");

        if (config.isSet("Blacklisted Commands"))
            blacklistedCommands = config.getStringList("Blacklisted Commands");

        if (config.isSet("Key Storage")) {
            try {
                keyStorage = KeyStorage.valueOf(config.getString("Key Storage").toUpperCase());
            } catch (Exception ignored) {
            }
        }

        if (keyStorage == KeyStorage.SQLITE) {
            mc2FA.getLogger().warning("SQLite storage is not yet supported, reverting to flat file storage");
            keyStorage = KeyStorage.FLAT;
        } else if (keyStorage == KeyStorage.MYSQL) {
            mc2FA.getLogger().warning("MySQL storage is not yet supported, reverting to flat file storage");
            keyStorage = KeyStorage.FLAT;
        }

        if (config.isSet("Forced")) {
            try {
                forced = Forced.valueOf(config.getString("Forced").toUpperCase());
            } catch (Exception ignored) {
            }
        }

        if (config.isSet("QR Code URL")) {
            qrCodeURL = config.getString("QR Code URL");
        }

        if (config.isSet("OTP Label")) {
            label = config.getString("OTP Label");
        }
    }

    public String getLabel(Player player) {
        if (player == null) {
            return "";
        }
        return getLabel().replaceAll("%%name%%", player.getName());
    }

}
