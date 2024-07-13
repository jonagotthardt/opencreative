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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mcchickenstudio.creative.utils.FileUtils;
import mcchickenstudio.creative.utils.PlayerUtils;
import mcchickenstudio.creative.utils.WorldUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ErrorUtils.*;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.*;
import static mcchickenstudio.creative.utils.PlayerUtils.*;
import static mcchickenstudio.creative.utils.WorldUtils.generateWorld;

public class Plot {

    /**
     * Some fields will be separated into different
     * classes in next updates.
     */

    public World world;
    public String worldName;
    public String worldID;

    private String owner;
    private String ownerGroup;

    public boolean isLoaded;
    public DevPlot devPlot;

    private String plotName;
    private String plotDescription;
    private Material plotIconMaterial;
    private ItemStack plotIcon;
    private String plotCustomID;

    private int plotReputation;
    private Mode plotMode;
    private Sharing plotSharing;
    private Category plotCategory;

    public boolean currentlyTransferringOwnership;

    public final int worldSize;
    public final int entitiesLimit;
    public final int redstoneOperationsLimit;
    public int lastRedstoneOperationsAmount;
    private final int openingInventoriesLimit;
    private final int variablesAmountLimit;
    private final List<BukkitRunnable> runningBukkitRunnables = new ArrayList<>();
    public final int codeOperationsLimit;

    private final WorldVariables worldVariables;
    private boolean debug = false;
    private final PlotFlags plotFlags;

    private boolean isCorrupted = false;
    public CodeScript script;

    private final Set<PlotPlayer> plotPlayers = new HashSet<>();


