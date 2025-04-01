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

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.VariableTransferEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.Random;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.sendMessageOnce;

public final class AprilFoolsAttackWorldAction extends WorldAction {
    public AprilFoolsAttackWorldAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        String worldId = getArguments().getValue("world","0",this);
        Planet planet = OpenCreative.getPlanetsManager().getPlanetById(worldId);
        if (planet == null) return;
        if (!planet.isLoaded()) return;
        if (!planet.isOwner(getPlanet().getOwner())) return;
        int random = new Random().nextInt(1,11);
        if (new AprilFoolsWorldAttackedEvent(planet,String.valueOf(getPlanet().getId())).callEvent()) {
            TextComponent warning = new TextComponent(getLocaleMessage("april-fools.attacks." + random,false));
            warning.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("april-fools.attacks.hint"))));
            sendMessageOnce(planet,warning,3);
            if (planet.getId() == getPlanet().getId()) {
                for (Player player : getPlanet().getPlayers()) {
                    player.sendMessage(getLocaleMessage("april-fools.attacks.self"));
                }
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.APRIL_FOOLS_ATTACK_WORLD;
    }
}
