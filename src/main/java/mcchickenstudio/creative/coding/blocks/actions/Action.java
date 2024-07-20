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

import mcchickenstudio.creative.coding.arguments.Argument;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.MobDamagesPlayerEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.PlayerDamagesMobEvent;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.coding.variables.VariableLink;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

import static mcchickenstudio.creative.coding.arguments.Argument.parseEntity;
import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugAction;

/**
 * <h1>Action</h1>
 * This class represents Action that will be executed in executor.
 * @since 1.5
 * @version 1.5
 * @author McChicken Studio
 */
public abstract class Action {

    private final Executor executor;
    private final Target target;
    private final int x;
    protected Entity entity;

    protected CreativeEvent event;
    protected ActionsHandler handler;

    protected final Arguments arguments;
    protected final String EMPTY_STRING = ChatColor.translateAlternateColorCodes('&',"&f");

    /**
     * Creates an Action with linked executor and specified arguments.
     * @param executor Executor where this action will be added.
     * @param x X from Action's block location in developers plot.
     * @param args List of arguments for action.
     */
    public Action(Executor executor, Target target, int x, Arguments args) {
        this.executor = executor;
        this.target = target;
        this.x = x;
        this.arguments = args;
    }

    public void prepareAndExecute(ActionsHandler handler) {
        this.handler = handler;
        this.event = handler.getEvent();
        sendCodingDebugAction(this);
        for (Entity entity : getTargets()) {
            if (!getActionType().isSelectionMustBeInWorld() || entity.getWorld() == getPlot().world) {
                this.entity = entity;
                execute(entity);
            }
        }
    }

    protected abstract void execute(Entity entity);
    public abstract ActionType getActionType();
    public abstract ActionCategory getActionCategory();

    protected final Arguments getArguments() {
        return arguments;
    }

    public final Executor getExecutor() {
        return executor;
    }

    public final int getX() {
        return x;
    }

    protected Set<Entity> getEntitiesByNameOrUUID(String text) {
        Set<Entity> entities = new HashSet<>();
        if (getWorld() == null) return entities;
        for (Entity entity : executor.getPlot().world.getEntities()) {
            if (entity.getName().equalsIgnoreCase(text) || entity.getUniqueId().equals(UUID.fromString(text))) {
                entities.add(entity);
            }
        }
        return entities;
    }

    protected Set<Player> getPlayersByNameOrUUID(String text) {
        Set<Player> players = new HashSet<>();
        if (getWorld() == null) return players;
        for (Player player : getWorld().getPlayers()) {
            if (player.getName().equalsIgnoreCase(text) || player.getUniqueId().equals(UUID.fromString(text))) {
                players.add(player);
            }
        }
        return players;
    }

    protected World getWorld() {
        return getPlot().world;
    }

    protected Plot getPlot() {
        return executor.getPlot();
    }

    public Entity getEntity() {
        return entity;
    }

    public CreativeEvent getEvent() {
        return event;
    }

    public ActionsHandler getHandler() {
        return handler;
    }

    public Target getTarget() {
        return target;
    }

    protected List<Entity> getTargets() {
        List<Entity> entities = new ArrayList<>();
        List<Entity> eventEntities = executor.getEvent().getSelection();
        switch (target) {
            case RANDOM_PLAYER -> {
                Player randomPlayer = null;
                List<Player> playerList = this.getExecutor().getPlot().getPlayers();
                if (!playerList.isEmpty()) {
                    Random r = new Random();
                    int i = r.nextInt(playerList.size());
                    randomPlayer = playerList.get(i);
                }
                entities.add(randomPlayer);
            }
            case ALL_PLAYERS -> {
                List<Player> playerList = this.getExecutor().getPlot().getPlayers();
                if (!playerList.isEmpty()) {
                    entities.addAll(playerList);
                }
            }
            case KILLER -> {
                Entity killer = null;
                if (executor.getEvent() instanceof PlayerDamagesMobEvent mobEvent) {
                    killer = mobEvent.getDamager();
                } else if (executor.getEvent() instanceof MobDamagesPlayerEvent playerEvent) {
                    killer = playerEvent.getDamager();
                }
                if (killer != null) {
                    entities.add(killer);
                }
            }
            case VICTIM -> {
                Entity victim = null;
                if (executor.getEvent() instanceof PlayerDamagesMobEvent mobEvent) {
                    victim = mobEvent.getVictim();
                } else if (executor.getEvent() instanceof MobDamagesPlayerEvent playerEvent) {
                    victim = playerEvent.getVictim();
                }
                if (victim != null) {
                    entities.add(victim);
                }
            }
            default -> entities.addAll(eventEntities);
        }
        return entities;
    }

    protected void setVarValue(VariableLink link, Object value) {
        if (link != null) {
            link.setName(parseEntity(link.getName(),this));
            link.setHandler(getHandler().getMainActionHandler());
            ValueType type = ValueType.getByObject(value);
            if (type == null) {
                type = ValueType.TEXT;
            }
            getPlot().getWorldVariables().setVariableValue(link, type, value, getHandler().getMainActionHandler());
        }
    }

    public List<Argument> getArgumentsList() {
        return getArguments().getArgumentList();
    }

}
