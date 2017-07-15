package com.connorlinfoot.mc2fa.bungee.listeners;

import com.connorlinfoot.mc2fa.bungee.MC2FA;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * File created by Connor Linfoot on 15/07/2017.
 * Copyright © 2017. All rights reserved.
 * This file along with any other assets may
 * not be reproduced or distributed in any
 * way without written permission of the author.
 */
public class PlayerListener implements Listener {
    private MC2FA mc2FA;

    public PlayerListener(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
    }

    @EventHandler
    public void onPlayerJoin(PreLoginEvent event) {
        mc2FA.getAuthHandler().playerJoin(event.getConnection().getUniqueId());
    }

}
