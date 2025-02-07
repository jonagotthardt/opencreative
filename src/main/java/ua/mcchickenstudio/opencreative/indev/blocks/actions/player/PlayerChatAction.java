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

package ua.mcchickenstudio.opencreative.indev.blocks.actions.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

public class PlayerChatAction extends PlayerActionBlock {

    public PlayerChatAction(boolean hasContainer) {
        super("send_message");
    }

    @Override
    public void execute(@NotNull Player player, @NotNull ActionsHandler actionsHandler, @NotNull Arguments arguments) {
        player.sendMessage("Hello world");
    }

    @Override
    public String getCodingPackId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Send chat message";
    }

    @Override
    public String getDescription() {
        return "Player will receive chat message";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BOOK);
    }
}
