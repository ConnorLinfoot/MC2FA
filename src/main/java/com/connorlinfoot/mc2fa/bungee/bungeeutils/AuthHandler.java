package com.connorlinfoot.mc2fa.bungee.bungeeutils;

import com.connorlinfoot.mc2fa.bungee.MC2FA;
import dev.wolveringer.bungeeutil.inventory.Inventory;
import dev.wolveringer.bungeeutil.item.*;
import dev.wolveringer.bungeeutil.player.Player;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * File created by Connor Linfoot on 15/07/2017.
 * Copyright © 2017. All rights reserved.
 * This file along with any other assets may
 * not be reproduced or distributed in any
 * way without written permission of the author.
 */
public class AuthHandler extends com.connorlinfoot.mc2fa.bungee.handlers.AuthHandler {
    private ArrayList<UUID> openGUIs = new ArrayList<>();
    private HashMap<UUID, String> currentGUIKeys = new HashMap<>();

    public AuthHandler(MC2FA mc2FA) {
        super(mc2FA);
    }

    @Override
    public void open2FAGUI(ProxiedPlayer proxiedPlayer) {
        Player player = (Player) proxiedPlayer;
        String title = "MC2FA";
        if (currentGUIKeys.containsKey(player.getUniqueId()) && currentGUIKeys.get(player.getUniqueId()).length() > 0) {
            if (currentGUIKeys.get(player.getUniqueId()).length() == 6) {
                boolean isValid = validateKey(player.getUniqueId(), Integer.valueOf(currentGUIKeys.get(player.getUniqueId())));
                if (isValid) {
                    player.closeInventory();
                    openGUIs.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Success");
                    return;
                } else {
                    currentGUIKeys.remove(player.getUniqueId());
                    title += " - Invalid";
                }
            } else {
                title += " - " + currentGUIKeys.get(player.getUniqueId());
            }
        }
        Inventory gui = new Inventory(54, title);

        int slot = 12;
        for (int i = 0; i < 10; i++) {
            if (slot == 15) {
                slot = 21;
            } else if (slot == 24) {
                slot = 30;
            } else if (slot == 33) {
                slot = 40;
            }
//                Item item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
            int no = (i + 1);
            if (no == 10) {
                no = 0;
            }

            Item item = ItemBuilder.create().material(Material.STAINED_GLASS_PANE).durability(13).name(ChatColor.WHITE + "" + no).listener(new ClickListener() {
                @Override
                public void click(ItemStack.Click click) {
                    int num = Integer.parseInt(ChatColor.stripColor(click.getItem().getItemMeta().getDisplayName()));
                    enterNumGUI(click.getPlayer(), num);
                    open2FAGUI(click.getPlayer());
                }
            }).build();
            gui.setItem(slot, item);
            slot++;
        }

        Item wat = ItemBuilder.create().material(Material.PAPER).name(ChatColor.AQUA + "What's this?").lore(ChatColor.WHITE + "You have connected to the").lore(ChatColor.WHITE + "network on an account with").lore(ChatColor.WHITE + "two-factor authentication enabled.").build();
        gui.setItem(45, wat);

        Item disconnect = ItemBuilder.create().material(Material.WOOL).durability(14).name(ChatColor.RED + "Cancel").lore(ChatColor.WHITE + "Cancel and disconnect").lore(ChatColor.WHITE + "from the network!").listener(click -> click.getPlayer().disconnect(ChatColor.RED + "Failed to Authenticate!")).build();
        gui.setItem(53, disconnect);

        openGUIs.add(player.getUniqueId());
        player.openInventory(gui);
    }

    public void enterNumGUI(ProxiedPlayer player, int num) {
        String current = "";
        if (currentGUIKeys.containsKey(player.getUniqueId())) {
            current = currentGUIKeys.get(player.getUniqueId());
        }
        currentGUIKeys.put(player.getUniqueId(), current + String.valueOf(num));
    }

    public void playerQuit(UUID uuid) {
        super.playerQuit(uuid);
        if (openGUIs.contains(uuid))
            openGUIs.remove(uuid);
        if (currentGUIKeys.containsKey(uuid))
            currentGUIKeys.remove(uuid);
    }

    public boolean hasGUIOpen(UUID uuid) {
        return isEnabled(uuid) && openGUIs.contains(uuid);
    }

}
