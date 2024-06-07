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

package mcchickenstudio.creative.utils;

import mcchickenstudio.creative.Main;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class HookUtils {

    public static boolean isPlaceholderAPIEnabled = false;
    /**
     Load hooks into other plugins for working with them. For example: Creative+ can hook into PlaceholderAPI.
     **/
    public static void loadHooks() {
        isPlaceholderAPIEnabled = isPluginEnabled("PlaceholderAPI");
        Main.getPlugin().getLogger().info((isPlaceholderAPIEnabled ? "Creative+ hooked into PlaceholderAPI." : "Creative+ didn't detect PlaceholderAPI."));
        if (isPlaceholderAPIEnabled) {
            PAPIUtils.registerPlaceholder();
        }
    }

    public static boolean isPluginEnabled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

}
