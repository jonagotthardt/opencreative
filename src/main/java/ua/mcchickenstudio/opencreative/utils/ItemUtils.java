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

package ua.mcchickenstudio.opencreative.utils;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleItemDescription;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleItemName;

public class ItemUtils {

    private final static NamespacedKey ITEM_ID_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_item_id");
    private final static NamespacedKey ITEM_TYPE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_item_type");
    private final static NamespacedKey CODING_VALUE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_value_type");
    private final static NamespacedKey CODING_PARTICLE_TYPE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_particle_type");
    private final static NamespacedKey CODING_VARIABLE_TYPE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_variable_type");
    private final static NamespacedKey CODING_DO_NOT_DROP_ME_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_do_not_drop_me");
    private final static NamespacedKey CODING_LOCATION_X = new NamespacedKey(OpenCreative.getPlugin(), "oc_loc_x");
    private final static NamespacedKey CODING_LOCATION_Y = new NamespacedKey(OpenCreative.getPlugin(), "oc_loc_y");
    private final static NamespacedKey CODING_LOCATION_Z = new NamespacedKey(OpenCreative.getPlugin(), "oc_loc_z");

    public static NamespacedKey getCodingValueKey() {
        return CODING_VALUE_KEY;
    }

    public static NamespacedKey getCodingVariableTypeKey() {
        return CODING_VARIABLE_TYPE_KEY;
    }

    public static NamespacedKey getCodingDoNotDropMeKey() {
        return CODING_DO_NOT_DROP_ME_KEY;
    }

    public static NamespacedKey getCodingLocationX() {
        return CODING_LOCATION_X;
    }

    public static NamespacedKey getCodingLocationY() {
        return CODING_LOCATION_Y;
    }

    public static NamespacedKey getCodingLocationZ() {
        return CODING_LOCATION_Z;
    }

    public static NamespacedKey getCodingParticleTypeKey() {
        return CODING_PARTICLE_TYPE_KEY;
    }

    public static NamespacedKey getItemTypeKey() {
        return ITEM_TYPE_KEY;
    }

    public static NamespacedKey getItemIdKey() {
        return ITEM_ID_KEY;
    }

    public static ItemStack setPersistentData(ItemStack item, NamespacedKey key, String value) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setPersistentData(ItemStack item, NamespacedKey key, double value) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
        return item;
    }

    /**
     Returns item stack with name, description found in localization file and persistent data.
     **/
    public static ItemStack createItem(Material material, int amount, String localizationPath, String persistentData) {

        ItemStack itemStack = createItem(material,amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getLocaleItemName(localizationPath + ".name"));
        itemMeta.setLore(getLocaleItemDescription(localizationPath + ".lore"));
        itemStack.setItemMeta(itemMeta);
        setPersistentData(itemStack,getItemTypeKey(),persistentData);
        return itemStack;

    }

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

    /**
     Returns item stack with name and description found in localization file.
     **/
    public static ItemStack createItem(Material material, int amount, String localizationPath, Object value) {

        ItemStack itemStack = createItem(material,amount,localizationPath);
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        container.set(CODING_VALUE_KEY, PersistentDataType.BYTE, (Byte) value);
        itemStack.setItemMeta(meta);

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
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_ARMOR_TRIM,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_STORED_ENCHANTS,
                ItemFlag.HIDE_ITEM_SPECIFICS
        );
        if (!meta.hasAttributeModifiers()) {
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR,new AttributeModifier(new NamespacedKey(OpenCreative.getPlugin(),"hide_attributes"),0.0d, AttributeModifier.Operation.ADD_NUMBER));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack clearItemMeta(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(null);
        meta.lore(null);
        meta.removeEnchantments();
        Set<NamespacedKey> persistentKeys = new HashSet<>(meta.getPersistentDataContainer().getKeys());
        for (NamespacedKey key : persistentKeys) {
            meta.getPersistentDataContainer().remove(key);
        }
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
            return (!itemStack.getItemMeta().hasLore() || !itemStack2.getItemMeta().hasLore()) || (itemStack.getItemMeta().getLore().equals(itemStack2.getItemMeta().getLore()));
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

    public static ItemStack replacePlaceholderInLore(ItemStack item, String placeholder, Object value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getLore() != null) {
            List<String> newLore = new ArrayList<>();
            List<String> oldLore = meta.getLore();
            for (String loreLine : oldLore) {
                newLore.add(loreLine.replace(placeholder,value.toString()));
            }
            meta.setLore(newLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack setDisplayName(ItemStack item, String displayName) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack setLore(ItemStack item, List<String> lore) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack addLoreAtBegin(ItemStack item, String loreLine) {
        if (loreLine.isEmpty()) return item;
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(loreLine);
            if (meta.hasLore()) {
                lore.addAll(meta.getLore());
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack addLoreAtEnd(ItemStack item, String loreLine) {
        if (loreLine.isEmpty()) return item;
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            lore.add(loreLine);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ValueType getValueType(ItemStack item) {
        String typeString = getPersistentData(item,getCodingValueKey());
        try {
            return ValueType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return ValueType.ITEM;
        }
    }

    public static String getItemType(ItemStack item) {
        return getPersistentData(item,getItemTypeKey());
    }

    public static String getPersistentData(ItemStack item, NamespacedKey key) {
        if (item.getItemMeta() == null) {
            return "";
        }
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (!container.has(key)) {
            return "";
        }
        String dataType = container.get(key, PersistentDataType.STRING);
        if (dataType == null) {
            return "";
        }
        return dataType;
    }

    public static ItemStack getItemWithIgnoreData(ItemStack item, boolean removeAmount, boolean removeName, boolean removeLore, boolean removeFlags, boolean removeEnchantments, boolean removeMaterial) {
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        if (removeAmount) {
            newItem.setAmount(1);
        }
        if (meta != null) {
            if (removeName) {
                meta.displayName(Component.text(""));
            }
            if (removeLore) {
                meta.lore(new ArrayList<>());
            }
            newItem.setItemMeta(meta);
        }
        if (removeMaterial) {
            newItem.setType(Material.DIAMOND);
        }
        if (removeEnchantments) {
            newItem.removeEnchantments();
        }
        if (removeFlags) {
            for (ItemFlag flag : newItem.getItemFlags()) {
                newItem.removeItemFlags(flag);
            }
        }
        return newItem;
    }

}
