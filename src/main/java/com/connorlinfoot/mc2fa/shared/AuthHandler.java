package com.connorlinfoot.mc2fa.shared;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AuthHandler {
	private ArrayList<UUID> authenticated = new ArrayList<>();
	private HashMap<UUID, String> pendingKeys = new HashMap<>();
	private HashMap<UUID, String> keys = new HashMap<>();

	public boolean isEnabled(UUID uuid) {
		return keys.containsKey(uuid);
	}

	public boolean isPending(UUID uuid) {
		return pendingKeys.containsKey(uuid);
	}

	public String createKey(UUID uuid) {
		GoogleAuthenticator authenticator = new GoogleAuthenticator();
		GoogleAuthenticatorKey key = authenticator.createCredentials();
		pendingKeys.put(uuid, key.getKey());
		return key.getKey();
	}

	public boolean validateKey(UUID uuid, Integer password) {
		String key = getKey(uuid);
		if (key != null && new GoogleAuthenticator().authorize(key, password)) {
			authenticated.add(uuid);
			return true;
		}
		return false;
	}

	public boolean approveKey(UUID uuid, Integer password) {
		String key = getPendingKey(uuid);
		if (key != null && new GoogleAuthenticator().authorize(key, password)) {
			authenticated.add(uuid);
			pendingKeys.remove(uuid);
			keys.put(uuid, key);
			return true;
		}
		return false;
	}

	private String getKey(UUID uuid) {
		if (!isEnabled(uuid))
			return null;
		return keys.get(uuid);
	}

	private String getPendingKey(UUID uuid) {
		if (!isPending(uuid))
			return null;
		return pendingKeys.get(uuid);
	}

	public String getQRCodeURL(UUID uuid) {
		String urlTemplate = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/TEST:TEST?secret=%key%";
		String key = getPendingKey(uuid);
		if (key == null)
			return null;
		return urlTemplate.replaceAll("%key%", key);
	}

	public boolean needsToAuthenticate(UUID uuid) {
		return isEnabled(uuid) && !authenticated.contains(uuid);
	}

	public void playerQuit(UUID uuid) {
		if (pendingKeys.containsKey(uuid))
			pendingKeys.remove(uuid);
		if (authenticated.contains(uuid))
			authenticated.remove(uuid);
	}

}
