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

package mcchickenstudio.creative.events;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import mcchickenstudio.creative.menu.*;
import mcchickenstudio.creative.menu.buttons.RadioButton;
import mcchickenstudio.creative.plots.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static mcchickenstudio.creative.events.ChangedWorld.addPlayerWithLocation;
import static mcchickenstudio.creative.events.ChangedWorld.isPlayerWithLocation;
import static mcchickenstudio.creative.utils.ErrorUtils.sendPlayerErrorMessage;

import static mcchickenstudio.creative.utils.MessageUtils.*;

public class InventoryClick implements Listener {

    final PlotManager plotManager = PlotManager.getInstance();

    @EventHandler
    public void click(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Plot plot1 = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot1 != null) {
            EventRaiser.raiseItemClickEvent((Player) event.getWhoClicked(), event);
            if (event.getAction() == InventoryAction.PLACE_ALL) {
                EventRaiser.raiseItemMoveEvent((Player) event.getWhoClicked(),event);
            }
        }

        if (event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();

            if (event.getInventory().getHolder() instanceof WorldSettingsMenu) {
                if (!(event.getClickedInventory().getHolder() instanceof WorldSettingsMenu)) return;
                if (event.getSlot() == 40 || item.getType() == Material.AIR) {
                    event.setCancelled(true);
                    return;
                }
                if (item.getType() == Material.EMERALD) {
                    try {
                        Material material = event.getCursor().getType();
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (material != Material.AIR) {
                            /*if (material == Material.PLAYER_HEAD) {
                                SkullMeta skullMeta = (SkullMeta) event.getCursor().getItemMeta();
                                if (skullMeta.toString().contains("internal")) {
                                    String internal = skullMeta.toString().split("internal=")[1];
                                    if (internal.contains(",")) {
                                        internal = internal.split(",")[0];
                                    } else if (internal.contains("}")) {
                                        internal = internal.replace("}","");
                                    }
                                    player.getInventory().addItem(getHeadItem(internal));
                                }
                            }*/
                            plot.setPlotIconMaterial(material);
                            player.sendMessage(getLocaleMessage("settings.world-icon.changed"));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    plot.updatePlotIcon();
                                }
                            }.runTaskAsynchronously(Main.getPlugin());
                            WorldSettingsMenu.openInventory(player);
                        } else {
                            player.sendMessage(getLocaleMessage("settings.world-icon.error"));
                            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 2);
                        }
                    } catch (NullPointerException error) {
                        sendPlayerErrorMessage(player, "Невозможно установить значок мира. " + error.getMessage());
                    }
                }
                event.setCancelled(true);
                if (item.getType() == Material.ENDER_PEARL) {
                    player.closeInventory();
                    if (event.getClick().isLeftClick()) {
                        player.getWorld().setSpawnLocation(player.getLocation());
                        player.sendTitle(getLocaleMessage("settings.world-spawn.title"), getLocaleMessage("settings.world-spawn.subtitle"));
                    } else {
                        player.teleport(player.getWorld().getSpawnLocation());
                    }
                } else if (item.getType() == Material.NAME_TAG) {
                    player.sendTitle(getLocaleMessage("settings.world-name.title"), getLocaleMessage("settings.world-name.subtitle"));
                    player.sendMessage(getLocaleMessage("settings.world-name.usage").replace("%player%", player.getName()));
                    player.closeInventory();
                    if (!(PlayerChat.confirmation.containsKey(player))) {
                        PlayerChat.confirmation.put(player, "title");
                    }
                } else if (item.getType() == Material.LEAD) {
                    player.sendTitle(getLocaleMessage("settings.world-id.title"), getLocaleMessage("settings.world-id.subtitle"));
                    player.sendMessage(getLocaleMessage("settings.world-id.usage").replace("%player%", player.getName()));
                    player.closeInventory();
                    if (!(PlayerChat.confirmation.containsKey(player))) {
                        PlayerChat.confirmation.put(player, "id");
                    }
                } else if (item.getType() == Material.BOOK) {
                    player.sendTitle(getLocaleMessage("settings.world-description.title"), getLocaleMessage("settings.world-description.subtitle"));
                    player.sendMessage(getLocaleMessage("settings.world-description.usage"));
                    player.closeInventory();
                    if (!(PlayerChat.confirmation.containsKey(player))) {
                        PlayerChat.confirmation.put(player, "description");
                    }
                } else if (item.getType() == Material.BOOKSHELF) {
                    new WorldSettingsCategoryMenu().open(player);
                } else if (item.getType() == Material.PLAYER_HEAD) {
                    WorldSettingsPlayersMenu.openInventory(player);
                } else if (item.getType() == Material.OAK_DOOR || item.getType() == Material.IRON_DOOR) {
                    Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                    if (plot.getPlotSharing() == Plot.Sharing.PUBLIC) {
                        plot.setPlotSharing(Plot.Sharing.PRIVATE);
                        player.sendMessage(getLocaleMessage("settings.world-sharing.disabled"));
                        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 100, 1);
                    } else {
                        plot.setPlotSharing(Plot.Sharing.PUBLIC);
                        player.sendMessage(getLocaleMessage("settings.world-sharing.enabled"));
                        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 100, 1);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plot.updatePlotIcon();
                        }
                    }.runTaskAsynchronously(Main.getPlugin());
                    WorldSettingsMenu.openInventory(player);
                } else if (item.getType() == Material.PISTON) {
                    new WorldSettingsFlagsMenu().open(player);
                } else if (item.getType() == Material.BRICKS) {
                    player.performCommand("build");
                } else if (item.getType() == Material.DIAMOND_BLOCK) {
                    player.performCommand("play");
                } else if (item.getType() == Material.COMMAND_BLOCK) {
                    player.performCommand("dev");
                } else if (item.getType() == Material.BEACON) {
                    player.performCommand("ad");
                    player.closeInventory();
                } else if (item.getType() == Material.CLOCK) {
                    RadioButton rd = RadioButton.getRadioButtonByItemStack(item);
                    rd.onChoice();
                    player.playSound(player.getLocation(), Sound.AMBIENT_UNDERWATER_EXIT, 100, 1);
                    WorldSettingsMenu.openInventory(player);
                }
            } else if (event.getInventory().getHolder() instanceof WorldSettingsFlagsMenu) {

            } else if (event.getInventory().getHolder() instanceof OwnWorldsMenu) {
                event.setCancelled(true);
                if (item.getType() == Material.AIR) return;
                if (!(event.getClickedInventory().getHolder() instanceof OwnWorldsMenu)) return;
                try {
                    boolean worldClicked = false;
                    if (!plotManager.getPlayerPlots(player).isEmpty()) {
                        for (int slot : OwnWorldsMenu.worldSlots) {
                            if (event.getSlot() == slot) {
                                worldClicked = true;
                                break;
                            }
                        }
                    }
                    if (worldClicked) {
                        List<String> lore = meta.getLore();
                        for (String loreLine : lore) {
                            if (loreLine.startsWith(getLocaleMessage("menus.own-worlds.items.world.id"))) {
                                String worldID = loreLine.replace(getLocaleMessage("menus.own-worlds.items.world.id"), "");
                                player.closeInventory();
                                if (PlotManager.getInstance().getPlotByCustomID(worldID) != null) {
                                    if (!(event.getClick() == ClickType.SHIFT_RIGHT)) {
                                        PlotManager.getInstance().getPlotByCustomID(worldID).teleportPlayer(player);
                                    } else {
                                        PlotManager.getInstance().deletePlot(PlotManager.getInstance().getPlotByCustomID(worldID), player);
                                    }
                                } else {
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 100, 2);
                                    player.clearTitle();
                                    player.sendMessage(getLocaleMessage("no-plot-found", player));
                                }

                            }
                        }
                    } else {
                        if (item.getType() == Material.SPECTRAL_ARROW) {
                            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(getLocaleItemName("menus.own-worlds.items.all-worlds.name"))) {
                                AllWorldsMenu.openInventory(player, 1);
                            } else {
                                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100, 1);
                                OwnWorldsMenu.openInventory(player, OwnWorldsMenu.openedPage.get(player) + 1);
                            }
                        } else if (item.getType() == Material.ARROW) {
                            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100, 1);
                            OwnWorldsMenu.openInventory(player, OwnWorldsMenu.openedPage.get(player) - 1);
                        } else if (item.getType() == Material.WHITE_STAINED_GLASS) {
                            new WorldCreationMenu().open(player);
                        }
                    }
                } catch (Exception error) {
                    sendPlayerErrorMessage(player, "An error has occurred while clicking item in inventory",error);
                }
            } else if (event.getInventory().getHolder() instanceof AllWorldsMenu) {
                event.setCancelled(true);
                if (item.getType() == Material.AIR) return;
                if (!(event.getClickedInventory().getHolder() instanceof AllWorldsMenu)) return;
                try {
                    boolean worldClicked = false;
                    if (!plotManager.getPlots().isEmpty()) {
                        for (int slot : AllWorldsMenu.worldSlots) {
                            if (event.getSlot() == slot) {
                                worldClicked = true;
                                break;
                            }
                        }
                    }
                    if (worldClicked) {
                        List<String> lore = meta.getLore();
                        for (String loreLine : lore) {
                            if (loreLine.startsWith(getLocaleMessage("menus.all-worlds.items.world.id"))) {
                                String worldID = loreLine.replace(getLocaleMessage("menus.all-worlds.items.world.id"), "");
                                player.closeInventory();
                                if (plotManager.getPlotByCustomID(worldID) != null) {
                                    plotManager.getPlotByCustomID(worldID).teleportPlayer(player);
                                } else {
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 100, 2);
                                    player.clearTitle();
                                    player.sendMessage(getLocaleMessage("no-plot-found", player));
                                }
                            }
                        }
                    } else {
                        if (item.getType() == Material.COMMAND_BLOCK) {
                            OwnWorldsMenu.openInventory(player, 1);
                        } else if (item.getType() == Material.SPECTRAL_ARROW) {
                            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100, 1);
                            AllWorldsMenu.openInventory(player, AllWorldsMenu.openedPage.get(player) + 1);
                        } else if (item.getType() == Material.ARROW) {
                            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100, 1);
                            AllWorldsMenu.openInventory(player, AllWorldsMenu.openedPage.get(player) - 1);
                        } else if (item.getType() == Material.BEACON) {
                            if (event.getClick() == ClickType.LEFT) {
                                player.sendTitle(getLocaleMessage("menus.all-worlds.items.search.title").replace("%search%", getLocaleMessage("menus.all-worlds.items.search.world-name")), getLocaleMessage("menus.all-worlds.items.search.subtitle").replace("%search%", getLocaleMessage("menus.all-worlds.items.search.world-name")));
                                player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.usage", player).replace("%search%", getLocaleMessage("menus.all-worlds.items.search.world-name")));
                                player.closeInventory();
                                if (!(PlayerChat.confirmation.containsKey(player))) {
                                    PlayerChat.confirmation.put(player, "searchPlotByPlotName");
                                }
                            } else {
                                player.sendTitle(getLocaleMessage("menus.all-worlds.items.search.title").replace("%search%", getLocaleMessage("menus.all-worlds.items.search.id")), getLocaleMessage("menus.all-worlds.items.search.subtitle").replace("%search%", getLocaleMessage("menus.all-worlds.items.search.id")));
                                player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.usage", player).replace("%search%", getLocaleMessage("menus.all-worlds.items.search.id")));
                                player.closeInventory();
                                if (!(PlayerChat.confirmation.containsKey(player))) {
                                    PlayerChat.confirmation.put(player, "searchPlotByID");
                                }
                            }
                        } else if (item.getType() == Material.CHEST_MINECART) {
                            int nextCategory = AllWorldsMenu.chosenCategories.get(player) + 1;
                            if (nextCategory < 1 || nextCategory > 9) nextCategory = 1;
                            player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 100, 1.3f);
                            AllWorldsMenu.chosenCategories.put(player, nextCategory);
                            if (nextCategory == 1) AllWorldsMenu.openInventory(player, 1);
                            else AllWorldsMenu.openInventory(player, 1, plotManager.getPlotsByCategory(AllWorldsMenu.getPlayerCategory(player)));
                        } else if (item.getType() == Material.HOPPER) {
                            int nextSort = AllWorldsMenu.chosenSorts.get(player) + 1;
                            if (nextSort < 1 || nextSort > 3) nextSort = 1;
                            player.playSound(player.getLocation(), Sound.ITEM_SPYGLASS_USE, 100, 2);
                            AllWorldsMenu.chosenSorts.put(player, nextSort);
                            AllWorldsMenu.openInventory(player, AllWorldsMenu.getCurrentPage(player), AllWorldsMenu.getCurrentPlotList(player));

                        }
                    }
                } catch (Exception error) {
                    sendPlayerErrorMessage(player, "An error has occurred while clicking item in inventory", error);
                }
        } else if (event.getInventory().getHolder() instanceof WorldSettingsPlayersMenu) {
            event.setCancelled(true);
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);

            String selectedPlayer = WorldSettingsPlayersMenu.playersSelected.get(player);

            if (plot == null) return;
            if (item.getType() == Material.SPECTRAL_ARROW) {
                WorldSettingsPlayersMenu.openInventory(player, WorldSettingsPlayersMenu.openedPage.get(player) + 1);
            } else if (item.getType() == Material.ARROW) {
                WorldSettingsPlayersMenu.openInventory(player, WorldSettingsPlayersMenu.openedPage.get(player) - 1);
            } else if (item.getType() == Material.PLAYER_HEAD) {
                boolean playerClicked = false;
                for (int slot : WorldSettingsPlayersMenu.playerSlots) {
                    if (event.getSlot() == slot) {
                        playerClicked = true;
                        break;
                    }
                }
                if (playerClicked) {
                    WorldSettingsPlayersMenu.playersSelected.put(player, ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                    WorldSettingsPlayersMenu.openInventory(player, WorldSettingsPlayersMenu.openedPage.get(player));
                }
            } else if (item.getType() == Material.BARRIER && event.getSlot() == 16) {
                player.closeInventory();
                player.sendMessage(getLocaleMessage("world.players.black-list.added").replace("%player%", selectedPlayer));
                plot.addBlacklist(selectedPlayer);
            } else if (item.getType() == Material.STRUCTURE_VOID) {
                if (event.getSlot() == 16) {
                    player.closeInventory();
                    player.sendMessage(getLocaleMessage("world.players.black-list.removed").replace("%player%", selectedPlayer));
                    plot.removeBlacklist(selectedPlayer);
                } else {
                    player.closeInventory();
                    player.sendMessage(getLocaleMessage("world.players.kick.kicked").replace("%player%", selectedPlayer));
                    Player plotPlayer = Bukkit.getPlayer(selectedPlayer);
                    if (plotPlayer != null) {
                        if (plotManager.getPlotByPlayer(plotPlayer) == plot) {
                            plot.kickPlayer(plotPlayer);
                        }
                    }
                }
                } else if (item.getType() == Material.FEATHER || item.getType() == Material.BRICKS || item.getType() == Material.COMMAND_BLOCK) {
                RadioButton rd = RadioButton.getRadioButtonByItemStack(item);
                    if (rd != null) {
                        rd.onChoice();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 100, 1);
                        WorldSettingsPlayersMenu.openInventory(player);
                    }
                } else if (item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
                    player.closeInventory();
                    if (WorldSettingsPlayersMenu.playersSelected.get(player) == null) return;
                    String newOwner = WorldSettingsPlayersMenu.playersSelected.get(player);
                    if (Bukkit.getPlayer(newOwner) == null) {
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", newOwner));
                        return;
                    }
                    if (!plot.getPlayers().contains(Bukkit.getPlayer(newOwner))) {
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", newOwner));
                        return;
                    }
                    if (PlotManager.getInstance().getPlayerPlots(Bukkit.getPlayer(newOwner)).size() >= PlayerUtils.getPlayerPlotsLimit(Bukkit.getPlayer(newOwner))) {
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit").replace("%player%", newOwner));
                        return;
                    }
                    player.sendMessage(getLocaleMessage("world.players.transfer-ownership.confirm-old").replace("%player%", newOwner).replace("%id%", plot.worldID));
                    player.closeInventory();
                    if (!(PlayerChat.confirmation.containsKey(player))) {
                        PlayerChat.confirmation.put(player, "transfer-ownership");
                    }
                }
            }
            if (!player.getWorld().getName().startsWith("plot")) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().displayName() != null) {
                        if (item.getItemMeta().getDisplayName().equals(getLocaleMessage("items.lobby.games.name")) || item.getItemMeta().displayName().equals(getLocaleMessage("items.lobby.own.name"))) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
        if (devPlot == null) return;
        if (player.getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
            cantDev(player);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof AllWorldsMenu || inventoryHolder instanceof OwnWorldsMenu || inventoryHolder instanceof WorldSettingsMenu) {
            event.setCancelled(true);
        }
    }

    private static void cantDev(Player player) {
        player.closeInventory();
        player.sendActionBar(getLocaleMessage("world.dev-mode.cant-dev"));
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer((Player) event.getPlayer());
        if (plot != null) EventRaiser.raiseOpenInventoryEvent((Player) event.getPlayer(),event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer((Player) event.getPlayer());
        if (plot != null) EventRaiser.raiseCloseInventoryEvent((Player) event.getPlayer(),event);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
        ItemStack currentItem = event.getOffHandItem();
        if (devPlot != null) {
            if (currentItem.getType() == Material.PAPER) {
                event.setCancelled(true);
                if (!player.hasCooldown(currentItem.getType())) {
                    if (plot != null && plot.world != null) {
                        addPlayerWithLocation(player);
                        if (currentItem.hasItemMeta()) {
                            ItemMeta meta = currentItem.getItemMeta();
                            String locationString = ChatColor.stripColor(meta.getDisplayName());
                            String[] locCoords = locationString.split(" ");
                            if (locCoords.length == 5) {
                                try {
                                    double x,y,z;
                                    float yaw,pitch;
                                    x = Double.parseDouble(locCoords[0]);
                                    y = Double.parseDouble(locCoords[1]);
                                    z = Double.parseDouble(locCoords[2]);
                                    yaw = Float.parseFloat(locCoords[3]);
                                    pitch = Float.parseFloat(locCoords[4]);
                                    player.teleport(new Location(plot.world,x,y,z,yaw,pitch));
                                } catch (Exception error) {
                                    player.teleport(plot.world.getSpawnLocation());
                                }
                            }
                        } else {
                            player.teleport(plot.world.getSpawnLocation());
                        }
                        player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100f,0.7f);
                        player.setCooldown(currentItem.getType(),60);
                    }
                }
            }
        } else {
            if (plot != null) {
                if (isPlayerWithLocation(player) && currentItem.getType() == Material.PAPER) {
                    event.setCancelled(true);
                    if (!player.hasCooldown(currentItem.getType())) {
                        if (currentItem.hasItemMeta()) {
                            ItemMeta meta = currentItem.getItemMeta();
                            String locationString = ChatColor.stripColor(meta.getDisplayName());
                            String[] locCoords = locationString.split(" ");
                            if (locCoords.length == 5) {
                                try {
                                    double x,y,z;
                                    float yaw,pitch;
                                    x = Double.parseDouble(locCoords[0]);
                                    y = Double.parseDouble(locCoords[1]);
                                    z = Double.parseDouble(locCoords[2]);
                                    yaw = Float.parseFloat(locCoords[3]);
                                    pitch = Float.parseFloat(locCoords[4]);
                                    player.teleport(new Location(plot.world,x,y,z,yaw,pitch));
                                } catch (Exception error) {
                                    player.teleport(plot.world.getSpawnLocation());
                                }
                            } else {
                                player.teleport(plot.world.getSpawnLocation());
                            }
                        player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100f,0.7f);
                        player.setCooldown(currentItem.getType(),60);
                        }
                    }
                } else {
                    EventRaiser.raiseItemChangeEvent(event.getPlayer(),event);
                }
            }
        }
    }

    @EventHandler
    public void onBookWrite(PlayerEditBookEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raiseBookWriteEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raiseSlotChangeEvent(event.getPlayer(),event);
    }

    private static ItemStack getHeadItem(String internal) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        Set<ProfileProperty> propertySet = profile.getProperties();
        propertySet.add(new ProfileProperty("textures", internal));
        profile.setProperties(propertySet);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setPlayerProfile(profile);
        head.setItemMeta(skullMeta);
        return head;
    }
}
