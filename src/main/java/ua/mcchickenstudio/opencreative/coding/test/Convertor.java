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

package ua.mcchickenstudio.opencreative.coding.test;

import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.plots.DevPlatform;
import ua.mcchickenstudio.opencreative.plots.DevPlot;
import ua.mcchickenstudio.opencreative.plots.Plot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getElapsedTime;

/**
 * <h1>Convertor</h1>
 * This class represents a convertor, that changes coding blocks
 * in specified developer's plots to required one.
 */
public abstract class Convertor {

    private final List<Plot> plots;
    private final String description;
    private final BukkitRunnable runnable;
    private final int MAX_AWAITING_TIME = 10;

    private boolean isRunning;
    private int convertedPlotsAmount = 0;
    private long launchTime;

    public Convertor(String description, List<Plot> plots) {
        this.plots = plots;
        this.description = description;
        this.runnable = new BukkitRunnable() {


            private Plot currentPlot = plots.getFirst();
            private int wastedTime = 0;
            private boolean converting = false;

            @Override
            public void run() {
                wastedTime += 1;
                if (wastedTime > MAX_AWAITING_TIME) {
                    if (!next()) return;
                }
                if (!currentPlot.getDevPlot().exists()) {
                    if (!next()) return;
                }
                if (!converting) {
                    if (!currentPlot.getDevPlot().isLoaded()) {
                        currentPlot.getTerritory().load();
                        currentPlot.getDevPlot().loadDevPlotWorld();
                    }
                    converting = true;
                    if (convertDevPlot(currentPlot.getDevPlot())) {
                        convertedPlotsAmount++;
                        if (!next()) return;
                    }
                }
            }

            public boolean next() {
                plots.remove(currentPlot);
                if (plots.isEmpty()) {
                    end();
                    currentPlot = null;
                    return false;
                }
                resetValues();
                currentPlot = plots.getFirst();
                return true;
            }

            public void resetValues() {
                wastedTime = 0;
                converting = false;
            }
        };
    }

    /**
     * Changes coding block's sign lines or container content to required one.
     * @param mainBlock main part of coding block.
     * @param containerLocation location of container.
     * @param container inventory of container.
     * @param signLocation location of sign.
     * @param first first sign line.
     * @param second second sign line.
     * @param third third sign line.
     * @param fourth fourth sign line.
     * @return true - if converted, false - if not converted.
     */
    public abstract boolean convertCodingBlock(@NotNull Block mainBlock, @NotNull Location containerLocation, @Nullable InventoryHolder container, @NotNull Location signLocation, @NotNull String first, @NotNull String second, @NotNull String third, @NotNull String fourth);

    /**
     * Starts convertor process.
     */
    public void start() {
        if (isRunning) return;
        isRunning = true;
        launchTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Starting developers plots convertor process...");
        OpenCreative.getPlugin().getLogger().info(" ");
        OpenCreative.getPlugin().getLogger().info(" Description: " + description);
        OpenCreative.getPlugin().getLogger().info(" Plots to be converted: " + plots.size());
        OpenCreative.getPlugin().getLogger().info(" ");
        runnable.runTaskTimer(OpenCreative.getPlugin(),20L,20L);
    }

    public void end() {
        if (!isRunning) return;
        isRunning = false;
        runnable.cancel();
        OpenCreative.getPlugin().getLogger().info("Converting is finished.");
        OpenCreative.getPlugin().getLogger().info(" ");
        OpenCreative.getPlugin().getLogger().info(" Converted plots: " + convertedPlotsAmount);
        OpenCreative.getPlugin().getLogger().info(" Time wasted: " + getElapsedTime(System.currentTimeMillis(),launchTime));
        OpenCreative.getPlugin().getLogger().info(" ");
    }

    public boolean convertDevPlot(DevPlot devPlot) {
        boolean converted = false;
        World world = devPlot.getWorld();
        for (DevPlatform platform : devPlot.getPlatforms()) {
            for (int z = platform.getBeginZ()+4; z <= platform.getEndZ()-4; z = z+4) {
                for (int x = platform.getBeginX()+4; x <= platform.getEndZ()-4; x = x + 2) {
                    Block codingBlock = world.getBlockAt(x, 1, z);
                    Block containerBlock = world.getBlockAt(x, 2, z);
                    Location signLocation = codingBlock.getRelative(BlockFace.SOUTH).getLocation();
                    String firstSignLine = getSignLine(signLocation,(byte) 1);
                    String secondSignLine = getSignLine(signLocation,(byte) 2);
                    String thirdSignLine = getSignLine(signLocation,(byte) 3);
                    String fourthSignLine = getSignLine(signLocation,(byte) 4);
                    InventoryHolder container = (containerBlock.getState() instanceof InventoryHolder inventory ? inventory : null);
                    converted = convertCodingBlock(codingBlock,containerBlock.getLocation(),container,signLocation,firstSignLine,secondSignLine,thirdSignLine,fourthSignLine);
                    devPlot.getPlot().getTerritory().getScript().clear();
                    devPlot.getPlot().getTerritory().getScript().saveCode();
                }
            }
        }
        devPlot.getPlot().getTerritory().unload();
        return converted;
    }

    public int getConvertedPlotsAmount() {
        return convertedPlotsAmount;
    }
}
