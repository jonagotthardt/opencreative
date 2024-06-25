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

package mcchickenstudio.creative.coding.blocks.actions.playeractions.inventory;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenSignAction extends PlayerAction {
    public OpenSignAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    protected void execute(List<Entity> selection) {
        Location location = getArguments().getValue("location",getWorld().getSpawnLocation());
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign)) return;
        Sign sign = (Sign) block.getState();
        String sideString = getArguments().getValue("side","front");
        Side side = (sideString.equals("back") ? Side.BACK : Side.FRONT);
        for (Player player : getPlayers(selection)) {
            player.openSign(sign,side);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_OPEN_SIGN;
    }
}
