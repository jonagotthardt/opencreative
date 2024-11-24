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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory;

import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemPickupEvent extends WorldEvent {

    private final ItemStack item;
    private final EntityPickupItemEvent event;

    public ItemPickupEvent(Player player, EntityPickupItemEvent event) {
        super(player);
        this.event = event;
        this.item = event.getItem().getItemStack();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(true);
    }

    public ItemStack getItem() {
        return item;
    }
}
