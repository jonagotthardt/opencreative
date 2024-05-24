/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.utils;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static mcchickenstudio.creative.utils.PlayerUtils.clearPlayer;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class ErrorUtils {

    /**
     Sends error message to player.
     **/
    public static void sendPlayerErrorMessage(Player player, String errorMessage) {
        Main.getPlugin().getLogger().warning("An player error has occured for " + player.getName() + ": " + errorMessage);
        player.sendMessage(getLocaleMessage("player-error").replace("%error%",errorMessage));
        player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_DESTROY"),100,2);
    }

    /**
     Sends error message for plot's players.
     **/
    public static void sendPlotErrorMessage(Plot plot, String errorMessage) {
        Main.getPlugin().getLogger().warning("An error has occured in plot " + plot.plotName + ": " + errorMessage);
        for (Player player : plot.getPlayers()) {
            player.sendMessage(getLocaleMessage("plot-error").replace("%error%",errorMessage));
            player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_DESTROY"),100,2);
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
     Stops plot's code execution and changes plot's mode to BUILD.
     **/
    public static void stopPlotCode(Plot plot) {
        Main.getPlugin().getLogger().info("Plot code has been stopped in " + plot.worldName + " because of operations limit.");
        if (plot.plotMode != Plot.Mode.BUILD) {
            plot.plotMode = Plot.Mode.BUILD;
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

}
