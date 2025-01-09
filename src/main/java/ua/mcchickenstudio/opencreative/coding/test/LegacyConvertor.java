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
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;

public class LegacyConvertor extends Convertor {


    public LegacyConvertor(List<Planet> planets) {
        super("Converts legacy code from 1.4 version to 5.0", planets);
    }

    @Override
    public boolean convertCodingBlock(@NotNull Block mainBlock, @NotNull Location containerLocation, InventoryHolder container, @NotNull Location signLocation, @NotNull String first, @NotNull String second, @NotNull String third, @NotNull String fourth) {
        if ("action_player".equalsIgnoreCase(second)) {
            setSignLine(signLocation,(byte) 2,"player_action");
            return true;
        }
        return false;
    }
}
