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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.events.status.MaintenanceEndEvent;
import ua.mcchickenstudio.opencreative.events.status.MaintenanceStartEvent;
import ua.mcchickenstudio.opencreative.indev.Items;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformer;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformers;
import ua.mcchickenstudio.opencreative.utils.world.platforms.HorizontalPlatformer;
import ua.mcchickenstudio.opencreative.utils.world.generators.*;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.groups.Groups;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * This class represents Settings, that stores
 * values which are used in plugin.
 */
public class Settings {

    private boolean debug = false;
    private boolean maintenance = false;
    private boolean creativeChatEnabled = true;

    private boolean consoleCriticalErrors = true;
    private boolean consoleNotFoundMessage = false;
    private boolean consoleWarnings = true;

    private int itemsMaxEnchantLevel = 10;
    private int itemsEntitiesMaxAmount = 3;
    private int itemsMaxBookPagesAmount = 50;
    private int itemsMaxEntityNameLength = 48;
    private int itemsContainerBigItemsLimit = 3;
    private int itemsMaxPersistentDataSize = 2048;
    private int itemsDisplayNameMaxLength = 64;
    private int itemsLoreLineMaxLength = 100;
    private int itemsLoreLinesMaxAmount = 25;
    private boolean itemsRemoveAttributes = true;
    private boolean itemsRemoveBossSpawnEggs = true;
    private boolean itemsRemoveCustomSpawnEggs = true;
    private boolean itemsRemoveClickableBooks = true;
    private boolean itemsClearCommandBlocksData = true;

    private boolean lobbyClearInventory = true;
    private boolean lobbyDisallowPlacingBlocks = true;
    private boolean lobbyDisallowDestroyingBlocks = true;
    private boolean lobbyDisallowSpawningMobs = true;
    private boolean lobbyDisallowDamagingMobs = true;
    private boolean lobbyDisallowWorldEdit = true;
    private boolean lobbyDisableExplosions = true;

    private boolean legacySelectionMenu = false;

    private BukkitRunnable announcer;
    private PlayerListChanger listChanger = PlayerListChanger.FULL;

    private int worldCreationMinSeconds = 30;
    private int worldReputationMinSeconds = 300;

    private String customIdPattern = "^[a-zA-Zа-яА-Я0-9_]+$";
    private int customIdMinLength = 2;
    private int customIdMaxLength = 16;

    private int worldNameMinLength = 4;
    private int worldNameMaxLength = 30;

    private int worldDescriptionMinLength = 4;
    private int worldDescriptionMaxLength = 256;

    private final Groups groups;
    private final Commands commands;
    private final Set<Integer> recommendedWorldsIDs = new HashSet<>();
    private final Set<String> allowedResourcePackLinks = new HashSet<>();
    private final Set<String> disabledEvents = new HashSet<>();
    private final Set<String> disabledActions = new HashSet<>();
    private final Set<String> disabledConditions = new HashSet<>();

    private final Map<Sounds,SettingsSound> sounds = new HashMap<>();
    private final Map<Items,SettingsItem> items = new HashMap<>();

    public Settings() {
        groups = new Groups();
        commands = new Commands();
    }

