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

package ua.mcchickenstudio.opencreative.utils.millennium.types.concurrent;

import lombok.Getter;

import java.util.*;

@SuppressWarnings("unused")
public final class ConcurrentList<T> {

    @Getter
    private final List<T> list;
    private final Set<T> clamp;

    public ConcurrentList(List<T> list) {
        this.list = Collections.synchronizedList(list);
        this.clamp = Collections.synchronizedSet(new HashSet<>());
    }

    public void add(T object) {
        Objects.requireNonNull(object, "Object cannot be null");
        list.add(object);
    }

    public T get(int i) {
        synchronized (list) {
            return list.get(i);
        }
    }

    public void remove(T object) {
        Objects.requireNonNull(object, "Object cannot be null");
        list.remove(object);
    }

    public void clear() {
        list.clear();
    }

    @SafeVarargs
    public final void remove(T... objects) {
        Objects.requireNonNull(objects, "Objects cannot be null");
        synchronized (list) {
            list.removeAll(Arrays.asList(objects));
        }
    }

    public void remove(Set<T> objects) {
        Objects.requireNonNull(objects, "Objects cannot be null");
        synchronized (list) {
            list.removeAll(objects);
        }
    }

    public void removeClamp(T object) {
        Objects.requireNonNull(object, "Object cannot be null");
        clamp.add(object);
    }

    public void applyClamp() {
        synchronized (list) {
            list.removeAll(clamp);
            synchronized (clamp) {
                clamp.clear();
            }
        }
    }
}
