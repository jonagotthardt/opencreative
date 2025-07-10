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
    protected final @NotNull UUID uuid;
    protected @Nullable String name;
    protected @Nullable String description;
    protected @Nullable Gender gender;
    protected @Nullable List<Integer> favoriteWorlds;
    protected int lastPlayedWorldId = -1;
    protected boolean hideHints;

    public OfflineWander(@NotNull UUID uuid) {
        this.uuid = uuid;
        loadInfo();
    }

    public OfflineWander(@NotNull OfflinePlayer offlinePlayer) {
        this.uuid = offlinePlayer.getUniqueId();
        loadInfo();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
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

    public boolean isHideHints() {
        return hideHints;
    }

    public @NotNull List<Integer> getFavoriteWorlds() {
        return favoriteWorlds != null ? favoriteWorlds : List.of();
    }

    public void setLastPlayedWorldId(int lastPlayedWorldId) {
        this.lastPlayedWorldId = lastPlayedWorldId;
        saveData();
    }

    public void setName(@NotNull String name) {
        this.name = name;
        saveData();
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
        saveData();
    }

    public void addFavoriteWorld(int worldId) {
        getFavoriteWorlds().add(worldId);
        saveData();
    }

    public void removeFavoriteWorld(int worldId) {
        getFavoriteWorlds().remove(worldId);
        saveData();
    }

    public void setGender(@NotNull Gender gender) {
        this.gender = gender;
        saveData();
    }

    public void clearData() {
        File jsonFile = getWanderJsonFile(this,false);
        if (jsonFile == null || !jsonFile.exists()) {
            return;
        }
        this.name = null;
        this.description = null;
        this.gender = null;
        this.lastPlayedWorldId = -1;
        this.getFavoriteWorlds().clear();
        try {
            jsonFile.delete();
        } catch (Exception error) {
            sendDebugError("Can't delete wander file: " + uuid, error);
        }
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

            if (json.has("hideHints")) {
                this.hideHints = json.get("hideHints").getAsBoolean();
            }

        } catch (Exception error) {
            sendDebugError("Failed to load info for wander " + uuid, error);
        }
    }

    public void saveData() {
        try {
            if (!shouldSaveData()) {
                return;
            }
            File jsonFile = getWanderJsonFile(this,true);
            if (jsonFile == null) {
                return;
            }
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
        if (!favoriteWorlds.isEmpty()) json.add("favoriteWorlds", favoriteWorlds);
        if (lastPlayedWorldId != -1) json.addProperty("lastPlayedWorldId", lastPlayedWorldId);
        if (hideHints) json.addProperty("hideHints", true);
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
