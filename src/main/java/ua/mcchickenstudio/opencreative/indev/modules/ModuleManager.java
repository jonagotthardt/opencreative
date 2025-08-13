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

package ua.mcchickenstudio.opencreative.indev.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeConfiguration;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

@ApiStatus.Experimental
public class ModuleManager {

    private static ModuleManager instance;

    private final Set<Module> modules = new HashSet<>();

    private ModuleManager() {}
    public static @NotNull ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public @Nullable Module getModuleById(@NotNull String id) {
        for (Module module : modules) {
            if (String.valueOf(module.getId()).equals(id)) {
                return module;
            }
        }
        return null;
    }

    public @NotNull Set<Module> getModules() {
        return new HashSet<>(modules);
    }

    public @NotNull Set<Module> getPlayerModules(@NotNull UUID uuid) {
        Set<Module> playerModules = new HashSet<>();
        for (Module module : modules) {
            if (module.getOwner().equals(uuid)) {
                playerModules.add(module);
            }
        }
        return playerModules;
    }

    public void registerModule(@NotNull Module module) {
        modules.add(module);
    }

    public void createModule(@NotNull Player owner, @NotNull DevPlanet devPlanet, @NotNull Set<Location> locations) {
        CodeConfiguration configuration = new CodeConfiguration();
        if (!new CodingBlockParser(devPlanet).parseExecutors(devPlanet, configuration, new LinkedList<>(locations))) {
            owner.sendMessage(getLocaleMessage("modules.error"));
            return;
        }
        configuration.set("owner",owner.getUniqueId().toString());
        configuration.set("name", MessageUtils.getPlayerLocaleMessage("modules.default-name",owner));
        configuration.set("description", MessageUtils.getPlayerLocaleMessage("modules.default-description",owner));
        configuration.set("icon", Material.CHEST.name());
        configuration.set("creation-time", System.currentTimeMillis());
        try {
            int id = generateModuleId();
            configuration.save(new File(getModuleConfigFile(id).getPath()));
            ModuleManager.getInstance().registerModule(new Module(id));
            owner.sendMessage(getLocaleMessage("modules.created"));
            Sounds.DEV_MODULE_CREATED.play(owner);
        } catch (Exception e) {
            sendPlayerErrorMessage(owner,"Can't create a module",e);
            Sounds.PLAYER_FAIL.play(owner);
        }

    }

    public void deleteModule(Module module) {
        ModuleSettingsMenu.removeFromCurrentEditing(module);
        modules.remove(module);
        File file = getModuleConfigFile(module.getId());
        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception error) {
            sendCriticalErrorMessage("Error while deleting module " + module.getId(), error);
        }
    }

    public static int generateModuleId() {
        int newModuleId = OpenCreative.getPlugin().getConfig().getInt("last-module-id",1);
        while (true) {
            newModuleId++;
            boolean exists = false;
            for (File file : getModulesFiles()) {
                if (file.getName().equalsIgnoreCase("module" + newModuleId + ".yml")) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                OpenCreative.getPlugin().getConfig().set("last-module-id",newModuleId);
                OpenCreative.getPlugin().saveConfig();
                return newModuleId;
            }
        }
    }

}
