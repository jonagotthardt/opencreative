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

package ua.mcchickenstudio.opencreative.planets;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;

import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.JoinEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.GamePlayEvent;
import ua.mcchickenstudio.opencreative.coding.variables.WorldVariables;
import ua.mcchickenstudio.opencreative.events.planet.PlanetConnectPlayerEvent;
import ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld;
import ua.mcchickenstudio.opencreative.managers.stability.StabilityState;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.settings.groups.Group;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

/**
 * <h1>Planet</h1>
 * This class represents a Planet, the individual place for players that
 * consists of two worlds: for building and for developing. It has owner,
 * world size, sharing, world mode, limits, flags, players data, variables
 * and states.
 *
 * <p>Planet files are stored in ./planets/planetID folder.</p>
 * @author McChicken Studio
 * @since 1.0
 * @version 5.0
 */
public class Planet {

    private final int id;
    private String owner;
    private String ownerGroup;

    private final PlanetInfo info;
    private final DevPlanet devPlanet;
    private final PlanetLimits limits;
    private final PlanetTerritory territory;
    private final PlanetPlayers worldPlayers;
    private final WorldVariables variables;
    private final PlanetExperiments experiments;

    private long creationTime;
    private long lastActivityTime;

    private Mode mode;
    private Sharing sharing;

    private boolean debug;
    private boolean corrupted;
    private boolean changingOwner;

    /**
     Loads a planet with world name.
     **/
    public Planet(int id) {

        this.id = id;
        devPlanet = new DevPlanet(this);
        info = new PlanetInfo(this);

        loadInfo();

        worldPlayers = new PlanetPlayers(this);
        limits = new PlanetLimits(this);
        territory = new PlanetTerritory(this);

        variables = new WorldVariables(this);
        experiments = new PlanetExperiments(this);

        OpenCreative.getPlanetsManager().registerPlanet(this);

        info.updateIconAsync();
    }

    public PlanetInfo getInformation() {
        return info;
    }

    public PlanetPlayers getWorldPlayers() {
        return worldPlayers;
    }

    public boolean isOwner(Player player) {
        return getOwner().equalsIgnoreCase(player.getName());
    }

    public boolean isOwner(String nickname) {
        return getOwner().equalsIgnoreCase(nickname);
    }

