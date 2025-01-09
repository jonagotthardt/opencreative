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

package ua.mcchickenstudio.opencreative.coding.test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;

public class PlayerToEntityConvertor extends Convertor {

    public PlayerToEntityConvertor(List<Planet> planets) {
        super("Converts player actions to entity actions", planets);
    }

    @Override
    public boolean convertCodingBlock(@NotNull Block mainBlock, @NotNull Location containerLocation, InventoryHolder container, @NotNull Location signLocation, @NotNull String first, @NotNull String second, @NotNull String third, @NotNull String fourth) {
        if (!second.equalsIgnoreCase("player_action")) return false;
        if (!third.startsWith("player_")) return false;
        String[] strings = new String[]{
                "give_items", "set_item_in_hand", "set_items", "set_armor",
                "give_random_item", "clear_inventory", "close_inventory", "remove_items",
                "set_hotbar", "get_item_by_slot", "teleport", "saddle_entity", "launch_vertical",
                "launch_horizontal", "launch_to_location",
                "set_health", "set_hunger", "set_walk_speed", "set_fly_speed", "set_max_health",
                "set_fire_ticks", "set_freeze_ticks", "set_no_damage_ticks", "give_potion_effects",
                "clear_potion_effects", "remove_potion_effects", "set_arrows_in_body", "set_bee_stinger_cooldown",
                "set_maximum_no_damage_ticks", "set_can_pickup_item", "set_last_damage", "set_fall_distance",
                "set_glowing"
        };
        for (String string : strings) {
            if (third.endsWith(string)) {
                mainBlock.setType(Material.MOSSY_COBBLESTONE);
                setSignLine(signLocation,(byte) 2,"entity_action");
                setSignLine(signLocation,(byte) 3,third.replace("player_","entity_"));
                return true;
            }
        }
        return false;
    }
}
