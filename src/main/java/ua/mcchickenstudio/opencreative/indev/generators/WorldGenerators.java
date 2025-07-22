package ua.mcchickenstudio.opencreative.indev.generators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;

public final class WorldGenerators {

    private static WorldGenerators instance;
    private final List<WorldGenerator> generators = new LinkedList<>();

    /**
     * Returns instance of world generators controller class.
     * @return instance of world generators.
     */
    public synchronized static @NotNull WorldGenerators getInstance() {
        if (instance == null) {
            instance = new WorldGenerators();
        }
        return instance;
    }

    /**
     * Registers world generator, that can be used for world generation.
     * @param value world generator to register.
     */
    public void registerWorldGenerator(@NotNull WorldGenerator value) {
        WorldGenerator existing = getById(value.getID());
        if (existing != null) {
            sendDebug("[GENERATORS] Can't register world generator " + value.getName() + " (from " + value.getExtensionId() + "), "
                    + "because there's already registered world generator " + existing.getName() + " (from " + existing.getExtensionId() + ") "
                    + "with same ID: " + value.getID());
            return;
        }
        sendDebug("[GENERATORS] Registered world generator: " + value.getName() + " (from " + value.getExtensionId() + ")");
        generators.add(value);
    }

    /**
     * Registers world generators, that can be used for world generation.
     * @param values world generators to register.
     */
    public void registerWorldGenerator(@NotNull WorldGenerator... values) {
        for (WorldGenerator value : values) {
            registerWorldGenerator(value);
        }
    }

    /**
     * Unregisters world generator if list contains it.
     * @param value world generator to unregister.
     */
    @SuppressWarnings("unused")
    public void unregisterWorldGenerator(@NotNull WorldGenerator value) {
        generators.remove(value);
    }

    /**
     * Returns a copy of list that contains all registered world generators.
     * @return world generators list.
     */
    public @NotNull List<WorldGenerator> getWorldGenerators() {
        return new ArrayList<>(generators);
    }

    /**
     * Returns world generator from registry by specified id
     * if it exists, otherwise will return null.
     * @param id id to get world generator.
     * @return world generator - if exists, or null - not exists.
     */
    public @Nullable WorldGenerator getById(@NotNull String id) {
        for (WorldGenerator generator : generators) {
            if (generator.getID().equals(id)) {
                return generator;
            }
        }
        return null;
    }

}
