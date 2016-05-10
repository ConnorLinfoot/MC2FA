package com.connorlinfoot.mc2fa.bukkit;

import com.connorlinfoot.mc2fa.bukkit.Handlers.CommandHandler;
import com.connorlinfoot.mc2fa.bukkit.Listeners.PlayerListener;
import com.connorlinfoot.mc2fa.shared.AuthHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class MC2FA extends JavaPlugin implements CommandExecutor {
	private AuthHandler authHandler;

	@Override
	public void onEnable() {
		authHandler = new AuthHandler();
		getServer().getPluginCommand("2fa").setExecutor(new CommandHandler(this));
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "MC2FA v" + getDescription().getVersion() + " has started!");
	}

	@Override
	public void onDisable() {

	}

	public AuthHandler getAuthHandler() {
		return authHandler;
	}

}
