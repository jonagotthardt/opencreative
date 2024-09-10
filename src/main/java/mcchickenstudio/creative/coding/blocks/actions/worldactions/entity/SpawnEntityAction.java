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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.entity;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class SpawnEntityAction extends WorldAction {
    public SpawnEntityAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {

        String typeString = "chicken";
        String customName = getArguments().getValue("name","",this);

        boolean ai = getArguments().getValue("ai",true,this);
        boolean gravity = getArguments().getValue("gravity",true,this);
        boolean glowing = getArguments().getValue("glowing",false,this);
        boolean invisible = getArguments().getValue("invisible",false,this);
        boolean invulnerable = getArguments().getValue("invulnerable",false,this);
        boolean customNameVisible = getArguments().getValue("show-name",true,this);
        boolean visibleByDefault = getArguments().getValue("visible-for-all",true,this);

        ItemStack spawnEgg = getArguments().getValue("type",new ItemStack(Material.AIR),this);
        if (spawnEgg.getType() != Material.AIR && spawnEgg.getType().name().endsWith("_SPAWN_EGG")) {
            typeString = spawnEgg.getType().name().replace("_SPAWN_EGG","");
        } else {
            typeString = getArguments().getValue("type","chicken",this);
        }
        EntityType type;
        try {
            type = EntityType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            type = EntityType.CHICKEN;
        }
        for (Location location : getArguments().getLocationList("locations",this)) {
            Entity spawnedEntity = getPlot().world.spawnEntity(location,type);

            spawnedEntity.setGravity(gravity);
            if (!customName.isEmpty()) {
                spawnedEntity.setCustomName(customName);
            }
            spawnedEntity.setGlowing(glowing);
            spawnedEntity.setInvisible(invisible);
            spawnedEntity.setInvulnerable(invulnerable);
            spawnedEntity.setCustomNameVisible(customNameVisible);
            spawnedEntity.setVisibleByDefault(visibleByDefault);

            if (spawnedEntity instanceof LivingEntity living) {
                living.setAI(ai);
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SPAWN_ENTITY;
    }
}
