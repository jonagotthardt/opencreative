/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.DamageBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.DestroyBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.menus.Menus;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.move;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getClosingBracketX;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getPlayerLocaleComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

/**
 * <h1>DestroyBlockListener</h1>
 * This class represents a listener for when a player
 * destroys block or damages block in world.
 */
public final class DestroyBlockListener implements Listener {
    private static final boolean BREAK_DEBUG = false;
    private static final int SHIFT_COMPACT_MAX_PASSES = 20;

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet != null) {
            Block block = event.getBlock();
            Block originalBlock = block;
            boolean chainBreak = OpenCreative.getSettings().getCodingSettings().isShiftBreakChainEnabled() && player.isSneaking();
            boolean signToCodingEnabled = OpenCreative.getSettings().getCodingSettings().isBreakSignDestroysCodingBlock();
            debug(player, "break=" + originalBlock.getType() + " shift=" + chainBreak);

            if (player.getInventory().getItemInMainHand().getType() == Material.COMPARATOR) {
                event.setCancelled(true);
                return;
            }

            DevPlatform platform = devPlanet.getPlatformInLocation(block.getLocation());
            if (platform == null) {
                event.setCancelled(true);
                debug(player, "cancel: platform=null");
                return;
            }

            block = resolveRelatedCodingBlock(devPlanet, platform, block, signToCodingEnabled);
            boolean resolvedToCodingBlock = isCodingBlockOnPlatform(devPlanet, platform, block);
            debug(player, "resolved=" + block.getType() + " coding=" + resolvedToCodingBlock);

            if (devPlanet.getIndestructibleBlocks().contains(block.getType())
                    || block.getType() == platform.getFloorMaterial()
                    || block.getType() == platform.getEventMaterial()
                    || block.getType() == platform.getActionMaterial()
                    || (block.getType() == Material.PISTON && block.getRelative(BlockFace.WEST).isSolid())
            ) {
                Sounds.DEV_NOT_ALLOWED.play(player);
                event.setCancelled(true);
                debug(player, "cancel: indestructible/material-guard");
            }

            if (block.getType() == Material.REDSTONE_WALL_TORCH) {
                devPlanet.setCodeChanged(true);
                Sounds.DEV_UNSET_DEBUG_TORCH.play(player);
                debug(player, "debug-torch removed");
                return;
            }

            if (resolvedToCodingBlock) {
                if (ActionCategory.getByMaterial(block.getType()) != null) {
                    ActionCategory category = ActionCategory.getByMaterial(block.getType());
                    boolean usedShiftChainOnMulti = false;
                    if (chainBreak && category != null && category.isMultiAction()) {
                        debug(player, "action: shift chain remove");
                        usedShiftChainOnMulti = true;
                        if (!destroyBracketChain(platform, devPlanet, block)) {
                            debug(player, "action: chain not found, single remove");
                            platform.destroyCodingBlock(block.getLocation(), devPlanet.isDropItems());
                        }
                    } else {
                        debug(player, "action: single remove");
                        platform.destroyCodingBlock(block.getLocation(), devPlanet.isDropItems());
                    }
                    devPlanet.setCodeChanged(true);
                    debugBlockState(player, block, "after-action");
                    if (usedShiftChainOnMulti) {
                        if (OpenCreative.getSettings().getCodingSettings().isShiftBreakChainCompactFull()) {
                            compactCodingLineLeftByVanillaMove(devPlanet, platform, block);
                        } else {
                            move(block.getLocation(), BlockFace.WEST);
                        }
                    } else {
                        move(block.getLocation(), BlockFace.WEST);
                    }
                } else {
                    if (ExecutorCategory.getByMaterial(block.getType()) != null
                            && chainBreak) {
                        debug(player, "executor: shift line remove");
                        platform.destroyCodingLine(block.getLocation(), devPlanet.isDropItems());
                        devPlanet.setCodeChanged(true);
                        debugBlockState(player, block, "after-executor-line");
                    } else {
                        debug(player, "executor: single remove");
                        devPlanet.setCodeChanged(true);
                        platform.destroyCodingBlock(block.getLocation(), devPlanet.isDropItems());
                        devPlanet.clearMarkedExecutors(block.getLocation());
                        debugBlockState(player, block, "after-executor-single");
                    }

                }
                event.setCancelled(true);
                debug(player, "done: coding branch handled");
                return;
            }

            if (isWallSign(originalBlock)) {
                if (!signToCodingEnabled || !resolvedToCodingBlock) {
                    event.setCancelled(true);
                    translateBlockSign(originalBlock, player);
                    debug(player, "sign: translated/cancelled");
                }
            }

            if (block.getState() instanceof InventoryHolder) {
                Block blockAtDown = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                if (blockAtDown.getType() != platform.getEventMaterial() && blockAtDown.getType() != platform.getActionMaterial()) {
                    return;
                }
                event.setCancelled(true);
                debug(player, "container: cancelled");
            }
        } else if (planet != null) {
            if (ChangedWorld.isPlayerWithLocation(player)) {
                if (!planet.getWorldPlayers().canBuild(player)) {
                    player.sendActionBar(getPlayerLocaleComponent("not-builder", player));
                    event.setCancelled(true);
                    return;
                }
                if (player.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                    event.setCancelled(true);
                }
                return;
            }
            new DestroyBlockEvent(event.getPlayer(), event).callEvent();
            if (!event.isCancelled()) {
                Menus.onBlockDestroy(event.getBlock().getLocation());
            }
        } else if (isEntityInLobby(player) && OpenCreative.getSettings().getLobbySettings().isDestroyingBlocksDisallowed()
                && !player.hasPermission("opencreative.lobby.destroying-blocks.bypass")) {
            event.setCancelled(true);
            player.sendActionBar(getPlayerLocaleComponent("not-for-lobby", player));
        }
    }

    @EventHandler
    public void onStartDamaging(BlockDamageEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new DamageBlockEvent(event.getPlayer(), event).callEvent();
    }

    private boolean isWallSign(Block block) {
        return block.getType().name().contains("WALL_SIGN");
    }

    private boolean isCodingBlock(DevPlanet devPlanet, Block block) {
        return devPlanet.getAllCodingBlocksForPlacing().contains(block.getType());
    }

    private Block resolveRelatedCodingBlock(DevPlanet devPlanet, DevPlatform platform, Block block, boolean signToCodingEnabled) {
        if (isCodingBlockOnPlatform(devPlanet, platform, block)) return block;

        // Sign-to-coding redirect is the only allowed side-resolution behavior.
        if (signToCodingEnabled && isWallSign(block)) {
            for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST}) {
                Block relative = block.getRelative(face);
                if (isCodingBlockOnPlatform(devPlanet, platform, relative)) return relative;
            }
        }

        return block;
    }

    private boolean isCodingBlockOnPlatform(DevPlanet devPlanet, DevPlatform platform, Block block) {
        if (!isCodingBlock(devPlanet, block)) {
            return false;
        }
        Material support = block.getRelative(BlockFace.DOWN).getType();
        return support == platform.getEventMaterial() || support == platform.getActionMaterial();
    }

    private boolean destroyBracketChain(DevPlatform platform, DevPlanet devPlanet, Block block) {
        int closingBracketX = getClosingBracketX(platform, block);
        if (closingBracketX < 0) {
            return false;
        }
        int y = block.getY();
        int z = block.getZ();
        int startX = Math.min(block.getX(), closingBracketX);
        int endX = Math.max(block.getX(), closingBracketX);

        // Remove every coding block in bracket range, including both bounds.
        for (int x = startX; x <= endX; x++) {
            Block inLine = block.getWorld().getBlockAt(x, y, z);
            if (isCodingBlock(devPlanet, inLine)) {
                platform.destroyCodingBlock(inLine.getLocation(), devPlanet.isDropItems());
            }
        }
        return true;
    }

    private void compactCodingLineLeftByVanillaMove(DevPlanet devPlanet, DevPlatform platform, Block startBlock) {
        int y = startBlock.getY();
        int z = startBlock.getZ();
        int beginX = startBlock.getX();
        int endX = devPlanet.getDevPlatformer().getPlatformEndLocation(platform).getBlockX() - 1;

        for (int pass = 0; pass < SHIFT_COMPACT_MAX_PASSES; pass++) {
            int gapX = findGapToCompact(startBlock, y, z, beginX, endX);
            if (gapX < 0) {
                break;
            }
            Location gap = startBlock.getWorld().getBlockAt(gapX, y, z).getLocation();
            if (!move(gap, BlockFace.WEST)) {
                break;
            }
        }
    }

    private int findGapToCompact(Block startBlock, int y, int z, int beginX, int endX) {
        for (int x = beginX; x <= endX; x++) {
            if (startBlock.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) {
                continue;
            }
            if (hasMovableBlockToRight(startBlock, y, z, x, endX)) {
                return x;
            }
        }
        return -1;
    }

    private boolean hasMovableBlockToRight(Block startBlock, int y, int z, int gapX, int endX) {
        for (int x = gapX + 1; x <= endX; x++) {
            if (startBlock.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    private void debug(Player player, String msg) {
        if (!BREAK_DEBUG) return;
        player.sendMessage("§8[OC DBG] §f" + msg);
    }

    private void debugBlockState(Player player, Block block, String stage) {
        if (!BREAK_DEBUG) return;
        debug(player, stage + " now=" + block.getType());
        Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
            debug(player, stage + " tick1=" + block.getWorld().getBlockAt(block.getLocation()).getType());
        }, 1L);
    }

}
