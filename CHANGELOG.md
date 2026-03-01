# 2.7.1

- Fix Wizard Staff being classified as a wand

# 2.7.0

DISCLAIMER: All spell books and spell scrolls will be reset, due to major API changes. Some (looted) weapons with custom spell containers become non-functional, and need to be re-obtained. Apologies for the inconvenience.

- Update to Spell Engine 1.9.0
- Spell books now offer 3 spells only, to match other classes
- Arcane Blast spell is now attached to Arcane Staff variants
- Frostbolt spell is now attached to Frost Staff variants
- Pyroblast spell is now attached to Fire Staff variants
- Wizard Staff now comes with spell choices

# 2.6.5

- Rebalance all weapon attributes

# 2.6.4

- Rebalance some of the armor attributes

# 2.6.3

- Update AzureLib

# 2.6.2

- Add basic armor trim support for all wizard robes

# 2.6.1

- Add vanilla recipe book support (fully datagen recipes) 
- Add spellbook descriptions
- Remove spell specific target modifiers
- Update translations

# 2.6.0

- Migrate to Architectury

# 2.5.2

- Fix infested snowy wizard tower #79

# 2.5.1 

- Built in compatibility with Repurposed Structures #2, thanks TelepathicGrunt

# 2.5.0

- Frozen status effect now only removed upon direct hit
- Frozen status effect may not be overcome by speed boost

# 2.4.12

- Fix Arcane Blast description

# 2.4.11

- Update to latest Spell Engine

# 2.4.10

- Frost Shard now bounces instead of piercing

# 2.4.9

- Update enchant compatibility tags
- Update translations

# 2.4.8

- AzureLib Armor version requirement (fabric.mod.json)

# 2.4.7

- Add spell scroll textures 

# 2.4.6

- Rebalance villager trades

# 2.4.5

- Fix trade advancement
- Wands now use different classification tag
- Add spell casting advancements

# 2.4.4

- Fix some disassembly smelting recipes

# 2.4.3

- Add Wizard trade advancement
- Add smelting recipes for disassembling archer weapons and armor pieces

# 2.4.2

- Update to Spell Engine 1.6

# 2.4.1

- Fix sound related issues, and crashes upon disconnects

# 2.4.0

- Support Spell Engine 1.5.0
- Frost Nova now prevents jumping

# 2.3.5

- Add 1 bounce to Arcane Missiles
- Rework cost structure

# 2.3.4

- Improve Frostbolt particles
- Add category for all crafting recipes

# 2.3.3

- Improve Arcane Beam particles
- Improve Arcane Missile projectile path

# 2.3.2

- Fix Arcane Missiles damage

# 2.3.1

- Arcane Blast spells is now tier 1 spell
- Rework Arcane Missile spell (now tier 2 spell), continuously fires missiles for 4 seconds

# 2.3.0

- Support Spell Engine 1.4
- Support AzureLib Armor 3.X
- Frost Nova freeze no longer works on any bosses
- Frost spells deal +30% damage against `#minecraft:freeze_hurts_extra_types` 
- Frost spells deal -30% damage against `#minecraft:freeze_immune_entity_types`
- Fire spells get +30% critical strike chance against `#minecraft:freeze_immune_entity_types`

# 2.2.5

- Support Lithostitched v1.4
- Increase status effect apply limit for Frost spells

# 2.2.4

- Update village wizard chest loot table

# 2.2.3

- Add spell scroll names

# 2.2.2

- Add support to Lithostitched village structure injection
- Update structure files to use new NBT format

# 2.2.1

- Adjust burning duration of Fireball and Scorch spells
- Adjust light output of spell projectiles

# 2.2.0

- Add new weapons, obtainable only as loot from Aether dungeons
  - Valkyrie Magister Staff

# 2.1.1

- Udpdate Russian translation, thanks to @Heimdallr
- Add Brazilian translation, thanks to @demorogabrtz

# 2.1.0

- Add new armor set: Netherite Arcane Robes
- Add new armor set: Netherite Fire Robes
- Add new armor set: Netherite Frost Robes
- Rebalance armor and weapon attributes
- Update spell projectile models and textures

# 2.0.2

- Slightly reduce Fireball knockback
- Update dependencies

# 2.0.1

- Update dependency declarations
- Allow running on 1.21

# 2.0.0

- Update to Minecraft 1.21.1
- Fire Staves and Wands now come with the basic Fireball spell
- Tome of Fire first spell to unlock is now Pyroblast
- Adjust balance of basic staff spells

# 1.3.0

- Increased velocity for tier 1 projectile spells

# 1.2.2

- Add enchantment condition tags

# 1.2.1

- Configurable item registration
- Reduce damage done by Wall of Fire by 20%
- Add French translation, thanks to valentin56610
- Update Fabric Loader to 15+ for embedded MixinExtras

# 1.2.0

- Migrate to new Spell Power Attributes API
- Rebalance secondary bonuses on specialized robes (swap Arcane-CritDmg and Frost-Haste)
- Reduce cooldown of Frost Nova
- Update `#wizards:wizard_robes` tag
- Update Italian translation, thanks to @Zano1999

# 1.1.1

- Arcane Missiles spell now shoots 2 projectiles, takes longer to cast
- Increase blast radius of Fireball by 25%
- Reduce damage done by Wall of Flames
- Reduce performance impact of Blizzard 

