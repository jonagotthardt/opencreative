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

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import ua.mcchickenstudio.opencreative.menu.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.menu.world.settings.WorldSettingsMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public class WorldEnvironmentMenu extends AbstractMenu {

    private final Player player;
    private final DevPlanet devPlanet;
    private final DevPlatform devPlatform;

    private final ItemStack back = createItem(Material.ARROW,1,"menus.developer.environment.items.back");

    private final ParameterButton debug;

    private final ItemStack variablesSize = createItem(Material.MAGMA_CREAM,1,"menus.developer.environment.items.variables-size");
    private final ItemStack variablesList = createItem(Material.BOOKSHELF,1,"menus.developer.environment.items.variables-list");
    private final ItemStack clearVariables = createItem(Material.BRUSH,1,"menus.developer.environment.items.clear-variables");

    private final ItemStack info;

    private final ParameterButton containers;
    private final ParameterButton time;
    private final ItemStack floorMaterial;
    private final ItemStack eventMaterial;
    private final ItemStack actionMaterial;


    public WorldEnvironmentMenu(Player player, DevPlanet devPlanet) {
        super((byte) 6, getLocaleMessage("menus.developer.environment.title"));
        this.player = player;
        this.devPlanet = devPlanet;
        this.devPlatform = isDevPlanet(player.getWorld()) ? devPlanet.getPlatformInLocation(player.getLocation()) : null;
        debug = new ParameterButton(devPlanet.getPlanet().isDebug() ? "all" : "disabled", List.of("disabled","all"),"debug","menus.developer.environment","menus.developer.environment.items.debug",List.of(Material.PUFFERFISH_BUCKET,Material.PUFFERFISH));
        containers = new ParameterButton(devPlanet.getContainerMaterial() == Material.CHEST ? "chest" : "barrel", List.of("chest","barrel"),"containers","menus.developer.environment","menus.developer.environment.items.containers",List.of(Material.CHEST,Material.BARREL));
        info = createItem(Material.AMETHYST_CLUSTER,1,"menus.developer.environment.items.info");
        replacePlaceholderInLore(info,"%executors%", devPlanet.getPlanet().getTerritory().getScript().getExecutors().getExecutorsList().size());
        replacePlaceholderInLore(info,"%scoreboards%", devPlanet.getPlanet().getTerritory().getScoreboards().size());
        replacePlaceholderInLore(info,"%scoreboards-limit%", devPlanet.getPlanet().getLimits().getScoreboardsLimit());
        replacePlaceholderInLore(info,"%bossbars%", devPlanet.getPlanet().getTerritory().getBossBars().size());
        replacePlaceholderInLore(info,"%bossbars-limit%", devPlanet.getPlanet().getLimits().getBossBarsLimit());
        replacePlaceholderInLore(info,"%variables%", devPlanet.getPlanet().getVariables().getTotalVariablesAmount());
        replacePlaceholderInLore(info,"%variables-limit%", devPlanet.getPlanet().getLimits().getVariablesAmountLimit());
        replacePlaceholderInLore(info,"%executor-calls-limit%", devPlanet.getPlanet().getLimits().getCodeOperationsLimit());
        replacePlaceholderInLore(info,"%planetID%", devPlanet.getPlanet().getId());
        replacePlaceholderInLore(info,"%version%", OpenCreative.getVersion());
        long currentTime = devPlanet.getWorld() == null ? 0 : devPlanet.getWorld().getTime();
        boolean isMorning = currentTime >= 0L && currentTime < 6000L;
        boolean isNight = currentTime >= 15000L && currentTime <= 23000L;
        boolean isEvening = currentTime >= 12500L && currentTime < 15000L;
        time = new ParameterButton(isMorning ? "morning" : isNight ? "night" : isEvening ? "evening" : "day", List.of("morning","day","evening","night"),"time","menus.developer.environment","menus.developer.environment.items.time",Material.CLOCK);
        floorMaterial = createItem(devPlatform != null ? devPlatform.getFloorMaterial() : DevPlanet.getDefaultFloorMaterial(),1,"menus.developer.environment.items.floor-material");
        eventMaterial = createItem(devPlatform != null ? devPlatform.getEventMaterial() : DevPlanet.getDefaultEventMaterial(),1,"menus.developer.environment.items.event-material");
        actionMaterial = createItem(devPlatform != null ? devPlatform.getActionMaterial() : DevPlanet.getDefaultActionMaterial(),1,"menus.developer.environment.items.action-material");
    }

    @Override
    public void fillItems(Player player) {

        setItem((byte) 10,debug.getItem());
        setItem((byte) 12,variablesSize);
        setItem((byte) 14,variablesList);
        setItem((byte) 16,clearVariables);

        setItem((byte) 28, devPlanet.getWorld() != null ? containers.getItem() : DECORATION_ITEM);
        setItem((byte) 30, devPlanet.getWorld() != null ? time.getItem() : DECORATION_ITEM);
        setItem((byte) 32, devPlanet.getWorld() != null ? floorMaterial : DECORATION_ITEM);
        setItem((byte) 33, devPlanet.getWorld() != null ? eventMaterial : DECORATION_ITEM);
        setItem((byte) 34, devPlanet.getWorld() != null ? actionMaterial : DECORATION_ITEM);

        setItem((byte) 45, devPlanet.getPlanet().isOwner(player) ? back : DECORATION_PANE_ITEM);
        setItem((byte) 46,DECORATION_PANE_ITEM);

        setItem((byte) 47,createItem(Material.MAGENTA_STAINED_GLASS_PANE,1));
        setItem((byte) 49,info);
        setItem((byte) 51,createItem(Material.MAGENTA_STAINED_GLASS_PANE,1));
        setItem((byte) 52,DECORATION_PANE_ITEM);
        setItem((byte) 53,DECORATION_PANE_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;
        if (itemEquals(currentItem,variablesList)) {
            player.performCommand("env vars list");
            player.closeInventory();
        } else if (itemEquals(currentItem,variablesSize)) {
            player.performCommand("env vars size");
            player.closeInventory();
        } else if (itemEquals(currentItem,clearVariables)) {
            player.performCommand("env vars clear");
            player.closeInventory();
        } else if (itemEquals(currentItem,time.getItem())) {
            if (devPlanet.getWorld() == null) return;
            time.next();
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,100,1.2f);
            setItem((byte) event.getRawSlot(),time.getItem());
            updateSlot((byte) event.getRawSlot());
            if ("night".equals(time.getCurrentValue().toString())) {
                devPlanet.getWorld().setTime(15000L);
            } else if ("evening".equals(time.getCurrentValue().toString())) {
                devPlanet.getWorld().setTime(12500L);
            } else if ("day".equals(time.getCurrentValue().toString())) {
                devPlanet.getWorld().setTime(6000L);
            } else {
                devPlanet.getWorld().setTime(0L);
            }
        } else if (itemEquals(currentItem,containers.getItem())) {
            if (devPlanet.getWorld() == null) return;
            player.performCommand("env barrel");
            player.closeInventory();
        } else if (itemEquals(currentItem,debug.getItem())) {
            player.performCommand("env debug " + (debug.getCurrentValue().toString().equals("all") ? "disable" : "enable"));
            player.closeInventory();
        } else if (itemEquals(currentItem,back)) {
            if (devPlanet.getPlanet().isOwner(player)) {
                new WorldSettingsMenu(devPlanet.getPlanet(),player).open(player);
            }
        } else if (itemEquals(currentItem,eventMaterial)) {
            new WorldEnvironmentColorMenu(player, devPlanet,devPlatform,"event").open(player);
        } else if (itemEquals(currentItem,actionMaterial)) {
            new WorldEnvironmentColorMenu(player, devPlanet,devPlatform,"action").open(player);
        } else if (itemEquals(currentItem,floorMaterial)) {
            new WorldEnvironmentColorMenu(player, devPlanet,devPlatform,"floor").open(player);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_CHIME,100,0.1f);
    }
}
