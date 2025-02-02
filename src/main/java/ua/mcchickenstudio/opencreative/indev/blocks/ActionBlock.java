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

package ua.mcchickenstudio.opencreative.indev.blocks;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

import java.util.Map;

public abstract class ActionBlock extends CodingBlock {

    public ActionBlock(@NotNull String id, @NotNull Material mainBlock, @NotNull Material offBlock, boolean hasContainer) {
        super(id, mainBlock, offBlock, hasContainer);
    }

    public abstract void execute(WorldEvent event);

    @Override
    public void onSignClick(PlayerInteractEvent event) {

    }

    @Override
    public @Nullable CodingBlock deserialize(Map<String, Object> args) {
        return null;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of();
    }
}
