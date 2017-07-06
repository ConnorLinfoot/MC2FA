package com.connorlinfoot.mc2fa.shared.storage;

import java.util.UUID;

public abstract class StorageHandler {

    public abstract String getKey(UUID uuid);

    public abstract void setKey(UUID uuid, String key);

    public abstract void removeKey(UUID uuid);

}
