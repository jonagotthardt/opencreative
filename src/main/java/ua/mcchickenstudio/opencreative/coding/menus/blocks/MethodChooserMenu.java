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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ua.mcchickenstudio.opencreative.menu.AbstractListMenu;
import ua.mcchickenstudio.opencreative.plots.DevPlot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public class MethodChooserMenu extends AbstractListMenu {

    private final DevPlot devPlot;
    private final Location signLocation;

    public MethodChooserMenu(Player player, DevPlot plot, Location location) {
        super(getLocaleMessage("menus.developer.method-chooser.title"), player);
        itemsSlots = allowedSlots;
        charmsBarSlots = new byte[]{};
        previousPageButtonSlot = 45;
        noElementsPageButtonSlot = 22;
        this.devPlot = plot;
        this.signLocation = location;
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof Location location) {
            Block signBlock = location.getBlock().getRelative(BlockFace.SOUTH);
            String line = getSignLine(signBlock.getLocation(),(byte) 3);
            if (line != null && !line.isEmpty()) {
                ItemStack itemStack = createItem(Material.EMERALD,1,"menus.developer.method-chooser.items.method");
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.GREEN + line);
                }
                itemStack.setItemMeta(meta);
                setPersistentData(itemStack,getCodingLocationX(),location.getX());
                setPersistentData(itemStack,getCodingLocationY(),location.getY());
                setPersistentData(itemStack,getCodingLocationZ(),location.getZ());
                replacePlaceholderInLore(itemStack,"%x%",location.getX());
                replacePlaceholderInLore(itemStack,"%y%",location.getY());
                replacePlaceholderInLore(itemStack,"%z%",location.getZ());
                return itemStack;
            }
        }
        return null;
    }

    @Override
    protected void fillDecorationItems() {
        for (byte slot = 0; slot <= 8; slot++) {
            setItem(slot,DECORATION_PANE_ITEM);
        }
        for (byte slot = 45; slot <= 53; slot++) {
            setItem(slot,DECORATION_PANE_ITEM);
        }
    }

    @Override
    protected void fillOtherItems() {}

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {}

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        if (isPlayerClicked(event) && isClickedInMenuSlots(event)) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (item.getItemMeta() == null) return;
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            event.getWhoClicked().closeInventory();
            if (event.isRightClick()) {
                PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                if (container.has(getCodingLocationX()) && container.has(getCodingLocationY()) && container.has(getCodingLocationZ())) {
                    try {
                        double x = container.get(getCodingLocationX(), PersistentDataType.DOUBLE);
                        double y = container.get(getCodingLocationY(), PersistentDataType.DOUBLE);
                        double z = container.get(getCodingLocationZ(), PersistentDataType.DOUBLE);
                        event.getWhoClicked().teleport(new Location(event.getWhoClicked().getWorld(),x,y,z+2,180,0));
                    } catch (NullPointerException ignored) {}
                }
            } else {
                setSignLine(signLocation,(byte) 3,name);
                translateBlockSign(signLocation.getBlock());
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("menus.developer.method-chooser.chosen")), Component.text(name).color(NamedTextColor.GREEN),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                ));
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE,100,1.2f);
            }
        }
        event.setCancelled(true);
    }

    @Override
    protected void fillArrowsItems(byte currentPage) {
        if (elements.isEmpty()) {
            setItem(noElementsPageButtonSlot, getNoElementsButton());
            setItem(previousPageButtonSlot, DECORATION_PANE_ITEM);
            setItem(nextPageButtonSlot, DECORATION_PANE_ITEM);
            updateSlot(noElementsPageButtonSlot);
            updateSlot(previousPageButtonSlot);
            updateSlot(nextPageButtonSlot);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            setItem(previousPageButtonSlot,currentPage > 1 ? getPreviousPageButton() : DECORATION_PANE_ITEM);
            updateSlot(previousPageButtonSlot);
            setItem(nextPageButtonSlot,currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_PANE_ITEM);
            updateSlot(nextPageButtonSlot);
        }
    }

    @Override
    protected List<Object> getElements() {
        return new ArrayList<>(devPlot.getPlacedMethods());
    }

    @Override
    protected ItemStack getNextPageButton() {
        return createItem(Material.SPECTRAL_ARROW,1,"menus.developer.method-chooser.items.next-page");
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return createItem(Material.ARROW,1,"menus.developer.method-chooser.items.previous-page");
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER,1,"menus.developer.method-chooser.items.no-elements");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }
}
