# MC2FA
MC2FA is a upcoming **free** two-factor authentication plugin for Bukkit, Spigot & BungeeCord (*Soon*). This plugin is still in early development, do not run this on any public server.

## Development Build
[![CircleCI](https://circleci.com/gh/ConnorLinfoot/MC2FA/tree/master.svg?style=svg)](https://api.connorlinfoot.com/v1/ci/artifact/MC2FA/latest/download)

## Features
- Works with Bukkit, Spigot or BungeeCord
- Works out of the box, just copy the plugin and restart your server
- Custom messages/prefixes, make it your own
- In-game QR display using maps *Bukkit/Spigot*
- Supports flat file or MySQL for key storage. *Coming Soon*
- Option to force 2FA for players (or even just on a permission)
- Disables basic tasks like player movement, block breaking, chat, inventory changes and more

## Planned
- MySQL support
- Fallback key, allow players to be given a backup key in the case that they lose their 2FA device
- Admin commands, allow staff to view players with 2FA and disable if needed
- Auto-allow if on the same IP within a certain time (option)

## Requirements
- Bukkit/Spigot/BungeeCord 1.8+
- Java 8

## Modes
MC2FA Currently supports 3 modes, these modes are as followed:
- *Bukkit Mode:* Useful if you only have one Bukkit instance and do not use BungeeCord. Just install the plugin into your Bukkit server.
- *BungeeGUI Mode:* Useful if you have a BungeeCord network. Requires BungeeUtils and the MC2FA plugin installed to BungeeCord.
- *Crossover Mode:* Also useful if you have a BungeeCord network. Requires the MC2FA plugin to be installed on the proxy and each Bukkit server. 