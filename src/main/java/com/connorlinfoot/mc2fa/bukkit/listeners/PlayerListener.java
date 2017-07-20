package com.connorlinfoot.mc2fa.bukkit.listeners;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import com.connorlinfoot.mc2fa.bukkit.events.PlayerStateChangeEvent;
import com.connorlinfoot.mc2fa.shared.AuthHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {
    private MC2FA mc2FA;

    public PlayerListener(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        mc2FA.getAuthHandler().playerJoin(event.getPlayer().getUniqueId());
        event.getPlayer().getInventory().forEach(itemStack -> {
            if (mc2FA.getAuthHandler().isQRCodeItem(itemStack))
                event.getPlayer().getInventory().remove(itemStack);
        });
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.getPlayer().setWalkSpeed(0);
            event.getPlayer().setFlySpeed(0);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        mc2FA.getAuthHandler().playerQuit(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            if (event.getTo().getBlockZ() != event.getFrom().getBlockZ() || event.getTo().getBlockX() != event.getFrom().getBlockX()) {
                mc2FA.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            mc2FA.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            mc2FA.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            mc2FA.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().startsWith("MC2FA") && mc2FA.getAuthHandler().needsToAuthenticate(event.getWhoClicked().getUniqueId()) && mc2FA.getAuthHandler().hasGUIOpen(event.getWhoClicked().getUniqueId())) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                int num = Integer.parseInt(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
                mc2FA.getAuthHandler().enterNumGUI((Player) event.getWhoClicked(), num);
                mc2FA.getAuthHandler().open2FAGUI((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        } else if (mc2FA.getAuthHandler().needsToAuthenticate(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }

        if ((mc2FA.getAuthHandler().isQRCodeItem(event.getCurrentItem()) || mc2FA.getAuthHandler().isQRCodeItem(event.getCursor())) && (event.getInventory().getType() != InventoryType.PLAYER)) {
            event.setCancelled(true);
        } else if (event.getHotbarButton() > -1 && mc2FA.getAuthHandler().isQRCodeItem(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent event) {
        if (mc2FA.getAuthHandler().isQRCodeItem(event.getItem()) && event.getDestination().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (mc2FA.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            if (mc2FA.getConfigHandler().isCommandsDisabled()) {
                String[] args = event.getMessage().substring(1).split("\\s+");
                if (args.length > 0) {
                    String command = args[0];
                    if (!mc2FA.getConfigHandler().getWhitelistedCommands().contains(command)) {
                        event.setCancelled(true);
                        mc2FA.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
                    }
                }
            } else {
                String[] args = event.getMessage().substring(1).split("\\s+");
                if (args.length > 0) {
                    String command = args[0];
                    if (mc2FA.getConfigHandler().getBlacklistedCommands().contains(command)) {
                        event.setCancelled(true);
                        mc2FA.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) {
            if (mc2FA.getAuthHandler().isQRCodeItem(event.getPlayer().getItemInHand())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAuthState(PlayerStateChangeEvent event) {
        if (event.getAuthState().equals(AuthHandler.AuthState.AUTHENTICATED)) {
            event.getPlayer().setFlySpeed((float) 0.1);
            event.getPlayer().setWalkSpeed((float) 0.2);
        }
    }

}
