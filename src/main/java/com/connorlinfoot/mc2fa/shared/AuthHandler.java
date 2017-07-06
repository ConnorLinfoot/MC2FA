package com.connorlinfoot.mc2fa.shared;

import com.connorlinfoot.mc2fa.shared.storage.StorageHandler;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.util.HashMap;
import java.util.UUID;

public abstract class AuthHandler {
    protected StorageHandler storageHandler;
    protected HashMap<UUID, AuthState> authStates = new HashMap<>();
    private HashMap<UUID, String> pendingKeys = new HashMap<>();

    public enum AuthState {
        DISABLED, PENDING_SETUP, PENDING_LOGIN, AUTHENTICATED;
    }

    public AuthState getState(UUID uuid) {
        if (authStates.containsKey(uuid))
            return authStates.get(uuid);
        return null;
    }

    public boolean isEnabled(UUID uuid) {
        return authStates.get(uuid).equals(AuthState.PENDING_LOGIN) || authStates.get(uuid).equals(AuthState.AUTHENTICATED);
    }

    public boolean isPendingSetup(UUID uuid) {
        return authStates.get(uuid).equals(AuthState.PENDING_SETUP);
    }

    public String createKey(UUID uuid) {
        GoogleAuthenticator authenticator = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = authenticator.createCredentials();
        authStates.put(uuid, AuthState.PENDING_SETUP);
        pendingKeys.put(uuid, key.getKey());
        return key.getKey();
    }

    public boolean validateKey(UUID uuid, Integer password) {
        try {
            String key = getKey(uuid);
            if (key != null && new GoogleAuthenticator().authorize(key, password)) {
                authStates.put(uuid, AuthState.AUTHENTICATED);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean approveKey(UUID uuid, Integer password) {
        String key = getPendingKey(uuid);
        if (key != null && new GoogleAuthenticator().authorize(key, password)) {
            authStates.put(uuid, AuthState.AUTHENTICATED);
            pendingKeys.remove(uuid);
            getStorageHandler().setKey(uuid, key);
            return true;
        }
        return false;
    }

    private String getKey(UUID uuid) {
        if (!isEnabled(uuid))
            return null;
        return getStorageHandler().getKey(uuid);
    }

    private String getPendingKey(UUID uuid) {
        if (!isPendingSetup(uuid))
            return null;
        return pendingKeys.get(uuid);
    }

    public String getQRCodeURL(UUID uuid) {
        String urlTemplate = "https://www.google.com/chart?chs=128x128&chld=M%%7C0&cht=qr&chl=otpauth://totp/MC2FA:MC2FA?secret=%key%";
        String key = getPendingKey(uuid);
        if (key == null)
            return null;
        return urlTemplate.replaceAll("%key%", key);
    }

    public boolean needsToAuthenticate(UUID uuid) {
        return isEnabled(uuid) && !authStates.get(uuid).equals(AuthState.AUTHENTICATED);
    }

    public void reset(UUID uuid) {
        pendingKeys.remove(uuid);
        authStates.put(uuid, AuthState.DISABLED);
        getStorageHandler().removeKey(uuid);
    }

    public void playerJoin(UUID uuid) {

    }

    public void playerQuit(UUID uuid) {
        if (pendingKeys.containsKey(uuid))
            pendingKeys.remove(uuid);
        if (authStates.containsKey(uuid))
            authStates.remove(uuid);
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

}
