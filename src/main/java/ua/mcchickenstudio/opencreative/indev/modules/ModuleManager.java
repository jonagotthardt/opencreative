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
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeConfiguration;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class ModuleManager {

    private static ModuleManager instance;

    private final Set<Module> modules = new HashSet<>();

    private ModuleManager() {}
    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public Module getModuleById(String id) {
        for (Module module : modules) {
            if (String.valueOf(module.getId()).equals(id)) {
                return module;
            }
        }
        return null;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    public void createModule(Player owner, DevPlanet devPlanet, List<Location> locations) {
        CodeConfiguration configuration = new CodeConfiguration();
        if (!new CodingBlockParser().parseExecutors(devPlanet, configuration, locations)) {
            owner.sendMessage(getLocaleMessage("modules.error"));
            return;
        }
        configuration.set("owner",owner.getUniqueId().toString());
        configuration.set("name",getLocaleMessage("modules.default-name",owner));
        configuration.set("description",getLocaleMessage("modules.default-description",owner));
        configuration.set("icon", Material.CHEST.name());
        try {
            int id = generateModuleId();
            configuration.save(new File(getModuleConfigFile(id).getPath()));
            owner.sendMessage(getLocaleMessage("modules.created"));
            Sounds.DEV_MODULE_CREATED.play(owner);
        } catch (Exception e) {
            sendPlayerErrorMessage(owner,"Can't create a module",e);
            Sounds.PLAYER_FAIL.play(owner);
        }

    }

    public void deleteModule(Module module) {
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
