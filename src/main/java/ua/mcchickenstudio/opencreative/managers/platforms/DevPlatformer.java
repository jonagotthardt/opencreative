package ua.mcchickenstudio.opencreative.managers.platforms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.managers.Manager;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;

import java.util.List;

/**
 * <h1>DevPlatformer</h1>
 * This interface represents a manager, that manipulates and
 * controls coding platforms in developer planets. It can create
 * platform, return a list of existing platforms, set world's border.
 * <p>
 * Platform must be plain formed.
 */
public interface DevPlatformer extends Manager {

    /**
     * Sets world border for developer planet.
     * @param devPlanet dev planet to set border.
     */
    void setWorldBorder(@NotNull DevPlanet devPlanet);

    /**
     * Returns coding platform by location.
     * @param location location to get platform.
     * @return coding platform - if location contains coding platform, otherwise - null.
     */
    @Nullable DevPlatform getPlatformInLocation(@NotNull DevPlanet devPlanet, @NotNull Location location);

    /**
     * Returns the most far platform by X coordinate.
     * @param devPlanet developer planet to get platform.
     * @return farthest platform by X, or coding platform placed at (1,1).
     */
    @NotNull DevPlatform getFarPlatformByX(@NotNull DevPlanet devPlanet);

    /**
     * Returns the most far platform by Z coordinate.
     * @param devPlanet developer planet to get platform.
     * @return farthest platform by Z, or coding platform placed at (1,1).
     */
    @NotNull DevPlatform getFarPlatformByZ(@NotNull DevPlanet devPlanet);

    /**
     * Returns list of existing coding platforms, that
     * can be used to place coding blocks.
     * @param devPlanet developer planet to get platforms.
     * @return list of developer platforms.
     */
    @NotNull List<@NotNull DevPlatform> getPlatforms(@NotNull DevPlanet devPlanet);

    /**
     * Claims coding platform, if it doesn't exist yet.
     * @param devPlanet developer planet to claim platform.
     * @param platform coding platform to claim.
     * @return true - claimed coding platform, false - already built and exists.
     */
    boolean claimPlatform(@NotNull DevPlanet devPlanet, @NotNull DevPlatform platform);

    /**
     * Builds coding platform by its coordinates.
     * Coordinates are not from Minecraft location.
     * @param platform platform to create.
     * @param floorMaterial material of block, that will be used as floor.
     * @param eventMaterial material of block, that will be placed as cells for event blocks placement.
     * @param actionMaterial material of block, that will be placed as cells for action blocks placement.
     * @return true - created coding platform, false - cannot build it.
     */
    boolean buildPlatform(@NotNull DevPlatform platform, Material floorMaterial, Material eventMaterial, Material actionMaterial);

    /**
     * Returns location of top left corner of platform.
     * @param platform dev platform to get location.
     * @return begin location of platform.
     */
    @NotNull Location getPlatformBeginLocation(@NotNull DevPlatform platform);

    /**
     * Returns location of bottom right corner of platform.
     * @param platform dev platform to get location.
     * @return end location of platform.
     */
    @NotNull Location getPlatformEndLocation(@NotNull DevPlatform platform);


    /**
     * Returns next platform, that will be created by player.
     * @return next available coding platform.
     */
    @NotNull DevPlatform getNextAvailablePlatform(@NotNull DevPlanet planet);

    /**
     * Checks if this manager creates platforms
     * on Z coordinate instead of Y coordinate.
     */
    boolean isHorizontal();
}
