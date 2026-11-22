# Changelog

All notable changes to Lucky's Blocky Siege are listed here.
Every pull request adds its entry under **Work In Progress**. When a version is released,
that section becomes the new version (with a row in the table below), and a new empty
Work In Progress section is started.

# Navigation
(Click on version number to jump to its changelog.)

| Version                               | Release Date | Type |
|---------------------------------------|--------------|------|
| [7.0.0 (Beta)](#7.0.0-beta) | `11/22/2026`        | Beta |

# Work In Progress (7.1.0)

**Added:**
- Soul Bomb cannon ammo (echo shard surrounded by glass). It isn't a projectile: the cannon plays the Warden's sonic charge sound while its fuse burns, and when it fires, a Warden-style sonic boom beam shoots out up to 64 blocks in the aimed direction. The beam hurts everything in its line (ignoring armor), tunnels about three blocks wide through blocks until it runs out of strength (harder blocks use more), and can break through obsidian (at a high cost) but stops at bedrock, reinforced deepslate and other unbreakable blocks. The tunnel is then lined with sculk veins. Mob crews only break the blocks their cannonballs can break, and mobs only break blocks when mob griefing is on. ([#67](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/67))
- Crossbow golem towers can now generate in vanilla villages (plains, taiga, snowy, desert and savanna, each built from that biome's materials): a stone-based watchtower with an armed crossbow golem on top, which defends the village against hostile mobs and leaves players alone. They are uncommon, roughly one per village (a single entry added to each vanilla house pool, nothing replaced) and can be turned off with the `villageCrossbowTowers` config option. ([#68](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/68))
- Crossbow golems can hang from ceilings: place the crossbow golem block against the underside of a block and the golem hangs upside down below it, aiming and firing from there. Placing it on a floor works as before. ([#71](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/71))
- Armor trims for the cannon, multi-cannon and spewer (vanilla patterns and materials; the item icon shows the trim too), applied at a smithing table just like armor: put an armor trim smithing template, the weapon, and a trim material in the table. The trim shows on the weapon in the material's colors, is kept when the weapon is broken and picked up (it shows in the item tooltip), and can be stripped by right-clicking with shears. ([#72](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/72))
- Hoppers, droppers and item pipes can insert arrows, fireworks and other valid ammo into a crossbow golem's base (nothing can be pulled back out). ([#74](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/74))

**Changed:**

**Fixed:**
- Cannonballs (normal and spectral) no longer crash the game when they hit a Dynamic Trees branch or any other block that reads its hardness from its position. ([#70](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/70), fixes [#69](https://github.com/luckyowlstudios/luckys-blocky-siege/issues/69))

# 7.0.0 (Beta)

**Added:**
- The mod now builds for both NeoForge and Fabric (Minecraft 1.21.1) from one shared `common` project. ([#1](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/1))
- Crossbow golem (built like an iron golem: iron block base, dispenser on top, and a log), with a spawn egg in the Spawn Eggs tab. It holds the crossbow from its menu, fires arrows and fireworks, and plays a muzzle ring animation when it shoots. ([#15](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/15), [#22](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/22), [#35](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/35), [#36](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/36), [#37](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/37))
- Crossbow golems join their placer's scoreboard team (also when placed with a spawn egg) and spare their teammates, while golems on rival teams shoot each other and other rival-team mobs, even when the same player built both. The menu shows the team icon. ([#21](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/21), [#45](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/45), [#60](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/60), [#61](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/61))
- Crossbow golems show cracks as they take damage, and their menu health bar shows their health. ([#25](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/25), [#26](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/26))
- Repair a crossbow golem by right-clicking it with an iron ingot (heals 25 HP). ([#33](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/33), [#46](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/46))
- Crossbow golems behave like iron golems with a player-made flag, and fletcher villagers restock their arrows in the morning. ([#43](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/43), [#48](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/48))
- New advancement "Iron Sentry" for building a crossbow golem. ([#40](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/40))
- Pillagers, Vindicators and Piglins can crew cannons, burst cannons and spewers. Right-click a cannon, multi-cannon or spewer with a spawn egg of a crewing mob to spawn it already manning the weapon (the egg is used up outside creative). ([#16](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/16), [#62](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/62))
- Mortar: a cannon that lobs its shot in a high arc (45 to 85 degrees). It uses the cannon's ammo, gunpowder, power settings and menu, and its shells whistle on the way down. Seated players aim it by looking toward where the shell should land. Pillager and Piglin crews use it from up to 160 blocks away without line of sight, and only fire when the arc will land on the target. ([#64](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/64))
- Right-click placed ammo (cannonball, spectral, fire, frost and wind bomb stacks) to take one piece back. ([#20](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/20))
- A distant cannon sound, and the old crossbow golem item texture. ([#17](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/17))
- Spewers accept modded fluids and have a drain slot. ([#7](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/7))
- Hold the fire key (right-click) to fire continuously from a spewer or a volley rack. ([#9](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/9), [#11](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/11))
- Russian, Ukrainian, Spanish (Spain) and Spanish (Mexico) translations. ([#41](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/41))
- Sable support: the crossbow golem rides along on moving sub-levels, targets and shoots from them, and you can crew siege weapons on them. Cannonballs also hit and break blocks on sub-levels. Sable is optional: the mod loads and runs without it. ([#44](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/44), [#49](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/49), [#50](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/50), [#65](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/65))
- Dying to a cannonball now shows one of 8 random death messages, with a version that names the player who fired it. They are translated into every supported language. ([#52](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/52))

**Changed:**
- The hunger bar is now hidden while controlling a siege weapon, like the hotbar and experience bar. ([#63](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/63))
- The ballista golem is now the crossbow golem, everywhere (classes, ids, names and assets). ([#32](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/32))
- The cannon fires any block, and the burst cannon fires any item. Their tooltips now say so, and the spewer's says it shoots any liquid. ([#3](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/3), [#5](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/5), [#6](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/6))
- The spewer's info (question mark) button is gone. ([#7](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/7))
- The crossbow golem block no longer has a crafting recipe. Build the iron block, dispenser, log and carved pumpkin structure to get one (the block is still in the creative tab).
- The burst cannon's projectiles fly further. ([#8](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/8))
- Burst cannon bullets fired from firework stars follow the star's explosion effects. ([#14](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/14))
- Cannonballs skim, bounce, roll and come to rest instead of sticking into the ground like arrows. ([#4](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/4), [#19](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/19))
- Landed cannonballs break instantly in creative mode. ([#12](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/12))
- Players now face the turret's aim direction when mounting it, instead of the turret snapping to where they looked. ([#10](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/10))
- The explosive keg projectile renders as the explosive barrel block, and TNT and keg projectiles blink like primed TNT. ([#13](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/13), [#23](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/23))
- The crossbow golem's ammo box sits lower the emptier its ammo is. ([#38](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/38))
- The crossbow golem shoots mobs far below it, such as from a tower. ([#34](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/34))
- A crossbow with Mending no longer loses durability when the golem fires it. ([#47](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/47))
- The crossbow golem menu no longer shows "No team" when it has no team. ([#31](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/31))
- The `cannon` damage type is now `cannonball`, and cannonball and bullet damage count as projectile damage (the `minecraft:is_projectile` tag). ([#52](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/52))

**Fixed:**
- Stacked cannonball blocks glowing yellow. ([#2](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/2))
- Cannonball trail streaks staying drawn after landing, which made the ball look like it was hanging. ([#18](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/18))
- The cannon staying stuck glowing in its charging state. ([#24](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/24))
- Frost, ember and sculk piles never melting away. ([#28](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/28))
- A bucket loop in the spewer's liquid slot, and the right-click menu opening while controlling it. ([#27](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/27))
- A duplicate health bar in the crossbow golem menu. ([#29](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/29))
- The crossbow golem's bow strings drawing the wrong way while charging and snapping back. ([#30](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/30), [#39](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/39))

**Compatibility:**
- **Sable and Create Aeronautics:** siege weapons work on moving ships and sub-levels. Sable is declared optional (NeoForge `optional`, Fabric `suggests`), so nothing changes if it is not installed.
- **LambDynamicLights (and other dynamic-light mods that read the standard datapack format):** fire bombs and embers light up their surroundings.
- **Farmer's Delight:** its crates and rice bags (beetroot, cabbage, carrot, onion, potato, tomato and rice) have custom cannon behavior and scatter their produce on impact.
- **Supplementaries:** its bombs (normal and blue) work as cannon ammo.
- **Amendments:** the dragon charge works as cannon ammo.
- **Modded fluids** work in the spewer.

**Development:**
- README rewritten as the mod page description for Modrinth and CurseForge.
- Hand-written lang, sounds, particles, tags, blockstates and item models are now produced by datagen providers. ([#42](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/42))
- Sable, Create and Create Aeronautics load in dev runs, and NeoForge for development is 21.1.267. Sable Companion is bundled for sub-level compatibility. ([#44](https://github.com/luckyowlstudios/luckys-blocky-siege/pull/44))
