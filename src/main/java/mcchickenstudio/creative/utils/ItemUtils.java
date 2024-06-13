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

package mcchickenstudio.creative.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleItemDescription;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleItemName;

public class ItemUtils {


    /**
     Returns item stack with name and description found in localization file.
     **/
    public static ItemStack createItem(Material material, int amount, String localizationPath) {

        ItemStack itemStack = createItem(material,amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getLocaleItemName(localizationPath + ".name"));
        itemMeta.setLore(getLocaleItemDescription(localizationPath + ".lore"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }

    public static ItemStack createItem(Material material, int amount) {

        ItemStack itemStack = new ItemStack(material,amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemStack.setItemMeta(itemMeta);
        return clearItemFlags(itemStack);

    }

    public static ItemStack clearItemFlags(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                //ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_ARMOR_TRIM,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE);
                //ItemFlag.HIDE_STORED_ENCHANTS,
                //ItemFlag.HIDE_ITEM_SPECIFICS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static boolean itemEquals(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack == null || itemStack2 == null) return false;
        if (itemStack.getType() != itemStack2.getType()) return false;
        if (itemStack.getItemMeta() != null && itemStack.getItemMeta() != null) {
            if (!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(itemStack2.getItemMeta().getDisplayName())) {
                return false;
            }
            if ((itemStack.getItemMeta().hasLore() && itemStack2.getItemMeta().hasLore()) && (!(itemStack.getItemMeta().getLore().equals(itemStack2.getItemMeta().getLore())))) {
                return false;
            }
        }
        return true;
    }

    public static ItemStack replacePlaceholderInName(ItemStack item, String placeholder, Object value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(meta.getDisplayName().replace(placeholder,value.toString()));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack addLoreAtEnd(ItemStack item, String loreLine) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            lore.add(loreLine);
            item.setItemMeta(meta);
        }
        return item;
    }

}
