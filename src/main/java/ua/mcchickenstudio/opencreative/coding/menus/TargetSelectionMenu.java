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

package ua.mcchickenstudio.opencreative.coding.menus;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public class TargetSelectionMenu extends AbstractMenu {

    private final Location signLocation;

    public TargetSelectionMenu(Location location) {
        super((byte) 1, getLocaleMessage("menus.developer.selection.title"));
        signLocation = location;
    }

    @Override
    public void fillItems(Player player) {
        byte slot = 0;
        for (Target target : Target.values()) {
            setItem(slot,createItem(target.getIcon(),1,"menus.developer.selection.items."+ target.name().toLowerCase().replace("_","-")));
            slot++;
        }
        if (slot < getSize()) {
            while (slot < getSize()) {
                setItem(slot,DECORATION_ITEM);
                slot++;
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (isPlayerClicked(event) && isClickedInMenuSlots(event)) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            event.setCancelled(true);
            if (item != null && !item.equals(DECORATION_ITEM)) {
                Target selection = Target.getByMaterial(item.getType());
                if (selection == Target.DEFAULT) {
                    setSignLine(signLocation,(byte) 4,"");
                } else {
                    setSignLine(signLocation,(byte) 4,selection.name().toLowerCase());
                }
                translateBlockSign(signLocation.getBlock());
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,100,0.2f);
            }
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {}
}
