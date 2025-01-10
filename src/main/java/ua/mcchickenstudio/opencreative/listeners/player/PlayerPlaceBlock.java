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

import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.Layout;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public class PlayerPlaceBlock implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
        DevPlanet devPlanet = PlanetManager.getInstance().getDevPlanet(player);

        if (devPlanet != null) {

            Block block = event.getBlock();
            Block blockAgainst = event.getBlockAgainst();

            DevPlatform platform = devPlanet.getPlatformInLocation(event.getBlock().getLocation());
            if (platform == null) {
                event.setCancelled(true);
                return;
            }

            if (blockAgainst.getType() == platform.getFloorMaterial()) {
                if ((!(block.getType() == Material.PISTON && (blockAgainst.getZ() % 4) == 0 && blockAgainst.getRelative(BlockFace.WEST).getType() == platform.getActionMaterial())) && (!(block.getType().name().contains("SIGN") &&  blockAgainst.getX() >= 4 && (blockAgainst.getX() % 2) == 0)) && (!devPlanet.getAllowedBlocks().contains(block.getType())) || block.getY() <= 0) {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-on-floor"));
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else if (blockAgainst.getType() == platform.getEventMaterial()) {
                // Easter egg :)
                if (block.getType() == Material.PUMPKIN) {
                    event.setCancelled(true);
                    block.getLocation().getWorld().strikeLightningEffect(block.getLocation());
                    player.getInventory().setHelmet(new ItemStack(Material.PUMPKIN,1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,40,0));
                    player.playSound(player.getLocation(),Sound.ENTITY_WITCH_CELEBRATE,100,1f);
                    return;
                }
                if (devPlanet.getEventsBlocks().contains(block.getType())) {
                    Material additionalBlockMaterial = Material.REDSTONE_ORE;
                    String signText = "unknown";
                    ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(block.getType());
                    ActionCategory actionCategory = ActionCategory.getByMaterial(block.getType());
                    if (executorCategory != null) {
                        signText = executorCategory.name().toLowerCase();
                        additionalBlockMaterial = executorCategory.getAdditionalBlock();
                    } else if (actionCategory != null) {
                        signText = actionCategory.name().toLowerCase();
                        additionalBlockMaterial = actionCategory.getAdditionalBlock();
                    }
                    placeDevBlock(block, additionalBlockMaterial, signText);
                } else {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-action-on-event"));
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else if (blockAgainst.getType() == platform.getActionMaterial()) {
                if (devPlanet.getActionsBlocks().contains(block.getType())) {
                    Material additionalBlockMaterial = Material.REDSTONE_ORE;
                    String signText = "unknown";
                    ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(block.getType());
                    ActionCategory actionCategory = ActionCategory.getByMaterial(block.getType());
                    if (executorCategory != null) {
                        signText = executorCategory.name().toLowerCase();
                        additionalBlockMaterial = executorCategory.getAdditionalBlock();
                    } else if (actionCategory != null) {
                        signText = actionCategory.name().toLowerCase();
                        additionalBlockMaterial = actionCategory.getAdditionalBlock();
                    }
                    if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                        move(block.getLocation(), BlockFace.EAST);
                    }
                    placeDevBlock(block, additionalBlockMaterial, signText);
                } else {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-event-on-action"));
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else {
                if (block.getType() != Material.COMPARATOR) {
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 1.2f);
                }
                event.setCancelled(true);
            }
        } else if (planet != null) {
            if (ChangedWorld.isPlayerWithLocation(player) && !planet.getWorldPlayers().canBuild(player)) {
                player.sendActionBar(getLocaleMessage("not-builder"));
                event.setCancelled(true);
                return;
            }
            EventRaiser.raisePlaceBlockEvent(event.getPlayer(),event);
        }
    }

    public static void placeDevBlock(Block block, Material additionalBlockMaterial, String signText) {
        Block eastBlock = block.getRelative(BlockFace.EAST);
        eastBlock.setType(additionalBlockMaterial);
        if (eastBlock.getType() == Material.PISTON) {
            Directional data = (Directional) eastBlock.getBlockData();
            data.setFacing(BlockFace.EAST);
            eastBlock.setBlockData(data);
            Block farEastBlock = eastBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
            move(eastBlock.getLocation(),BlockFace.EAST);
            farEastBlock.setType(Material.PISTON);
            data = (Directional) farEastBlock.getBlockData();
            data.setFacing(BlockFace.WEST);
            farEastBlock.setBlockData(data);
        }

        Block wallSign = block.getRelative(BlockFace.SOUTH);
        wallSign.setType(Material.OAK_WALL_SIGN);

        Sign sign = (Sign) wallSign.getState();
        sign.setLine(1, signText);
        if (block.getType() == Material.OXIDIZED_COPPER) {
            sign.setLine(2,"20");
        }
        if (block.getType() == Material.PURPUR_BLOCK) {
            sign.setLine(1,"");
            sign.setLine(3,"selection_set");
        }
        sign.update();

        translateBlockSign(wallSign);

        Directional data = (Directional) wallSign.getBlockData();
        data.setFacing(BlockFace.SOUTH);
        wallSign.setBlockData(data);
    }

    public static boolean move(Location location, BlockFace face) {
        DevPlanet devPlanet = PlanetManager.getInstance().getDevPlanet(location.getWorld());
        if (devPlanet == null) return false;
        DevPlatform platform = devPlanet.getPlatformInLocation(location);
        if (platform == null) return false;
        if (face == BlockFace.EAST) {
            /*
             Moves blocks to right
             */
            if (location.getX() >= platform.getEndX()-4) return false;
            Set<Block> movedBlocks = new HashSet<>();
            for (double x = platform.getEndX()-5; x > location.getX(); x--) {
                Block oldBlock = location.getWorld().getBlockAt((int) x, location.getBlockY(), location.getBlockZ());
                if (oldBlock.getType() == Material.AIR) continue;
                if (!movedBlocks.contains(oldBlock)) {
                    Block newBlock = location.getWorld().getBlockAt((int) x + 2, location.getBlockY(), location.getBlockZ());
                    moveCodingBlock(oldBlock,newBlock);
                    movedBlocks.add(newBlock);
                }
            }
        } else if (face == BlockFace.WEST) {
            /*
             Moves blocks to left.
             */
            if (location.getX() <= platform.getBeginX()+5) return false;
            if (!location.getBlock().isEmpty()) return false;
            //if (!location.getBlock().getRelative(BlockFace.WEST).isEmpty()) return false;
            Set<Block> movedBlocks = new HashSet<>();
            for (double x = location.getX()+1; x < platform.getEndX(); x++) {
                Block oldBlock = location.getWorld().getBlockAt((int) x, location.getBlockY(), location.getBlockZ());
                if (oldBlock.getType() == Material.AIR) continue;
                if (!movedBlocks.contains(oldBlock)) {
                    Block newBlock = location.getWorld().getBlockAt((int) x-2, location.getBlockY(), location.getBlockZ());
                    moveCodingBlock(oldBlock,newBlock);
                    movedBlocks.add(oldBlock);
                }
            }
        }
        return true;
    }

    /**
     * Moves coding block with wall sign and container to next block.
     * @param oldBlock Block to move and replace with air.
     * @param newBlock Block where to move.
     */
    public static void moveCodingBlock(Block oldBlock, Block newBlock) {
        Block oldSignBlock = oldBlock.getRelative(BlockFace.SOUTH);
        Block oldContainerBlock = oldBlock.getRelative(BlockFace.UP);

        Block newSignBlock = newBlock.getRelative(BlockFace.SOUTH);
        Block newContainerBlock = newBlock.getRelative(BlockFace.UP);

        newBlock.setType(oldBlock.getType());
        newBlock.setBlockData(oldBlock.getBlockData());

        if (oldSignBlock.getType().toString().contains("WALL_SIGN")) {
            Sign oldSign = (Sign) oldSignBlock.getState();
            newSignBlock.setType(Material.OAK_WALL_SIGN);
            Sign sign = (Sign) newSignBlock.getState();
            for (byte i = 0; i < oldSign.getSide(Side.FRONT).lines().size(); i++) {
                sign.getSide(Side.FRONT).line(i,oldSign.getSide(Side.FRONT).line(i));
            }
            sign.getSide(Side.FRONT).setGlowingText(oldSign.getSide(Side.FRONT).isGlowingText());
            sign.setBlockData(oldSign.getBlockData());
            sign.update();
            translateBlockSign(newSignBlock);
        }
        if (oldContainerBlock.getState() instanceof InventoryHolder container) {
            newContainerBlock.setType(oldContainerBlock.getType());
            newContainerBlock.setBlockData(oldContainerBlock.getBlockData());
            DevPlanet devPlanet = PlanetManager.getInstance().getDevPlanet(oldContainerBlock.getWorld());
            if (devPlanet != null) {
                Layout layout = devPlanet.getOpenedMenu(oldContainerBlock.getLocation());
                if (layout != null) {
                    for (Player player : layout.getViewers()) {
                        player.closeInventory();
                    }
                }
            }
            if (newContainerBlock.getState() instanceof InventoryHolder newContainer) {
                newContainer.getInventory().setContents(container.getInventory().getContents());
            }
        }
        oldSignBlock.setType(Material.AIR);
        oldBlock.setType(Material.AIR);
        oldContainerBlock.setType(Material.AIR);
    }
}

