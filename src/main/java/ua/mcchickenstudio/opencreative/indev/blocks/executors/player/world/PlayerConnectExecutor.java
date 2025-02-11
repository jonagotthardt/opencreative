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

package ua.mcchickenstudio.opencreative.indev.blocks.executors.player.world;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.indev.blocks.executors.player.PlayerEvent;
import ua.mcchickenstudio.opencreative.indev.blocks.executors.player.PlayerExecutor;

public class PlayerConnectExecutor extends PlayerExecutor {
    public PlayerConnectExecutor(@NotNull String id) {
        super("join");
    }

    @Override
    public Class<? extends PlayerEvent> getPlayerEventClass() {
        return PlayerConnectEvent.class;
    }

    @Override
    public String getCodingPackId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Player Joined World";
    }

    @Override
    public String getDescription() {
        return "Triggers when player joins world";
    }

    @Override
    public @NotNull ItemStack getIcon() {
        return new ItemStack(Material.POTATO);
    }

    public static class PlayerConnectEvent extends PlayerEvent {
        public PlayerConnectEvent(Player player) {
            super(player);
        }
    }
}


