package com.connorlinfoot.mc2fa.bungee.bungeeutils;

import com.connorlinfoot.mc2fa.bungee.MC2FA;
import dev.wolveringer.bungeeutil.packetlib.PacketLib;
import dev.wolveringer.bungeeutil.packets.PacketPlayInBlockDig;
import dev.wolveringer.bungeeutil.packets.PacketPlayInBlockPlace;
import dev.wolveringer.bungeeutil.packets.PacketPlayInCloseWindow;
import dev.wolveringer.bungeeutil.packets.PacketPlayInPositionLook;

import java.util.Arrays;
import java.util.List;

/**
 * File created by Connor Linfoot on 15/07/2017.
 * Copyright © 2017. All rights reserved.
 * This file along with any other assets may
 * not be reproduced or distributed in any
 * way without written permission of the author.
 */
public class PacketHandler {

    public PacketHandler(MC2FA mc2FA) {
        List<Class> packetsToHandle = Arrays.asList(PacketPlayInCloseWindow.class, PacketPlayInPositionLook.class, PacketPlayInBlockDig.class, PacketPlayInBlockPlace.class);

        PacketLib.addHandler(event -> {
            for (Class packet : packetsToHandle) {
                if (packet.isInstance(event.getPacket())) {
                    if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
                        mc2FA.getAuthHandler().open2FAGUI(event.getPlayer());
                    }
                }
            }

        });
    }

}
