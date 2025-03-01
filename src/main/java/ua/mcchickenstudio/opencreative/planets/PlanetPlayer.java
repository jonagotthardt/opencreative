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

package ua.mcchickenstudio.opencreative.planets;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getPlayerDataJson;

/**
 * <h1>PlanetPlayer</h1>
 * This class represents a Player in planet, he has saved purchases,
 * inventory, own ender chest and other required parameters.
 * Saved data stores in planet's world as /playersData/UUID.json.
 */
public class PlanetPlayer {

    private final Planet currentPlanet;
    private final Player player;

    private final Set<String> purchases = new HashSet<>();
    private final ItemStack[] savedInventory = new ItemStack[41];
    private final ItemStack[] savedEnderChest = new ItemStack[54];

    private final Map<Location, BlockData> codingBlocksBuffer = new HashMap<>();

    public PlanetPlayer(Planet currentPlanet, Player player) {
        this.currentPlanet = currentPlanet;
        this.player = player;
    }

    public Planet getCurrentPlanet() {
        return currentPlanet;
    }

    /**
     * Returns Bukkit's player.
     * @return World player as Bukkit's player.
     */
    public Player getPlayer() {
        return player;
    }

    public ItemStack[] getSavedInventory() {
        return savedInventory;
    }

    public ItemStack[] getSavedEnderChest() {
        return savedEnderChest;
    }

    /**
     * Saves items array as inventory.
     * Used in player action "Save Inventory".
     * @param items Array of ItemStacks to save.
     */
    public void saveInventory(ItemStack[] items) {
        Arrays.fill(savedInventory, new ItemStack(Material.AIR));
        int slot = 0;
        for (ItemStack item : items) {
            savedInventory[slot] = item;
            slot++;
        }
    }

    /**
     * Saves items array as items in ender chest.
     * Used for saving player's own ender chest.
     * @param items Array of ItemStacks to save.
     */
    public void saveEnderChest(ItemStack[] items) {
        Arrays.fill(savedEnderChest, null);
        int slot = 0;
        for (ItemStack item : items) {
            savedEnderChest[slot] = item;
            slot++;
        }
    }

    /**
     * Loads saved player data from JSON file, that
     * stored in planet's folder as /playerData/UUID.json.
     * @return True - if successfully loaded, false - if failed to load.
     */
    @SuppressWarnings("unchecked")
    public boolean load() {
        File playerDataJson = getPlayerDataJson(currentPlanet,player);
        if (playerDataJson == null) {
            return false;
        }
        if (playerDataJson.length() == 0) {
            return true;
        }
        JSONParser parser = new JSONParser();
        try (FileReader fileReader = new FileReader(playerDataJson)) {
            JSONObject playerObject = (JSONObject) parser.parse(fileReader);
            Object purchases = playerObject.getOrDefault("purchases", new JSONArray());
            if (purchases instanceof JSONArray array) {
                this.purchases.addAll(array);
            }
            Object savedInventory = playerObject.getOrDefault("saved-inventory", new JSONArray());
            if (savedInventory instanceof JSONArray array) {
                List<ItemStack> items = new ArrayList<>();
                for (Object object : array) {
                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines((String) object));
                    BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(arrayInputStream);
                    items.add((ItemStack) objectInputStream.readObject());
                }
                saveInventory(items.toArray(new ItemStack[]{}));
            }
            Object savedEnderChest = playerObject.getOrDefault("saved-ender-chest", new JSONArray());
            if (savedEnderChest instanceof JSONArray array) {
                List<ItemStack> items = new ArrayList<>();
                for (Object object : array) {
                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines((String) object));
                    BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(arrayInputStream);
                    items.add((ItemStack) objectInputStream.readObject());
                }
                saveEnderChest(items.toArray(new ItemStack[]{}));
            }
            return true;
        } catch (Exception e) {
            sendCriticalErrorMessage("Couldn't read player data " + player.getName() + " " + currentPlanet.getWorldName());
            return false;
        }
    }

    /**
     * Saves some required player data into JSON file
     * in planet's folder as /playerData/UUID.json.
     * @return True - if successfully saved, false - if failed to save.
     */
    @SuppressWarnings("unchecked")
    public boolean save() {
        File playerDataJson = getPlayerDataJson(currentPlanet,player);
        if (playerDataJson == null) {
            return false;
        }
        try {
            Files.newBufferedWriter(playerDataJson.toPath() , StandardOpenOption.TRUNCATE_EXISTING);
            FileWriter writer = new FileWriter(playerDataJson);
            JSONObject playerObject = new JSONObject();

            JSONArray purchasesJson = new JSONArray();
            purchasesJson.addAll(purchases);
            playerObject.put("purchases",purchasesJson);

            JSONArray savedInventoryJson = new JSONArray();
            for (ItemStack item : savedInventory) {
                final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
                objectOutputStream.writeObject(item);
                String itemString = Base64Coder.encodeLines(arrayOutputStream.toByteArray());
                savedInventoryJson.add(itemString);
            }
            playerObject.put("saved-inventory",savedInventoryJson);

            JSONArray enderChestJson = new JSONArray();
            for (ItemStack item : savedEnderChest) {
                final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
                objectOutputStream.writeObject(item);
                String itemString = Base64Coder.encodeLines(arrayOutputStream.toByteArray());
                enderChestJson.add(itemString);
            }
            playerObject.put("saved-ender-chest",enderChestJson);

            writer.write(playerObject.toString());
            writer.close();
            return true;
        } catch (Exception e) {
            sendCriticalErrorMessage("Couldn't save player data " + player.getName() + " " + currentPlanet.getWorldName(),e);
            return false;
        }
    }

    /**
     * Returns set of saved purchases IDs.
     * @return Set of saved purchases IDs.
     */
    public Set<String> getPurchases() {
        return purchases;
    }

    /**
     * Returns map of buffered location and block data, that
     * are used when player copies coding lines in development planet.
     * @return Map of location and block data of coding blocks.
     */
    public Map<Location, BlockData> getCodingBlocksBuffer() {
        return codingBlocksBuffer;
    }

    /**
     * Adds purchase ID into set of saved player's purchases.
     * @param id ID of purchase.
     */
    public void addPurchase(String id) {
        purchases.add(id);
    }
}
