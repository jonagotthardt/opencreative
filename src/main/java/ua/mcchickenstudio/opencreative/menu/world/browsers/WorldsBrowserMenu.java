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

import ua.mcchickenstudio.opencreative.menu.AbstractListMenu;
import ua.mcchickenstudio.opencreative.menu.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menu, that displays specified list of worlds.
 * Player can sort worlds and change pages.
 */
public class WorldsBrowserMenu extends AbstractListMenu {

    private final List<Planet> planets;
    private final List<ParameterButton> buttons = new ArrayList<>();
    private final ItemStack RECOMMENDED;
    private byte sortType = 1;

    public WorldsBrowserMenu(Player player, Set<Planet> planets) {
        super(getLocaleMessage("menus.all-worlds.title",false), player);
        this.planets = new ArrayList<>(planets);
        Comparator<Planet> sortByOnline = (planet1, planet2) -> Integer.compare(planet2.getOnline(), planet1.getOnline());
        this.planets.sort(sortByOnline);
        itemsSlots = new byte[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        noElementsPageButtonSlot = 13;
        decorationSlots = new byte[]{45,46,47,51,52,53};
        charmsBarSlots = new byte[]{45,48,50};
        previousPageButtonSlot = 45;
        RECOMMENDED = createItem(Material.WIND_CHARGE,1,"menus.all-worlds.items.recommended");
    }

    public WorldsBrowserMenu(Player player, Set<Planet> planets, boolean withRecommendedButton) {
        super(getLocaleMessage("menus.all-worlds.title",false), player);
        this.planets = new ArrayList<>(planets);
        Comparator<Planet> sortByOnline = (planet1, planet2) -> Integer.compare(planet2.getOnline(), planet1.getOnline());
        this.planets.sort(sortByOnline);
        itemsSlots = new byte[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        noElementsPageButtonSlot = 13;
        decorationSlots = new byte[]{45,46,47,51,52,53};
        charmsBarSlots = new byte[]{45,48,50};
        previousPageButtonSlot = 45;
        RECOMMENDED = withRecommendedButton ? createItem(Material.WIND_CHARGE,1,"menus.all-worlds.items.recommended") : DECORATION_ITEM;
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof Planet planet) {
            return planet.getInformation().getIcon();
        }
        return null;
    }

    @Override
    protected void fillOtherItems() {
        ParameterButton sort = new ParameterButton(
                "online",
                List.of("online","likes","last"),
                "sort",
                "menus.all-worlds",
                "menus.all-worlds.items.sort",
                List.of(Material.HOPPER, Material.GOLDEN_APPLE, Material.CLOCK)
        );
        ParameterButton category = new ParameterButton(
                "online",
                List.of("all","sandbox","adventure","arcade","roleplay","simulator","experiment","story","strategy"),
                "category",
                "menus.all-worlds",
                "menus.all-worlds.items.category",
                List.of(Material.CHERRY_CHEST_BOAT, Material.SANDSTONE, Material.DARK_PRISMARINE_STAIRS, Material.TARGET, Material.AXOLOTL_BUCKET, Material.CAMPFIRE, Material.TNT, Material.WRITABLE_BOOK, Material.CROSSBOW)
        );
        buttons.add(sort);
        buttons.add(category);
        setItem((byte) 45,RECOMMENDED);
        setItem((byte) 48,category.getItem());
        setItem((byte) 50,sort.getItem());
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        for (ParameterButton button : buttons) {
            if (itemEquals(item,button.getItem(true))) {
                if (event.getRawSlot() == 50) {
                    button.next();
                    sortType = button.getCurrentChoice();
                    sortElements();
                    fillElements(getCurrentPage());
                    fillArrowsItems(getCurrentPage());
                    setItem((byte) 50, button.getItem());
                    updateSlot((byte) 50);
                    player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_SPAWN_ITEM,100,0.6F);
                } else if (event.getRawSlot() == 48) {
                    button.next();
                    elements.clear();
                    if (button.getCurrentValue().equals("all")) {
                        elements.addAll(getElements());
                    } else {
                        elements.addAll(new ArrayList<>(planets).stream().filter(planet -> planet.getInformation().getCategory().name().equalsIgnoreCase(button.getCurrentValue().toString())).toList());
                    }
                    sortElements();
                    fillElements(getCurrentPage());
                    fillArrowsItems(getCurrentPage());
                    setItem((byte) 48, button.getItem());
                    updateSlot((byte) 48);
                    player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER,100,1.2F);
                }
            }
        }
        if (itemEquals(item,RECOMMENDED)) {
            new RecommendedWorldsMenu().open(player);
        }

    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        String worldID = getPersistentData(currentItem,getWorldIdKey());
        if (worldID.isEmpty()) {
            return;
        }
        Planet planet = PlanetManager.getInstance().getPlanetByCustomID(worldID);
        if (planet != null) {
            onPlanetClick(player, planet);
        }
    }

    protected void onPlanetClick(Player player, Planet planet) {
        player.closeInventory();
        planet.connectPlayer(player);
    }

    @Override
    protected void fillArrowsItems(byte currentPage) {
        if (elements.isEmpty()) {
            setItem(noElementsPageButtonSlot, getNoElementsButton());
            setItem(previousPageButtonSlot, RECOMMENDED);
            setItem(nextPageButtonSlot, DECORATION_ITEM);
            updateSlot(noElementsPageButtonSlot);
            updateSlot(previousPageButtonSlot);
            updateSlot(nextPageButtonSlot);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            setItem(previousPageButtonSlot,currentPage > 1 ? getPreviousPageButton() : RECOMMENDED);
            updateSlot(previousPageButtonSlot);
            setItem(nextPageButtonSlot,currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_ITEM);
            updateSlot(nextPageButtonSlot);
        }
    }

    private void sortElements() {
        Comparator<Object> planetComparator = switch (sortType) {
            case 2 -> (planet1, planet2) -> Integer.compare(((Planet) planet2).getInformation().getReputation(), ((Planet) planet1).getInformation().getReputation());
            case 3 -> (planet1, planet2) -> Long.compare(((Planet) planet2).getCreationTime(), ((Planet) planet1).getCreationTime());
            default -> (planet1, planet2) -> Integer.compare(((Planet) planet2).getOnline(), ((Planet) planet1).getOnline());
        };
        elements.sort(planetComparator);
    }

    @Override
    protected List<Object> getElements() {
        return new ArrayList<>(planets);
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInLore(createItem(Material.SPECTRAL_ARROW,getCurrentPage()+1,"menus.all-worlds.items.next-page"),"%page%",getCurrentPage()+1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInLore(createItem(Material.SPECTRAL_ARROW,Math.max(1, getCurrentPage()-1),"menus.all-worlds.items.previous-page"),"%page%",getCurrentPage()-1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER,1,"menus.all-worlds.items.no-worlds");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        player.playSound(player.getLocation(),Sound.BLOCK_VAULT_ACTIVATE,100,1);
    }

}
