package com.connorlinfoot.mc2fa.bungee.storage;

import com.connorlinfoot.mc2fa.shared.storage.StorageHandler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FlatStorage extends StorageHandler {
    private File file;
    private Configuration configuration;

    public FlatStorage(File file) {
        this.file = file;
        try {
            file.createNewFile();
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getKey(UUID uuid) {
        return configuration.getString(uuid.toString() + ".Key", null);
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
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
