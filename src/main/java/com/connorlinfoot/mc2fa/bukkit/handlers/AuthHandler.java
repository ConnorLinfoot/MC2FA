package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import com.connorlinfoot.mc2fa.bukkit.utils.ImageRenderer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AuthHandler extends com.connorlinfoot.mc2fa.shared.AuthHandler {
    private MC2FA mc2FA;
    private ArrayList<UUID> openGUIs = new ArrayList<>();
    private HashMap<UUID, ArrayList<ItemStack>> heldItems = new HashMap<>();

    public AuthHandler(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
    }

    public void giveQRItem(MC2FA mc2FA, Player player) {
        String url = getQRCodeURL(player.getUniqueId());
        final MessageHandler messageHandler = mc2FA.getMessageHandler();
        new BukkitRunnable() {
            @Override
            public void run() {
                MapView view = Bukkit.createMap(player.getWorld());
                view.getRenderers().forEach(view::removeRenderer);
                try {
                    ImageRenderer renderer = new ImageRenderer(url);
                    view.addRenderer(renderer);
                    ItemStack mapItem = new ItemStack(Material.MAP, 1, view.getId());
                    ItemMeta mapMeta = mapItem.getItemMeta();
                    mapMeta.setDisplayName(ChatColor.GOLD + "QR Code");
                    mapItem.setItemMeta(mapMeta);

                    // Clear inventory into "heldItems"
                    ArrayList<ItemStack> items = new ArrayList<>();
                    player.getInventory().forEach(itemStack -> {
                        items.add(itemStack);
                        if (itemStack != null)
                            player.getInventory().remove(itemStack);
                    });
                    heldItems.put(player.getUniqueId(), items);
                    player.getInventory().addItem(mapItem);
                    player.getInventory().setHeldItemSlot(0);
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "Please use the QR code given to setup two-factor authentication");
                    player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "Please validate by entering your key: /2fa <key>");
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                }
            }
        }.runTaskAsynchronously(mc2FA);
    }

    public void giveItemsBack(Player player) {
        if (!heldItems.containsKey(player.getUniqueId()))
            return;
        player.getInventory().remove(0);
        ArrayList<ItemStack> items = heldItems.get(player.getUniqueId());
        final int[] i = {0};
        items.forEach(itemStack -> {
            if (itemStack != null)
                player.getInventory().setItem(i[0], itemStack);
            i[0]++;
        });
        heldItems.remove(player.getUniqueId());
    }

    public void open2FAGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, "2FA");

        // I always forget my inventories, okay!
        // 0 1 2 3 4 5 6 7 8
        // 9 0 1 2 3 4 5 6 7
        // 8 9 0 1 2 3 4 5 6
        // 7 8 9 0 1 2 3 4 5
        // 6 7 8 9 0 1 2 3 4
        // 5 6 7 8 9 0 1 2 3

        // 0 1 2 3 4 5 6 7 8
        // 9 0 1 * * * 5 6 7
        // 8 9 0 * * * 4 5 6
        // 7 8 9 * * * 3 4 5
        // 6 7 8 9 * 1 2 3 4
        // 5 6 7 8 9 0 1 2 3

        int slot = 12;
        for (int i = 0; i < 9; i++) {
            if (slot == 15) {
                slot = 21;
            } else if (slot == 24) {
                slot = 30;
            } else if (slot == 33) {
                slot = 40;
            }
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.WHITE + "" + (i + 1));
            itemStack.setItemMeta(itemMeta);
            gui.setItem(slot, itemStack);
            slot++;
        }
        openGUIs.add(player.getUniqueId());
        player.openInventory(gui);
    }

    @Override
    public void playerJoin(UUID uuid) {
        super.playerJoin(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            return;
        }
        boolean is2fa = isEnabled(uuid);
        if (is2fa) {
            if (needsToAuthenticate(uuid)) {
                // Require password from 2FA
                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.RED + "/2fa");
                open2FAGUI(player);
            }
        } else {
            if (mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.TRUE || (player.isOp() && mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.OP)) {
                // Force 2FA
            } else {
                // Advise of 2FA
                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.GOLD + "This server supports two-factor authentication and is highly recommended");
                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.GOLD + "Get started by running /2fa");
            }
        }
    }

    public void playerQuit(UUID uuid) {
        super.playerQuit(uuid);
        if (openGUIs.contains(uuid))
            openGUIs.remove(uuid);
    }

}
