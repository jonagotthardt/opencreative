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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.inventory;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class EntityRemoveItemsAction extends EntityAction {
    public EntityRemoveItemsAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void execute(Entity entity) {
        if (entity instanceof InventoryHolder holder) {
            for (ItemStack item : getArguments().getItemList("items",this)) {
                holder.getInventory().removeItemAnySlot(item);
            }
        } else if (entity instanceof LivingEntity living && living.getEquipment() != null) {
            ItemStack[] armor = living.getEquipment().getArmorContents();
            for (ItemStack item : getArguments().getItemList("items", this)) {
                for (int i = 0; i < armor.length; i++) {
                    if (armor[i] != null && armor[i].isSimilar(item)) {
                        armor[i] = null;
                    }
                }
            }
            living.getEquipment().setArmorContents(armor);
        }

    }

    @Override
    public ActionType getActionType() {
        return ActionType.ENTITY_REMOVE_ITEMS;
    }
}
