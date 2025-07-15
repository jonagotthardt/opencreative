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

package ua.mcchickenstudio.opencreative.utils.hooks;

import org.bukkit.World;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ua.mcchickenstudio.opencreative.managers.blocks.BlocksManager;
import ua.mcchickenstudio.opencreative.managers.blocks.DisabledBlocksManager;
import ua.mcchickenstudio.opencreative.managers.blocks.WorldEditManager;
import ua.mcchickenstudio.opencreative.managers.economy.DisabledEconomy;
import ua.mcchickenstudio.opencreative.managers.economy.Economy;
import ua.mcchickenstudio.opencreative.managers.economy.VaultEconomy;
import ua.mcchickenstudio.opencreative.managers.packets.DisabledPacketManager;
import ua.mcchickenstudio.opencreative.managers.packets.PacketManager;
import ua.mcchickenstudio.opencreative.managers.packets.ProtocolLibManager;

public class HookUtils {

    public static boolean isPlaceholderAPIEnabled = false;
    public static boolean isProtocolLibEnabled = false;
    public static boolean isVaultEnabled = false;
    public static boolean isLibsDisguisesEnabled = false;
    public static boolean isWorldEditEnabled = false;

    /**
     Load hooks into other plugins for working with them. For example: Creative+ can hook into PlaceholderAPI.
     **/
    public static void loadHooks() {
        isPlaceholderAPIEnabled = isPluginEnabled("PlaceholderAPI");
        isProtocolLibEnabled = isPluginEnabled("ProtocolLib");
        isVaultEnabled = isPluginEnabled("Vault");
        isWorldEditEnabled = isPluginEnabled("WorldEdit");
        isLibsDisguisesEnabled = isProtocolLibEnabled && isPluginEnabled("LibsDisguises");
        OpenCreative.getPlugin().getLogger().info((isPlaceholderAPIEnabled ? "Successfully integrated to PlaceholderAPI: Added placeholders." : "Didn't detect PlaceholderAPI."));
        OpenCreative.getPlugin().getLogger().info((isProtocolLibEnabled ? "Successfully integrated to ProtocolLib: Added blocks effects and animations." : "Didn't detect ProtocolLib, some block effects will be not available."));
        OpenCreative.getPlugin().getLogger().info((isVaultEnabled ? "Successfully integrated to Vault: Economy actions are working." : "Didn't detect Vault, action Request Purchase will be not available."));
        OpenCreative.getPlugin().getLogger().info((isLibsDisguisesEnabled ? "Successfully integrated to LibsDisguises: Added morph actions." : "Didn't detect LibsDisguises or ProtocolLib, disguise actions will be not available."));
        OpenCreative.getPlugin().getLogger().info((isWorldEditEnabled ? "Successfully integrated to WorldEdit: Added out-of-borders limit." : "Didn't detect WorldEdit."));
        if (isPlaceholderAPIEnabled) {
            PAPIUtils.registerPlaceholder();
        }
    }

    public static boolean isPluginEnabled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    public static void clearEntitiesHook(World world) {
        if (isLibsDisguisesEnabled) DisguiseUtils.clearDisguises(world);
    }

    public static void clearPlayerHook(Player player) {
        if (isLibsDisguisesEnabled) DisguiseUtils.clearDisguise(player);
    }

    public static Economy getEconomy() {
        if (isVaultEnabled) {
            return new VaultEconomy();
        } else {
            return new DisabledEconomy();
        }
    }

    public static PacketManager getPacketManager() {
        if (isProtocolLibEnabled) {
            return new ProtocolLibManager();
        } else {
            return new DisabledPacketManager();
        }
    }

    public static BlocksManager getBlocks() {
        if (isWorldEditEnabled) {
            return new WorldEditManager();
        } else {
            return new DisabledBlocksManager();
        }
    }
}
