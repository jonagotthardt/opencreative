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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.blocks;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.variables.VariableLink;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class GetSignLineAction extends WorldAction {
    public GetSignLineAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        VariableLink variable = getArguments().getVariableLink("variable",this);
        Location location = getArguments().getValue("location",getWorld().getSpawnLocation(),this);
        int number = getArguments().getValue("number",1,this);
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign sign)) return;
        String sideString = getArguments().getValue("side","front",this);
        Side side = (sideString.equals("back") ? Side.BACK : Side.FRONT);
        if (number <= 0 || number > sign.getSide(side).lines().size()) number = 1;
        setVarValue(variable,sign.getSide(side).getLine(number-1));
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_GET_SIGN_LINE;
    }
}
