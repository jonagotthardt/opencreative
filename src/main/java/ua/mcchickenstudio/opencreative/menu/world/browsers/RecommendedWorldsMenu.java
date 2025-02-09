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

package ua.mcchickenstudio.opencreative.menu.world.browsers;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.listeners.player.ChatListener;
import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.PlayerConfirmation;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class RecommendedWorldsMenu extends AbstractMenu {

    private final int[] featuredWorldsSlots = {10,11,12,13,14,15,16, 19,20,21,22,23,24,25, 28,29,30,31,32,33,34};
    private final ItemStack OWN_WORLDS = createItem(Material.REPEATING_COMMAND_BLOCK,1,"menus.all-worlds.items.own-worlds");
    private final ItemStack DECORATION_OWN_WORLDS = createItem(Material.PURPLE_STAINED_GLASS_PANE,1);
    private final ItemStack SEARCH = createItem(Material.SPYGLASS,1,"menus.all-worlds.items.search");
    private final ItemStack ALL_WORLDS = createItem(Material.SPECTRAL_ARROW,1,"menus.recommended-worlds.items.all-worlds");

    public RecommendedWorldsMenu() {
        super(6, getLocaleMessage("menus.recommended-worlds.title",false));
    }

    @Override
    public void fillItems(Player player) {
        setItem(45, SEARCH);
        setItem(46, DECORATION_PANE_ITEM);
        setItem(47, DECORATION_OWN_WORLDS);
        setItem(49, OWN_WORLDS);
        setItem(51, DECORATION_OWN_WORLDS);
        setItem(52, DECORATION_PANE_ITEM);
        setItem(53, ALL_WORLDS);
        List<Planet> featuredPlanets = PlanetManager.getInstance().getRecommendedPlanets();
        if (featuredPlanets.isEmpty()) {
            for (int slot : featuredWorldsSlots) {
                setItem(slot,DECORATION_ITEM);
            }
            return;
        }
        Collections.shuffle(featuredPlanets);
        int index = 0;
        for (int slot : featuredWorldsSlots) {
            if (index < featuredPlanets.size()) {
                setItem(slot, featuredPlanets.get(index).getInformation().getIcon());
                index++;
            } else {
                setItem(slot,DECORATION_ITEM);
            }
        }
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        if (itemEquals(currentItem,SEARCH)) {
            PlayerConfirmation request = switch (event.getClick()) {
                case LEFT -> PlayerConfirmation.FIND_PLANETS_BY_NAME;
                case RIGHT, SHIFT_RIGHT -> PlayerConfirmation.FIND_PLANETS_BY_ID;
                case SHIFT_LEFT -> PlayerConfirmation.FIND_PLANETS_BY_OWNER;
                default -> null;
            };
            if (request == null) return;
            player.closeInventory();
            player.updateInventory();
            String searchQuery = request == PlayerConfirmation.FIND_PLANETS_BY_NAME ? "world-name" : request == PlayerConfirmation.FIND_PLANETS_BY_ID ? "id" : "owner";
            player.showTitle(Title.title(
                    Component.text(
                            getLocaleMessage("menus.all-worlds.items.search.title")
                                    .replace("%search%", getLocaleMessage("menus.all-worlds.items.search." + searchQuery))),
                    Component.text(
                            getLocaleMessage("menus.all-worlds.items.search.subtitle")
                                    .replace("%search%", getLocaleMessage("menus.all-worlds.items.search." + searchQuery))),
                    Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(20), Duration.ofMillis(750))
            ));
            player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.usage", player).replace("%search%", getLocaleMessage("menus.all-worlds.items.search." + searchQuery)));
            Sounds.MENU_WORLD_SEARCH.play(player);
            ChatListener.confirmation.put(player,request);
        } else if (itemEquals(currentItem,ALL_WORLDS)) {
            new WorldsBrowserMenu(player, PlanetManager.getInstance().getPlanets()).open(player);
        } else if (itemEquals(currentItem,OWN_WORLDS)) {
            new OwnWorldsBrowserMenu(player).open(player);
        } else if (!itemEquals(currentItem,DECORATION_ITEM) && Arrays.stream(featuredWorldsSlots).anyMatch(i -> i == event.getRawSlot())) {
            String worldID = getPersistentData(currentItem, getItemIdKey());
            if (worldID.isEmpty()) {
                return;
            }
            Planet planet = PlanetManager.getInstance().getPlanetByCustomID(worldID);
            if (planet != null) {
                player.closeInventory();
                planet.connectPlayer(player);
            }
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_RECOMMENDATIONS.play(event.getPlayer());
    }
}
