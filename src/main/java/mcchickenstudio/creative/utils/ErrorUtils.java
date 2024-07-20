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

package mcchickenstudio.creative.utils;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.arguments.Argument;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.BlockUtils.getSignLine;
import static mcchickenstudio.creative.utils.PlayerUtils.clearPlayer;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class ErrorUtils {

    private static String parseException(Exception error) {
        List<String> lastStacks = new ArrayList<>();
        byte i = 0;
        for (StackTraceElement stackTraceElement : error.getStackTrace()) {
            lastStacks.add(stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber());
            i++;
            if (i == 15) {
                break;
            }
        }
        return "\n" + error.getClass().getSimpleName() + ": " + error.getMessage() + "\n" + String.join("\n",lastStacks);
    }

    /**
     Sends error message to player.
     **/
    public static void sendPlayerErrorMessage(Player player, String errorMessage) {
        Main.getPlugin().getLogger().warning("An player error has occurred for " + player.getName() + ": " + errorMessage);
        player.sendMessage(getLocaleMessage("player-error").replace("%error%",errorMessage));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK,100f,0.1f);
    }

    /**
     Sends error message to player.
     **/
    public static void sendPlayerErrorMessage(Player player, String errorMessage, Exception error) {
        Main.getPlugin().getLogger().warning("An player error has occurred for " + player.getName() + ": " + errorMessage + " " + parseException(error));
        TextComponent message = new TextComponent(getLocaleMessage("player-error").replace("%error%",errorMessage));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(parseException(error))));
        player.sendMessage(message);
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK,100f,0.1f);
    }

    /**
     Sends error message for plot's players.
     **/
    public static void sendPlotErrorMessage(Plot plot, String errorMessage) {
        Main.getPlugin().getLogger().warning("An error has occurred in plot " + plot.getPlotName() + ": " + errorMessage);
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
            TextComponent message = new TextComponent(getLocaleMessage("plot-code-warning.message").replace("%event%", executor.getExecutorType().getLocaleName()).replace("%action%",action.getActionType().getLocaleName()).replace("%warning%",warningMessage).replace("%x%",String.valueOf(action.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("plot-code-error.hover-message"))));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dev " + action.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,100,1.7f);
        }
    }

    /**
     Sends error message about plot's code exception on running Action for plot's players.
     **/
    public static void sendPlotCodeErrorMessage(Executor executor, Action action, String errorMessage) {
        Plot plot = executor.getPlot();
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            TextComponent message = new TextComponent(getLocaleMessage("plot-code-error.message").replace("%event%", executor.getExecutorType().getLocaleName()).replace("%action%",action.getActionType().getLocaleName()).replace("%error%",errorMessage).replace("%x%",String.valueOf(action.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("plot-code-error.hover-message"))));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dev " + action.getX() + " " + executor.getY() + " " + executor.getZ()));
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
            TextComponent message = new TextComponent(getLocaleMessage("plot-code-error.message-event-critical").replace("%event%", executor.getExecutorType().getLocaleName()).replace("%error%",errorMessage).replace("%x%",String.valueOf(executor.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("plot-code-error.hover-message"))));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dev " + executor.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
        }
    }

    /**
     Sends error message about plot's code exception on executing Executor for plot's players.
     **/
    public static void sendPlotCodeErrorMessage(Plot plot, Executor executor, String errorMessage) {
        if (plot == null) return;
        for (Player player : plot.getPlayers()) {
            TextComponent message = new TextComponent(getLocaleMessage("plot-code-error.message-event").replace("%event%", executor.getExecutorType().getLocaleName()).replace("%error%",errorMessage).replace("%x%",String.valueOf(executor.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("plot-code-error.hover-message"))));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dev " + executor.getX() + " " + executor.getY() + " " + executor.getZ()));
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
            TextComponent message = new TextComponent(getLocaleMessage("plot-code-error.message-compile").replace("%error%",errorMessage).replace("%x%",String.valueOf(block.getLocation().getX())).replace("%y%",String.valueOf(block.getLocation().getY())).replace("%z%",String.valueOf(block.getLocation().getZ())));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("plot-code-error.hover-message"))));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dev " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ()));
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
                ChatColor color = ChatColor.GRAY;
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

                TextComponent blockCoordinatesMessage = new TextComponent(getLocaleMessage("plot-code-error.unknown-block-coords")
                        .replace("%x%", String.valueOf(block.getLocation().getX()))
                        .replace("%y%", String.valueOf(block.getLocation().getY()))
                        .replace("%z%", String.valueOf(block.getLocation().getZ()))
                        .replace("%category%",category)
                        .replace("%type%",type));

                blockCoordinatesMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("plot-code-error.hover-message"))));
                blockCoordinatesMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dev " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ()));
                blockCoordinatesMessage.setColor(color);
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
        Main.getPlugin().getLogger().info("Plot code has been stopped in " + plot.worldName + " because of operations limit.");
        if (plot.getPlotMode() != Plot.Mode.BUILD) {
            plot.setPlotMode(Plot.Mode.BUILD);
            for (Player p : plot.getPlayers()){
                if (PlotManager.getInstance().getDevPlot(p) == null) {
                    clearPlayer(p);
                    p.teleport(plot.world.getSpawnLocation());
                }
            }
        }
    }

    /**
     Sends warning message about problem with plugin.
     **/
    public static void sendWarningErrorMessage(String errorMessage) {
        Main.getPlugin().getLogger().warning("Warning! An error has occured: " + errorMessage);
    }

    /**
     Sends critical error message about problem with plugin.
     **/
    public static void sendCriticalErrorMessage(String errorMessage) {
        Main.getPlugin().getLogger().severe("CRITICAL ERROR has occured: " + errorMessage);
    }

    /**
     Sends critical error message about problem with plugin.
     **/
    public static void sendCriticalErrorMessage(String errorMessage, Exception error) {
        Main.getPlugin().getLogger().severe("CRITICAL ERROR has occured: " + errorMessage + " " + parseException(error));
    }

    public static void sendDebug(String message) {
        if (Main.debug) {
            Main.getPlugin().getLogger().info("[DEBUG] " + message);
        }
    }

    public static void sendCodingDebugNotFoundVariable(Plot plot, String name, Object value) {
        if (plot.getDebug()) return;
        if (value == null) value = "null";
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-debug.variable-not-found",false).replace("%name%",name).replace("%value%",value.toString()));
        }
    }

    public static void sendCodingNotFoundTempVar(Plot plot, Executor executor, EventValues.Variable variable) {
        if (plot == null) return;
        sendPlotCodeErrorMessage(plot,executor,getLocaleMessage("plot-code-error.temp-var-not-exists",false).replace("%variable%", variable.getLocaleName()));
    }

    public static void sendCodingDebugVariable(Plot plot, String name, Object value) {
        if (plot.getDebug()) return;
        if (value == null) value = "null";
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-debug.variable-found",false).replace("%name%",name).replace("%value%",value.toString()));
        }
    }

    public static void sendCodingDebugExecutor(Executor executor) {
        Plot plot = executor.getPlot();
        if (plot == null || plot.getDebug()) return;
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-code-debug.executor-message",false).replace("%type%",executor.getExecutorType().getLocaleName()).replace("%x%",String.valueOf(executor.getX())).replace("%y%",String.valueOf(executor.getY())).replace("%z%",String.valueOf(executor.getZ())));
        }
    }

    public static void sendCodingDebugAction(Action action) {
        if (action.getExecutor() == null) return;
        Plot plot = action.getExecutor().getPlot();
        if (plot == null || plot.getDebug()) return;
        for (Player player : plot.getPlayers()) {
            List<Argument> arguments = action.getArgumentsList();
            StringBuilder hoverMessage = new StringBuilder();
            for (Argument arg : arguments) {
                hoverMessage.append(getLocaleMessage("plot-code-debug.action-hover", false).replace("%type%", arg.getType().getLocalized()).replace("%name%", arg.getPath()).replace("%value%", arg.getValue(action).toString().substring(0, Math.min(30, arg.getValue(action).toString().length())))).append("\n");
            }
            String actionMessage = getLocaleMessage("plot-code-debug.action-message",false).replace("%type%",action.getActionType().getLocaleName()).replace("%x%",String.valueOf(action.getX())).replace("%y%",String.valueOf(action.getExecutor().getY())).replace("%z%",String.valueOf(action.getExecutor().getZ()));
            player.sendMessage(Component.text(actionMessage).hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(hoverMessage.toString()))));
        }
    }

}
