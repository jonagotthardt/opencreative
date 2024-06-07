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
import mcchickenstudio.creative.coding.BlockParser;
import mcchickenstudio.creative.coding.CodeScript;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import org.bukkit.*;
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
import static mcchickenstudio.creative.utils.PlayerUtils.clearPlayer;
import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;import static mcchickenstudio.creative.utils.ErrorUtils.*;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.*;
import static mcchickenstudio.creative.utils.PlayerUtils.giveBuildPermissions;
import static mcchickenstudio.creative.utils.WorldUtils.generateWorld;

public class Plot {

    /*
     This Plot class will be split in next updates
     and this poorly "public" code will be replaced.
     */

    public World world;
    public String worldName;
    public String worldID;

    public String owner;
    public String ownerGroup;

    public boolean isLoaded;
    public DevPlot devPlot;

    public String plotName;
    public String plotDescription;
    public Material plotIconMaterial;
    public ItemStack plotIcon;
    public String plotCustomID;

    public int plotReputation;
    public Mode plotMode;
    public Sharing plotSharing;
    public Category plotCategory;

    public boolean currentlyTransferringOwnership;

    public int worldSize;
    public int entitiesLimit;
    public int redstoneOperationsLimit;
    public int lastRedstoneOperationsAmount;
    public int codeOperationsLimit;

    private final boolean debug = true;
    private final PlotFlags plotFlags;

    public CodeScript script;

    public boolean isOwner(Player player) {
        return owner.equalsIgnoreCase(player.getName());
    }

    public boolean isOwner(String nickname) {
        return owner.equalsIgnoreCase(nickname);
    }

    public enum Mode {
        PLAYING() {
            public void onPlayerJoin(Player player) {
                player.setGameMode(GameMode.ADVENTURE);
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) player.setGameMode(plot.owner.equalsIgnoreCase(player.getName()) ? GameMode.CREATIVE : GameMode.ADVENTURE);
                if (plot.script != null && plot.script.exists()) {
                    plot.script.loadCode();
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

    /**
     Creates a new plot for specified player with specified generator.
     **/
    public Plot(Player player, WorldUtils.WorldGenerator generator) {

        player.closeInventory();
        owner = player.getName();
        ownerGroup = getOwnerGroup();

        plotName = getLocaleMessage("creating-world.default-world-name",false).replace("%player%",owner);
        plotDescription = getLocaleMessage("creating-world.default-world-description",false).replace("%player%",owner);
        plotIconMaterial = Material.DIAMOND;

        plotMode = Mode.BUILD;
        plotCategory = Category.SANDBOX;
        plotSharing = Sharing.PUBLIC;
        plotReputation = 0;

        lastRedstoneOperationsAmount = 0;
        redstoneOperationsLimit = PlayerUtils.getPlayerPlotRedstoneOperationsLimit(ownerGroup);
        entitiesLimit = PlayerUtils.getPlayerPlotEntitiesLimit(ownerGroup);
        codeOperationsLimit = PlayerUtils.getPlayerPlotCodeOperationsLimit(ownerGroup);
        worldSize = PlayerUtils.getPlayerPlotSize(ownerGroup);
        currentlyTransferringOwnership = false;

        PlotManager.getInstance().addToPlots(this);
        create(this,generator);
        plotFlags = new PlotFlags(this);

        devPlot = new DevPlot(this);
        script = new CodeScript(this,getPlotScriptFile(this));
        PlotManager.getInstance().loadPlotFlags(this);

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
        owner = this.getOwner();
        ownerGroup = getOwnerGroup();

        plotFlags = new PlotFlags(this);

        plotName = this.getPlotName();
        plotDescription = this.getPlotDescription();
        plotIconMaterial = this.getPlotIconMaterial();

        isLoaded = false;

        plotMode = this.getPlotMode();
        plotCategory = this.getPlotCategory();
        plotSharing = this.getWorldSharing();
        plotReputation = this.getReputation();

        lastRedstoneOperationsAmount = 0;
        redstoneOperationsLimit = PlayerUtils.getPlayerPlotRedstoneOperationsLimit(ownerGroup);
        entitiesLimit = PlayerUtils.getPlayerPlotEntitiesLimit(ownerGroup);
        codeOperationsLimit = PlayerUtils.getPlayerPlotCodeOperationsLimit(ownerGroup);
        worldSize = PlayerUtils.getPlayerPlotSize(ownerGroup);

        currentlyTransferringOwnership = false;

        worldID = fileName.replace("plot","");
        plotCustomID = this.getPlotCustomID();
        PlotManager.getInstance().loadPlotFlags(this);

        devPlot = new DevPlot(this);
        PlotManager.getInstance().addToPlots(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlotIcon();
            }
        }.runTaskAsynchronously(Main.getPlugin());
        script = new CodeScript(this,getPlotScriptFile(this));
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
        Material material = this.plotIconMaterial;
        if (!(this.plotSharing == Plot.Sharing.PUBLIC)) material = Material.BARRIER;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLocaleItemName("menus.all-worlds.items.world.name").replace("%plotName%",this.plotName));
        List<String> lore = new ArrayList<>();
        for (String loreLine : getLocaleItemDescription("menus.all-worlds.items.world.lore")) {
            if (loreLine.contains("%plotDescription%")) {
                String[] newLines = this.plotDescription.split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%plotDescription%", ChatColor.translateAlternateColorCodes('&',newLine)));
                }
            } else {
                lore.add(parsePlotLines(this,loreLine.replace("%id%",getLocaleMessage("menus.all-worlds.items.world.id",false) + this.plotCustomID)));
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
        this.plotIcon = item;
    }

    /**
     Creates a world for plot.
     **/
    public void create(Plot plot, WorldUtils.WorldGenerator generator) {
        Player player = Bukkit.getPlayer(plot.owner);
        String worldName = "plot" + WorldUtils.generateWorldID();
        player.sendTitle(getLocaleMessage("creating-world.title"),getLocaleMessage("creating-world.subtitle"),10,300,40);
        Main.getPlugin().getLogger().info("Creating new " + worldName + " by " + player.getName() + "...");
        if (!generateWorld(plot,player,worldName,generator)) {
            player.clearTitle();
            sendPlayerErrorMessage(player,"§cПроизошла ошибка при создании мира... \n§cОбратитесь к администрации!");
        }
    }

    public Sharing getWorldSharing() {
        if (getPlotConfig(this).get("sharing") != null) {
            try {
                return Sharing.valueOf(String.valueOf(getPlotConfig(this).get("sharing")));
            } catch (Exception error) {
                return Sharing.PRIVATE;
            }
        } else {
            return Sharing.PRIVATE;
        }
    }

    // Получить название плота
    public String getPlotName() {
        if (getPlotConfig(this).get("name") != null) {
            return String.valueOf(getPlotConfig(this).get("name"));
        } else {
            return "Unknown name";
        }
    }

    // Получить описание плота
    public String getPlotDescription() {
        if (getPlotConfig(this).get("description") != null) {
            return String.valueOf(getPlotConfig(this).get("description"));
        } else {
            return "Unknown description";
        }
    }

    // Получить описание плота
    public String getPlotCustomID() {
        if (getPlotConfig(this).get("customID") != null) {
            return String.valueOf(getPlotConfig(this).get("customID"));
        } else {
            return this.worldID;
        }
    }

    public Mode getPlotMode() {
        if (getPlotConfig(this).get("mode") != null) {
            try {
                return Mode.valueOf(String.valueOf(getPlotConfig(this).get("mode")));
            } catch (Exception error) {
                return Mode.BUILD;
            }
        } else {
            return Mode.BUILD;
        }
    }

    public Category getPlotCategory() {
        if (getPlotConfig(this).get("category") != null) {
            try {
                return Category.valueOf(String.valueOf(getPlotConfig(this).get("category")));
            } catch (Exception error) {
                return Category.SANDBOX;
            }
        } else {
            return Category.SANDBOX;
        }
    }

    // Получить значок плота
    public Material getPlotIconMaterial() {
        if (getPlotConfig(this).get("icon") != null) {
            if (String.valueOf(getPlotConfig(this).get("icon")).contains("AIR")) return Material.DIAMOND;
            return Material.valueOf(String.valueOf(getPlotConfig(this).get("icon")));
        } else {
            return Material.REDSTONE;
        }
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
            allPlayers.remove(this.owner);

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
            if (devPlot != null && devPlot.isLoaded) {
                playerList.addAll(devPlot.world.getPlayers());
            }
        }
        return playerList;
    }

