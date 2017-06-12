package com.connorlinfoot.mc2fa.bukkit.Handlers;

import com.connorlinfoot.mc2fa.bukkit.MC2FA;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class MessageHandler extends com.connorlinfoot.mc2fa.shared.MessageHandler {
	private MC2FA mc2FA;
	private YamlConfiguration messagesConfig;

	public MessageHandler(MC2FA mc2FA) {
		this.mc2FA = mc2FA;
	}

	public String getPrefix() {
		String prefix = super.getPrefix();
		if (messagesConfig.isSet("Prefix")) {
			prefix = messagesConfig.getString("Prefix", prefix);
		}
		return ChatColor.translateAlternateColorCodes('&', prefix);
	}

	public void loadConfiguration() {
		File messagesFile = new File(mc2FA.getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			try {
				messagesFile.createNewFile();
				InputStream inputStream = mc2FA.getResource("messages.yml");
				OutputStream outputStream = new FileOutputStream(messagesFile);
				ByteStreams.copy(inputStream, outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

		if (!messagesConfig.isSet("Prefix"))
			messagesConfig.set("Prefix", "&7[&bCratesPlus&7]");

		try {
			messagesConfig.save(messagesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
