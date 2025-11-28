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

package ua.mcchickenstudio.opencreative.settings;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.indev.Items;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>SettingsCustomItem</h1>
 * This class represents item
 */
public class SettingsCustomItem implements SettingsItem {

    private String preset;
    private String data;
    private String translationKey;

    private String name;
    private String description;

    private Material material;
    private Integer amount;
    private Boolean glowing;

    public void setData(String data) {
        this.data = data;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull Player player) {
        ItemStack item = null;
        if (preset != null) {
            Items itemType = Items.getById(preset.toUpperCase().replace("-", "_"));
            if (itemType == null) {
                item = new ItemStack(Material.AIR);
            } else {
                item = itemType.get();
            }
        } else if (data != null) {
            item = ItemUtils.loadItemFromByteArray(data);
        }

        if (material != null && material.isItem()) {
            if (item == null) {
                item = createItem(material, 1);
            }
            item.setType(material);
        } else if (item == null) {
            return new ItemStack(Material.AIR);
        }

        if (amount != 0) item.setAmount(Math.clamp(amount, 1, 64));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (translationKey != null) {
                meta.setDisplayName(getLocaleItemName(translationKey + ".name"));
                meta.setLore(getLocaleItemDescription(translationKey + ".lore"));
            }
            if (name != null) {
                meta.displayName(toComponent(name));
            }
            if (description != null) {
                List<Component> lore = new ArrayList<>();
                for (String line : description.split("\n")) {
                    lore.add(toComponent(line));
                }
                meta.lore(lore);
            }
            if (glowing) {
                item.getItemMeta().setEnchantmentGlintOverride(true);
            }
        }
        return item;
    }
}
