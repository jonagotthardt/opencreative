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

package ua.mcchickenstudio.opencreative.commands.world.reputation;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
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
            Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(MessageUtils.getLocaleMessage("only-in-world"));
                return true;
            }
            if (CooldownUtils.getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(MessageUtils.getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(CooldownUtils.getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            CooldownUtils.setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (FileUtils.getPlayersFromPlanetList(planet, Planet.PlayersType.LIKED).contains(sender.getName())) {
                sender.sendMessage(MessageUtils.getLocaleMessage("world.already-rated"));
            } else if (FileUtils.getPlayersFromPlanetList(planet, Planet.PlayersType.DISLIKED).contains(sender.getName())) {
                sender.sendMessage(MessageUtils.getLocaleMessage("world.already-rated"));
            } else {
                if (FileUtils.addPlayerInPlanetList(planet,sender.getName(), Planet.PlayersType.DISLIKED)) {
                    planet.getInformation().setPlanetReputation(planet.getInformation().getReputation() -1);
                    player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,0.7f);
                    sender.sendMessage(MessageUtils.getLocaleMessage("world.disliked",player));
                }
            }
        }
        return true;
    }

}
