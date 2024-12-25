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

package ua.mcchickenstudio.opencreative.menu.world.settings;

import ua.mcchickenstudio.opencreative.events.planet.PlanetSharingChangeEvent;
import ua.mcchickenstudio.opencreative.listeners.player.PlayerChat;
import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import ua.mcchickenstudio.opencreative.menu.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.menu.world.WorldDeleteMobsMenu;
import ua.mcchickenstudio.opencreative.menu.world.WorldEnvironmentMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetFlags;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;

public class WorldSettingsMenu extends AbstractMenu {

    private final Planet planet;
    private final Player player;

    private final ItemStack playersControl = createItem(Material.PLAYER_HEAD,1,"menus.world-settings.items.planet-players");
    private final ItemStack parameters = createItem(Material.COMPARATOR,1,"menus.world-settings.items.planet-flags");
    private final ItemStack name = createItem(Material.BIRCH_SIGN,1,"menus.world-settings.items.change-name");
    private final ItemStack description = createItem(Material.WRITABLE_BOOK,1,"menus.world-settings.items.change-description");
    private final ItemStack category = createItem(Material.NAME_TAG,1,"menus.world-settings.items.change-category");
    private final ItemStack customID = createItem(Material.LEAD,1,"menus.world-settings.items.change-id");

    private final ItemStack buildMode = createItem(Material.BRICKS,1,"menus.world-settings.items.build-mode");
    private final ItemStack playMode = createItem(Material.DIAMOND_BLOCK,1,"menus.world-settings.items.play-mode");
    private final ItemStack devMode = createItem(Material.REPEATING_COMMAND_BLOCK,1,"menus.world-settings.items.dev-mode");

    private final ItemStack spawn = createItem(Material.ENDER_PEARL,1,"menus.world-settings.items.change-spawn");
    private final ParameterButton time;
    private final ItemStack removeMobs = createItem(Material.BRUSH,1,"menus.world-settings.items.remove-mobs");
    private final ItemStack environment = createItem(Material.AMETHYST_CLUSTER,1,"menus.world-settings.items.environment");
    private final ParameterButton autoSave;

    private final ParameterButton access;
    private ItemStack worldIcon;
    private final ItemStack advertise = createItem(Material.BEACON,1,"menus.world-settings.items.advertisement");

    public WorldSettingsMenu(Planet planet, Player player) {
        super((byte) 6, getLocaleMessage("menus.world-settings.title",false));
        this.planet = planet;
        this.player = player;
        worldIcon = getPlanetIcon();
        access = new ParameterButton(planet.getSharing().name().toLowerCase(), List.of("public","private"),"access","menus.world-settings","menus.world-settings.items.change-sharing",List.of(Material.SPRUCE_DOOR,Material.IRON_DOOR));
        Boolean isTimeChanging = planet.getTerritory().getWorld().getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
        long currentTime = planet.getTerritory().getWorld().getTime();
        boolean isNight = currentTime >= 15000L && currentTime <= 23000;
        boolean isEvening = currentTime >= 12500 && currentTime < 15000;
        int timeValue = (isTimeChanging != null && isTimeChanging ? 4 : isNight ? 3 : isEvening ? 2 : 1);
        time = new ParameterButton(timeValue, List.of(1,2,3,4),"time","menus.world-settings","menus.world-settings.items.time",Material.CLOCK);
        autoSave = new ParameterButton(planet.getTerritory().getWorld().isAutoSave(), List.of(true,false),"autosave","menus.world-settings","menus.world-settings.items.autosave",List.of(Material.CHEST_MINECART,Material.TNT_MINECART));

    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 10, playersControl);
        setItem((byte) 11, parameters);
        setItem((byte) 19, name);
        setItem((byte) 20, description);
        setItem((byte) 28, category);
        setItem((byte) 29, customID);

        setItem((byte) 15, spawn);
        setItem((byte) 16, time.getItem());
        setItem((byte) 24, removeMobs);
        setItem((byte) 25, environment);
        setItem((byte) 33, autoSave.getItem());

