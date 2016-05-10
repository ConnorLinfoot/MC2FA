package com.connorlinfoot.mc2fa.bukkit;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AuthHandler {
	private ArrayList<UUID> authenticated = new ArrayList<>();
	private HashMap<UUID, String> pendingKeys = new HashMap<>();
	private HashMap<UUID, String> keys = new HashMap<>();

	public boolean isEnabled(Player player) {
		return keys.containsKey(player.getUniqueId());
	}

	public boolean isPending(Player player) {
		return pendingKeys.containsKey(player.getUniqueId());
	}

	public String createKey(Player player) {
		GoogleAuthenticator authenticator = new GoogleAuthenticator();
		GoogleAuthenticatorKey key = authenticator.createCredentials();
		pendingKeys.put(player.getUniqueId(), key.getKey());
		return key.getKey();
	}

	public boolean validateKey(Player player, Integer password) {
		String key = getKey(player);
		if (key != null && new GoogleAuthenticator().authorize(key, password)) {
			authenticated.add(player.getUniqueId());
			return true;
		}
		return false;
	}

	public boolean approveKey(Player player, Integer password) {
		String key = getPendingKey(player);
		if (key != null && new GoogleAuthenticator().authorize(key, password)) {
			authenticated.add(player.getUniqueId());
			pendingKeys.remove(player.getUniqueId());
			keys.put(player.getUniqueId(), key);
			return true;
		}
		return false;
	}

	private String getKey(Player player) {
		if (!isEnabled(player))
			return null;
		return keys.get(player.getUniqueId());
	}

	private String getPendingKey(Player player) {
		if (!isPending(player))
			return null;
		return pendingKeys.get(player.getUniqueId());
	}

	public String getQRCodeURL(Player player) {
		String urlTemplate = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%server%:%playername%?secret=%key%";
		String key = getPendingKey(player);
		if (key == null)
			return null;
		return urlTemplate.replaceAll("%playername%", player.getName()).replaceAll("%server%", "TestMC").replaceAll("%key%", key);
	}

	public boolean needsToAuthenticated(Player player) {
		return isEnabled(player) && !authenticated.contains(player.getUniqueId());
	}

	public void playerQuit(Player player) {
		if (pendingKeys.containsKey(player.getUniqueId()))
			pendingKeys.remove(player.getUniqueId());
		if (authenticated.contains(player.getUniqueId()))
			authenticated.remove(player.getUniqueId());
	}

}
