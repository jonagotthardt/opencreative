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


package mcchickenstudio.creative.coding.menus.actions;

import mcchickenstudio.creative.coding.menus.executors.Executor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import mcchickenstudio.creative.utils.ErrorUtils;

import java.util.*;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class PlayerAction extends Action {

    private final PlayerActionSubtype actionType;

    public PlayerAction(Location location, PlayerActionSubtype actionType, List<String> arguments, int parameter) {
        super(location,arguments,parameter);
        this.actionType = actionType;
    }

    @Override
    public void execute(Executor executor, Entity entity) {
        String errorMessage = "Something went wrong, that's all we can say to you... :(";

        if (actionType.isPlayerInWorldRequired()) {
            if (entity.getWorld() != this.getLocation().getWorld()) return;
        }

        try {
            switch (actionType) {
                case SEND_MESSAGE:
                    if (getParameter() == 1) {
                        entity.sendMessage(String.join("\n",getArguments()));
                    } else if (getParameter() == 2) {
                        entity.sendMessage(String.join(" ",getArguments()));
                    }
                    break;
                case SHOW_TITLE:
                    errorMessage = "Hello! SecondText. 20 100 20";
                    ((Player) entity).sendTitle(getArguments().get(0),getArguments().get(1),Math.round(Float.parseFloat(getArguments().get(2))),Math.round(Float.parseFloat(getArguments().get(3))),Math.round(Float.parseFloat(getArguments().get(4))));
                    break;
                case PLAY_SOUND:
                    errorMessage = "ENTITY_PLAYER_LEVELUP, 100, 2.0";
                    ((Player) entity).playSound(entity.getLocation(),Sound.valueOf(getArguments().get(0)),Float.parseFloat(getArguments().get(1)),Float.parseFloat(getArguments().get(2)));
                    break;
                case SET_GAMEMODE:
                    errorMessage = "CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR";
                    ((Player) entity).setGameMode(GameMode.valueOf(getArguments().get(0)));
                    break;
                case SET_HEALTH:
                    ((Player) entity).setHealth(Double.parseDouble(getArguments().get(0)));
                    break;
                case SET_HUNGER:
                    ((Player) entity).setFoodLevel(Math.round(Float.parseFloat(getArguments().get(0))));
                    break;
                case CLEAR_INVENTORY:
                    ((Player) entity).getInventory().clear();
                    break;
                case GIVE_ITEMS:
                    for (String item : getArguments()) {
                        ((Player) entity).getInventory().addItem(parseItem(item));
                    }
                    break;
                case TELEPORT_PLAYER:
                    entity.teleport(new Location(entity.getWorld(),Double.parseDouble(getArguments().get(0)),Double.parseDouble(getArguments().get(1)),Double.parseDouble(getArguments().get(2))));
                    break;
            }
        } catch (Exception e) {
            if (e.getClass().getName().contains("IndexOutOfBounds")) {
                ErrorUtils.sendPlotCodeErrorMessage(executor, this, entity, getLocaleMessage("plot-code-error.arguments"));
            } else if (e.getClass().getName().contains("NumberFormat")) {
                ErrorUtils.sendPlotCodeErrorMessage(executor, this, entity, getLocaleMessage("plot-code-error.wrong-number"));
            } else if (e.getClass().getName().contains("IllegalArgument")) {
                ErrorUtils.sendPlotCodeErrorMessage(executor, this, entity, getLocaleMessage("plot-code-error.wrong-argument") + errorMessage);
            } else {
                ErrorUtils.sendPlotCodeErrorMessage(executor,this,entity,errorMessage);
            }
        }

    }

    public PlayerActionSubtype getActionType() {
        return actionType;
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