    /**
     Creates a new plot for specified player with specified generator.
     **/
    public Plot(Player player, WorldUtils.WorldGenerator generator) {

        player.closeInventory();
        plotPlayers.add(new PlotPlayer(this,player));
        owner = (player.getName());
        ownerGroup = getGroup(player);

        plotName = (getLocaleMessage("creating-world.default-world-name",false).replace("%player%", getOwner()));
        plotDescription = (getLocaleMessage("creating-world.default-world-description",false).replace("%player%", getOwner()));
        plotIconMaterial = (Material.DIAMOND);

        plotMode = (Mode.BUILD);
        plotCategory = (Category.SANDBOX);
        plotSharing = (Sharing.PUBLIC);
        setPlotReputation(0);

        lastRedstoneOperationsAmount = 0;
        redstoneOperationsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_REDSTONE_OPERATIONS_LIMIT);
        entitiesLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_ENTITIES_LIMIT);
        codeOperationsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_CODE_OPERATIONS_LIMIT);
        openingInventoriesLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_OPENING_INVENTORIES_LIMIT);
        variablesAmountLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_VARIABLES_LIMIT);
        worldSize = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_SIZE);
        currentlyTransferringOwnership = false;

        PlotManager.getInstance().addToPlots(this);
        create(this,generator);
        plotFlags = new PlotFlags(this);

        devPlot = new DevPlot(this);
        script = new CodeScript(this,getPlotScriptFile(this));
        worldVariables = new WorldVariables(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlotIcon();
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

        plotFlags = new PlotFlags(this);
        worldVariables = new WorldVariables(this);
        devPlot = new DevPlot(this);
        script = new CodeScript(this,getPlotScriptFile(this));

        lastRedstoneOperationsAmount = 0;
        redstoneOperationsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_REDSTONE_OPERATIONS_LIMIT);
        entitiesLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_ENTITIES_LIMIT);
        codeOperationsLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_CODE_OPERATIONS_LIMIT);
        openingInventoriesLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_OPENING_INVENTORIES_LIMIT);
        variablesAmountLimit = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_VARIABLES_LIMIT);
        worldSize = PlayerUtils.getPlayerLimitValue(getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_SIZE);

        if (!isCorrupted) {
            PlotManager.getInstance().addToPlots(this);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlotIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public boolean isOwner(Player player) {
        return getOwner().equalsIgnoreCase(player.getName());
    }

    public boolean isOwner(String nickname) {
        return getOwner().equalsIgnoreCase(nickname);
    }

    public void setPlotName(String name) {
        this.plotName = name;
        setPlotConfigParameter(this,"name",name);
    }

    public void setPlotDescription(String description) {
        this.plotDescription = description;
        setPlotConfigParameter(this,"description",description);
    }

    public void setPlotIconMaterial(Material material) {
        this.plotIconMaterial = material;
        setPlotConfigParameter(this,"icon",material.name());
    }

    public void setPlotIcon(ItemStack plotIcon) {
        this.plotIcon = plotIcon;
    }

    public void setPlotCustomID(String customID) {
        this.plotCustomID = customID;
        setPlotConfigParameter(this,"customID",customID);
    }

    public int getPlotReputation() {
        return plotReputation;
    }

    public void setPlotReputation(int plotReputation) {
        this.plotReputation = plotReputation;
    }

    public void setPlotMode(Mode mode) {
        this.plotMode = mode;
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

    public enum Mode {
        PLAYING() {
            public void onPlayerJoin(Player player) {
                player.setGameMode(GameMode.ADVENTURE);
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) {
                    player.setGameMode(plot.getOwner().equalsIgnoreCase(player.getName()) ? GameMode.CREATIVE : GameMode.ADVENTURE);
                    if (plot.script != null && plot.script.exists()) {
                        plot.script.loadCode();
                    }
                }
            }
        }, BUILD() {
            public void onPlayerJoin(Player player) {
                player.setGameMode(GameMode.ADVENTURE);
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null)  {
                    if (plot.isOwner(player) || plot.getBuildersList().contains(player.getName())) {
                        player.setGameMode(GameMode.CREATIVE);
                        giveBuildPermissions(player);
                    }
                }

            }
        };

        public String getName() {
            return getLocaleMessage("world." + (this == PLAYING ? "play-mode" : "build-mode") + ".name",false);
        }

        public void onPlayerJoin(Player player) {}
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
        String name = "Unknown name";
        String description = "World data is corrupted,\\nplease report server admin\\nabout this world.";
        String customID = worldID;
        Mode mode = Mode.BUILD;
        Category category = Category.SANDBOX;
        Material material = Material.REDSTONE;
        Sharing sharing = Sharing.PRIVATE;
        if (config != null) {
            if (config.getString("owner") != null) {
                owner = config.getString("owner");
            } else {
                isCorrupted = true;
            }
            if (config.getString("owner-group") != null) {
                ownerGroup = config.getString("owner-group");
            }
            if (config.getString("name") != null) {
                name = config.getString("name");
            }
            if (config.getString("description") != null) {
                description = config.getString("description");
            }
            if (config.getString("customID") != null) {
                customID = config.getString("customID");
            }
            if (config.getString("mode") != null) {
                try {
                    mode = Mode.valueOf(config.getString("mode"));
                } catch (Exception error) {
                    mode = Mode.BUILD;
                }
            }
            if (config.getString("category") != null) {
                try {
                    category = Category.valueOf(config.getString("category"));
                } catch (Exception error) {
                    category = Category.SANDBOX;
                }
            }
            if (config.getString("icon") != null) {
                try {
                    material = Material.valueOf(config.getString("icon"));
                    if (material == Material.AIR) {
                        material = Material.REDSTONE;
                    }
                } catch (Exception error) {
                    material = Material.REDSTONE;
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
        this.plotName = name;
        this.plotDescription = description;
        this.plotCustomID = customID;
        this.plotCategory = category;
        this.plotMode = mode;
        this.plotIconMaterial = material;
        this.plotSharing = sharing;
    }

    public byte getFlagValue(PlotFlags.PlotFlag flag) {
        return (this.plotFlags == null ? 1 : this.plotFlags.getFlagValue(flag));
    }

    public void setFlagValue(PlotFlags.PlotFlag flag, byte value) {
        this.plotFlags.setFlag(flag,value);
    }

    public ItemStack getPlotIcon() {
        return plotIcon;
    }

    /**
     Updates plot's icon.
     **/
    public void updatePlotIcon() {
        Material material = this.getPlotIconMaterial();
        if (!(this.getPlotSharing() == Plot.Sharing.PUBLIC)) material = Material.BARRIER;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLocaleItemName("menus.all-worlds.items.world.name").replace("%plotName%", this.getPlotName()));
        List<String> lore = new ArrayList<>();
        for (String loreLine : getLocaleItemDescription("menus.all-worlds.items.world.lore")) {
            if (loreLine.contains("%plotDescription%")) {
                String[] newLines = this.getPlotDescription().split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%plotDescription%", ChatColor.translateAlternateColorCodes('&',newLine)));
                }
            } else {
                lore.add(parsePlotLines(this,loreLine.replace("%id%",getLocaleMessage("menus.all-worlds.items.world.id",false) + this.getPlotCustomID())));
            }
        }
        item.setAmount((Math.max(this.getOnline(), 1)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.setLore(lore);
        item.setItemMeta(meta);
        this.setPlotIcon(item);
    }

    /**
     Creates a world for plot.
     **/
    public void create(Plot plot, WorldUtils.WorldGenerator generator) {
        Player player = Bukkit.getPlayer(plot.getOwner());
        String worldName = "plot" + WorldUtils.generateWorldID();
        player.sendTitle(getLocaleMessage("creating-world.title"),getLocaleMessage("creating-world.subtitle"),10,300,40);
        Main.getPlugin().getLogger().info("Creating new " + worldName + " by " + player.getName() + "...");
        if (!generateWorld(plot,player,worldName,generator)) {
            player.clearTitle();
            sendPlayerErrorMessage(player,"§cПроизошла ошибка при создании мира... \n§cОбратитесь к администрации!");
        }
    }

    public Sharing getWorldSharing() {
        return getPlotSharing();
    }

    // Получить название плота
    public String getPlotName() {
        return plotName;
    }

    // Получить описание плота
    public String getPlotDescription() {
        return plotDescription;
    }

    // Получить описание плота
    public String getPlotCustomID() {
        return plotCustomID;
    }

    public Mode getPlotMode() {
        return plotMode;
    }

    public Category getPlotCategory() {
        return plotCategory;
    }

    // Получить значок плота
    public Material getPlotIconMaterial() {
        return plotIconMaterial;
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

    public Set<String> getAllPlayersFromConfig() {
        Set<String> allPlayers = new HashSet<>();
        try {
            List<String> onlinePlayers = new ArrayList<>();
            List<String> trustedBuilders = getPlayersFromPlotConfig(this, PlayersType.BUILDERS_TRUSTED);
            List<String> notTrustedBuilders = getPlayersFromPlotConfig(this, PlayersType.BUILDERS_NOT_TRUSTED);
            List<String> trustedDevelopers = getPlayersFromPlotConfig(this, PlayersType.BUILDERS_NOT_TRUSTED);
            List<String> notTrustedDevelopers = getPlayersFromPlotConfig(this, PlayersType.BUILDERS_NOT_TRUSTED);

            this.getPlayers().forEach(player -> onlinePlayers.add(player.getName()));

            allPlayers.addAll(trustedBuilders);
            allPlayers.addAll(notTrustedBuilders);
            allPlayers.addAll(trustedDevelopers);
            allPlayers.addAll(notTrustedDevelopers);
            allPlayers.addAll(onlinePlayers);
            allPlayers.remove(this.getOwner());

            return allPlayers;
        } catch (Exception error) {
            return allPlayers;
        }
    }

    public List<String> getBuildersList() {
        try {
            List<String> trustedBuilders = getPlayersFromPlotConfig(this, PlayersType.BUILDERS_TRUSTED);
            List<String> notTrustedBuilders = getPlayersFromPlotConfig(this, PlayersType.BUILDERS_NOT_TRUSTED);
            trustedBuilders.addAll(notTrustedBuilders);
            return trustedBuilders;
        } catch (Exception error) {
            return new ArrayList<>();
        }
    }

    public String getBuilders() {
        try {
            List<String> trustedBuilders = getPlayersFromPlotConfig(this, PlayersType.BUILDERS_TRUSTED);
            List<String> notTrustedBuilders = getPlayersFromPlotConfig(this, PlayersType.BUILDERS_NOT_TRUSTED);
            trustedBuilders.addAll(notTrustedBuilders);
            String builders = String.join(", ",trustedBuilders);
            return builders;
        } catch (Exception error) {
            return "";
        }
    }

    public String getDevelopers() {
        try {
            List<String> trustedDevelopers = getPlayersFromPlotConfig(this, PlayersType.DEVELOPERS_TRUSTED);
            List<String> notTrustedDevelopers = getPlayersFromPlotConfig(this, PlayersType.DEVELOPERS_NOT_TRUSTED);
            List<String> guestDevelopers = getPlayersFromPlotConfig(this, PlayersType.DEVELOPERS_GUESTS);
            trustedDevelopers.addAll(notTrustedDevelopers);
            trustedDevelopers.addAll(guestDevelopers);
            String developers = String.join(", ",trustedDevelopers);
            return developers;
        } catch (Exception error) {
            return "";
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
        PlotPlayer plotPlayer = new PlotPlayer(this,player);
        plotPlayers.add(plotPlayer);
        player.sendTitle(getLocaleMessage("world.connecting.title"),getLocaleMessage("world.connecting.subtitle"),15,9999,15);
        if (!this.isLoaded) {
            Main.getPlugin().getLogger().info("Loading " + this.worldName + " and teleporting " + player.getName());
            PlotManager.getInstance().loadPlot(this);
        }
        clearPlayer(player);
        this.world.getSpawnLocation().getChunk().load(true);
        player.teleport(this.world.getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        this.plotMode.onPlayerJoin(player);
        plotPlayer.load();
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
            if (this.script.exists() && this.devPlot.isLoaded) {
                new CodingBlockParser().parseCode(this.devPlot);
            }
        }
        EventRaiser.raiseJoinEvent(player);
        Plot plot = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                plot.updatePlotIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    // Телепортировать игрока в мир разработки плота
    public void teleportToDevPlot(Player player) {
        player.sendTitle(getLocaleMessage("world.dev-mode.connecting.title"),getLocaleMessage("world.dev-mode.connecting.subtitle"),15,9999,15);
        devPlot.loadDevPlotWorld();
        devPlot.world.getSpawnLocation().getChunk().load(true);
        Location lastLocation = this.devPlot.lastLocations.get(player);
        System.out.println(Bukkit.getWorld(devPlot.worldName) == null);
        System.out.println(lastLocation);
        if (this.devPlot.world == null) {
            player.sendMessage(ChatColor.RED + " Failed to teleport to developer's environment.");
            return;
        }
        player.teleport(lastLocation == null ? this.devPlot.world.getSpawnLocation() : lastLocation);
        clearPlayer(player);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(devPlot.world.getWorldBorder().getCenter());
        border.setSize(devPlot.world.getWorldBorder().getSize()*5);
        player.setWorldBorder(border);
        devPlot.translateCodingBlocks(player);
    }

    public void teleportToDevPlot(Player player, double x, double y, double z) {
        teleportToDevPlot(player);
        if (x > 0 && y > 0 && z > 0 && x < 99 && y < 99 && z < 99) {
            Location location = new Location(this.devPlot.world, x+1,y,z+2,180,5);
            player.teleport(location);
            spawnGlowingBlock(player,new Location(this.devPlot.world,x,y,z));
        }
    }

    public void removeDeveloper(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (this == plot) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }
        }
        removePlayerFromListInPlotConfig(this,nickname,PlayersType.DEVELOPERS_NOT_TRUSTED);
        removePlayerFromListInPlotConfig(this,nickname,PlayersType.DEVELOPERS_TRUSTED);
    }

    public void removeBuilder(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (this == plot) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
                if (PlotManager.getInstance().getDevPlot(player) != null) {
                    this.teleportPlayer(player);
                }
            }
        }
        removePlayerFromListInPlotConfig(this,nickname,PlayersType.BUILDERS_NOT_TRUSTED);
        removePlayerFromListInPlotConfig(this,nickname,PlayersType.BUILDERS_TRUSTED);
    }

    public void setDeveloperGuest(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
             if (this == plot) {
                    player.sendMessage(getLocaleMessage("world.players.developers.player-guest").replace("%player%",player.getName()));
                    player.playSound(player.getLocation(),Sound.ENTITY_CAT_AMBIENT,100,1);
                }
            }
        addPlayerToListInPlotConfig(this,nickname,PlayersType.DEVELOPERS_GUESTS);
    }

    public void setDeveloperTrusted(String nickname, boolean isTrusted) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (this == plot) {
                if (!isTrusted) {
                    player.sendMessage(getLocaleMessage("world.players.developers.player").replace("%player%",player.getName()));
                    player.playSound(player.getLocation(),Sound.ENTITY_CAT_AMBIENT,100,1);
                    if (PlotManager.getInstance().getDevPlot(player) != null) player.setGameMode(GameMode.CREATIVE);
                }
            }
        }
        if (isTrusted) {
            removePlayerFromListInPlotConfig(this,nickname,PlayersType.DEVELOPERS_NOT_TRUSTED);
            addPlayerToListInPlotConfig(this,nickname,PlayersType.DEVELOPERS_TRUSTED);
        } else {
            addPlayerToListInPlotConfig(this,nickname,PlayersType.DEVELOPERS_NOT_TRUSTED);
        }
        removePlayerFromListInPlotConfig(this,nickname,PlayersType.DEVELOPERS_GUESTS);
    }

    public void setBuilderTrusted(String nickname, boolean isTrusted) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (this == plot) {
                if (!isTrusted) {
                    player.sendMessage(getLocaleMessage("world.players.builders.player").replace("%player%",player.getName()));
                    player.playSound(player.getLocation(),Sound.ENTITY_CAT_AMBIENT,100,1);
                    if (PlotManager.getInstance().getDevPlot(player) != null) return;
                    player.setGameMode(GameMode.CREATIVE);
                }
            }
        }
        if (isTrusted) {
            removePlayerFromListInPlotConfig(this,nickname,PlayersType.BUILDERS_NOT_TRUSTED);
            addPlayerToListInPlotConfig(this,nickname,PlayersType.BUILDERS_TRUSTED);
        } else {
            addPlayerToListInPlotConfig(this,nickname,PlayersType.BUILDERS_NOT_TRUSTED);
        }
    }

    public void kickPlayer(Player player) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (this == plot) {
            teleportToLobby(player);
            player.sendMessage(getLocaleMessage("world.players.kick.player").replace("%player%",player.getName()));
            player.playSound(player.getLocation(),Sound.ENTITY_CAT_HURT,100,1);
        }
    }

    public void addBlacklist(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (this == plot) {
                teleportToLobby(player);
                player.sendMessage(getLocaleMessage("world.players.black-list.player").replace("%player%",player.getName()));
                player.playSound(player.getLocation(),Sound.ENTITY_CAT_HURT,100,1);
            }
        }
        addPlayerToListInPlotConfig(this,nickname,PlayersType.BLACKLISTED);
    }


    public void setOwner(String owner) {
        this.owner = owner;
        FileUtils.setPlotConfigParameter(this,"owner",owner);
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlotIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }
    public void removeBlacklist(String nickname) {
        removePlayerFromListInPlotConfig(this,nickname,PlayersType.BLACKLISTED);
    }

    public void addWhitelist(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (this == plot) {
                player.sendMessage(getLocaleMessage("world.players.white-list.player").replace("%player%",player.getName()));
                player.playSound(player.getLocation(),Sound.ENTITY_CAT_AMBIENT,100,1);
            }
        }
        addPlayerToListInPlotConfig(this,nickname,PlayersType.BLACKLISTED);
    }

    public void setPlotCategory(Category category) {
        this.plotCategory = category;
        setPlotConfigParameter(this,"category",category.toString());
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
            runnable.cancel();
        }
        runningBukkitRunnables.clear();
    }

    public int getVariablesAmountLimit() {
        return variablesAmountLimit;
    }

    public int getOpeningInventoriesLimit() {
        return openingInventoriesLimit;
    }

    public WorldVariables getWorldVariables() {
        return worldVariables;
    }

    public boolean isDeveloper(Player player) {
        if (isOwner(player)) {
            return true;
        }
        List<String> trustedList = FileUtils.getPlayersFromPlotConfig(this,PlayersType.DEVELOPERS_TRUSTED);
        for (String nickname : trustedList) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        if (Bukkit.getPlayer(owner) == null) {
            return false;
        }
        List<String> notTrustedList = FileUtils.getPlayersFromPlotConfig(this,PlayersType.DEVELOPERS_NOT_TRUSTED);
        for (String nickname : notTrustedList) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBuilder(Player player) {
        if (isOwner(player)) {
            return true;
        }
        List<String> trustedList = FileUtils.getPlayersFromPlotConfig(this,PlayersType.BUILDERS_TRUSTED);
        for (String nickname : trustedList) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        if (Bukkit.getPlayer(owner) == null) {
            return false;
        }
        List<String> notTrustedList = FileUtils.getPlayersFromPlotConfig(this,PlayersType.BUILDERS_NOT_TRUSTED);
        for (String nickname : notTrustedList) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public void removePlotPlayer(Player player) {
        plotPlayers.removeIf(plotPlayer -> plotPlayer.getPlayer().equals(player));
    }

    public PlotPlayer getPlotPlayer(Player player) {
        for (PlotPlayer plotPlayer : plotPlayers) {
            if (plotPlayer.getPlayer().equals(player)) {
                return plotPlayer;
            }
        }
        return null;
    }

}
