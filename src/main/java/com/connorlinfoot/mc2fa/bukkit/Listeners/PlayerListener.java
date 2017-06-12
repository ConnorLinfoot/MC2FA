package com.connorlinfoot.mc2fa.bukkit.Listeners;

import com.connorlinfoot.mc2fa.bukkit.Handlers.ConfigHandler;
import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
	private MC2FA mc2FA;

	public PlayerListener(MC2FA mc2FA) {
		this.mc2FA = mc2FA;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		boolean is2fa = mc2FA.getAuthHandler().isEnabled(event.getPlayer().getUniqueId());
		if (is2fa) {
			// Require password from 2FA
		} else {
			if (mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.TRUE || (event.getPlayer().isOp() && mc2FA.getConfigHandler().getForced() == ConfigHandler.Forced.OP)) {
				// Force 2FA
			} else {
				// Advise of 2FA
				event.getPlayer().sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.GOLD + "This server supports two-factor authentication and is highly recommended");
				event.getPlayer().sendMessage(mc2FA.getMessageHandler().getPrefix() + ChatColor.GOLD + "Get started by running /2fa");
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		mc2FA.getAuthHandler().playerQuit(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (mc2FA.getAuthHandler().needsToAuthenticated(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(mc2FA.getMessageHandler().getMessage("Validate"));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (mc2FA.getAuthHandler().needsToAuthenticated(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(mc2FA.getMessageHandler().getMessage("Validate"));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (mc2FA.getAuthHandler().needsToAuthenticated(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(mc2FA.getMessageHandler().getMessage("Validate"));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (mc2FA.getAuthHandler().needsToAuthenticated(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(mc2FA.getMessageHandler().getMessage("Validate"));
		}
	}

}
