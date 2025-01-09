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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.communication;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PlaySoundAction extends PlayerAction {

    public PlaySoundAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(Player player) {
        String sound = getArguments().getValue("sound","entity.player.levelup",this);
        ItemStack musicDisc = getArguments().getValue("sound",new ItemStack(Material.AIR),this);
        if (musicDisc.getType() != Material.AIR && musicDisc.getType().name().contains("MUSIC_DISC")) {
            sound = musicDisc.getType().name().toLowerCase().replace("music_disc_","music_disc.");
        }
        float volume = getArguments().getValue("volume",100f,this);
        float pitch = getArguments().getValue("pitch",1f,this);
        String categoryString = getArguments().getValue("category","ambient",this);
        Location location;
        SoundCategory category;
        try {
            category = SoundCategory.valueOf(categoryString.toUpperCase());
        } catch (Exception error) {
            category = SoundCategory.AMBIENT;
        }
        location = getArguments().getValue("location",player.getLocation(),this);
        player.playSound(location,sound,category,volume,pitch);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_PLAY_SOUND;
    }
}
