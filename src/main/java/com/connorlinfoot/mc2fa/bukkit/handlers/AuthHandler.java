package com.connorlinfoot.mc2fa.bukkit.handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import com.connorlinfoot.mc2fa.bukkit.events.PlayerStateChangeEvent;
import com.connorlinfoot.mc2fa.bukkit.storage.FlatStorage;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AuthHandler extends com.connorlinfoot.mc2fa.shared.AuthHandler {
    private MC2FA mc2FA;
    private ArrayList<UUID> openGUIs = new ArrayList<>();
    private HashMap<UUID, String> currentGUIKeys = new HashMap<>();

    public AuthHandler(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
        switch (mc2FA.getConfigHandler().getKeyStorage()) {
            default:
            case FLAT:
                this.storageHandler = new FlatStorage(new File(mc2FA.getDataFolder(), "data.yml"));
                break;
            case SQLITE:
            case MYSQL:
                // SoonTM
                Bukkit.getLogger().warning("How? O.o");
                break;
        }
    }

    public void sendLinkedMessage(Player player, String message, String url) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " {\"text\":\"\",\"extra\":[{\"text\":\"%%message%%\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"%%url%%\"}}]}".replaceAll("%%message%%", message).replaceAll("%%url%%", url));
    }

    public void giveQRItem(MC2FA mc2FA, Player player) {
        String url = getQRCodeURL(mc2FA.getConfigHandler().getQrCodeURL(), player.getUniqueId());
        final MessageHandler messageHandler = mc2FA.getMessageHandler();
        if (player.getInventory().firstEmpty() < 0) {
            // Just send a link, inventory is full!
            messageHandler.sendMessage(player, "&aPlease use the QR code given to setup two-factor authentication");
            sendLinkedMessage(player, messageHandler.getMessage("&aPlease click here to open the QR code"), url.replaceAll("128x128", "256x256"));
            messageHandler.sendMessage(player, "&aPlease validate by entering your key: /2fa <key>");
        } else {
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

                        if (player.getInventory().firstEmpty() < 0) {
                            // Send message instead!
                            messageHandler.sendMessage(player, "&aPlease use the QR code given to setup two-factor authentication");
                            sendLinkedMessage(player, messageHandler.getMessage("&aPlease click here to open the QR code"), url.replaceAll("128x128", "256x256"));
                            messageHandler.sendMessage(player, "&aPlease validate by entering your key: /2fa <key>");
                        } else {
                            ItemStack itemStack0 = null;
                            if (player.getInventory().firstEmpty() != 0) {
                                itemStack0 = player.getInventory().getItem(0);
                            }
                            player.getInventory().setItem(0, mapItem);
                            if (itemStack0 != null) {
                                player.getInventory().addItem(itemStack0);
                            }
                            player.getInventory().setHeldItemSlot(0);
                            messageHandler.sendMessage(player, "&aPlease use the QR code given to setup two-factor authentication");
                            messageHandler.sendMessage(player, "&aPlease validate by entering your key: /2fa <key>");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                    }
                }
            }.runTaskAsynchronously(mc2FA);
        }
    }

    public void open2FAGUI(Player player) {
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
        Inventory gui = Bukkit.createInventory(null, 54, title);

        int slot = 12;
        for (int i = 0; i < 10; i++) {
            if (slot == 15) {
                slot = 21;
            } else if (slot == 24) {
                slot = 30;
            } else if (slot == 33) {
                slot = 40;
            }
            int no = (i + 1);
            if (no == 10) {
                no = 0;
            }
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, no, (short) 13);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.WHITE + "" + no);
            itemStack.setItemMeta(itemMeta);
            gui.setItem(slot, itemStack);
            slot++;
        }
        openGUIs.add(player.getUniqueId());
        player.openInventory(gui);
    }

    public void enterNumGUI(Player player, int num) {
        String current = "";
        if (currentGUIKeys.containsKey(player.getUniqueId())) {
            current = currentGUIKeys.get(player.getUniqueId());
        }
        currentGUIKeys.put(player.getUniqueId(), current + String.valueOf(num));
    }

    public void playerJoin(UUID uuid) {
        super.playerJoin(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            return;
        }

        // Load auth state
        if (getStorageHandler().getKey(uuid) != null) {
            changeState(uuid, AuthState.PENDING_LOGIN);
        } else {
            changeState(uuid, AuthState.DISABLED);
        }

        boolean is2fa = isEnabled(uuid);
        if (is2fa) {
            if (needsToAuthenticate(uuid)) {
                // Require key from 2FA
                mc2FA.getMessageHandler().sendMessage(player, "&cTwo-factor authentication is enabled on this account");
                mc2FA.getMessageHandler().sendMessage(player, "&cPlease authenticate using /2fa <key>");
                if (mc2FA.getConfigHandler().isGuiKeypad())
                    Bukkit.getScheduler().runTaskLater(mc2FA, () -> open2FAGUI(player), 5L);
            }
        } else {
            if (mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.TRUE || (player.isOp() && mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.OP)) {
                // Force 2FA
                mc2FA.getAuthHandler().createKey(player.getUniqueId());
                mc2FA.getAuthHandler().giveQRItem(mc2FA, player);
            } else {
                // Advise of 2FA
                if (mc2FA.getConfigHandler().isAdvise()) {
                    mc2FA.getMessageHandler().sendMessage(player, "&6This server supports two-factor authentication and is highly recommended");
                    mc2FA.getMessageHandler().sendMessage(player, "&6Get started by running \"/2fa enable\"");
                }
            }
        }
    }

    public boolean hasGUIOpen(UUID uuid) {
        return isEnabled(uuid) && openGUIs.contains(uuid);
    }

    public void playerQuit(UUID uuid) {
        super.playerQuit(uuid);
        if (openGUIs.contains(uuid))
            openGUIs.remove(uuid);
        if (currentGUIKeys.containsKey(uuid))
            currentGUIKeys.remove(uuid);
    }

    public void changeState(UUID uuid, AuthState authState) {
        if (authState == getState(uuid))
            return;

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            PlayerStateChangeEvent event = new PlayerStateChangeEvent(player, authState);
            mc2FA.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return;
            authState = event.getAuthState();
        }

        authStates.put(uuid, authState);
    }

    @Override
    public String getQRCodeURL(String urlTemplate, UUID uuid) {
        String label;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            label = mc2FA.getConfigHandler().getLabel(player);
        } else {
            label = "";
        }
        return super.getQRCodeURL(urlTemplate, uuid).replaceAll("%%label%%", label);
    }

    public boolean isQRCodeItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == Material.MAP && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "QR Code");
    }

}
