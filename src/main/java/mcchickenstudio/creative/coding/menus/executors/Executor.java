/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*//*


package mcchickenstudio.creative.coding.menus.executors;

import mcchickenstudio.creative.coding.blocks.CodeBlock;
import mcchickenstudio.creative.coding.menus.actions.Action;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public abstract class Executor implements CodeBlock {

    private final Location location;
    private List<Action> actions;

    public Executor(Location location) {
        this.location = location;
    }

    public void executeActions(Entity entity) {
        if (actions == null) return;
        for (Action action : actions) {
            action.execute(this,entity);
        }
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Location getLocation() {
        return location;
    }

    public List<Action> getActions() {
        return actions;
    }

}
*/
