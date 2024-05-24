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
*/

package mcchickenstudio.creative.menu.buttons;

import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RadioButton {

    int currentChoice;
    int maxChoicesAmount;
    List<Runnable> choiceActions;
    ItemStack buttonItem;
    List<String> originalLore;
    String turnedPath;
    String itemLocalePath;
    static Map<ItemStack,RadioButton> radioButtonList = new HashMap<>();

    public RadioButton(Material material, String name, List<String> lore, int currentChoice,
                       int maxChoicesAmount, List<Runnable> choicesActions, String itemLocalePath,
                       String turnedPath) {
        setChoices(currentChoice, maxChoicesAmount, choicesActions);
        setItemButton(material, name, lore, itemLocalePath, turnedPath);
        radioButtonList.put(getButtonItem(),this);
    }

    private void setChoices(int currentChoice, int maxChoicesAmount, List<Runnable> choicesActions) {
        this.currentChoice = currentChoice;
        this.maxChoicesAmount = maxChoicesAmount;
        this.choiceActions = choicesActions;
    }

    private void setItemButton(Material material, String name, List<String> lore, String itemLocalePath, String chosenLocalePath) {
        this.originalLore = lore;
        this.itemLocalePath = itemLocalePath;
        this.turnedPath = chosenLocalePath;

        ItemStack buttonItem = new ItemStack(material,1);
        ItemMeta buttonItemMeta = buttonItem.getItemMeta();
        buttonItemMeta.setDisplayName(name);
        buttonItem.setItemMeta(buttonItemMeta);
        buttonItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        buttonItem.addItemFlags(ItemFlag.HIDE_DESTROYS);
        buttonItem.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        this.buttonItem = buttonItem;
        updateLore();
    }

    private void updateLore() {

        ItemMeta buttonItemMeta = buttonItem.getItemMeta();
        List<String> lore = new ArrayList<>();

        String turnedOn = MessageUtils.getLocaleMessage(turnedPath+".turned-on");
        String turnedOff = MessageUtils.getLocaleMessage(turnedPath+".turned-off");
        String turned;

        for (String loreLine : originalLore) {
            if (loreLine.matches("%[0-9]+%")) {
                int choiceNumber = Integer.parseInt((loreLine.replace("%","")));
                if (choiceNumber == currentChoice) turned = turnedOn;
                else turned = turnedOff;
                loreLine = loreLine.replace("%" + choiceNumber + "%", turned + MessageUtils.getLocaleMessage(itemLocalePath + "." + choiceNumber, false));
            }
            lore.add(loreLine);
        }

        buttonItemMeta.setLore(lore);
        buttonItem.setItemMeta(buttonItemMeta);
    }

    public int getCurrentChoice() {
        return currentChoice;
    }

    public void onChoice() {

        if (this.currentChoice == 0) this.currentChoice = 1;
        int nextChoice = this.currentChoice+1;
        if (nextChoice > this.maxChoicesAmount) {
            this.currentChoice = 1;
            nextChoice = 1;
        }

        if (nextChoice-1 < choiceActions.size()) {
            Runnable actions = this.choiceActions.get(nextChoice-1);
            actions.run();
        }

        this.currentChoice = nextChoice;

        radioButtonList.remove(buttonItem);
        updateLore();
        radioButtonList.put(buttonItem,this);
    }

    public ItemStack getButtonItem() {
        return buttonItem;
    }

    public static RadioButton getRadioButtonByItemStack(ItemStack itemStack) {
        return radioButtonList.get(itemStack);
    }
}
