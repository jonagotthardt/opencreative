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
import org.bukkit.*;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import mcchickenstudio.creative.utils.FileUtils;
import mcchickenstudio.creative.utils.PlayerUtils;
import mcchickenstudio.creative.utils.WorldUtils;
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
import static mcchickenstudio.creative.utils.WorldUtils.generateWorld;

/**
 * <h1>Plot</h1>
 * This class represents a world, that has owner, ID,
 * information, players, script, limits and developer's world.
 * @since 1.0
 * @version 5.0
 */
public class Plot {

    /**
     * Some fields will be separated into different
     * classes in next updates.
     */

    public World world;
    public String worldName;
    public String worldID;
    private World.Environment environment;

    private final PlotInfo plotInformation;
    private final PlotPlayers worldPlayers;

    private String owner;
    private String ownerGroup;

    public boolean isLoaded;
    public DevPlot devPlot;

    private int plotReputation;
    private Mode plotMode;
    private Sharing plotSharing;

    public final int worldSize;
    public int lastModifiedBlocksAmount;
    public int lastRedstoneOperationsAmount;
    public boolean currentlyTransferringOwnership;

    private final int entitiesLimit;
    private final int codeOperationsLimit;
    private final int redstoneOperationsLimit;
    private final int modifyingBlocksLimit;
    private final int scoreboardsLimit;
    private final int bossBarsLimit;
    private final int openingInventoriesLimit;
    private final int variablesAmountLimit;

    private final Map<String, BossBar> bossBars = new HashMap<>();
    private final Map<String, Scoreboard> scoreboards = new HashMap<>();
    private final List<BukkitRunnable> runningBukkitRunnables = new ArrayList<>();

    private final WorldVariables worldVariables;
    private boolean debug = false;
    private final PlotFlags plotFlags;

    private boolean isCorrupted = false;
    private CodeScript script;

