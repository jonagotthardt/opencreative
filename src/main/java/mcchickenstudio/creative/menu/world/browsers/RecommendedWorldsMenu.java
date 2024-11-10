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

package mcchickenstudio.creative.menu.world.browsers;

import mcchickenstudio.creative.events.player.PlayerChat;
import mcchickenstudio.creative.menu.AbstractMenu;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class RecommendedWorldsMenu extends AbstractMenu {

    private final int[] featuredWorldsSlots = {10,11,12,13,14,15,16, 19,20,21,22,23,24,25, 28,29,30,31,32,33,34};
    private final ItemStack OWN_WORLDS = createItem(Material.REPEATING_COMMAND_BLOCK,1,"menus.all-worlds.items.own-worlds");
    private final ItemStack DECORATION_OWN_WORLDS = createItem(Material.CYAN_STAINED_GLASS_PANE,1);
    private final ItemStack SEARCH = createItem(Material.SPYGLASS,1,"menus.all-worlds.items.search");
    private final ItemStack ALL_WORLDS = createItem(Material.SPECTRAL_ARROW,1,"menus.recommended-worlds.items.all-worlds");

    public RecommendedWorldsMenu() {
        super((byte) 6, getLocaleMessage("menus.recommended-worlds.title",false));
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 45, SEARCH);
        setItem((byte) 46, DECORATION_PANE_ITEM);
        setItem((byte) 47, DECORATION_PANE_ITEM);
        setItem((byte) 48, DECORATION_OWN_WORLDS);
        setItem((byte) 49, OWN_WORLDS);
        setItem((byte) 50, DECORATION_OWN_WORLDS);
        setItem((byte) 51, DECORATION_PANE_ITEM);
        setItem((byte) 52, DECORATION_PANE_ITEM);
        setItem((byte) 53, ALL_WORLDS);
        List<Plot> featuredPlots = PlotManager.getInstance().getRecommendedPlots();
        if (featuredPlots.isEmpty()) {
            for (int slot : featuredWorldsSlots) {
                setItem((byte) slot,DECORATION_ITEM);
            }
            return;
        }
        Collections.shuffle(featuredPlots);
        byte index = 0;
        for (int slot : featuredWorldsSlots) {
            if (index < featuredPlots.size()) {
                setItem((byte) slot,featuredPlots.get(index).getInformation().getIcon());
                index++;
            } else {
                setItem((byte) slot,DECORATION_ITEM);
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        if (itemEquals(currentItem,SEARCH)) {
            player.closeInventory();
            String searchQuery = event.getClick() == ClickType.LEFT ? "world-name" : "id";
            player.sendTitle(getLocaleMessage("menus.all-worlds.items.search.title").replace("%search%", getLocaleMessage("menus.all-worlds.items.search." + searchQuery)), getLocaleMessage("menus.all-worlds.items.search.subtitle").replace("%search%", getLocaleMessage("menus.all-worlds.items.search." + searchQuery)));
            player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.usage", player).replace("%search%", getLocaleMessage("menus.all-worlds.items.search." + searchQuery)));
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_AMBIENT, 100, 1);
            PlayerChat.confirmation.put(player, event.getClick() == ClickType.LEFT ? "searchPlotByPlotName" : "searchPlotByID");
        } else if (itemEquals(currentItem,ALL_WORLDS)) {
            new WorldsBrowserMenu(player,PlotManager.getInstance().getPlots()).open(player);
        } else if (itemEquals(currentItem,OWN_WORLDS)) {
            player.closeInventory();
            OwnWorldsMenu.openInventory(player,1);
        } else if (!itemEquals(currentItem,DECORATION_ITEM) && Arrays.stream(featuredWorldsSlots).anyMatch(i -> i == event.getRawSlot())) {
            String worldID = getPersistentData(currentItem,getWorldIdKey());
            if (worldID.isEmpty()) {
                return;
            }
            Plot plot = PlotManager.getInstance().getPlotByCustomID(worldID);
            if (plot != null) {
                player.closeInventory();
                plot.connectPlayer(player);
            }
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(),Sound.BLOCK_ENDER_CHEST_OPEN,100,0.1F);
    }
}
