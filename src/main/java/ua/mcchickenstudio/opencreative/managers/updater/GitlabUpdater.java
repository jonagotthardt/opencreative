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

package ua.mcchickenstudio.opencreative.managers.updater;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.coding.variables.WorldVariable;
import ua.mcchickenstudio.opencreative.utils.ErrorUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class GitlabUpdater implements Updater {

    private boolean updatesAvailable;

    @Override
    public void checkUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(),() -> {
            try {
                String url = "https://gitlab.com/api/v4/projects/58168569/packages?opencreative=&order_by=created_at&sort=desc";
                URL requestUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(5000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                int code = connection.getResponseCode();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        code >= 400 ? connection.getErrorStream() : connection.getInputStream()))) {
                    char[] buffer = new char[512];
                    int read = reader.read(buffer);
                    String response = new String(buffer, 0, Math.max(0, read));
                    int lastBuild = getLastBuildNumber(response);
                    int currentBuild = getCurrentBuildNumber();
                    if (lastBuild > currentBuild) {
                        OpenCreative.getPlugin().getLogger().info("A new build (" + lastBuild + ") is available for downloading! Current: " + currentBuild);
                        OpenCreative.getPlugin().getLogger().info("Visit this site, select latest version, read changelogs and replace OpenCreative.jar with new one.");
                        OpenCreative.getPlugin().getLogger().info("https://gitlab.com/eagles-creative/opencreative/-/packages");
                    }
                }
            } catch (Exception error) {
                ErrorUtils.sendWarningMessage("Can't check updates",error);
            }
        });

    }

    @Override
    public boolean canBeUpdated() {
        return false;
    }

    private int getCurrentBuildNumber() {
        String currentVersion = OpenCreative.getPlugin().getPluginMeta().getVersion();
        if (currentVersion.isEmpty()) {
            updatesAvailable = false;
            return 0;
        }
        String[] currentVersionSplit = currentVersion.split(" ");
        if (currentVersionSplit.length == 0) {
            updatesAvailable = false;
            return 0;
        }
        List<String> lastBuildList = Arrays.asList(currentVersionSplit);
        return Integer.parseInt(lastBuildList.getLast());
    }

    private int getLastBuildNumber(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(json);
            JSONObject object = (JSONObject) array.getFirst();
            String lastVersion = (String) object.get("version");
            String[] lastVersionSplit = lastVersion.split("-");
            if (lastVersionSplit.length == 0) {
                updatesAvailable = false;
                return 0;
            }
            List<String> lastBuildList = Arrays.asList(lastVersionSplit);
            return Integer.parseInt(lastBuildList.getLast());
        } catch (Exception exception) {
            return 0;
        }
    }
}
