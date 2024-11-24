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
import mcchickenstudio.creative.coding.blocks.actions.selectionactions.SelectionAction;
import mcchickenstudio.creative.coding.blocks.events.WorldEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.MobDamagesPlayerEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.PlayerDamagesMobEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.PlayerDamagesPlayerEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.PlayerKilledPlayerEvent;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.exceptions.TooLongTextException;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.coding.variables.VariableLink;
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
 * @since 5.0
 * @version 5.0
 * @author McChicken Studio
 */
public abstract class Action {

    private final Executor executor;
    private final Target target;
    private final int x;
    protected Entity entity;

    protected WorldEvent event;
    protected ActionsHandler handler;

    protected final Arguments arguments;
    protected final String EMPTY_STRING = ChatColor.translateAlternateColorCodes('&',"&f");

    /**
     * Creates an Action with linked executor and specified arguments.
     * @param executor Executor where this action will be added.
     * @param target Target, that will execute this action.
     * @param x X coordinate from Action's block location in developers plot.
     * @param args List of arguments for action.
     */
    public Action(Executor executor, Target target, int x, Arguments args) {
        this.executor = executor;
        this.target = target;
        this.x = x;
        this.arguments = args;
    }

    /**
     * Prepares action for executing, sets handler and event, and executes action with target.
     * @param handler ActionsHandler that stores event data and temporary variables.
     */
    public void prepareAndExecute(ActionsHandler handler) {
        this.handler = handler;
        this.event = handler.getEvent();
        sendCodingDebugAction(this);
        if (this instanceof SelectionAction) {
            execute(null);
        }
        for (Entity entity : getTargets()) {
            if (!getActionType().isSelectionMustBeInWorld() || (entity != null && entity.getWorld() == getPlot().getTerritory().getWorld())) {
                this.entity = entity;
                execute(entity);
            }
        }
    }

    /**
     * Executes action with specified entity.
     * @param entity Entity to execute action.
     */
    protected abstract void execute(Entity entity);
    public abstract ActionType getActionType();
    public abstract ActionCategory getActionCategory();

    /**
     * Returns arguments of action.
     * @return Arguments of action.
     */
    protected final Arguments getArguments() {
        return arguments;
    }

    /**
     * Returns executor, that stores this action.
     * @return Executor with this action.
     */
    public final Executor getExecutor() {
        return executor;
    }

    /**
     * Returns X coordinate of action coding block in developer's world.
     * @return X coordinate of coding block location.
     */
    public final int getX() {
        return x;
    }

