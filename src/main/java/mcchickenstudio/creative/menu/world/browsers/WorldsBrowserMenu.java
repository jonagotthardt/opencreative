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

package mcchickenstudio.creative.menu.world.browsers;

import mcchickenstudio.creative.menu.AbstractListMenu;
import mcchickenstudio.creative.menu.buttons.ParameterButton;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.ItemUtils.getWorldIdKey;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menu, that displays specified list of worlds.
 * Player can sort worlds and change pages.
 */
public class WorldsBrowserMenu extends AbstractListMenu {

    private final List<Plot> plots;
    private final List<ParameterButton> buttons = new ArrayList<>();
    private final ItemStack RECOMMENDED = createItem(Material.WIND_CHARGE,1,"menus.all-worlds.items.recommended");
    private byte sortType = 1;

    public WorldsBrowserMenu(Player player, List<Plot> plots) {
        super(getLocaleMessage("menus.all-worlds.title",false), player);
        this.plots = new ArrayList<>(plots);
        Comparator<Plot> sortByOnline = (plot1, plot2) -> Integer.compare(plot2.getOnline(), plot1.getOnline());
        this.plots.sort(sortByOnline);
        itemsSlots = new byte[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        noElementsPageButtonSlot = 13;
        decorationSlots = new byte[]{45,46,47,51,52,53};
        charmsBarSlots = new byte[]{45,48,50};
        previousPageButtonSlot = 45;
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof Plot plot) {
            return plot.getInformation().getIcon();
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
                        elements.addAll(new ArrayList<>(plots).stream().filter(plot -> plot.getInformation().getCategory().name().equalsIgnoreCase(button.getCurrentValue().toString())).toList());
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
        Plot plot = PlotManager.getInstance().getPlotByCustomID(worldID);
        if (plot != null) {
            player.closeInventory();
            plot.teleportPlayer(player);
        }
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
        Comparator<Object> plotComparator = switch (sortType) {
            case 2 -> (plot1, plot2) -> Integer.compare(((Plot) plot2).getReputation(), ((Plot) plot1).getReputation());
            case 3 -> (plot1, plot2) -> Long.compare(((Plot) plot2).getCreationTime(), ((Plot) plot1).getCreationTime());
            default -> (plot1, plot2) -> Integer.compare(((Plot) plot2).getOnline(), ((Plot) plot1).getOnline());
        };
        elements.sort(plotComparator);
    }

    @Override
    protected List<Object> getElements() {
        return new ArrayList<>(plots);
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
