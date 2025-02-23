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

package ua.mcchickenstudio.opencreative.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;

public abstract class AbstractInputMenu implements InventoryMenu {

    private String title;
    protected AnvilInventory inventory;
    protected final long creationTime;

    public AbstractInputMenu(String title) {
        this.title = title;
        this.creationTime = System.currentTimeMillis();
    }

    public void setMainItem(@NotNull ItemStack item) {
        getInventory().setItem(0,item);
    }

    public void setOffItem(@NotNull ItemStack item) {
        getInventory().setItem(1,item);
    }

    public void setResultItem(@NotNull ItemStack item) {
        getInventory().setItem(0,item);
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory == null || inventory.getType() != InventoryType.ANVIL) {
            inventory = (AnvilInventory) Bukkit.createInventory(this, InventoryType.ANVIL, Component.text(title));
        }
        return inventory;
    }

    @Override
    public void open(@NotNull Player player) {
        Menus.addMenu(this);
        try {
            inventory = (AnvilInventory) getInventory();

            player.openInventory(inventory);
        } catch (Exception e) {
            sendPlayerErrorMessage(player,"Failed to open AbstractMenu with title " + title + ". ",e);
        }
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {

    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }
}
