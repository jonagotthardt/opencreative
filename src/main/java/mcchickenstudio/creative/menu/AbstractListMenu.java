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

package mcchickenstudio.creative.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;

/**
 * <h1>AbstractListMenu</h1>
 * This class represents a AbstractMenu that has scrollable list
 * of elements. It creates pages and arrows to change current page.
 * @see mcchickenstudio.creative.menu.AbstractMenu
 */
public abstract class AbstractListMenu extends AbstractMenu {

    protected final Player player;
    protected Inventory inventory;
    protected byte currentPage;

    protected final List<Object> elements = getElements();

    protected static byte previousPageButtonSlot = 47;
    protected static byte nextPageButtonSlot = 53;
    protected static byte noElementsPageButtonSlot = 23;

    protected static byte[] charmsBarSlots = {0,9,18,27,36,45};
    protected static byte[] decorationSlots = {1,10,19,28,37,46};
    protected static byte[] itemsSlots = {12,13,14,15,16,21,22,23,24,25,30,31,32,33,34,39,40,41,42,43};

    public AbstractListMenu(String title, Player player) {
        super((byte) 6, title);
        this.player = player;
        this.currentPage = 1;
    }

    @Override
    public void fillItems(Player player) {
        fillDecorationItems();
        fillElements(getCurrentPage());
        fillArrowsItems(getCurrentPage());
        fillOtherItems();
    }

    protected void fillDecorationItems() {
        for (byte slot : decorationSlots) {
            setItem(slot,DECORATION_PANE_ITEM);
        }
    }

    protected void fillArrowsItems(byte currentPage) {
        if (elements.isEmpty()) {
            setItem(noElementsPageButtonSlot,getNoElementsButton());
        } else {
            byte maxPagesAmount = (byte) dividePagesByElements(elements).size();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            if (currentPage > 1) {
                setItem(previousPageButtonSlot,getPreviousPageButton());
                updateSlot(previousPageButtonSlot);
            }
            if (currentPage < maxPagesAmount) {
                setItem(nextPageButtonSlot,getNextPageButton());
                updateSlot(nextPageButtonSlot);
            }
        }
    }

    protected void fillElements(byte page) {
        fillEmpty();
        byte slot = 0;
        for (Object object : dividePagesByElements(elements).get(page-1)) {
            setItem(itemsSlots[slot], getElementIcon(object));
            updateSlot(itemsSlots[slot]);
            slot++;
        }
    }

    protected void fillEmpty() {
        for (byte slot : itemsSlots) {
            setItem(slot, AIR_ITEM);
            updateSlot(slot);
        }
        setItem(nextPageButtonSlot,AIR_ITEM);
        setItem(previousPageButtonSlot,AIR_ITEM);
        updateSlot(nextPageButtonSlot);
        updateSlot(previousPageButtonSlot);
    }
    
    protected abstract ItemStack getElementIcon(Object object);
    protected abstract void fillOtherItems();

    public byte getCurrentPage() {
        return currentPage;
    }

    protected abstract void onCharmsBarClick(InventoryClickEvent event);
    protected abstract void onElementClick(InventoryClickEvent event);

    protected abstract List<Object> getElements();
    protected abstract ItemStack getNextPageButton();
    protected abstract ItemStack getPreviousPageButton();
    protected abstract ItemStack getNoElementsButton();

    protected static List<List<Object>> dividePagesByElements(List<Object> elements) {
        List<List<Object>> pages = new ArrayList<>();

        byte pageSize = (byte) itemsSlots.length;
        byte pageCount = countPages(elements);
        // For pages
        for (int i = 0; i < pageCount; i++) {

            int fromIndex = i * pageSize;
            int toIndex = Math.min((i + 1) * pageSize, elements.size());
            List<Object> sublist = elements.subList(fromIndex, toIndex);

            // One list of items
            ArrayList<Object> page = new ArrayList<>(sublist);
            pages.add(page);
        }

        return pages;
    }

    private static byte countPages(List<Object> objects) {
        return (byte) Math.ceil((double) objects.size() / itemsSlots.length);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) {
            event.setCancelled(true);
            return;
        }
        if (isCharmsBarClicked((byte) event.getSlot()) && !isEmpty(event.getCurrentItem())) {
            onCharmsBarClick(event);
        } else if (isElementClicked((byte) event.getSlot()) && !isEmpty(event.getCurrentItem())) {
            onElementClick(event);
        } else if (itemEquals(event.getCurrentItem(),getNextPageButton())) {
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ITEM_BOOK_PAGE_TURN,100f,1f);
            nextPage();
            event.setCancelled(true);
        } else if (itemEquals(event.getCurrentItem(),getPreviousPageButton())) {
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ITEM_BOOK_PAGE_TURN,100f,1f);
            previousPage();
            event.setCancelled(true);
        } else {
            event.setCancelled(true);
        }
    }

    protected void nextPage() {
        currentPage = getNextPage();
        fillElements(currentPage);
        fillArrowsItems(currentPage);
    }

    protected void previousPage() {
        currentPage = getPreviousPage();
        fillElements(currentPage);
        fillArrowsItems(currentPage);
    }

    private byte getPreviousPage() {
        byte previousPage = (byte) (currentPage-1);
        byte maxPagesAmount = (byte) dividePagesByElements(elements).size();
        if (previousPage > maxPagesAmount || previousPage < 1) {
            previousPage = 1;
        }
        return previousPage;
    }

    private byte getNextPage() {
        byte nextPage = (byte) (currentPage+1);
        byte maxPagesAmount = (byte) dividePagesByElements(elements).size();
        if (nextPage > maxPagesAmount || nextPage < 1) {
            nextPage = 1;
        }
        return nextPage;
    }

    private boolean isCharmsBarClicked(byte clickedSlot) {
        for (byte itemSlot : charmsBarSlots) {
            if (itemSlot == clickedSlot) {
                return true;
            }
        }
        return false;
    }

    private boolean isElementClicked(byte clickedSlot) {
        for (byte itemSlot : itemsSlots) {
            if (itemSlot == clickedSlot) {
                return true;
            }
        }
        return false;
    }
}
