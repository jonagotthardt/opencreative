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

package ua.mcchickenstudio.opencreative.managers.updater;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;

public class HangarUpdater implements Updater {

    private boolean updatesAvailable;

    @Override
    public void checkUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(),() -> {
            try {
                HttpURLConnection connection = getHttpURLConnection();
                int code = connection.getResponseCode();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        code >= 400 ? connection.getErrorStream() : connection.getInputStream()))) {
                    char[] buffer = new char[64];
                    int read = reader.read(buffer);
                    String response = new String(buffer, 0, Math.max(0, read));
                    int latestVersion = getLatestVersion(response);
                    int currentVersion = getCurrentVersion();
                    if (latestVersion > currentVersion) {
                        OpenCreative.getPlugin().getLogger().info("A new version (" + getSemVer(response) + ") is available for downloading! Current: " + getSemVer(OpenCreative.getPlugin().getPluginMeta().getVersion()));
                        OpenCreative.getPlugin().getLogger().info("Visit this site, select latest version, read changelogs and replace OpenCreative.jar with new one.");
                        OpenCreative.getPlugin().getLogger().info("https://hangar.papermc.io/mcchickenstudio/OpenCreative");
                    } else {
                        OpenCreative.getPlugin().getLogger().info("No updates detected, probably it's latest version :) (" + currentVersion + " - " + latestVersion + ")");
                    }
                }
            } catch (Exception error) {
                sendDebugError("Can't check updates",error);
            }
        });
    }

    private static HttpURLConnection getHttpURLConnection() throws IOException {
        String url = "https://hangar.papermc.io/api/v1/projects/OpenCreative/latestrelease";
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/plain");
        return connection;
    }

    @Override
    public boolean canBeUpdated() {
        return updatesAvailable;
    }

    private int getCurrentVersion() {
        String currentVersion = getSemVer(OpenCreative.getPlugin().getPluginMeta().getVersion());
        if (currentVersion.isEmpty()) {
            updatesAvailable = false;
            return 0;
        }
        String[] currentVersionSplit = currentVersion.split("\\.");
        if (currentVersionSplit.length < 2) {
            updatesAvailable = false;
            return 0;
        }
        /*
         * Example:
         * 5.3.0 -> 530
         * 5.2 -> 520
         * 5.3.2 Technical Preview -> 532
         */
        String semVer = currentVersion.replaceAll("\\D","")
                + (currentVersionSplit.length == 2 ? "0" : "");
        return Integer.parseInt(semVer);
    }

    private int getLatestVersion(String text) {
        try {
            String lastVersion = getSemVer(text);
            String[] lastVersionSplit = lastVersion.split("\\.");
            if (lastVersionSplit.length < 2) {
                updatesAvailable = false;
                return 0;
            }
            /*
             * Example:
             * 5.3.0 -> 530
             * 5.2 -> 520
             * 5.3.2 Technical Preview -> 532
             */
            String semVer = lastVersion.replace(".","")
                    + (lastVersionSplit.length == 2 ? "0" : "");
            return Integer.parseInt(semVer);
        } catch (Exception exception) {
            return 0;
        }
    }

    private String getSemVer(String version) {
        return version.replaceAll("[^\\d.]","");
    }

    @Override
    public void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkUpdates();
            }
        }.runTaskLaterAsynchronously(OpenCreative.getPlugin(),60L);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "Hangar Updater";
    }
}
