# MC2FA
MC2FA is a upcoming **free** two-factor authentication plugin for Bukkit, Spigot & BungeeCord (*Soon*).

## Development Build
[![CircleCI](https://circleci.com/gh/ConnorLinfoot/MC2FA/tree/master.svg?style=svg)](https://api.connorlinfoot.com/v1/ci/artifact/MC2FA/latest/download)

## Features
- Works instantly, just copy the plugin and restart your server.
- Custom messages/prefixes, make it your own!
- Supports flat file or MySQL for key storage.
- Option to force 2FA for players (or even just OPs)
- Disables basic tasks like player movment, block breaking, chat, inventory changes and more.

## TODO
- BungeeCord support.
- Fallback key, allow players to be given a backup key in the case that they lose their 2FA device.
- Admin commands, allow staff to view players with 2FA and disable if needed.
- Built-in QR display, using maps if possible!
- Auto-allow if on the same IP within a certain time (option)