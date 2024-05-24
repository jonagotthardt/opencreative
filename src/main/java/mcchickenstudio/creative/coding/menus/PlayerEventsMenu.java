/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.coding.menus;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import mcchickenstudio.creative.coding.menus.executors.PlayerExecutorSubtype;
import mcchickenstudio.creative.menu.LegacyMenu;
import mcchickenstudio.creative.utils.MessageUtils;

import java.util.*;

import static mcchickenstudio.creative.utils.MessageUtils.*;

 public class PlayerEventsMenu extends LegacyMenu {

    public static Map<Player,Integer> openedPage = new HashMap<>();
    public static Map<Player,Location> signLocation = new HashMap<>();
    public static int[] eventsSlots = {12,13,14,15,16,21,22,23,24,25,30,31,32,33,34,39,40,41,42,43};

    public PlayerEventsMenu(Player player, int page, List<PlayerExecutorSubtype> executors, Location signLoc) {

        super(6,getLocaleMessage("blocks.event_player",false));

        int[] eventsSlots = PlayerEventsMenu.eventsSlots;
        Map<Integer,ItemStack> items = new HashMap<>();
        PlayerEventsMenu.signLocation.put(player,signLoc);

        int[] decorationSlots = {1,10,19,28,37,46};
        for (int slot : decorationSlots) {
            ItemStack decorationItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = decorationItem.getItemMeta();
            meta.setDisplayName(" ");
            decorationItem.setItemMeta(meta);
            items.put(slot,decorationItem);
        }

        int[] categoriesSlots = new int[]{0, 9, 18, 27, 36, 45};
        PlayerEventCategory[] eventCategories = PlayerEventsMenu.PlayerEventCategory.values();
        for(int slotCategory = 0; slotCategory < categoriesSlots.length-1; slotCategory++) {
            PlayerEventCategory eventCategory = eventCategories[slotCategory];
            ItemStack decorationItem = new ItemStack(eventCategory.getMaterial(), 1);
            ItemMeta meta = decorationItem.getItemMeta();
            meta.setDisplayName(MessageUtils.getLocaleItemName(eventCategory.getMessagePath() + ".name"));
            meta.setLore(MessageUtils.getLocaleItemDescription(eventCategory.getMessagePath() + ".lore"));
            decorationItem.setItemMeta(meta);
            decorationItem.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            items.put(categoriesSlots[slotCategory], decorationItem);
        }

        if (executors.isEmpty()) {
            ItemStack noEventsItem = getNoEventsMenu();
            items.put(23,noEventsItem);
        } else {
            List<List<PlayerExecutorSubtype>> allPages = getPagesForEvents(executors);
            int pageToOpen = page;
            if (page > allPages.size() || page < 1) pageToOpen = 1;
            if (pageToOpen > 1) {
                items.put(47,getPreviousPageButton());
            }
            if (pageToOpen < allPages.size()) {
                items.put(53,getNextPageButton(pageToOpen+1));
            }
            int slot = 0;
            for (PlayerExecutorSubtype subtype: allPages.get(pageToOpen-1)) {
                ItemStack item = subtype.getIcon();
                items.put(eventsSlots[slot],item);
                slot++;
            }

            openedPage.put(player,pageToOpen);
        }
        setItems(items);
    }

    // Показать инвентарь игроку
    public static void openInventory(Player player, int page, Location signLoc) {
        player.openInventory(new PlayerEventsMenu(player,page,PlayerEventCategory.WORLD.getExecutorByCategory(),signLoc).getInventory());
    }

     public static void openInventory(Player player, PlayerEventCategory eventCategory, Location signLoc) {
         player.openInventory(new PlayerEventsMenu(player,1, eventCategory.getExecutorByCategory(),signLoc).getInventory());
     }




    public static ItemStack getNoEventsMenu() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLocaleItemName("items.developer.categories.events.no-events.name"));
        meta.setLore(getLocaleItemDescription("items.developer.categories.events.no-events.lore"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.LURE, 1);
        return item;
    }

    public static ItemStack getNextPageButton(int page) {
        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLocaleItemName("menus.all-worlds.items.next-page.name").replace("page",String.valueOf(page)));
        meta.setLore(getLocaleItemDescription("menus.all-worlds.items.next-page.lore"));
        item.setAmount(page);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getPreviousPageButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLocaleItemName("menus.all-worlds.items.previous-page.name"));
        meta.setLore(getLocaleItemDescription("menus.all-worlds.items.previous-page.lore"));
        item.setItemMeta(meta);
        return item;
    }

    private static List<List<PlayerExecutorSubtype>> getPagesForEvents(List<PlayerExecutorSubtype> executors) {

        List<List<PlayerExecutorSubtype>> pages = new ArrayList<>();

        int pageSize = 20;
        int pageCount = (int) Math.ceil((double) executors.size() / pageSize);

        for (int i = 0; i < pageCount; i++) {
            int fromIndex = i * pageSize;
            int toIndex = Math.min((i + 1) * pageSize, executors.size());

            List<PlayerExecutorSubtype> sublist = executors.subList(fromIndex, toIndex);
            ArrayList<PlayerExecutorSubtype> page = new ArrayList<>(sublist);

            pages.add(page);
        }

        return pages;
    }

    public static PlayerEventCategory getCategoryClicked(Material material) {

         for (PlayerEventCategory eventCategory : PlayerEventCategory.values()) {
             if (eventCategory.getMaterial() == material) return eventCategory;
         }
         return null;

    }

    public enum PlayerEventCategory {

        WORLD("world",Material.BEACON),
        WORLD_INTERACTION("world-interaction",Material.GRASS_BLOCK),
        MOVEMENT("movement",Material.LEATHER_BOOTS),
        INVENTORY("inventory",Material.CHEST),
        COMBAT("combat",Material.GOLDEN_SWORD);

        private final String messagePath;
        private final Material material;

        PlayerEventCategory(String messagePath, Material material) {
            this.messagePath = messagePath;
            this.material = material;
        }

        public String getMessagePath() {
            return "items.developer.categories.events." + messagePath;
        }

        public Material getMaterial() {
            return this.material;
        }

        public List<PlayerExecutorSubtype> getExecutorByCategory() {
            List<PlayerExecutorSubtype> playerExecutorSubtypes = new ArrayList<>();
            for (PlayerExecutorSubtype subtype : PlayerExecutorSubtype.values()) {
                if (subtype.getCategory() == this) {
                    playerExecutorSubtypes.add(subtype);
                }
            }
            return playerExecutorSubtypes;
        }

        public String getLocaleName() {
            return getLocaleMessage("items.developer.actions." + this.messagePath + ".name");
        }
    }
}
