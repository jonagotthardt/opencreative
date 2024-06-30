# OpenCreative+

Minecraft plugin for PaperMC servers that allows players to create their worlds.

![logo](https://media.discordapp.net/attachments/1203026811647303721/1254848334208831508/image.png?ex=66823c54&is=6680ead4&hm=0d812c0552f6cd2ce6d31f56f87d21e8c8480eda208a9a472986c11d8583247b&=&format=webp&quality=lossless&width=756&height=452)

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

## License

This plugin is licensed under GNU GPL v3, because it uses Paper API. When you use or edit source code of this plugin, please open your source code.