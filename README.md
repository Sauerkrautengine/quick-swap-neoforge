# Quick Swap Hotkeys

Two hotkeys for Minecraft 26.3 that work without opening your inventory:

- **X** puts a Totem of Undying from your inventory into your offhand
- **G** swaps between your elytra and your chestplate

It is a client-side Fabric mod. Servers don't need it, and it only sends the same inventory clicks you would make by hand.

## How it works

**Totem:** if you already hold a totem in your offhand, nothing happens. Otherwise the mod takes the first totem it finds in your inventory or hotbar and swaps it into your offhand. Whatever was in the offhand goes back to the slot the totem came from.

**Elytra / chestplate:** if you wear an elytra, it looks for a chestplate. If you wear a chestplate (or nothing), it looks for an elytra. When there are several, it picks in this order:

1. the one in your offhand
2. the one with more durability left
3. the one with more enchantment levels

A nearly broken elytra (1 durability left) is skipped.

## Changing the keys

Options → Controls → Key Binds → **Quick Swap Hotkeys**.

## Installing

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.3.
2. Put [Fabric API](https://modrinth.com/mod/fabric-api) and the mod's `.jar` into your `mods` folder.

Requires Java 25 and Fabric Loader 0.19.5 or newer.


## License

MIT
