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

package mcchickenstudio.creative.utils;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.utils.hooks.HookUtils;
import mcchickenstudio.creative.utils.hooks.ProtocolLibUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCriticalErrorMessage;
import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.messageExists;

public class PlayerUtils {

    public enum PlayerLimit {

        WORLD_SIZE("world.size"),
        WORLD_ENTITIES_LIMIT("world.entities-limit"),
        WORLD_CODE_OPERATIONS_LIMIT("world.code-operations-limit"),
        WORLD_REDSTONE_OPERATIONS_LIMIT("world.redstone-operations-limit"),
        WORLD_OPENING_INVENTORIES_LIMIT("world.opening-inventories-limit"),
        WORLD_VARIABLES_LIMIT("world.variables-limit"),
        WORLD_MODIFYING_BLOCKS_LIMIT("world.modifying-blocks-limit"),
        PLAYER_WORLDS_AMOUNT_LIMIT("creating-world.limit");

        private final String path;

        PlayerLimit(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    @NotNull
    final static Plugin plugin = Main.getPlugin();
    final static Map<UUID, PermissionAttachment> permissionAttachmentMap = new HashMap<>();

    /**
     Returns player's group from config.yml. If player doesn't have any "creative.group" permissions, then returns "default".
     **/
    public static String getGroup(Player player) {
        ConfigurationSection groupsSection = plugin.getConfig().getConfigurationSection("groups");
        if (groupsSection != null) {
            String returnGroup;
            returnGroup = "default";
            for (String group : groupsSection.getKeys(false)) {
                if (!(group.equals("default"))) {
                    String permission = plugin.getConfig().getString("groups." + group + ".permission");
                    if (permission != null && player.hasPermission(permission)) {
                        returnGroup = group;
                    }
                }
            }
            return returnGroup;
        } else {
            sendCriticalErrorMessage("При попытке получить группу игрока оказалось, что секция groups из config.yml не заполнена, вам необходимо её заполнить.");
            return "default";
        }
    }

    public static int getPlayerLimitValue(Player player, PlayerLimit type) {
        return getIntFromGroups(player,type.getPath());
    }

    public static int getPlayerLimitValue(String group, PlayerLimit type) {
        return getIntFromGroups(group,type.getPath());
    }

    public static int getIntFromGroups(Player player, String intPath) {
        return plugin.getConfig().getInt("groups." + getGroup(player) + "." + intPath);
    }

    public static int getIntFromGroups(String group, String intPath) {
        return plugin.getConfig().getInt("groups." + group + "." + intPath);
    }

    public static int getListFromGroups(String group, String listPath) {
        return plugin.getConfig().getInt("groups." + group + "." + listPath);
    }

    public static int getPlayerPlotsLimit(Player player) {
        return getIntFromGroups(player,"creating-world.limit");
    }

    public static int getPlayerPlotSize(String group) {
        return getIntFromGroups(group,"world.size");
    }

    public static int getPlayerPlotEntitiesLimit(String group) {
        return getIntFromGroups(group,"world.entities-limit");
    }

    public static int getPlayerPlotCodeOperationsLimit(String group) {
        return getIntFromGroups(group,"world.code-operations-limit");
    }

    public static int getPlayerPlotRedstoneOperationsLimit(String group) {
        return getIntFromGroups(group,"world.redstone-operations-limit");
    }

    public static int getPlayerPlot(String group) {
        return getIntFromGroups(group,"world.redstone-operations-limit");
    }


    public static int getPlayerPermissionsList(String group) {
        return getListFromGroups(group,"permissions");
    }

    public static void loadPermissions() {

        permissionAttachmentMap.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PermissionAttachment permissionAttachment = player.addAttachment(plugin);
            permissionAttachmentMap.put(player.getUniqueId(),permissionAttachment);
        }
        Main.getPlugin().getLogger().info("Loaded build permissions for every player...");

    }

    public static void loadPermissions(Player player) {

        PermissionAttachment permissionAttachment = player.addAttachment(plugin);
        permissionAttachmentMap.put(player.getUniqueId(),permissionAttachment);

    }

    public static void removeFromPermissionsMap(Player player) {

        permissionAttachmentMap.remove(player.getUniqueId());

    }

    public static void  giveBuildPermissions(Player player) {

        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());

        List<String> buildPermissions = plugin.getConfig().getStringList("groups." + getGroup(player) + ".world.build-permissions");
        for (String permission : buildPermissions) {
            permissionAttachment.setPermission(permission,true);
        }

    }

    /**
     Clears player's builders permissions.
     **/
    public static void clearBuildPermissions(Player player) {

        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        Map<String, Boolean> permissions = permissionAttachment.getPermissions();
        Map<String, Boolean> permissions2 = new HashMap<>(permissions);

        for (Map.Entry<String, Boolean> entry : permissions2.entrySet()) {
            String key = entry.getKey();
            permissionAttachment.unsetPermission(key);
        }

    }

