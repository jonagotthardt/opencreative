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

package ua.mcchickenstudio.opencreative.indev;

import com.google.gson.*;
import com.google.gson.annotations.Since;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebugError;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getWanderJsonFile;

/**
 * <h1>OfflineWander</h1>
 * This class represents wander, that could be offline
 * or online. Wander is a player, who plays on planets.
 * He has nickname, description, gender, favorite
 * worlds and last played world.
 */
public class OfflineWander {

    @Since(5.6)
    private final @NotNull UUID uuid;
    private @Nullable String name;
    private @Nullable String description;
    private @Nullable Gender gender;
    private @Nullable List<Integer> favoriteWorlds;
    private int lastPlayedWorldId = -1;

    public OfflineWander(@NotNull UUID uuid) {
        this.uuid = uuid;
        loadInfo();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public @Nullable Gender getGender() {
        return gender;
    }

    public int getLastPlayedWorldId() {
        return lastPlayedWorldId;
    }

    public @NotNull List<Integer> getFavoriteWorlds() {
        return favoriteWorlds != null ? favoriteWorlds : List.of();
    }

    public void loadInfo() {
        File jsonFile = getWanderJsonFile(this,false);
        if (jsonFile == null || !jsonFile.exists()) {
            return;
        }
        try (Reader reader = new FileReader(jsonFile)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            if (json.has("name")) {
                this.name = json.get("name").getAsString();
            }

            if (json.has("description")) {
                this.description = json.get("description").getAsString();
            }

            if (json.has("gender")) {
                this.gender = Gender.getGender(json.get("gender").getAsString());
            }

            if (json.has("favoriteWorlds")) {
                JsonArray arr = json.getAsJsonArray("favoriteWorlds");
                this.favoriteWorlds = new ArrayList<>();
                for (JsonElement el : arr) {
                    this.favoriteWorlds.add(el.getAsInt());
                }
            }

            if (json.has("lastPlayedWorldId")) {
                this.lastPlayedWorldId = json.get("lastPlayedWorldId").getAsInt();
            }

        } catch (Exception error) {
            sendDebugError("Failed to load info for wander " + uuid, error);
        }
    }

    public void saveData() {
        if (!shouldSaveData()) return;
        File jsonFile = getWanderJsonFile(this,true);
        if (jsonFile == null) {
            return;
        }
        try {
            JsonObject json = getJsonObject();
            try (Writer writer = new FileWriter(jsonFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(json, writer);
            }
        } catch (Exception error) {
            sendDebugError("Failed to save wander data: " + uuid, error);
        }
    }

    private boolean shouldSaveData() {
        return favoriteWorlds != null || description != null || gender != null || lastPlayedWorldId != -1;
    }

    private JsonObject getJsonObject() {
        JsonObject json = new JsonObject();

        json.addProperty("uuid", uuid.toString());
        if (name != null) json.addProperty("name", name);
        if (description != null) json.addProperty("description", description);
        if (gender != null) json.addProperty("gender", gender.name());
        JsonArray favoriteWorlds = new JsonArray();
        if (this.favoriteWorlds != null) {
            for (int id : this.favoriteWorlds) {
                favoriteWorlds.add(id);
            }
        }
        json.add("favoriteWorlds", favoriteWorlds);
        json.addProperty("lastPlayedWorldId", lastPlayedWorldId);
        return json;
    }

    public enum Gender {

        MALE,
        FEMALE,
        OTHER;

        public static @NotNull Gender getGender(@NotNull String text) {
            for (Gender gender : values()) {
                if (gender.name().equalsIgnoreCase(text)) {
                    return gender;
                }
            }
            return OTHER;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OfflineWander wander) {
            return uuid.equals(wander.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
