package com.connorlinfoot.mc2fa.shared;

import java.util.Arrays;
import java.util.List;

public abstract class MessageHandler {
    private String prefix = "&7[&bMC2FA&7]&r ";
    private List<String> defaults = Arrays.asList(
            "&cPlease validate your account with two-factor authentication",
            "&cThe key you entered was not valid, please try again",
            "&aYou have successfully setup two-factor authentication",
            "&cThis command must be ran as a player",
            "&cCorrect usage: /2fa <key>",
            "&aYou have successfully authenticated",
            "&cIncorrect key, please try again",
            "&cInvalid key entered",
            "&cPlease validate your two-factor authentication key with /2fa <key>",
            "&cUnknown argument",
            "&cYou are already setup with 2FA",
            "&aYour 2FA has been reset",
            "&cYou are not setup with 2FA",
            "&cYou do not have permission to run this command"
    );

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public abstract String getMessage(String message);

    public abstract void loadConfiguration();

    public List<String> getDefaults() {
        return defaults;
    }

}
