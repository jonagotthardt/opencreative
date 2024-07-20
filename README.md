# OpenCreative+

Minecraft plugin for PaperMC servers that allows players to create their worlds.

![logo](https://media.discordapp.net/attachments/1203026811647303721/1254848334208831508/image.png?ex=669c9a54&is=669b48d4&hm=f872bb68ed1cb64d2c077060178e2cbe29c52c0720494c1478662eb9a5d34d6e&=&format=webp&quality=lossless&width=756&height=452)

This plugin is:
- **Fast.** It uses RAM more often than hard drive.
- **Translatable.** You can change every single message.
- **Customizable.** Set world sizes and player's permissions.
- **Open-sourced.** See code, add features, test and commit it.
- **Free.** Download it without wasting any cent.

## Features

### Creating a world
Players can create their worlds using 4 different generators:
- Plains.
- Flat world.
- Ocean.
- Void.

You can change world's sizes with player's permissions in config.yml.

### Worlds Browser
Players can search, sort and like worlds. World can be found by name or ID. Also worlds list can be sorted by:
- Categories (Fighting, Sandbox, Arcade...)
- Amount of online players in world.
- Creation time.

### World's settings
Every owner of world can change in his world:
- Name
- Description
- Category
- ID
- Flags
- Icon Item

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

## License

This plugin is licensed under GNU GPL v3, because it uses Paper API. When you use or edit source code of this plugin, please open your source code.