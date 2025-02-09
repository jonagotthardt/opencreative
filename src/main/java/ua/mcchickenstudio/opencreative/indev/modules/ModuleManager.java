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

import ua.mcchickenstudio.opencreative.utils.ErrorUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.getModuleConfig;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getModuleConfigFile;

public class ModuleManager {

    private static ModuleManager instance;
    private static Set<Module> modules = new HashSet<>();

    private ModuleManager() {}
    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    public void createModule() {

    }

    public void deleteModule(Module module) {
        modules.remove(module);
        File file = getModuleConfigFile(module);
        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception error) {
            ErrorUtils.sendCriticalErrorMessage("Error while deleting module " + module.getId(), error);
        }
    }

}
