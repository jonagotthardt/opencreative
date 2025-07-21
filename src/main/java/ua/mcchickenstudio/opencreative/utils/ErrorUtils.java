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

package ua.mcchickenstudio.opencreative.utils;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Argument;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.events.planet.PlanetModeChangeEvent;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.planets.Planet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;

/**
 * <h1>ErrorUtils</h1>
 * This class contains utilities for handling exceptions
 * and logging them in console with friendly look.
 */
public class ErrorUtils {

    private static String cutClassesName(String text) {
        String newText = text == null ? "Error message is not available" : text;
        newText = newText.replace("ua.mcchickenstudio.opencreative.coding.","");
        newText = newText.replace("ua.mcchickenstudio.opencreative.","");
        newText = newText.replace("org.bukkit.","");
        newText = newText.replace("io.papermc.","");
        newText = newText.replace("com.destroystokyo.","");
        newText = newText.replace("java.lang.","java.");
        newText = newText.replace("blocks.","");
        return newText;
    }

    public static String parseException(Exception error, boolean colored) {
        Set<String> lastStacks = new HashSet<>();
        byte i = 0;
        for (StackTraceElement stackTraceElement : error.getStackTrace()) {
            String stack = stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
            stack = cutClassesName(stack);
            lastStacks.add((colored ? "§c" : "") + stack);
            i++;
            if (i == 15) {
                break;
            }
        }
        return "\n" +
                (colored ? "§c" : "") +
                "☹ Exception has occurred..." +
                "\n" +
                (!colored ?
                """
                \\|/ _____ \\|/
                "@'/ . . \\`@"\s""" + OpenCreative.getVersion() + """
                \n/_| \\___/ |_\\\s""" + getRandomPhrase() + """
                \n   \\___U_/
                """ : "") +
                (colored ? "§4 " : " ") +
                error.getClass().getSimpleName() +
                ": " +
                cutClassesName(error.getMessage()) +
                "\n \n" +
                String.join("\n", lastStacks);
    }

    private static String getRandomPhrase() {
        String[] phrases = new String[]{
                "Things aren't so different..", "Seems like we messed up..",
                "We never gonna give console up..", "We'll meet again, some sunny day..",
                "We're fine, We won't lose our mind..", "Oops!", "Kernel Panic.. are we kernel?",
                "We'll see.. if you will report it.", "It is fine..", "I don't like errors, okay?",
                "At least not memory leak, yes?", "Don't be mad, be happy that something works.",
                "Totally OpenCreative+.", "My final message - goodbye!", "And here's we hanging out.",
                "This plugin ate a sparc! Gah!", "I blame PEOPLE BELOW for this.", "Well, it's possible. <-- (our catchphrase)",
                "Bug after bug, after bug, it never ends.", "I'll get out of this problem tonight.",
                "Try not to think about how long bug was here.", "You'll get one bug instead of zero.",
                "Totally powered by Java.", "Totally Minecraft plugin.", "Can someone just show me the fix?",
                "Errors in the plugin that never ends", "It might just work if we can try not to go insane",
                "Try not to panic when you see this", "This is not supposed to be here",
                ":( Your PLUGIN ran into a problem.", "Why is this happening?", "Dum dum, this sucks..",
                "Error the Troublemaker..", "Knocks off worlds like a terminator", "Gotta hate it cause' you just can't like it",
                "We had something to learn from that experience"
        };
        return phrases[new Random().nextInt(phrases.length)];
    }

    /**
     Sends error message to player.
     **/
    public static void sendPlayerErrorMessage(Player player, String errorMessage) {
        OpenCreative.getPlugin().getLogger().warning("An player error has occurred for " + player.getName() + ": " + errorMessage);
        player.sendMessage(getLocaleMessage("player-error").replace("%error%",errorMessage));
        Sounds.PLAYER_ERROR.play(player);
    }

