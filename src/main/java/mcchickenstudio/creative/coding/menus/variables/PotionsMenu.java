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
 *//*


package mcchickenstudio.creative.coding.menus.variables;

import mcchickenstudio.creative.menu.AbstractListMenu;
import mcchickenstudio.creative.menu.AbstractMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class PotionsMenu extends AbstractListMenu {
    public PotionsMenu(Player player) {
        super(getLocaleMessage("menus.developer.potions-list.title"), player);
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        return null;
    }

    @Override
    protected void fillOtherItems() {

    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {

    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {

    }

    @Override
    protected List<Object> getElements() {
        return List.of();
    }

    @Override
    protected ItemStack getNextPageButton() {
        return null;
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return null;
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return null;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
*/