# 1.1.0

Arcane Wizard changes:
- Add new spell: Blink
  - Teleport the caster forwards by 15 blocks

Fire Wizard changes:
- Add new skill: Wall of Fire
  - Creates a wall of fire, dealing up to fire spell damage continuously to enemies passing thru
- Update Fire Breath visuals

Frost Wizard changes:
- Add new skill: Blizzard
  - Channels a rain of frost shards down onto your target and nearby enemies

Other changes:
- Staves and Wands now all come with tier 0 spell, all of them are now able to cast from spell books
- Retexture for Crystal Arcane Staff
- Add equipment tier item tags in `rpg_series` scope
- Add Russian translation, thanks to @SwayMini
- Add Spanish translation, thanks to @SirColor
- Update Italian translation, thanks to @Zano1999
- Remove loot config, replaced by `config/rpg_series/loot.json`

# 1.0.5

- Add compatibility for `c:wood_sticks` in recipes #16
- Fix `wizards:staves` tag when BetterX mods are not present 
- Update Chinese translation, thanks to Sillymoon

# 1.0.4

- Add `wizards:staves` and `wizards:wands` item tags, containing relevant items
- Add effect descriptions

# 1.0.3

- Update name for Mod Menu

# 1.0.2

- Limit Wizard Tower spawns to 1 per village (configurable), thanks to the Structure Pool API
- Fix minor structural issues with taiga and plains Wizard Towers
- Fix some default loot table configurations
- Fix some player skin cases clipping through armor
- Tweak villager trade offers
- Update to latest Spell Engine API
- Add italian translation, thanks to Zano1999
- Update Simplified Chinese translation, thanks to Sillymoon #26

# 1.0.1

- Update mod menu settings link

# 1.0.0

Now works with Minecraft Forge, via Connector.

- Migrate to Azure Armor Lib
- Reduce range of Scorch
- Fix default loot config

# 0.9.23

- Add quartz to Villager Wizard chest loot table
- Update several staff textures

# 0.9.22

- Add new early game weapon: Wizard Staff
- Add new texture for Novice Wand
- Add emissive textures for weapons
- Frostbolt slowness status effect emits snowflake particles
- Rebalance Arcane Staff, Fire Staff, Frost Staff durability and enchantability (now matching diamond tier)
- Fix luminance of spell projectiles and effects

# 0.9.21

- New Arcane Robe texture
- Optimize Wizard Robe armor model 

# 0.9.20

- Rebalance Arcane Missile: piercing through 2 targets
- Rebalance Fireball: dealing area damage on impact
- Rebalance Fire breath: increase breath angle
- Rebalance Meteor: Spawn 3 meteors instead of 1
- Rebalance Frostbolt: ricochet to 2 additional targets
- Rebalance Frost Nova: increase damage dealt
- Use new json API
- Fix warnings recipes with absent materials

# 0.9.19

- Fix netherite staff and wand upgrade recipes

# 0.9.18

- Support Minecraft 1.20.1

# 0.9.17

- Remove netherite weapon crafting recipes

# 0.9.16

- Add smithing table upgrade recipes to upgrade to netherite weapons
- Update default loot table configurations, add some enchanted loot

# 0.9.15

- Add Arcane, Fire and Frost spell books

# 0.9.14

- Restructure advancements
- Reduce cost of netherite staves based on community feedback
- Phase out deprecated API usage
- Slightly improve Frost Shield rendering

# 0.9.13

- Add configurable bonus roll chance
- Reduce default drop chance for Novice Wand

# 0.9.12

- Add Ukrainian translation, thanks to un_roman
- Update dependencies

# 0.9.11

- Update wand spell assignments data files to new format
- Move entry and config definitions of armors and weapons to Spell Engine

# 0.9.10

All spell bindings have been reset due to a major API change! We apologize for the inconvenience.

- Add spell pool data files
- Update spell assignment data files

# 0.9.9

- Add Arcane, Fire, Frost specific robes

# 0.9.8

- Add netherite staff and wand variants
- Add Better Nether, Better End specific staves
- Wizard robes repairable using wool
- Fix wizard robe clipping multi layer skins
- Improve loot configuration

# 0.9.7

- Update dependencies
- Fix wizard villager chest loot table
- Fix frost trap render while texture packs are used

# 0.9.6

- Fixate status effect raw ids
- Rework loot table injection, wizard items are now much less common
- Fix wizard hat front clipping

# 0.9.5

- Mod Menu settings opens Spell Engine settings
- Improve advancements root 
- Fix server launch crash alongside Wilder Wild
- Fix staff textures causing general grainy rendering
- Fix hat recipe reminiscence
- Fix Frost Wand texture

# 0.9.4

- Add Merchant Markers support
- Update world gen weight defaults
- Fix zombie wizard merchant texture

# 0.9.3

- Add advancements
- Add equipment attribute configuration
- Add vanilla loot tables with configuration
- Improve wizard robes

# 0.9.2

- Add custom model and texture for Meteor projectile
- Improve some wand textures
- Fine tune Meteor spell properties
- Fix Novice Wand attributes
- Fix Wizard Hat clipping and position

# 0.9.1

- Initial release