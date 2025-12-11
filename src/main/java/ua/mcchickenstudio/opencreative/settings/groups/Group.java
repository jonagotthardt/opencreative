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

package ua.mcchickenstudio.opencreative.settings.groups;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <h1>Group</h1>
 * This class represents a player group, that stores
 * fields of limits, modifiers and cooldowns.
 */
public class Group {

    private final String name;
    private final String permission;

    private final int worldSize;
    private final int worldsLimit;
    private final int modulesLimit;
    private final int codingPlatformsLimit;
    private final int scriptSizeLimit;

    private final int chatCooldown;
    private final int genericCommandCooldown;
    private final int creativeChatCooldown;
    private final int advertisementCooldown;
    private final int modulesUsageCooldown;
    private final int blocksDuplicationCooldown;

    private final double likeReward;
    private final double advertisementPrice;
    private final boolean canUsePrompter;

    private final Set<String> playPermissions = new HashSet<>();
    private final Set<String> buildPermissions = new HashSet<>();
    private final Set<String> devPermissions = new HashSet<>();
    private final Set<String> lobbyPermissions = new HashSet<>();

    private final Map<LimitType, LimitModifier> limits = new HashMap<>();

    public Group(@NotNull String name, @NotNull FileConfiguration config) {

        this.name = name;
        String path = "groups." + name + ".";
        this.permission = config.getString(path + "permission", "default");

        /*
         * Registering constant values.
         */
        worldSize = config.getInt(path + "world.size",25);
        likeReward = config.getInt(path + "world.like-reward",1);
        advertisementPrice = config.getInt(path + "world.advertisement-cost",0);
        canUsePrompter = config.getBoolean(path + "world.coding-prompter", false);

        /*
         * Registering specific limits, that don't require modifiers.
         */
        worldsLimit = config.getInt(path + "creating-world.limit",1);
        modulesLimit = config.getInt(path + "creating-module.limit",1);
        codingPlatformsLimit = config.getInt(path + "world.limits.coding-platforms",1);
        scriptSizeLimit = config.getInt(path + "world.limits.script-size",10);

        /*
         * Registering cooldowns.
         */
        genericCommandCooldown = config.getInt(path + "cooldowns.generic-command",5);
        advertisementCooldown = config.getInt(path + "cooldowns.advertisement",120);
        creativeChatCooldown = config.getInt(path + "cooldowns.creative-chat",5);
        modulesUsageCooldown = config.getInt(path + "cooldowns.module-usage",7);
        blocksDuplicationCooldown = config.getInt(path + "cooldowns.duplication-usage",7);
        chatCooldown = config.getInt(path + "cooldowns.world-chat",2);

        /*
         * Registering sets of permissions.
         */
        playPermissions.addAll(config.getStringList(path + "world.play-permissions"));
        buildPermissions.addAll(config.getStringList(path + "world.build-permissions"));
        devPermissions.addAll(config.getStringList(path + "world.dev-permissions"));
        lobbyPermissions.addAll(config.getStringList(path + "lobby-permissions"));

        boolean changedConfig = false;
        for (LimitType type : LimitType.values()) {
            String limitPath = path + "world.limits." + type.getPath();
            String modifierPath = path + "world.per-player-limit-modifiers." + type.getPath();
            limits.put(type,
                new LimitModifier(
                    config.getInt(limitPath, type.getDefaultLimit()),
                    config.getInt(modifierPath, type.getDefaultModifier())
            ));

            if (!config.contains(limitPath)) {
                OpenCreative.getPlugin().getConfig().set(limitPath, type.getDefaultLimit());
                changedConfig = true;
            }
            if (!config.contains(modifierPath)) {
                OpenCreative.getPlugin().getConfig().set(modifierPath, type.getDefaultModifier());
                changedConfig = true;
            }
        }
        if (changedConfig) OpenCreative.getPlugin().saveConfig();
    }

