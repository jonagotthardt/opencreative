<div align="center">

<img src="https://i.imgur.com/rcYzFd2.png" alt="drawing" width="600"/>

[![bStats Servers](https://img.shields.io/bstats/servers/22001?color=blue)](https://bstats.org/plugin/bukkit/OpenCreative/22001)
[![bStats Players](https://img.shields.io/bstats/players/22001?=blue)](https://bstats.org/plugin/bukkit/OpenCreative/22001)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Discord](https://img.shields.io/discord/1300864436076286069.svg?label=Discord&color=purple)](https://discord.com/invite/sSFCXUeq63)

</div>

# OpenCreative+

Minecraft plugin for PaperMC servers that allows players to create their worlds.

![Screenshot](https://i.imgur.com/4jzB4F9.png)

## Features

- **Let your players create entire worlds**, where they can build and even create mini-games with code.
- **World Generators**. Players can create flat, normal, ocean and large biomes worlds with normal, nether, the end environment.
- **World Settings**. World owners can change world's name, description, icon and other options. 
- **Translatable**. You can change every single message for your server, and some of them has PlaceholderAPI support.
- **Player Ranks**. Change world size, cooldowns, limits, modifiers, play/build/dev permissions for player groups.
- **Open-source**. You can download source code and create similar plugins with your own features.

## Usage

### Creating a world
Players can create their worlds using 4 different generators:
- Plains.
- Flat world.
- Ocean.
- Void.

You can change world's sizes with player's permissions in config.yml.

![](https://i.imgur.com/sID0dxv.png)

### Worlds Browser
Players can search, sort and like worlds. World can be found by name or ID. Also worlds list can be sorted by:
- Categories (Fighting, Sandbox, Arcade...)
- Amount of online players in world.
- Creation time.

![](https://i.imgur.com/mL6FS4o.png)

### World's settings
Every owner of world can change in his world:
- Name
- Description
- Category
- ID
- Flags
- Icon Item

![](https://i.imgur.com/iMJjFh0.png)

### Coding
Players can create code for their worlds with coding blocks and run it.

Coding blocks will be parsed into codeScript.yml.
Example: Send a message "Hello world!"

```yaml
code:
  blocks:
    exec_block_4_1:
      category: EVENT_PLAYER
      type: PLAYER_JOIN
      actions:
        action_block1:
          category: PLAYER_ACTION
          type: PLAYER_SEND_MESSAGE
          arguments:
            type:
              type: TEXT
              value: join-spaces
            messages:
              type: LIST
              value:
                '1':
                  type: TEXT
                  value:
                    name: Hello
                '2':
                  type: TEXT
                  value:
                    name: World
```

## Commands

### `/creative`
Without arguments opens a menu with information about OpenCreative+.
- `reload` - reloads configuration and localization files.
- `resetlocale` - resets localization file to default from plugin.
- `load [world id]` - loads specified world.
- `unload [world id]` - unloads specified world.
- `list` - displays list of loaded worlds on server.
- `deprecated` - displays list of deprecated worlds, whose owner haven't logged in for a month.
- `corrupted` - displays list of corrupted worlds, that lost settings.yml.
- `maintenance start [seconds]` - start maintenance mode in next [seconds], that disallows players without permissions to create or join worlds.
- `maintenance stop` - stop maintenance mode, players will be able to create and join worlds.
- `creative-chat clear` - clears chat for every player. Useful to hide some inappropriate messages.
- `creative-chat disable` - disables creative chat for players, that don't have bypass permission, they can't send messages in /cc chat.
- `creative-chat enable` - enables creative chat for players, they will be able to speak in creative chat again.
- `kick-all starts [nickname part]` - kicks players with nicknames that start with part. Useful for kicking bots.
- `kick-all ends [nickname part]` - kicks players with nicknames that end with part. Useful for kicking bots.
- `kick-all contains [nickname part]` - kicks players with nicknames that contain part. Useful for kicking bots.
- `kick-all ignore [nickname] [nickname2]...` - kicks everyone except command sender and specified players.

### `/cc` - Creative Chat
Creative chat is global chat where players can send messages and speak with everyone.
- `[message]` - send messages. It supports colors!
- `off` - hides creative chat messages only for command sender.
- `on` - shows creative chat messages for command sender.

### `/world`
- `info` - displays information about world, where command sender currently is in.
- `delete` - deletes world.
- `deletemobs` - opens a menu where owner can delete mobs from world.

### `/ad`
Without arguments advertises world, where command sender currently is in. World advertisement is a clickable message with world information that will be sent to every player.
- `[world id]` - teleports command sender to world.

### `/play`
Changes world's mode to Play mode.

### `/build`
Changes world's mode to Build mode.

### `/dev`
Teleports command sender to developer world.

### `/like`
Increases reputation of world, where command sender currently is in.

### `/dislike`
Decreases reputation of world, where command sender currently is in.

### `/join`
- `[world id]` - teleports command sender to world.

### `/locate`
- `[player name]` - locates in which world player currently plays.

### `/spawn`
Without arguments teleports command sender to spawn world.
- `[player name]` - teleports specified player to spawn world.

### `/environment`
- `variables list` - shows list of all variables in world.
- `variables size` - outputs amount of all variables in world.
- `variables clear` - clears all variables.
- `debug enable` - enables debug mode for world. It sends messages about events and action arguments.
- `debug disable` - disables debug mode for world.

### Minecraft commands
These commands overwrite minecraft and EssentialsX commands.
They will work only if command sender is owner of world, or if he has bypass permission. Using commands from console will redirect to default /minecraft:command.

- `/gamemode [player name] [game mode]` - changes player's game mode.
- `/gamemode [game mode]` - changes player's game mode.
- `/teleport [x] [y] [z] [yaw] [pitch]` - teleports sender to specified coordinates.
- `/teleport [player name] [x] [y] [z] [yaw] [pitch]` - teleports specified player to specified coordinates.
- `/give [player name] [item material] [amount]` - gives item to specified player.
- `/playsound [player name] [sound name] [volume] [pitch]` - plays a sound for player.
- `/stopsound [player name] [sound category]` - stops a playing sounds for player.
- `/weather [rain/thunder/sun]` - changes weather in world.
- `/time [set/add] [ticks]` - changes time in world.


## Development

To develop this plugin, simply download this project and use Intellij IDEA to code.

We use Maven to compile plugin into jar file.

Contribute your code into [`development branch`](https://gitlab.com/eagles-creative/opencreative/-/tree/development), then we will check it and we create merge request to the Main branch.

## Statistic

[<img src="https://bstats.org/signatures/bukkit/OpenCreative.svg" height="250" />](https://bstats.org/plugin/bukkit/OpenCreative/22001)

## License

This plugin is licensed under GNU GPL v3, because it uses Paper API. When you use or edit source code of this plugin, please open your source code.