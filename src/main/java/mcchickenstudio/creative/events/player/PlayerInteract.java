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

package mcchickenstudio.creative.events.player;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.menus.*;
import mcchickenstudio.creative.coding.menus.variables.EventValuesMenu;
import mcchickenstudio.creative.coding.menus.variables.PotionsMenu;
import mcchickenstudio.creative.coding.menus.variables.VariablesMenu;
import mcchickenstudio.creative.coding.menus.layouts.LayoutMaker;
import mcchickenstudio.creative.coding.variables.VariableLink;
import mcchickenstudio.creative.plots.PlotFlags;
import mcchickenstudio.creative.plots.PlotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import mcchickenstudio.creative.menu.world.browsers.AllWorldsMenu;
import mcchickenstudio.creative.menu.world.browsers.OwnWorldsMenu;
import mcchickenstudio.creative.menu.world.settings.WorldSettingsMenu;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static mcchickenstudio.creative.events.player.ChangedWorld.*;
import static mcchickenstudio.creative.events.player.PlayerPlaceBlock.move;
import static mcchickenstudio.creative.utils.BlockUtils.getSignLine;
import static mcchickenstudio.creative.utils.BlockUtils.setSignLine;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleItemName;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (currentItem.getType() == Material.NETHER_STAR && event.getAction().isRightClick() && event.getPlayer().getWorld().getName().equals("world") && !(event.getPlayer().getCooldown(Material.NETHER_STAR) > 0)) {
            if (Main.maintenance && !player.hasPermission("creative.maintenance.bypass")) {
                player.sendMessage(getLocaleMessage("maintenance"));
                return;
            }
            event.getPlayer().setCooldown(Material.NETHER_STAR,60);
            OwnWorldsMenu.openInventory(event.getPlayer(),1);
        }

        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
        if (devPlot != null) {
            Block clickedBlock = event.getClickedBlock();
            if (currentItem.getItemMeta() != null) {
                if (currentItem.getType() == Material.COMPARATOR && player.isSneaking() && clickedBlock != null) {
                    if (ActionCategory.getByMaterial(clickedBlock.getType()) != null) {
                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            if (clickedBlock.getX() < 96) {
                                move(clickedBlock.getRelative(BlockFace.WEST).getLocation(),BlockFace.EAST);
                                player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 100, 1.3f);
                            } else {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                            }
                        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            if (clickedBlock.getX() > 6 && clickedBlock.getRelative(BlockFace.WEST).getType() == Material.AIR && clickedBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getType() == Material.AIR) {
                                move(clickedBlock.getRelative(BlockFace.WEST).getLocation(),BlockFace.WEST);
                                player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 100, 1.3f);
                            } else {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                            }                        }
                    }
                } else if (currentItem.getType() == Material.FEATHER && currentItem.getItemMeta().getDisplayName().equals(getLocaleItemName("items.developer.fly-speed-changer.name"))) {
                    currentItem.setAmount((currentItem.getAmount() > 3 ? 1 : currentItem.getAmount() + 1));
                    float currentSpead = currentItem.getAmount() + 1;
                    float speed = ((currentSpead - 1) / 9) * (1 - 0.1f);
                    if (speed > 1) {
                        speed = currentSpead * 0.1f;
                    }
                    player.setFlying(true);
                    player.setFlySpeed(speed);
                    player.sendActionBar(getLocaleMessage("world.dev-mode.changed-fly-speed").replace("%speed%", String.valueOf(currentItem.getAmount())));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1.9f);
                } else if (itemEquals(currentItem,createItem(Material.IRON_INGOT,1,"items.developer.variables"))) {
                    new VariablesMenu().open(player);
                } else if (currentItem.getType() == Material.POTION || currentItem.getType() == Material.GLASS_BOTTLE) {
                    new PotionsMenu(player).open(player);
                    event.setCancelled(true);
                } else if (currentItem.getType() == Material.NAME_TAG) {
                    new EventValuesMenu(player).open(player);
                    event.setCancelled(true);
                } else if (currentItem.getType() == Material.PAPER) {
                    if (event.getAction() == Action.LEFT_CLICK_AIR && !player.hasCooldown(currentItem.getType())) {
                        Plot plot = devPlot.getLinkedPlot();
                        if (plot != null && plot.world != null) {
                            addPlayerWithLocation(player);
                            player.teleport(plot.world.getSpawnLocation());
                            player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100f,0.7f);
                            player.setCooldown(currentItem.getType(),60);
                        }
                    }
                } else if (currentItem.getType() == Material.CLOCK) {
                    if (event.getAction().isRightClick()) {
                        if (currentItem.hasItemMeta()) {
                            ItemMeta meta = currentItem.getItemMeta();
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',(meta.getDisplayName().contains("true") ? "&cfalse" : "&atrue")));
                            currentItem.setItemMeta(meta);
                            player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.7f);
                            player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"),meta.getDisplayName(),0,40,20);
                        }
                    }
                } else if (currentItem.getType() == Material.MAGMA_CREAM) {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        if (currentItem.hasItemMeta()) {
                            event.setCancelled(true);
                            ItemMeta meta = currentItem.getItemMeta();
                            char colorCode = 'c';
                            if (currentItem.hasItemMeta()) {
                                String itemName = meta.getDisplayName();
                                if (itemName.length() >= 2) {
                                    colorCode = itemName.charAt(1);
                                }
                            }
                            VariableLink.VariableType type = colorCode == 'a' ? VariableLink.VariableType.SAVED : colorCode == 'e' ? VariableLink.VariableType.GLOBAL : VariableLink.VariableType.LOCAL;
                            if (type == VariableLink.VariableType.LOCAL) {
                                type = VariableLink.VariableType.GLOBAL;
                            } else if (type == VariableLink.VariableType.GLOBAL) {
                                type = VariableLink.VariableType.SAVED;
                            } else {
                                type = VariableLink.VariableType.LOCAL;
                            }
                            meta.setDisplayName(type.getColor() + ChatColor.stripColor(meta.getDisplayName()));
                            player.sendTitle(meta.getDisplayName(),type.getLocalized(),0,40,20);
                            currentItem.setItemMeta(meta);
                            setPersistentData(currentItem,getCodingValueKey(),"VARIABLE");
                            setPersistentData(currentItem,getCodingVariableTypeKey(),type.name());
                            player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.7f);
                            player.sendMessage(Component.text(meta.getDisplayName()).clickEvent(ClickEvent.copyToClipboard(meta.getDisplayName())));
                        }
                    }
                } else if (currentItem.getType() == Material.BOOK && event.getAction() == Action.RIGHT_CLICK_AIR) {
                    ItemMeta meta = currentItem.getItemMeta();
                    if (meta != null) {
                        player.sendMessage(Component.text(meta.getDisplayName()).hoverEvent(HoverEvent.showText(Component.text(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(meta.getDisplayName().replace("§","&"))));
                        setPersistentData(currentItem,getCodingValueKey(),"TEXT");
                    }
                } else if (currentItem.getType() == Material.SLIME_BALL && event.getAction() == Action.RIGHT_CLICK_AIR) {
                    ItemMeta meta = currentItem.getItemMeta();
                    if (meta != null) {
                        player.sendMessage(Component.text(meta.getDisplayName()).hoverEvent(HoverEvent.showText(Component.text(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(ChatColor.stripColor(meta.getDisplayName()))));
                        setPersistentData(currentItem,getCodingValueKey(),"NUMBER");
                    }
                }
            }

            if (clickedBlock == null) return;
            if (event.getAction().isRightClick() && clickedBlock.getType().toString().contains("WALL_SIGN")) {
                event.setCancelled(true);
            }

            Block mainBlock = clickedBlock.getRelative(BlockFace.NORTH);

            if (clickedBlock.getType() == Material.OAK_WALL_SIGN) {
                ExecutorCategory mainBlockCategory = ExecutorCategory.getByMaterial(mainBlock.getType());
                ActionCategory actionBlockCategory = ActionCategory.getByMaterial(mainBlock.getType());

                if (player.isSneaking() && actionBlockCategory != null) {
                    new TargetSelectionMenu(clickedBlock.getLocation()).open(player);
                } else {
                    CodingBlockTypesMenu menu = null;
                    if (mainBlockCategory == ExecutorCategory.EVENT_PLAYER) {
                        menu = new PlayerEventsMenu(player,clickedBlock.getLocation());
                    }
                    if (actionBlockCategory != null) {
                        menu = switch (actionBlockCategory) {
                            case PLAYER_ACTION -> new PlayerActionsMenu(player,clickedBlock.getLocation());
                            case CONTROL_ACTION -> new ControlActionsMenu(player,clickedBlock.getLocation());
                            case PLAYER_CONDITION -> new PlayerConditionsMenu(player,clickedBlock.getLocation());
                            case VARIABLE_CONDITION -> new VariableConditionsMenu(player,clickedBlock.getLocation());
                            case VARIABLE_ACTION -> new VariableActionsMenu(player,clickedBlock.getLocation());
                            case WORLD_ACTION -> new WorldActionsMenu(player,clickedBlock.getLocation());
                            //case HANDLER_ACTION -> new HandlerActionsMenu(player,clickedBlock.getLocation());
                            //case REPEAT_ACTION -> new RepeatActionsMenu(player,clickedBlock.getLocation());
                            default -> null;
                        };
                    }
                    if (menu != null) {
                        menu.open(player);
                    } else if (actionBlockCategory == ActionCategory.LAUNCH_FUNCTION_ACTION) {
                        new FunctionChooserMenu(player, devPlot,clickedBlock.getLocation()).open(player);
                    } else if (mainBlockCategory == ExecutorCategory.CYCLE) {
                        String cycleTicksString = getSignLine(clickedBlock.getLocation(),(byte) 3);
                        if (cycleTicksString != null && !cycleTicksString.isEmpty()) {
                            int cycleTicks = 20;
                            try {
                                cycleTicks = Integer.parseInt(cycleTicksString);
                            } catch (NumberFormatException exception){
                                setSignLine(clickedBlock.getLocation(),(byte) 3,"20");
                            }
                            if (event.getAction().isRightClick()) {
                                if (event.getPlayer().isSneaking()) {
                                    cycleTicks -= 1;
                                    player.playSound(player.getLocation(),Sound.BLOCK_CHAIN_FALL,100f,0.1f);
                                } else {
                                    ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                                    if (!item.isEmpty() && item.hasItemMeta()) {
                                        String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                        if (item.getType() == Material.SLIME_BALL) {
                                            try {
                                                cycleTicks = Math.round(Float.parseFloat(displayName));
                                            } catch (NumberFormatException ignored) {}
                                            player.playSound(player.getLocation(),Sound.BLOCK_CHAIN_FALL,100f,0.5f);
                                        } else if (displayName.length() < 15) {
                                            setSignLine(clickedBlock.getLocation(),(byte) 1,displayName);
                                            player.playSound(player.getLocation(),Sound.BLOCK_CHAIN_FALL,100f,0.7f);
                                        }
                                    } else {
                                        cycleTicks += 1;
                                        player.playSound(player.getLocation(),Sound.BLOCK_CHAIN_FALL,100f,1f);
                                    }
                                }
                            }
                            if (cycleTicks < 5) {
                                cycleTicks = 5;
                            }
                            if (cycleTicks > 3600) {
                                cycleTicks = 3600;
                            }
                            setSignLine(clickedBlock.getLocation(),(byte) 3,String.valueOf(cycleTicks));
                            translateBlockSign(clickedBlock);
                        }
                    } else if (mainBlockCategory == ExecutorCategory.FUNCTION) {
                        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                        if (!item.isEmpty() && item.hasItemMeta()) {
                            String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                            if (displayName.length() < 15) {
                                setSignLine(clickedBlock.getLocation(),(byte) 3,displayName);
                                player.playSound(player.getLocation(),Sound.BLOCK_ENCHANTMENT_TABLE_USE,100f,0.7f);
                                translateBlockSign(clickedBlock);
                            }
                        }
                    }
                }

            }
            if ((!event.getPlayer().isSneaking()) && clickedBlock.getState() instanceof InventoryHolder) {
                Block actionBlock = clickedBlock.getRelative(BlockFace.DOWN);
                Block signBlock = actionBlock.getRelative(BlockFace.SOUTH);
                if (signBlock.getType().toString().contains("SIGN")) {
                    Sign sign = (Sign) signBlock.getState();
                    if (sign.lines().size() < 3) return;
                    String type = sign.getLine(2);
                    try {
                        ActionType action = ActionType.valueOf(type.toUpperCase());
                        new LayoutMaker(action,clickedBlock).open(player);
                        event.setCancelled(true);
                    } catch (IllegalArgumentException e) {
                        player.sendActionBar(getLocaleMessage("plot-code-error.unknown-layout"));
                    }
                }
            }


        } else {
            if (currentItem.getType() == Material.PAPER && isPlayerWithLocation(player) && currentItem.hasItemMeta()) {
                if (event.getAction().isRightClick()) {
                    Block clickedBlock = event.getClickedBlock();
                    Location location = player.getLocation();
                    if (clickedBlock != null) {
                        location = clickedBlock.getLocation();
                    }
                    ItemMeta meta = currentItem.getItemMeta();
                    double x = Math.round(location.getX() * 100.0)/100.0;
                    double y = Math.round(location.getY() * 100.0)/100.0;
                    double z = Math.round(location.getZ() * 100.0)/100.0;
                    float yaw = Math.round(location.getYaw() * 100.0f)/100.0f;
                    float pitch = Math.round(location.getPitch() * 100.0f)/100.0f;
                    String locationString = ChatColor.translateAlternateColorCodes('&',"&a" + x + " " + y + " " + z + " &7" + yaw + " " + pitch);
                    meta.setDisplayName(locationString);
                    currentItem.setItemMeta(meta);
                    player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"),locationString,5,40,5);
                    player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,2);
                } else if (event.getAction() == Action.LEFT_CLICK_AIR && !player.hasCooldown(currentItem.getType())) {
                    Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                    if (plot != null && plot.devPlot != null && plot.devPlot.isLoaded()) {
                        player.teleport(getOldLocationPlayerWithLocation(player));
                        player.setCooldown(currentItem.getType(),60);
                        player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100f,0.7f);
                    }
                }
            }
        }
    }

    @EventHandler
    public void compass(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && (event.getPlayer().getItemInHand().getType() == Material.COMPASS)
                && event.getPlayer().getWorld().getName().equals("world")
                && !(event.getPlayer().getCooldown(Material.COMPASS) > 0)) {
            if (Main.maintenance && !event.getPlayer().hasPermission("creative.maintenance.bypass")) {
                event.getPlayer().sendMessage(getLocaleMessage("maintenance"));
                return;
            }
            event.getPlayer().setCooldown(Material.COMPASS,60);
            AllWorldsMenu.openInventory(event.getPlayer(),1);
        }

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getPlayer().getItemInHand().getType() == Material.COMPASS
                && event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(getLocaleItemName("items.developer.world-settings.name"))
                && !(event.getPlayer().getCooldown(Material.COMPASS) > 0)) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
            if (plot == null) return;
            if (plot.isOwner(event.getPlayer())) {
                event.getPlayer().setCooldown(Material.COMPASS,60);
                WorldSettingsMenu.openInventory(event.getPlayer());
            }
        }

        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) {
            if (!event.getPlayer().getWorld().getName().contains("dev") && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL)) {
                if (!(plot.getOwner().equalsIgnoreCase(event.getPlayer().getName()) || plot.getBuilders().contains(event.getPlayer().getName()))) {
                    switch(plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_INTERACT)) {
                        case 2:
                            event.getPlayer().sendActionBar(getLocaleMessage("world.cant-block-interact"));
                            event.setCancelled(true);
                            break;
                        case 3:
                            if (event.getClickedBlock().getType() == Material.COMPARATOR || event.getClickedBlock().getType() == Material.REPEATER || event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
                                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-block-interact"));
                                event.setCancelled(true);
                            }
                            break;
                        case 4:
                            if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType().toString().contains("DOOR")) {
                                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-block-interact"));
                                event.setCancelled(true);
                            }
                            break;
                        case 5:
                            if (event.getClickedBlock().getType().toString().contains("BUTTON") || event.getClickedBlock().getType().toString().contains("PRESSURE_PLATE") || event.getClickedBlock().getType() == Material.LEVER) {
                                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-block-interact"));
                                event.setCancelled(true);
                            }
                            break;
                    }
                }
            }
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                EventRaiser.raiseLeftClickEvent(event.getPlayer(),event);
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    EventRaiser.raiseBlockInteractionEvent(event.getPlayer(),event);
                }
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getHand() == EquipmentSlot.HAND) {
                    return;
                }
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    EventRaiser.raiseBlockInteractionEvent(event.getPlayer(),event);
                }
                EventRaiser.raiseRightClickEvent(event.getPlayer(),event);
            }
            if (event.getAction() == Action.PHYSICAL) {
                EventRaiser.raiseWorldInteractEvent(event.getPlayer(),event);
            }
        }


    }

    @EventHandler
    public void onMobClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot != null) {
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 2 && (!plot.isOwner(player))) {
                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 3 && (!plot.isOwner(player))) {
                if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
                    event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onMobClick(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot != null) {
            EventRaiser.raiseMobInteractionEvent(event.getPlayer(),event);
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 2 && (!plot.isOwner(player))) {
                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 3 && (!plot.isOwner(player))) {
                if (event.getRightClicked().getType() == EntityType.ITEM_FRAME ||
                        event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
                    event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onHang(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot != null) {
            EventRaiser.raiseMobInteractionEvent(player,event);
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 2 && (!plot.isOwner(player))) {
                player.sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 3 && (!plot.isOwner(player))) {
                if (event.getEntity().getType() == EntityType.ITEM_FRAME)
                    player.sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (event.getCaught().getType() == EntityType.ITEM) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
            if (plot != null) EventRaiser.raiseFishEvent(event.getPlayer(),event);
        }
    }

    @EventHandler
    public void onSpectating(PlayerStartSpectatingEntityEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null)  EventRaiser.raiseStartSpectatingEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onSpectatingStop(PlayerStopSpectatingEntityEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null)  EventRaiser.raiseStopSpectatingEvent(event.getPlayer(),event);
    }
}
