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

package ua.mcchickenstudio.opencreative.coding;

import org.apache.commons.io.FileUtils;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import org.bukkit.configuration.ConfigurationSection;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getPlanetScriptFile;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>CodeScript</h1>
 * This class represents configuration file that stores planet's code.
 * It has methods to load code and save coding blocks.
 * @see CodingBlockParser
 */
public class CodeScript {

    private final Planet planet;
    private final Executors executors;
    private CodeConfiguration scriptConfig;

    public CodeScript(Planet planet) {
        this.planet = planet;
        this.executors = new Executors(planet);
        this.scriptConfig = new CodeConfiguration();
    }

    /**
     * Loads code from codeScript.yml file.
     */
    public void loadCode() {
        sendCodingDebugLog(planet,"Starting code, please wait...");
        File scriptFile = getPlanetScriptFile(planet);
        long totalSize = ua.mcchickenstudio.opencreative.utils.FileUtils.getFileSize(scriptFile);
        long limit = planet.getGroup().getScriptSizeLimit() * 1024L * 1024L;
        if (totalSize > limit) {
            sendPlanetErrorMessage(planet, getLocaleMessage("world.script-size-limit")
                    .replace("%amount%", FileUtils.byteCountToDisplaySize(totalSize))
                    .replace("%limit%", String.valueOf(planet.getGroup().getScriptSizeLimit())));
            sendCodingDebugLog(planet,"Script File is too large to load :(");
            return;
        }
        scriptConfig = CodeConfiguration.loadConfiguration(scriptFile);
        new BukkitRunnable() {
            @Override
            public void run() {
                executors.load(getPlanetScriptFile(planet));
            }
        }.run();
    }

    /**
     * Saves code script config into file.
     * @return true - if saved, false - if failed.
     */
    public boolean saveCode() {
        long time = System.currentTimeMillis();
        sendCodingDebugLog(planet,"Saving code...");
        try {
            scriptConfig.save(getPlanetScriptFile(planet));
            sendCodingDebugLog(planet,"Saved code in " + (System.currentTimeMillis()-time) + " ms.");
            return true;
        } catch (IOException error) {
            sendCriticalErrorMessage("An IO Exception has occurred while saving code.", error);
            return false;
        }
    }

    /**
     * Moves stored code in old-code section to prevent being overwritten by new code.
     */
    public void clear() {
        executors.clear();
        ConfigurationSection section = scriptConfig.getConfigurationSection("code.blocks");
        if (section == null) return;
        scriptConfig.set("old-code.blocks", null);
        Map<String, Object> newCode = section.getValues(false);
        scriptConfig.set("old-code.blocks", newCode);
        scriptConfig.set("code.blocks", null);
        scriptConfig.set("last-activity-time", System.currentTimeMillis());
        try {
            scriptConfig.save(getPlanetScriptFile(planet));
        } catch (IOException exception) {
            sendCriticalErrorMessage("An error has occurred while clearing and saving code script " + this.getPlanet().getWorldName(),exception);
        }
    }

    public CodeConfiguration getConfig() {
        return scriptConfig;
    }

    public void unload() {
        scriptConfig = new CodeConfiguration();
        executors.clear();
    }

    public Executors getExecutors() {
        return executors;
    }

    public Planet getPlanet() {
        return planet;
    }
}

