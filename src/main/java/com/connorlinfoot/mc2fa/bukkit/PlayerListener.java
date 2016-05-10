package com.connorlinfoot.mc2fa.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
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
		boolean is2fa = mc2FA.getAuthHandler().isEnabled(event.getPlayer());
		if( is2fa ) {
			// Require password from 2FA
		} else {
			// Advise of 2FA
			event.getPlayer().sendMessage(ChatColor.GOLD + "This server supports two-factor authentication and is highly recommended!");
			event.getPlayer().sendMessage(ChatColor.GOLD + "Get setup by running /2fa");
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		mc2FA.getAuthHandler().playerQuit(event.getPlayer());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(mc2FA.getAuthHandler().needsToAuthenticated(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Please validate your account with two-factor authentication");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(mc2FA.getAuthHandler().needsToAuthenticated(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Please validate your account with two-factor authentication");
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(mc2FA.getAuthHandler().needsToAuthenticated(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Please validate your account with two-factor authentication");
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(mc2FA.getAuthHandler().needsToAuthenticated(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Please validate your account with two-factor authentication");
		}
	}

}