    /**
     * Changes planet's mode to Play or Build.
     * <p>In the Build mode players cannot get damaged, they only can look at builders which are creating a map.</p>
     * <p>In the Play mode code script will work, player damaging is enabled.</p>
     * @param mode Mode to set.
     */
    public void setMode(Mode mode) {
        if (this.mode == mode) return;
        setPlanetConfigParameter(this,"mode",mode.name());
        if (!isLoaded()) {
            this.mode = mode;
            return;
        }
        try {
            territory.getWorld().getSpawnLocation().getChunk().load(true);
            if (mode == Mode.BUILD) {
                for (Player player : getPlayers()) {
                    if (!isEntityInDevPlanet(player)) {
                        new QuitEvent(player).callEvent();
                        player.showTitle(Title.title(
                                toComponent(getLocaleMessage("world.build-mode.title")), toComponent(getLocaleMessage("world.build-mode.subtitle")),
                                Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(2), Duration.ofMillis(130))
                        ));
                        clearPlayer(player);
                        player.teleport(territory.getWorld().getSpawnLocation());
                        Sounds.WORLD_MODE_BUILD.play(player);
                        territory.showBorders(player);
                        if (isOwner(player)) {
                            player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                        }
                        if (worldPlayers.canBuild(player)) {
                            player.setGameMode(GameMode.CREATIVE);
                            giveBuildPermissions(player);
                            player.sendMessage(getLocaleMessage("world.build-mode.message.owner"));
                            if (!territory.isAutoSave()) {
                                player.sendMessage(getLocaleMessage("settings.autosave.warning"));
                            }
                        } else {
                            player.sendMessage(getLocaleMessage("world.build-mode.message.players"));
                        }
                    } else {
                        player.sendMessage(getLocaleMessage("world.build-mode.message.players"));
                    }
                }
                territory.stopBukkitRunnables();
                HookUtils.clearEntitiesHook(territory.getWorld());
            } else {
                this.mode = mode;
                territory.stopBukkitRunnables();
                HookUtils.clearEntitiesHook(territory.getWorld());
                for (Player player : getPlayers()) {
                    if (!isEntityInDevPlanet(player)) {
                        clearPlayer(player);
                        player.clearTitle();
                        player.teleport(territory.getWorld().getSpawnLocation());
                        territory.showBorders(player);
                        if (worldPlayers.canDevelop(player)) {
                            player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                        } else {
                            player.sendMessage(getLocaleMessage("world.play-mode.message.players"));
                        }
                    } else {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                    }
                }
                if (devPlanet.isLoaded()) {
                    new CodingBlockParser(devPlanet).parseCode(devPlanet);
                } else {
                    territory.getScript().loadCode();
                }
                new GamePlayEvent(this).callEvent();
                for (Player player : getPlayers()) {
                    if (OpenCreative.getPlanetsManager().getDevPlanet(player) == null) {
                        new JoinEvent(player).callEvent();
                    }
                }
            }
        } catch (Exception error) {
            sendPlanetErrorMessage(this,"Failed to change mode to " + mode.name());
        }
        this.mode = mode;
    }

    public Sharing getSharing() {
        return sharing;
    }

    /**
     * Sharing is ability for players to connect to the world.
     * <p>Public - all players can connect to the world.</p>
     * <p>Private - only world owner can connect to the world.</p>
     * <p>Closed - no one can connect to the world, deleting mode.</p>
     * @param sharing Sharing mode.
     */
    public void setSharing(Sharing sharing) {
        if (this.sharing == sharing) return;
        this.sharing = sharing;
        setPlanetConfigParameter(this,"sharing",sharing.name());
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getWorldName() {
        return "./planets/planet" + id;
    }

    public int getId() {
        return id;
    }

    public DevPlanet getDevPlanet() {
        return devPlanet;
    }

    public boolean isChangingOwner() {
        return changingOwner;
    }

    public void setChangingOwner(boolean changingOwner) {
        this.changingOwner = changingOwner;
    }

    public PlanetTerritory getTerritory() {
        return territory;
    }

    /**
     * Checks is loaded main world of planet.
     * @return true - if loaded, false - unloaded.
     */
    public boolean isLoaded() {
        return Bukkit.getWorld(getWorldName()) != null;
    }

    /**
     * Returns world if planet is loaded, null - planet is unloaded.
     * @return world, or null.
     */
    public World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public enum Mode {
        PLAYING() {
            public void onPlayerConnect(Player player, Planet planet) {
                player.setGameMode(GameMode.ADVENTURE);
            }
        }, BUILD() {
            public void onPlayerConnect(Player player, Planet planet) {
                if (planet.getWorldPlayers().canBuild(player)) {
                    player.setGameMode(GameMode.CREATIVE);
                    giveBuildPermissions(player);
                }
            }
        };

        public String getName() {
            return getLocaleMessage("world." + (this == PLAYING ? "play-mode" : "build-mode") + ".name",false);
        }

        public void onPlayerConnect(Player player, Planet planet) {}
    }

    public enum Sharing {
        PUBLIC, PRIVATE, CLOSED;

        public String getName() {
            return getLocaleMessage("world.sharing." + (this == PUBLIC ? "public" : "private"),false);
        }
    }

    public enum PlayersType {

        UNIQUE("players.unique"),
        LIKED("players.liked"),
        DISLIKED("players.disliked"),
        WHITELISTED("players.whitelist"),
        BLACKLISTED("players.blacklist"),
        BUILDERS_TRUSTED("players.builders.trusted"),
        BUILDERS_NOT_TRUSTED("players.builders.not-trusted"),
        DEVELOPERS_TRUSTED("players.developers.trusted"),
        DEVELOPERS_NOT_TRUSTED("players.developers.not-trusted"),
        DEVELOPERS_GUESTS("players.developers.guests");

        private final String path;

        PlayersType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    public void loadInfo() {
        FileConfiguration config = getPlanetConfig(this);
        String owner = "Unknown owner";
        String ownerGroup = "default";
        Mode mode = Mode.BUILD;
        Sharing sharing = Sharing.PRIVATE;
        if (config.getString("owner") != null) {
            owner = config.getString("owner");
        } else {
            corrupted = true;
        }
        if (config.getString("owner-group") != null) {
            ownerGroup = config.getString("owner-group");
        }
        if (config.getString("mode") != null) {
            try {
                mode = Mode.valueOf(config.getString("mode"));
            } catch (Exception ignored) {}
        }
        if (config.getString("sharing") != null) {
            try {
                sharing = Sharing.valueOf(config.getString("sharing"));
            } catch (Exception ignored) {}
        }
        if (config.get("creation-time") != null) {
            try {
                creationTime = Long.parseLong(String.valueOf(config.get("creation-time")));
            } catch (Exception error) {
                creationTime = 1670573410000L;
            }
        }
        if (config.get("last-activity-time") != null) {
            try {
                lastActivityTime = Long.parseLong(String.valueOf(config.get("last-activity-time")));
            } catch (Exception error) {
                lastActivityTime = 1670573410000L;
            }
        }
        if (corrupted) {
            sendCriticalErrorMessage("Planet " + id + " lost it's config file, please check planet files in " + getWorldName());
        }
        this.owner = owner;
        this.ownerGroup = ownerGroup;
        this.mode = mode;
        this.sharing = sharing;
    }

    public byte getFlagValue(PlanetFlags.PlanetFlag flag) {
        return territory.getFlags().getFlagValue(flag);
    }

    public void setFlagValue(PlanetFlags.PlanetFlag flag, byte value) {
        territory.getFlags().setFlag(flag,value);
    }

    public Mode getMode() {
        return mode;
    }

    public int getOnline() {
        return this.getPlayers().size();
    }

    public long getCreationTime() {
        if (creationTime == 0) {
            return 1670573410000L;
        }
        return creationTime;
    }

    public long getLastActivityTime() {
        if (lastActivityTime == 0) {
            return 1670573410000L;
        }
        return lastActivityTime;
    }

    public List<Player> getPlayers() {
        List<Player> playerList = new ArrayList<>();
        if (!isLoaded()) return playerList;
        playerList.addAll(territory.getWorld().getPlayers());
        if (getDevPlanet().isLoaded()) {
            playerList.addAll(getDevPlanet().getWorld().getPlayers());
        }
        return playerList;
    }

    public Audience getAudience() {
        Audience audience = Audience.empty();
        if (isLoaded()) {
            audience = Audience.audience(territory.getWorld());
            if (devPlanet.isLoaded()) {
                audience = Audience.audience(territory.getWorld(), devPlanet.getWorld());
            }
        }
        return audience;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnerGroup() {
        return ownerGroup;
    }

    public void connectPlayer(Player player) {
        connectPlayer(player,false);
    }

    public void connectPlayer(Player player, boolean hidePlayer) {
        if (OpenCreative.getStability().getState() != StabilityState.FINE && !isLoaded()) {
            player.sendMessage(getLocaleMessage("creative.stability.cannot"));
            Sounds.PLAYER_FAIL.play(player);
            return;
        }
        if (!isOwner(player.getName())) {
            if (getSharing() != Sharing.PUBLIC && !player.hasPermission("opencreative.world.private.bypass") && !worldPlayers.isWhitelisted(player.getName())) {
                player.sendMessage(MessageUtils.getPlayerLocaleMessage("private-planet", player));
                return;
            }
            if (worldPlayers.isBanned(player.getName()) && !player.hasPermission("opencreative.world.banned.bypass")) {
                player.sendMessage(MessageUtils.getPlayerLocaleMessage("blacklisted-in-planet", player));
                return;
            }
        }
        player.showTitle(Title.title(
                toComponent(getLocaleMessage("world.connecting.title")), toComponent(getLocaleMessage("world.connecting.subtitle")),
                Title.Times.times(Duration.ofMillis(710), Duration.ofSeconds(30), Duration.ofMillis(130))
        ));
        Sounds.WORLD_CONNECTION.play(player);
        boolean wasLoaded = isLoaded();
        if (!isLoaded()) {
            OpenCreative.getPlugin().getLogger().info("Loading planet " + id + " and teleporting " + player.getName());
            territory.load();
        } else {
            OpenCreative.getPlugin().getLogger().info("Planet " + id + " is already loaded, teleporting " + player.getName());
        }
        player.teleportAsync(territory.getWorld().getSpawnLocation()).thenAccept(success -> {
            clearPlayer(player);
            if (success) {
                if (!hidePlayer && getFlagValue(PlanetFlags.PlanetFlag.JOIN_MESSAGES) == 1) {
                    for (Player onlinePlayer : getPlayers()) {
                        onlinePlayer.sendMessage(MessageUtils.getPlayerLocaleMessage("world.joined", player));
                    }
                }
                clearPlayer(player);
                Sounds.WORLD_CONNECTED.play(player);
                mode.onPlayerConnect(player,this);
                getWorldPlayers().getPlanetPlayer(player).load();
                player.clearTitle();
                territory.showBorders(player);
                if (!getPlayersFromPlanetList(this, PlayersType.UNIQUE).contains(player.getName())) {
                    addPlayerInPlanetList(this,player.getName(), PlayersType.UNIQUE);
                    info.setUniques(info.getUniques()+1);
                    if (this.isOwner(player)) {
                        player.showTitle(Title.title(
                                toComponent(MessageUtils.getPlayerLocaleMessage("creating-world.welcome-title",player)), toComponent(MessageUtils.getPlayerLocaleMessage("creating-world.welcome-subtitle",player)),
                                Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(9), Duration.ofSeconds(2))
                        ));
                        player.sendMessage(getLocaleMessage("creating-world.welcome"));
                        Sounds.WELCOME_TO_NEW_WORLD.play(player);
                        player.setGameMode(GameMode.CREATIVE);
                        ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                        player.getInventory().setItem(8,worldSettingsItem);
                    }
                } else {
                    if (isOwner(player) && getFlagValue(PlanetFlags.PlanetFlag.JOIN_MESSAGES) == 1) {
                        player.sendMessage(MessageUtils.getPlayerLocaleMessage("world.connecting.owner-help",player));
                    }
                }
                if (this.isOwner(player.getName())) {
                    ownerGroup = OpenCreative.getSettings().getGroups().getGroup(player).getName().toLowerCase();
                    ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                    player.getInventory().setItem(8,worldSettingsItem);
                    if (this.getDevPlanet().isLoaded() && OpenCreative.getStability().isFine()) {
                        new CodingBlockParser(devPlanet).parseCode(this.getDevPlanet());
                    }
                }
                if (!territory.isAutoSave() && worldPlayers.canBuild(player)) {
                    player.sendMessage(getLocaleMessage("settings.autosave.warning"));
                }
                if (!wasLoaded) {
                    territory.getScript().loadCode();
                    new GamePlayEvent(this).callEvent();
                }
                if (mode == Mode.PLAYING && worldPlayers.canDevelop(player)) {
                    givePlayPermissions(player);
                } else if (mode == Mode.BUILD && worldPlayers.canBuild(player)) {
                    giveBuildPermissions(player);
                }
                if (!hidePlayer) {
                    new JoinEvent(player).callEvent();
                } else {
                    player.setGameMode(GameMode.SPECTATOR);
                    ChangedWorld.addPlayerWithLocation(player);
                    for (Player onlinePlayer : getPlayers()) {
                        onlinePlayer.hidePlayer(OpenCreative.getPlugin(),player);
                    }
                }
                new PlanetConnectPlayerEvent(this,player).callEvent();
                info.updateIconAsync();
            } else {
                sendPlayerErrorMessage(player,"Can't join planet. World is unloaded.");
            }
        });

    }

    public void connectToDevPlanet(Player player) {
        connectToDevPlanet(player,false);
    }

    public void connectToDevPlanet(Player player, boolean hidePlayer) {
        player.showTitle(Title.title(
                toComponent(getLocaleMessage("world.dev-mode.connecting.title")), toComponent(getLocaleMessage("world.dev-mode.connecting.subtitle")),
                Title.Times.times(Duration.ofSeconds(15), Duration.ofSeconds(9999), Duration.ofSeconds(10))
        ));
        getDevPlanet().loadDevPlanetWorld();
        getDevPlanet().getWorld().getSpawnLocation().getChunk().load(true);
        Location lastLocation = this.getDevPlanet().getLastLocations().get(player);
        if (!this.getDevPlanet().isLoaded()) {
            return;
        }
        if (lastLocation == null || !devPlanet.isSaveLocation()) {
            lastLocation = getDevPlanet().getWorld().getSpawnLocation();
        }
        player.teleportAsync(lastLocation).thenAccept(success -> {
            if (success) {
                if (!hidePlayer) {
                    for (Player onlinePlayer : player.getWorld().getPlayers()) {
                        onlinePlayer.sendMessage(MessageUtils.getPlayerLocaleMessage("world.dev-mode.joined", player));
                    }
                } else {
                    player.setGameMode(GameMode.SPECTATOR);
                    for (Player onlinePlayer : player.getWorld().getPlayers()) {
                        onlinePlayer.hidePlayer(OpenCreative.getPlugin(),player);
                    }
                }
                if (devPlanet.isSaveLocation()) devPlanet.getLastLocations().put(player,player.getLocation());
                if (devPlanet.isNightVision()) player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false,false));
                Sounds.DEV_CONNECTED.play(player);
                Sounds.WORLD_MODE_DEV.play(player);
                devPlanet.displayWorldBorders();
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.title")), toComponent(getLocaleMessage("world.dev-mode.subtitle")),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(2), Duration.ofMillis(750))
                ));
                BukkitRunnable translation = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (getDevPlanet().getWorld() == null) return;
                        getDevPlanet().translateCodingBlocks(player);
                        territory.removeBukkitRunnable(this);
                    }
                };
                territory.addBukkitRunnable(translation);
                translation.runTaskLater(OpenCreative.getPlugin(),5L);
            }
        });
    }

    public void connectToDevPlanet(Player player, double x, double y, double z) {
        connectToDevPlanet(player);
        if (x > 0 && y > 0 && z > 0 && y < 30 && !isOutOfBorders(new Location(devPlanet.getWorld(),x+1,y,z+2))) {
            Location location = new Location(this.getDevPlanet().getWorld(), x+1,y,z+2,180,5);
            player.teleportAsync(location).thenAccept(success -> {
                if (success) {
                    spawnGlowingBlock(player,new Location(this.getDevPlanet().getWorld(),x+0.5,y,z+0.5));
                }
            });
        }
    }

    public void setLastActivityTime(long activityTime) {
        this.lastActivityTime = activityTime;
        FileUtils.setPlanetConfigParameter(this,"last-activity-time",activityTime);
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        FileUtils.setPlanetConfigParameter(this,"creation-time",creationTime);
        info.updateIconAsync();
    }

    public void setOwner(String owner) {
        this.owner = owner;
        FileUtils.setPlanetConfigParameter(this,"owner",owner);
        FileUtils.setPlanetConfigParameter(this,"owner-uuid",Bukkit.getOfflinePlayer(owner).getUniqueId().toString());
        info.updateIconAsync();
    }

    public boolean isDebug() {
        return debug;
    }

    public WorldVariables getVariables() {
        return variables;
    }

    public boolean isCorrupted() {
        return corrupted;
    }

    public PlanetLimits getLimits() {
        return limits;
    }

    public PlanetExperiments getExperiments() {
        return experiments;
    }

    public Group getGroup() {
        return OpenCreative.getSettings().getGroups().getGroup(ownerGroup);
    }
}