    /**
     * Loads settings values from configuration file.
     * @param config Configuration file.
     */
    public void load(FileConfiguration config) {

        allowedResourcePackLinks.clear();
        recommendedWorldsIDs.clear();
        sounds.clear();

        listChanger = PlayerListChanger.fromString(config.getString("hide-from-tab","full"));
        recommendedWorldsIDs.addAll(config.getIntegerList("recommended-worlds"));
        allowedResourcePackLinks.addAll(config.getStringList("allowed-links.resource-pack"));

        debug = config.getBoolean("debug",false);
        maintenance = config.getBoolean("maintenance",false);
        consoleCriticalErrors = config.getBoolean("messages.critical-errors",true);
        consoleNotFoundMessage = config.getBoolean("messages.not-found",false);
        consoleWarnings = config.getBoolean("messages.warnings",true);

        lobbyClearInventory = config.getBoolean("lobby.clear-inventory",true);
        lobbyDisableExplosions = config.getBoolean("lobby.disable-explosions",true);
        lobbyDisallowWorldEdit = config.getBoolean("lobby.disallow-world-edit",true);
        lobbyDisallowDamagingMobs = config.getBoolean("lobby.disallow-damaging-mobs",true);
        lobbyDisallowSpawningMobs = config.getBoolean("lobby.disallow-spawning-mobs",true);
        lobbyDisallowPlacingBlocks = config.getBoolean("lobby.disallow-placing-blocks",true);
        lobbyDisallowDestroyingBlocks = config.getBoolean("lobby.disallow-destroying-blocks",true);

        legacySelectionMenu = config.getBoolean("coding.old-selection-menu",false);

        worldCreationMinSeconds = config.getInt("requirements.world-creation.played-seconds",30);
        worldReputationMinSeconds = config.getInt("requirements.world-reputation.creation-seconds",300);

        customIdPattern = config.getString("requirements.world-custom-id.pattern","^[a-zA-Zа-яА-Я0-9_]+$");
        customIdMinLength = config.getInt("requirements.world-custom-id.min-length",2);
        customIdMaxLength = config.getInt("requirements.world-custom-id.max-length",16);

        worldNameMinLength = config.getInt("requirements.world-name.min-length",2);
        worldNameMaxLength = config.getInt("requirements.world-name.max-length",16);

        worldDescriptionMinLength = config.getInt("requirements.world-description.min-length",2);
        worldDescriptionMaxLength = config.getInt("requirements.world-description.max-length",256);

        itemsRemoveAttributes = config.getBoolean("item-fixer.remove-attribute-modifiers",true);
        itemsRemoveClickableBooks = config.getBoolean("item-fixer.remove-clickable-in-books",true);
        itemsRemoveCustomSpawnEggs = config.getBoolean("item-fixer.remove-custom-spawn-eggs",true);
        itemsRemoveBossSpawnEggs = config.getBoolean("item-fixer.remove-boss-spawn-eggs",true);
        itemsClearCommandBlocksData = config.getBoolean("item-fixer.clear-command-blocks-data",true);
        itemsMaxEnchantLevel = config.getInt("item-fixer.max-enchantment-level",10);
        itemsMaxEntityNameLength = config.getInt("item-fixer.entity-name-max-length",48);
        itemsMaxPersistentDataSize = config.getInt("item-fixer.persistent-data-max-size",2048);
        itemsEntitiesMaxAmount = config.getInt("item-fixer.entities-max-amount",3);
        itemsMaxBookPagesAmount = config.getInt("item-fixer.books-pages-max-amount",50);
        itemsContainerBigItemsLimit = config.getInt("item-fixer.container-big-items-max-amount",3);
        itemsDisplayNameMaxLength = config.getInt("item-fixer.display-name-max-length",64);
        itemsLoreLineMaxLength = config.getInt("item-fixer.lore-line-max-length",100);
        itemsLoreLinesMaxAmount = config.getInt("item-fixer.container-lore-lines-max-amount",25);

        groups.load();
        commands.load();

        String soundsTheme = config.getString("sounds.theme","default");
        loadSounds(config, soundsTheme);
        loadItems(config);
        loadDisabledBlocks(config);
        loadWorldGenerators(config);

        if (maintenance) {
            OpenCreative.getPlugin().getLogger().warning("Maintenance mode is still enabled in config.yml, to disable: /maintenance end");
        }
        if (debug) {
            OpenCreative.getPlugin().getLogger().warning("Debug Mode is enabled in config.yml, some logs will appear in console.");
        }
        String platformsGeneratorId = config.getString("coding.platforms-generator", "horizontal");
        DevPlatformer platformer = DevPlatformers.getInstance().getById(platformsGeneratorId);
        if (platformer == null) {
            sendWarningErrorMessage("[PLATFORMERS] Unknown coding platform generator: " + platformsGeneratorId + ", using the default horizontal.");
            platformer = new HorizontalPlatformer();
        }
        OpenCreative.setDevPlatformer(platformer);
        checkDebugAnnouncer();
    }

