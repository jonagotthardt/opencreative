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

package mcchickenstudio.creative.coding.config;

import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;

import java.util.ArrayList;
import java.util.List;

public class ConfigExecutor {

    private final ExecutorCategory category;
    private final ExecutorType type;
    private final List<ConfigAction> actions = new ArrayList<>();

    ConfigExecutor(ExecutorCategory category, ExecutorType type) {
        this.category = category;
        this.type = type;
    }

    public void addAction(ConfigAction action) {
        actions.add(action);
    }

    public void setActions(List<ConfigAction> actions) {
        for (ConfigAction action : actions) {
            addAction(action);
        }
    }



}
