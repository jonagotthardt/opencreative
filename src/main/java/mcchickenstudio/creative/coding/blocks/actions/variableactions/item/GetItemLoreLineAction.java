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

package mcchickenstudio.creative.coding.blocks.actions.variableactions.item;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.VariableAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.debug.values.VariableLink;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GetItemLoreLineAction extends VariableAction {
    public GetItemLoreLineAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        VariableLink link = getArguments().getVariableLink("variable",this);
        ItemStack item = getArguments().getValue("item",getArguments().getValue("variable",new ItemStack(Material.APPLE),this),this);
        int index = getArguments().getValue("index",1,this);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        List<String> lore = meta.getLore();
        if (lore == null) {
            setVarValue(link,null);
            return;
        }
        if (lore.size() > 64 || index > 64) {
            setVarValue(link,null);
            return;
        }
        if (index > lore.size()) {
            setVarValue(link,null);
            return;
        }
        setVarValue(link,lore.get(index-1));
    }

    @Override
    public ActionType getActionType() {
        return ActionType.VAR_GET_ITEM_LORE_LINE;
    }
}
