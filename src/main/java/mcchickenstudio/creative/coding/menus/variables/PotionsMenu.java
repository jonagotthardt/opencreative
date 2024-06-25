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

package mcchickenstudio.creative.coding.menus.variables;

import mcchickenstudio.creative.menu.AbstractListMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class PotionsMenu extends AbstractListMenu {

    private static final Map<PotionEffectType,Material> potions = new HashMap<>();

    public PotionsMenu(Player player) {
        super(getLocaleMessage("menus.developer.potions-list.title"), player);
        itemsSlots = allowedSlots;
        charmsBarSlots = new byte[]{};
        previousPageButtonSlot = 45;
    }

    static {
        potions.put(PotionEffectType.BLINDNESS,Material.BLACK_SHULKER_BOX);
        potions.put(PotionEffectType.JUMP_BOOST,Material.RABBIT_FOOT);
        potions.put(PotionEffectType.HEALTH_BOOST,Material.GOLDEN_APPLE);
    }

    private Material getMaterial(PotionEffectType type) {
        if (potions.containsKey(type)) {
            return potions.get(type);
        }
        return Material.POTION;
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof PotionEffectType) {
            PotionEffectType type = (PotionEffectType) object;
            ItemStack itemStack = new ItemStack(Material.POTION,1);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            PotionType potionType = PotionType.getByEffect(type);
            if (potionType != null) {
                meta.setBasePotionType(potionType);
            } else {
                meta.setDisplayName(type.getName().toLowerCase().replace("minecraft:",""));
                meta.setBasePotionType(PotionType.WATER);
            }
            meta.setColor(type.getColor());
            itemStack.setItemMeta(meta);
            return itemStack;
        }
        return null;
    }

    @Override
    protected void fillDecorationItems() {}

    @Override
    protected void fillOtherItems() {}

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {}

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        if (isPlayerClicked(event) && isClickedInMenuSlots(event)) {
            if (event.getCurrentItem() == null) return;
            event.getWhoClicked().getInventory().setItemInMainHand(event.getCurrentItem());
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_BREWING_STAND_BREW,100,1.2f);
        }
        event.setCancelled(true);
    }

    @Override
    protected List<Object> getElements() {
        return Arrays.asList(PotionEffectType.values());
    }

    @Override
    protected ItemStack getNextPageButton() {
        return createItem(Material.SPECTRAL_ARROW,1,"menus.developer.potions-list.items.next-page");
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return createItem(Material.ARROW,1,"menus.developer.potions-list.items.previous-page");
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER,1,"menus.developer.potions-list.items.no-elements");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
