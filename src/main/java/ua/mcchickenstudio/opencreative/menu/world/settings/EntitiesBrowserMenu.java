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

package ua.mcchickenstudio.opencreative.menu.world.settings;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.menu.AbstractListMenu;
import ua.mcchickenstudio.opencreative.menu.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menu, that displays specified list of entities in world.
 * Player can teleport to entity or remove it.
 */
public class EntitiesBrowserMenu extends AbstractListMenu {

    private final Planet planet;
    private final List<ParameterButton> buttons = new ArrayList<>();
    private final ItemStack REMOVE_ALL = createItem(Material.BARRIER,1,"menus.entities-browser.items.remove-all");
    private final ItemStack BACK_TO_SETTINGS = createItem(Material.SPECTRAL_ARROW,1,"menus.entities-browser.items.back");

    public EntitiesBrowserMenu(Player player, Planet planet) {
        super(getLocaleMessage("menus.entities-browser.title",false), player);
        this.planet = planet;
        itemsSlots = new byte[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        noElementsPageButtonSlot = 13;
        decorationSlots = new byte[]{46,52};
        charmsBarSlots = new byte[]{45,48,50};
        previousPageButtonSlot = 45;
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof Entity entity) {
            return createEntityItem(entity);
        }
        return null;
    }

    private ItemStack createEntityItem(Entity entity) {
        ItemStack item = createItem(getEntityMaterial(entity),1,"menus.entities-browser.items.entity");
        replacePlaceholderInLore(item,"%name%",entity.getName().substring(0,Math.min(20,entity.getName().length())));
        replacePlaceholderInLore(item,"%type%", WordUtils.capitalize(entity.getType().name().toLowerCase().replace('_',' ')));
        replacePlaceholderInLore(item,"%x%",entity.getLocation().getBlockX());
        replacePlaceholderInLore(item,"%y%",entity.getLocation().getBlockY());
        replacePlaceholderInLore(item,"%z%",entity.getLocation().getBlockZ());
        setPersistentData(item,getItemTypeKey(),entity.getUniqueId().toString());
        return item;
    }

    private Material getEntityMaterial(Entity entity) {
        Material material = Material.getMaterial(entity.getType().name());
        if (material == null && entity.getType().isAlive()) {
            material = Material.getMaterial(entity.getType().name()+"_SPAWN_EGG");
        } else if (entity instanceof Item item) {
            return item.getItemStack().getType();
        } else if (entity instanceof ItemDisplay item) {
            return item.getItemStack().getType();
        }
        return material != null ? material : Material.HEAVY_CORE;
    }

