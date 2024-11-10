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
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotInfo;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static org.bukkit.Material.*;

public class WorldSettingsCategoryMenu extends AbstractMenu {

    private final ItemStack BACK_ITEM = createItem(SPECTRAL_ARROW,1,"menus.world-settings-categories.items.back");
    private final ItemStack CATEGORY_SANDBOX_ITEM = createItem(BRICKS,1,"menus.world-settings-categories.items.sandbox");
    private final ItemStack CATEGORY_ADVENTURE_ITEM = createItem(LEATHER_BOOTS,1,"menus.world-settings-categories.items.adventure");
    private final ItemStack CATEGORY_STRATEGY_ITEM = createItem(SHIELD,1,"menus.world-settings-categories.items.strategy");
    private final ItemStack CATEGORY_ARCADE_ITEM = createItem(REPEATING_COMMAND_BLOCK,1,"menus.world-settings-categories.items.arcade");
    private final ItemStack CATEGORY_ROLEPLAY_ITEM = createItem(CHEST_MINECART,1,"menus.world-settings-categories.items.roleplay");
    private final ItemStack CATEGORY_STORY_ITEM = createItem(WRITABLE_BOOK,1,"menus.world-settings-categories.items.story");
    private final ItemStack CATEGORY_SIMULATOR_ITEM = createItem(GOLDEN_PICKAXE,1,"menus.world-settings-categories.items.simulator");
    private final ItemStack CATEGORY_EXPERIMENT_ITEM = createItem(TNT,1,"menus.world-settings-categories.items.experiment");

    public WorldSettingsCategoryMenu() {
        super((byte) 6, MessageUtils.getLocaleMessage("menus.world-settings.title"));
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 10,CATEGORY_SANDBOX_ITEM);
        setItem((byte) 11,CATEGORY_ADVENTURE_ITEM);
        setItem((byte) 12,CATEGORY_STRATEGY_ITEM);
        setItem((byte) 13,CATEGORY_ARCADE_ITEM);
        setItem((byte) 14,CATEGORY_ROLEPLAY_ITEM);
        setItem((byte) 15,CATEGORY_STORY_ITEM);
        setItem((byte) 16,CATEGORY_SIMULATOR_ITEM);
        setItem((byte) 19,CATEGORY_EXPERIMENT_ITEM);
        setItem((byte) 46,BACK_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!isPlayerClicked(event)) return;
        Plot plot = PlotManager.getInstance().getPlotByPlayer((Player) event.getWhoClicked());
        if (plot == null) return;
        if (!plot.isOwner(event.getWhoClicked().getName())) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (getItems().contains(event.getCurrentItem())) {
            if (!itemEquals(event.getCurrentItem(),BACK_ITEM)) {
                final String category = MessageUtils.getPathFromMessage("menus.world-settings-categories.items",event.getCurrentItem().getItemMeta().getDisplayName()).replace("menus.world-settings-categories.items.","").replace(".name","").toUpperCase();
                event.getWhoClicked().closeInventory();
                plot.getInformation().setCategory(PlotInfo.Category.valueOf(category));
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_PLAYER_LEVELUP,100,1.6f);
                event.getWhoClicked().sendMessage(getLocaleMessage("settings.world-category.changed").replace("%category%",getLocaleMessage("world.categories." + category.toLowerCase())));
            } else {
                new WorldSettingsMenu(plot,(Player) event.getWhoClicked()).open((Player) event.getWhoClicked());
            }
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {}
}
