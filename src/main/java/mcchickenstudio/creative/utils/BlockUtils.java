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

package mcchickenstudio.creative.utils;

import mcchickenstudio.creative.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>BlockUtils</h1>
 * This class represents useful utils for manipulating with blocks.
 */
public class BlockUtils {

    private BlockUtils() {}

    /**
     * Change text into line on sign.
     * @param location Location of sign block
     * @param line Number of sign line (1-4)
     * @param text Text to set in sign
     * @return true - if sign line changed successful, false - if failed
     */
    public static boolean setSignLine(Location location, byte line, String text) {
        Block block = location.getBlock();
        if (line < 1 || line > 4) return false;
        if (!(block.getState() instanceof Sign)) return false;
        Sign sign = (Sign) block.getState();
        SignSide side = sign.getSide(Side.FRONT);
        side.line(line-1,Component.text(text));
        sign.update();
        return true;
    }

    /**
     * Get text from line in sign block
     * @param location Location of sign block
     * @param line Number of sign line (1-4)
     * @return Text from sign line
     */
    public static String getSignLine(Location location, byte line) {
        Block block = location.getBlock();
        if (line < 1 || line > 4) return null;
        if (!(block.getState() instanceof Sign)) return null;
        Sign sign = (Sign) block.getState();
        SignSide side = sign.getSide(Side.FRONT);
        TextComponent textComponent = (TextComponent) side.line(line-1);
        return textComponent.content();
    }

    public static boolean sendSignChange(Location location, Player player, byte lineNumber, String newLine) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign)) return false;
        Sign sign = (Sign) block.getState();
        List<Component> newLines = sign.getSide(Side.FRONT).lines();
        newLines.set(lineNumber-1,Component.text(newLine));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendSignChange(block.getLocation(), newLines);
            }
        }.runTaskLater(Main.getPlugin(),5L);
        return true;
    }

    public static int getClosingBracketX(Block conditionBlock) {
        Location location = conditionBlock.getLocation();
        World world = location.getWorld();

        List<String> conditions = new ArrayList<>();
        try {
            for (byte x = (byte) (location.getX()+2); x < 96; x= (byte) (x+2)) {
                Block block = world.getBlockAt(new Location(world,x,location.getBlockY(),location.getBlockZ()));
                if (block.getType() == Material.AIR) {
                    if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                        if (!conditions.isEmpty()) {
                            String last = conditions.get(conditions.size()-1);
                            conditions.remove(last);
                        } else {
                            return block.getRelative(BlockFace.EAST).getX();
                        }
                    }
                } else if (block.getType() == Material.OAK_PLANKS) {
                    if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                        conditions.add("cound" + block.getX());
                    }
                }
            }
        } catch (Exception exception) {
            return -1;
        }
        return -1;
    }

}
