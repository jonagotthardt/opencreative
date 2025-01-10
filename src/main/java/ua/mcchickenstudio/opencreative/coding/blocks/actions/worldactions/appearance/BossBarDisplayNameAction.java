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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.appearance;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;

public final class BossBarDisplayNameAction extends WorldAction {
    public BossBarDisplayNameAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        if (!getArguments().pathExists("name")) {
            return;
        }
        String name = getArguments().getValue("name","boss",this);
        String displayName = getArguments().getValue("display-name"," ",this);
        BossBar bossBar = getPlanet().getTerritory().getBossBars().get(name.toLowerCase());
        if (bossBar != null) {
            bossBar.name(Component.text(displayName));
        }
        getPlanet().getTerritory().getBossBars().put(name.toLowerCase(),bossBar);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_BOSS_BAR_DISPLAY_NAME;
    }
}