    /**
     Sends error message to player.
     **/
    public static void sendPlayerErrorMessage(Player player, String errorMessage, Exception error) {
        if (OpenCreative.getSettings().isConsoleWarnings()) OpenCreative.getPlugin().getLogger().warning("An player error has occurred for " + player.getName() + ": " + errorMessage + " " + parseException(error,false));
        Component message = Component
                .text(getLocaleMessage("player-error").replace("%error%",errorMessage))
                .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(parseException(error,true))));
        player.sendMessage(message);
        Sounds.PLAYER_ERROR.play(player);
    }

    /**
     Sends error message for planet's players.
     **/
    public static void sendPlanetErrorMessage(Planet planet, String errorMessage) {
        if (OpenCreative.getSettings().isConsoleWarnings()) OpenCreative.getPlugin().getLogger().warning("An error has occurred in planet " + planet.getWorldName() + ": " + errorMessage);
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("planet-error").replace("%error%",errorMessage));
            Sounds.PLAYER_ERROR.play(player);
        }
    }

    /**
     Sends error message for planet's players.
     **/
    public static void sendPlanetErrorMessage(Planet planet, String errorMessage, Exception error) {
        if (OpenCreative.getSettings().isConsoleWarnings()) OpenCreative.getPlugin().getLogger().warning("An error has occurred in planet " + planet.getWorldName() + ": " + errorMessage + " " + parseException(error,false));
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("planet-error").replace("%error%",errorMessage))
                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(parseException(error,true))));
            player.sendMessage(message);
            Sounds.PLAYER_ERROR.play(player);
        }
    }

    /**
     Sends error message about planet's code exception on running Action for planet's players.
     **/
    public static void sendPlanetCodeWarningMessage(Executor executor, Action action, String warningMessage) {
        Planet planet = executor.getPlanet();
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("planet-code-warning.message")
                            .replace("%event%", executor.getExecutorType().getLocaleName())
                            .replace("%action%",action.getActionType().getLocaleName())
                            .replace("%warning%",warningMessage)
                            .replace("%x%",String.valueOf(action.getX()))
                            .replace("%y%",String.valueOf(executor.getY()))
                            .replace("%z%",String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + action.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
            Sounds.WORLD_CODE_ERROR.play(player);
        }
    }

    /**
     Sends error message about planet's code exception on running Action for planet's players.
     **/
    public static void sendPlanetCodeErrorMessage(Executor executor, Action action, String errorMessage, Exception error) {
        Planet planet = executor.getPlanet();
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("coding-error.message")
                            .replace("%event%", executor.getExecutorType().getLocaleName())
                            .replace("%action%",action.getActionType().getLocaleName())
                            .replace("%error%",errorMessage)
                            .replace("%x%",String.valueOf(action.getX()))
                            .replace("%y%",String.valueOf(1))
                            .replace("%z%",String.valueOf(action.getExecutor().getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message") + "\n" + parseException(error,true))))
                    .clickEvent(ClickEvent.runCommand("/dev " + action.getX() + " " + 1 + " " + action.getExecutor().getZ()));
            player.sendMessage(message);
            Sounds.WORLD_CODE_ERROR.play(player);
        }
    }

    /**
     Sends error message about planet's code exception on running Action for planet's players.
     **/
    public static void sendPlanetCodeErrorMessage(Executor executor, Action action, Entity entity, String errorMessage) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(entity.getWorld());
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-error.message").replace("%event%",executor.getExecutorType().getLocaleName()).replace("%action%", action.getActionType().toString()).replace("%error%",errorMessage).replace("%x%",String.valueOf(action.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
            Sounds.WORLD_CODE_ERROR.play(player);
        }
    }

    /**
     Sends error message about planet's code exception on executing Executor for planet's players.
     **/
    public static void sendPlanetCodeCriticalErrorMessage(Planet planet, Executor executor, String errorMessage) {
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            Sounds.WORLD_CODE_CRITICAL_ERROR.play(player);
            Component message = Component
                    .text(getLocaleMessage("coding-error.message-event-critical")
                            .replace("%event%", executor.getExecutorType().getLocaleName())
                            .replace("%error%",errorMessage)
                            .replace("%x%",String.valueOf(executor.getX()))
                            .replace("%y%",String.valueOf(executor.getY()))
                            .replace("%z%",String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + executor.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
        }
    }

    /**
     Sends error message about planet's code exception on executing Executor for planet's players.
     **/
    public static void sendPlanetCodeErrorMessage(Planet planet, Executor executor, String errorMessage) {
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("coding-error.message-event")
                            .replace("%event%", executor.getExecutorType().getLocaleName())
                            .replace("%error%",errorMessage)
                            .replace("%x%",String.valueOf(executor.getX()))
                            .replace("%y%",String.valueOf(executor.getY()))
                            .replace("%z%",String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + executor.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
            Sounds.WORLD_CODE_ERROR.play(player);
        }
    }

    /**
     Sends error message about planet's code exception on executing Executor for planet's players.
     **/
    public static void sendPlanetCompileErrorMessage(Planet planet, Block block, String errorMessage) {
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("coding-error.message-compile")
                            .replace("%error%",errorMessage)
                            .replace("%x%",String.valueOf(block.getX()))
                            .replace("%y%",String.valueOf(block.getY()))
                            .replace("%z%",String.valueOf(block.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + block.getX() + " " + block.getY() + " " + block.getZ()));
            player.sendMessage(message);
            Sounds.WORLD_CODE_COMPILE_ERROR.play(player);
        }
    }

    /**
     Sends error message about planet's code exception on compiling unknown blocks
     **/
    public static void sendPlanetCompileErrorMessage(Planet planet, List<Block> unknownBlocks) {
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-error.unknown-block-detected").replace("%error%",getLocaleMessage("coding-error.unknown-blocks",false)));
            for (Block block : unknownBlocks) {
                NamedTextColor color = NamedTextColor.GRAY;
                String category = "???";
                String type = getSignLine(block.getLocation(),(byte) 3);
                if (type == null || type.isEmpty()) type = "???";
                ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(block.getType());
                ActionCategory actionCategory = ActionCategory.getByMaterial(block.getType());

                if (executorCategory != null) {
                    color = executorCategory.getColor();
                    category = executorCategory.getLocaleName();
                } else if (actionCategory != null) {
                    color = actionCategory.getColor();
                    category = actionCategory.getLocaleName();
                }

                Component blockCoordinatesMessage = Component
                        .text(getLocaleMessage("coding-error.unknown-block-coords")
                            .replace("%x%", String.valueOf(block.getLocation().getX()))
                            .replace("%y%", String.valueOf(block.getLocation().getY()))
                            .replace("%z%", String.valueOf(block.getLocation().getZ()))
                            .replace("%category%",category)
                            .replace("%type%",type))
                        .color(color)
                        .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                        .clickEvent(ClickEvent.runCommand("/dev " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ()));
                player.sendMessage(blockCoordinatesMessage);
            }
            Sounds.WORLD_CODE_COMPILE_ERROR.play(player);
            player.sendMessage(" ");
        }
    }

    /**
     Stops planet's code execution and changes planet's mode to BUILD.
     **/
    public static void stopPlanetCode(Planet planet) {
        OpenCreative.getPlugin().getLogger().info("Planet code has been stopped in " + planet.getWorldName() + " because of operations limit.");
        if (planet.getMode() != Planet.Mode.BUILD) {
            PlanetModeChangeEvent event = new PlanetModeChangeEvent(planet, planet.getMode(), Planet.Mode.BUILD);
            event.callEvent();
            if (!event.isCancelled()) {
                planet.setMode(Planet.Mode.BUILD);
            }
        }
    }

    /**
     Sends warning message about problem with plugin.
     **/
    public static void sendWarningErrorMessage(String errorMessage) {
        if (OpenCreative.getSettings().isConsoleWarnings()) OpenCreative.getPlugin().getLogger().warning("Warning! An error has occured: " + errorMessage);
    }

    /**
     Sends warning message about problem with plugin.
     **/
    public static void sendWarningMessage(String errorMessage, Exception error) {
        if (OpenCreative.getSettings().isConsoleWarnings()) OpenCreative.getPlugin().getLogger().warning("Warning! " + errorMessage + " " + parseException(error,false));
    }

    /**
     Sends critical error message about problem with plugin.
     **/
    public static void sendCriticalErrorMessage(String errorMessage) {
        if (OpenCreative.getSettings().isConsoleCriticalErrors()) OpenCreative.getPlugin().getLogger().severe("CRITICAL ERROR has occured: " + errorMessage);
    }

    /**
     Sends critical error message about problem with plugin.
     **/
    public static void sendCriticalErrorMessage(String errorMessage, Exception error) {
        if (OpenCreative.getSettings().isConsoleCriticalErrors()) OpenCreative.getPlugin().getLogger().severe("CRITICAL ERROR has occurred: " + errorMessage + " " + parseException(error,false));
    }

    public static void sendDebug(String message) {
        if (OpenCreative.getSettings().isDebug()) {
            OpenCreative.getPlugin().getLogger().info("[DEBUG] " + message);
        }
    }

    public static void sendDebugError(String errorMessage, Exception error) {
        if (OpenCreative.getSettings().isDebug()) {
            OpenCreative.getPlugin().getLogger().severe("CRITICAL ERROR has occurred: " + errorMessage + " " + parseException(error,false));
        }
    }

    public static void sendCodingDebugNotFoundVariable(Planet planet, String name) {
        if (true) {
            return;
        }
        if (!planet.isDebug()) return;
        Object value = null;
        if (value == null) value = "null";
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-debug.variable-not-found",false).replace("%name%",name).replace("%value%", value.toString()));
        }
    }

    public static void sendCodingNotFoundEventValue(Planet planet, Executor executor, Class<? extends EventValue> clazz) {
        if (planet == null) return;
        EventValue eventValue = EventValues.getInstance().getByClass(clazz);
        sendPlanetCodeErrorMessage(planet,executor, getLocaleMessage("coding-error.temp-var-not-exists",false)
                .replace("%variable%", eventValue != null ? eventValue.getLocaleName() : clazz.getSimpleName()));
    }

    public static void sendCodingDebugLog(Planet planet, String log) {
        if (!planet.isDebug()) return;
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-debug.log",false).replace("%log%",log));
        }
    }

    public static void sendCodingDebugVariable(Planet planet, String name, Object value) {
        if (true) {
            return;
        }
        if (!planet.isDebug()) return;
        if (value == null) value = "null";
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-debug.variable-found",false).replace("%name%",name).replace("%value%",value.toString()));
        }
    }

    public static void sendCodingDebugExecutor(Executor executor) {
        Planet planet = executor.getPlanet();
        if (planet == null || !planet.isDebug()) return;
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-debug.executor-message",false).replace("%type%",executor.getExecutorType().getLocaleName()).replace("%x%",String.valueOf(executor.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
        }
    }

    public static void sendCodingDebugAction(Action action) {
        if (action.getExecutor() == null) return;
        Planet planet = action.getExecutor().getPlanet();
        if (planet == null || !planet.isDebug()) return;
        List<Argument> arguments = action.getArgumentsList();
        String message = getLocaleMessage("coding-debug.hover." + (action.getActionType().isCondition() ? "condition" : "action"));
        message = message.replace("%category%",action.getActionCategory().getLocaleName());
        message = message.replace("%type%",action.getActionType().getLocaleName());
        if (action instanceof Condition condition) {
            message = message.replace("%opposed%",getLocaleMessage("coding-debug.condition.opposed." + condition.isOpposed()));
        }
        List<String> argumentsString = new ArrayList<>();
        for (Argument arg : arguments) {
            argumentsString.add(getLocaleMessage("coding-debug.hover.argument").replace("%name%",arg.getPath()).replace("%type%",arg.getType().getLocaleName()).replace("%value%",arg.getValue(action).toString().substring(0, Math.min(30, arg.getValue(action).toString().length()))));
        }
        message = message.replace("%arguments%",String.join(" \n",argumentsString));
        String actionMessage = getLocaleMessage("coding-debug.action-message",false).replace("%type%",action.getActionType().getLocaleName()).replace("%x%",String.valueOf(action.getX())).replace("%y%",String.valueOf(action.getExecutor().getY())).replace("%z%",String.valueOf(action.getExecutor().getZ()));
        for (Player player : planet.getPlayers()) {
            player.sendMessage(Component.text(actionMessage).hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(message))));
        }
    }

}
