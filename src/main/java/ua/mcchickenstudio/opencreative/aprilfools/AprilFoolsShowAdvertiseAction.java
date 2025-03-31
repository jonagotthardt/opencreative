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

package ua.mcchickenstudio.opencreative.aprilfools;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.Random;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class AprilFoolsShowAdvertiseAction extends PlayerAction {
    public AprilFoolsShowAdvertiseAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(Player player) {
        Random random = new Random();
        int advertise = random.nextInt(1,11);
        player.sendMessage(getLocaleMessage("april-fools.advertisements." + advertise + ".message",false));
        player.sendTitle(
                getLocaleMessage("april-fools.advertisements." + advertise + ".title",false),
                getLocaleMessage("april-fools.advertisements." + advertise + ".subtitle",false)
        );
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,0.1f);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.APRIL_FOOLS_SHOW_ADVERTISEMENT;
    }
}
