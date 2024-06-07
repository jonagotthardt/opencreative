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
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static mcchickenstudio.creative.utils.BlockUtils.setSignLine;
import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.replacePlaceholderInName;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getPathFromMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public abstract class CodingBlockTypesMenu extends AbstractListMenu {

    private final String codingBlockName;
    private final Location signLocation;
    protected MenusCategory currentCategory = MenusCategory.WORLD;

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
        if (signLocation.getWorld().getName().contains("dev")) {
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
                Block chestBlock = codingBlock.getRelative(BlockFace.UP);
                if (type != null && type.isChestRequired()) {
                    chestBlock.setType(Material.CHEST);
                    BlockData blockData = chestBlock.getBlockData();
                    Chest chestState = (Chest) chestBlock.getState();
                    chestState.setCustomName(typeString);
                    chestState.update();
                    ((Directional) blockData).setFacing(BlockFace.SOUTH);
                    chestBlock.setBlockData(blockData);
                    //player.spawnParticle(Particle.EXPLOSION,chestBlock.getLocation(),1);
                    player.playSound(player.getLocation(),Sound.BLOCK_ENDER_CHEST_CLOSE,100f,1.2f);
                } else {
                    chestBlock.setType(Material.AIR);
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

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
