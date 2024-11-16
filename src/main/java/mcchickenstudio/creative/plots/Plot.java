/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
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

package mcchickenstudio.creative.plots;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.CodingBlockParser;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.coding.variables.WorldVariables;
import mcchickenstudio.creative.events.plot.PlotConnectPlayerEvent;
import mcchickenstudio.creative.utils.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.BlockUtils.isOutOfBorders;
import static mcchickenstudio.creative.utils.ErrorUtils.*;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.*;
import static mcchickenstudio.creative.utils.PlayerUtils.*;

/**
 * <h1>Plot</h1>
 * This class represents a Plot, the individual place for players that
 * consists of two worlds: for building and for developing. It has owner,
 * world size, sharing, world mode, limits, flags, players data, variables
 * and states.
 *
 * <p>Plot has two file states: unloaded and loaded. Unloaded plots files
 * are stored in /unloadedWorlds/ directory, and loaded plots files are
 * stored in server's worlds storage directory.</p>
 * @author McChicken Studio
 * @since 1.0
 * @version 5.0
 */
public class Plot {

    private final int id;
    private String owner;
    private String ownerGroup;

    private final PlotInfo info;
    private final DevPlot devPlot;
    private final PlotFlags flags;
    private final PlotLimits limits;
    private final PlotTerritory territory;
    private final PlotPlayers worldPlayers;
    private final WorldVariables variables;
    private final PlotExperiments experiments;

    private Mode mode;
    private Sharing sharing;

    private boolean debug;
    private boolean corrupted;
    private boolean changingOwner;

