package com.connorlinfoot.mc2fa.bukkit.storage;

import com.connorlinfoot.mc2fa.shared.storage.StorageHandler;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FlatStorage extends StorageHandler {
    private File file;
    private YamlConfiguration configuration;

    public FlatStorage(File file) {
        this.file = file;
        try {
            file.createNewFile();
            this.configuration = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getKey(UUID uuid) {
        if (configuration.isSet(uuid.toString() + ".Key")) {
            return configuration.getString(uuid.toString() + ".Key");
        }
        return null;
    }

    public void setKey(UUID uuid, String key) {
        configuration.set(uuid.toString() + ".Key", key);
        save();
    }

    public void removeKey(UUID uuid) {
        configuration.set(uuid.toString() + ".Key", null);
        save();
    }

    private void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
