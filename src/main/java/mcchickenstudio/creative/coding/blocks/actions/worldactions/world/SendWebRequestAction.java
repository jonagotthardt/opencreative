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

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class SendWebRequestAction extends WorldAction {
    public SendWebRequestAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        String url = getArguments().getValue("url", "", this);
        String body = getArguments().getValue("body", "", this);
        String request = getArguments().getValue("request", "GET", this).toUpperCase();
        String media = getArguments().getValue("media", "text", this);

        if (url.isEmpty()) return;

        if (!List.of("GET", "POST").contains(request)) {
            throw new IllegalArgumentException("Invalid HTTP method, use GET or POST.");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("Invalid URL protocol, use http:// or https:// on the start of URL.");
        }

        if (url.toLowerCase().startsWith("http://localhost")
                || url.toLowerCase().startsWith("http://127.0.0.1")
                || url.toLowerCase().startsWith("https://localhost")
                || url.toLowerCase().startsWith("https://127.0.0.1")
        ) {
            throw new IllegalArgumentException("This URL is blocked from sending requests.");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = getHttpURLConnection(url, request, media);

                    if ("POST".equalsIgnoreCase(request) && !body.isEmpty()) {
                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(body.getBytes(StandardCharsets.UTF_8));
                        }
                    }

                    int code = connection.getResponseCode();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                            code >= 400 ? connection.getErrorStream() : connection.getInputStream()))) {
                        char[] buffer = new char[1024];
                        int read = reader.read(buffer);
                        String response = new String(buffer, 0, Math.max(0, read));
                        Bukkit.getScheduler().runTask(Main.getPlugin(),
                                () -> EventRaiser.raiseWebResponseEvent(getPlot(), url, code, response));
                    }
                } catch (Exception e) {
                    Bukkit.getScheduler().runTask(Main.getPlugin(),
                            () -> EventRaiser.raiseWebResponseEvent(getPlot(), url, 408, "Error: " + e.getMessage()));
                }
            }
        }.runTaskAsynchronously(Main.getPlugin());

    }

    private static HttpURLConnection getHttpURLConnection(String url, String request, String media) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod(request);
        connection.setDoOutput("POST".equalsIgnoreCase(request));

        if ("json".equalsIgnoreCase(media)) {
            connection.setRequestProperty("Content-Type", "application/json");
        } else {
            connection.setRequestProperty("Content-Type", "text/plain");
        }
        return connection;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SEND_WEB_REQUEST;
    }
}