    private void loadWorldGenerators(FileConfiguration config) {
        WorldGenerators instance = WorldGenerators.getInstance();
        instance.clearWorldGenerators();
        if (config.getBoolean("generators.flat",true)) instance.registerWorldGenerator(new FlatGenerator());
        if (config.getBoolean("generators.empty",true)) instance.registerWorldGenerator(new EmptyGenerator());
        if (config.getBoolean("generators.water",true)) instance.registerWorldGenerator(new OceanGenerator());
        if (config.getBoolean("generators.survival",true)) instance.registerWorldGenerator(new SurvivalGenerator());
        if (config.getBoolean("generators.large-biomes",true)) instance.registerWorldGenerator(new LargeBiomesGenerator());
        ConfigurationSection customFlatsSection = config.getConfigurationSection("generators.custom-flats");
        if (customFlatsSection != null) {
            for (String customFlatId : customFlatsSection.getKeys(false)) {
                String generation = customFlatsSection.getString(customFlatId + ".generation");
                if (generation == null) continue;
                String iconMaterial = customFlatsSection.getString(customFlatId + ".icon","GRASS_BLOCK");
                Material material = Material.getMaterial(iconMaterial.toUpperCase());
                if (material == null || !material.isItem()) material = Material.GRASS_BLOCK;
                boolean generateTrees = customFlatsSection.getBoolean(customFlatId + ".generate-trees",true);
                instance.registerWorldGenerator(new CustomFlatGenerator(customFlatId, new ItemStack(material), generation, generateTrees));
            }
        }
        ConfigurationSection templatesSection = config.getConfigurationSection("generators.templates");
        if (templatesSection != null) {
            for (String templateId : templatesSection.getKeys(false)) {
                boolean enabled = templatesSection.getBoolean(templateId + ".enabled",true);
                if (!enabled) continue;
                String folderName = templatesSection.getString(templateId + ".folder");
                if (folderName == null) continue;
                File template = new File(OpenCreative.getPlugin().getDataPath()
                        + File.separator + "templates" + File.separator + folderName);
                if (!template.exists() || !template.isDirectory()) {
                    sendWarningErrorMessage("Can't register template world " + templateId + " because it's folder " + folderName + " doesn't exists.");
                    continue;
                }
                String iconMaterial = templatesSection.getString(templateId + ".icon","GRASS_BLOCK");
                Material material = Material.getMaterial(iconMaterial.toUpperCase());
                if (material == null || !material.isItem()) material = Material.GRASS_BLOCK;
                instance.registerWorldGenerator(new WorldTemplate(templateId, new ItemStack(material), folderName));
            }
        }
        int registeredGenerators = instance.getWorldGenerators().size();
        if (registeredGenerators != 5) {
            OpenCreative.getPlugin().getLogger().info("Registered " + registeredGenerators + " world generators");
        }
    }

    public boolean isWorldGenerationUnavailable() {
        return WorldGenerators.getInstance().getWorldGenerators().isEmpty();
    }

    private void loadDisabledBlocks(FileConfiguration config) {
        disabledEvents.clear();
        disabledConditions.clear();
        disabledActions.clear();
        int count = 0;
        List<String> unknownBlocks = new ArrayList<>();
        for (String disabled : config.getStringList("coding.disabled-actions")) {
            try {
                disabled = disabled.toUpperCase().replace("-","_");
                ActionType type = ActionType.valueOf(disabled);
                disabledActions.add(type.name());
                count++;
            } catch (Exception ignored) {
                unknownBlocks.add(disabled);
            }
        }
        for (String disabled : config.getStringList("coding.disabled-conditions")) {
            try {
                disabled = disabled.toUpperCase().replace("-","_");
                ActionType type = ActionType.valueOf(disabled);
                disabledConditions.add(type.name());
                count++;
            } catch (Exception ignored) {
                unknownBlocks.add(disabled);
            }
        }
        for (String disabled : config.getStringList("coding.disabled-events")) {
            try {
                disabled = disabled.toUpperCase().replace("-","_");
                ExecutorType type = ExecutorType.valueOf(disabled);
                disabledEvents.add(type.name());
                count++;
            } catch (Exception ignored) {
                unknownBlocks.add(disabled);
            }
        }
        if (count >= 1) {
            OpenCreative.getPlugin().getLogger().info("Disabled " + count + " coding blocks!");
        }
        if (!unknownBlocks.isEmpty()) {
            sendWarningErrorMessage("Can't recognize these coding blocks in disabled list, do they exist? " + unknownBlocks);
        }
    }

    private void loadSounds(FileConfiguration config, String soundsTheme) {
        sounds.clear();
        ConfigurationSection soundsSection = config.getConfigurationSection("sounds." + soundsTheme);
        if (soundsSection != null) {
            for (String key : soundsSection.getKeys(false)) {
                try {
                    Sounds type = Sounds.valueOf(key.toUpperCase().replace("-","_"));
                    String sound = soundsSection.getString(key+".name","");
                    float pitch = (float) soundsSection.getDouble(key+".name",1.0f);
                    sounds.put(type,new SettingsSound(sound,pitch));
                } catch (Exception ignored) {
                    sendWarningErrorMessage("Sound " + key.toLowerCase() + " doesn't exists.");
                }
            }
        }
        if (!sounds.isEmpty()) {
            OpenCreative.getPlugin().getLogger().info("Added " + sounds.size() + " custom sounds");
        }
    }

