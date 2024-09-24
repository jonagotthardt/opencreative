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

package mcchickenstudio.creative.commands.minecraft;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandGamemode implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            /*
             * If sender is console, then replace with default /minecraft:gamemode command
             */
            Bukkit.getServer().dispatchCommand(sender, "minecraft:gamemode " + String.join(" ", args));
            return true;
        }
        int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
        if (cooldown > 0) {
            sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
            return true;
        }
        setCooldown(player, Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
        if (!player.hasPermission("creative.game-mode.bypass")) {
            /*
             * Checking is player owner, builder or developer of world.
             * If not, he can't change his game mode.
             */
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (!(plot.isOwner(player) || plot.getWorldPlayers().canDevelop(player) || plot.getWorldPlayers().canBuild(player))) {
                player.sendMessage(getLocaleMessage("not-owner"));
                return true;
            }
            /*
             * Players should not change game mode in developer world,
             * because it's work depends on game mode.
             */
            if (PlotManager.getInstance().getDevPlot(player) != null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
        }
        if (args.length == 1) {
            /*
             * Example: /gamemode survival
             */
            GameMode mode = null;
            switch (args[0]) {
                case "0" -> mode = GameMode.SURVIVAL;
                case "1" -> mode = GameMode.CREATIVE;
                case "2" -> mode = GameMode.ADVENTURE;
                case "3" -> mode = GameMode.SPECTATOR;
            }
            try {
                if (mode == null) {
                    mode = GameMode.valueOf(args[0].toUpperCase());
                }
                player.setGameMode(mode);
            } catch (IllegalArgumentException error) {
                player.sendMessage(getLocaleMessage("commands.game-mode.wrong"));
            }

        } else if (args.length == 2) {
            /*
             * Example: /gamemode survival PlayerName
             */
            GameMode mode = null;
            switch (args[0]) {
                case "0" -> mode = GameMode.SURVIVAL;
                case "1" -> mode = GameMode.CREATIVE;
                case "2" -> mode = GameMode.ADVENTURE;
                case "3" -> mode = GameMode.SPECTATOR;
            }
            try {
                if (mode == null) {
                    mode = GameMode.valueOf(args[0].toUpperCase());
                }
                Player modePlayer = Bukkit.getPlayer(args[1]);
                if (modePlayer == null) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return true;
                } else {
                    /*
                     * Check player's, that will receive new game mode, world.
                     * If players' world is not same as sender's world game mode
                     * will be not changed.
                     */
                    Plot modePlot = PlotManager.getInstance().getPlotByPlayer(modePlayer);
                    if (!player.hasPermission("creative.game-mode.bypass")) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (plot == null || !plot.equals(modePlot)) {
                            player.sendMessage(getLocaleMessage("no-player-found"));
                            return true;
                        }
                        if (PlotManager.getInstance().getDevPlot(modePlayer) != null) {
                            player.sendMessage(getLocaleMessage("only-in-world"));
                            return true;
                        }
                    }
                }
                modePlayer.setGameMode(mode);
            } catch (IllegalArgumentException e) {
                player.sendMessage(getLocaleMessage("commands.game-mode.wrong"));
            }
        } else {
            sender.sendMessage(getLocaleMessage("commands.game-mode.help"));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                tabCompleter.addAll(Arrays.stream(GameMode.values()).map(gameMode -> gameMode.name().toLowerCase()).toList());
            } else if (args.length == 2) {
                tabCompleter.addAll(player.getWorld().getPlayers().stream().map(Player::getName).toList());
            }
        }
        return tabCompleter;
    }

}
