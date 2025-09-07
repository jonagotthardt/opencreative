/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2025, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.coding.prompters;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ArgumentSlot;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ParameterSlot;
import ua.mcchickenstudio.opencreative.coding.values.*;

import java.util.StringJoiner;

public final class PrompterInstruction {

    private final String input;

    public PrompterInstruction(@NotNull String playerRequest) {
        this.input = playerRequest;
    }

    public @NotNull String get() {
        return """
        You're coding writer, and user asked you to write a code:
        \"""" + input + "\"" + """
        YOU MUST RETURN ONLY YAML CODE.
        DO NOT WRITE COMMENTS, BECAUSE THEY WILL BE IGNORED.
       \s
        Example of code, that sends "Hello World" message when player joins the world.
        code:
          blocks:
            exec_block_1:
              category: EVENT_PLAYER
              type: PLAYER_JOIN
              actions:
                action_block2:
                  category: PLAYER_ACTION
                  type: PLAYER_SEND_MESSAGE
                  arguments:
                    messages:
                      type: LIST
                      value:
                        '1':
                          type: TEXT
                          value: Hello
                        '2':
                          type: TEXT
                          value: World
                    type:
                      type: TEXT
                      value: join-spaces
       \s
        Example of code, that gives random item to player if he destroys magma block.
        exec_block_1:
          category: EVENT_PLAYER
          type: PLAYER_DESTROY_BLOCK
          actions:
            condition_block_1:
              category: PLAYER_CONDITION
              type: IF_PLAYER_BLOCK_EQUALS
              arguments:
                blocks:
                  type: LIST
                  value:
                    '1':
                      type: ITEM
                      value:
                        v: 4082
                        type: MAGMA_BLOCK
              actions:
                action_block1:
                  category: PLAYER_ACTION
                  type: PLAYER_GIVE_RANDOM_ITEM
                  arguments:
                    items:
                      type: LIST
                      value:
                        '1':
                          type: ITEM
                          value:
                            v: 4082
                            type: DIAMOND_SWORD
                        '2':
                          type: ITEM
                          value:
                            v: 4082
                            type: PUFFERFISH
                        '3':
                          type: ITEM
                          value:
                            v: 4082
                            type: APPLE
                            amount: 4
                        '4':
                          type: ITEM
                          value:
                            v: 4082
                            type: SPRUCE_LOG
                            amount: 2
                        '5':
                          type: ITEM
                          value:
                            v: 4082
                            type: DIAMOND
        \s
        Example of code, that sends message to all players on player quit.
        exec_block_1:
          category: EVENT_PLAYER
          type: PLAYER_QUIT
          actions:
            action_block2:
              category: SELECTION_ACTION
              type: SELECTION_SET
              target: ALL_PLAYERS
              arguments: {}
            action_block3:
              category: PLAYER_ACTION
              type: PLAYER_SEND_MESSAGE
              target: SELECTED
              arguments:
                messages:
                  type: LIST
                  value:
                    '1':
                      type: TEXT
                      value: '%player% left the world'
                type:
                  type: TEXT
                  value: new-line\s
         \s
         Example of code, that gives 10 money to player for killing a mob
         code:
            blocks:
              exec_block_1:
                category: EVENT_PLAYER
                type: PLAYER_KILLED_MOB
                actions:
                  action_block2:
                    category: VARIABLE_ACTION
                    type: VAR_SUM_ASSIGN_NUMBER
                    arguments:
                      variable:
                        type: VARIABLE
                        value:
                          name: '%player%_money'
                          type: SAVED
                      number:
                        type: NUMBER
                        value: 10.0
                  action_block3:
                    category: VARIABLE_ACTION
                    type: VAR_ROUND_NUMBER
                    arguments:
                      variable:
                        type: VARIABLE
                        value:
                          name: displaymoney
                          type: LOCAL
                      number:
                        type: VARIABLE
                        value:
                          name: '%player%_money'
                          type: SAVED
                      type:
                        type: TEXT
                        value: ceil
                  action_block4:
                    category: PLAYER_ACTION
                    type: PLAYER_SEND_MESSAGE
                    arguments:
                      messages:
                        type: LIST
                        value:
                          '1':
                            type: TEXT
                            value: You got §e
                          '2':
                            type: VARIABLE
                            value:
                              name: displaymoney
                              type: LOCAL
                          '3':
                            type: TEXT
                            value: §f$ for killing %victim%
                      type:
                        type: TEXT
                        value: join
       \s
        Saving executor format
       \s
        code:
          blocks:
            # Player, Entity, World events
            exec_block_1:
              category: EXECUTOR_CATEGORY # EVENT_PLAYER, EVENT_ENTITY, EVENT_WORLD
              type: EXECUTOR_TYPE # PLAYER_JOIN, ENTITY_SPAWNED, WORLD_BLOCK_BURNED, and others...
            # Function, Method
            exec_block_2:
              category: EXECUTOR_CATEGORY # FUNCTION, METHOD
              type: EXECUTOR_TYPE # FUNCTION, METHOD
              name: "Name to execute" # Function's or method's name
            # Cycle
            exec_block_3:
              category: CYCLE
              type: CYCLE
              name: "Name to execute" # Cycle's name
              time: 20 # Execute code every ticks
       \s
        Saving action format
        MAXIMUM AMOUNT OF ACTIONS PER EXECUTOR IS 45.
       \s
        code:
          blocks:
            exec_block_number:
              # ...
              actions:
                # Player, Entity, World, Control, Variable actions
                action_blockNUMBER:
                  category: ACTION_CATEGORY # PLAYER_ACTION, ENTITY_ACTION, WORLD_ACTION, CONTROL_ACTION, VARIABLE_ACTION
                  type: ACTION_TYPE # PLAYER_SEND_MESSAGE, ENTITY_REMOVE, WORLD_SET_BLOCK_TYPE, and others...
                  target: TARGET_TYPE # SELECTED, ALL_PLAYERS, ALL_ENTITIES, RANDOM_PLAYER, RANDOM_TARGET, KILLER, VICTIM. - We don't save DEFAULT to save more space.
       \s
                # Selection action (Target)
                action_block2:
                  category: SELECTION_ACTION
                  type: ACTION_TYPE # SELECTION_SET, SELECTION_ADD, SELECTION_REMOVE
                  target: TARGET_TYPE
                # Selection action (Condition)
                action_block3:
                  category: SELECTION_ACTION
                  type: ACTION_TYPE # SELECTION_SET, SELECTION_ADD, SELECTION_REMOVE
                  condition:
                    category: CONDITION_CATEGORY
                    type: CONDITION_TYPE
        code:     \s
          blocks:
            exec_block_1:
              # ...
              actions:
                # Launch Function, Launch Method actions
                action_block4:
                  category: SELECTION_ACTION # LAUNCH_FUNCTION_ACTION, LAUNCH_METHOD_ACTION
                  type: ACTION_TYPE # LAUNCH_FUNCTION, LAUNCH_METHOD
                  target: TARGET_TYPE
                  name: "Name to execute" # Function's or method's name
       \s
        Targets
        In actions and conditions use targets only:
        - If it's kill event executor: VICTIM, KILLER
        - If code requires SELECTION_ACTION then use SELECTED
        - If user requires SELECTION_ACTION and asked from random then use RANDOM_TARGET
        - If user asked from random then use RANDOM_PLAYER
        - If user asked for all players/entities use ALL_PLAYERS or ALL_ENTITIES
        In ALL OTHER CASES USE target: DEFAULT
       \s
        Saving arguments format
        MAXIMUM AMOUNT OF ARGUMENTS PER ACTION IS 27.
       \s
      code:
        blocks:
          exec_block_1:
            # ...
            actions:
              action_block5:
                # ...
                arguments:
                  id-of-argument:
                    type: VALUE_TYPE # TEXT, NUMBER, ITEM, LOCATION, PARTICLE, VARIABLE, LIST, BOOLEAN, VECTOR, COLOR, EVENT_VALUE
                    value: value of argument # String, String List, MapValues with type: POTION, ANY will be saved as ITEM.# Examples
                  text-value:
                    type: TEXT\s
                    value: &aHello World
                  number-value:
                    type: NUMBER
                    value: 2.0\s
                  location-value:
                    type: LOCATION
                    value:
                      x: 1
                      y: 2
                      z: 3
                      yaw: 20
                      pitch: 25
                  vector-value:
                    type: VECTOR
                    value:
                      x: 1
                      y: 2
                      z: 3
                  variable-value:
                    type: VARIABLE
                    value:
                      name: Variable Name
                      type: VARIABLE_TYPE # LOCAL, GLOBAL, SAVED
                  event-value:
                    type: EVENT_VALUE
                    value:\s
                      name: EVENT_VALUE_ID
                      target: TARGET_TYPE # DEFAULT, SELECTED, RANDOM_PLAYER, RANDOM_TARGET, KILLER, VICTIM
                  boolean-value:
                    type: BOOLEAN
                    value: true # true, false
                  color-value:
                    type: COLOR
                    value:
                      red: 0
                      green: 50
                      blue: 255
                  particle-value:
                    type: PARTICLE
                    value:
                      type: PARTICLE_TYPE
                  item-value:
                    type: ITEM
                    value:
                      # Item is saved by Minecraft's serialize
                      v: 4189
                      type: DIAMOND_SWORD
                      meta:
                        ==: ItemMeta
                        meta-type: UNSPECIFIC
                        display-name: '{"text":"","extra":["","Display Name Example"],"italic":false}'
                        lore:
                        - '{"text":"","extra":["",{"text":"Lore Example","color":"dark_gray"},{"text":"  ","bold":true,"color":"dark_gray"}],"italic":false,"color":"white"}'
                        enchants:
                          minecraft:aqua_affinity: 1
                        attribute-modifiers: {}
                        ItemFlags:
                        - HIDE_ENCHANT
       \s
        List of available executors:
       \s""" + getExecutors() + "\n \n List of available actions and conditions: " + "\n" + getActions()
                + "\n" + "List of available Game Values: " + getGameValues();
    }