    private void loadItems(FileConfiguration config) {
        items.clear();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                try {
                    Items type = Items.valueOf(key.toUpperCase().replace("-","_"));
                    ItemStack item = null;
                    if (itemsSection.isString(key)) {
                        // Get item only from material
                        Material material = Material.getMaterial(itemsSection.getString(key,"").toUpperCase());
                        if (material != null) {
                            item = new ItemStack(material);
                        }
                    } else {
                        // Get custom item with data
                        item = itemsSection.getItemStack(key);
                    }
                    if (item != null) {
                        items.put(type,new SettingsItem(item));
                    }
                } catch (Exception ignored) {
                    sendWarningErrorMessage("Item " + key.toLowerCase() + " doesn't exists.");
                }
            }
        }
        if (!items.isEmpty()) {
            OpenCreative.getPlugin().getLogger().info("Added " + items.size() + " custom items");
        }
    }

    public boolean setSoundsTheme(String theme) {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        if (config.getConfigurationSection("sounds."+theme) == null) {
            return false;
        }
        loadSounds(config, theme);
        OpenCreative.getPlugin().getConfig().set("sounds.theme",theme);
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean setCustomItem(Items type, ItemStack item) {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        OpenCreative.getPlugin().getConfig().set("items."+type.name().toLowerCase().replace("_","-"),item.serialize());
        OpenCreative.getPlugin().saveConfig();
        loadItems(config);
        return true;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        if (this.debug == debug) return;
        this.debug = debug;
        OpenCreative.getPlugin().getConfig().set("debug",debug);
        OpenCreative.getPlugin().saveConfig();
        OpenCreative.getPlugin().getLogger().info("Debug mode is " + (debug ? "enabled." : "disabled."));
        checkDebugAnnouncer();
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public boolean isLobbyClearInventory() {
        return lobbyClearInventory;
    }

    public void setMaintenance(boolean maintenance) {
        setMaintenance(maintenance, null);
    }

    public void setMaintenance(boolean maintenance, @Nullable CommandSender sender) {
        if (this.maintenance == maintenance) return;
        this.maintenance = maintenance;
        try {
            OpenCreative.getPlugin().getConfig().set("maintenance",maintenance);
            OpenCreative.getPlugin().saveConfig();
        } catch (Exception ignored) {}
        if (maintenance) {
            OpenCreative.getPlugin().getLogger().info("Maintenance mode started! Unloading planets, please wait...");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Sounds.MAINTENANCE_START.play(onlinePlayer);
                onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.started"));
                for (Planet planet : OpenCreative.getPlanetsManager().getPlanets()) {
                    if (planet.isLoaded()) {
                        for (Player player : planet.getPlayers()) {
                            teleportToLobby(player);
                        }
                    }
                }
            }
            new MaintenanceStartEvent(sender).callEvent();
        } else {
            OpenCreative.getPlugin().getLogger().info("Maintenance mode ended, now players can play in worlds.");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Sounds.MAINTENANCE_END.play(onlinePlayer);
                onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.ended"));
            }
            new MaintenanceEndEvent(sender).callEvent();
        }
    }

    /**
     * Checks if debug mode is enabled in config.yml.
     */
    private void checkDebugAnnouncer() {
        if (announcer != null) {
            announcer.cancel();
        }
        if (debug) {
            announcer = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().sendActionBar(Component.text("§fOpen§7Creative§b+ §3" + OpenCreative.getVersion() + "§7 Debug Mode. §fShhh, let's not leak our hard work..."));
                }
            };
            announcer.runTaskTimer(OpenCreative.getPlugin(),20L,20L);
        } else {
            announcer = null;
        }
    }

    public Set<String> getAllowedResourcePackLinks() {
        return allowedResourcePackLinks;
    }

    public Set<Integer> getRecommendedWorldsIDs() {
        return recommendedWorldsIDs;
    }

    public void setCreativeChatEnabled(boolean creativeChatEnabled) {
        this.creativeChatEnabled = creativeChatEnabled;
    }

    public boolean isCreativeChatEnabled() {
        return creativeChatEnabled;
    }

    public PlayerListChanger getListChanger() {
        return listChanger;
    }

    public enum PlayerListChanger {

        SPECTATOR,
        FULL,
        NONE;

        public static PlayerListChanger fromString(String string) {
            for (PlayerListChanger changer : PlayerListChanger.values()) {
                if (string.equalsIgnoreCase(changer.name())) {
                    return changer;
                }
            }
            return FULL;
        }
    }

    public boolean isDisabledEvent(@NotNull ExecutorType event) {
        return disabledEvents.contains(event.name());
    }

    public boolean isDisabledAction(@NotNull ActionType action) {
        return disabledActions.contains(action.name());
    }

    public boolean isDisabledCondition(@NotNull ActionType condition) {
        return disabledConditions.contains(condition.name());
    }

    public Groups getGroups() {
        return groups;
    }

    public Commands getCommands() {
        return commands;
    }

    public boolean isConsoleCriticalErrors() {
        return consoleCriticalErrors;
    }

    public boolean isConsoleWarnings() {
        return consoleWarnings;
    }

    public boolean isConsoleNotFoundMessage() {
        return consoleNotFoundMessage;
    }

    public String getCustomIdPattern() {
        return customIdPattern;
    }

    public int getCustomIdMaxLength() {
        return customIdMaxLength;
    }

    public int getCustomIdMinLength() {
        return customIdMinLength;
    }

    public int getWorldDescriptionMaxLength() {
        return worldDescriptionMaxLength;
    }

    public int getWorldDescriptionMinLength() {
        return worldDescriptionMinLength;
    }

    public int getWorldNameMaxLength() {
        return worldNameMaxLength;
    }

    public int getWorldNameMinLength() {
        return worldNameMinLength;
    }

    public int getWorldCreationMinSeconds() {
        return worldCreationMinSeconds;
    }

    public int getWorldReputationMinSeconds() {
        return worldReputationMinSeconds;
    }

    public Map<Sounds, SettingsSound> getSounds() {
        return sounds;
    }

    public Map<Items, SettingsItem> getItems() {
        return items;
    }

    public int getItemsContainerBigItemsLimit() {
        return itemsContainerBigItemsLimit;
    }

    public int getItemsMaxBookPagesAmount() {
        return itemsMaxBookPagesAmount;
    }

    public int getItemsMaxEnchantLevel() {
        return itemsMaxEnchantLevel;
    }

    public boolean isItemsRemoveAttributes() {
        return itemsRemoveAttributes;
    }

    public boolean isItemsRemoveClickableBooks() {
        return itemsRemoveClickableBooks;
    }

    public boolean isItemsRemoveCustomSpawnEggs() {
        return itemsRemoveCustomSpawnEggs;
    }

    public boolean isItemsRemoveBossSpawnEggs() {
        return itemsRemoveBossSpawnEggs;
    }

    public boolean isLegacySelectionMenu() {
        return legacySelectionMenu;
    }

    public boolean isLobbyDisableExplosions() {
        return lobbyDisableExplosions;
    }

    public boolean isLobbyDisallowDamagingMobs() {
        return lobbyDisallowDamagingMobs;
    }

    public boolean isLobbyDisallowDestroyingBlocks() {
        return lobbyDisallowDestroyingBlocks;
    }

    public boolean isLobbyDisallowPlacingBlocks() {
        return lobbyDisallowPlacingBlocks;
    }

    public boolean isLobbyDisallowSpawningMobs() {
        return lobbyDisallowSpawningMobs;
    }

    public boolean isLobbyDisallowWorldEdit() {
        return lobbyDisallowWorldEdit;
    }

    public int getItemsMaxEntityNameLength() {
        return itemsMaxEntityNameLength;
    }

    public int getItemsMaxPersistentDataSize() {
        return itemsMaxPersistentDataSize;
    }

    public int getItemsEntitiesMaxAmount() {
        return itemsEntitiesMaxAmount;
    }

    public int getItemsDisplayNameMaxLength() {
        return itemsDisplayNameMaxLength;
    }

    public int getItemsLoreLineMaxLength() {
        return itemsLoreLineMaxLength;
    }

    public int getItemsLoreLinesMaxAmount() {
        return itemsLoreLinesMaxAmount;
    }

    public boolean isItemsClearCommandBlocksData() {
        return itemsClearCommandBlocksData;
    }
}
