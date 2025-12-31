/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.utils.millennium.types;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class EvictingMap<K, V> extends HashMap<K, V> {

    private final int size;
    private final Deque<K> storedKeys = new LinkedList<>();

    public EvictingMap(int size) {
        this.size = size;
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        storedKeys.remove(key);
        return super.remove(key, value);
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        if (!storedKeys.contains(key) || !get(key).equals(value))
            checkAndRemove();
        return super.putIfAbsent(key, value);
    }

    @Override
    public V put(final K key, final V value) {
        checkAndRemove();
        storedKeys.addLast(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        storedKeys.clear();
        super.clear();
    }

    @Override
    public V remove(final Object key) {
        storedKeys.remove(key);
        return super.remove(key);
    }

    private boolean checkAndRemove() {
        if (storedKeys.size() >= size) {
            final K key = storedKeys.removeFirst();

            remove(key);
            return true;
        }
        return false;
    }
}