    private @NotNull String getExecutors() {
        StringJoiner joiner = new StringJoiner(", ");
        for (ExecutorType type : ExecutorType.values()) {
            if (type.isDisabled()) continue;
            joiner.add(type.name());
        }
        return joiner.toString();
    }

    private @NotNull String getGameValues() {
        StringJoiner joiner = new StringJoiner(", ");
        for (EventValue value : EventValues.getInstance().getEventValues()) {
            joiner.add(value.getID().toUpperCase() + "(" + getGameValueType(value) + ")");
        }
        return joiner.toString();
    }

    private @NotNull String getGameValueType(@NotNull EventValue eventValue) {
        return switch (eventValue) {
            case NumberEventValue ignored -> "NUMBER";
            case VectorEventValue ignored -> "VECTOR";
            case BooleanEventValue ignored -> "BOOLEAN";
            case ListEventValue ignored -> "LIST";
            case ItemEventValue ignored -> "ITEM";
            case LocationEventValue ignored -> "LOCATION";
            default -> "TEXT";
        };
    }

    private @NotNull String getActions() {
        StringJoiner joiner = new StringJoiner("\n");
        for (ActionType type : ActionType.values()) {
            if (type.isDisabled()) continue;
            joiner.add(type.name() + " " + getActionArguments(type));
        }
        return joiner.toString();
    }

