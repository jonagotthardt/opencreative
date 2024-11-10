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
import mcchickenstudio.creative.coding.CodeScript;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.coding.variables.WorldVariables;
import mcchickenstudio.creative.utils.*;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcchickenstudio.creative.utils.ErrorUtils.*;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.*;
import static mcchickenstudio.creative.utils.PlayerUtils.*;

/**
 * <h1>Plot</h1>
 * This class represents a world, that has owner, ID,
 * information, players, script, limits and developer's world.
 * @since 1.0
 * @version 5.0
 */
public class Plot {

    private final int id;
    private final PlotInfo info;
    private final DevPlot devPlot;
    private final PlotFlags flags;
    private final PlotLimits limits;
    private final PlotPlayers worldPlayers;
    private final WorldVariables variables;
    private final int worldSize;

    private String owner;
    private String ownerGroup;

    private World world;
    private World.Environment environment;
    private boolean isLoaded;

    private int plotReputation;
    private Mode mode;
    private Sharing plotSharing;

    private final Map<String, BossBar> bossBars = new HashMap<>();
    private final Map<String, Scoreboard> scoreboards = new HashMap<>();
    private final List<BukkitRunnable> runningBukkitRunnables = new ArrayList<>();

    private boolean debug;
    private boolean corrupted;
    private boolean changingOwner;

    private CodeScript script;

    /**
     Loads a plot with world name.
     **/
    public Plot(int id) {

        this.id = id;
        devPlot = new DevPlot(this);
        loadInfo();

        info = new PlotInfo(this);
        worldPlayers = new PlotPlayers(this);
        limits = new PlotLimits(this);

        flags = new PlotFlags(this);
        variables = new WorldVariables(this);
        script = new CodeScript(this,getPlotScriptFile(this));

        worldSize = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_SIZE);

        PlotManager.getInstance().registerPlot(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                info.updateIcon();
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

    public int getPlotReputation() {
        return plotReputation;
    }

    public void setPlotReputation(int plotReputation) {
        this.plotReputation = plotReputation;
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
        stopBukkitRunnables();
        world.getSpawnLocation().getChunk().load(true);
        if (mode == Mode.BUILD) {
            for (Player player : getPlayers()){
                if (!isEntityInDevPlot(player)) {
                    clearPlayer(player);
                    player.sendTitle(getLocaleMessage("world.build-mode.title"),getLocaleMessage("world.build-mode.subtitle"));
                    player.teleport(world.getSpawnLocation());
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,100,1.7f);
                    if (worldPlayers.canBuild(player)) {
                        player.setGameMode(GameMode.CREATIVE);
                        giveBuildPermissions(player);
                        player.sendMessage(getLocaleMessage("world.build-mode.message.owner"));
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
                    player.teleport(world.getSpawnLocation());
                    if (worldPlayers.canDevelop(player)) {
                        player.sendMessage(getLocaleMessage("world.build-mode.message.owner"));
                    } else {
                        player.sendMessage(getLocaleMessage("world.build-mode.message.players"));
                    }
                } else {
                    player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                }
            }
            if (devPlot.isLoaded()) {
                new CodingBlockParser().parseCode(devPlot);
            } else {
                script.loadCode();
            }
            EventRaiser.raiseWorldPlayEvent(this);
            for (Player player : getPlayers()) {
                if (PlotManager.getInstance().getDevPlot(player) == null) {
                    EventRaiser.raiseJoinEvent(player);
                }
            }
        }

    }

    public World generateWorld(WorldUtils.WorldGenerator worldGenerator, World.Environment environment, long seed, boolean generateStructures) {
        WorldCreator worldCreator = new WorldCreator(getWorldName()).generateStructures(false);
        worldCreator.type(WorldType.FLAT);
        worldCreator.environment(environment);
        worldCreator.generateStructures(generateStructures);
        worldCreator.seed(seed);

        if (worldGenerator == WorldUtils.WorldGenerator.EMPTY) {
            worldCreator.generator(new EmptyChunkGenerator());
        } else if (worldGenerator == WorldUtils.WorldGenerator.WATER) {
            worldCreator.generator(new WaterChunkGenerator());
        } else if (worldGenerator == WorldUtils.WorldGenerator.SURVIVAL) {
            worldCreator.type(WorldType.NORMAL);
        } else if (worldGenerator == WorldUtils.WorldGenerator.LARGE_BIOMES) {
            worldCreator.type(WorldType.LARGE_BIOMES);
        }

        worldCreator.keepSpawnLoaded(TriState.FALSE);
        World world = Bukkit.createWorld(worldCreator);

        if (world != null) {
            world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 1);
            world.getWorldBorder().setSize(worldSize);

            world.setGameRule(GameRule.DO_MOB_LOOT, true);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.KEEP_INVENTORY, false);
            world.setGameRule(GameRule.MOB_GRIEFING, true);
            world.setGameRule(GameRule.NATURAL_REGENERATION, true);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, true);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

