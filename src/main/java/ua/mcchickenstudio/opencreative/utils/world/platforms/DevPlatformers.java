package ua.mcchickenstudio.opencreative.utils.world.platforms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;

/**
 * <h1>DevPlatformers</h1>
 * This class represents a registry, that contains list of
 * all coding platforms generators, that can be used for
 * building and manipulating with platforms in dev planets.
 * <p>
 * To create own dev platformer, create a class that extends
 * {@link DevPlatformer}, and register it with {@link DevPlatformers#registerDevPlatformer(DevPlatformer)}.
 * To get instance of registry, use {@link DevPlatformers#getInstance()}.
 */
public final class DevPlatformers {

    private static DevPlatformers instance;
    private final List<DevPlatformer> platformers = new LinkedList<>();

    private DevPlatformers() {}

    public synchronized static DevPlatformers getInstance() {
        if (instance == null) {
            instance = new DevPlatformers();
            instance.registerDevPlatformer(new HorizontalPlatformer());
            instance.registerDevPlatformer(new VerticalPlatformer());
            instance.registerDevPlatformer(new LegacyPlatformer());
        }
        return instance;
    }

    /**
     * Registers coding platform generator.
     * @param platformer dev platformer to register.
     */
    public void registerDevPlatformer(@NotNull DevPlatformer platformer) {
        DevPlatformer existing = getById(platformer.getID());
        if (existing != null) {
            sendDebug("[PLATFORMERS] Can't register coding platforms generator " + platformer.getName() + ", "
                    + "because there's already registered coding platformer " + existing.getName() + " "
                    + "with same ID: " + platformer.getID());
            return;
        }
        sendDebug("[PLATFORMERS] Registered coding platform generator: " + platformer.getName() + " (from " + platformer.getExtensionId() + ")");
        platformers.add(platformer);
    }

    /**
     * Returns coding platform generator from registry
     * by specified id, if it exists, otherwise - null.
     * @param id id to get dev platformer.
     * @return dev platform - if found, otherwise - null.
     */
    public @Nullable DevPlatformer getById(@NotNull String id) {
        for (DevPlatformer platformer : platformers) {
            if (platformer.getID().equals(id)) {
                return platformer;
            }
        }
        return null;
    }

    /**
     * Returns a list of all registered platformers IDs.
     * @return platformers ID list.
     */
    public @NotNull List<String> getPlatformersIDs() {
        List<String> list = new ArrayList<>();
        for (DevPlatformer platformer : platformers) {
            list.add(platformer.getID());
        }
        return list;
    }

}
