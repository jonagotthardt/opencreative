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

import java.util.HashSet;
import java.util.Set;

public class Group {

    private int worldsLimit = 2;
    private int worldSize = 25;

    private int genericCommandCooldown = 5;
    private int creativeChatCooldown = 5;
    private int advertisementCooldown = 120;
    private int chatCooldown = 2;

    private final Set<String> playPermissions = new HashSet<>();
    private final Set<String> buildPermissions = new HashSet<>();
    private final Set<String> devPermissions = new HashSet<>();

    public void load(String name, FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("groups." + name);
        if (section == null) return;
        worldsLimit = section.getInt("creating-world.limit",worldsLimit);
        worldSize = section.getInt("world.size",worldSize);
    }

}
