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

package mcchickenstudio.creative.menu;

import mcchickenstudio.creative.menu.buttons.RadioButton;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.FileUtils;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;

public class WorldSettingsPlayersMenu extends LegacyMenu {

    private final Player player;
    public static final Map<Player,String> playersSelected = new HashMap<>();
    public static final Map<Player,Integer> openedPage = new HashMap<>();
    public static final Map<Player,List<String>> currentPlayersList = new HashMap<>();

    public final int[] decorationSlots = {18,19,20,21,22,23,24,25,26};
    public static final int[] playerSlots = {29,30,31,32,33,38,39,40,41,42};

    public WorldSettingsPlayersMenu(Player player, int page) {

        super(6, MessageUtils.getLocaleMessage("menus.world-settings-players.title"));
        this.player = player;

        Map<Integer, ItemStack> items = new HashMap<>();

        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);

        for (int slot : decorationSlots) {
            ItemStack decorationItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = decorationItem.getItemMeta();
            meta.setDisplayName(" ");
            decorationItem.setItemMeta(meta);
            items.put(slot,decorationItem);
        }

        items.put(10,getPlayerButton(plot));
        items.put(12,getFlyButton(plot));
        items.put(13,getBuildButton(plot));
        items.put(14,getDevButton(plot));
        items.put(15,getKickButton(plot));
        items.put(16,getBanButton(plot));
        items.put(6,getTransferOwnershipButton(plot));

        Set<String> playersList = plot.getAllPlayersFromConfig();