    @Override
    protected void fillOtherItems() {
        ParameterButton type = new ParameterButton(
                "all",
                List.of("all","friendly","monsters","items","transport","decoration","not-living"),
                "type",
                "menus.all-worlds",
                "menus.entities-browser.items.type",
                List.of(Material.HOPPER, Material.PIGLIN_HEAD, Material.ZOMBIE_HEAD, Material.POISONOUS_POTATO, Material.BIRCH_CHEST_BOAT, Material.PAINTING,Material.PUMPKIN_SEEDS)
        );
        buttons.add(type);
        setItem((byte) 47,createItem(Material.RED_STAINED_GLASS_PANE,1));
        setItem((byte) 48,type.getItem());
        setItem((byte) 50,REMOVE_ALL);
        setItem((byte) 51,createItem(Material.RED_STAINED_GLASS_PANE,1));
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        for (ParameterButton button : buttons) {
            if (itemEquals(item,button.getItem(true))) {
                if (event.getRawSlot() == 48) {
                    button.next();
                    elements.clear();
                    switch (button.getCurrentChoice()) {
                        case 2 -> elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof LivingEntity && (!(entity instanceof Monster))).toList());
                        case 3 -> elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof Enemy).toList());
                        case 4 -> elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof Item).toList());
                        case 5 -> elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof Vehicle).toList());
                        case 6 -> elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof Painting || entity instanceof ArmorStand || entity instanceof TextDisplay || entity instanceof ItemDisplay || entity instanceof BlockDisplay || entity instanceof ItemFrame).toList());
                        case 7 -> elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> !(entity instanceof LivingEntity)).toList());
                        default -> elements.addAll(getElements());
                    }
                    fillElements(getCurrentPage());
                    fillArrowsItems(getCurrentPage());
                    setItem((byte) 48, button.getItem());
                    updateSlot((byte) 48);
                    player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER,100,1.2F);
                    return;
                }
            }
        }
        if (itemEquals(item,BACK_TO_SETTINGS) && planet.isOwner(player)) {
            new WorldSettingsMenu(planet,player).open(player);
        } else if (itemEquals(item,REMOVE_ALL)) {
            if (elements.isEmpty()) return;
            int count = elements.size();
            for (Object element : new ArrayList<>(elements)) {
                if (element instanceof Entity entity) {
                    elements.remove(entity);
                    entity.remove();
                }
            }
            for (Player p : planet.getPlayers()) {
                if (planet.getWorldPlayers().canBuild(p)) {
                    p.sendMessage(getLocaleMessage("menus.entities-browser.removed-all",player).replace("%count%",String.valueOf(count)));
                }
            }
            elements.removeIf(e -> ((Entity) e).isDead());
            fillElements(currentPage);
            fillArrowsItems(currentPage);
        }
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        if (!planet.isLoaded() || !(planet.getWorldPlayers().canBuild(player))) {
            player.closeInventory();
            return;
        }
        String uuidString = getItemType(currentItem);
        if (uuidString.isEmpty()) {
            return;
        }
        UUID uuid = UUID.fromString(uuidString);
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) {
            elements.removeIf(e -> ((Entity) e).getUniqueId().equals(uuid));
            elements.removeIf(e -> ((Entity) e).isDead());
            fillElements(currentPage);
            fillArrowsItems(currentPage);
            return;
        }
        switch (event.getClick()) {
            case LEFT -> {
                if (PlayerUtils.isEntityInDevPlanet(player)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 0.3f);
                    return;
                }
                player.closeInventory();
                player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100,0.8f);
                player.teleport(entity.getLocation());
            }
            case RIGHT -> {
                if (PlayerUtils.isEntityInDevPlanet(player)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 0.3f);
                    return;
                }
                player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_CAST_SPELL,100,0.8f);
                entity.teleport(player.getLocation());
                if (elements.removeIf(e -> ((Entity) e).isDead())) {
                    fillElements(currentPage);
                    fillArrowsItems(currentPage);
                }
            }
            case SHIFT_LEFT -> {
                for (Player p : planet.getPlayers()) {
                    if (planet.getWorldPlayers().canBuild(p)) {
                        p.sendMessage(getLocaleMessage("menus.entities-browser.removed",player)
                                .replace("%name%",entity.getName().substring(0,Math.min(20,entity.getName().length())))
                                .replace("%x%",String.valueOf(entity.getLocation().getBlockX()))
                                .replace("%y%",String.valueOf(entity.getLocation().getBlockY()))
                                .replace("%z%",String.valueOf(entity.getLocation().getBlockZ())));
                    }
                }
                player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS,100f,1.6f);
                elements.remove(entity);
                entity.remove();
                elements.removeIf(e -> ((Entity) e).isDead());
                fillElements(currentPage);
                fillArrowsItems(currentPage);
                player.updateInventory();
            }
        }
    }

    @Override
    protected void fillArrowsItems(byte currentPage) {
        if (elements.isEmpty()) {
            setItem(noElementsPageButtonSlot, getNoElementsButton());
            setItem(previousPageButtonSlot, planet.isOwner(player) ? BACK_TO_SETTINGS : DECORATION_ITEM);
            setItem(nextPageButtonSlot, DECORATION_ITEM);
            setItem((byte) 50, DECORATION_ITEM);
            updateSlot(noElementsPageButtonSlot);
            updateSlot(previousPageButtonSlot);
            updateSlot(nextPageButtonSlot);
            updateSlot((byte) 50);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                this.currentPage = 1;
            }
            setItem(previousPageButtonSlot,currentPage > 1 ? getPreviousPageButton() : planet.isOwner(player) ? BACK_TO_SETTINGS : DECORATION_ITEM);
            updateSlot(previousPageButtonSlot);
            setItem(nextPageButtonSlot,currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_ITEM);
            updateSlot(nextPageButtonSlot);
            setItem((byte) 50, REMOVE_ALL);
            updateSlot((byte) 50);
        }
    }

    @Override
    protected List<Object> getElements() {
        if (!planet.isLoaded()) {
            return List.of();
        }
        List<Entity> entities = new ArrayList<>(planet.getTerritory().getWorld().getEntities().stream().filter(e -> !(e instanceof Player)).toList());
        entities.sort(Comparator.comparingInt(Entity::getTicksLived).reversed());
        return new ArrayList<>(entities);
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInLore(createItem(Material.ARROW,getCurrentPage()+1,"menus.entities-browser.items.next-page"),"%page%",getCurrentPage()+1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInLore(createItem(Material.SPECTRAL_ARROW,Math.max(1, getCurrentPage()-1),"menus.entities-browser.items.previous-page"),"%page%",getCurrentPage()-1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER,1,"menus.entities-browser.items.no-entities");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        player.playSound(player.getLocation(), Sound.ENTITY_PANDA_WORRIED_AMBIENT,100,0.1f);
    }

}
