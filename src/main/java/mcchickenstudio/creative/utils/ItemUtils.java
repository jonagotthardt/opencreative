package mcchickenstudio.creative.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        itemStack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        itemStack.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemStack.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.addItemFlags(ItemFlag.HIDE_DYE);
        itemStack.addItemFlags(ItemFlag.HIDE_PLACED_ON);

        itemStack.setItemMeta(itemMeta);

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

}
