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

package ua.mcchickenstudio.opencreative.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.boss.KeyedBossBar;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.settings.Settings;
import ua.mcchickenstudio.opencreative.utils.async.AsyncScheduler;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.ProtocolLibUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public class PlayerUtils {

    private final static Map<UUID, PermissionAttachment> permissionAttachmentMap = new HashMap<>();

    /**
     * Clears player from modifications made by world
     * and resets his states, parameters and attributes.
     * @param player player to clear.
     */
    public static void clearPlayer(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        PlayerUtils.clearWorldModePermissions(player);
        player.closeInventory();
        if (OpenCreative.getSettings().isLobbyClearInventory()) {
            player.getInventory().clear();
        }
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        resetAttributes(player);
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.removeResourcePacks();
        player.releaseLeftShoulderEntity();
        player.releaseRightShoulderEntity();
        player.setSimulationDistance(Bukkit.getSimulationDistance());
        player.setViewDistance(Math.min(player.getClientViewDistance(),Bukkit.getViewDistance()));
        player.setWorldBorder(player.getWorld().getWorldBorder());
        for (Sound sound : Sound.values()) {
            player.stopSound(sound);
        }
        for (Entity entity : player.getWorld().getEntities()) {
            player.showEntity(OpenCreative.getPlugin(),entity);
        }
        for (Player p : player.getWorld().getPlayers()) {
            player.showEntity(OpenCreative.getPlugin(),p);
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        clearBossBars(player);
        HookUtils.clearPlayerHook(player);
        player.setGameMode(GameMode.ADVENTURE);
    }

    /**
     * Resets changeable attributes for player.
     * @param player player to clear attributes.
     */
    public static void resetAttributes(Player player) {
        player.setFireTicks(0);
        player.setFreezeTicks(0);
        player.setNoDamageTicks(20);
        player.setMaximumNoDamageTicks(20);
        player.setArrowsInBody(0);
        player.setExp(0);
        player.setLevel(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFlying(false);
        player.setGliding(false);
        player.setFlySpeed(0.1f);
        player.setWalkSpeed(0.2f);
        player.setCanPickupItems(true);
        player.setGlowing(false);
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1f);
        player.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(1);
        player.getAttribute(Attribute.GENERIC_STEP_HEIGHT).setBaseValue(0.6f);
    }

    /**
     * Clears and removes displayed bossbars for player.
     * @param player player to clear boss bars.
     */
    public static void clearBossBars(Player player) {
        try {
            for (@NotNull Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
                KeyedBossBar bar = it.next();
                bar.removePlayer(player);
            }
            player.activeBossBars().forEach(player::hideBossBar);
        } catch (Exception ignored) {}
    }

    /**
     * Teleports player to lobby.
     * @param player specified player to teleport.
     **/
    public static void teleportToLobby(Player player) {

        World lobbyWorld = getLobbyWorld();
        if (lobbyWorld != null) {
            player.teleport(lobbyWorld.getSpawnLocation());
        }
        clearPlayer(player);

        player.showTitle(Title.title(
                toComponent(getLocaleMessage("lobby.title")), toComponent(getLocaleMessage("lobby.subtitle")),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
        ));
        player.sendMessage(toComponent(getLocaleMessage("lobby.message")));
        player.playSound(player.getLocation(),Sound.BLOCK_BEACON_DEACTIVATE,100,1.5f);
        player.playSound(player.getLocation(), OpenCreative.getPlugin().getConfig().getString("lobby.sound.name","music_disc.precipice") ,100,(float) OpenCreative.getPlugin().getConfig().getDouble("lobby.sound.pitch",0.1f));

        ItemStack gamesItem = createItem(Material.COMPASS,1,"items.lobby.games","worlds");
        player.getInventory().setItem(3, gamesItem);

        ItemStack myWorldsItem = createItem(Material.NETHER_STAR,1,"items.lobby.own","own_worlds");
        player.getInventory().setItem(5, myWorldsItem);
    }

    /**
     * Returns a lobby world, where player will get lobby items.
     * @return lobby world.
     */
    public static World getLobbyWorld() {
        String spawnWorld = OpenCreative.getPlugin().getConfig().getString("lobby.world");
        if (spawnWorld == null || spawnWorld.isEmpty() || Bukkit.getWorld(spawnWorld) == null) {
            spawnWorld = "world";
        }
        return Bukkit.getWorld(spawnWorld);
    }

    public static boolean isEntityInDevPlanet(Entity entity) {
        return isDevPlanet(entity.getWorld());
    }

    public static boolean isEntityInLobby(Entity entity) {
        return getLobbyWorld().equals(entity.getWorld());
    }

    public static void loadPermissions() {
        permissionAttachmentMap.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPermissions(player);
        }
    }

    public static void loadPermissions(Player player) {
        PermissionAttachment permissionAttachment = player.addAttachment(OpenCreative.getPlugin());
        permissionAttachmentMap.put(player.getUniqueId(),permissionAttachment);
    }

    public static void removeFromPermissionsMap(Player player) {
        clearWorldModePermissions(player);
        permissionAttachmentMap.remove(player.getUniqueId());
    }

    public static void giveDevPermissions(Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        Set<String> perms = OpenCreative.getSettings().getGroups().getGroup(player).getDevPermissions();
        for (String permission : perms) {
            permissionAttachment.setPermission(permission,true);
        }
    }

    public static void givePlayPermissions(Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        Set<String> perms = OpenCreative.getSettings().getGroups().getGroup(player).getPlayPermissions();
        for (String permission : perms) {
            permissionAttachment.setPermission(permission,true);
        }
    }

    public static void giveBuildPermissions(Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        Set<String> perms = OpenCreative.getSettings().getGroups().getGroup(player).getBuildPermissions();
        for (String permission : perms) {
            permissionAttachment.setPermission(permission,true);
        }
    }

    /**
     * Removes player's permissions that he got in world in Build, Play or Dev mode.
     * @param player player to remove permissions.
     */
    public static void clearWorldModePermissions(Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        if (permissionAttachment == null) return;
        Map<String, Boolean> permissions = permissionAttachment.getPermissions();
        Set<Map.Entry<String, Boolean>> permissionsCopy = new HashSet<>(permissions.entrySet());

        for (Map.Entry<String, Boolean> entry : permissionsCopy) {
            String key = entry.getKey();
            permissionAttachment.unsetPermission(key);
        }
    }

    /**
     * Translates sign text on code block.
     * @param block block with sign that will be translated.
     */
    public static void translateBlockSign(Block block) {
        if (!block.getType().toString().contains("SIGN")) return;
        Sign sign = (Sign) block.getState();
        AsyncScheduler.run(() -> {
            List<Component> newLines = new ArrayList<>();
            for (Component line : sign.lines()) {
                String content = ((TextComponent) line).content();
                String path = "blocks." + content;
                if (content.isEmpty()) {
                    newLines.add(Component.text(""));
                } else if (!messageExists(path)) {
                    newLines.add(Component.text(content));
                } else {
                    newLines.add(toComponent(getLocaleMessage(path,false)));
                }
            }
            AsyncScheduler.later(() -> {
                for (Player player : block.getLocation().getWorld().getPlayers()) {
                    player.sendSignChange(block.getLocation(), newLines);
                }
            }, 100, TimeUnit.MILLISECONDS);
        });
    }

    /**
     * Translates sign text on code block.
     * @param block block with sign that will be translated.
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
                newLines.add(toComponent(getLocaleMessage(path,false)));
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendSignChange(block.getLocation(), newLines);
            }
        }.runTaskLater(OpenCreative.getPlugin(),5L);
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
                newLines.add(toComponent(getLocaleMessage(path,false)));
            }
        }
        player.sendSignChange(block.getLocation(), newLines);
    }

    public static void spawnGlowingBlock(Player player, Location location) {
        if (HookUtils.isProtocolLibEnabled) {
            ProtocolLibUtils.spawnGlowingFallingBlock(player,location);
        }
    }

    public static void sendOpenedChestAnimation(Player player, Block block) {
        if (HookUtils.isProtocolLibEnabled && (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST)) {
            ProtocolLibUtils.sendOpenedChestAnimation(player,block);
        }
    }

    public static void sendClosedChestAnimation(Player player, Block block) {
        if (HookUtils.isProtocolLibEnabled && (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST)) {
            ProtocolLibUtils.sendClosedChestAnimation(player,block);
        }
    }

    public static void hidePlayerInTab(Player spectator, Player receiver) {
        if (spectator == receiver) return;
        Settings.PlayerListChanger changer = OpenCreative.getSettings().getListChanger();
        if (changer == Settings.PlayerListChanger.SPECTATOR) {
            if (HookUtils.isProtocolLibEnabled) {
                ProtocolLibUtils.sendSpectatorColoredNickname(spectator,receiver);
            } else {
                receiver.hidePlayer(OpenCreative.getPlugin(),spectator);
            }
        } else if (changer == Settings.PlayerListChanger.FULL) {
            receiver.hidePlayer(OpenCreative.getPlugin(),spectator);
        }
    }

    public static void showPlayerFromTab(Player spectator, Player receiver) {
        if (spectator == receiver) return;
        if (HookUtils.isProtocolLibEnabled && OpenCreative.getSettings().getListChanger() == Settings.PlayerListChanger.SPECTATOR) {
            ProtocolLibUtils.sendSpectatorUncoloredNickname(spectator,receiver);
        } else {
            receiver.showPlayer(OpenCreative.getPlugin(),spectator);
        }
    }

}
