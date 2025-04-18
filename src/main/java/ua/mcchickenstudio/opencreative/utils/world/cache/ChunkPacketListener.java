package ua.mcchickenstudio.opencreative.utils.world.cache;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebugError;

public class ChunkPacketListener extends PacketAdapter {

    public ChunkPacketListener(Plugin plugin) {
        super(plugin, ListenerPriority.HIGHEST,
                        Arrays.asList(PacketType.Play.Server.MAP_CHUNK),
                        ListenerOptions.ASYNC);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        final World world = event.getPlayer().getWorld();
        try {
            final int
            chunkX = event.getPacket().getIntegers().read(0),
            chunkZ = event.getPacket().getIntegers().read(1);
            ChunkCache.preLoad(world, chunkX, chunkZ);
        } catch (Exception error) {
            sendDebugError("Cannot preload chunks.", error);
        }
    }
}
