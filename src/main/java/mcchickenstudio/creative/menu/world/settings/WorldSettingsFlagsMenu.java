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

package mcchickenstudio.creative.menu.world.settings;

import mcchickenstudio.creative.menu.AbstractMenu;
import mcchickenstudio.creative.menu.buttons.RadioButton;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotFlags;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;

public class WorldSettingsFlagsMenu extends AbstractMenu {

    private final ItemStack BACK_ITEM = createItem(Material.SPECTRAL_ARROW,1,"menus.world-settings-flags.items.back");

    public WorldSettingsFlagsMenu() {
        super((byte) 6, MessageUtils.getLocaleMessage("menus.world-settings.title"));
    }

    public static RadioButton getPlayerDamageFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.PLAYER_DAMAGE,(byte)1));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.PLAYER_DAMAGE,(byte)2));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.PLAYER_DAMAGE,(byte)3));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.PLAYER_DAMAGE,(byte)4));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.PLAYER_DAMAGE,(byte)5));
        return new RadioButton(Material.TOTEM_OF_UNDYING, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.player-damage.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.player-damage.lore"), plot.getFlagValue(PlotFlags.PlotFlag.PLAYER_DAMAGE), 5, choicesActions, "menus.world-settings-flags.items.player-damage.choices", "menus.world-settings-flags");
    }

    public static RadioButton getMobInteractFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.MOB_INTERACT, (byte) 1));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.MOB_INTERACT, (byte) 2));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.MOB_INTERACT, (byte) 3));
        return new RadioButton(Material.VILLAGER_SPAWN_EGG, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.mob-interact.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.mob-interact.lore"), plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT), 3, choicesActions, "menus.world-settings-flags.items.mob-interact.choices", "menus.world-settings-flags");
    }

    public static RadioButton getMobLootFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_MOB_LOOT, true);
            plot.setFlagValue(PlotFlags.PlotFlag.MOB_LOOT,(byte) 1);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_MOB_LOOT, false);
            plot.setFlagValue(PlotFlags.PlotFlag.MOB_LOOT, (byte) 2);
        });
        return new RadioButton(Material.FEATHER, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.mob-loot.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.mob-loot.lore"), plot.getFlagValue(PlotFlags.PlotFlag.MOB_LOOT), 2, choicesActions, "menus.world-settings-flags.items.mob-loot.choices", "menus.world-settings-flags");
    }

    public static RadioButton getKeepInventoryFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.KEEP_INVENTORY, false);
            plot.setFlagValue(PlotFlags.PlotFlag.KEEP_INVENTORY, (byte) 1);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.KEEP_INVENTORY, true);
            plot.setFlagValue(PlotFlags.PlotFlag.KEEP_INVENTORY, (byte) 2);
        });
        return new RadioButton(Material.CHEST_MINECART, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.keep-inventory.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.keep-inventory.lore"), plot.getFlagValue(PlotFlags.PlotFlag.KEEP_INVENTORY), 2, choicesActions, "menus.world-settings-flags.items.keep-inventory.choices", "menus.world-settings-flags");
    }

    public static RadioButton getNaturalRegenerationFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.NATURAL_REGENERATION, true);
            plot.setFlagValue(PlotFlags.PlotFlag.NATURAL_REGENERATION, (byte) 1);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            plot.setFlagValue(PlotFlags.PlotFlag.NATURAL_REGENERATION, (byte) 2);
        });
        return new RadioButton(Material.POTION, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.natural-regeneration.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.natural-regeneration.lore"), plot.getFlagValue(PlotFlags.PlotFlag.NATURAL_REGENERATION), 2, choicesActions, "menus.world-settings-flags.items.natural-regeneration.choices", "menus.world-settings-flags");
    }

    public static RadioButton getBlockChangingFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_CHANGING, (byte) 1));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_CHANGING, (byte) 2));
        return new RadioButton(Material.ICE, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.block-changing.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.block-changing.lore"), plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_CHANGING), 2, choicesActions, "menus.world-settings-flags.items.block-changing.choices", "menus.world-settings-flags");
    }

    public static RadioButton getImmediateRespawnFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
            plot.setFlagValue(PlotFlags.PlotFlag.IMMEDIATE_RESPAWN, (byte) 1);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            plot.setFlagValue(PlotFlags.PlotFlag.IMMEDIATE_RESPAWN, (byte) 2);
        });
        return new RadioButton(Material.SKELETON_SKULL, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.immediate-respawn.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.immediate-respawn.lore"), plot.getFlagValue(PlotFlags.PlotFlag.IMMEDIATE_RESPAWN), 2, choicesActions, "menus.world-settings-flags.items.immediate-respawn.choices", "menus.world-settings-flags");
    }

    public static RadioButton getDeathMessagesFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.DEATH_MESSAGES, (byte) 1));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.DEATH_MESSAGES, (byte) 2));
        return new RadioButton(Material.WITHER_SKELETON_SKULL, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.death-messages.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.death-messages.lore"), plot.getFlagValue(PlotFlags.PlotFlag.DEATH_MESSAGES), 2, choicesActions, "menus.world-settings-flags.items.death-messages.choices", "menus.world-settings-flags");
    }

    public static RadioButton getLikeMessagesFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.LIKE_MESSAGES, (byte) 1));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.LIKE_MESSAGES, (byte) 2));
        return new RadioButton(Material.KNOWLEDGE_BOOK, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.like-messages.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.like-messages.lore"), plot.getFlagValue(PlotFlags.PlotFlag.LIKE_MESSAGES), 2, choicesActions, "menus.world-settings-flags.items.like-messages.choices", "menus.world-settings-flags");
    }

    public static RadioButton getMobSpawnFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            plot.setFlagValue(PlotFlags.PlotFlag.MOB_SPAWN, (byte)1);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
            plot.setFlagValue(PlotFlags.PlotFlag.MOB_SPAWN, (byte)2);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
            plot.setFlagValue(PlotFlags.PlotFlag.MOB_SPAWN, (byte)3);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
            plot.setFlagValue(PlotFlags.PlotFlag.MOB_SPAWN, (byte)4);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
            plot.setFlagValue(PlotFlags.PlotFlag.MOB_SPAWN, (byte)5);
        });
        return new RadioButton(Material.EGG, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.mob-spawn.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.mob-spawn.lore"), plot.getFlagValue(PlotFlags.PlotFlag.MOB_SPAWN), 5, choicesActions, "menus.world-settings-flags.items.mob-spawn.choices", "menus.world-settings-flags");
    }

    public static RadioButton getBlockExplosionFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.MOB_GRIEFING, true);
            plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_EXPLOSION,(byte) 1);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.MOB_GRIEFING, false);
            plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_EXPLOSION,(byte) 2);
        });
        return new RadioButton(Material.TNT, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.block-explosion.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.block-explosion.lore"), plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_EXPLOSION), 2, choicesActions, "menus.world-settings-flags.items.block-explosion.choices", "menus.world-settings-flags");
    }

    public static RadioButton FireSpreadButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_FIRE_TICK, true);
            plot.setFlagValue(PlotFlags.PlotFlag.FIRE_SPREAD,(byte) 1);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_FIRE_TICK, false);
            plot.setFlagValue(PlotFlags.PlotFlag.FIRE_SPREAD,(byte) 2);
        });
        return new RadioButton(Material.CAMPFIRE, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.fire-spread.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.fire-spread.lore"), plot.getFlagValue(PlotFlags.PlotFlag.FIRE_SPREAD), 2, choicesActions, "menus.world-settings-flags.items.fire-spread.choices", "menus.world-settings-flags");
    }

    public static RadioButton getJoinQuitMessagesFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES,(byte) 1));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES,(byte) 2));
        return new RadioButton(Material.OAK_SIGN, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.join-messages.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.join-messages.lore"), plot.getFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES), 2, choicesActions, "menus.world-settings-flags.items.join-messages.choices", "menus.world-settings-flags");
    }

    public static RadioButton getBlockInteractFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_INTERACT, (byte) 1));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_INTERACT, (byte) 2));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_INTERACT, (byte) 3));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_INTERACT, (byte) 4));
        choicesActions.add(() -> plot.setFlagValue(PlotFlags.PlotFlag.BLOCK_INTERACT, (byte) 5));
        return new RadioButton(Material.CHEST, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.block-interact.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.block-interact.lore"), plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_INTERACT), 5, choicesActions, "menus.world-settings-flags.items.block-interact.choices", "menus.world-settings-flags");
    }

    public static RadioButton getWeatherFlagButton(Plot plot) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            plot.world.setStorm(false);
            plot.setFlagValue(PlotFlags.PlotFlag.WEATHER, (byte)1);
        });
        choicesActions.add(() -> {
            plot.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            plot.world.setStorm(true);
            plot.setFlagValue(PlotFlags.PlotFlag.WEATHER, (byte)2);
        });
        choicesActions.add(() -> {
            plot.world.setStorm(false);
            plot.world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
            plot.setFlagValue(PlotFlags.PlotFlag.WEATHER, (byte)3);
        });
        Boolean isWeatherChanging = plot.world.getGameRuleValue(GameRule.DO_WEATHER_CYCLE);
        byte currentValue = (byte) (isWeatherChanging != null && isWeatherChanging ? 3 : plot.world.hasStorm() ? 2 : 1);
        return new RadioButton(Material.WATER_BUCKET, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.weather.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.weather.lore"), currentValue, 3, choicesActions, "menus.world-settings-flags.items.weather.choices", "menus.world-settings-flags");
    }

    @Override
    public void fillItems(Player player) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        setItem((byte) 46,BACK_ITEM);
        if (plot == null) return;
        if (!plot.isOwner(player.getName())) return;
        setItem((byte) 10, getPlayerDamageFlagButton(plot).getButtonItem());
        setItem((byte) 11, getBlockExplosionFlagButton(plot).getButtonItem());
        setItem((byte) 12, getBlockInteractFlagButton(plot).getButtonItem());
        setItem((byte) 13, FireSpreadButton(plot).getButtonItem());
        setItem((byte) 14, getMobInteractFlagButton(plot).getButtonItem());
        setItem((byte) 15, getWeatherFlagButton(plot).getButtonItem());
        setItem((byte) 16, getKeepInventoryFlagButton(plot).getButtonItem());
        setItem((byte) 19, getMobSpawnFlagButton(plot).getButtonItem());
        setItem((byte) 20, getImmediateRespawnFlagButton(plot).getButtonItem());
        setItem((byte) 21, getJoinQuitMessagesFlagButton(plot).getButtonItem());
        setItem((byte) 22, getDeathMessagesFlagButton(plot).getButtonItem());
        setItem((byte) 23, getLikeMessagesFlagButton(plot).getButtonItem());
        setItem((byte) 24, getBlockChangingFlagButton(plot).getButtonItem());
        setItem((byte) 25, getNaturalRegenerationFlagButton(plot).getButtonItem());
        setItem((byte) 28, getMobLootFlagButton(plot).getButtonItem());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!isClickedInMenuSlots(event)) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;

        Plot plot = PlotManager.getInstance().getPlotByPlayer((Player) event.getWhoClicked());
        if (event.getCurrentItem().getType() == Material.SPECTRAL_ARROW) {
            WorldSettingsMenu.openInventory((Player) event.getWhoClicked());
        } else if (event.getCurrentItem().getType() != Material.AIR) {
            if (plot == null) return;
            RadioButton rd = RadioButton.getRadioButtonByItemStack(event.getCurrentItem());
            if (rd != null) {
                rd.onChoice();
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 100, 1f);
                new WorldSettingsFlagsMenu().open((Player) event.getWhoClicked());
            }
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}