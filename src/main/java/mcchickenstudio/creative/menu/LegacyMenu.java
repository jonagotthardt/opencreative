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

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Deprecated
abstract public class LegacyMenu implements InventoryHolder {

    private final int rows;
    private String title;
    private Map<Integer,ItemStack> items;

    @Deprecated
    public LegacyMenu(int rows, String title) {

        this.rows = rows;
        this.title = title;

    }

    public void setItems(Map<Integer, ItemStack> items) {
        this.items = items;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.rows * 9, this.title);
        for (Map.Entry<Integer,ItemStack> item : items.entrySet()) {
            inventory.setItem(item.getKey(),item.getValue());
        }
        return inventory;
    }
}
