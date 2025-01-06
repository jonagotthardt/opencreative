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

package ua.mcchickenstudio.opencreative.coding.test;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class DefaultCodingPack extends CodingPack {

    public DefaultCodingPack() {
        super("default",
                "Default pack",
                "This coding pack is default for OpenCreative+.",
                new String[]{"opencreative"},
                1);
    }

    @Override
    public Executor[] getCustomExecutors() {
        return new Executor[]{};
    }

    @Override
    public Action[] getCustomActions() {
        return new Action[0];
    }

    @Override
    public EventValues.Variable[] getCustomValues() {
        return new EventValues.Variable[0];
    }
}
