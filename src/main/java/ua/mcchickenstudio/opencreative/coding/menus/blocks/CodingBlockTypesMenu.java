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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

/**
 * This class represents a menus where player can select type of coding block.
 * Every category of coding blocks has this menus.
 */
public abstract class CodingBlockTypesMenu extends ListBrowserMenu<Object> {

    private final String codingBlockName;
    private final Location signLocation;
    protected MenusCategory currentCategory;

    public CodingBlockTypesMenu(Player player, Location location, String codingBlockName, String titleName) {
        super(player,ChatColor.stripColor(getLocaleMessage("blocks." + titleName)));
        this.codingBlockName = codingBlockName;
        signLocation = location;
    }

    public CodingBlockTypesMenu(Player player, Location location, String codingBlockName, String titleName, boolean bottom) {
        super(player,ChatColor.stripColor(getLocaleMessage("blocks." + titleName)),bottom ? PlacementLayout.BOTTOM_CHARMS_BAR : PlacementLayout.LEFT_CHARMS_BAR);
        this.codingBlockName = codingBlockName;
        signLocation = location;
    }

    protected abstract ItemStack getElementIcon(Object object);

    @Override
    protected void fillOtherItems() {
        int slot = 0;
        for (MenusCategory category : getMenusCategories()) {
            setItem(getCharmsBarSlots()[slot],category.getItem(codingBlockName));
            slot++;
        }
        if (slot < getCharmsBarSlots().length) {
            while (slot < getCharmsBarSlots().length) {
                setItem(getCharmsBarSlots()[slot],DECORATION_ITEM);
                slot++;
            }
        }
    }

    protected abstract Set<MenusCategory> getMenusCategories();

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        event.setCancelled(true);
        MenusCategory category = MenusCategory.getByIcon(clicked);
        if (category != null) {
            Sounds.DEV_CHANGE_CATEGORY.play(event.getWhoClicked());
            currentCategory = category;
            elements.clear();
            elements.addAll(getElements());
            fillElements(1);
            fillArrowsItems(1);
        }

    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        event.setCancelled(true);
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(getPlayer());
        Block codingBlock = signLocation.getBlock().getRelative(BlockFace.NORTH);
        if (signLocation.getWorld().getName().contains("dev") && devPlanet != null) {
            String typeString = getPersistentData(item,getCodingValueKey());
            ExecutorType executorType = null;
            ActionType actionType = null;
            try {
                actionType = ActionType.valueOf(typeString);
            } catch (Exception ignored) {}
            try {
                executorType = ExecutorType.valueOf(typeString);
            } catch (Exception ignored) {}
            ActionCategory actionCategory = actionType == null ? null : actionType.getCategory();
            ExecutorCategory executorCategory = executorType == null ? null : ExecutorCategory.getByMaterial(codingBlock.getType());
            if (actionCategory != null) {
                setSignLine(signLocation,2, actionCategory.name().toLowerCase());
            }
            if (executorCategory != null) {
                setSignLine(signLocation,2, executorCategory.name().toLowerCase());
            }
            if (setSignLine(signLocation,3,typeString.toLowerCase())) {
                translateBlockSign(signLocation.getBlock());
                getPlayer().closeInventory();
                getPlayer().showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.set-" + codingBlockName)), item.getItemMeta().displayName(),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(1), Duration.ofMillis(750))
                ));
                switch (codingBlockName) {
                    case "events" -> Sounds.DEV_SET_EVENT.play(event.getWhoClicked());
                    case "actions" -> Sounds.DEV_SET_ACTION.play(event.getWhoClicked());
                    case "conditions" -> Sounds.DEV_SET_CONDITION.play(event.getWhoClicked());
                }
            }
            /*
             Setting a chest block if action requires container.
             Executors don't have arguments, neither chests.
            */
            if (actionCategory != null && executorCategory == null)  {
                Block containerBlock = codingBlock.getRelative(BlockFace.UP);
                if (containerBlock.getState() instanceof InventoryHolder container) {
                    if (devPlanet.isDropItems()) {
                        for (ItemStack chestItem : container.getInventory().getContents()) {
                            if (chestItem != null) {
                                if (chestItem.getItemMeta() == null || !chestItem.getItemMeta().getPersistentDataContainer().has(getCodingDoNotDropMeKey())) {
                                    containerBlock.getWorld().dropItem(containerBlock.getLocation(),chestItem);
                                }
                            }
                        }
                    }
                    containerBlock.setType(Material.AIR);
                }
                if (actionType.isChestRequired()) {
                    containerBlock.setType(devPlanet.getContainerMaterial());
                    BlockData blockData = containerBlock.getBlockData();
                    ((Directional) blockData).setFacing(BlockFace.SOUTH);
                    containerBlock.setBlockData(blockData);
                    getPlayer().spawnParticle(Particle.BLOCK,containerBlock.getLocation(),1,0,0.5f,0.5f,containerBlock.getBlockData());
                    Sounds.WORLD_SETTINGS_FLAG_CHANGE.play(getPlayer());
                }
            }
        }
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInName(createItem(Material.SPECTRAL_ARROW,1,"items.developer.categories." + codingBlockName + ".next-page"),"%page%", getCurrentPage() +1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInName(createItem(Material.SPECTRAL_ARROW,1,"items.developer.categories." + codingBlockName + ".previous-page"),"%page%", getCurrentPage() -1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER,1,"items.developer.categories." + codingBlockName + ".empty");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {}
}
