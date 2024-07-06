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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;

public class Item implements CodingValue {

    private ItemStack item;
    private final ValueType type = ValueType.TEXT;

    public Item(ItemStack item) {
        this.item = item;
    }

    @Override
    public ValueType getType() {
        return ValueType.ITEM;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof ItemStack item) {
            this.item = item;
        }
    }

    @Override
    public ItemStack getValue(boolean deep) {
        return item;
    }

    @Override
    public Object serialize() {
        try {
            final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(item);
            return Base64Coder.encodeLines(arrayOutputStream.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }
}
