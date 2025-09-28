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

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ForceThreadList<T> {

    private final List<T> list;
    private final Set<T> clamp;
    private final ExecutorService service;

    public ForceThreadList(List<T> list, ExecutorService service) {
        this.list = Collections.synchronizedList(list);
        this.clamp = Collections.synchronizedSet(new HashSet<>());
        this.service = service;
    }

    public ForceThreadList(List<T> list, int pool) {
        this(list, Executors.newScheduledThreadPool(pool));
    }

    public ForceThreadList(List<T> list) {
        this(list, Executors.newScheduledThreadPool(1));
    }

    public void add(T object) {
        service.submit(() -> {
            synchronized (list) {
                list.add(object);
            }
        });
    }

    public T get(int i) {
        try {
            return service.submit(() -> {
                synchronized (list) {
                    return list.get(i);
                }
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get element", e);
        }
    }

    public void remove(T object) {
        service.submit(() -> {
            synchronized (list) {
                list.remove(object);
            }
        });
    }

    public void clear() {
        service.submit(() -> {
            synchronized (list) {
                list.clear();
            }
        });
    }

    @SafeVarargs
    public final void remove(T... objects) {
        service.submit(() -> {
            synchronized (list) {
                list.removeAll(Arrays.asList(objects));
            }
        });
    }

    public void remove(Set<T> objects) {
        service.submit(() -> {
            synchronized (list) {
                list.removeAll(objects);
            }
        });
    }

    public void removeClamp(T object) {
        service.submit(() -> clamp);
    }
}