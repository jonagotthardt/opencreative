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

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

public class PlayerItemDamagedEvent extends WorldEvent {

    private final PlayerItemDamageEvent event;
    private final ItemStack item;
    private final int damage;

    public PlayerItemDamagedEvent(Player player, PlayerItemDamageEvent event) {
        super(player);
        this.event = event;
        this.item = event.getItem();
        this.damage = event.getDamage();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    public ItemStack getItem() {
        return item;
    }

    public int getDamage() {
        return damage;
    }

}
