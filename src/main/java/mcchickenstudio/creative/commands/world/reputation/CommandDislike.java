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

package mcchickenstudio.creative.commands.world.reputation;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.utils.FileUtils;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDislike implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot == null) {
                player.sendMessage(MessageUtils.getLocaleMessage("only-in-world"));
                return true;
            }
            if (CooldownUtils.getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(MessageUtils.getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(CooldownUtils.getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            CooldownUtils.setCooldown(player, Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (FileUtils.getPlayersFromPlotList(plot, Plot.PlayersType.LIKED).contains(sender.getName())) {
                sender.sendMessage(MessageUtils.getLocaleMessage("world.already-rated"));
            } else if (FileUtils.getPlayersFromPlotList(plot, Plot.PlayersType.DISLIKED).contains(sender.getName())) {
                sender.sendMessage(MessageUtils.getLocaleMessage("world.already-rated"));
            } else {
                if (FileUtils.addPlayerInPlotList(plot,sender.getName(), Plot.PlayersType.DISLIKED)) {
                    plot.getInformation().setPlotReputation(plot.getInformation().getReputation() -1);
                    player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,0.7f);
                    sender.sendMessage(MessageUtils.getLocaleMessage("world.disliked",player));
                }
            }
        }
        return true;
    }

}
