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

package ua.mcchickenstudio.opencreative.planets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.planets.Planet.Sharing.PUBLIC;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>PlanetInfo</h1>
 * This class represents an information of planet. It contains
 * display name, description, custom ID, category, reputation
 * and icon of planet.
 *
 * <p>This information will be displayed in worlds browser
 * or in advertisement messages.
 * </p>
 */
public class PlanetInfo {

    private final Planet planet;

    private String displayName;
    private String description;
    private String customID;

    private int uniques;
    private int reputation;
    private Category category;
    private ItemStack icon;
    private boolean downloadable;

    public PlanetInfo(Planet planet) {
        this.planet = planet;
        loadInformation();
    }

    private void loadInformation() {
        FileConfiguration config = getPlanetConfig(planet);
        String name = "Unknown name";
        String description = "World data is corrupted,\\nplease report server admin\\nabout this world.";
        String customID = String.valueOf(planet.getId());
        Category category = Category.SANDBOX;
        ItemStack icon = new ItemStack(Material.REDSTONE);
        boolean downloadable = false;
        reputation = getPlayersFromPlanetList(planet, Planet.PlayersType.LIKED).size()- getPlayersFromPlanetList(planet, Planet.PlayersType.DISLIKED).size();
        uniques = getPlayersFromPlanetList(planet, Planet.PlayersType.UNIQUE).size();
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
        if (config.get("icon") != null) {
            try {
                if (config.isString("icon")) {
                    icon = new ItemStack(Material.valueOf(config.getString("icon")));
                } else {
                    icon = ItemStack.deserialize(config.getConfigurationSection("icon").getValues(true));
                }
            } catch (Exception ignored) {}
        }
        if (config.getString("downloadable") != null) {
            downloadable = config.getBoolean("downloadable");
        }
        this.displayName = name;
        this.description = description;
        this.category = category;
        this.customID = customID;
        this.downloadable = downloadable;
        this.icon = icon;
    }

    public void updateIcon() {
        ItemStack item = icon.clone();
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                getLocaleComponent("menus.all-worlds.items.world.name")
                        .replaceText(TextReplacementConfig.builder()
                                .match("%planetName%")
                                .replacement(displayName()).build()));
        List<String> lore = new ArrayList<>();
        for (String loreLine : getLocaleItemDescription("menus.all-worlds.items.world.lore")) {
            if (loreLine.contains("%planetDescription%")) {
                String[] newLines = this.description.split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%planetDescription%", ChatColor.translateAlternateColorCodes('&',newLine)));
                }
            } else {
                lore.add(parsePlanetLines(this.planet,loreLine));
            }
        }
        item.setAmount((Math.max(planet.getOnline(), 1)));
        meta.setLore(lore);
        item.setItemMeta(meta);
        clearItemFlags(item);
        setPersistentData(item, getItemIdKey(),customID);
        icon = item;
    }

    public void setCategory(Category category) {
        this.category = category;
        setPlanetConfigParameter(planet,"category",category.toString());
    }

    public void setDisplayName(String name) {
        this.displayName = name;
        setPlanetConfigParameter(planet,"name",name);
    }

    public void setDescription(String description) {
        this.description = description;
        setPlanetConfigParameter(planet,"description",description);
    }

    public void setIcon(ItemStack itemStack) {
        ItemStack newIcon = clearItemMeta(itemStack.clone());
        newIcon.setAmount(1);
        setPlanetConfigParameter(planet,"icon",newIcon.serialize());
        this.icon = newIcon;
        updateIcon();
    }

    public void setCustomID(String customID) {
        this.customID = customID;
        setPlanetConfigParameter(planet,"customID",customID);
    }

    public Component displayName() {
        return LegacyComponentSerializer.legacySection().deserialize(displayName).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public Component description() {
        return LegacyComponentSerializer.legacySection().deserialize(description);
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
        if (planet.getSharing() == PUBLIC) return icon;
        else {
            return icon.clone().withType(Material.BARRIER);
        }
    }

    public int getUniques() {
        return uniques;
    }

    public void setUniques(int uniques) {
        this.uniques = uniques;
    }

    public int getReputation() {
        return reputation;
    }

    public void setPlanetReputation(int reputation) {
        this.reputation = reputation;
    }

    public enum Category {

        SANDBOX(Material.YELLOW_CONCRETE_POWDER),
        ADVENTURE(Material.NETHERITE_BOOTS),
        STRATEGY(Material.ZOMBIE_HEAD),
        ARCADE(Material.HEART_OF_THE_SEA),
        ROLEPLAY(Material.CHERRY_CHEST_BOAT),
        STORY(Material.WRITABLE_BOOK),
        SIMULATOR(Material.NETHERITE_PICKAXE),
        EXPERIMENT(Material.TNT_MINECART);

        private final Material material;

        Category(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }

        public String getLocaleName() {
            return getLocaleMessage("world.categories."+name().toLowerCase());
        }
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
        setPlanetConfigParameter(planet,"downloadable",downloadable);
    }
}