    /**
     Loads a plot with world name.
     **/
    public Plot(int id) {

        this.id = id;
        devPlot = new DevPlot(this);
        info = new PlotInfo(this);

        loadInfo();

        worldPlayers = new PlotPlayers(this);
        limits = new PlotLimits(this);
        territory = new PlotTerritory(this);

        flags = new PlotFlags(this);
        variables = new WorldVariables(this);
        experiments = new PlotExperiments(this);

        PlotManager.getInstance().registerPlot(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                getInformation().updateIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public PlotInfo getInformation() {
        return info;
    }

    public PlotPlayers getWorldPlayers() {
        return worldPlayers;
    }

    public boolean isOwner(Player player) {
        return getOwner().equalsIgnoreCase(player.getName());
    }

    public boolean isOwner(String nickname) {
        return getOwner().equalsIgnoreCase(nickname);
    }

    /**
     * Changes plot's mode to Play or Build.
     * <p>In the Build mode players cannot get damaged, they only can look at builders which are creating a map.</p>
     * <p>In the Play mode code script will work, player damaging is enabled.</p>
     * @param mode Mode to set.
     */
    public void setMode(Mode mode) {
        if (this.mode == mode) return;
        this.mode = mode;
        setPlotConfigParameter(this,"mode",mode);
        territory.stopBukkitRunnables();
        territory.getWorld().getSpawnLocation().getChunk().load(true);
        if (mode == Mode.BUILD) {
            for (Player player : getPlayers()){
                if (!isEntityInDevPlot(player)) {
                    clearPlayer(player);
                    player.showTitle(Title.title(
                            Component.text(getLocaleMessage("world.build-mode.title")), Component.text(getLocaleMessage("world.build-mode.subtitle")),
                            Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(30), Duration.ofMillis(130))
                    ));
                    player.teleport(territory.getWorld().getSpawnLocation());
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,100,1.7f);
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
        } else {
            for (Player player : getPlayers()) {
                if (!isEntityInDevPlot(player)) {
                    clearPlayer(player);
                    player.teleport(territory.getWorld().getSpawnLocation());
                    if (worldPlayers.canDevelop(player)) {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                    } else {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.players"));
                    }
                } else {
                    player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                }
            }
            if (devPlot.isLoaded()) {
                new CodingBlockParser().parseCode(devPlot);
            } else {
                territory.getScript().loadCode();
            }
            EventRaiser.raiseWorldPlayEvent(this);
            for (Player player : getPlayers()) {
                if (PlotManager.getInstance().getDevPlot(player) == null) {
                    EventRaiser.raiseJoinEvent(player);
                }
            }
        }

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
        setPlotConfigParameter(this,"sharing",sharing);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getWorldName() {
        return "plot" + id;
    }

    public int getId() {
        return id;
    }

    public DevPlot getDevPlot() {
        return devPlot;
    }

    public boolean isChangingOwner() {
        return changingOwner;
    }

    public void setChangingOwner(boolean changingOwner) {
        this.changingOwner = changingOwner;
    }

    public PlotTerritory getTerritory() {
        return territory;
    }

    /**
     * Checks is loaded main world of plot.
     * @return true - if loaded, false - unloaded.
     */
    public boolean isLoaded() {
        return Bukkit.getWorld(getWorldName()) != null;
    }

    public enum Mode {
        PLAYING() {
            public void onPlayerConnect(Player player, Plot plot) {
                player.setGameMode(plot.getOwner().equalsIgnoreCase(player.getName()) ? GameMode.CREATIVE : GameMode.ADVENTURE);
            }
        }, BUILD() {
            public void onPlayerConnect(Player player, Plot plot) {
                if (plot.getWorldPlayers().canBuild(player)) {
                    player.setGameMode(GameMode.CREATIVE);
                    giveBuildPermissions(player);
                }
            }
        };

        public String getName() {
            return getLocaleMessage("world." + (this == PLAYING ? "play-mode" : "build-mode") + ".name",false);
        }

        public void onPlayerConnect(Player player, Plot plot) {}
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

    private void loadInfo() {
        FileConfiguration config = getPlotConfig(this);
        String owner = "Unknown owner";
        String ownerGroup = "default";
        Mode mode = Mode.BUILD;
        Sharing sharing = Sharing.PRIVATE;
        if (config != null) {
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
        } else {
            corrupted = true;
        }
        if (corrupted) {
            sendCriticalErrorMessage("Plot " + getWorldName() + " lost it's config file, please check plot files in /unloadedWorlds/" + getWorldName());
        }
        this.owner = owner;
        this.ownerGroup = ownerGroup;
        this.mode = mode;
        this.sharing = sharing;
    }

    public byte getFlagValue(PlotFlags.PlotFlag flag) {
        return (this.flags == null ? 1 : this.flags.getFlagValue(flag));
    }

    public void setFlagValue(PlotFlags.PlotFlag flag, byte value) {
        this.flags.setFlag(flag,value);
    }

    public Mode getMode() {
        return mode;
    }

    public int getOnline() {
        return this.getPlayers().size();
    }

    public int getUniques() {
        try {
            return (getPlayersFromPlotConfig(this, PlayersType.UNIQUE).size());
        } catch (Exception error) {
            return 0;
        }
    }

    @SuppressWarnings("all")
    public long getCreationTime() {
        try {
            return Long.parseLong(String.valueOf(getPlotConfig(this).get("creation-time")));
        } catch (Exception error) {
            return 0;
        }
    }

    @SuppressWarnings("all")
    public long getLastActivityTime() {
        try {
            return Long.parseLong(String.valueOf(getPlotConfig(this).get("last-activity-time")));
        } catch (Exception error) {
            return 0;
        }
    }

    public List<Player> getPlayers() {
        List<Player> playerList = new ArrayList<>();
        if (this.territory.getWorld() != null) {
            playerList.addAll(this.territory.getWorld().getPlayers());
            if (getDevPlot() != null && getDevPlot().world != null) {
                playerList.addAll(getDevPlot().world.getPlayers());
            }
        }
        return playerList;
    }

    public Audience getAudience() {
        Audience audience = Audience.empty();
        if (isLoaded()) {
            audience = Audience.audience(territory.getWorld());
            if (devPlot.isLoaded()) {
                audience = Audience.audience(territory.getWorld(),devPlot.world);
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
        if (getSharing() != Sharing.PUBLIC) {
            if (!isOwner(player)) {
                if (!(player.hasPermission("opencreative.world.private.bypass"))) {
                    player.sendMessage(getLocaleMessage("private-plot", player));
                    return;
                }
            }
        }
        if (!isOwner(player.getName())) {
            if (getSharing() != Sharing.PUBLIC && !player.hasPermission("opencreative.world.private.bypass")) {
                player.sendMessage(getLocaleMessage("private-plot", player));
                return;
            }
            if (worldPlayers.isBanned(player.getName()) && !player.hasPermission("opencreative.world.banned.bypass")) {
                player.sendMessage(getLocaleMessage("blacklisted-in-plot", player));
                return;
            }
        }
        getWorldPlayers().registerPlayer(player);
        player.showTitle(Title.title(
                Component.text(getLocaleMessage("world.connecting.title")), Component.text(getLocaleMessage("world.connecting.subtitle")),
                Title.Times.times(Duration.ofMillis(710), Duration.ofSeconds(30), Duration.ofMillis(130))
        ));
        player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM,100,1);
        boolean wasLoaded = isLoaded();
        if (!isLoaded()) {
            Main.getPlugin().getLogger().info("Loading " + this.getWorldName() + " and teleporting " + player.getName());
            territory.load();
        }
        clearPlayer(player);
        territory.getWorld().getSpawnLocation().getChunk().load(true);
        player.teleport(territory.getWorld().getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        mode.onPlayerConnect(player,this);
        getWorldPlayers().getPlotPlayer(player).load();
        clearPlayer(player);
        player.clearTitle();
        if (!getPlayersFromPlotConfig(this, PlayersType.UNIQUE).contains(player.getName())) {
            addPlayerToListInPlotConfig(this,player.getName(), PlayersType.UNIQUE);
        }
        if (this.isOwner(player.getName())) {
            ownerGroup = PlayerUtils.getGroup(player);
            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
            player.getInventory().setItem(8,worldSettingsItem);
            if (flags.getFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES) == 1) {
                player.sendMessage(getLocaleMessage("world.connecting.owner-help",player));
            }
            if (this.getDevPlot().isLoaded()) {
                new CodingBlockParser().parseCode(this.getDevPlot());
            }
        }
        if (!territory.isAutoSave() && worldPlayers.canBuild(player)) {
            player.sendMessage(getLocaleMessage("settings.autosave.warning"));
        }
        if (!wasLoaded) {
            territory.getScript().loadCode();
            EventRaiser.raiseWorldPlayEvent(this);
        }
        EventRaiser.raiseJoinEvent(player);
        new PlotConnectPlayerEvent(this,player).callEvent();
        new BukkitRunnable() {
            @Override
            public void run() {
                getInformation().updateIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public void connectToDevPlot(Player player) {
        player.showTitle(Title.title(
                Component.text(getLocaleMessage("world.dev-mode.connecting.title")), Component.text(getLocaleMessage("world.dev-mode.connecting.subtitle")),
                Title.Times.times(Duration.ofSeconds(15), Duration.ofSeconds(9999), Duration.ofSeconds(10))
        ));
        getDevPlot().loadDevPlotWorld();
        getDevPlot().world.getSpawnLocation().getChunk().load(true);
        Location lastLocation = this.getDevPlot().lastLocations.get(player);
        if (this.getDevPlot().world == null) {
            return;
        }
        if (lastLocation == null) {
            lastLocation = getDevPlot().world.getSpawnLocation();
        }
        player.teleport(lastLocation);
        getDevPlot().lastLocations.put(player,player.getLocation());
        clearPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false,false));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        for (Player developer : getDevPlot().world.getPlayers()) {
            WorldBorder border = Bukkit.createWorldBorder();
            border.setCenter(getDevPlot().world.getWorldBorder().getCenter());
            border.setSize(getDevPlot().world.getWorldBorder().getSize()*5);
            developer.setWorldBorder(border);
        }
        BukkitRunnable translation = new BukkitRunnable() {
            @Override
            public void run() {
                if (getDevPlot().world == null) return;
                getDevPlot().translateCodingBlocks(player);
                territory.removeBukkitRunnable(this);
            }
        };
        territory.addBukkitRunnable(translation);
        translation.runTaskLater(Main.getPlugin(),5L);
    }

    public void connectToDevPlot(Player player, double x, double y, double z) {
        connectToDevPlot(player);
        if (x > 0 && y > 0 && z > 0 && y < 30 && !isOutOfBorders(new Location(devPlot.world,x+1,y,z+2))) {
            Location location = new Location(this.getDevPlot().world, x+1,y,z+2,180,5);
            player.teleport(location);
            if (y == 1) spawnGlowingBlock(player,new Location(this.getDevPlot().world,x,y,z));
        }
    }

    public void setOwner(String owner) {
        this.owner = owner;
        FileUtils.setPlotConfigParameter(this,"owner",owner);
        new BukkitRunnable() {
            @Override
            public void run() {
                getInformation().updateIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
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

    public PlotLimits getLimits() {
        return limits;
    }

    public PlotExperiments getExperiments() {
        return experiments;
    }
}
