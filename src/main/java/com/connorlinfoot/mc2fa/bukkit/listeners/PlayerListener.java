package com.connorlinfoot.mc2fa.bukkit.listeners;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import com.connorlinfoot.mc2fa.bukkit.handlers.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {
    private MC2FA mc2FA;

    public PlayerListener(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        mc2FA.getAuthHandler().playerJoin(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        mc2FA.getAuthHandler().playerQuit(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            if (event.getTo().getBlockZ() != event.getFrom().getBlockZ() || event.getTo().getBlockX() != event.getFrom().getBlockX()) {
                event.getPlayer().sendMessage(mc2FA.getMessageHandler().getMessage("Validate"));
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(mc2FA.getMessageHandler().getMessage("Validate"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(mc2FA.getMessageHandler().getMessage("Validate"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(mc2FA.getMessageHandler().getMessage("Validate"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else if (event.getItemDrop().getItemStack().getType() == Material.MAP && event.getItemDrop().getItemStack().hasItemMeta() && event.getItemDrop().getItemStack().getItemMeta().hasDisplayName() && event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "QR Code")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (mc2FA.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (mc2FA.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

}
