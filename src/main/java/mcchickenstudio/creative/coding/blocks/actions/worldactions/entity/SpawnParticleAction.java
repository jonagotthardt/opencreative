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
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

public class SpawnParticleAction extends WorldAction {
    public SpawnParticleAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        Particle particle = getArguments().getValue("particle",Particle.HEART,this);
        int count = Math.min(30,getArguments().getValue("count",1,this));
        double offsetX = getArguments().getValue("offset-x",0.0d,this);
        double offsetY = getArguments().getValue("offset-y",0.0d,this);
        double offsetZ = getArguments().getValue("offset-z",0.0d,this);
        for (Location location : getArguments().getLocationList("locations",this)) {
            getPlot().getWorld().spawnParticle(particle,location,count,offsetX,offsetY,offsetZ);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SPAWN_PARTICLE;
    }
}
