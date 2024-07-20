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

package mcchickenstudio.creative.utils.hooks;

import mcchickenstudio.creative.Main;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class HookUtils {

    public static boolean isPlaceholderAPIEnabled = false;
    public static boolean isProtocolLibEnabled = false;
    public static boolean isVaultEnabled = false;
    /**
     Load hooks into other plugins for working with them. For example: Creative+ can hook into PlaceholderAPI.
     **/
    public static void loadHooks() {
        isPlaceholderAPIEnabled = isPluginEnabled("PlaceholderAPI");
        isProtocolLibEnabled = isPluginEnabled("ProtocolLib");
        isVaultEnabled = isPluginEnabled("Vault");
        Main.getPlugin().getLogger().info((isPlaceholderAPIEnabled ? "Creative+ hooked into PlaceholderAPI." : "Creative+ didn't detect PlaceholderAPI."));
        Main.getPlugin().getLogger().info((isProtocolLibEnabled ? "Creative+ hooked into ProtocolLib." : "Creative+ didn't detect PlaceholderAPI."));
        Main.getPlugin().getLogger().info((isVaultEnabled ? "Creative+ hooked into Vault." : "Creative+ didn't detect Vault."));
        if (isPlaceholderAPIEnabled) {
            PAPIUtils.registerPlaceholder();
        }
        if (isProtocolLibEnabled) {
            ProtocolLibUtils.init();
        }
        if (isVaultEnabled) {
            VaultUtils.init();
        }
    }

    public static boolean isPluginEnabled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

}
