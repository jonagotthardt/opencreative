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

package ua.mcchickenstudio.opencreative.menu.world.settings;

import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetInfo;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static org.bukkit.Material.*;

public class WorldSettingsCategoryMenu extends AbstractMenu {

    private final PlanetInfo.Category currentCategory;
    private final ItemStack BACK_ITEM = createItem(SPECTRAL_ARROW,1,"menus.world-settings-categories.items.back");

    public WorldSettingsCategoryMenu(PlanetInfo.Category currentCategory) {
        super((byte) 6, MessageUtils.getLocaleMessage("menus.world-settings.title"));
        this.currentCategory = currentCategory;
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 10,createButton(PlanetInfo.Category.SANDBOX));
        setItem((byte) 12,createButton(PlanetInfo.Category.ADVENTURE));
        setItem((byte) 14,createButton(PlanetInfo.Category.STRATEGY));
        setItem((byte) 16,createButton(PlanetInfo.Category.ARCADE));
        setItem((byte) 28,createButton(PlanetInfo.Category.ROLEPLAY));
        setItem((byte) 30,createButton(PlanetInfo.Category.STORY));
        setItem((byte) 32,createButton(PlanetInfo.Category.SIMULATOR));
        setItem((byte) 34,createButton(PlanetInfo.Category.EXPERIMENT));
        setItem((byte) 45,BACK_ITEM);
        setItem((byte) 46,DECORATION_PANE_ITEM);
        setItem((byte) 47,new ItemStack(GREEN_STAINED_GLASS_PANE));
        setItem((byte) 49,setPersistentData(
                createItem(currentCategory.getMaterial(),1,
                        "menus.world-settings-categories.items." +
                                currentCategory.name().toLowerCase()),
                getItemTypeKey(),currentCategory.name()));
        setItem((byte) 51,new ItemStack(GREEN_STAINED_GLASS_PANE));
        setItem((byte) 52,DECORATION_PANE_ITEM);
        setItem((byte) 53,DECORATION_PANE_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!isPlayerClicked(event)) return;
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer((Player) event.getWhoClicked());
        if (planet == null) return;
        if (!planet.isOwner(event.getWhoClicked().getName())) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (getItems().contains(event.getCurrentItem())) {
            if (!itemEquals(event.getCurrentItem(),BACK_ITEM)) {
                String categoryString = getPersistentData(event.getCurrentItem(),getItemTypeKey());
                if (categoryString.isEmpty()) return;
                try {
                    PlanetInfo.Category category = PlanetInfo.Category.valueOf(categoryString);
                    planet.getInformation().setCategory(category);
                    event.getWhoClicked().sendMessage(getLocaleMessage("settings.world-category.changed").replace("%category%",category.getLocaleName()));
                } catch (IllegalArgumentException ignored) {
                    return;
                }
                event.getWhoClicked().closeInventory();
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_PLAYER_LEVELUP,100,1.6f);
            } else {
                new WorldSettingsMenu(planet,(Player) event.getWhoClicked()).open((Player) event.getWhoClicked());
            }
        }
    }

    private ItemStack createButton(PlanetInfo.Category category) {
        if (category == currentCategory) return DECORATION_ITEM;
        return setPersistentData(
                createItem(category.getMaterial(),1,
                        "menus.world-settings-categories.items." +
                                category.name().toLowerCase()),
                getItemTypeKey(),category.name());
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {}
}
