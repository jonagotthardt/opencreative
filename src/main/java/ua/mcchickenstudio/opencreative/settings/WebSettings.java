/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ua.mcchickenstudio.opencreative.OpenCreative;

/**
 * <h1>WebSettings</h1>
 * This class represents a settings of web services.
 */
public final class WebSettings {

    private String displayLink = "localhost:8080";
    private int port = 8080;

    /**
     * Loads settings of web from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("web");
        if (section == null) {
            section = config.createSection("web");
        }

        displayLink = section.getString("display-link", "localhost:8080");
        port = section.getInt("port", 8080);
    }

    /**
     * Returns a link, that will be displayed
     * for player to visit, instead of numeric IP.
     * <p>
     * Example: yourserver.net
     *
     * @return display link
     */
    public String getDisplayLink() {
        return displayLink;
    }

    /**
     * Returns a port, that will be used
     * for opening a site.
     *
     * @return port
     */
    public int getPort() {
        return port;
    }
}