    /**
     Clears player's settings: inventory, health, fire ticks, game mode.
     **/
    public static void clearPlayer(Player player) {
        PlayerUtils.clearBuildPermissions(player);
        player.closeInventory();
        player.getInventory().clear();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setFireTicks(0);
        player.setFreezeTicks(0);
        player.setNoDamageTicks(20);
        player.setMaximumNoDamageTicks(20);
        player.setArrowsInBody(0);
        player.setExp(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setGliding(false);
        player.setFlySpeed(0.1f);
        player.setWalkSpeed(0.2f);
        player.setGlowing(false);
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.removeResourcePacks();

        for (Entity entity : player.getWorld().getEntities()) {
            player.showEntity(Main.getPlugin(),entity);
        }

        for (Player p : player.getWorld().getPlayers()) {
            player.showEntity(Main.getPlugin(),p);
        }

        for (Sound sound : Sound.values()) {
            player.stopSound(sound);
        }
    }

    /**
     Teleports player to lobby.
     @param player specified player to teleport.
     **/
    public static void teleportToLobby(Player player) {

        String spawnWorld = Main.getPlugin().getConfig().getString("spawn.world");
        if (spawnWorld == null || spawnWorld.isEmpty()) {
            spawnWorld = "world";
        }
        World lobbyWorld = Bukkit.getWorld(spawnWorld);
        if (lobbyWorld != null) {
            player.teleport(lobbyWorld.getSpawnLocation());
        }
        clearPlayer(player);

        player.sendTitle(getLocaleMessage("lobby.title"), getLocaleMessage("lobby.subtitle"),20,60,20);
        player.sendMessage(getLocaleMessage("lobby.message"));
        player.playSound(player.getLocation(),Sound.BLOCK_BEACON_DEACTIVATE,100,1.5f);
        player.playSound(player.getLocation(),Sound.MUSIC_DISC_CREATOR ,100,0.7f);

        ItemStack gamesItem = createItem(Material.COMPASS,1,"items.lobby.games");
        player.getInventory().setItem(3, gamesItem);

        ItemStack myWorldsItem = createItem(Material.NETHER_STAR,1,"items.lobby.own");
        player.getInventory().setItem(5, myWorldsItem);
    }

    /**
     * Translate sign text on code block.
     * @param block Block with sign that will be translated.
     */
    public static void translateBlockSign(Block block) {
        if (!block.getType().toString().contains("SIGN")) return;
        Sign sign = (Sign) block.getState();
        List<Component> newLines = new ArrayList<>();
        for (Component line : sign.lines()) {
            String content = ((TextComponent) line).content();
            String path = "blocks." + content;
            if (content.isEmpty()) {
                newLines.add(Component.text(""));
            } else if (!messageExists(path)) {
                newLines.add(Component.text(content));
            } else {
                newLines.add(Component.text(getLocaleMessage(path,false)));
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : block.getLocation().getWorld().getPlayers()) {
                    player.sendSignChange(block.getLocation(), newLines);
                }
            }
        }.runTaskLater(Main.getPlugin(),5L);
    }

    /**
     * Translate sign text on code block.
     * @param block Block with sign that will be translated.
     */
    public static void translateBlockSign(Block block, Player player) {
        if (block == null) return;
        if (!block.getType().toString().contains("SIGN")) return;
        Sign sign = (Sign) block.getState();
        List<Component> newLines = new ArrayList<>();
        for (Component line : sign.lines()) {
            String content = ((TextComponent) line).content();
            String path = "blocks." + content;
            if (content.isEmpty()) {
                newLines.add(Component.text(""));
            } else if (!messageExists(path)) {
                newLines.add(Component.text(content));
            } else {
                newLines.add(Component.text(getLocaleMessage(path,false)));
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendSignChange(block.getLocation(), newLines);
            }
        }.runTaskLater(Main.getPlugin(),5L);
    }

    /**
     * Translate sign text on code block.
     * @param block Block with sign that will be translated.
     */
    public static void translateSign(Block block, Player player) {
        if (block == null) return;
        if (!block.getType().toString().contains("SIGN")) return;
        Sign sign = (Sign) block.getState();
        List<Component> newLines = new ArrayList<>();
        for (Component line : sign.lines()) {
            String content = ((TextComponent) line).content();
            String path = "blocks." + content;
            if (content.isEmpty()) {
                newLines.add(Component.text(""));
            } else if (!messageExists(path)) {
                newLines.add(Component.text(content));
            } else {
                newLines.add(Component.text(getLocaleMessage(path,false)));
            }
        }
        player.sendSignChange(block.getLocation(), newLines);
    }

    public static void spawnGlowingBlock(Player player, Location location) {
        if (HookUtils.isProtocolLibEnabled) {
            ProtocolLibUtils.spawnGlowingFallingBlock(player,location);
        }
    }

}
