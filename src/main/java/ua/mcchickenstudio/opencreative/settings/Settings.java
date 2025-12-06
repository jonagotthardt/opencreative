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
import ua.mcchickenstudio.opencreative.coding.prompters.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.commands.experiments.Experiment;
import ua.mcchickenstudio.opencreative.commands.experiments.Experiments;
import ua.mcchickenstudio.opencreative.events.status.MaintenanceEndEvent;
import ua.mcchickenstudio.opencreative.events.status.MaintenanceStartEvent;
import ua.mcchickenstudio.opencreative.settings.items.Items;
import ua.mcchickenstudio.opencreative.managers.stability.DisabledWatchdog;
import ua.mcchickenstudio.opencreative.managers.stability.Watchdog;
import ua.mcchickenstudio.opencreative.settings.items.*;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformer;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformers;
import ua.mcchickenstudio.opencreative.utils.world.platforms.HorizontalPlatformer;
import ua.mcchickenstudio.opencreative.utils.world.generators.*;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.groups.Groups;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>Settings</h1>
 * This class represents Settings, that stores
 * values which are used in plugin.
 */
public final class Settings {

    private boolean debug = false;
    private boolean maintenance = false;
    private boolean creativeChatEnabled = true;

    private boolean consoleCriticalErrors = true;
    private boolean consoleNotFoundMessage = false;
    private boolean consoleWarnings = true;

    private boolean itemFixerEnabled = true;
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

    private boolean notifyNoPlayersAround = true;

    private boolean lobbyClearInventory = true;
    private boolean lobbyDisallowPlacingBlocks = true;
    private boolean lobbyDisallowDestroyingBlocks = true;
    private boolean lobbyDisallowSpawningMobs = true;
    private boolean lobbyDisallowDamagingMobs = true;
    private boolean lobbyDisallowWorldEdit = true;
    private boolean lobbyDisableExplosions = true;

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

    private int moduleNameMinLength = 4;
    private int moduleNameMaxLength = 30;

    private int moduleDescriptionMinLength = 4;
    private int moduleDescriptionMaxLength = 256;

    private final Groups groups;
    private final Commands commands;
    private final Set<Integer> recommendedWorldsIDs = new HashSet<>();
    private final Set<String> allowedResourcePackLinks = new HashSet<>();

    private boolean enabledCoding = true;
    private boolean cancelChatOnValueSet = false;
    private final Set<String> disabledEvents = new HashSet<>();
    private final Set<String> disabledActions = new HashSet<>();
    private final Set<String> disabledConditions = new HashSet<>();
    private boolean legacySelectionMenu = false;
    private int prompterMaxExecutors = 10;
    private int prompterTimeout = 120;

    private final Set<String> messagesIgnoringReset = new HashSet<>();

    private final Map<Sounds, SettingsSound> sounds = new HashMap<>();
    private final Map<ItemsGroup, SettingsItemsGroup> itemsGroups = new HashMap<>();

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
        messagesIgnoringReset.clear();
        sounds.clear();

        listChanger = PlayerListChanger.fromString(config.getString("hide-from-tab","full"));
        recommendedWorldsIDs.addAll(config.getIntegerList("recommended-worlds"));
        allowedResourcePackLinks.addAll(config.getStringList("allowed-links.resource-pack"));
        messagesIgnoringReset.addAll(config.getStringList("messages.do-not-reset"));

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

        boolean enabledWatchdog = config.getBoolean("watchdog.enabled", false);

        legacySelectionMenu = config.getBoolean("coding.old-selection-menu",false);
        cancelChatOnValueSet = config.getBoolean("coding.cancel-chat-on-value-set", false);
        enabledCoding = config.getBoolean("coding.enabled",true);
        notifyNoPlayersAround = config.getBoolean("messages.notify-no-players-around", true);

        worldCreationMinSeconds = config.getInt("requirements.world-creation.played-seconds",30);
        worldReputationMinSeconds = config.getInt("requirements.world-reputation.creation-seconds",300);

        customIdPattern = config.getString("requirements.world-custom-id.pattern","^[a-zA-Zа-яА-Я0-9_]+$");
        customIdMinLength = config.getInt("requirements.world-custom-id.min-length",2);
        customIdMaxLength = config.getInt("requirements.world-custom-id.max-length",16);

        worldNameMinLength = config.getInt("requirements.world-name.min-length",2);
        worldNameMaxLength = config.getInt("requirements.world-name.max-length",16);