    /**
     * Returns a set of entities whose name or UUID is equal to specified text.
     * @param text Text to compare world's entities names and UUIDs.
     * @return Set of entities with same names or UUIDs, as text.
     */
    protected Set<Entity> getEntitiesByNameOrUUID(String text) {
        Set<Entity> entities = new HashSet<>();
        if (getWorld() == null) return entities;
        for (Entity entity : executor.getPlot().getTerritory().getWorld().getEntities()) {
            if (entity.getName().equalsIgnoreCase(text) || entity.getUniqueId().toString().equalsIgnoreCase(text)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    protected Set<Player> getPlayersByNameOrUUID(String text) {
        Set<Player> players = new HashSet<>();
        if (getWorld() == null) return players;
        for (Player player : getWorld().getPlayers()) {
            if (player.getName().equalsIgnoreCase(text) || player.getUniqueId().toString().equalsIgnoreCase(text)) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * Returns plot's world, where action will be executed.
     * @return Plot's world.
     */
    protected World getWorld() {
        return getPlot().getTerritory().getWorld();
    }

    protected Plot getPlot() {
        return executor.getPlot();
    }

    /**
     * Returns involved entity in action from ActionsHandler.
     * @return Involved entity.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Returns last stored event in action.
     * @return CreativeEvent, that called executor with this action.
     */
    public WorldEvent getEvent() {
        return event;
    }

    /**
     * Returns current ActionsHandler in action.
     * @return Handler of this action.
     */
    public ActionsHandler getHandler() {
        return handler;
    }

    /**
     * Returns enum of target.
     * @return Enum of target.
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Returns list of entities that will execute this action.
     * @return List of entities to execute action.
     */
    protected List<Entity> getTargets() {
        List<Entity> entities = new ArrayList<>();
        List<Entity> eventEntities = executor.getEvent().getSelection();
        switch (target) {
            case RANDOM_PLAYER -> {
                Player randomPlayer = null;
                List<Player> playerList = this.getExecutor().getPlot().getTerritory().getWorld().getPlayers();
                if (!playerList.isEmpty()) {
                    Random r = new Random();
                    int i = r.nextInt(playerList.size());
                    randomPlayer = playerList.get(i);
                }
                if (randomPlayer != null) {
                    entities.add(randomPlayer);
                }
            }
            case ALL_PLAYERS -> {
                List<Player> playerList = this.getExecutor().getPlot().getPlayers();
                if (!playerList.isEmpty()) {
                    entities.addAll(playerList);
                }
            }
            case KILLER -> {
                Entity killer = getKiller();
                if (killer != null) {
                    entities.add(killer);
                }
            }
            case VICTIM -> {
                Entity victim = getVictim();
                if (victim != null) {
                    entities.add(victim);
                }
            }
            case SELECTED -> entities.addAll(getHandler().getSelectedTargets());
            case ALL_ENTITIES -> {
                for (Entity entity : getWorld().getEntities()) {
                    if (!(entity instanceof Player)) {
                        entities.add(entity);
                    }
                }
            }
            case RANDOM_TARGET -> {
                List<Entity> selectedTargets = new ArrayList<>(getHandler().getSelectedTargets());
                if (!selectedTargets.isEmpty()) {
                    entities.add(selectedTargets.get(new Random().nextInt(selectedTargets.size())));
                }
            }
            default -> entities.addAll(eventEntities);
        }
        return entities;
    }

    private Entity getVictim() {
        Entity victim = null;
        if (executor.getEvent() instanceof PlayerDamagesMobEvent mobEvent) {
            victim = mobEvent.getVictim();
        } else if (executor.getEvent() instanceof MobDamagesPlayerEvent playerEvent) {
            victim = playerEvent.getVictim();
        } else if (executor.getEvent() instanceof PlayerDamagesPlayerEvent playerEvent) {
            victim = playerEvent.getVictim();
        } else if (executor.getEvent() instanceof PlayerKilledPlayerEvent playerEvent) {
            victim = playerEvent.getVictim();
        }
        return victim;
    }

    /**
     * Returns entity killer, that involved in damage event.
     * @return Killer, or null if there's no involved entity in damage event.
     */
    private Entity getKiller() {
        Entity killer = null;
        if (executor.getEvent() instanceof PlayerDamagesMobEvent mobEvent) {
            killer = mobEvent.getDamager();
        } else if (executor.getEvent() instanceof MobDamagesPlayerEvent playerEvent) {
            killer = playerEvent.getDamager();
        } else if (executor.getEvent() instanceof PlayerDamagesPlayerEvent playerEvent) {
            killer = playerEvent.getDamager();
        } else if (executor.getEvent() instanceof PlayerKilledPlayerEvent playerEvent) {
            killer = playerEvent.getKiller();
        }
        return killer;
    }

    /**
     * Sets value in local, global or saved variable in world.
     * @param link Link of variable.
     * @param value New value.
     */
    protected void setVarValue(VariableLink link, Object value) {
        if (link != null) {
            link.setHandler(getHandler().getMainActionHandler());
            ValueType type = ValueType.getByObject(value);
            if (type == null) {
                type = ValueType.TEXT;
            }
            if (value instanceof String text) {
                if (text.length() > 1024) {
                    throw new TooLongTextException(1024);
                }
            }
            getPlot().getVariables().setVariableValue(link, type, value, getHandler().getMainActionHandler(), this);
        }
    }

    /**
     * Sets new ActionsHandler.
     * @param handler New handler of action.
     */
    public void setHandler(ActionsHandler handler) {
        this.handler = handler;
    }

    /**
     * Returns a list of all arguments in this action.
     * @return List of action's arguments.
     */
    public List<Argument> getArgumentsList() {
        return getArguments().getArgumentList();
    }

    /**
     * Sets entity involved in executor's event.
     * @param entity New entity.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
