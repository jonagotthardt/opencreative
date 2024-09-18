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
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class ParticlesMenu extends AbstractListMenu {

    private static final Map<Particle,Material> particles = new HashMap<>();

    public ParticlesMenu(Player player) {
        super(getLocaleMessage("menus.developer.particles-list.title"), player);
        itemsSlots = allowedSlots;
        charmsBarSlots = new byte[]{};
        previousPageButtonSlot = 45;
    }

    static {
        particles.put(Particle.DUST,Material.GLOWSTONE_DUST);
        particles.put(Particle.ANGRY_VILLAGER,Material.BEETROOT);
        particles.put(Particle.HAPPY_VILLAGER,Material.EMERALD);
        particles.put(Particle.TOTEM_OF_UNDYING,Material.TOTEM_OF_UNDYING);
        particles.put(Particle.DRIPPING_LAVA,Material.LAVA_BUCKET);
        particles.put(Particle.DRIPPING_WATER,Material.WATER_BUCKET);
        particles.put(Particle.ITEM_COBWEB,Material.COBWEB);
        particles.put(Particle.DAMAGE_INDICATOR,Material.NETHERITE_SWORD);
        particles.put(Particle.ITEM_SNOWBALL,Material.SNOWBALL);
        particles.put(Particle.VIBRATION,Material.SCULK_SENSOR);
    }

    private Material getMaterial(Particle type) {
        if (particles.containsKey(type)) {
            return particles.get(type);
        }
        return Material.NETHER_STAR;
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof Particle type) {
            ItemStack itemStack = createItem(getMaterial(type),1);
            setDisplayName(itemStack,type.name());
            setPersistentData(itemStack,getCodingValueKey(),"PARTICLE");
            setPersistentData(itemStack,getCodingParticleTypeKey(),type.name());
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
            ItemStack item = event.getCurrentItem().clone();
            item.setType(Material.NETHER_STAR);
            event.getWhoClicked().getInventory().setItemInMainHand(item);
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR,100,1.2f);
        }
        event.setCancelled(true);
    }

    @Override
    protected List<Object> getElements() {
        return Arrays.asList(Particle.values());
    }

    @Override
    protected ItemStack getNextPageButton() {
        return createItem(Material.SPECTRAL_ARROW,1,"menus.developer.particles-list.items.next-page");
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return createItem(Material.ARROW,1,"menus.developer.particles-list.items.previous-page");
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER,1,"menus.developer.particles-list.items.no-elements");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }
}
