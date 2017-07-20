package com.connorlinfoot.mc2fa.shared;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigHandler {
    protected String qrCodeURL = "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/%%label%%?secret=%%key%%";
    protected String label = "%%name%%:MC2FA";
    protected boolean debug = false;
    protected boolean enabled = true;
    protected boolean commandsDisabled = true;
    protected boolean advise = true;
    protected boolean guiKeypad = true;
    protected KeyStorage keyStorage = KeyStorage.FLAT;
    protected Forced forced = Forced.FALSE;
    protected Mode mode = Mode.DEFAULT;
    protected List<String> whitelistedCommands = new ArrayList<>();
    protected List<String> blacklistedCommands = new ArrayList<>();

    public enum KeyStorage {
        FLAT, SQLITE, MYSQL
    }

    public enum Forced {
        TRUE, FALSE, PERM, OP
    }

    public enum Mode {
        DEFAULT, BUKKIT, BUNGEE, CROSSOVER
    }

    public ConfigHandler() {
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isAdvise() {
        return advise;
    }

    public boolean isGuiKeypad() {
        return guiKeypad;
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

    public Mode getMode() {
        return mode;
    }

    public String getQrCodeURL() {
        return qrCodeURL;
    }

    public String getLabel() {
        return label;
    }

}
