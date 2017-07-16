package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    private String qrCodeURL = "https://www.google.com/chart?chs=128x128&chld=M%%7C0&cht=qr&chl=otpauth://totp/%%label%%?secret=%%key%%";
    private boolean debug = false;
    private boolean enabled = true;
    private boolean commandsDisabled = true;
    private KeyStorage keyStorage = KeyStorage.FLAT;
    private Forced forced = Forced.FALSE;
    private List<String> whitelistedCommands = new ArrayList<>();
    private List<String> blacklistedCommands = new ArrayList<>();

    public enum KeyStorage {
        FLAT, SQLITE, MYSQL
    }

    public enum Forced {
        TRUE, FALSE, OP
    }

    public ConfigHandler(MC2FA mc2FA) {
        FileConfiguration config = mc2FA.getConfig();

        if (config.isSet("Debug"))
            debug = config.getBoolean("Debug");

        if (config.isSet("Enabled"))
            enabled = config.getBoolean("Enabled");

        if (config.isSet("Disable Commands"))
            commandsDisabled = config.getBoolean("Disable Commands");

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
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isCommandsDisabled() {
        return commandsDisabled;
    }

    public List<String> getWhitelistedCommands() {
        return whitelistedCommands;
    }

    public List<String> getBlacklistedCommands() {
        return blacklistedCommands;
    }

    public KeyStorage getKeyStorage() {
        return keyStorage;
    }

    public Forced getForced() {
        return forced;
    }

    public String getQrCodeURL() {
        return qrCodeURL;
    }

}
