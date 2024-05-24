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
