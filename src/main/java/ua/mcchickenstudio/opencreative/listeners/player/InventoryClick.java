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

package ua.mcchickenstudio.opencreative.listeners.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.menu.world.browsers.RecommendedWorldsMenu;
import ua.mcchickenstudio.opencreative.menu.world.browsers.OwnWorldsMenu;
import ua.mcchickenstudio.opencreative.menu.world.*;
import ua.mcchickenstudio.opencreative.menu.world.settings.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ua.mcchickenstudio.opencreative.menu.buttons.RadioButton;
import ua.mcchickenstudio.opencreative.planets.*;
import ua.mcchickenstudio.opencreative.utils.PlayerConfirmation;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.addPlayerWithLocation;
import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.isPlayerWithLocation;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class InventoryClick implements Listener {

    final PlanetManager planetManager = PlanetManager.getInstance();

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        EventRaiser.raiseItemCraftEvent(player,event);
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Planet planet1 = PlanetManager.getInstance().getPlanetByPlayer(player);
        if (planet1 != null) {
            if (event.getCurrentItem() != null) {
                EventRaiser.raiseItemClickEvent((Player) event.getWhoClicked(), event);
                if (event.getAction() == InventoryAction.PLACE_ALL) {
                    EventRaiser.raiseItemMoveEvent((Player) event.getWhoClicked(),event);
                }
            }
        }

        if (event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();

            if (event.getInventory().getHolder() instanceof OwnWorldsMenu) {
                event.setCancelled(true);
                if (item.getType() == Material.AIR) return;
                if (!(event.getClickedInventory().getHolder() instanceof OwnWorldsMenu)) return;
                try {
                    boolean worldClicked = false;
                    if (!planetManager.getPlayerPlanets(player).isEmpty()) {
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
                                if (PlanetManager.getInstance().getPlanetByCustomID(worldID) != null) {
                                    if (!(event.getClick() == ClickType.SHIFT_RIGHT)) {
                                        PlanetManager.getInstance().getPlanetByCustomID(worldID).connectPlayer(player);
                                    } else {
                                        PlanetManager.getInstance().deletePlanet(PlanetManager.getInstance().getPlanetByCustomID(worldID), player);
                                        player.updateInventory();
                                    }
                                } else {
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 100, 2);
                                    player.clearTitle();
                                    player.sendMessage(getLocaleMessage("no-planet-found", player));
                                }

                            }
                        }
                    } else {
                        if (item.getType() == Material.SPECTRAL_ARROW) {
                            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(getLocaleItemName("menus.own-worlds.items.all-worlds.name"))) {
                                new RecommendedWorldsMenu().open(player);
                            } else {
                                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100, 1);
                                OwnWorldsMenu.openInventory(player, OwnWorldsMenu.openedPage.get(player) + 1);
                            }
                        } else if (item.getType() == Material.ARROW) {
                            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100, 1);
                            OwnWorldsMenu.openInventory(player, OwnWorldsMenu.openedPage.get(player) - 1);
                        } else if (item.getType() == Material.WHITE_STAINED_GLASS) {
                            new WorldGenerationMenu(player).open(player);
                        }
                    }
                } catch (Exception error) {
                    sendPlayerErrorMessage(player, "An error has occurred while clicking item in inventory",error);
                }
        } else if (event.getInventory().getHolder() instanceof WorldSettingsPlayersMenu) {
            event.setCancelled(true);
            Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);

            String selectedPlayer = WorldSettingsPlayersMenu.playersSelected.get(player);

            if (planet == null) return;
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
                planet.getWorldPlayers().banPlayer(selectedPlayer);
            } else if (item.getType() == Material.STRUCTURE_VOID) {
                if (event.getSlot() == 16) {
                    player.closeInventory();
                    player.sendMessage(getLocaleMessage("world.players.black-list.removed").replace("%player%", selectedPlayer));
                    planet.getWorldPlayers().unbanPlayer(selectedPlayer);
                } else {
                    player.closeInventory();
                    player.sendMessage(getLocaleMessage("world.players.kick.kicked").replace("%player%", selectedPlayer));
                    Player planetPlayer = Bukkit.getPlayer(selectedPlayer);
                    if (planetPlayer != null) {
                        if (planetManager.getPlanetByPlayer(planetPlayer) == planet) {
                            planet.getWorldPlayers().kickPlayer(planetPlayer);
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
                    if (!planet.getPlayers().contains(Bukkit.getPlayer(newOwner))) {
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", newOwner));
                        return;
                    }
                    if (PlanetManager.getInstance().getPlayerPlanets(Bukkit.getPlayer(newOwner)).size() >= OpenCreative.getSettings().getGroups().getGroup(Bukkit.getPlayer(newOwner)).getWorldsLimit()) {
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit").replace("%player%", newOwner));
                        return;
                    }
                    player.sendMessage(getLocaleMessage("world.players.transfer-ownership.confirm-old").replace("%player%", newOwner).replace("%id%", String.valueOf(planet.getId())));
                    player.closeInventory();
                    if (!(PlayerChat.confirmation.containsKey(player))) {
                        PlayerChat.confirmation.put(player, PlayerConfirmation.TRANSFER_OWNERSHIP);
                    }
                }
            }
            if (!player.getWorld().getName().startsWith("planet")) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().displayName() != null) {
                        if (item.getItemMeta().getDisplayName().equals(getLocaleMessage("items.lobby.games.name")) || item.getItemMeta().getDisplayName().equals(getLocaleMessage("items.lobby.own.name"))) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        DevPlanet devPlanet = PlanetManager.getInstance().getDevPlanet(player);
        if (devPlanet == null) return;
        if (player.getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
            cantDev(player);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof OwnWorldsMenu) {
            event.setCancelled(true);
        }
    }

    private static void cantDev(Player player) {
        player.closeInventory();
        player.sendActionBar(getLocaleMessage("world.dev-mode.cant-dev"));
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer((Player) event.getPlayer());
        if (planet != null) EventRaiser.raiseOpenInventoryEvent((Player) event.getPlayer(),event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer((Player) event.getPlayer());
        if (planet != null) EventRaiser.raiseCloseInventoryEvent((Player) event.getPlayer(),event);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
        DevPlanet devPlanet = PlanetManager.getInstance().getDevPlanet(player);
        ItemStack currentItem = event.getOffHandItem();
        if (devPlanet != null) {
            if (currentItem.getType() == Material.PAPER) {
                event.setCancelled(true);
                if (!player.hasCooldown(currentItem.getType())) {
                    if (planet != null && planet.getTerritory().getWorld() != null) {
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
                                    player.teleport(new Location(planet.getTerritory().getWorld(),x,y,z,yaw,pitch));
                                } catch (Exception error) {
                                    player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
                                }
                            }
                        } else {
                            player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
                        }
                        player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100f,0.7f);
                        player.setCooldown(currentItem.getType(),60);
                    }
                }
            }
        } else {
            if (planet != null) {
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
                                    player.teleport(new Location(planet.getTerritory().getWorld(),x,y,z,yaw,pitch));
                                } catch (Exception error) {
                                    player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
                                }
                            } else {
                                player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
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
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) EventRaiser.raiseBookWriteEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) EventRaiser.raiseItemConsumeEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) EventRaiser.raiseItemBreakEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) EventRaiser.raiseSlotChangeEvent(event.getPlayer(),event);
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
