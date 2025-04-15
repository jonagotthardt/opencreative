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

package ua.mcchickenstudio.opencreative.utils.world.cache;

import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <h1>ChunkCache</h1>
 * Which saves the state of the chunk in the
 * cache so as not to overload the memory
 */


// author: pawsashatoy
@UtilityClass
public class ChunkCache {
    private final Map<Long, Boolean> statusKeeper = new ConcurrentHashMap<>();
    public boolean isChunkGenerated(final World world, final int i, final int i1) {
        if (world == null) return false;
        final long hash = hash(world, i, i1);
        return statusKeeper.computeIfAbsent(hash, k -> world.isChunkGenerated(i, i1));
    }
    public void preCheck(final World world, final int i, final int i1) {
        if (world == null) return;
        final long hash = hash(world, i, i1);
        statusKeeper.computeIfAbsent(hash, k -> world.isChunkGenerated(i, i1));
    }
    public void preLoad(final World world, final int i, final int i1) {
        if (world == null) return;
        final long hash = hash(world, i, i1);
        statusKeeper.putIfAbsent(hash, true);
    }
    public void unload(final World world, final int i, final int i1) {
        if (world == null) return;
        final long hash = hash(world, i, i1);
        statusKeeper.putIfAbsent(hash, false);
    }
    private long hash(final World world, final int i, final int i1) {
        return (31L * i + i1 + world.getUID().hashCode()) * 31L;
    }
}