        worldDescriptionMinLength = config.getInt("requirements.world-description.min-length",2);
        worldDescriptionMaxLength = config.getInt("requirements.world-description.max-length",256);

        moduleNameMinLength = config.getInt("requirements.module-name.min-length",2);
        moduleNameMaxLength = config.getInt("requirements.module-name.max-length",16);

        moduleDescriptionMinLength = config.getInt("requirements.module-description.min-length",2);
        moduleDescriptionMaxLength = config.getInt("requirements.module-description.max-length",256);

        itemFixerEnabled = config.getBoolean("item-fixer.enabled", true);
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

        OpenCreative.setStability(enabledWatchdog ? new Watchdog() : new DisabledWatchdog());
        setupPromptHandler(config);
        loadExperiments(config);
        checkDebugAnnouncer();

        loadItems(config);
    }

    private void setupPromptHandler(FileConfiguration config) {
        prompterTimeout = config.getInt("coding.prompt-handler.timeout",120);
        prompterMaxExecutors = config.getInt("coding.prompt-handler.executors-limit",10);

        String type = config.getString("coding.prompt-handler.type","none");
        if (type.equalsIgnoreCase("none")) {
            OpenCreative.setCodingPrompter(new DisabledCodingPrompter());
            return;
        }

        String token = config.getString("coding.prompt-handler.token", "");
        if (token.length() <= 10) {
            sendWarningErrorMessage("[CODING PROMPT] The token is not valid, disabling prompt handler.");
            OpenCreative.setCodingPrompter(new DisabledCodingPrompter());
        } else {
            switch (type.toLowerCase()) {
                case "chatgpt", "openai" -> OpenCreative.setCodingPrompter(new OpenAIPrompter());
                case "openrouter", "openrouterai" -> OpenCreative.setCodingPrompter(new OpenRouterPrompter());
                case "gemini", "google" -> OpenCreative.setCodingPrompter(new GeminiPrompter());
                default -> {
                    sendWarningErrorMessage("[CODING PROMPT] Unknown prompter: " + type + ", using disabled prompt handler.");
                    OpenCreative.setCodingPrompter(new DisabledCodingPrompter());
                    return;
                }
            }
            OpenCreative.getCodingPrompter().setToken(token);
        }

        if (OpenCreative.getCodingPrompter() instanceof PrompterModelCapable modelable) {
            String model = config.getString("coding.prompt-handler.model", "");
            if (!model.isEmpty()) modelable.setModel(model);
        }

        CodingPrompter prompter = OpenCreative.getCodingPrompter();
        if (OpenCreative.getCodingPrompter().isEnabled()) {
            sendDebug("[CODING PROMPT] Using prompter (" + prompter.getName() +")" +
                    (prompter instanceof PrompterModelCapable model ? " with model: " + model.getModel() : "")
                    + " for /env make");
        }
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
                    sendWarningErrorMessage("Can't register template world " + templateId + " because it's folder " + folderName + " doesn't exist.");
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

    private void loadExperiments(FileConfiguration config) {
        int count = 0;
        ConfigurationSection section = config.getConfigurationSection("experiments");
        if (section == null) return;
        for (Experiment experiment : Experiments.getInstance().getExperiments()) {
            boolean enabled = section.getBoolean(experiment.getId(), false);
            Experiments.getInstance().setEnabled(experiment, enabled);
            if (enabled) count++;
        }
        if (count >= 1) {
            OpenCreative.getPlugin().getLogger().info("Enabled " + count + " experiments.");
        }
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
                    float pitch = (float) soundsSection.getDouble(key+".pitch",1.0f);
                    sounds.put(type,new SettingsSound(sound,pitch));
                } catch (Exception ignored) {
                    sendWarningErrorMessage("Sound " + key.toLowerCase() + " doesn't exist.");
                }
            }
        }
        if (!sounds.isEmpty()) {
            OpenCreative.getPlugin().getLogger().info("Added " + sounds.size() + " custom sounds");
        }
    }

    private void loadItems(FileConfiguration config) {
        itemsGroups.clear();
        ConfigurationSection groupsSection = config.getConfigurationSection("items");
        if (groupsSection == null) {
            return;
        }
        for (String groupId : groupsSection.getKeys(false)) {
            ItemsGroup group = ItemsGroup.getById(groupId.toUpperCase().replace("-", "_"));
            if (group == null) {
                sendWarningErrorMessage("Unknown items kit in config: " + groupId);
                continue;
            }
            if (groupsSection.isString(groupId)) {
                continue;
            }
            ConfigurationSection slots = groupsSection.getConfigurationSection(groupId);
            if (slots == null) continue;
            SettingsItemsGroup settingsItemsGroup = new SettingsItemsGroup();
            for (String slotString : slots.getKeys(false)) {
                int slot;
                try {
                    slot = Math.clamp(Integer.parseInt(slotString), 1, 36);
                } catch (Exception ignored) {
                    sendWarningErrorMessage("Unknown slot, not a number: " + slotString + " (kit: " + groupId + ")");
                    continue;
                }
                SettingsItem item = loadItem(slots, slotString);
                if (item == null) continue;
                settingsItemsGroup.setItem(slot, item);
            }
            itemsGroups.put(group, settingsItemsGroup);
        }
        if (!itemsGroups.isEmpty()) {
            OpenCreative.getPlugin().getLogger().info("Changed " + itemsGroups.size() + " item kits");
        }
    }

    private @Nullable SettingsItem loadItem(@NotNull ConfigurationSection section, @NotNull String slot) {
        if (section.isString(slot)) {
            // 1: "own-worlds"
            String typeString = section.getString(slot, "");
            Items itemType = Items.getById(typeString);
            if (itemType == null) {
                sendWarningErrorMessage("Unknown system item type " + typeString + " for item (kit: " + section.getName() + " in slot: " + slot  + ")");
                return null;
            } else {
                return new SettingsPresetItem(itemType);
            }
        }
        if (section.isConfigurationSection(slot)) {
            section = section.getConfigurationSection(slot);
            if (section == null) return null;
            SettingsCustomItem item = new SettingsCustomItem();
            /*
             * 1:
             *   type: "own-worlds"
             *   material: "nether_star"
             *   name: "Create your worlds"
             *   lore:
             *     - "Use right click"
             *   amount: 1
             *   glowing: true
             */
            if (section.isString("type")) {
                // Preset will copy material, name, lore from Items enum.
                String typeString = section.getString("type", "");
                Items itemType = Items.getById(typeString);
                if (itemType == null) {
                    sendWarningErrorMessage("Unknown system item type " + typeString + " for item (kit: " + section.getName() + " in slot: " + slot  + ")");
                } else {
                    item.setPreset(itemType);
                }

            }
            if (section.isString("data")) {
                // Data will copy all item content from specified text.
                item.setData(section.getString("data", ""));
            }

            // Overrides material
            if (section.isString("material")) {
                String materialString = section.getString("material", "");
                Material material = Material.matchMaterial(materialString);
                if (material == null) {
                    sendWarningErrorMessage("Unknown material " + materialString + " for item (kit: " + section.getName() + " in slot: " + slot  + ")");
                } else if (!material.isItem()) {
                    sendWarningErrorMessage("Material " + materialString + " is not an obtainable item (kit: " + section.getName() + " in slot: " + slot  + ")");
                } else {
                    item.setMaterial(material);
                }
            }
            if (section.isString("translation")) {
                // Overrides name and lore.
                item.setTranslationKey(section.getString("translation", ""));
            }


            // Overrides name
            if (section.isString("name")) item.setName(section.getString("name", ""));
            // Overrides lore
            if (section.isList("lore")) {
                List<String> list = section.getStringList("lore");
                item.setDescription(section.getString("lore", String.join("\n", list)));
            }

            // Overrides glowing
            if (section.isBoolean("glowing")) item.setGlowing(section.getBoolean("glowing", false));
            // Overrides amount
            if (section.isInt("amount")) item.setAmount(section.getInt("amount", 1));
            return item;
        }
        return null;
    }

    public boolean setSoundsTheme(String theme) {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        if (config.getConfigurationSection("sounds." + theme) == null) {
            return false;
        }
        loadSounds(config, theme);
        OpenCreative.getPlugin().getConfig().set("sounds.theme",theme);
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean addRecommendedWorld(int worldID) {
        if (recommendedWorldsIDs.contains(worldID)) {
            return false;
        }
        recommendedWorldsIDs.add(worldID);
        OpenCreative.getPlugin().getConfig().set("recommended-worlds", new ArrayList<>(recommendedWorldsIDs));
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean removeRecommendedWorld(int worldID) {
        if (!recommendedWorldsIDs.contains(worldID)) {
            return false;
        }
        recommendedWorldsIDs.remove(worldID);
        OpenCreative.getPlugin().getConfig().set("recommended-worlds", new ArrayList<>(recommendedWorldsIDs));
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean addMessageIgnoringReset(@NotNull String path) {
        if (messagesIgnoringReset.contains(path)) {
            return false;
        }
        messagesIgnoringReset.add(path);
        OpenCreative.getPlugin().getConfig().set("messages.do-not-reset", new ArrayList<>(messagesIgnoringReset));
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean removeMessageIgnoringReset(@NotNull String path) {
        if (!messagesIgnoringReset.contains(path)) {
            return false;
        }
        messagesIgnoringReset.remove(path);
        OpenCreative.getPlugin().getConfig().set("messages.do-not-reset", new ArrayList<>(messagesIgnoringReset));
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public void setCustomItem(ItemsGroup group, int slot, @NotNull ItemStack item) {
        String path = "items." + group.name().toLowerCase().replace("_","-") + "." + slot;
        SettingsItemsGroup settingsGroup = itemsGroups.getOrDefault(group, new SettingsItemsGroup());
        if (item.isEmpty()) {
            OpenCreative.getPlugin().getConfig().set(path, null);
            settingsGroup.removeItem(slot);
        } else {
            Map<String, String> itemInfo = new HashMap<>();
            String data = ItemUtils.saveItemAsByteArray(item);
            itemInfo.put("data", data);
            OpenCreative.getPlugin().getConfig().set(path, itemInfo);
            SettingsCustomItem customItem = new SettingsCustomItem();
            customItem.setData(data);
            settingsGroup.setItem(slot, customItem);
        }
        OpenCreative.getPlugin().saveConfig();
        itemsGroups.put(group, settingsGroup);
    }

    public void setCustomItem(ItemsGroup group, int slot, @NotNull Items item) {
        String path = "items." + group.name().toLowerCase().replace("_","-") + "." + slot;
        SettingsItemsGroup settingsGroup = itemsGroups.getOrDefault(group, new SettingsItemsGroup());
        OpenCreative.getPlugin().getConfig().set(path, item.name().toLowerCase().replace("_", "-"));
        SettingsPresetItem customItem = new SettingsPresetItem(item);
        settingsGroup.setItem(slot, customItem);
        OpenCreative.getPlugin().saveConfig();
        itemsGroups.put(group, settingsGroup);
    }

    public void resetItemsGroup(ItemsGroup group) {
        String path = "items." + group.name().toLowerCase();
        // Removes duplicate with under score if exists
        OpenCreative.getPlugin().getConfig().set(path, null);
        path = path.replace("_","-");
        OpenCreative.getPlugin().getConfig().set(path, null);

        itemsGroups.remove(group);
        for (ItemPair pair : group.getPairs()) {
            OpenCreative.getPlugin().getConfig().set(path + "." + pair.slot(),
                    pair.item().name().toLowerCase().replace("_", "-"));
        }

        OpenCreative.getPlugin().saveConfig();
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

    public Map<ItemsGroup, SettingsItemsGroup> getItemsGroups() {
        return itemsGroups;
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

    public int getModuleDescriptionMaxLength() {
        return moduleDescriptionMaxLength;
    }

    public int getModuleDescriptionMinLength() {
        return moduleDescriptionMinLength;
    }

    public int getModuleNameMaxLength() {
        return moduleNameMaxLength;
    }

    public int getModuleNameMinLength() {
        return moduleNameMinLength;
    }

    public boolean isItemFixerEnabled() {
        return itemFixerEnabled;
    }

    public boolean isEnabledCoding() {
        return enabledCoding;
    }

    public boolean isCancelChatOnValueSet() {
        return cancelChatOnValueSet;
    }

    public boolean isItemsClearCommandBlocksData() {
        return itemsClearCommandBlocksData;
    }

    public int getPrompterMaxExecutors() {
        return prompterMaxExecutors;
    }

    public int getPrompterTimeout() {
        return prompterTimeout;
    }

    public boolean isNotifyNoPlayersAround() {
        return notifyNoPlayersAround;
    }

    public Set<String> getMessagesIgnoringReset() {
        return messagesIgnoringReset;
    }
}
