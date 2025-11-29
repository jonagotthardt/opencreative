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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.indev.Items;

import java.util.List;
import java.util.Map;

public enum ItemsGroup {

    LOBBY(new ItemPair(1, Items.CHANGELOGS), new ItemPair(4, Items.GAMES), new ItemPair(6, Items.OWN)),
    BUILD_OWNER,
    PLAY_OWNER,
    CODING,
    CODING_OWNER;

    private final List<ItemPair> pairs;

    ItemsGroup() {
        pairs = null;
    }

    ItemsGroup(@NotNull ItemPair... pairs) {
        this.pairs = List.of(pairs);
    }

    public @Nullable List<ItemPair> getPairs() {
        return pairs;
    }

    public static @Nullable ItemsGroup getById(@NotNull String id) {
        for (ItemsGroup group : ItemsGroup.values()) {
            if (group.name().equals(id)) {
                return group;
            }
        }
        return null;
    }

    public void setItems(@NotNull Player player) {
        SettingsItemsGroup group = OpenCreative.getSettings().getItemsGroups().get(this);
        if (group == null) {
            for (ItemPair pair : pairs) {
                player.getInventory().setItem(pair.slot()-1, pair.item().get());
            }
            return;
        }
        Map<Integer, SettingsItem> items = group.getItems();
        for (int slot : items.keySet()) {
            player.getInventory().setItem(slot-1, items.get(slot).getItem(player));
        }
    }

    public boolean giveItem(@NotNull Player player, int slot) {
        SettingsItemsGroup group = OpenCreative.getSettings().getItemsGroups().get(this);
        if (group == null) {
            for (ItemPair pair : pairs) {
                if (pair.slot() == slot) {
                    player.getInventory().addItem(pair.item().get());
                    return true;
                }
            }
            return false;
        }
        Map<Integer, SettingsItem> items = group.getItems();
        SettingsItem item = items.get(slot);
        if (item == null) return false;
        player.getInventory().addItem(item.getItem(player));
        return true;
    }
}
