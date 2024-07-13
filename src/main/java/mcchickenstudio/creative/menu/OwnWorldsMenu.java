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

package mcchickenstudio.creative.menu;

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.MessageUtils;
import mcchickenstudio.creative.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import mcchickenstudio.creative.plots.PlotManager;

import java.util.*;

public class OwnWorldsMenu extends LegacyMenu {

    public static final Map<Player,Integer> openedPage = new HashMap<>();
    public static final int[] worldSlots = {10,11,12,13,14,15,16,21,22,23,24,25};
    public static final int[] worldCreationSlots = {37,38,39,40,41,42,43,44,45};

    public OwnWorldsMenu(Player player, int page) {

        super(6, MessageUtils.getLocaleMessage("menus.own-worlds.title",false));

        int[] worldSlots = OwnWorldsMenu.worldSlots;

        Map<Integer,ItemStack> items = new HashMap<>();

        items.put(46,getAllWorldsButton());

        if (PlotManager.getInstance().getPlots() == null || PlotManager.getInstance().getPlots().isEmpty() || getPagesForPlots(player).isEmpty()) {
            ItemStack noWorldsItem = getNoWorldsButton();
            items.put(13,noWorldsItem);
        } else {

            List<List<Plot>> allPages = getPagesForPlots(player);
            int pageToOpen = page;

            if (page > allPages.size() || page < 1) pageToOpen = 1;
            if (pageToOpen > 1) {
                items.put(47,getPreviousPageButton(pageToOpen));
            }
            if (pageToOpen < allPages.size()) {
                items.put(53,getNextPageButton(pageToOpen));
            }

            int slot = 0;
            for (Plot plot: allPages.get(pageToOpen-1)) {
                if (plot.getOwner().equalsIgnoreCase(player.getName())) {
                    Material material = plot.getPlotIconMaterial();
                    if (!(plot.getPlotSharing() == Plot.Sharing.PUBLIC)) material = Material.BARRIER;
                    ItemStack item = new ItemStack(material);
                    ItemMeta meta = item.getItemMeta();
                    meta.displayName(Component.text(plot.getPlotName()));
                    List<String> lore = new ArrayList<>();
                    for (String loreLine : MessageUtils.getLocaleItemDescription("menus.own-worlds.items.world.lore")) {
                        if (loreLine.contains("%plotDescription%")) {
                            String[] newLines = plot.getPlotDescription().split("\\\\n");
                            for (String newLine : newLines) {
                                lore.add(loreLine.replace("%plotDescription%", ChatColor.translateAlternateColorCodes('&',newLine)));
                            }
                        } else {
                            lore.add(MessageUtils.parsePlotLines(plot,loreLine.replace("%id%", MessageUtils.getLocaleMessage("menus.own-worlds.items.world.id",false) + plot.getPlotCustomID())));
                        }
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
                    meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addItemFlags(ItemFlag.HIDE_DYE);
                    meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                    items.put(worldSlots[slot],item);
                    slot++;
                }
            }

            if (pageToOpen > 1) setTitle(MessageUtils.getLocaleMessage("menus.own-worlds.title-pages",false).replace("%page%",String.valueOf(pageToOpen)).replace("%pages%",String.valueOf(allPages.size())));
            openedPage.put(player,pageToOpen);
        }

        int plotsAmount = PlotManager.getInstance().getPlayerPlots(player).size();
        int plotsLimit = PlayerUtils.getPlayerPlotsLimit(player);

        // Если у игрока созданных плотов меньше чем лимит
        if (plotsAmount < plotsLimit) {

            int createWorldButtons = plotsLimit-plotsAmount;
            if (createWorldButtons > 7) createWorldButtons = 7;

            for (int createWorldButtonSlot = 0; createWorldButtonSlot < createWorldButtons; createWorldButtonSlot++) {
                ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(MessageUtils.getLocaleItemName("menus.own-worlds.items.create-world.name"));
                meta.setLore(MessageUtils.getLocaleItemDescription("menus.own-worlds.items.create-world.lore"));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_DYE);
                meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                item.setItemMeta(meta);
                items.put(worldCreationSlots[createWorldButtonSlot],item);
            }
        } else {
            ItemStack item = new ItemStack(Material.RED_STAINED_GLASS);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtils.getLocaleItemName("menus.own-worlds.items.limit.name"));
            List<String> lore = new ArrayList<>();
            for (String loreLine : MessageUtils.getLocaleItemDescription("menus.own-worlds.items.limit.lore")) {
                lore.add(loreLine.replace("%plots%",String.valueOf(plotsAmount)).replace("%limit%",String.valueOf(plotsLimit)));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            items.put(40,item);
        }

        setItems(items);
    }

    public static void openInventory(Player player, int page) {
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 100, 1.4f);
        player.openInventory(new OwnWorldsMenu(player,page).getInventory());
    }

    public static ItemStack getAllWorldsButton() {
        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.own-worlds.items.all-worlds.name"));
        meta.setLore(MessageUtils.getLocaleItemDescription("menus.own-worlds.items.all-worlds.lore"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getNoWorldsButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.own-worlds.items.no-worlds.name"));
        meta.setLore(MessageUtils.getLocaleItemDescription("menus.own-worlds.items.no-worlds.lore"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.LURE, 1);
        return item;
    }

    public static ItemStack getNextPageButton(int page) {
        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.all-worlds.items.next-page.name").replace("%page%",String.valueOf(page+1)));
        meta.setLore(MessageUtils.getLocaleItemDescription("menus.all-worlds.items.next-page.lore"));
        item.setAmount(page+1);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getPreviousPageButton(int page) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.all-worlds.items.previous-page.name").replace("%page%",String.valueOf(page-1)));
        meta.setLore(MessageUtils.getLocaleItemDescription("menus.all-worlds.items.previous-page.lore"));
        item.setAmount(page-1);
        item.setItemMeta(meta);
        return item;
    }

    private static List<List<Plot>> getPagesForPlots(Player player) {

        List<List<Plot>> pages = new ArrayList<>();
        List<Plot> playerPlots = new ArrayList<>();

        for (Plot plot : PlotManager.getInstance().getPlots()) {
            if (plot.getOwner().equalsIgnoreCase(player.getName())) playerPlots.add(plot);
        }

        int pageSize = 24;
        int pageCount = (int) Math.ceil((double) playerPlots.size() / pageSize);

        Comparator<Plot> plotComparator = (plot1, plot2) -> Integer.compare(plot2.getOnline(), plot1.getOnline());
        playerPlots.sort(plotComparator);

        for (int i = 0; i < pageCount; i++) {
            int fromIndex = i * pageSize;
            int toIndex = Math.min((i + 1) * pageSize, playerPlots.size());

            List<Plot> sublist = playerPlots.subList(fromIndex, toIndex);
            ArrayList<Plot> page = new ArrayList<>(sublist);

            pages.add(page);
        }

        return pages;
    }

}
