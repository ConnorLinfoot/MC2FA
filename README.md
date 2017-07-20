# MC2FA
MC2FA is a upcoming **free** two-factor authentication plugin for Bukkit, Spigot & BungeeCord (*Soon*). This plugin is still in early development, do not run this on any public server.

## Development Build
[![CircleCI](https://circleci.com/gh/ConnorLinfoot/MC2FA/tree/master.svg?style=svg)](https://api.connorlinfoot.com/v1/ci/artifact/MC2FA/latest/download)

## Features
- Works out of the box, just copy the plugin and restart your server
- Custom messages/prefixes, make it your own
- In-game QR display using maps
- Supports flat file, SQLite or MySQL for key storage. (SQLite and MySQL coming soon)
- Option to force 2FA for players (or even just OPs)
- Disables basic tasks like player movement, block breaking, chat, inventory changes and more

## Planned
- BungeeCord support
- MySQL support
- Fallback key, allow players to be given a backup key in the case that they lose their 2FA device
- Admin commands, allow staff to view players with 2FA and disable if needed
- Auto-allow if on the same IP within a certain time (option)

## Requirements
- Bukkit/Spigot 1.8+
- Java 8+