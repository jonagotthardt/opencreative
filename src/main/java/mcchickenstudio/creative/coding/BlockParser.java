/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.coding;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import mcchickenstudio.creative.plots.DevPlot;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.ErrorUtils.sendPlotCompileErrorMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class BlockParser {

    public void parseCode(DevPlot devPlot) {

        World world = devPlot.world;
        CodeScript script = devPlot.linkedPlot.script;
        script.clear();

        // For floors
        for (byte y = 1; y < devPlot.getFloors()*4; y=(byte)(y+4)) {

            // For code lines
            for (byte z = 4; z < 96; z = (byte)(z+4)) {

                Block executorBlock = world.getBlockAt(4,y,z);
                String executorType = "";
                String executorSubtype = getSubtype(executorBlock);
                switch(executorBlock.getType()) {
                    case DIAMOND_BLOCK:
                        executorType = "event_player";
                        break;
                }
                if (!executorType.isEmpty() && !executorSubtype.isEmpty()) {
                    script.setExecBlock(executorBlock,executorType,executorSubtype);
                } else {
                    continue;
                }
                List<String> conditions = new ArrayList<>();
                for (byte x = 6; x < 96; x= (byte) (x+2)) {

                    Block actionBlock = world.getBlockAt(x,y,z);
                    String actionType = "";
                    String actionSubtype = getSubtype(actionBlock);

                    int parameter = 1;
                    Block container = actionBlock.getRelative(BlockFace.UP);

                    switch (actionBlock.getType()) {
                        case COBBLESTONE: {
                            actionType = "action_player";
                            break;
                        }
                        case OAK_PLANKS: {
                            actionType = "if_player";
                            conditions.add("condition_block_" + script.getBlockActionNumber(actionBlock));
                            break;
                        }
                        case AIR: {
                            if (world.getBlockAt(x+1,y,z).getType() == Material.PISTON) {
                                if (!conditions.isEmpty()) {
                                    String last = conditions.get(conditions.size()-1);
                                    conditions.remove(last);
                                } else {
                                    sendPlotCompileErrorMessage(devPlot.linkedPlot,actionBlock,getLocaleMessage("plot-code-error.bad-piston"));
                                    return;
                                }
                            }
                        }
                    }

                    if (!actionType.isEmpty() && !actionSubtype.isEmpty()) {

                        List<String> arguments;
                        if (actionSubtype.equalsIgnoreCase("give_items")) {
                            arguments = new ArrayList<>(parseChestArguments(container, true));
                        } else {
                            arguments = new ArrayList<>(parseChestArguments(container, false));
                        }

                        if (!(actionBlock.getType() == Material.OAK_PLANKS)) {
                            script.setActionBlock(conditions,executorBlock,actionBlock,actionType,actionSubtype,parameter,arguments);
                        } else {
                            script.setConditionBlock(conditions,executorBlock,actionBlock,actionType,actionSubtype,parameter,arguments);
                        }

                    }

                }
            }
        }
        devPlot.linkedPlot.script.loadCode();
    }

    private List<String> parseChestArguments(Block chest, boolean returnItemStacks) {
        List<String> arguments = new ArrayList<>();
        if (chest.getType() == Material.CHEST) {
            Chest chestB = (Chest) chest.getState();
            for (ItemStack item : chestB.getBlockInventory().getContents()) {
                if (item != null) {
                    if (!returnItemStacks) {
                        if (item.getType() == Material.SLIME_BALL) {
                            arguments.add(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        } else if (item.getType() == Material.BOOK) {
                            arguments.add(item.getItemMeta().getDisplayName());
                        } else {
                            arguments.add(formatItemStack(item));
                        }
                    } else {
                        arguments.add(formatItemStack(item));
                    }
                } else {
                    arguments.add("");
                }
            }
        }
        return arguments;
    }

    private String formatItemStack(ItemStack item) {
        StringBuilder lore = new StringBuilder();
        if (item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
            for (String loreString : item.getItemMeta().getLore()) {
                lore.append("\\n").append(loreString);
            }
        }
        return "ItemStack::" + item.getType() + "::" + item.getAmount() + "::" + item.getItemMeta().getDisplayName() + "::" + lore;
    }

    public String getMainTypeByMaterial(Material material) {
        switch(material) {
            case DIAMOND_BLOCK:
                return "event_player";
            case GOLD_BLOCK:
                return "event_world";
            case LAPIS_BLOCK:
                return "function";
            case EMERALD_BLOCK:
                return "cycle";
            default:
                return "unknown";
        }
    }

    public String getActionBlockType(Material material) {
        switch(material) {
            case COBBLESTONE:
                return "action_player";
            case IRON_BLOCK:
                return "action_var";
            case NETHER_BRICKS:
                return "action_world";
            case LAPIS_ORE:
                return "exec_function";
            case OAK_PLANKS:
                return "if_player";
            default:
                return "unknown";
        }
    }

    public String getSubtype(Block block) {
         Block signBlock = block.getRelative(BlockFace.SOUTH);
         if (signBlock.getType().toString().contains("WALL_SIGN")) {
             Sign sign = (Sign) signBlock.getState();
             if (sign.lines().size() >= 3) {
                 Component signText = sign.line(2);
                 return ((TextComponent) signText).content();
             }
         }
         return "";
    }
}
