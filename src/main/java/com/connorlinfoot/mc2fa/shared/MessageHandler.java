package com.connorlinfoot.mc2fa.shared;

public abstract class MessageHandler {
	private String prefix = "&7[&bMC2FA&7]&r ";

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public abstract void loadConfiguration();
}