            world.setTime(0);

            if (worldGenerator == WorldUtils.WorldGenerator.EMPTY) {
                world.setSpawnLocation(0, 5, 0);

                for (int x = 1; x >= -1; x--) {
                    for (int z = 1; z >= -1; z--) {
                        world.getBlockAt(x, 4, z).setType(Material.STONE);
                    }
                }

            } else if (worldGenerator == WorldUtils.WorldGenerator.WATER) {
                world.setSpawnLocation(0, 8, 0);
            }

            this.world = world;
            isLoaded = true;

            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.PLAYER) entity.remove();
            }

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof EnderDragon dragon) {
                            dragon.setHealth(0);
                        }
                    }
                }
            };
            runnable.runTaskLater(Main.getPlugin(),10L);

            return world;
        }
        return null;
    }

    public Sharing getPlotSharing() {
        return plotSharing;
    }

    public void setPlotSharing(Sharing sharing) {
        this.plotSharing = sharing;
        setPlotConfigParameter(this,"sharing",sharing);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public CodeScript getScript() {
        return script;
    }

    public void setScript(CodeScript script) {
        this.script = script;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public String getWorldName() {
        return "plot" + id;
    }

    public int getId() {
        return id;
    }

    public int getWorldSize() {
        return worldSize;
    }

    public DevPlot getDevPlot() {
        return devPlot;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public boolean isChangingOwner() {
        return changingOwner;
    }

    public void setChangingOwner(boolean changingOwner) {
        this.changingOwner = changingOwner;
    }

    public enum Mode {
        PLAYING() {
            public void onPlayerConnect(Player player, Plot plot) {
                player.setGameMode(GameMode.ADVENTURE);
                player.setGameMode(plot.getOwner().equalsIgnoreCase(player.getName()) ? GameMode.CREATIVE : GameMode.ADVENTURE);
                plot.getScript().loadCode();
            }
        }, BUILD() {
            public void onPlayerConnect(Player player, Plot plot) {
                player.setGameMode(GameMode.ADVENTURE);
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
        World.Environment environment = World.Environment.NORMAL;
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
            if (config.getString("environment") != null) {
                try {
                    environment = World.Environment.valueOf(config.getString("environment"));
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
        this.plotSharing = sharing;
        this.plotReputation = getPlayersFromPlotConfig(this,PlayersType.LIKED).size()-getPlayersFromPlotConfig(this,PlayersType.DISLIKED).size();
        this.environment = environment;
    }

    public byte getFlagValue(PlotFlags.PlotFlag flag) {
        return (this.flags == null ? 1 : this.flags.getFlagValue(flag));
    }

    public void setFlagValue(PlotFlags.PlotFlag flag, byte value) {
        this.flags.setFlag(flag,value);
    }

    /**
     Creates a world for plot.
     **/
    public void create(Plot plot, WorldUtils.WorldGenerator generator, World.Environment environment, long seed, boolean generateStructures) {
        Player player = Bukkit.getPlayer(plot.getOwner());
        player.sendTitle(getLocaleMessage("creating-world.title"),getLocaleMessage("creating-world.subtitle"),10,300,40);
        Main.getPlugin().getLogger().info("Creating new " + getWorldName() + " by " + player.getName() + "...");
        if (!WorldUtils.generateWorld(plot,player,getWorldName(),generator,environment,seed,generateStructures)) {
            player.clearTitle();
            sendPlayerErrorMessage(player,"§cПроизошла ошибка при создании мира... \n§cОбратитесь к администрации!");
        }
    }

    public Mode getMode() {
        return mode;
    }

    public int getOnline() {
        return this.getPlayers().size();
    }

    public int getReputation() {
        try {
            return (getPlayersFromPlotConfig(this, PlayersType.LIKED).size() - getPlayersFromPlotConfig(this, PlayersType.DISLIKED).size());
        } catch (Exception error) {
            return 0;
        }
    }

    public int getUniques() {
        try {
            return (getPlayersFromPlotConfig(this, PlayersType.UNIQUE).size());
        } catch (Exception error) {
            return 0;
        }
    }

    public long getCreationTime() {
        try {
            return Long.parseLong(String.valueOf(getPlotConfig(this).get("creation-time")));
        } catch (Exception error) {
            return 0;
        }
    }

    public long getLastActivityTime() {
        try {
            return Long.parseLong(String.valueOf(getPlotConfig(this).get("last-activity-time")));
        } catch (Exception error) {
            return 0;
        }
    }

    public List<Player> getPlayers() {
        List<Player> playerList = new ArrayList<>();
        if (this.getWorld() != null) {
            playerList.addAll(this.getWorld().getPlayers());
            if (getDevPlot() != null && getDevPlot().world != null) {
                playerList.addAll(getDevPlot().world.getPlayers());
            }
        }
        return playerList;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnerGroup() {
        return ownerGroup;
    }

    public void connectPlayer(Player player) {
        if (!(this.getPlotSharing() == Sharing.PUBLIC)) {
            if (!this.getOwner().equalsIgnoreCase(player.getName())) {
                if (!(player.hasPermission("creative.private.bypass"))) {
                    player.sendMessage(getLocaleMessage("private-plot", player));
                    return;
                }
            }
        }
        if (!this.isOwner(player.getName()) && FileUtils.getPlayersFromPlotConfig(this,PlayersType.BLACKLISTED).contains(player.getName())) {
            player.sendMessage(getLocaleMessage("blacklisted-in-plot", player));
            return;
        }
        getWorldPlayers().registerPlayer(player);
        player.sendTitle(getLocaleMessage("world.connecting.title"),getLocaleMessage("world.connecting.subtitle"),15,9999,15);
        player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM,100,1);
        boolean wasLoaded = isLoaded();
        if (!isLoaded()) {
            Main.getPlugin().getLogger().info("Loading " + this.getWorldName() + " and teleporting " + player.getName());
            PlotManager.getInstance().loadPlot(this);
        }
        clearPlayer(player);
        getWorld().getSpawnLocation().getChunk().load(true);
        player.teleport(this.getWorld().getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        mode.onPlayerConnect(player,this);
        getWorldPlayers().getPlotPlayer(player).load();
        clearPlayer(player);
        player.sendTitle("","");
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
        if (!wasLoaded) {
            EventRaiser.raiseWorldPlayEvent(this);
        }
        EventRaiser.raiseJoinEvent(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                info.updateIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public void connectToDevPlot(Player player) {
        player.sendTitle(getLocaleMessage("world.dev-mode.connecting.title"),getLocaleMessage("world.dev-mode.connecting.subtitle"),15,9999,15);
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
                removeBukkitRunnable(this);
            }
        };
        addBukkitRunnable(translation);
        translation.runTaskLater(Main.getPlugin(),5L);
    }

    public void connectToDevPlot(Player player, double x, double y, double z) {
        connectToDevPlot(player);
        if (x > 0 && y > 0 && z > 0 && x < 99 && y < 99 && z < 99) {
            Location location = new Location(this.getDevPlot().world, x+1,y,z+2,180,5);
            player.teleport(location);
            spawnGlowingBlock(player,new Location(this.getDevPlot().world,x,y,z));
        }
    }

    public void setOwner(String owner) {
        this.owner = owner;
        FileUtils.setPlotConfigParameter(this,"owner",owner);
        new BukkitRunnable() {
            @Override
            public void run() {
                info.updateIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public boolean getDebug() {
        return debug;
    }

    public void addBukkitRunnable(BukkitRunnable runnable) {
        runningBukkitRunnables.add(runnable);
    }

    public void removeBukkitRunnable(BukkitRunnable runnable) {
        runningBukkitRunnables.remove(runnable);
    }

    public void stopBukkitRunnables() {
        for (BukkitRunnable runnable : runningBukkitRunnables) {
            try {
                runnable.cancel();
            } catch (IllegalStateException ignored) {}
        }
        runningBukkitRunnables.clear();
    }


    public WorldVariables getVariables() {
        return variables;
    }

    public Map<String, Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public Map<String, BossBar> getBossBars() {
        return bossBars;
    }


    public boolean isCorrupted() {
        return corrupted;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public PlotLimits getLimits() {
        return limits;
    }
}
