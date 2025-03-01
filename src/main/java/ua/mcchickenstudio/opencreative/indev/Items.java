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

package ua.mcchickenstudio.opencreative.indev;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.settings.SettingsItem;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

public enum Items {

    LOBBY_WORLDS_BROWSER(Material.COMPASS),
    LOBBY_MY_WORLDS(Material.NETHER_STAR),
    WORLD_SETTINGS(Material.COMPASS),
    DEV_EVENT_PLAYER(Material.DIAMOND_BLOCK),
    DEV_EVENT_WORLD(Material.REDSTONE_BLOCK),
    DEV_EVENT_ENTITY(Material.GOLD_BLOCK),
    DEV_CYCLE(Material.OXIDIZED_COPPER),
    DEV_METHOD(Material.EMERALD_BLOCK),
    DEV_FUNCTION(Material.LAPIS_BLOCK),
    DEV_ACTION_PLAYER(Material.COBBLESTONE),
    DEV_ACTION_WORLD(Material.NETHER_BRICKS),
    DEV_ACTION_ENTITY(Material.MOSSY_COBBLESTONE),
    DEV_ACTION_VAR(Material.IRON_BLOCK),
    DEV_CONDITION_PLAYER(Material.OAK_PLANKS),
    DEV_CONDITION_WORLD(Material.RED_NETHER_BRICKS),
    DEV_CONDITION_ENTITY(Material.BRICKS),
    DEV_CONDITION_VAR(Material.OBSIDIAN),
    DEV_LAUNCH_FUNCTION(Material.LAPIS_ORE),
    DEV_LAUNCH_METHOD(Material.EMERALD_ORE),
    DEV_CONDITION_ELSE(Material.END_STONE);

    private final Material material;

    Items(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack get() {
        SettingsItem item = OpenCreative.getSettings().getItems().get(this);
        if (item == null) {
            return createItem(material,1);
        }
        return item.item();
    }
}