    private @NotNull String getActionArguments(@NotNull ActionType type) {
        ArgumentSlot[] args = type.getArgumentsSlots();
        if (args == null) return "";
        if (args.length == 0) return "";
        StringJoiner joiner = new StringJoiner(", ");
        for (ArgumentSlot arg : args) {
            joiner.add(getArgument(arg));
        }
        return joiner.toString();
    }

    private @NotNull String getArgument(@NotNull ArgumentSlot argument) {
        // SLOT (NUMBER)
        // SAVE (BOOLEAN)
        // HAND (PARAMETER: main, off)
        // ITEMS (ITEM LIST OF 27)
        StringBuilder builder = new StringBuilder();
        builder.append(argument.getPath());
        builder.append(" (");
        if (argument instanceof ParameterSlot parameter) {
            builder.append("PARAMETER: ");
            builder.append(getParameterChoices(parameter));
            builder.append(")");
        } else if (argument.isList()) {
            builder.append(argument.getVarType().name());
            builder.append(" LIST OF ");
            builder.append(argument.getListSize());
            builder.append(")");
        } else {
            builder.append(argument.getVarType().name());
            builder.append(")");
        }
        return builder.toString();
    }

    private @NotNull String getParameterChoices(@NotNull ParameterSlot parameter) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Object value : parameter.getValues()) {
            joiner.add(String.valueOf(value));
        }
        return joiner.toString();
    }

}
