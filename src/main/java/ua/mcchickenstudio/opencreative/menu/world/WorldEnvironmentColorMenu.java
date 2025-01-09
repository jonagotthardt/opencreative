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

package ua.mcchickenstudio.opencreative.menu.world;

import ua.mcchickenstudio.opencreative.menu.AbstractListMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class WorldEnvironmentColorMenu extends AbstractListMenu {

    private final Set<Material> materials = new HashSet<>();
    private final String type;
    private final Material currentMaterial;
    private final DevPlanet devPlanet;
    private final DevPlatform platform;

    public WorldEnvironmentColorMenu(Player player, DevPlanet devPlanet, DevPlatform devPlatform, String type) {
        super(getLocaleMessage("menus.developer.environment.colors.title"), player);
        this.devPlanet = devPlanet;
        this.platform = devPlatform;
        this.type = type;
        setRows((byte) 5);
        itemsSlots = new byte[]{10,11,12,13,14,15,16,19,20,21,22,23,24,25};
        charmsBarSlots = new byte[]{36};
        previousPageButtonSlot = -1;
        nextPageButtonSlot = -1;
        decorationSlots = new byte[]{37,38,42,43,44};
        materials.add(Material.RED_STAINED_GLASS);
        materials.add(Material.ORANGE_STAINED_GLASS);
        materials.add(Material.YELLOW_STAINED_GLASS);
        materials.add(Material.LIME_STAINED_GLASS);
        materials.add(Material.GREEN_STAINED_GLASS);
        materials.add(Material.PURPLE_STAINED_GLASS);
        materials.add(Material.MAGENTA_STAINED_GLASS);
        materials.add(Material.PINK_STAINED_GLASS);
        materials.add(Material.BLUE_STAINED_GLASS);
        materials.add(Material.CYAN_STAINED_GLASS);
        materials.add(Material.LIGHT_BLUE_STAINED_GLASS);
        materials.add(Material.BLACK_STAINED_GLASS);
        materials.add(Material.GRAY_STAINED_GLASS);
        materials.add(Material.LIGHT_GRAY_STAINED_GLASS);
        materials.add(Material.WHITE_STAINED_GLASS);
        materials.add(Material.BARRIER);
        if (devPlatform == null) {
            devPlatform = new DevPlatform(devPlanet.getWorld(),1,1);
        }
        switch (type.toLowerCase()) {
            case "floor" -> {
                this.currentMaterial = devPlatform.getFloorMaterial();
                materials.remove(devPlatform.getActionMaterial());
                materials.remove(devPlatform.getEventMaterial());
            }
            case "event" -> {
                this.currentMaterial = devPlatform.getEventMaterial();
                materials.remove(devPlatform.getActionMaterial());
                materials.remove(devPlatform.getFloorMaterial());
            }
            default -> {
                this.currentMaterial = devPlatform.getActionMaterial();
                materials.remove(devPlatform.getEventMaterial());
                materials.remove(devPlatform.getFloorMaterial());
            }
        }
        materials.remove(currentMaterial);
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof Material material) {
            return new ItemStack(material,1);
        }
        return null;
    }

    @Override
    protected void fillOtherItems() {
        setItem((byte) 36,createItem(Material.ARROW,1,"menus.developer.environment.items.back"));
        setItem((byte) 40,createItem(currentMaterial,1));
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        new WorldEnvironmentMenu(player, devPlanet).open(player);
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;
        Material material = currentItem.getType();
        if (devPlanet != null && devPlanet.isLoaded() && devPlanet.getPlanet().getWorldPlayers().canDevelop(player)) {
            switch (type.toLowerCase()) {
                case "floor" -> {
                    if (platform == null) {
                        for (DevPlatform p : devPlanet.getPlatforms()) {
                            p.setFloorMaterial(material);
                        }
                    } else {
                        platform.setFloorMaterial(material);
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR,100,1);
                }
                case "event" -> {
                    if (platform == null) {
                        for (DevPlatform p : devPlanet.getPlatforms()) {
                            p.setEventMaterial(material);
                        }
                    } else {
                        platform.setEventMaterial(material);
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR,100,1);
                }
                case "action" -> {
                    if (platform == null) {
                        for (DevPlatform p : devPlanet.getPlatforms()) {
                            p.setActionMaterial(material);
                        }
                    } else {
                        platform.setActionMaterial(material);
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR,100,1);
                }
            }
        }
        player.closeInventory();
    }

    @Override
    protected List<Object> getElements() {
        return new ArrayList<>(materials);
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
}
