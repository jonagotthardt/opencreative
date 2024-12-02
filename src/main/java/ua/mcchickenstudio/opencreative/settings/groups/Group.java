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

package ua.mcchickenstudio.opencreative.settings.groups;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

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

    private final int worldsLimit;
    private final int worldSize;

    private final int genericCommandCooldown;
    private final int creativeChatCooldown;
    private final int advertisementCooldown;
    private final int chatCooldown;

    private final Set<String> playPermissions = new HashSet<>();
    private final Set<String> buildPermissions = new HashSet<>();
    private final Set<String> devPermissions = new HashSet<>();

    private final Map<LimitType, LimitModifier> limits = new HashMap<>();

    public Group(String name, FileConfiguration config) {
        this.name = name;
        String path = "groups." + name + ".";
        this.permission = config.getString(path + "permission","default");
        worldsLimit = config.getInt(path + "creating-world.limit",1);
        worldSize = config.getInt(path + "world.size",25);
        genericCommandCooldown = config.getInt(path + "cooldown.generic",5);
        advertisementCooldown = config.getInt(path + "cooldown.advertisement",120);
        creativeChatCooldown = config.getInt(path + "cooldown.creative-chat",5);
        chatCooldown = config.getInt(path + "cooldown.chat",2);
        playPermissions.addAll(config.getStringList(path + "play-permissions"));
        buildPermissions.addAll(config.getStringList(path + "build-permissions"));
        devPermissions.addAll(config.getStringList(path + "dev-permissions"));
        for (LimitType type : LimitType.values()) {
            limits.put(type,
                    new LimitModifier(
                            config.getInt(path + "world.limits." + type.getPath(),0),
                            config.getInt(path + "world.per-player-limit-modifiers." + type.getPath(),0)
                    ));
        }
    }

    public String getName() {
        return name;
    }

    public int getAdvertisementCooldown() {
        return advertisementCooldown;
    }

    public int getChatCooldown() {
        return chatCooldown;
    }

    public int getCreativeChatCooldown() {
        return creativeChatCooldown;
    }

    public int getGenericCommandCooldown() {
        return genericCommandCooldown;
    }

    public int getWorldSize() {
        return worldSize;
    }

    public int getWorldsLimit() {
        return worldsLimit;
    }

    public Set<String> getBuildPermissions() {
        return buildPermissions;
    }

    public Set<String> getDevPermissions() {
        return devPermissions;
    }

    public Set<String> getPlayPermissions() {
        return playPermissions;
    }

    public String getPermission() {
        return permission;
    }

    public @NotNull LimitModifier getLimit(LimitType type) {
        for (LimitType limitType : limits.keySet()) {
            if (limitType == type) {
                return limits.get(type);
            }
        }
        return new LimitModifier(0,0);
    }
}
