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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory;

import org.bukkit.event.player.PlayerItemHeldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import org.bukkit.entity.Player;

public class SlotChangeEvent extends WorldEvent {

    private final PlayerItemHeldEvent event;
    private final int oldSlot;
    private final int newSlot;

    public SlotChangeEvent(Player player, PlayerItemHeldEvent event) {
        super(player);
        this.event = event;
        this.oldSlot = event.getPreviousSlot();
        this.newSlot = event.getNewSlot();
    }

    public int getOldSlot() {
        return oldSlot;
    }

    public int getNewSlot() {
        return newSlot;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }
}
