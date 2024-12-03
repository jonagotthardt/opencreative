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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.HashSet;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;

/**
 * <h1>Groups</h1>
 * This class represents a set of groups, it's used
 * when getting player's or world's owner group to
 * get some limits or modifiers.
 */
public class Groups {

    private final Set<Group> groups = new HashSet<>();

    public void load() {
        groups.clear();
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("groups");
        if (section == null) {
            sendCriticalErrorMessage("Can't load player groups, section `groups` in config.yml is empty.");
            return;
        }
        for (String group : section.getKeys(false)) {
            registerGroup(new Group(group,config));
        }
    }

    public @NotNull Group getDefaultGroup() {
        for (Group group : groups) {
            if (group.getPermission().equalsIgnoreCase("default")) {
                return group;
            }
        }
        return new Group("default",OpenCreative.getPlugin().getConfig());
    }

    public @NotNull Group getGroup(String name) {
        for (Group group : groups) {
            if (group.getName().equalsIgnoreCase(name)) {
                return group;
            }
        }
        return getDefaultGroup();
    }

    public @NotNull Group getGroup(Player player) {
        Group currentGroup = getDefaultGroup();
        for (Group group : groups) {
            if (player.hasPermission(group.getPermission())) {
                currentGroup = group;
            }
        }
        return currentGroup;
    }

    public void registerGroup(Group group) {
        OpenCreative.getPlugin().getLogger().info("Registered player group " + group.getName());
        groups.add(group);
    }

}
