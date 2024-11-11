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

package mcchickenstudio.creative.plots;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.plots.Plot.Sharing.PUBLIC;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.FileUtils.getPlayersFromPlotConfig;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.*;

/**
 * <h1>PlotInfo</h1>
 * This class represents an information of plot. It contains
 * display name, description, custom ID, category, reputation
 * and icon of plot.
 *
 * <p>This information will be displayed in worlds browser
 * or in advertisement messages.
 * </p>
 */
public class PlotInfo {

    private final Plot plot;

    private String displayName;
    private String description;
    private String customID;

    private int reputation;
    private Category category;
    private Material material;
    private ItemStack icon;

    public PlotInfo(Plot plot) {
        this.plot = plot;
        loadInformation();
    }

    private void loadInformation() {
        FileConfiguration config = getPlotConfig(plot);
        String name = "Unknown name";
        String description = "World data is corrupted,\\nplease report server admin\\nabout this world.";
        String customID = String.valueOf(plot.getId());
        Category category = Category.SANDBOX;
        Material material = Material.REDSTONE;
        reputation = getPlayersFromPlotConfig(plot, Plot.PlayersType.LIKED).size()-getPlayersFromPlotConfig(plot, Plot.PlayersType.DISLIKED).size();
        if (config != null) {
            if (config.getString("name") != null) {
                name = config.getString("name");
            }
            if (config.getString("description") != null) {
                description = config.getString("description");
            }
            if (config.getString("customID") != null) {
                customID = config.getString("customID");
            }
            if (config.getString("category") != null) {
                try {
                    category = Category.valueOf(config.getString("category"));
                } catch (Exception error) {
                    category = Category.SANDBOX;
                }
            }
            if (config.getString("icon") != null) {
                try {
                    material = Material.valueOf(config.getString("icon"));
                    if (material == Material.AIR) {
                        material = Material.REDSTONE;
                    }
                } catch (Exception error) {
                    material = Material.REDSTONE;
                }
            }
        }
        this.displayName = name;
        this.description = description;
        this.category = category;
        this.material = material;
        this.customID = customID;
    }

    public void updateIcon() {
        ItemStack item = new ItemStack(plot.getSharing() == PUBLIC ? material : Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLocaleItemName("menus.all-worlds.items.world.name").replace("%plotName%", displayName));
        List<String> lore = new ArrayList<>();
        for (String loreLine : getLocaleItemDescription("menus.all-worlds.items.world.lore")) {
            if (loreLine.contains("%plotDescription%")) {
                String[] newLines = this.description.split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%plotDescription%", ChatColor.translateAlternateColorCodes('&',newLine)));
                }
            } else {
                lore.add(parsePlotLines(this.plot,loreLine));
            }
        }
        item.setAmount((Math.max(plot.getOnline(), 1)));
        meta.setLore(lore);
        item.setItemMeta(meta);
        clearItemFlags(item);
        setPersistentData(item,getWorldIdKey(),customID);
        icon = item;
    }

    public void setCategory(Category category) {
        this.category = category;
        setPlotConfigParameter(plot,"category",category.toString());
    }

    public void setDisplayName(String name) {
        this.displayName = name;
        setPlotConfigParameter(plot,"name",name);
    }

    public void setDescription(String description) {
        this.description = description;
        setPlotConfigParameter(plot,"description",description);
    }

    public void setIconMaterial(Material material) {
        this.material = material;
        setPlotConfigParameter(plot,"icon",material.name());
    }

    public void setCustomID(String customID) {
        this.customID = customID;
        setPlotConfigParameter(plot,"customID",customID);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public String getCustomID() {
        return customID;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Material getMaterial() {
        return material;
    }

    public int getReputation() {
        return reputation;
    }

    public void setPlotReputation(int reputation) {
        this.reputation = reputation;
    }

    public enum Category {
        SANDBOX(getLocaleMessage("world.categories.sandbox")),
        ADVENTURE(getLocaleMessage("world.categories.adventure")),
        STRATEGY(getLocaleMessage("world.categories.strategy")),
        ARCADE(getLocaleMessage("world.categories.arcade")),
        ROLEPLAY(getLocaleMessage("world.categories.roleplay")),
        STORY(getLocaleMessage("world.categories.story")),
        SIMULATOR(getLocaleMessage("world.categories.simulator")),
        EXPERIMENT(getLocaleMessage("world.categories.experiment"));

        private final String name;

        Category(String localeMessage) {
            this.name = localeMessage;
        }

        public String getName() {
            return name;
        }
    }
}
