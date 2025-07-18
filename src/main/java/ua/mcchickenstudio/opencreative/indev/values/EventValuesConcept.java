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

package ua.mcchickenstudio.opencreative.indev.values;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.entity.*;
import ua.mcchickenstudio.opencreative.indev.values.events.*;
import ua.mcchickenstudio.opencreative.indev.values.human.*;
import ua.mcchickenstudio.opencreative.indev.values.living.*;
import ua.mcchickenstudio.opencreative.indev.values.player.*;
import ua.mcchickenstudio.opencreative.indev.values.world.*;

public class EventValuesConcept {
    
    private static EventValuesConcept instance;
    private final List<EventValueTest> eventValues = new LinkedList<>();

    /**
     * Returns instance of event values controller class.
     * @return instance of event values.
     */
    public synchronized static EventValuesConcept getInstance() {
        if (instance == null) {
            instance = new EventValuesConcept();
            instance.registerDefaults();
        }
        return instance;
    }

    /**
     * Registers event value, that will be replaced in coding.
     * @param value event value to register.
     */
    public void registerEventValue(@NotNull EventValueTest value) {
        sendDebug("[VALUES] Registered " + value);
        eventValues.add(value);
    }

    /**
     * Registers event values, that will be replaced in coding.
     * @param values event values to register.
     */
    public void registerEventValue(@NotNull EventValueTest... values) {
        for (EventValueTest value : values) {
            registerEventValue(value);
        }
    }

    /**
     * Unregisters event value if list contains it.
     * @param value event value to unregister.
     */
    public void unregisterEventValue(@NotNull EventValueTest value) {
        eventValues.remove(value);
    }

    /**
     * Returns a copy of list that contains all registered event values.
     * @return event values list.
     */
    public @NotNull List<EventValueTest> getEventValueTests() {
        return new ArrayList<>(eventValues);
    }

    private void registerDefaults() {
        registerEventValue(new WorldNameValue(), new WorldDescriptionValue(), new WorldOnlineValue(),
                new WorldCustomIdValue(), new WorldSpawnValue(), new WorldIdValue(), new WorldIconValue(),
                new WorldRatingValue(), new WorldEntitiesAmountValue(), new WorldEntitiesAmountLimitValue(),
                new WorldLastRedstoneOperationsValue(), new WorldRedstoneOperationsLimitValue(),
                new WorldVariablesAmountValue(), new WorldVariablesAmountLimitValue(), new WorldTimeValue(),
                new ClearWeatherDurationValue(), new ThunderWeatherDurationValue(), new UnixTimeValue(),
                new UnixTimeHoursValue(), new UnixTimeMinutesValue(), new UnixTimeSecondsValue());
        registerEventValue(new BedEnterResultValue(), new BedEventValue(), new EventItemValue(),
                new EventNewItemValue(), new BlockInteractionTypeValue(), new CursorItemValue(),
                new ClickTypeValue(), new ClickedSlotValue(), new OldSlotValue(), new NewSlotValue(),
                new ChatMessageValue(), new BlockMaterialValue(), new BlockLocationValue(), new TransferKeyValue(),
                new TransferVariableValue(), new WebUrlValue(), new WebResponseCodeValue(), new WebResponseValue(),
                new PurchaseIdValue(), new PurchaseNameValue(), new PurchasePriceValue(), new PurchaseSaveValue(),
                new FoodLevelValue(), new DamageCauseValue(), new DamageValue());
        registerEventValue(new ClientBrandValue(), new HumanHungerValue(), new HumanLastDeathLocationValue(),
                new PlayerPingValue(), new PlayerExperienceValue(), new PlayerExperienceValue(), new PlayerTotalExperienceValue(),
                new HumanGameModeValue(), new LocaleCountryValue(), new LocaleDisplayCountryValue(), new LocaleLanguageValue(),
                new LocaleDisplayLanguageValue(), new PlayerCompassTargetValue());
        registerEventValue(new EntityNameValue(), new EntityTypeValue(), new EntityUUIDValue(), new EntityLocationValue(),
                new EntityVelocityValue(), new HumanItemInMainHandValue(), new HumanItemInOffHandValue(), new HumanHelmetValue(),
                new HumanChestplateValue(), new HumanLeggingsValue(), new HumanBootsValue(), new LivingHealthValue(), new EyeLocationValue(),
                new TargetEntityValue(), new TargetBlockValue(), new PlayerWalkSpeedValue(), new PlayerFlightSpeedValue(),
                new LivingLastDamageValue(), new EntityLastDamageCauseValue(), new LivingMaxHealthValue(),
                new EntityFallDistanceValue(), new LivingNoDamageTicksValue(), new EntityFireTicksValue(),
                new EntityFreezeTicksValue(), new LivingMaxNoDamageTicksValue(), new LivingCanPickupItemValue(),
                new LivingArrowsInBodyValue(), new LivingShieldBlockingDelayValue(), new LivingBeeStingerCooldownValue(),
                new LivingRemainingAirValue(), new LivingMaximumAirValue());
    }

    public @NotNull List<EventValueTest> getByCategories(@NotNull MenusCategory menusCategory) {
        List<EventValueTest> list = new ArrayList<>();
        for (EventValueTest name : eventValues) {
            if (name.getCategory() == menusCategory) {
                list.add(name);
            }
        }
        return list;
    }

    public boolean exists(@NotNull String name) {
        return getByName(name) != null;
    }

    public boolean exists(@NotNull Class<? extends EventValueTest> clazz) {
        return getByClass(clazz) != null;
    }

    public @Nullable EventValueTest getByClass(@NotNull Class<? extends EventValueTest> clazz) {
        for (EventValueTest eventValue : eventValues) {
            if (eventValue.getClass().equals(clazz)) {
                return eventValue;
            }
        }
        return null;
    }

    public @Nullable EventValueTest getByName(@NotNull String name) {
        for (EventValueTest eventValue : eventValues) {
            if (eventValue.getName().equals(name)) {
                return eventValue;
            }
        }
        return null;
    }

    public @Nullable Object getValue(@NotNull String name, @NotNull ActionsHandler handler, @NotNull Action action) {
        EventValueTest value = getByName(name);
        if (value == null) return null;
        return value.getValue(handler, action);
    }

    public @Nullable Object getValue(@NotNull Class<? extends EventValueTest> clazz, @NotNull ActionsHandler handler, @NotNull Action action) {
        EventValueTest value = getByClass(clazz);
        if (value == null) return null;
        return value.getValue(handler, action);
    }

}
