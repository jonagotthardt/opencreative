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

package mcchickenstudio.creative.coding.menus;

 import mcchickenstudio.creative.coding.menus.conditions.PlayerConditionSubtype;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.enchantments.Enchantment;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemFlag;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.inventory.meta.ItemMeta;
 import mcchickenstudio.creative.menu.LegacyMenu;
 import mcchickenstudio.creative.utils.MessageUtils;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import static mcchickenstudio.creative.utils.MessageUtils.*;

 public class LegacyPlayerConditionsMenu extends LegacyMenu {

    public static Map<Player,Integer> openedPage = new HashMap<>();
    public static Map<Player,Location> signLocation = new HashMap<>();
    public static int[] actionSlots = {12,13,14,15,16,21,22,23,24,25,30,31,32,33,34,39,40,41,42,43};

    public LegacyPlayerConditionsMenu(Player player, int page, List<PlayerConditionSubtype> actionList, Location signLoc) {

        super(6,getLocaleMessage("blocks.condition_player",false));

        int[] eventsSlots = LegacyPlayerConditionsMenu.actionSlots;
        Map<Integer,ItemStack> items = new HashMap<>();
        LegacyPlayerConditionsMenu.signLocation.put(player,signLoc);

        int[] decorationSlots = {1,10,19,28,37,46};
        for (int slot : decorationSlots) {
            ItemStack decorationItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = decorationItem.getItemMeta();
            meta.setDisplayName(" ");
            decorationItem.setItemMeta(meta);
            items.put(slot,decorationItem);
        }

        int[] categoriesSlots = new int[]{0, 9, 18, 27, 36, 45};
        LegacyPlayerConditionsMenu.PlayerConditionCategory[] actionCategories = LegacyPlayerConditionsMenu.PlayerConditionCategory.values();
        for(int slotCategory = 0; slotCategory < categoriesSlots.length-3; slotCategory++) {
            LegacyPlayerConditionsMenu.PlayerConditionCategory actionCategory = actionCategories[slotCategory];
            ItemStack decorationItem = new ItemStack(actionCategory.getMaterial(), 1);
            ItemMeta meta = decorationItem.getItemMeta();
            meta.setDisplayName(MessageUtils.getLocaleItemName(actionCategory.getMessagePath() + ".name"));
            meta.setLore(MessageUtils.getLocaleItemDescription(actionCategory.getMessagePath() + ".lore"));
            decorationItem.setItemMeta(meta);
            decorationItem.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            items.put(categoriesSlots[slotCategory], decorationItem);
        }

        // Если действий нет
        if (actionList.isEmpty()) {
            ItemStack noActionsButton = getNoActionsButton();
            items.put(23,noActionsButton);
        } else {
            List<List<PlayerConditionSubtype>> allPages = getPagesForActions(actionList);
            int pageToOpen = page;

            if (page > allPages.size() || page < 1) pageToOpen = 1;
            if (pageToOpen > 1) {
                items.put(47,getPreviousPageButton());
            }
            if (pageToOpen < allPages.size()) {
                items.put(53,getNextPageButton(pageToOpen+1));
            }
            int slot = 0;
            for (PlayerConditionSubtype action : allPages.get(pageToOpen-1)) {
                ItemStack item = action.getIcon();
                items.put(eventsSlots[slot],item);
                slot++;
            }

            openedPage.put(player,pageToOpen);
        }
        setItems(items);
    }

    // Показать инвентарь игроку
    public static void openInventory(Player player, int page, Location signLoc) {
        player.openInventory(new LegacyPlayerConditionsMenu(player,page,PlayerConditionCategory.PARAMETERS.getConditionsByCategory(),signLoc).getInventory());
    }

    public static void openInventory(Player player, int page, List<PlayerConditionSubtype> actionList, Location signLoc) {
        player.openInventory(new LegacyPlayerConditionsMenu(player,page,actionList,signLoc).getInventory());
    }

     public static void openInventory(Player player, int page, PlayerConditionCategory category, Location signLoc) {
         player.openInventory(new LegacyPlayerConditionsMenu(player,page,category.getConditionsByCategory(),signLoc).getInventory());
     }

    public static ItemStack getNoActionsButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLocaleItemName("items.developer.categories.conditions.no-conditions.name"));
        meta.setLore(getLocaleItemDescription("items.developer.categories.conditions.no-conditions.lore"));
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

    public int getCurrentPage(Player player) {
        return openedPage.getOrDefault(player, 1);
    }

    private static List<List<PlayerConditionSubtype>> getPagesForActions(List<PlayerConditionSubtype> plotsList) {

        List<List<PlayerConditionSubtype>> pages = new ArrayList<>();

        int pageSize = 20;
        int pageCount = (int) Math.ceil((double) plotsList.size() / pageSize);

        for (int i = 0; i < pageCount; i++) {
            int fromIndex = i * pageSize;
            int toIndex = Math.min((i + 1) * pageSize, plotsList.size());

            List<PlayerConditionSubtype> sublist = plotsList.subList(fromIndex, toIndex);
            ArrayList<PlayerConditionSubtype> page = new ArrayList<>(sublist);

            pages.add(page);
        }

        return pages;
    }

     public enum PlayerConditionCategory {

         INVENTORY("inventory",Material.CHEST),
         BLOCK("block",Material.GRASS_BLOCK),
         PARAMETERS("parameters",Material.CRAFTING_TABLE);

         private final String messagePath;
         private final Material material;

         PlayerConditionCategory(String messagePath, Material material) {
             this.messagePath = messagePath;
             this.material = material;
         }

         public String getMessagePath() {
             return "items.developer.categories.conditions." + messagePath;
         }

         public Material getMaterial() {
             return this.material;
         }

         public List<PlayerConditionSubtype> getConditionsByCategory() {
             List<PlayerConditionSubtype> subtypes = new ArrayList<>();
             for (PlayerConditionSubtype subtype : PlayerConditionSubtype.values()) {
                 if (subtype.getConditionSubtype() == this) {
                     subtypes.add(subtype);
                 }
             }
             return subtypes;
         }
     }

     public static PlayerConditionCategory getCategoryClicked(Material material) {

         for (PlayerConditionCategory actionCategory : PlayerConditionCategory.values()) {
             if (actionCategory.getMaterial() == material) return actionCategory;
         }
         return null;

     }
}
