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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.world;

import com.google.gson.Gson;
import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.utils.ErrorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class SendWebRequestAction extends WorldAction {
    public SendWebRequestAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        String url = getArguments().getValue("url","",this);
        String body = getArguments().getValue("body","",this);
        String request = getArguments().getValue("request","get",this);
        String media = getArguments().getValue("media","text",this);
        if (url.isEmpty()) return;
        if (body.isEmpty()) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL requestUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestMethod(request.toUpperCase());
                    connection.setDoOutput("POST".equalsIgnoreCase(request)); // Разрешить отправку данных для POST
                    if ("json".equalsIgnoreCase(media)) {
                        connection.setRequestProperty("Content-Type", "application/json");
                    } else if ("text".equalsIgnoreCase(media)) {
                        connection.setRequestProperty("Content-Type", "text/plain");
                    }
                    if ("POST".equalsIgnoreCase(request)) {
                        connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
                    }
                    int code = connection.getResponseCode();
                    InputStream is = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder responseString = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseString.append(line);
                    }
                    bufferedReader.close();
                    connection.disconnect();
                    String response = responseString.toString();
                    response = response.substring(0,Math.min(1024,response.length()));
                    String finalResponse = response;
                    Bukkit.getScheduler().runTask(Main.getPlugin(), () -> EventRaiser.raiseWebResponseEvent(getPlot(),url,code, finalResponse));
                } catch (Exception error) {
                    Bukkit.getScheduler().runTask(Main.getPlugin(), () -> EventRaiser.raiseWebResponseEvent(getPlot(),url,408,"Error: " + error.getMessage()));
                }

            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SEND_WEB_REQUEST;
    }
}
