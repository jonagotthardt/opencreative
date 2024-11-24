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

package ua.mcchickenstudio.opencreative.utils.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.comphenix.protocol.PacketType.Play.Server.*;
import static com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE;

public class ProtocolLibUtils {

    private static ProtocolManager manager;

    public static void init() {
        manager = ProtocolLibrary.getProtocolManager();
        registerEvents();
    }
    
    private static void registerEvents() {
        OpenCreative.getPlugin().getLogger().info("Registered all ProtocolLib events.");
    }

    public static void spawnGlowingFallingBlock(Player player, Location location) {
        World world = player.getWorld();
        PacketContainer spawnEntityPacket = manager.createPacket(SPAWN_ENTITY);
        spawnEntityPacket.getIntegers().write(0, 83);
        spawnEntityPacket.getUUIDs().write(0, UUID.randomUUID());
        spawnEntityPacket.getEntityTypeModifier().write(0, EntityType.SHULKER);
        spawnEntityPacket.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        PacketContainer entityDataPacket = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) (0x40 | 0x20));
        entityDataPacket.getIntegers().write(0, 83);
        List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
        for(final WrappedWatchableObject entry : watcher.getWatchableObjects()) {
            if(entry == null) continue;
            final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(
                    new WrappedDataValue(
                            watcherObject.getIndex(),
                            watcherObject.getSerializer(),
                            entry.getRawValue()
                    )
            );
        }
        entityDataPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        PacketContainer hideGlowingPacket = manager.createPacket(ENTITY_DESTROY);
        hideGlowingPacket.getModifier().write(0, new IntArrayList(new int[]{83}));
        manager.sendServerPacket(player,spawnEntityPacket);
        manager.sendServerPacket(player,entityDataPacket);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getWorld() == world) {
                    manager.sendServerPacket(player,hideGlowingPacket);
                }
            }
        }.runTaskLater(OpenCreative.getPlugin(),60L);
    }

    public static void sendOpenedChestAnimation(Player player, Block block) {
        PacketContainer blockActionPacket = manager.createPacket(BLOCK_ACTION);
        blockActionPacket.getBlockPositionModifier().write(0, new BlockPosition(block.getLocation().toVector()));
        blockActionPacket.getIntegers().write(0,1);
        blockActionPacket.getIntegers().write(1,1);
        manager.sendServerPacket(player,blockActionPacket);
    }

    public static void sendClosedChestAnimation(Player player, Block block) {
        PacketContainer blockActionPacket = manager.createPacket(BLOCK_ACTION);
        blockActionPacket.getBlockPositionModifier().write(0, new BlockPosition(block.getLocation().toVector()));
        blockActionPacket.getIntegers().write(0,1);
        blockActionPacket.getIntegers().write(1,0);
        manager.sendServerPacket(player,blockActionPacket);
    }

    public static void sendSpectatorColoredNickname(Player spectator, Player receiver) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoActions().write(0, EnumSet.of(UPDATE_GAME_MODE));
        packet.getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                new WrappedGameProfile(spectator.getUniqueId(), spectator.getName()),
                spectator.getPing(),
                EnumWrappers.NativeGameMode.SPECTATOR,
                WrappedChatComponent.fromText(spectator.getName())
        )));
        manager.sendServerPacket(receiver,packet);
    }

    public static void sendSpectatorUncoloredNickname(Player spectator, Player receiver) {
        if (spectator.getGameMode() == GameMode.SPECTATOR) return;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoActions().write(0, EnumSet.of(UPDATE_GAME_MODE));
        packet.getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                new WrappedGameProfile(spectator.getUniqueId(), spectator.getName()),
                spectator.getPing(),
                EnumWrappers.NativeGameMode.valueOf(spectator.getGameMode().name()),
                WrappedChatComponent.fromText(spectator.getName())
        )));
        manager.sendServerPacket(receiver,packet);
    }
}
