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

package mcchickenstudio.creative.coding.blocks.actions;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.events.EventVariables;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.PlayerDamagesMobEvent;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.blocks.executors.player.fighting.PlayerDamagesMobExecutor;
import mcchickenstudio.creative.coding.blocks.executors.player.world.ChatExecutor;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugAction;

/**
 * <h1>Action</h1>
 * This class represents Action that will be executed in executor.
 * @since 1.5
 * @version 1.5
 * @author McChicken Studio
 */
public abstract class Action {

    private final Executor EXECUTOR;
    private final int X;
    private List<Entity> entities;

    protected final Arguments ARGUMENTS;
    protected final String EMPTY_STRING = ChatColor.translateAlternateColorCodes('&',"&f");

    /**
     * Creates an Action with linked executor and specified arguments.
     * @param executor Executor where this action will be added.
     * @param x X from Action's block location in developers plot.
     * @param args List of arguments for action.
     */
    public Action(Executor executor, int x, Arguments args) {
        this.EXECUTOR = executor;
        this.X = x;
        this.ARGUMENTS = args;
    }

    public void run(List<Entity> selection) {
        this.entities = selection;
        sendCodingDebugAction(this);
        for (Entity entity : selection) {
            if (!getActionType().isSelectionMustBeInWorld() || entity.getWorld() == getPlot().world) {
                execute(selection);
            }
        }
    }

    protected abstract void execute(List<Entity> selection);
    public abstract ActionType getActionType();
    public abstract ActionCategory getActionCategory();

    protected final Arguments getArguments() {
        return ARGUMENTS;
    }

    public final Executor getExecutor() {
        return EXECUTOR;
    }

    public final int getX() {
        return X;
    }

    //FIXME: Replace it
    protected String parseEntityPlaceholders(String text, Entity entity) {
        Plot plot = getExecutor().getPlot();
        String newText = text;
        newText = text.replace("%player%",entity.getName())
                .replace("%entity%",entity.getName())
                .replace("%plot_online%",String.valueOf(plot.getOnline()))
                .replace("%plot_name%",plot.getPlotName())
                .replace("%plot_description%",plot.getPlotDescription());
        if (EXECUTOR instanceof PlayerDamagesMobExecutor) {
            PlayerDamagesMobEvent event = (PlayerDamagesMobEvent) EXECUTOR.getEvent();
            newText = newText.replace("%damager%",event.getDamager().getName())
                    .replace("%damage%",String.valueOf(event.getDamage()));
        } else if (EXECUTOR instanceof ChatExecutor) {
            newText = newText.replace("%message%",(String) getExecutor().getVarValue(EventVariables.Variable.MESSAGE));
        }
        return newText;
    }

    protected String parseColors(String text) {
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    protected String parseText(String text, Entity entity) {
        return parseColors(parseEntityPlaceholders(text,entity)).replace("\\n","\n");
    }

    protected Set<Entity> getEntitiesByNameOrUUID(String text) {
        Set<Entity> entities = new HashSet<>();
        if (getWorld() == null) return entities;
        for (Entity entity : EXECUTOR.getPlot().world.getEntities()) {
            if (entity.getName().equalsIgnoreCase(text) || entity.getUniqueId().equals(text)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    protected Set<Player> getPlayersByNameOrUUID(String text) {
        Set<Player> players = new HashSet<>();
        if (getWorld() == null) return players;
        for (Player player : getWorld().getPlayers()) {
            if (player.getName().equalsIgnoreCase(text) || player.getUniqueId().equals(text)) {
                players.add(player);
            }
        }
        return players;
    }

    protected Plot getPlot() {
        if (EXECUTOR != null) {
            return EXECUTOR.getPlot();
        }
        return null;
    }

    protected World getWorld() {
        if (getPlot() != null) {
            return getPlot().world;
        }
        return null;
    }
}