        if (playersList == null || playersList.isEmpty()) {
            ItemStack noPlayersButton = getNoPlayersButton();
            items.put(31,noPlayersButton);
        } else {
            List<List<String>> allPages = getPagesForPlayers(playersList);
            int pageToOpen = page;

            if (page > allPages.size() || page < 1) pageToOpen = 1;
            if (pageToOpen > 1) {
                items.put(46,getPreviousPageButton(page));
            }
            if (pageToOpen < allPages.size()) {
                items.put(52,getNextPageButton(page));
            }
            int slot = 0;
            for (String plotPlayer: allPages.get(pageToOpen-1)) {
                Material material = Material.PLAYER_HEAD;
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                SkullMeta skullMeta = (SkullMeta) meta;
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(plotPlayer));
                skullMeta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings-players.items.player.name").replace("%player%",plotPlayer));
                List<String> lore = new ArrayList<>();
                for (String loreLine : MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.player.lore")) {

                    String build = MessageUtils.getLocaleMessage("menus.world-settings-players.items.build.choices.1");
                    String dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.1");
                    String fly = MessageUtils.getLocaleMessage("menus.world-settings-players.items.fly.choices.1");
                    String online = MessageUtils.getLocaleMessage("menus.world-settings-players.not-in-world");

                    if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_TRUSTED).contains(plotPlayer)) {
                        build = MessageUtils.getLocaleMessage("menus.world-settings-players.items.build.choices.3");
                    } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_NOT_TRUSTED).contains(plotPlayer)) {
                        build = MessageUtils.getLocaleMessage("menus.world-settings-players.items.build.choices.2");
                    }
                    if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED).contains(plotPlayer)) {
                        dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.3");
                    } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED).contains(plotPlayer)) {
                        dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.2");
                    }

                    Player plotPlayer_ = Bukkit.getPlayer(plotPlayer);
                    if (plotPlayer_ != null) {
                        if (PlotManager.getInstance().getPlotByPlayer(plotPlayer_) == plot) {
                            online = MessageUtils.getLocaleMessage("menus.world-settings-players.in-world");
                        }
                        if (plotPlayer_.isFlying()) fly = MessageUtils.getLocaleMessage("menus.world-settings-players.items.fly.choices.2");
                    }
                    if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED).contains(plotPlayer)) {
                        dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.3");
                    } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED).contains(plotPlayer)) {
                        dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.2");
                    }

                    lore.add(MessageUtils.parsePAPI(Bukkit.getOfflinePlayer(plotPlayer),loreLine).replace("%build%",build).replace("%dev%",dev).replace("%fly%",fly).replace("%online%",online));
                }
                skullMeta.setLore(lore);
                item.setItemMeta(skullMeta);
                items.put(playerSlots[slot],item);
                slot++;
            }

            if (pageToOpen > 1) setTitle(MessageUtils.getLocaleMessage("menus.all-worlds.title-pages",false).replace("%page%",String.valueOf(pageToOpen)).replace("%pages%",String.valueOf(allPages.size())));
            openedPage.put(player,pageToOpen);
            currentPlayersList.put(player,new ArrayList<>(playersList));
        }
        setItems(items);
    }

    public static void openInventory(Player player) {
        player.closeInventory();
        player.openInventory(new WorldSettingsPlayersMenu(player,1).getInventory());
    }

    public static void openInventory(Player player, int page) {
        player.closeInventory();
        player.openInventory(new WorldSettingsPlayersMenu(player,page).getInventory());
    }

    public static ItemStack getNextPageButton(int page) {
        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings-players.items.next-page.name").replace("%page%",String.valueOf(page)));
        meta.setLore(MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.next-page.lore"));
        item.setAmount(page);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getPreviousPageButton(int page) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings-players.items.previous-page.name").replace("%page%",String.valueOf(page-1)));
        meta.setLore(MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.previous-page.lore"));
        item.setAmount(page-1);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getNoPlayersButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings-players.items.no-players.name"));
        meta.setLore(MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.no-players.lore"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.LURE, 1);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getBanButton(Plot plot) {
        if (playersSelected.get(player) == null) return null;
        ItemStack item;
        String plotPlayer = playersSelected.get(player);
        if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BLACKLISTED).contains(plotPlayer)) {
            item = new ItemStack(Material.STRUCTURE_VOID);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings-players.items.unban.name"));
            meta.setLore(MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.unban.lore"));
            item.setItemMeta(meta);
        } else {
            item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings-players.items.ban.name"));
            meta.setLore(MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.ban.lore"));
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getKickButton(Plot plot) {
        if (playersSelected.get(player) == null) return null;
        String nickname = playersSelected.get(player);
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            if (PlotManager.getInstance().getPlotByPlayer(player) == plot) {
                ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings-players.items.kick.name"));
                meta.setLore(MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.kick.lore"));
                item.setItemMeta(meta);
                return item;
            }
        }
        return null;

    }

    public ItemStack getTransferOwnershipButton(Plot plot) {
        if (playersSelected.get(player) == null) return null;
        String plotPlayer = playersSelected.get(player);
        if (Bukkit.getPlayer(plotPlayer) == null) return null;
        if (plot.getPlayers().contains(Bukkit.getPlayer(plotPlayer))) {
            if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_TRUSTED).contains(plotPlayer)
            && FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED).contains(plotPlayer)
            && !plot.currentlyTransferringOwnership) {
                return createItem(Material.ENCHANTED_GOLDEN_APPLE,1, "menus.world-settings-players.items.transfer-ownership");
            }
        }
        return null;
    }

    public ItemStack getFlyButton(Plot plot) {
        if (playersSelected.get(player) == null) return null;

        String nickname = playersSelected.get(player);
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            if (PlotManager.getInstance().getPlotByPlayer(player) == plot) {

                List<Runnable> actions = new ArrayList<>();
                actions.add(() -> {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                });
                actions.add(() -> {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                });

                int isFlying = player.isFlying() ? 2 : 1;
                RadioButton radioButton = new RadioButton(Material.FEATHER, MessageUtils.getLocaleItemName("menus.world-settings-players.items.fly.name"),
                        MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.fly.lore"),isFlying,2,actions,
                        "menus.world-settings-players.items.fly.choices","menus.world-settings-players");
                return radioButton.getButtonItem();
            }
        }

        return null;
    }

    public ItemStack getDevButton(Plot plot) {
        if (playersSelected.get(player) == null) return null;
        String nickname = playersSelected.get(player);
        List<Runnable> actions = new ArrayList<>();
        actions.add(() -> {
            player.sendMessage(MessageUtils.getLocaleMessage("world.players.developers.removed").replace("%player%",nickname));
            plot.removeDeveloper(nickname);
        });
        actions.add(() -> {
            player.sendMessage(MessageUtils.getLocaleMessage("world.players.developers.guest").replace("%player%",nickname));
            plot.setDeveloperGuest(nickname);
        });
        actions.add(() -> {
            player.sendMessage(MessageUtils.getLocaleMessage("world.players.developers.added").replace("%player%",nickname));
            plot.setDeveloperTrusted(nickname, false);
        });
        actions.add(() -> {
            player.sendMessage(MessageUtils.getLocaleMessage("world.players.developers.trusted").replace("%player%",nickname));
            plot.setDeveloperTrusted(nickname, true);
        });

        int canDev = 1;
        if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_GUESTS).contains(nickname)) {
            canDev = 2;
        } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED).contains(nickname)) {
            canDev = 3;
        } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED).contains(nickname)) {
            canDev = 4;
        }

        RadioButton radioButton = new RadioButton(Material.COMMAND_BLOCK, MessageUtils.getLocaleItemName("menus.world-settings-players.items.dev.name"),
                MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.dev.lore"), canDev, 4, actions,
                "menus.world-settings-players.items.dev.choices", "menus.world-settings-players");

        ItemStack item = radioButton.getButtonItem();
        return item;
    }

    public ItemStack getBuildButton(Plot plot) {
        if (playersSelected.get(player) == null) return null;

        String nickname = playersSelected.get(player);

        List<Runnable> actions = new ArrayList<>();
        actions.add(() -> {
            player.sendMessage(MessageUtils.getLocaleMessage("world.players.builders.removed").replace("%player%",nickname));
            plot.removeBuilder(nickname);
        });
        actions.add(() -> {
            player.sendMessage(MessageUtils.getLocaleMessage("world.players.builders.added").replace("%player%",nickname));
            plot.setBuilderTrusted(nickname, false);
        });
        actions.add(() -> {
            player.sendMessage(MessageUtils.getLocaleMessage("world.players.builders.trusted").replace("%player%",nickname));
            plot.setBuilderTrusted(nickname, true);
        });

        int canBuild = 1;
        if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_NOT_TRUSTED).contains(nickname)) {
            canBuild = 2;
        } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_TRUSTED).contains(nickname)) {
            canBuild = 3;
        }

        RadioButton radioButton = new RadioButton(Material.BRICKS, MessageUtils.getLocaleItemName("menus.world-settings-players.items.build.name"),
                MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.build.lore"), canBuild, 3, actions,
                "menus.world-settings-players.items.build.choices", "menus.world-settings-players");

        ItemStack item = radioButton.getButtonItem();
        return item;
    }

    public ItemStack getPlayerButton(Plot plot) {
        if (playersSelected.get(player) == null) return null;
        String plotPlayer = playersSelected.get(player);
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        SkullMeta skullMeta = (SkullMeta) meta;
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(plotPlayer));
        skullMeta.setDisplayName(MessageUtils.getLocaleItemName("menus.world-settings-players.items.selected-player.name").replace("%player%",plotPlayer));

        List<String> lore = new ArrayList<>();
        for (String loreLine : MessageUtils.getLocaleItemDescription("menus.world-settings-players.items.player.lore")) {

            String build = MessageUtils.getLocaleMessage("menus.world-settings-players.items.build.choices.1");
            String dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.1");
            String fly = MessageUtils.getLocaleMessage("menus.world-settings-players.items.fly.choices.1");
            String online = MessageUtils.getLocaleMessage("menus.world-settings-players.not-in-world");

            if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_TRUSTED).contains(plotPlayer)) {
                build = MessageUtils.getLocaleMessage("menus.world-settings-players.items.build.choices.3");
            } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_NOT_TRUSTED).contains(plotPlayer)) {
                build = MessageUtils.getLocaleMessage("menus.world-settings-players.items.build.choices.2");
            }
            if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED).contains(plotPlayer)) {
                dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.3");
            } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED).contains(plotPlayer)) {
                dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.2");
            }

            Player plotPlayer_ = Bukkit.getPlayer(plotPlayer);
            if (plotPlayer_ != null) {
                if (PlotManager.getInstance().getPlotByPlayer(plotPlayer_) == plot) {
                    online = MessageUtils.getLocaleMessage("menus.world-settings-players.in-world");
                }
                if (plotPlayer_.isFlying()) fly = MessageUtils.getLocaleMessage("menus.world-settings-players.items.fly.choices.2");
            }
            if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED).contains(plotPlayer)) {
                dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.3");
            } else if (FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED).contains(plotPlayer)) {
                dev = MessageUtils.getLocaleMessage("menus.world-settings-players.items.dev.choices.2");
            }

            lore.add(MessageUtils.parsePAPI(Bukkit.getOfflinePlayer(plotPlayer),loreLine).replace("%build%",build).replace("%dev%",dev).replace("%fly%",fly).replace("%online%",online));
        }

        skullMeta.setLore(lore);
        item.setItemMeta(skullMeta);
        return item;
    }

 /*   public static List<Plot> getCurrentPlotList(Player player) {
        return currentPlotList.getOrDefault(player,plots);
    }

    public static int getCurrentPage(Player player) {
        return openedPage.getOrDefault(player, 1);
    }
*/
    private static List<List<String>> getPagesForPlayers(Set<String> playerList) {

        List<List<String>> pages = new ArrayList<>();

        int pageSize = 10;
        int pageCount = (int) Math.ceil((double) playerList.size() / pageSize);

        for (int i = 0; i < pageCount; i++) {
            int fromIndex = i * pageSize;
            int toIndex = Math.min((i + 1) * pageSize, playerList.size());

            List<String> sublist = new ArrayList<>(playerList).subList(fromIndex, toIndex);
            ArrayList<String> page = new ArrayList<>(sublist);

            pages.add(page);
        }

        return pages;
    }


}