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

package mcchickenstudio.creative.listeners.player;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.menus.*;
import mcchickenstudio.creative.coding.menus.blocks.*;
import mcchickenstudio.creative.coding.menus.layouts.Layout;
import mcchickenstudio.creative.coding.menus.variables.EventValuesMenu;
import mcchickenstudio.creative.coding.menus.variables.ParticlesMenu;
import mcchickenstudio.creative.coding.menus.variables.PotionsMenu;
import mcchickenstudio.creative.coding.menus.variables.VariablesMenu;
import mcchickenstudio.creative.coding.menus.layouts.LayoutMaker;
import mcchickenstudio.creative.coding.variables.VariableLink;
import mcchickenstudio.creative.menu.AbstractMenu;
import mcchickenstudio.creative.menu.world.browsers.RecommendedWorldsMenu;
import mcchickenstudio.creative.menu.world.settings.WorldSettingsMenu;
import mcchickenstudio.creative.plots.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import mcchickenstudio.creative.menu.world.browsers.OwnWorldsMenu;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.listeners.player.ChangedWorld.*;
import static mcchickenstudio.creative.listeners.player.PlayerPlaceBlock.move;
import static mcchickenstudio.creative.utils.BlockUtils.*;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleItemName;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.isEntityInLobby;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onInteraction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
        if (devPlot == null) {
            setPaperLocation(event, player,currentItem);
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null) {
            if (clickedBlock.getType().toString().contains("WALL_SIGN")) {
                handleSignClick(event,player,currentItem,clickedBlock,devPlot);
            } else if (clickedBlock.getState() instanceof InventoryHolder) {
                handleContainerClick(event,player,devPlot,event.getClickedBlock());
            }
        }
        if (currentItem.getItemMeta() != null) {
            handleCodingItemInteraction(event, player, currentItem, clickedBlock);
        }
    }

    /**
     * Handles player's coding item interaction, often with Right Click Air event.
     * Sets coding items types with persistent data, changes values (CLOCK, FEATHER),
     * changes types (MAGMA_CREAM), opens menus (NAME_TAG, NETHER_STAR, POTION),
     * sends a display name of item (BOOK, SLIME_BALL).
     */
    private void handleCodingItemInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem, Block clickedBlock) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (itemEquals(currentItem,createItem(Material.IRON_INGOT,1,"items.developer.variables"))) {
            new VariablesMenu().open(player);
            return;
        }
        switch (currentItem.getType()) {
            case BOOK -> handleBookClick(event, player, currentItem);
            case SLIME_BALL -> handleSlimeBallClick(event, player, currentItem);
            case BLACK_DYE -> handleDyeClick(event, player, currentItem);
            case FEATHER -> handleFeatherInteraction(event, player, currentItem);
            case CLOCK -> handleClockInteraction(event, player, currentItem);
            case MAGMA_CREAM -> handleMagmaCreamInteraction(event, player, currentItem);
            case PAPER -> handlePaperInteraction(event, player, currentItem);
            case NAME_TAG -> {
                new EventValuesMenu(player).open(player);
                event.setCancelled(true);
            }
            case COMPARATOR -> {
                if (clickedBlock != null) {
                    handleComparatorInteraction(event, player, clickedBlock);
                }
            }
            case NETHER_STAR -> {
                if (player.isSneaking()) {
                    String particleType = getPersistentData(currentItem,getCodingParticleTypeKey());
                    if (particleType.isEmpty()) return;
                    try {
                        Particle particle = Particle.valueOf(particleType.toUpperCase());
                        Vector direction = player.getLocation().getDirection().normalize().multiply(1.5);;
                        Location particleLocation = player.getLocation().add(direction).add(0,1,0);
                        player.spawnParticle(particle,particleLocation,1);
                    } catch (Exception ignored) {}
                } else {
                    new ParticlesMenu(player).open(player);
                }
                event.setCancelled(true);
            }
            case POTION, GLASS_BOTTLE, LINGERING_POTION, SPLASH_POTION -> {
                event.setCancelled(true);
                if (player.isSneaking() && currentItem.getType() != Material.GLASS_BOTTLE) {
                    if (player.hasCooldown(currentItem.getType())) return;
                    player.setCooldown(currentItem.getType(),10);
                    try {
                        PotionMeta potionMeta = (PotionMeta) currentItem.getItemMeta();
                        List<PotionEffect> effects = new ArrayList<>();
                        if (potionMeta.getBasePotionType() != null) {
                            effects.addAll(potionMeta.getBasePotionType().getPotionEffects());
                        }
                        if (potionMeta.hasCustomEffects()) {
                            effects.addAll(potionMeta.getCustomEffects());
                        }
                        for (PotionEffect potionEffect : effects) {
                            if (player.hasPotionEffect(potionEffect.getType())) {
                                player.removePotionEffect(potionEffect.getType());
                            } else {
                                player.addPotionEffect(potionEffect);
                            }
                        }
                    } catch (Exception ignored) {}
                } else {
                    new PotionsMenu(player, currentItem.getType()).open(player);
                }
            }
        }
    }

    /**
     * Handles event, when player clicks coding container block, like chest or barrel.
     * Used for creating and opening layout menu of action.
     */
    private void handleContainerClick(PlayerInteractEvent event, Player player, DevPlot devPlot, Block clickedBlock) {
        if ((!event.getPlayer().isSneaking()) && clickedBlock.getState() instanceof InventoryHolder) {
            Block actionBlock = clickedBlock.getRelative(BlockFace.DOWN);
            Block signBlock = actionBlock.getRelative(BlockFace.SOUTH);
            if (signBlock.getType().toString().contains("WALL_SIGN")) {
                Sign sign = (Sign) signBlock.getState();
                if (sign.lines().size() < 3) return;
                String type = sign.getLine(2);
                try {
                    ActionType action = ActionType.valueOf(type.toUpperCase());
                    Layout layout = devPlot.getOpenedMenu(clickedBlock.getLocation());
                    event.setCancelled(true);
                    if (layout == null) {
                        layout = new LayoutMaker(action,clickedBlock);
                        layout.open(player);
                        devPlot.registerOpenedMenu(clickedBlock.getLocation(),layout);
                    } else {
                        player.openInventory(layout.getCurrentInventory());
                    }
                } catch (IllegalArgumentException e) {
                    player.sendActionBar(getLocaleMessage("plot-code-error.unknown-layout"));
                    event.setCancelled(false);
                }
            }
        }
    }

    /**
     * Handles event, when player clicks WALL_SIGN block.
     * Used for opening menus (executor, action type selection),
     * renaming (function, cycle), changing time (cycle),
     * reversing condition (if conditions).
     */
    private void handleSignClick(PlayerInteractEvent event, Player player, ItemStack currentItem, Block clickedBlock, DevPlot devPlot) {
        event.setCancelled(true);
        if (currentItem.getType() == Material.COMPARATOR) {
            return;
        }
        Block mainBlock = clickedBlock.getRelative(BlockFace.NORTH);
        ExecutorCategory mainBlockCategory = ExecutorCategory.getByMaterial(mainBlock.getType());
        ActionCategory actionBlockCategory = ActionCategory.getByMaterial(mainBlock.getType());
        if (currentItem.getType() == Material.ARROW && (actionBlockCategory != null && actionBlockCategory.isCondition() || actionBlockCategory == ActionCategory.SELECTION_ACTION)) {
            if (event.getHand() == EquipmentSlot.HAND) {
                /*
                 * We cancel changing NOT in selection action,
                 * when sign doesn't have specified condition type
                 * in third sign line, because we can't select
                 * ALL PLAYERS with NOT parameter, it's useless.
                 */
                if (actionBlockCategory == ActionCategory.SELECTION_ACTION && isSignLineEmpty(clickedBlock.getLocation(),(byte) 3)) {
                    return;
                }
                if (isSignLineEmpty(clickedBlock.getLocation(),(byte) 1)) {
                    setSignLine(clickedBlock.getLocation(),(byte) 1,"not");
                    player.playSound(player.getLocation(),Sound.BLOCK_TRIAL_SPAWNER_CLOSE_SHUTTER,100, 1);
                } else {
                    setSignLine(clickedBlock.getLocation(),(byte) 1,"");
                    player.playSound(player.getLocation(),Sound.BLOCK_TRIAL_SPAWNER_CLOSE_SHUTTER,100, 0.1f);
                }
                translateBlockSign(clickedBlock);
            }
        } else if (player.isSneaking() && actionBlockCategory != null) {
            if (actionBlockCategory == ActionCategory.SELECTION_ACTION) {
                String selectionAction = getSignLine(clickedBlock.getLocation(),(byte) 4);
                switch (selectionAction) {
                    case "selection_set" -> {
                        setSignLine(clickedBlock.getLocation(),(byte) 4,"selection_add");
                        player.playSound(player.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_RESONATE,100f,0.5f);
                    }
                    case "selection_add" -> {
                        setSignLine(clickedBlock.getLocation(),(byte) 4,"selection_remove");
                        player.playSound(player.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_RESONATE,100f,0.1f);
                    }
                    case null, default -> {
                        setSignLine(clickedBlock.getLocation(),(byte) 4,"selection_set");
                        player.playSound(player.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_RESONATE,100f,1f);
                    }
                }
                translateBlockSign(clickedBlock);
            } else {
                new TargetSelectionMenu(clickedBlock.getLocation()).open(player);
            }
        } else {
            AbstractMenu menu = null;
            if (mainBlockCategory != null) {
                menu = switch (mainBlockCategory) {
                    case EVENT_PLAYER -> new PlayerEventsMenu(player,clickedBlock.getLocation());
                    case EVENT_WORLD -> new WorldEventsMenu(player,clickedBlock.getLocation());
                    case EVENT_ENTITY -> new EntityEventsMenu(player,clickedBlock.getLocation());
                    default -> null;
                };
            }
            if (actionBlockCategory != null) {
                menu = switch (actionBlockCategory) {
                    case PLAYER_ACTION -> new PlayerActionsMenu(player,clickedBlock.getLocation());
                    case CONTROL_ACTION -> new ControlActionsMenu(player,clickedBlock.getLocation());
                    case PLAYER_CONDITION -> new PlayerConditionsMenu(player,clickedBlock.getLocation());
                    case VARIABLE_CONDITION -> new VariableConditionsMenu(player,clickedBlock.getLocation());
                    case WORLD_CONDITION -> new WorldConditionsMenu(player,clickedBlock.getLocation());
                    case ENTITY_CONDITION -> new EntityConditionsMenu(player,clickedBlock.getLocation());
                    case VARIABLE_ACTION -> new VariableActionsMenu(player,clickedBlock.getLocation());
                    case WORLD_ACTION -> new WorldActionsMenu(player,clickedBlock.getLocation());
                    case HANDLER_ACTION -> new HandlerActionsMenu(player,clickedBlock.getLocation());
                    case REPEAT_ACTION -> new RepeatActionsMenu(player,clickedBlock.getLocation());
                    case SELECTION_ACTION -> new SelectionActionsMenu(player,clickedBlock.getLocation());
                    case ENTITY_ACTION -> new EntityActionsMenu(player,clickedBlock.getLocation());
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

    private void handleDyeClick(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }
            Component displayName = meta.displayName();
            if (displayName != null) {
                player.sendMessage(displayName.hoverEvent(HoverEvent.showText(Component.text(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(ChatColor.stripColor(meta.getDisplayName()))));
            }
        }
    }

    private void handleSlimeBallClick(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }
            Component displayName = meta.displayName();
            if (displayName != null) {
                player.sendMessage(displayName.hoverEvent(HoverEvent.showText(Component.text(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(ChatColor.stripColor(meta.getDisplayName()))));
                setPersistentData(currentItem,getCodingValueKey(),"NUMBER");
            }
        }
    }

    private void handleBookClick(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }
            Component displayName = meta.displayName();
            if (displayName != null) {
                player.sendMessage(displayName.hoverEvent(HoverEvent.showText(Component.text(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(meta.getDisplayName().replace("§","&"))));
                setPersistentData(currentItem,getCodingValueKey(),"TEXT");
            }
        }
    }

    private void handleComparatorInteraction(PlayerInteractEvent event, Player player, Block clickedBlock) {
        if (clickedBlock.getType() == Material.OAK_WALL_SIGN) {
            clickedBlock = clickedBlock.getRelative(BlockFace.NORTH);
        }
        if (ActionCategory.getByMaterial(clickedBlock.getType()) != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (move(clickedBlock.getRelative(BlockFace.WEST).getLocation(),BlockFace.EAST)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 100, 1.3f);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                }
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (move(clickedBlock.getRelative(BlockFace.WEST).getLocation(),BlockFace.WEST)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 100, 1.3f);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                }
            }
        }
    }

    private void handlePaperInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.LEFT_CLICK_AIR && !player.hasCooldown(currentItem.getType())) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot != null && plot.getTerritory().getWorld() != null) {
                addPlayerWithLocation(player);
                player.teleport(plot.getTerritory().getWorld().getSpawnLocation());
                player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100f,0.7f);
                player.setCooldown(currentItem.getType(),60);
            }
        }
    }

    private void handleMagmaCreamInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        /*
         * We use RIGHT_CLICK_AIR to prevent
         * accidental value change on container click.
         */
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (currentItem.getItemMeta() == null || !currentItem.getItemMeta().hasDisplayName()) {
            return;
        }
        event.setCancelled(true);
        ItemMeta meta = currentItem.getItemMeta();
        VariableLink.VariableType type = getVariableType(meta);
        meta.setDisplayName(type.getColor() + ChatColor.stripColor(meta.getDisplayName()));
        player.sendTitle(meta.getDisplayName(),type.getLocalized(),0,40,20);
        currentItem.setItemMeta(meta);
        setPersistentData(currentItem,getCodingValueKey(),"VARIABLE");
        setPersistentData(currentItem,getCodingVariableTypeKey(),type.name());
        player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.7f);
        player.sendMessage(Component.text(meta.getDisplayName()).clickEvent(ClickEvent.copyToClipboard(meta.getDisplayName())));
    }

    private static VariableLink.VariableType getVariableType(ItemMeta meta) {
        char colorCode = 'c';
        String itemName = meta.getDisplayName();
        if (itemName.length() >= 2) {
            colorCode = itemName.charAt(1);
        }
        VariableLink.VariableType type = colorCode == 'a' ? VariableLink.VariableType.SAVED : colorCode == 'e' ? VariableLink.VariableType.GLOBAL : VariableLink.VariableType.LOCAL;
        if (type == VariableLink.VariableType.LOCAL) {
            type = VariableLink.VariableType.GLOBAL;
        } else if (type == VariableLink.VariableType.GLOBAL) {
            type = VariableLink.VariableType.SAVED;
        } else {
            type = VariableLink.VariableType.LOCAL;
        }
        return type;
    }

    private void handleFeatherInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (currentItem.getItemMeta() == null || !currentItem.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!currentItem.getItemMeta().getDisplayName().equals(getLocaleItemName("items.developer.fly-speed-changer.name"))) {
            return;
        }
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
    }

    private void handleClockInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        /*
         * We use RIGHT_CLICK_AIR to prevent
         * accidental value change on container click.
         */
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        boolean value = false;
        ItemMeta meta = currentItem.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            value = meta.getDisplayName().contains("true");
        }
        String displayName = ChatColor.translateAlternateColorCodes('&',!value ? "&atrue" : "&cfalse");
        setDisplayName(currentItem,displayName);
        player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, 100, 1.7f);
        player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"), displayName, 0, 40, 20);
    }

    private void setPaperLocation(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (isPlayerWithLocation(player) && plot != null && !plot.getWorldPlayers().canBuild(player)) {
            event.setCancelled(true);
        }
        if (currentItem.getType() != Material.PAPER || !isPlayerWithLocation(player) || player.hasCooldown(currentItem.getType())) {
            return;
        }
        if (event.getAction().isRightClick()) {
            Block clickedBlock = event.getClickedBlock();
            Location location = player.getLocation();
            if (clickedBlock != null) {
                location = clickedBlock.getLocation();
            }
            String locationString = formatLocation(location);
            setDisplayName(currentItem,locationString);
            player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"),locationString,5,40,5);
            player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,2);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR) {
            if (plot != null && plot.getDevPlot().isLoaded()) {
                player.teleport(getOldLocationPlayerWithLocation(player));
                player.setCooldown(currentItem.getType(),60);
                player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100f,0.7f);
                for (Player developer : plot.getDevPlot().world.getPlayers()) {
                    WorldBorder border = Bukkit.createWorldBorder();
                    border.setCenter(plot.getDevPlot().world.getWorldBorder().getCenter());
                    border.setSize(plot.getDevPlot().world.getWorldBorder().getSize()*5);
                    developer.setWorldBorder(border);
                }
            }
        }
    }

    private String formatLocation(Location location) {
        double x = Math.round(location.getX() * 100.0)/100.0;
        double y = Math.round(location.getY() * 100.0)/100.0;
        double z = Math.round(location.getZ() * 100.0)/100.0;
        float yaw = Math.round(location.getYaw() * 100.0f)/100.0f;
        float pitch = Math.round(location.getPitch() * 100.0f)/100.0f;
        return ChatColor.translateAlternateColorCodes('&',"&a" + x + " " + y + " " + z + " &7" + yaw + " " + pitch);
    }

    @EventHandler
    public void onCompass(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (!event.getAction().isRightClick()) {
            return;
        }
        if (player.hasCooldown(currentItem.getType())) {
            return;
        }
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (isEntityInLobby(player)) {
            if (getItemType(currentItem).equals("worlds")) {
                // Opens recommended worlds menu.
                if (Main.maintenance && !player.hasPermission("opencreative.maintenance.bypass")) {
                    player.sendMessage(getLocaleMessage("maintenance"));
                    return;
                }
                player.setCooldown(Material.COMPASS,60);
                new RecommendedWorldsMenu().open(player);
            } else if (getItemType(currentItem).equals("own_worlds")) {
                // Opens player's worlds menu.
                if (Main.maintenance && !player.hasPermission("opencreative.maintenance.bypass")) {
                    player.sendMessage(getLocaleMessage("maintenance"));
                    return;
                }
                player.setCooldown(Material.NETHER_STAR,60);
                OwnWorldsMenu.openInventory(player,1);
            }
        } else if (plot != null && currentItem.getType() == Material.COMPASS) {
            // Opens world settings menu.
            if (Main.maintenance && !player.hasPermission("opencreative.maintenance.bypass")) {
                player.sendMessage(getLocaleMessage("maintenance"));
                return;
            }
            if (plot.isOwner(player)) {
                player.setCooldown(Material.COMPASS,60);
                new WorldSettingsMenu(plot,player).open(player);
            }
        }
    }

    @EventHandler
    public void onUsing(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot == null) {
            return;
        }
        if (PlotManager.getInstance().getDevPlot(player) != null) {
            return;
        }
        switch (event.getAction()) {
            case LEFT_CLICK_AIR -> EventRaiser.raiseLeftClickEvent(player,event);
            case LEFT_CLICK_BLOCK -> {
                EventRaiser.raiseLeftClickEvent(player,event);
                EventRaiser.raiseBlockInteractionEvent(player,event);
            }
            case PHYSICAL -> EventRaiser.raiseWorldInteractEvent(player,event);
            case RIGHT_CLICK_AIR -> {
                if (event.getHand() == EquipmentSlot.HAND) {
                    EventRaiser.raiseRightClickEvent(player,event);
                }
            }
            case RIGHT_CLICK_BLOCK -> {
                if (event.getHand() == EquipmentSlot.HAND) {
                    EventRaiser.raiseRightClickEvent(player,event);
                    EventRaiser.raiseBlockInteractionEvent(player,event);
                }
            }
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
            if (plot.getWorldPlayers().canBuild(player)) {
                return;
            }
            switch(plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_INTERACT)) {
                // Disallow every block interact.
                case 2 -> {
                    player.sendActionBar(getLocaleMessage("world.cant-block-interact"));
                    event.setCancelled(true);
                }
                // Disallow changing comparator, repeater, note block.
                case 3 -> {
                    if (event.getClickedBlock().getType() == Material.COMPARATOR || event.getClickedBlock().getType() == Material.REPEATER || event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
                        player.sendActionBar(getLocaleMessage("world.cant-block-interact"));
                        event.setCancelled(true);
                    }
                }
                // Disallow changing doors and chests.
                case 4 -> {
                    if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType().toString().contains("DOOR")) {
                        player.sendActionBar(getLocaleMessage("world.cant-block-interact"));
                        event.setCancelled(true);
                    }
                }
                // Disallow interacting with buttons, plates, levers
                case 5 -> {
                    if (event.getClickedBlock().getType().toString().contains("BUTTON") || event.getClickedBlock().getType().toString().contains("PRESSURE_PLATE") || event.getClickedBlock().getType() == Material.LEVER) {
                        player.sendActionBar(getLocaleMessage("world.cant-block-interact"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMobClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot != null) {
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 2 && !plot.getWorldPlayers().canBuild(player)) {
                // Disallow entire mob interaction.
                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 3 && !plot.getWorldPlayers().canBuild(player)) {
                // Disallow changing item frames and armor stands.
                if (event.getRightClicked().getType() == EntityType.ITEM_FRAME || event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
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
            EventRaiser.raiseMobInteractionEvent(player,event);
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 2 && !plot.getWorldPlayers().canBuild(player)) {
                // Disallow entire mob interaction.
                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 3 && !plot.getWorldPlayers().canBuild(player)) {
                // Disallow changing item frames and armor stands.
                if (event.getRightClicked().getType() == EntityType.ITEM_FRAME || event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
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
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 2 && !plot.getWorldPlayers().canBuild(player)) {
                player.sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (plot.getFlagValue(PlotFlags.PlotFlag.MOB_INTERACT) == 3 && !plot.getWorldPlayers().canBuild(player)) {
                if (event.getEntity().getType() == EntityType.ITEM_FRAME) {
                    player.sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (event.getCaught() == null) {
            return;
        }
        if (event.getCaught().getType() == EntityType.ITEM) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
            if (plot != null) EventRaiser.raiseFishEvent(event.getPlayer(),event);
        }
    }

    @EventHandler
    public void onSpectating(PlayerStartSpectatingEntityEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raiseStartSpectatingEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onSpectatingStop(PlayerStopSpectatingEntityEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raiseStopSpectatingEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onBedInteract(PlayerBedEnterEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raisePlayerBedEnterEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onBedInteract(PlayerBedLeaveEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raisePlayerBedLeaveEvent(event.getPlayer(),event);
    }
}
