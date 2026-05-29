![Logo](assets/logo.png)

*[简体中文](README.md) | English*

# Effortless Structure

[Effortless Structure]() is a multiplatform mod for placing and breaking blocks in Minecraft. It offers a set of modes that let players create structures and designs with ease. In addition, it includes a randomizer that randomly selects items from a predefined list for random placement.

<div style="text-align: center">
    <a href="https://modrinth.com/mod/effortless">Modrinth</a>
    <span> | </span>
    <a href="https://www.curseforge.com/minecraft/mc-mods/effortless">CurseForge</a>
    <span> | </span>
    <a href="https://github.com/kakala666/effortless">GitHub</a>
    <span> | </span>
    <a href="https://github.com/kakala666/effortless/wiki">Documentation</a>
</div>


## Acknowledgements

This mod is a modified version of [Effortless Structure](https://github.com/huskuraft/effortless), originally created by [**Huskcasaca**](https://github.com/huskcasaca). The original author is no longer maintaining it, so this fork fixes known issues and continues maintenance on top of their code.

Heartfelt thanks to the original author **Huskcasaca** — without their years of hard work, this mod would not exist.

## Features

- **Pure Vanilla Compatibility**: This mod is designed to be fully compatible with a pure vanilla game, adding no new items and making no incompatible modifications.
- **Item Randomizer**: This mod includes an item randomizer that lets players randomly place blocks and entities from a predefined list.
- **Clipboard**: This mod includes a clipboard that lets players copy and paste blocks and entities between worlds.

## Platforms

- You need to install this mod on both the client side and the server side.
- You can use this mod on a server whose platform differs from your client's.
- The same mod jar file can be used for multiple target platforms.

> **This fork currently supports NeoForge only.** Support for Fabric and Forge is planned to be restored gradually in the future.

### Targets

| Filename                      | Versions | NeoForge |
|-------------------------------|----------|:--------:|
| `effortless-1.21.1-3.4.0.jar` | `1.21.1` | &check;  |

### Compatible Plugins

- You can use this mod together with the following plugins.

| Name                                                                        | Supported | Note                                          |
|-----------------------------------------------------------------------------|:---------:|-----------------------------------------------|
| [Open Parties and Claims](https://modrinth.com/mod/open-parties-and-claims) |  &check;  | Allows you to claim chunks and set build permissions. |
| [FTB Chunks](https://www.curseforge.com/minecraft/mc-mods/ftb-chunks-forge) |  &check;  | Allows you to claim chunks and set build permissions. |

## How to Use

- Hold the **LEFT_ALT / LEFT_OPTION** key to open the **Build Mode Radial menu**, where you can switch build modes to create different structures. The left side of the menu also has buttons for **Undo / Redo**, **Replace**, **Settings**, **Pattern**, and **Clipboard**.
- Press the **ATTACK / DESTROY** key to start breaking blocks.
- Press the **USE_ITEM / PLACE_BLOCK** key to start placing/interacting with blocks.
- Press the **LEFT_BRACKET** (`[`) key to **Undo** your last build operation.
- Press the **RIGHT_BRACKET** (`]`) key to **Redo** the last undone operation.

### Build Modes

- Build modes are the basic shapes you choose when creating a structure. Each mode has its own features (such as hollow or filled).

- **Disabled**: Place in the vanilla way.
- **Single**: Place a single block with increased reach distance.
- **Line**: Place a line along any one of the three axes.
- **Wall**: Place a wall along the X or Z axis.
- **Floor**: Place a floor along the Y axis.
- **Diagonal Line**: Place a line at any angle.
- **Diagonal Wall**: Place a wall at any angle.
- **Slope Floor**: Place a sloped surface at any angle.
- **Square**: Place a square plane.
- **Cuboid**: Place a cuboid.
- **Circle**: Place a circle along any one of the three axes.
- **Cylinder**: Place a cylinder along any one of the three axes.
- **Sphere**: Place a sphere made of blocks.
- **Dome**: Place a dome made of blocks.
- **Pyramid**: Place a pyramid made of blocks.
- **Cone**: Place a cone made of blocks.

### Replace

- You can choose how existing blocks are replaced when placing new blocks.

- **Disable**: Replace only air and replaceable blocks (such as grass).
- **Blocks and Air**: Replace air as well as any blocks that can be destroyed by tools.
- **Blocks Only**: Replace only blocks that can be destroyed by tools.
- **Offhand Only**: Replace only blocks matching the item held in your offhand.

### Pattern

- You can create complex shapes by combining different **transformers**. For example, use "Mirror" to create a symmetric copy of a wall, or use the "Item Randomizer" to build a wall of random blocks. There are currently 4 types of transformers.

- **Mirror**: Generates a symmetric mirror of blocks and entities, for both even and uneven builds.
- **Array**: Copies blocks and entities in a specified direction a specified number of times.
- **Radial**: Places blocks and entities in a circular pattern around a center point. The circle can be divided into sectors, and each sector contains a copy of the placement.
- **Item Randomizer**: Randomizes the placement of blocks.

### Clipboard

- You can use the clipboard to transfer structures between worlds by copying and pasting.

## Dependencies

### NeoForge

| Dependency      | Download                                    |
|-----------------|---------------------------------------------|
| NeoForge Loader | https://neoforged.net/categories/releases/  |

## Contributors

* **[Requioss](https://www.curseforge.com/members/requioss)**, author of the original inspiration [Effortless Building](https://www.curseforge.com/minecraft/mc-mods/effortless-building).
* **[loehnertj](https://github.com/loehnertj)**, for the 1.20.2 port.
* **[Huskcasaca](https://github.com/huskcasaca)**, author of the upstream version of this mod (Effortless Structure).

## License

Effortless Structure is released under the [LGPLv3](LICENSE) license.
