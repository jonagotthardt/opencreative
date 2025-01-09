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

package ua.mcchickenstudio.opencreative.coding.arguments;

import org.bukkit.block.Block;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.placeholders.Placeholders;
import ua.mcchickenstudio.opencreative.coding.variables.EventValueLink;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Argument {

    protected final Planet planet;
    protected final String path;
    protected final ValueType type;
    protected final Object value;

    public Argument(Planet planet, ValueType type, String path, Object value) {
        this.planet = planet;
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
            Object variableValue = planet.getVariables().getVariableValue(link,action);
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
            return parseEntity(string,action.getHandler().getMainActionHandler(),action);
        }
        return value;
    }

    public boolean isList() {
        return (this.type == ValueType.LIST);
    }

    private void setTempVars(Action action) {
        // FIXME: We need to separate these methods into different class.
        if (action.getEntity() instanceof Entity entity) {
            setEventVariable(action, EventValues.Variable.NICKNAME,entity.getName());
            setEventVariable(action, EventValues.Variable.TYPE,entity.getType().name().toLowerCase());
            setEventVariable(action, EventValues.Variable.UUID,entity.getUniqueId().toString());
        }
        setTempPlanetVars(action);
        setTempPlayerVars(action);
    }

    private void setEventVariable(Action action, EventValues.Variable variable, Object value) {
        action.getHandler().setVarValue(variable,value);
    }

    private void setTempPlanetVars(Action action) {
        long time = System.currentTimeMillis();

        SimpleDateFormat hoursFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minutesFormat = new SimpleDateFormat("mm");
        SimpleDateFormat secondsFormat = new SimpleDateFormat("ss");
        Date date = new Date(time);

        setEventVariable(action, EventValues.Variable.PLANET_NAME, planet.getInformation().getDisplayName());
        setEventVariable(action, EventValues.Variable.PLANET_DESCRIPTION, planet.getInformation().getDescription());
        setEventVariable(action, EventValues.Variable.PLANET_ONLINE, planet.getTerritory().getWorld().getPlayerCount());
        setEventVariable(action, EventValues.Variable.PLANET_ICON,new ItemStack(planet.getInformation().getIcon().getType(),1));
        setEventVariable(action, EventValues.Variable.PLANET_REPUTATION, planet.getInformation().getReputation());
        setEventVariable(action, EventValues.Variable.PLANET_ENTITIES_AMOUNT, planet.getTerritory().getWorld().getEntityCount() + ((planet.getDevPlanet() != null && planet.getDevPlanet().getWorld() != null) ? planet.getDevPlanet().getWorld().getEntityCount() : 0));
        setEventVariable(action, EventValues.Variable.PLANET_ENTITIES_AMOUNT_LIMIT, planet.getLimits().getEntitiesLimit());
        setEventVariable(action, EventValues.Variable.PLANET_LAST_REDSTONE_OPERATIONS, planet.getLimits().getLastRedstoneOperationsAmount());
        setEventVariable(action, EventValues.Variable.PLANET_REDSTONE_OPERATIONS_LIMIT, planet.getLimits().getRedstoneOperationsLimit());
        setEventVariable(action, EventValues.Variable.PLANET_CUSTOM_ID, planet.getInformation().getCustomID());
        setEventVariable(action, EventValues.Variable.PLANET_ID, planet.getId());
        setEventVariable(action, EventValues.Variable.PLANET_VARIABLES_AMOUNT, planet.getVariables().getTotalVariablesAmount());
        setEventVariable(action, EventValues.Variable.PLANET_VARIABLES_AMOUNT_LIMIT, planet.getLimits().getVariablesAmountLimit());
        setEventVariable(action, EventValues.Variable.UNIX_TIME,time);
        setEventVariable(action, EventValues.Variable.UNIX_TIME_HOURS,Integer.parseInt(hoursFormat.format(date)));
        setEventVariable(action, EventValues.Variable.UNIX_TIME_MINUTES,Integer.parseInt(minutesFormat.format(date)));
        setEventVariable(action, EventValues.Variable.UNIX_TIME_SECONDS,Integer.parseInt(secondsFormat.format(date)));
        setEventVariable(action, EventValues.Variable.WORLD_TIME, planet.getTerritory().getWorld().getTime());
        setEventVariable(action, EventValues.Variable.CLEAR_WEATHER_DURATION, planet.getTerritory().getWorld().getClearWeatherDuration());
        setEventVariable(action, EventValues.Variable.THUNDER_WEATHER_DURATION, planet.getTerritory().getWorld().getThunderDuration());
    }

    private void setTempPlayerVars(Action action) {
        if (action.getEntity() instanceof Entity entity) {
            setEventVariable(action, EventValues.Variable.FALL_DISTANCE, entity.getFallDistance());
            setEventVariable(action, EventValues.Variable.FREEZE_TICKS, entity.getFreezeTicks());
            setEventVariable(action, EventValues.Variable.FIRE_TICKS, entity.getFireTicks());
            setEventVariable(action, EventValues.Variable.LOCATION, entity.getLocation());
            setEventVariable(action, EventValues.Variable.VELOCITY, entity.getVelocity());
            setEventVariable(action, EventValues.Variable.LAST_DAMAGE_CAUSE, (entity.getLastDamageCause() != null ? entity.getLastDamageCause().getCause().name().toLowerCase() : null));
        }
        if (action.getEntity() instanceof LivingEntity livingEntity) {
            setEventVariable(action, EventValues.Variable.ARROWS_IN_BODY, livingEntity.getArrowsInBody());
            setEventVariable(action, EventValues.Variable.BEE_STINGER_COOLDOWN, livingEntity.getBeeStingerCooldown());
            setEventVariable(action, EventValues.Variable.NO_DAMAGE_TICKS, livingEntity.getNoDamageTicks());
            setEventVariable(action, EventValues.Variable.MAX_NO_DAMAGE_TICKS, livingEntity.getMaximumNoDamageTicks());
            setEventVariable(action, EventValues.Variable.HEALTH, livingEntity.getHealth());
            setEventVariable(action, EventValues.Variable.MAX_HEALTH, (livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null ? livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : null));
            setEventVariable(action, EventValues.Variable.SHIELD_BLOCKING_DELAY, livingEntity.getShieldBlockingDelay());
            setEventVariable(action, EventValues.Variable.EYE_LOCATION, livingEntity.getEyeLocation());
            setEventVariable(action, EventValues.Variable.LAST_DAMAGE, livingEntity.getLastDamage());
            setEventVariable(action, EventValues.Variable.CAN_PICKUP_ITEM, livingEntity.getCanPickupItems());
            Entity target = livingEntity.getTargetEntity(10);
            Block targetBlock = livingEntity.getTargetBlockExact(10);
            setEventVariable(action, EventValues.Variable.TARGET_ENTITY, target != null ? target.getUniqueId().toString() : null);
            setEventVariable(action, EventValues.Variable.TARGET_BLOCK, targetBlock != null ? targetBlock.getLocation() : null);
        }
        if (action.getEntity() instanceof HumanEntity humanEntity) {
            setEventVariable(action, EventValues.Variable.GAME_MODE, humanEntity.getGameMode().name().toLowerCase());
            setEventVariable(action, EventValues.Variable.HUNGER, humanEntity.getFoodLevel());
            setEventVariable(action, EventValues.Variable.LAST_DEATH_LOCATION, humanEntity.getLastDeathLocation());
            setEventVariable(action, EventValues.Variable.ITEM_IN_MAIN_HAND, humanEntity.getInventory().getItemInMainHand());
            setEventVariable(action, EventValues.Variable.ITEM_IN_OFF_HAND, humanEntity.getInventory().getItemInOffHand());
            setEventVariable(action, EventValues.Variable.HELMET, humanEntity.getInventory().getHelmet());
            setEventVariable(action, EventValues.Variable.CHESTPLATE, humanEntity.getInventory().getChestplate());
            setEventVariable(action, EventValues.Variable.LEGGINGS, humanEntity.getInventory().getLeggings());
            setEventVariable(action, EventValues.Variable.BOOTS, humanEntity.getInventory().getBoots());
        }
        if (action.getEntity() instanceof Player player) {
            setEventVariable(action, EventValues.Variable.CLIENT_BRAND, player.getClientBrandName());
            setEventVariable(action, EventValues.Variable.LOCALE_COUNTRY, player.locale().getCountry());
            setEventVariable(action, EventValues.Variable.LOCALE_DISPLAY_COUNTRY, player.locale().getDisplayCountry());
            setEventVariable(action, EventValues.Variable.LOCALE_LANGUAGE, player.locale().getLanguage());
            setEventVariable(action, EventValues.Variable.LOCALE_DISPLAY_LANGUAGE, player.locale().getDisplayLanguage());
            setEventVariable(action, EventValues.Variable.PING, player.getPing());
            setEventVariable(action, EventValues.Variable.WALK_SPEED, player.getWalkSpeed());
            setEventVariable(action, EventValues.Variable.FLY_SPEED, player.getFlySpeed());
            setEventVariable(action, EventValues.Variable.EXPERIENCE, player.getExp());
            setEventVariable(action, EventValues.Variable.EXPERIENCE_LEVEL, player.getLevel());
            setEventVariable(action, EventValues.Variable.TOTAL_EXPERIENCE, player.getTotalExperience());
            setEventVariable(action, EventValues.Variable.COMPASS_TARGET, player.getCompassTarget());
        }
    }

    public static String parseEntity(String text, ActionsHandler handler, Action action) {
        return Placeholders.getInstance().parseAction(text,handler,action);
    }

    @Override
    public String toString() {
        return path + " - " + type.name() + ": " + value.toString();
    }
}