    /**
     Creates a new plot for specified player with specified generator.
     **/
    public Plot(Player player, WorldUtils.WorldGenerator generator, World.Environment environment, long seed, boolean generateStructures) {

        player.closeInventory();
        owner = (player.getName());
        ownerGroup = getGroup(player);

        plotMode = (Mode.BUILD);
        plotSharing = (Sharing.PUBLIC);
        plotReputation = 0;

        lastModifiedBlocksAmount = 0;
        lastRedstoneOperationsAmount = 0;
        redstoneOperationsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_REDSTONE_OPERATIONS_LIMIT);
        entitiesLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_ENTITIES_LIMIT);
        codeOperationsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_CODE_OPERATIONS_LIMIT);
        openingInventoriesLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_OPENING_INVENTORIES_LIMIT);
        variablesAmountLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_VARIABLES_LIMIT);
        modifyingBlocksLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerLimit.WORLD_MODIFYING_BLOCKS_LIMIT);
        scoreboardsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerLimit.WORLD_SCOREBOARDS_LIMIT);
        bossBarsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerLimit.WORLD_BOSSBARS_LIMIT);
        worldSize = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_SIZE);
        currentlyTransferringOwnership = false;

        PlotManager.getInstance().registerPlot(this);

        create(this,generator,environment,seed,generateStructures);
        this.environment = environment;

        worldPlayers = new PlotPlayers(this);
        plotInformation = new PlotInfo(this);
        plotFlags = new PlotFlags(this);

        worldPlayers.registerPlayer(player);

        devPlot = new DevPlot(this);
        script = new CodeScript(this,getPlotScriptFile(this));
        worldVariables = new WorldVariables(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                plotInformation.updateIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());

    }

    /**
     Loads a plot with world name.
     **/
    public Plot(String fileName) {

        worldName = fileName;
        worldID = fileName.replace("plot","");
        isLoaded = false;
        currentlyTransferringOwnership = false;

        loadInfo();

        plotInformation = new PlotInfo(this);
        worldPlayers = new PlotPlayers(this);

        plotFlags = new PlotFlags(this);
        worldVariables = new WorldVariables(this);
        devPlot = new DevPlot(this);
        script = new CodeScript(this,getPlotScriptFile(this));

        lastModifiedBlocksAmount = 0;
        lastRedstoneOperationsAmount = 0;
        redstoneOperationsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_REDSTONE_OPERATIONS_LIMIT);
        entitiesLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_ENTITIES_LIMIT);
        codeOperationsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_CODE_OPERATIONS_LIMIT);
        openingInventoriesLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_OPENING_INVENTORIES_LIMIT);
        variablesAmountLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_VARIABLES_LIMIT);
        modifyingBlocksLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerLimit.WORLD_MODIFYING_BLOCKS_LIMIT);
        scoreboardsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerLimit.WORLD_SCOREBOARDS_LIMIT);
        bossBarsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerLimit.WORLD_BOSSBARS_LIMIT);
        worldSize = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_SIZE);

        PlotManager.getInstance().registerPlot(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                plotInformation.updateIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public PlotInfo getInformation() {
        return plotInformation;
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

    public void setPlotMode(Mode mode) {
        this.plotMode = mode;
        stopBukkitRunnables();
        setPlotConfigParameter(this,"mode",mode);
    }

    public Sharing getPlotSharing() {
        return plotSharing;
    }

    public void setPlotSharing(Sharing sharing) {
        this.plotSharing = sharing;
        setPlotConfigParameter(this,"sharing",sharing);
    }

    public void setOwnerGroup(String group) {
        this.ownerGroup = group;
        setPlotConfigParameter(this,"owner-group",group);
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
                if (plot.worldPlayers.canBuild(player)) {
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

    public enum Category {
        SANDBOX(getLocaleMessage("world.categories.sandbox")),
        ADVENTURE(getLocaleMessage("world.categories.adventure")),
        STRATEGY(getLocaleMessage("world.categories.strategy")),
        ARCADE(getLocaleMessage("world.categories.arcade")),
        ROLEPLAY(getLocaleMessage("world.categories.roleplay")),
        STORY(getLocaleMessage("world.categories.story")),
        SIMULATOR(getLocaleMessage("world.categories.simulator")),
        EXPERIMENT(getLocaleMessage("world.categories.experiment"));

        private final String name;

        Category(String localeMessage) {
            this.name = localeMessage;
        }

        public String getName() {
            return name;
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
        Category category = Category.SANDBOX;
        Sharing sharing = Sharing.PRIVATE;
        World.Environment environment = World.Environment.NORMAL;
        if (config != null) {
            if (config.getString("owner") != null) {
                owner = config.getString("owner");
            } else {
                isCorrupted = true;
            }
            if (config.getString("owner-group") != null) {
                ownerGroup = config.getString("owner-group");
            }
            if (config.getString("mode") != null) {
                try {
                    mode = Mode.valueOf(config.getString("mode"));
                } catch (Exception error) {
                    mode = Mode.BUILD;
                }
            }
            if (config.getString("environment") != null) {
                try {
                    environment = World.Environment.valueOf(config.getString("environment"));
                } catch (Exception error) {
                    environment = World.Environment.NORMAL;
                }
            }
            if (config.getString("sharing") != null) {
                try {
                    sharing = Sharing.valueOf(config.getString("sharing"));
                } catch (Exception error) {
                    sharing = Sharing.PRIVATE;
                }
            }
        } else {
            isCorrupted = true;
        }
        if (isCorrupted) {
            sendCriticalErrorMessage("Plot " + worldName + " lost it's config file, please check plot files in /unloadedWorlds/" + worldName);
        }
        this.owner = owner;
        this.ownerGroup = ownerGroup;
        this.plotMode = mode;
        this.plotSharing = sharing;
        this.plotReputation = getPlayersFromPlotConfig(this,PlayersType.LIKED).size()-getPlayersFromPlotConfig(this,PlayersType.DISLIKED).size();
        this.environment = environment;
    }

    public byte getFlagValue(PlotFlags.PlotFlag flag) {
        return (this.plotFlags == null ? 1 : this.plotFlags.getFlagValue(flag));
    }

    public void setFlagValue(PlotFlags.PlotFlag flag, byte value) {
        this.plotFlags.setFlag(flag,value);
    }

    /**
     Creates a world for plot.
     **/
    public void create(Plot plot, WorldUtils.WorldGenerator generator, World.Environment environment, long seed, boolean generateStructures) {
        Player player = Bukkit.getPlayer(plot.getOwner());
        String worldName = "plot" + WorldUtils.generateWorldID();
        player.sendTitle(getLocaleMessage("creating-world.title"),getLocaleMessage("creating-world.subtitle"),10,300,40);
        Main.getPlugin().getLogger().info("Creating new " + worldName + " by " + player.getName() + "...");
        if (!generateWorld(plot,player,worldName,generator,environment,seed,generateStructures)) {
            player.clearTitle();
            sendPlayerErrorMessage(player,"§cПроизошла ошибка при создании мира... \n§cОбратитесь к администрации!");
        }
    }

    public Mode getPlotMode() {
        return plotMode;
    }

    public int getOnline() {
        List<Player> playersList = this.getPlayers();
        return playersList.size();
    }

    public int getReputation() {
        try {
            return (getPlayersFromPlotConfig(this, PlayersType.LIKED).size() - getPlayersFromPlotConfig(this, PlayersType.DISLIKED).size());
        } catch (Exception error) {
            return 0;
        }
    }

    public String getBuilders() {
        return String.join(", ",getWorldPlayers().getAllBuilders());
    }

    public String getDevelopers() {
        return String.join(", ",getWorldPlayers().getAllDevelopers());
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
        if (this.world != null) {
            playerList.addAll(this.world.getPlayers());
            if (devPlot != null && devPlot.world != null) {
                playerList.addAll(devPlot.world.getPlayers());
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

    public void teleportPlayer(Player player) {
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
        worldPlayers.registerPlayer(player);
        player.sendTitle(getLocaleMessage("world.connecting.title"),getLocaleMessage("world.connecting.subtitle"),15,9999,15);
        player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM,100,1);
        boolean wasLoaded = isLoaded;
        if (!isLoaded) {
            Main.getPlugin().getLogger().info("Loading " + this.worldName + " and teleporting " + player.getName());
            PlotManager.getInstance().loadPlot(this);
        }
        clearPlayer(player);
        world.getSpawnLocation().getChunk().load(true);
        player.teleport(this.world.getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        plotMode.onPlayerConnect(player,this);
        worldPlayers.getPlotPlayer(player).load();
        clearPlayer(player);
        player.sendTitle("","");
        if (!getPlayersFromPlotConfig(this, PlayersType.UNIQUE).contains(player.getName())) {
            addPlayerToListInPlotConfig(this,player.getName(), PlayersType.UNIQUE);
        }
        if (this.isOwner(player.getName())) {
            this.setOwnerGroup(PlayerUtils.getGroup(player));
            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
            player.getInventory().setItem(8,worldSettingsItem);
            if (plotFlags.getFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES) == 1) {
                player.sendMessage(getLocaleMessage("world.connecting.owner-help",player));
            }
            if (this.devPlot.isLoaded()) {
                new CodingBlockParser().parseCode(this.devPlot);
            }
        }
        if (!wasLoaded) {
            EventRaiser.raiseWorldPlayEvent(this);
        }
        EventRaiser.raiseJoinEvent(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                plotInformation.updateIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public void connectToDevPlot(Player player) {
        player.sendTitle(getLocaleMessage("world.dev-mode.connecting.title"),getLocaleMessage("world.dev-mode.connecting.subtitle"),15,9999,15);
        devPlot.loadDevPlotWorld();
        devPlot.world.getSpawnLocation().getChunk().load(true);
        Location lastLocation = this.devPlot.lastLocations.get(player);
        if (this.devPlot.world == null) {
            player.sendMessage(ChatColor.RED + " Failed to teleport to developer's environment.");
            return;
        }
        if (lastLocation == null) {
            lastLocation = devPlot.world.getSpawnLocation();
        }
        player.teleport(lastLocation);
        devPlot.lastLocations.put(player,player.getLocation());
        clearPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false,false));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        for (Player developer : devPlot.world.getPlayers()) {
            WorldBorder border = Bukkit.createWorldBorder();
            border.setCenter(devPlot.world.getWorldBorder().getCenter());
            border.setSize(devPlot.world.getWorldBorder().getSize()*5);
            developer.setWorldBorder(border);
        }
        BukkitRunnable translation = new BukkitRunnable() {
            @Override
            public void run() {
                if (devPlot.world == null) return;
                devPlot.translateCodingBlocks(player);
                removeBukkitRunnable(this);
            }
        };
        addBukkitRunnable(translation);
        translation.runTaskLater(Main.getPlugin(),5L);
    }

    public void connectToDevPlot(Player player, double x, double y, double z) {
        connectToDevPlot(player);
        if (x > 0 && y > 0 && z > 0 && x < 99 && y < 99 && z < 99) {
            Location location = new Location(this.devPlot.world, x+1,y,z+2,180,5);
            player.teleport(location);
            spawnGlowingBlock(player,new Location(this.devPlot.world,x,y,z));
        }
    }

    public void setOwner(String owner) {
        this.owner = owner;
        FileUtils.setPlotConfigParameter(this,"owner",owner);
        new BukkitRunnable() {
            @Override
            public void run() {
                plotInformation.updateIcon();
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

    public int getVariablesAmountLimit() {
        return variablesAmountLimit + (getPlayers().size() * PlayerUtils.getPlayerModifierValue(ownerGroup,PlayerLimit.WORLD_VARIABLES_LIMIT));
    }

    public int getOpeningInventoriesLimit() {
        return openingInventoriesLimit + (getPlayers().size() * PlayerUtils.getPlayerModifierValue(ownerGroup,PlayerLimit.WORLD_OPENING_INVENTORIES_LIMIT));
    }

    public WorldVariables getWorldVariables() {
        return worldVariables;
    }

    public int getModifyingBlocksLimit() {
        return modifyingBlocksLimit + (getPlayers().size() * PlayerUtils.getPlayerModifierValue(ownerGroup,PlayerLimit.WORLD_MODIFYING_BLOCKS_LIMIT));
    }

    public int getRedstoneOperationsLimit() {
        return redstoneOperationsLimit + (getPlayers().size() * PlayerUtils.getPlayerModifierValue(ownerGroup,PlayerLimit.WORLD_REDSTONE_OPERATIONS_LIMIT));
    }

    public int getCodeOperationsLimit() {
        return codeOperationsLimit + (getPlayers().size() * PlayerUtils.getPlayerModifierValue(ownerGroup,PlayerLimit.WORLD_CODE_OPERATIONS_LIMIT));
    }

    public int getEntitiesLimit() {
        return entitiesLimit + (getPlayers().size() * PlayerUtils.getPlayerModifierValue(ownerGroup,PlayerLimit.WORLD_ENTITIES_LIMIT));
    }

    public Map<String, Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public Map<String, BossBar> getBossBars() {
        return bossBars;
    }

    public int getScoreboardsLimit() {
        return scoreboardsLimit + (getPlayers().size() * PlayerUtils.getPlayerModifierValue(ownerGroup,PlayerLimit.WORLD_SCOREBOARDS_LIMIT));
    }

    public int getBossBarsLimit() {
        return bossBarsLimit + (getPlayers().size() * PlayerUtils.getPlayerModifierValue(ownerGroup,PlayerLimit.WORLD_BOSSBARS_LIMIT));
    }

    public boolean isCorrupted() {
        return isCorrupted;
    }

    public World.Environment getEnvironment() {
        return environment;
    }
}
