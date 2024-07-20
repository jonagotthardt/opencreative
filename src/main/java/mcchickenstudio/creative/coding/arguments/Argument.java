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

package mcchickenstudio.creative.coding.arguments;

import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.placeholders.Placeholders;
import mcchickenstudio.creative.coding.variables.EventValueLink;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.coding.variables.VariableLink;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Argument {

    protected final Plot plot;
    protected final String path;
    protected final ValueType type;
    protected final Object value;

    public Argument(Plot plot, ValueType type, String path, Object value) {
        this.plot = plot;
        this.path = path;
        this.value = value;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public ValueType getType() {
        return type;
    }

    public Object getValue(Action action) {
        if (value instanceof VariableLink link) {
            link.setName(parseEntity(link.getName(),action));
            link.setHandler(action.getHandler().getMainActionHandler());
            Object variableValue = plot.getWorldVariables().getVariableValue(link);
            if (variableValue != null) {
                return variableValue;
            }
        } else if (value instanceof EventValueLink link) {
            setTempVars(action);
            Object value = action.getHandler().getVariables().getVarValue(link.type());
            if (value != null) {
                return value;
            }
        } else if (value instanceof String string) {
            return parseEntity(string,action);
        }
        return value;
    }

    public boolean isList() {
        return (this.type == ValueType.LIST);
    }

    private void setTempVars(Action action) {
        // FIXME: We need to separate these methods into different class.
        setEventVariable(action, EventValues.Variable.NICKNAME,action.getEntity().getName());
        setEventVariable(action, EventValues.Variable.UUID,action.getEntity().getUniqueId());setTempPlotVars(action);
        setTempPlayerVars(action);
    }

    private void setEventVariable(Action action, EventValues.Variable variable, Object value) {
        action.getHandler().setVarValue(variable,value);
    }

    private void setTempPlotVars(Action action) {
        long time = System.currentTimeMillis();

        SimpleDateFormat hoursFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minutesFormat = new SimpleDateFormat("mm");
        SimpleDateFormat secondsFormat = new SimpleDateFormat("ss");
        Date date = new Date(time);

        setEventVariable(action, EventValues.Variable.PLOT_NAME,plot.getPlotName());
        setEventVariable(action, EventValues.Variable.PLOT_DESCRIPTION,plot.getPlotDescription());
        setEventVariable(action, EventValues.Variable.PLOT_ONLINE,plot.getOnline());
        setEventVariable(action, EventValues.Variable.PLOT_ICON,new ItemStack(plot.getPlotIconMaterial(),1));
        setEventVariable(action, EventValues.Variable.PLOT_REPUTATION,plot.getReputation());
        setEventVariable(action, EventValues.Variable.PLOT_ENTITIES_AMOUNT,plot.world.getEntityCount() + ((plot.devPlot != null && plot.devPlot.world != null) ? plot.devPlot.world.getEntityCount() : 0));
        setEventVariable(action, EventValues.Variable.PLOT_ENTITIES_AMOUNT_LIMIT,plot.entitiesLimit);
        setEventVariable(action, EventValues.Variable.PLOT_LAST_REDSTONE_OPERATIONS,plot.lastRedstoneOperationsAmount);
        setEventVariable(action, EventValues.Variable.PLOT_REDSTONE_OPERATIONS_LIMIT,plot.redstoneOperationsLimit);
        setEventVariable(action, EventValues.Variable.PLOT_CUSTOM_ID,plot.getPlotCustomID());
        setEventVariable(action, EventValues.Variable.PLOT_ID,plot.worldID);
        setEventVariable(action, EventValues.Variable.PLOT_VARIABLES_AMOUNT,plot.getWorldVariables().getTotalVariablesAmount());
        setEventVariable(action, EventValues.Variable.PLOT_VARIABLES_AMOUNT_LIMIT,plot.getVariablesAmountLimit());
        setEventVariable(action, EventValues.Variable.UNIX_TIME,time);
        setEventVariable(action, EventValues.Variable.UNIX_TIME_HOURS,Integer.parseInt(hoursFormat.format(date)));
        setEventVariable(action, EventValues.Variable.UNIX_TIME_MINUTES,Integer.parseInt(minutesFormat.format(date)));
        setEventVariable(action, EventValues.Variable.UNIX_TIME_SECONDS,Integer.parseInt(secondsFormat.format(date)));
        setEventVariable(action, EventValues.Variable.WORLD_TIME,plot.world.getTime());
        setEventVariable(action, EventValues.Variable.CLEAR_WEATHER_DURATION,plot.world.getClearWeatherDuration());
        setEventVariable(action, EventValues.Variable.THUNDER_WEATHER_DURATION,plot.world.getThunderDuration());
    }

    private void setTempPlayerVars(Action action) {
        if (action.getEntity() instanceof Player player) {
            setEventVariable(action, EventValues.Variable.PING, player.getPing());
            setEventVariable(action, EventValues.Variable.WALK_SPEED, player.getWalkSpeed());
            setEventVariable(action, EventValues.Variable.FLY_SPEED, player.getFlySpeed());
            setEventVariable(action, EventValues.Variable.ARROWS_IN_BODY, player.getArrowsInBody());
            setEventVariable(action, EventValues.Variable.BEE_STINGER_COOLDOWN, player.getBeeStingerCooldown());
            setEventVariable(action, EventValues.Variable.FALL_DISTANCE, player.getFallDistance());
            setEventVariable(action, EventValues.Variable.FREEZE_TICKS, player.getFreezeTicks());
            setEventVariable(action, EventValues.Variable.FIRE_TICKS, player.getFireTicks());
            setEventVariable(action, EventValues.Variable.GAME_MODE, player.getGameMode().name().toLowerCase());
            setEventVariable(action, EventValues.Variable.NO_DAMAGE_TICKS, player.getNoDamageTicks());
            setEventVariable(action, EventValues.Variable.MAX_NO_DAMAGE_TICKS, player.getMaximumNoDamageTicks());
            setEventVariable(action, EventValues.Variable.HEALTH, player.getHealth());
            setEventVariable(action, EventValues.Variable.MAX_HEALTH, (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null ? player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : null));
            setEventVariable(action, EventValues.Variable.HUNGER, player.getFoodLevel());
            setEventVariable(action, EventValues.Variable.EXPERIENCE, player.getExp());
            setEventVariable(action, EventValues.Variable.EXPERIENCE_LEVEL, player.getLevel());
            setEventVariable(action, EventValues.Variable.TOTAL_EXPERIENCE, player.getTotalExperience());
            setEventVariable(action, EventValues.Variable.SHIELD_BLOCKING_DELAY, player.getShieldBlockingDelay());

            setEventVariable(action, EventValues.Variable.LOCATION, player.getLocation());
            setEventVariable(action, EventValues.Variable.EYE_LOCATION, player.getEyeLocation());
            setEventVariable(action, EventValues.Variable.COMPASS_TARGET, player.getCompassTarget());

            setEventVariable(action, EventValues.Variable.LAST_DEATH_LOCATION, player.getLastDeathLocation());
            setEventVariable(action, EventValues.Variable.LAST_DAMAGE, player.getLastDamage());
            setEventVariable(action, EventValues.Variable.LAST_DAMAGE_CAUSE, (player.getLastDamageCause() != null ? player.getLastDamageCause().getCause().name().toLowerCase() : null));
            setEventVariable(action, EventValues.Variable.CAN_PICKUP_ITEM, player.getCanPickupItems());

            setEventVariable(action, EventValues.Variable.ITEM_IN_MAIN_HAND, player.getInventory().getItemInMainHand());
            setEventVariable(action, EventValues.Variable.ITEM_IN_OFF_HAND, player.getInventory().getItemInOffHand());
            setEventVariable(action, EventValues.Variable.HELMET, player.getInventory().getHelmet());
            setEventVariable(action, EventValues.Variable.CHESTPLATE, player.getInventory().getChestplate());
            setEventVariable(action, EventValues.Variable.LEGGINGS, player.getInventory().getLeggings());
            setEventVariable(action, EventValues.Variable.BOOTS, player.getInventory().getBoots());
        }
    }

    public static String parseEntity(String text, Action action) {
        return Placeholders.getInstance().parseAction(text,action);
    }

    @Override
    public String toString() {
        return "Argument. Name: " + path + ", Type: " + type.name() + ", Value: " + value.toString();
    }
}
