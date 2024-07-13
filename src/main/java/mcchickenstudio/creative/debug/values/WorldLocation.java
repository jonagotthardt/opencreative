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

package mcchickenstudio.creative.debug.values;

import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class WorldLocation implements CodingValue {

    private final Plot plot;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Location location;

    public WorldLocation(Plot plot, double x, double y, double  z, float yaw, float pitch) {
        this.plot = plot;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.location = new Location(plot.world,x,y,z,yaw,pitch);
    }

    @Override
    public ValueType getType() {
        return ValueType.LOCATION;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof WorldLocation worldLocation) {
            this.x = worldLocation.x;
            this.y = worldLocation.y;
            this.z = worldLocation.z;
            this.yaw = worldLocation.yaw;
            this.pitch = worldLocation.pitch;
        } else if (value instanceof Location loc) {
            this.x = loc.getX();
            this.y = loc.getY();
            this.z = loc.getZ();
            this.yaw = loc.getYaw();
            this.pitch = loc.getPitch();
            this.location = loc;
        }
    }

    @Override
    public Object getValue(boolean deep) {
        if (deep) {
            return location;
        } else {
            return this;
        }

    }

    @Override
    public Object serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("x",x);
        map.put("y",y);
        map.put("z",z);
        map.put("yaw",yaw);
        map.put("pitch",pitch);
        return map;
    }

    public Plot getPlot() {
        return plot;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
