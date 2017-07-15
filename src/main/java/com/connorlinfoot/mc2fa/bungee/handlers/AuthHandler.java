package com.connorlinfoot.mc2fa.bungee.handlers;

import com.connorlinfoot.mc2fa.bungee.MC2FA;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class AuthHandler extends com.connorlinfoot.mc2fa.shared.AuthHandler {
    private MC2FA mc2FA;

    public AuthHandler(MC2FA mc2FA) {
        this.mc2FA = mc2FA;
        switch (mc2FA.getConfigHandler().getKeyStorage()) {
            default:
            case FLAT:
//                this.storageHandler = new FlatStorage(new File(mc2FA.getDataFolder(), "data.yml"));
                break;
            case MYSQL:
                // SoonTM
                ProxyServer.getInstance().getLogger().warning("How? O.o");
                break;
        }
    }

//    public void giveQRItem(MC2FA mc2FA, Player player) {
//        String url = getQRCodeURL(player.getUniqueId());
//        final MessageHandler messageHandler = mc2FA.getMessageHandler();
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                MapView view = Bukkit.createMap(player.getWorld());
//                view.getRenderers().forEach(view::removeRenderer);
//                try {
//                    ImageRenderer renderer = new ImageRenderer(url);
//                    view.addRenderer(renderer);
//                    ItemStack mapItem = new ItemStack(Material.MAP, 1, view.getId());
//                    ItemMeta mapMeta = mapItem.getItemMeta();
//                    mapMeta.setDisplayName(ChatColor.GOLD + "QR Code");
//                    mapItem.setItemMeta(mapMeta);
//
//                    player.getInventory().addItem(mapItem);
////                    player.getInventory().setHeldItemSlot(0);
//                    player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "Please use the QR code given to setup two-factor authentication");
//                    player.sendMessage(messageHandler.getPrefix() + ChatColor.GREEN + "Please validate by entering your key: /2fa <key>");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    player.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
//                }
//            }
//        }.runTaskAsynchronously(mc2FA);
//    }

    public void open2FAGUI(ProxiedPlayer proxiedPlayer) {

    }

    public void enterNumGUI(ProxiedPlayer proxiedPlayer, int num) {
    }

    public void playerJoin(UUID uuid) {
//        super.playerJoin(uuid);
//        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
//        if (player == null || !player.isConnected()) {
//            return;
//        }
//
//        // Load auth state
//        if (getStorageHandler().getKey(uuid) != null) {
//            authStates.put(uuid, AuthState.PENDING_LOGIN);
//        } else {
//            authStates.put(uuid, AuthState.DISABLED);
//        }
//
//        boolean is2fa = isEnabled(uuid);
//        if (is2fa) {
//            if (needsToAuthenticate(uuid)) {
//                // Require password from 2FA
//                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.RED + "You must authenticate using /2fa");
//                Bukkit.getScheduler().runTaskLater(mc2FA, () -> open2FAGUI(player), 5L);
//            }
//        } else {
//            if (mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.TRUE || (player.isOp() && mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.OP)) {
//                // Force 2FA
//                mc2FA.getAuthHandler().createKey(player.getUniqueId());
//                mc2FA.getAuthHandler().giveQRItem(mc2FA, player);
//            } else {
//                // Advise of 2FA
//                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.GOLD + "This server supports two-factor authentication and is highly recommended");
//                player.sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.GOLD + "Get started by running /2fa");
//            }
//        }
    }

    public boolean hasGUIOpen(UUID uuid) {
        return false;
    }

}
