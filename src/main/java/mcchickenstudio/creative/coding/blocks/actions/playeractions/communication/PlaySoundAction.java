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

package mcchickenstudio.creative.coding.blocks.actions.playeractions.communication;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaySoundAction extends PlayerAction {

    public PlaySoundAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    public void execute(List<Entity> selection) {
        String sound = getArguments().getValue("sound","entity.player.levelup");
        float volume = getArguments().getValue("volume",100f);
        float pitch = getArguments().getValue("pitch",1f);
        String categoryString = getArguments().getValue("category","ambient");
        Location location;
        SoundCategory category;
        try {
            category = SoundCategory.valueOf(categoryString.toUpperCase());
        } catch (Exception error) {
            category = SoundCategory.AMBIENT;
        }
        for (Player player : getPlayers(selection)) {
            location = getArguments().getValue("location",player.getLocation());
            player.playSound(location,sound,category,volume,pitch);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_PLAY_SOUND;
    }
}