    public String getOwner() {
        if (getPlotConfig(this).get("owner") != null) {
            return String.valueOf(getPlotConfig(this).get("owner"));
        } else {
            return "Unknown";
        }
    }

    public String getOwnerGroup() {
        if (getPlotConfig(this).get("owner-group") != null) {
            return String.valueOf(getPlotConfig(this).get("owner-group"));
        } else {
            return "default";
        }
    }

    public void teleportPlayer(Player player) {
        if (!(this.plotSharing == Sharing.PUBLIC)) {
            if (!this.owner.equalsIgnoreCase(player.getName())) {
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
        player.sendTitle(getLocaleMessage("teleporting-to-world.title"),getLocaleMessage("teleporting-to-world.subtitle"),15,9999,15);
        if (!this.isLoaded) {
            Main.getPlugin().getLogger().info("Loading " + this.worldName + " and teleporting " + player.getName());
            PlotManager.getInstance().loadPlot(this);
        }
        clearPlayer(player);
        this.world.getSpawnLocation().getChunk().load(true);
        player.teleport(this.world.getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        (this.plotMode == Mode.PLAYING ? Mode.PLAYING : Mode.BUILD).onPlayerJoin(player);
        clearPlayer(player);
        player.sendTitle("","");
        if (!getPlayersFromPlotConfig(this, PlayersType.UNIQUE).contains(player.getName())) {
            addPlayerToListInPlotConfig(this,player.getName(), PlayersType.UNIQUE);
        }
        if (this.isOwner(player.getName())) {
            setPlotConfigParameter(this,"owner-group",PlayerUtils.getGroup(player));
            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
            player.getInventory().setItem(8,worldSettingsItem);
            this.ownerGroup = PlayerUtils.getGroup(player);
            if (this.script.exists() && this.devPlot.isLoaded) {
                new BlockParser().parseCode(this.devPlot);
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
        player.sendTitle(getLocaleMessage("teleporting-to-world.title"),getLocaleMessage("teleporting-to-world.subtitle"),15,9999,15);
        devPlot.loadDevPlotWorld();
        clearPlayer(player);
        devPlot.world.getSpawnLocation().getChunk().load(true);
        player.teleport(this.devPlot.world.getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        devPlot.translateCodingBlocks(player);
    }

    public void teleportToDevPlot(Player player, double x, double y, double z) {
        player.sendTitle(getLocaleMessage("teleporting-to-world.title"),getLocaleMessage("teleporting-to-world.subtitle"),15,9999,15);
        devPlot.loadDevPlotWorld();
        clearPlayer(player);
        player.teleport(this.devPlot.world.getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
        if (x > 0 && y > 0 && z > 0 && x < 99 && y < 99 && z < 99) {
            player.teleport(new Location(this.devPlot.world, x+1,y,z+2,180,5));
        }
        devPlot.translateCodingBlocks(player);
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
}
