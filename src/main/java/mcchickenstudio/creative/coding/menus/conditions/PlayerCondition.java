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


package mcchickenstudio.creative.coding.menus.conditions;

import mcchickenstudio.creative.coding.menus.actions.Action;
import mcchickenstudio.creative.coding.menus.executors.Executor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import mcchickenstudio.creative.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class PlayerCondition extends Condition {

    private final PlayerConditionSubtype conditionSubtype;

    public PlayerCondition(Location location, PlayerConditionSubtype conditionSubtype, List<String> arguments, int parameter, boolean isOpposed, List<Action> actions) {
        super(location, arguments, parameter, isOpposed, actions);
        this.conditionSubtype = conditionSubtype;
    }

    @Override
    public void execute(Executor playerExecutor, Entity entity) {

        String errorMessage = "Unknown error";
        boolean returnValue = false;

        try {
            switch (conditionSubtype) {
                case NICKNAME_EQUALS:
                    for (String name : getArguments()) {
                        if (entity.getName().equalsIgnoreCase(name)) {
                            returnValue = true;
                            break;
                        }
                    }
                    break;
                case NEAR_LOCATION: {
                    Location location = new Location(entity.getWorld(),Double.parseDouble(getArguments().get(0)),Double.parseDouble(getArguments().get(1)),Double.parseDouble(getArguments().get(2)));
                    if (entity.getLocation().distance(location) < Double.parseDouble(getArguments().get(3))) {
                        returnValue = true;
                        break;
                    }
                }
                case INVENTORY_FULL:
                    if (((Player) entity).getInventory().firstEmpty() == -1) {
                        returnValue = true;
                        break;
                    }
                    break;
            }
            if (!isOpposed() && returnValue) {
                for (Action action : getActions()) {
                    action.execute(playerExecutor,entity);
                }
            } else if (isOpposed() && !returnValue) {
                for (Action action : getActions()) {
                    action.execute(playerExecutor,entity);
                }
            }
        } catch (Exception e) {
            if (e.getClass().getName().contains("IndexOutOfBounds")) {
                ErrorUtils.sendPlotCodeErrorMessage(playerExecutor, this, entity, getLocaleMessage("plot-code-error.arguments"));
            } else if (e.getClass().getName().contains("NumberFormat")) {
                ErrorUtils.sendPlotCodeErrorMessage(playerExecutor, this, entity, getLocaleMessage("plot-code-error.wrong-number"));
            } else if (e.getClass().getName().contains("IllegalArgument")) {
                ErrorUtils.sendPlotCodeErrorMessage(playerExecutor, this, entity, getLocaleMessage("plot-code-error.wrong-argument") + errorMessage);
            } else {
                ErrorUtils.sendPlotCodeErrorMessage(playerExecutor,this,entity,errorMessage);
            }
        }

    }

    public PlayerConditionSubtype getConditionSubtype() {
        return conditionSubtype;
    }

    private static ItemStack parseItem(String itemString) {
        ItemStack itemStack = new ItemStack(Material.AIR);

        String[] itemData = itemString.split("::");
        Material material = Material.valueOf(itemData[1]);
        int amount = Integer.parseInt(itemData[2]);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemData.length >= 4) {
            String name = itemData[3];
            itemMeta.setDisplayName(name);
        }
        if (itemData.length >= 5) {
            String loreString = itemData[4];
            List<String> lore = new ArrayList<>(Arrays.asList(loreString.split("\\n")));
            itemMeta.setLore(lore);
        }

        itemStack.setType(material);
        itemStack.setAmount(amount);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
*/
