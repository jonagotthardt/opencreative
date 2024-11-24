/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
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
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.events.plot.PlotModeChangeEvent;
import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;

public class ErrorUtils {

    private static String cutClassesName(String text) {
        String newText = text == null ? "null" : text;
        newText = newText.replace("ua.mcchickenstudio.opencreative.coding.","");
        newText = newText.replace("ua.mcchickenstudio.opencreative.","");
        newText = newText.replace("org.bukkit.","");
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
            lastStacks.add("§c" + stack);
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
                "@'/ . . \\`@"
                /_| \\___/ |_\\
                   \\___U_/""" : "") +
                (colored ? "§4 " : " ") +
                error.getClass().getSimpleName() +
                ": " +
                cutClassesName(error.getMessage()) +
                "\n \n" +
                String.join("\n", lastStacks);
    }

    /**
     Sends error message to player.
     **/
    public static void sendPlayerErrorMessage(Player player, String errorMessage) {
        OpenCreative.getPlugin().getLogger().warning("An player error has occurred for " + player.getName() + ": " + errorMessage);
        player.sendMessage(getLocaleMessage("player-error").replace("%error%",errorMessage));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK,100f,0.1f);
    }

    /**
     Sends error message to player.
     **/
    public static void sendPlayerErrorMessage(Player player, String errorMessage, Exception error) {
        OpenCreative.getPlugin().getLogger().warning("An player error has occurred for " + player.getName() + ": " + errorMessage + " " + parseException(error,false));
        Component message = Component
                .text(getLocaleMessage("player-error").replace("%error%",errorMessage))
                .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(parseException(error,true))));
        player.sendMessage(message);
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK,100f,0.1f);
    }

    /**
     Sends error message for plot's players.
     **/
    public static void sendPlotErrorMessage(Plot plot, String errorMessage) {
        OpenCreative.getPlugin().getLogger().warning("An error has occurred in plot " + plot.getWorldName() + ": " + errorMessage);
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-error").replace("%error%",errorMessage));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY,100f,2f);
        }
    }

    /**
     Sends error message about plot's code exception on running Action for plot's players.
     **/
    public static void sendPlotCodeWarningMessage(Executor executor, Action action, String warningMessage) {
        Plot plot = executor.getPlot();
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("plot-code-warning.message")
                            .replace("%event%", executor.getExecutorType().getLocaleName())
                            .replace("%action%",action.getActionType().getLocaleName())
                            .replace("%warning%",warningMessage)
                            .replace("%x%",String.valueOf(action.getX()))
                            .replace("%y%",String.valueOf(executor.getY()))
                            .replace("%z%",String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("plot-code-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + action.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,100,1.7f);
        }
    }

    /**
     Sends error message about plot's code exception on running Action for plot's players.
     **/
    public static void sendPlotCodeErrorMessage(Executor executor, Action action, String errorMessage, Exception error) {
        Plot plot = executor.getPlot();
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("plot-code-error.message")
                            .replace("%event%", executor.getExecutorType().getLocaleName())
                            .replace("%action%",action.getActionType().getLocaleName())
                            .replace("%error%",errorMessage)
                            .replace("%x%",String.valueOf(action.getX()))
                            .replace("%y%",String.valueOf(executor.getY()))
                            .replace("%z%",String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("plot-code-error.hover-message") + "\n" + parseException(error,false))))
                    .clickEvent(ClickEvent.runCommand("/dev " + action.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,100,1.7f);
        }
    }

    /**
     Sends error message about plot's code exception on running Action for plot's players.
     **/
    public static void sendPlotCodeErrorMessage(Executor executor, Action action, Entity entity, String errorMessage) {
        Plot plot = PlotManager.getInstance().getPlotByWorld(entity.getWorld());
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-error.message").replace("%event%",executor.getExecutorType().getLocaleName()).replace("%action%", action.getActionType().toString()).replace("%error%",errorMessage).replace("%x%",String.valueOf(action.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,100,1.7f);
        }
    }

    /**
     Sends error message about plot's code exception on executing Executor for plot's players.
     **/
    public static void sendPlotCodeCriticalErrorMessage(Plot plot, Executor executor, String errorMessage) {
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,100,0.5f);
            Component message = Component
                    .text(getLocaleMessage("plot-code-error.message-event-critical")
                            .replace("%event%", executor.getExecutorType().getLocaleName())
                            .replace("%error%",errorMessage)
                            .replace("%x%",String.valueOf(executor.getX()))
                            .replace("%y%",String.valueOf(executor.getY()))
                            .replace("%z%",String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("plot-code-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + executor.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
        }
    }

    /**
     Sends error message about plot's code exception on executing Executor for plot's players.
     **/
    public static void sendPlotCodeErrorMessage(Plot plot, Executor executor, String errorMessage) {
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("plot-code-error.message-event")
                            .replace("%event%", executor.getExecutorType().getLocaleName())
                            .replace("%error%",errorMessage)
                            .replace("%x%",String.valueOf(executor.getX()))
                            .replace("%y%",String.valueOf(executor.getY()))
                            .replace("%z%",String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("plot-code-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + executor.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,100,1.7f);

        }
    }

    /**
     Sends error message about plot's code exception on executing Executor for plot's players.
     **/
    public static void sendPlotCompileErrorMessage(Plot plot, Block block, String errorMessage) {
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("plot-code-error.message-compile")
                            .replace("%error%",errorMessage)
                            .replace("%x%",String.valueOf(block.getX()))
                            .replace("%y%",String.valueOf(block.getY()))
                            .replace("%z%",String.valueOf(block.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("plot-code-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + block.getX() + " " + block.getY() + " " + block.getZ()));
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,100,1.7f);
        }
    }

    /**
     Sends error message about plot's code exception on compiling unknown blocks
     **/
    public static void sendPlotCompileErrorMessage(Plot plot, List<Block> unknownBlocks) {
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-error.unknown-block-detected").replace("%error%",getLocaleMessage("plot-code-error.unknown-blocks",false)));
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
                        .text(getLocaleMessage("plot-code-error.unknown-block-coords")
                            .replace("%x%", String.valueOf(block.getLocation().getX()))
                            .replace("%y%", String.valueOf(block.getLocation().getY()))
                            .replace("%z%", String.valueOf(block.getLocation().getZ()))
                            .replace("%category%",category)
                            .replace("%type%",type))
                        .color(color)
                        .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("plot-code-error.hover-message"))))
                        .clickEvent(ClickEvent.runCommand("/dev " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ()));
                player.sendMessage(blockCoordinatesMessage);
            }
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,100,1.7f);
            player.sendMessage(" ");
        }
    }

    /**
     Stops plot's code execution and changes plot's mode to BUILD.
     **/
    public static void stopPlotCode(Plot plot) {
        OpenCreative.getPlugin().getLogger().info("Plot code has been stopped in " + plot.getWorldName() + " because of operations limit.");
        if (plot.getMode() != Plot.Mode.BUILD) {
            PlotModeChangeEvent event = new PlotModeChangeEvent(plot,plot.getMode(), Plot.Mode.BUILD);
            event.callEvent();
            if (!event.isCancelled()) {
                plot.setMode(Plot.Mode.BUILD);
            }
        }
    }

    /**
     Sends warning message about problem with plugin.
     **/
    public static void sendWarningErrorMessage(String errorMessage) {
        OpenCreative.getPlugin().getLogger().warning("Warning! An error has occured: " + errorMessage);
    }

    /**
     Sends critical error message about problem with plugin.
     **/
    public static void sendCriticalErrorMessage(String errorMessage) {
        OpenCreative.getPlugin().getLogger().severe("CRITICAL ERROR has occured: " + errorMessage);
    }

    /**
     Sends critical error message about problem with plugin.
     **/
    public static void sendCriticalErrorMessage(String errorMessage, Exception error) {
        OpenCreative.getPlugin().getLogger().severe("CRITICAL ERROR has occured: " + errorMessage + " " + parseException(error,false));
    }

    public static void sendDebug(String message) {
        if (OpenCreative.debug) {
            OpenCreative.getPlugin().getLogger().info("[DEBUG] " + message);
        }
    }

    public static void sendCodingDebugNotFoundVariable(Plot plot, String name) {
        if (true) {
            return;
        }
        if (!plot.isDebug()) return;
        Object value = null;
        if (value == null) value = "null";
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-debug.variable-not-found",false).replace("%name%",name).replace("%value%", value.toString()));
        }
    }

    public static void sendCodingNotFoundTempVar(Plot plot, Executor executor, EventValues.Variable variable) {
        if (plot == null) return;
        sendPlotCodeErrorMessage(plot,executor,getLocaleMessage("plot-code-error.temp-var-not-exists",false).replace("%variable%", variable.getLocaleName()));
    }

    public static void sendCodingDebugLog(Plot plot, String log) {
        if (!plot.isDebug()) return;
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-debug.log",false).replace("%log%",log));
        }
    }

    public static void sendCodingDebugVariable(Plot plot, String name, Object value) {
        if (true) {
            return;
        }
        if (!plot.isDebug()) return;
        if (value == null) value = "null";
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-debug.variable-found",false).replace("%name%",name).replace("%value%",value.toString()));
        }
    }

    public static void sendCodingDebugExecutor(Executor executor) {
        Plot plot = executor.getPlot();
        if (plot == null || !plot.isDebug()) return;
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-debug.executor-message",false).replace("%type%",executor.getExecutorType().getLocaleName()).replace("%x%",String.valueOf(executor.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
        }
    }

    public static void sendCodingDebugAction(Action action) {
        if (action.getExecutor() == null) return;
        Plot plot = action.getExecutor().getPlot();
        if (plot == null || !plot.isDebug()) return;
        List<Argument> arguments = action.getArgumentsList();
        String message = getLocaleMessage("plot-code-debug.hover." + (action.getActionType().isCondition() ? "condition" : "action"));
        message = message.replace("%category%",action.getActionCategory().getLocaleName());
        message = message.replace("%type%",action.getActionType().getLocaleName());
        if (action instanceof Condition condition) {
            message = message.replace("%opposed%",getLocaleMessage("plot-code-debug.condition.opposed." + condition.isOpposed()));
        }
        List<String> argumentsString = new ArrayList<>();
        for (Argument arg : arguments) {
            argumentsString.add(getLocaleMessage("plot-code-debug.hover.argument").replace("%name%",arg.getPath()).replace("%type%",arg.getType().getLocaleName()).replace("%value%",arg.getValue(action).toString().substring(0, Math.min(30, arg.getValue(action).toString().length()))));
        }
        message = message.replace("%arguments%",String.join(" \n",argumentsString));
        String actionMessage = getLocaleMessage("plot-code-debug.action-message",false).replace("%type%",action.getActionType().getLocaleName()).replace("%x%",String.valueOf(action.getX())).replace("%y%",String.valueOf(action.getExecutor().getY())).replace("%z%",String.valueOf(action.getExecutor().getZ()));
        for (Player player : plot.getPlayers()) {
            player.sendMessage(Component.text(actionMessage).hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(message))));
        }
    }

}
