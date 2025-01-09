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

package ua.mcchickenstudio.opencreative.coding.test;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.LinkedHashSet;
import java.util.Set;

// in development!!!

/**
 * <h1>CodingPack</h1>
 * This class represents a coding pack that contains
 * custom-made executors, actions and event values.
 */
public abstract class CodingPack {

    private final String id;
    private final String displayName;
    private final String description;
    private final String[] authors;
    private final int version;

    public CodingPack(String id, String displayName, String description, String[] authors, int version) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.authors = authors;
        this.version = version;
    }

    public abstract Executor[] getCustomExecutors();

    public abstract Action[] getCustomActions();

    public abstract EventValues.Variable[] getCustomValues();

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAuthors() {
        return authors;
    }

    public int getVersion() {
        return version;
    }
}
