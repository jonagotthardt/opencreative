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

import mcchickenstudio.creative.menu.LegacyMenu;
import mcchickenstudio.creative.menu.buttons.RadioButton;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotFlags;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.FileUtils;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcchickenstudio.creative.utils.ItemUtils.clearItemFlags;
import static mcchickenstudio.creative.utils.ItemUtils.createItem;

public class WorldSettingsMenu extends LegacyMenu {

    // Открытие меню

    public WorldSettingsMenu(Player player) {

        super(6, MessageUtils.getLocaleMessage("menus.world-settings.title"));

        Map<Integer,ItemStack> items = new HashMap<>();

        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        items.put(40,getPlotExampleButton(plot));
        items.put(10,getPlotNameButton());
        items.put(11,getPlotDescriptionButton());
        items.put(12,getPlotIconButton());
        items.put(16,getPlotSpawnButton());
        items.put(21,getPlotSharingButton(plot));
        items.put(19,getPlotCustomIDButton());
        items.put(14,getPlotFlagsButton());
        items.put(15,getPlotPlayersButton());
        items.put(20,getPlotCategoryButton());
        items.put(23,getBuildModeButton());
        items.put(24,getPlayModeButton());
        items.put(25,getDevModeButton());
        items.put(37,getDayAndNightFlagButton(plot).getButtonItem());
        items.put(43,getAdvertisementButton());

        this.setItems(items);
    }

    public static void openInventory(Player player) {
        player.openInventory(new WorldSettingsMenu(player).getInventory());    }

    public static ItemStack getPlotExampleButton(Plot plot) {
        ItemStack item = new ItemStack(plot.getInformation().getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings.items.world.name").replace("%plotName%", plot.getInformation().getDisplayName()));
        List<String> lore = new ArrayList<>();
        for (String loreLine : MessageUtils.getLocaleItemDescription("menus.world-settings.items.world.lore")) {
            if (loreLine.contains("%plotDescription%")) {
                String[] newLines = plot.getInformation().getDescription().split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%plotDescription%",ChatColor.translateAlternateColorCodes('&',newLine)));
                }
            } else {
                lore.add(MessageUtils.parsePlotLines(plot,loreLine.replace("%id%", MessageUtils.getLocaleMessage("menus.world-settings.items.world.id",false) + plot.getInformation().getCustomID())));
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        clearItemFlags(item);
        return item;
    }

    public static ItemStack getPlotNameButton() {
        return createItem(Material.NAME_TAG,1,"menus.world-settings.items.change-name");
    }

    public static ItemStack getPlotDescriptionButton() {
        return createItem(Material.BOOK,1,"menus.world-settings.items.change-description");
    }

    public static ItemStack getPlotIconButton() {
        return createItem(Material.EMERALD,1,"menus.world-settings.items.change-icon");
    }

    public static ItemStack getPlotCategoryButton() {
        return createItem(Material.BOOKSHELF,1,"menus.world-settings.items.change-category");
    }

    public static ItemStack getPlotCustomIDButton() {
        return createItem(Material.LEAD,1,"menus.world-settings.items.change-id");
    }

    public static ItemStack getPlotSpawnButton() {
        return createItem(Material.ENDER_PEARL,1,"menus.world-settings.items.change-spawn");
    }

    public static ItemStack getPlayModeButton() {
        return createItem(Material.DIAMOND_BLOCK,1,"menus.world-settings.items.play-mode");
    }

    public static ItemStack getBuildModeButton() {
        return createItem(Material.BRICKS,1,"menus.world-settings.items.build-mode");
    }

    public static ItemStack getDevModeButton() {
        return createItem(Material.COMMAND_BLOCK,1,"menus.world-settings.items.dev-mode");
    }

    public static ItemStack getPlotFlagsButton() {
        return createItem(Material.PISTON,1,"menus.world-settings.items.plot-flags");
    }

    public static ItemStack getPlotPlayersButton() {
        return createItem(Material.PLAYER_HEAD,1,"menus.world-settings.items.plot-players");
    }

    public static ItemStack getAdvertisementButton() {
        return createItem(Material.BEACON,1,"menus.world-settings.items.advertisement");
    }

    public static ItemStack getPlotSharingButton(Plot plot) {
        boolean isPublic = plot.getPlotSharing() == Plot.Sharing.PUBLIC;
        Material material = Material.OAK_DOOR;
        if (!isPublic) material = Material.IRON_DOOR;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings.items.change-sharing.name"));
        List<String> lore = new ArrayList<>();

        String sharing1 = MessageUtils.getLocaleMessage("menus.world-settings.items.change-sharing.sharing1",false);
        String sharing2 = MessageUtils.getLocaleMessage("menus.world-settings.items.change-sharing.sharing2",false);

        String turnedOn = MessageUtils.getLocaleMessage("menus.world-settings.turned-on",false);
        String turnedOnNegative = MessageUtils.getLocaleMessage("menus.world-settings.turned-on-negative",false);
        String turnedOff = MessageUtils.getLocaleMessage("menus.world-settings.turned-off",false);

        if (isPublic) {
            sharing1 = turnedOn + sharing1;
            sharing2 = turnedOff + sharing2;
        } else {
            sharing1 = turnedOff + sharing1;
            sharing2 = turnedOnNegative + sharing2;
        }

        for (String loreLine : MessageUtils.getLocaleItemDescription("menus.world-settings.items.change-sharing.lore")) {
            lore.add(loreLine.replace("%sharing1%",sharing1).replace("%sharing2%",sharing2));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;    }

    public static RadioButton getDayAndNightFlagButton(Plot plot) {
        ArrayList<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            plot.world.setTime(1000L);
            plot.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            plot.setFlagValue(PlotFlags.PlotFlag.DAY_CYCLE, (byte) 1);
        }
        );
        choicesActions.add(() -> {
            plot.world.setTime(12500L);
            plot.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            plot.setFlagValue(PlotFlags.PlotFlag.DAY_CYCLE, (byte) 2);
        });
        choicesActions.add(() -> {
            plot.world.setTime(15000L);
            plot.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            plot.setFlagValue(PlotFlags.PlotFlag.DAY_CYCLE, (byte) 3);
        });
        choicesActions.add(() -> {
            plot.world.setTime(1000L);
            plot.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            plot.setFlagValue(PlotFlags.PlotFlag.DAY_CYCLE, (byte) 4);
            FileUtils.setPlotConfigParameter(plot, "flags.day-cycle", 4);
        });
        Boolean isTimeChanging = plot.world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
        long currentTime = plot.world.getTime();
        boolean isNight = currentTime >= 15000L && currentTime <= 23000;
        boolean isEvening = currentTime >= 12500 && currentTime < 15000;
        byte currentValue = (byte) (isTimeChanging != null && isTimeChanging ? 4 : isNight ? 3 : isEvening ? 2 : 1);
        return new RadioButton(Material.CLOCK, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.day-and-night.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.day-and-night.lore"), currentValue, 4, choicesActions, "menus.world-settings-flags.items.day-and-night.choices", "menus.world-settings-flags");    }

}
