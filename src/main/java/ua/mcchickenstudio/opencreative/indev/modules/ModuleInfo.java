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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

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
    private boolean isPublic;

    public ModuleInfo(@NotNull Module module) {
        this.module = module;
        loadInformation();
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }

    /**
     * Returns text component of display name, that can be
     * used in item stacks or texts.
     * @return display name of planet.
     */
    public Component displayName() {
        return LegacyComponentSerializer.legacySection().deserialize(displayName).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public @NotNull String getDescription() {
        return description;
    }

    public @NotNull ItemStack getIcon() {
        return icon;
    }

    public boolean isPublic() {
        return isPublic;
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
        isPublic = config.getBoolean("public",true);
        if (config.get("icon") != null) {
            try {
                if (config.isString("icon")) {
                    icon = new ItemStack(Material.valueOf(config.getString("icon")));
                } else {
                    icon = ItemStack.deserialize(config.getConfigurationSection("icon").getValues(true));
                }
            } catch (Exception ignored) {
                icon = new ItemStack(Material.BARREL);
            }
        } else {
            icon = new ItemStack(Material.BARREL);
        }
        setPersistentData(icon, getItemIdKey(), String.valueOf(module.getId()));
        creationTime = config.getLong("creation-time",1670573410000L);
        reputation = config.getStringList("players.liked").size()-config.getStringList("players.disliked").size();
        downloads = config.getStringList("planets").size();
    }

    public void updateIcon() {
        ItemStack item = icon.clone();
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                getLocaleComponent("menus.modules.items.module.name")
                        .replaceText(TextReplacementConfig.builder()
                                .match("%moduleName%")
                                .replacement(displayName()).build()));
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

    /**
     * Sets new item stack as module's icon. Name, lore
     * and enchantments will be removed from item.
     * @param itemStack new icon.
     */
    public void setIcon(ItemStack itemStack) {
        ItemStack newIcon = clearItemMeta(itemStack.clone());
        newIcon.setAmount(1);
        setModuleConfigParameter(module,"icon",newIcon.serialize());
        this.icon = newIcon;
        updateIcon();
    }

    public void setPublic(boolean isPublic) {
        if (isPublic == this.isPublic) return;
        this.isPublic = isPublic;
        setModuleConfigParameter(module,"public", isPublic);
        updateIconAsync();
    }

    /**
     * Sets new display name of module, that will be displayed
     * in modules browser menu.
     * @param name new display name.
     */
    public void setDisplayName(String name) {
        this.displayName = name;
        setModuleConfigParameter(module,"name",name);
        updateIconAsync();
    }

    /**
     * Sets new description of module, that will be displayed
     * in worlds browser menu.
     * @param description new description.
     */
    public void setDescription(String description) {
        this.description = description;
        setModuleConfigParameter(module,"description",description);
        updateIconAsync();
    }

    /**
     * Updates icon in asynchronous task. Used to not
     * load the main thread.
     */
    public void updateIconAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateIcon();
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());
    }
}