        setItem((byte) 13, buildMode);
        setItem((byte) 22, playMode);
        setItem((byte) 31, devMode);

        setItem((byte) 45, DECORATION_PANE_ITEM);
        setItem((byte) 46, createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,1));

        setItem((byte) 47, access.getItem());
        setItem((byte) 49, worldIcon);
        setItem((byte) 51, advertise);

        setItem((byte) 52, createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,1));
        setItem((byte) 53, DECORATION_PANE_ITEM);
    }

    public ItemStack getPlanetIcon() {
        ItemStack item = createItem(planet.getSharing() == Planet.Sharing.PUBLIC ? planet.getInformation().getMaterial() : Material.BARRIER,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings.items.world.name").replace("%planetName%", planet.getInformation().getDisplayName()));
        List<String> lore = new ArrayList<>();
        for (String loreLine : MessageUtils.getLocaleItemDescription("menus.world-settings.items.world.lore")) {
            if (loreLine.contains("%planetDescription%")) {
                String[] newLines = planet.getInformation().getDescription().split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%planetDescription%", ChatColor.translateAlternateColorCodes('&',newLine)));
                }
            } else {
                lore.add(MessageUtils.parsePlanetLines(planet,loreLine));
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        clearItemFlags(item);
        return item;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        if (itemEquals(currentItem,name)) {
            player.showTitle(Title.title(
                    toComponent(getLocaleMessage("settings.world-name.title")), toComponent(getLocaleMessage("settings.world-name.subtitle")),
                    Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(30), Duration.ofMillis(130))
            ));
            player.sendMessage(getLocaleMessage("settings.world-name.usage").replace("%player%", player.getName()));
            player.closeInventory();
            if (!(PlayerChat.confirmation.containsKey(player))) {
                PlayerChat.confirmation.put(player, "title");
            }
        } else if (itemEquals(currentItem,description)) {
            player.showTitle(Title.title(
                    toComponent(getLocaleMessage("settings.world-description.title")), toComponent(getLocaleMessage("settings.world-description.subtitle")),
                    Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(30), Duration.ofMillis(130))
            ));            player.sendMessage(getLocaleMessage("settings.world-description.usage"));
            player.closeInventory();
            if (!(PlayerChat.confirmation.containsKey(player))) {
                PlayerChat.confirmation.put(player, "description");
            }
        } else if (itemEquals(currentItem,customID)) {
            player.showTitle(Title.title(
                    toComponent(getLocaleMessage("settings.world-id.title")), toComponent(getLocaleMessage("settings.world-id.subtitle")),
                    Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(30), Duration.ofMillis(130))
            ));
            player.sendMessage(getLocaleMessage("settings.world-id.usage").replace("%player%", player.getName()));
            player.closeInventory();
            if (!(PlayerChat.confirmation.containsKey(player))) {
                PlayerChat.confirmation.put(player, "id");
            }
        } else if (itemEquals(currentItem,spawn)) {
            if (isEntityInDevPlanet(player)) {
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 0.3f);
                return;
            }
            player.closeInventory();
            if (event.getClick().isLeftClick()) {
                player.getWorld().setSpawnLocation(player.getLocation());
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("settings.world-spawn.title")), toComponent(getLocaleMessage("settings.world-spawn.subtitle")),
                        Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(30), Duration.ofMillis(130))
                ));
                player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_CAST_SPELL,100,0.8f);
            } else {
                player.teleport(player.getWorld().getSpawnLocation());
                player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100,0.8f);
            }
        } else if (itemEquals(currentItem,category)) {
            new WorldSettingsCategoryMenu().open(player);
        } else if (itemEquals(currentItem,playersControl)) {
            WorldSettingsPlayersMenu.openInventory(player);
        } else if (itemEquals(currentItem,parameters)) {
            new WorldSettingsFlagsMenu().open(player);
        } else if (itemEquals(currentItem,environment)) {
            new WorldEnvironmentMenu(player, planet.getDevPlanet()).open(player);
        } else if (itemEquals(currentItem,advertise)) {
            player.performCommand("ad");
            player.closeInventory();
        } else if (itemEquals(currentItem,removeMobs)) {
            new WorldDeleteMobsMenu().open(player);
        } else if (itemEquals(currentItem,buildMode)) {
            player.performCommand("build");
        }  else if (itemEquals(currentItem,playMode)) {
            player.performCommand("play");
        } else if (itemEquals(currentItem,devMode)) {
            player.performCommand("dev");
        } else if (itemEquals(currentItem,access.getItem())) {
            access.next();
            setItem((byte) event.getRawSlot(),access.getItem());
            updateSlot((byte) event.getRawSlot());
            if ("public".equals(access.getCurrentValue().toString())) {
                PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PUBLIC,player);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) return;
                planet.setSharing(Planet.Sharing.PUBLIC);
                player.sendMessage(getLocaleMessage("settings.world-sharing.enabled"));
                player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 100, 1);
                planet.getInformation().updateIcon();
            } else {
                PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PRIVATE,player);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) return;
                planet.setSharing(Planet.Sharing.PRIVATE);
                player.sendMessage(getLocaleMessage("settings.world-sharing.disabled"));
                player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, 100, 0.6f);
                planet.getInformation().updateIcon();
            }
            worldIcon = getPlanetIcon();
            setItem((byte) 49,getPlanetIcon());
            updateSlot((byte) 49);
        } else if (itemEquals(currentItem,time.getItem())) {
            time.next();
            player.playSound(player.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,100,1.2f);
            setItem((byte) event.getRawSlot(),time.getItem());
            updateSlot((byte) event.getRawSlot());
            if (time.getCurrentValue().equals(1)) {
                planet.getTerritory().getWorld().setTime(1000L);
                planet.getTerritory().getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                planet.setFlagValue(PlanetFlags.PlanetFlag.DAY_CYCLE, (byte) 1);
            } else if (time.getCurrentValue().equals(2)) {
                planet.getTerritory().getWorld().setTime(12500L);
                planet.getTerritory().getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                planet.setFlagValue(PlanetFlags.PlanetFlag.DAY_CYCLE, (byte) 2);
            } else if (time.getCurrentValue().equals(3)) {
                planet.getTerritory().getWorld().setTime(15000L);
                planet.getTerritory().getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                planet.setFlagValue(PlanetFlags.PlanetFlag.DAY_CYCLE, (byte) 3);
            } else {
                planet.getTerritory().getWorld().setTime(1000L);
                planet.getTerritory().getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                planet.setFlagValue(PlanetFlags.PlanetFlag.DAY_CYCLE, (byte) 4);
            }
        } else if (itemEquals(currentItem,autoSave.getItem())) {
            autoSave.next();
            setItem((byte) event.getRawSlot(),autoSave.getItem());
            updateSlot((byte) event.getRawSlot());
            if (autoSave.getCurrentValue().equals(true)) {
                planet.getTerritory().setAutoSave(true);
                player.playSound(player.getLocation(),Sound.BLOCK_BEACON_ACTIVATE,100,0.7f);
            } else if (autoSave.getCurrentValue().equals(false)) {
                planet.getTerritory().setAutoSave(false);
                player.playSound(player.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,100,0.3f);
            }
        } else if (itemEquals(currentItem,worldIcon)) {
            if (event.getCursor().isEmpty()) {
                player.sendMessage(getLocaleMessage("settings.world-icon.error"));
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 0.3f);
            } else {
                planet.getInformation().setIconMaterial(event.getCursor().getType());
                planet.getInformation().updateIcon();
                player.sendMessage(getLocaleMessage("settings.world-icon.changed"));
                worldIcon = getPlanetIcon();
                setItem((byte) 49,getPlanetIcon());
                updateSlot((byte) 49);
                event.setCursor(null);
            }
        }

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if (!planet.isOwner(event.getPlayer().getName())) {
            event.setCancelled(true);
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE,100,0.1f);
    }
}
