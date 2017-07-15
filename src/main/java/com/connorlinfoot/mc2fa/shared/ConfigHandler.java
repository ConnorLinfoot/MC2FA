package com.connorlinfoot.mc2fa.shared;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigHandler {
    protected boolean debug = false;
    protected boolean enabled = true;
    protected boolean commandsDisabled = true;
    protected KeyStorage keyStorage = KeyStorage.FLAT;
    protected Forced forced = Forced.FALSE;
    protected Mode mode = Mode.UNKNOWN;
    protected List<String> whitelistedCommands = new ArrayList<>();
    protected List<String> blacklistedCommands = new ArrayList<>();

    public enum KeyStorage {
        FLAT, MYSQL
    }

    public enum Forced {
        TRUE, FALSE, PERM
    }

    public enum Mode {
        UNKNOWN, BUKKIT, BUNGEE, CROSSOVER
    }

    public ConfigHandler() {
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

    public Mode getMode() {
        return mode;
    }

}
