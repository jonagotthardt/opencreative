package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.async.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public final class BlockActionCoverage {

    private static final int LIMIT_PER_TICK = 90;
    private static final int LIMIT_TO_PREVENT = 400;

    @Getter
    private static final Map<Integer, ActionsPool> pools = new ConcurrentHashMap<>();

    public static void tick() {
        pools.values().forEach(pool -> {
            List<ActionsPool.Action> toRemove = new ArrayList<>();
            synchronized (pool.getActions()) {
                int actions = 0;
                int limit = (pool.getActions().size() > LIMIT_TO_PREVENT)
                                ? LIMIT_PER_TICK : LIMIT_TO_PREVENT;
                int m = (pool.total > Short.MAX_VALUE)
                                ? 10 : (pool.total > 10000)
                                ? 5 : (pool.total > 3000) ? 3 : (pool.total > 1000) ? 2 : 1;
                limit /= m;
                Iterator<ActionsPool.Action> iterator = pool.getActions().iterator();
                while (iterator.hasNext() && actions < limit) {
                    ActionsPool.Action action = iterator.next();
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                                    () -> action.getBlock().setType(action.getType()));
                    toRemove.add(action);
                    pool.total++;
                    actions++;
                }
                pool.getActions().removeAll(toRemove);
            }

            if (pool.total > 0) pool.total -= 10;
        });
    }
    public static void addBlockAction(Planet planet, List<Pair<Block, Material>> pairs) {
        if (!pools.containsKey(planet.getId())) pools.put(planet.getId(), new ActionsPool());
        ActionsPool pool = pools.get(planet.getId());
        for (Pair<Block, Material> pair : pairs)
            pool.getActions().add(new ActionsPool.Action(pair.getX(), pair.getY()));
    }

    @Data
    public static class ActionsPool {
        private final List<Action> actions = new CopyOnWriteArrayList<>();
        private int total = 0;
        @Data
        @AllArgsConstructor
        public static class Action {
            private final Block block;
            private final Material type;
        }
    }
}