    /**
     * Returns ID of group, that was specified in settings.
     * @return ID of group.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns how much time needs to pass to advertise worlds.
     * @return cooldown of advertising worlds.
     */
    public int getAdvertisementCooldown() {
        return advertisementCooldown;
    }

    /**
     * Returns how much time needs to pass to chat in world.
     * @return cooldown of using local chat.
     */
    public int getChatCooldown() {
        return chatCooldown;
    }

    /**
     * Returns how much time needs to pass to chat on server.
     * @return cooldown of using creative chat.
     */
    public int getCreativeChatCooldown() {
        return creativeChatCooldown;
    }

    /**
     * Returns how much time needs to pass to duplicate coding lines.
     * @return cooldown of duplicating coding lines.
     */
    public int getBlocksDuplicationCooldown() {
        return blocksDuplicationCooldown;
    }

    /**
     * Returns how much time needs to pass to install, or create a module.
     * @return cooldown of module operations.
     */
    public int getModuleManipulationCooldown() {
        return modulesUsageCooldown;
    }

    /**
     * Returns how much time needs to pass to use command.
     * @return cooldown of command usage.
     */
    public int getGenericCommandCooldown() {
        return genericCommandCooldown;
    }

    /**
     * Returns size of world, that will be used for setting
     * worlds borders.
     * @return size of world.
     */
    public int getWorldSize() {
        return worldSize;
    }

    /**
     * Returns how many worlds player can create.
     * @return limit of worlds.
     */
    public int getWorldsLimit() {
        return worldsLimit;
    }

    /**
     * Returns how many modules player can create.
     * @return limit of modules.
     */
    public int getModulesLimit() {
        return modulesLimit;
    }

    /**
     * Returns set of permissions, that will be given when player is
     * a builder of world and the world is in build mode.
     * @return set of build permissions.
     */
    public Set<String> getBuildPermissions() {
        return buildPermissions;
    }

    /**
     * Returns set of permissions, that will be given when player is
     * a developer of world, and they entered coding world.
     * @return set of coding permissions.
     */
    public Set<String> getDevPermissions() {
        return devPermissions;
    }

    /**
     * Returns set of permissions, that will be given when player is
     * a developer of world and the world is in play mode.
     * @return set of build permissions.
     */
    public Set<String> getPlayPermissions() {
        return playPermissions;
    }

    /**
     * Returns set of permissions, that will be given when player
     * connects the lobby world.
     * @return set of lobby permissions.
     */
    public Set<String> getLobbyPermissions() {
        return lobbyPermissions;
    }

    /**
     * Returns permission node, that player must to have
     * to get this group.
     * @return permission node of group.
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Returns limit instance for group, that will be used
     * to calculate and check limits.
     * @param type type of limit.
     * @return instance of limit.
     */
    public @NotNull LimitModifier getLimit(LimitType type) {
        for (LimitType limitType : limits.keySet()) {
            if (limitType == type) {
                return limits.get(type);
            }
        }
        return new LimitModifier(type.getDefaultLimit(), type.getDefaultModifier());
    }

    /**
     * Returns maximum amount of coding platforms.
     * @return coding platforms limit.
     */
    public int getCodingPlatformsLimit() {
        return codingPlatformsLimit;
    }

    /**
     * Returns amount of money required to advertise world.
     * @return price of world advertisement.
     */
    public double getAdvertisementPrice() {
        return advertisementPrice;
    }

    /**
     * Returns amount of money, that will be given to world's owner,
     * when someone likes their world.
     * @return reward for world's owner for like.
     */
    public double getLikeReward() {
        return likeReward;
    }

    /**
     * Checks whether player can use prompt handler with /env make.
     * @return true - allowed to use, false - not enough permissions.
     */
    public boolean canUsePrompter() {
        return canUsePrompter;
    }

    /**
     * Returns maximum size of code script in megabytes.
     * @return script's size limit.
     */
    public int getScriptSizeLimit() {
        return scriptSizeLimit;
    }
}
