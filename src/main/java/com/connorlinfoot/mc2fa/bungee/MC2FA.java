package com.connorlinfoot.mc2fa.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class MC2FA extends Plugin {

	@Override
	public void onEnable() {
		getProxy().getLogger().severe("MC2FA does NOT yet support BungeeCord, sorry :(");
	}

}
