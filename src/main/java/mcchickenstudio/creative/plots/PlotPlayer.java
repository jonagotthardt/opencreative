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

package mcchickenstudio.creative.plots;

import org.bukkit.Material;
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

import static mcchickenstudio.creative.utils.ErrorUtils.sendCriticalErrorMessage;
import static mcchickenstudio.creative.utils.FileUtils.getPlayerDataJson;

public class PlotPlayer {

    private final Plot currentPlot;
    private final Player player;

    private final Set<String> purchases = new HashSet<>();
    private final ItemStack[] savedInventory = new ItemStack[41];
    private final ItemStack[] savedEnderChest = new ItemStack[54];

    public PlotPlayer(Plot currentPlot, Player player) {
        this.currentPlot = currentPlot;
        this.player = player;
    }

    public Plot getCurrentPlot() {
        return currentPlot;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack[] getSavedInventory() {
        return savedInventory;
    }

    public void saveInventory(ItemStack[] items) {
        Arrays.fill(savedInventory, new ItemStack(Material.AIR));
        int slot = 0;
        for (ItemStack item : items) {
            savedInventory[slot] = item;
            slot++;
        }
    }

    public void saveEnderChest(ItemStack[] items) {
        Arrays.fill(savedEnderChest, null);
        int slot = 0;
        for (ItemStack item : items) {
            savedEnderChest[slot] = item;
            slot++;
        }
    }

    @SuppressWarnings("unchecked")
    public void load() {
        File playerDataJson = getPlayerDataJson(currentPlot,player);
        if (playerDataJson == null) {
            return;
        }
        if (playerDataJson.length() == 0) {
            return;
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
        } catch (Exception e) {
            sendCriticalErrorMessage("Couldn't read player data " + player.getName() + " " + currentPlot.worldName);
        }
    }

    @SuppressWarnings("unchecked")
    public void save() {
        File playerDataJson = getPlayerDataJson(currentPlot,player);
        if (playerDataJson == null) {
            return;
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
        } catch (Exception e) {
            sendCriticalErrorMessage("Couldn't save player data " + player.getName() + " " + currentPlot.worldName,e);
        }
    }

    public Set<String> getPurchases() {
        return purchases;
    }

    public void addPurchase(String id) {
        purchases.add(id);
    }
}
