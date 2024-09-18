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

package mcchickenstudio.creative.coding.menus;

import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.menu.AbstractListMenu;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static mcchickenstudio.creative.utils.BlockUtils.setSignLine;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getPathFromMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

/**
 * This class represents a menu where player can select type of coding block.
 * Every category of coding blocks has this menu.
 */
public abstract class CodingBlockTypesMenu extends AbstractListMenu {

    private final String codingBlockName;
    private final Location signLocation;
    protected MenusCategory currentCategory;

    public CodingBlockTypesMenu(Player player, Location location, String codingBlockName, String titleName) {
        super(ChatColor.stripColor(getLocaleMessage("blocks." + titleName)),player);
        this.codingBlockName = codingBlockName;
        signLocation = location;
    }

    protected abstract ItemStack getElementIcon(Object object);

    @Override
    protected void fillOtherItems() {
        byte slot = 0;
        for (MenusCategory category : getMenusCategories()) {
            setItem(charmsBarSlots[slot],category.getItem(codingBlockName));
            slot++;
        }
        if (slot < charmsBarSlots.length) {
            while (slot < charmsBarSlots.length) {
                setItem(charmsBarSlots[slot],DECORATION_ITEM);
                slot++;
            }
        }
    }

    protected abstract Set<MenusCategory> getMenusCategories();

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        event.setCancelled(true);
        MenusCategory category = MenusCategory.getByMaterial(clicked.getType());
        if (category != null) {
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ITEM_BOOK_PAGE_TURN,100f,0.5f);
            currentCategory = category;
            elements.clear();
            elements.addAll(getElements());
            fillElements((byte) 1);
            fillArrowsItems((byte) 1);
        }

    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        event.setCancelled(true);
        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
        if (signLocation.getWorld().getName().contains("dev") && devPlot != null) {
            String beginLocalizationPath = "items.developer." + codingBlockName + ".";
            String path = getPathFromMessage(beginLocalizationPath, item.getItemMeta().getDisplayName());
            if (path == null || !path.endsWith(".name")) {
                return;
            }
            String typeString = path.replace(beginLocalizationPath,"").replace(".name","").replace("-","_");
            if (setSignLine(signLocation,(byte) 3,typeString)) {
                translateBlockSign(signLocation.getBlock());
                player.closeInventory();
                player.sendTitle(getLocaleMessage("world.dev-mode.set-" + codingBlockName),item.getItemMeta().getDisplayName(),15,20,15);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1.7f);
            }
            Block codingBlock = signLocation.getBlock().getRelative(BlockFace.NORTH);
            /*
             Setting a chest block if action requires container.
             Executors don't have arguments, neither chests...
            */
            if (ActionCategory.getByMaterial(codingBlock.getType()) != null)  {
                ActionType type = ActionType.getType(codingBlock);
                Block containerBlock = codingBlock.getRelative(BlockFace.UP);
                if (containerBlock.getState() instanceof InventoryHolder container) {
                    for (ItemStack chestItem : container.getInventory().getContents()) {
                        if (chestItem != null) {
                            if (chestItem.getItemMeta() == null || !chestItem.getItemMeta().getPersistentDataContainer().has(getCodingDoNotDropMeKey())) {
                                containerBlock.getWorld().dropItem(containerBlock.getLocation(),chestItem);
                            }
                        }
                    }
                    containerBlock.setType(Material.AIR);
                }
                if (type != null && type.isChestRequired()) {
                    containerBlock.setType(devPlot.getContainerMaterial());
                    BlockData blockData = containerBlock.getBlockData();
                    ((Directional) blockData).setFacing(BlockFace.SOUTH);
                    containerBlock.setBlockData(blockData);
                    player.playSound(player.getLocation(),Sound.BLOCK_ENDER_CHEST_CLOSE,100f,1.2f);
                }
            }
        }
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInName(createItem(Material.SPECTRAL_ARROW,1,"items.developer.categories." + codingBlockName + ".next-page"),"%page%",currentPage+1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInName(createItem(Material.SPECTRAL_ARROW,1,"items.developer.categories." + codingBlockName + ".previous-page"),"%page%",currentPage-1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER,1,"items.developer.categories." + codingBlockName + ".empty");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {}
}
