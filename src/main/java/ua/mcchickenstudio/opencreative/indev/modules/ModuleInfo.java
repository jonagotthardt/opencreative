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

package ua.mcchickenstudio.opencreative.indev.modules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class ModuleInfo {

    private final Module module;

    private String displayName;
    private String description;
    private ItemStack icon;
    private int reputation;
    private int downloads;
    private long creationTime;

    public ModuleInfo(Module module) {
        this.module = module;
        loadInformation();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public long getCreationTime() {
        if (creationTime == 0) {
            return 1670573410000L;
        }
        return creationTime;
    }

    public int getReputation() {
        return reputation;
    }

    public int getDownloads() {
        return downloads;
    }

    private void loadInformation() {
        FileConfiguration config = getModuleConfig(module);
        displayName = config.getString("name","Unknown name");
        description = config.getString("description","Unknown description");
        if (config.get("icon") != null) {
            try {
                if (config.isString("icon")) {
                    icon = new ItemStack(Material.valueOf(config.getString("icon")));
                } else {
                    icon = ItemStack.deserialize(config.getConfigurationSection("icon").getValues(true));
                }
            } catch (Exception ignored) {
                icon = new ItemStack(Material.REDSTONE);
            }
        } else {
            icon = new ItemStack(Material.REDSTONE);
        }
        creationTime = config.getLong("creation-time",1670573410000L);
        reputation = config.getStringList("players.liked").size()-config.getStringList("players.disliked").size();
        downloads = config.getStringList("planets").size();
    }

    public void updateIcon() {
        ItemStack item = icon.clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLocaleItemName("menus.modules.items.module.name").replace("%planetName%", displayName));
        List<String> lore = new ArrayList<>();
        for (String loreLine : getLocaleItemDescription("menus.modules.items.module.lore")) {
            if (loreLine.contains("%moduleDescription%")) {
                String[] newLines = this.description.split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%moduleDescription%", ChatColor.translateAlternateColorCodes('&',newLine)));
                }
            } else {
                lore.add(parseModuleLines(module,loreLine));
            }
        }
        item.setAmount(1);
        meta.setLore(lore);
        item.setItemMeta(meta);
        clearItemFlags(item);
        setPersistentData(item, getItemIdKey(), String.valueOf(module.getId()));
        icon = item;
    }
}
