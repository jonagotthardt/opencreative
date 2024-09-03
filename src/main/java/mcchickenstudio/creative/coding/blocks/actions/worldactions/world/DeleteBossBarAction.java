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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.world;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugLog;

public class DeleteBossBarAction extends WorldAction {
    public DeleteBossBarAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        if (!getArguments().pathExists("name")) {
            return;
        }
        String name = getArguments().getValue("name","boss",this);
        BossBar bossBar = getPlot().getBossBars().get(name.toLowerCase());
        if (bossBar != null) {
            getPlot().world.audiences().forEach(bossBar::removeViewer);
            getPlot().getBossBars().remove(name.toLowerCase());
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_DELETE_BOSS_BAR;
    }
}
