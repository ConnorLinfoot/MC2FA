package com.connorlinfoot.mc2fa.bungee.handlers;

import com.connorlinfoot.mc2fa.bungee.MC2FA;
import com.connorlinfoot.mc2fa.bungee.storage.FlatStorage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.util.UUID;

public class AuthHandler extends com.connorlinfoot.mc2fa.shared.AuthHandler {
    private MC2FA mc2FA;

    public AuthHandler(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
        switch (mc2FA.getConfigHandler().getKeyStorage()) {
            default:
            case FLAT:
                this.storageHandler = new FlatStorage(new File(mc2FA.getDataFolder(), "data.yml"));
                break;
            case MYSQL:
                // SoonTM
                ProxyServer.getInstance().getLogger().warning("How? O.o");
                break;
        }
    }

    public void open2FAGUI(ProxiedPlayer proxiedPlayer) {

    }

    public void enterNumGUI(ProxiedPlayer proxiedPlayer, int num) {
    }

    public void playerJoin(UUID uuid) {
        super.playerJoin(uuid);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player == null || !player.isConnected()) {
            return;
        }

        // Load auth state
        if (getStorageHandler().getKey(uuid) != null) {
            authStates.put(uuid, AuthState.PENDING_LOGIN);
        } else {
            authStates.put(uuid, AuthState.DISABLED);
        }

        boolean is2fa = isEnabled(uuid);
        if (is2fa) {
            if (needsToAuthenticate(uuid)) {
                // Require password from 2FA
                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.RED + "You must authenticate using /2fa");
            }
        } else {
//            if (mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.TRUE || (player.isOp() && mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.OP)) {
            if (mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.TRUE) {
                // Force 2FA
                mc2FA.getAuthHandler().createKey(player.getUniqueId());
                player.sendMessage(mc2FA.getAuthHandler().getQRCodeURL(player.getUniqueId()));
            } else {
                // Advise of 2FA
                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.GOLD + "This server supports two-factor authentication and is highly recommended");
                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.GOLD + "Get started by running /2fa");
            }
        }
    }

    public boolean hasGUIOpen(UUID uuid) {
        return false;
    }

}
