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

package ua.mcchickenstudio.opencreative.coding.menus.layouts;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import org.bukkit.block.Block;

public class LayoutMaker extends Layout {

    public LayoutMaker(ActionType action, Block chestBlock) {
        super((byte) 3, action, chestBlock);
    }

    @Override
    protected void fillVarsItems() {
        if (actionType.getArgumentsSlots().length > 0 && actionType.getArgumentsSlots()[0].isList()) {
            switch (actionType.getArgumentsSlots()[0].getListSize()) {
                case 9: {
                    setRows((byte) 3);
                    if (actionType == ActionType.WORLD_SPAWN_ENTITY) {
                        setRows((byte) 6);
                    }
                    for (byte slot = 0; slot < 9; slot++) {
                        setGlass((byte) 1,slot);
                    }
                    for (byte slot = 9; slot < 18; slot++) {
                        setArgSlot((byte) 1,slot);
                    }
                    for (byte slot = 18; slot < 27; slot++) {
                        setGlass((byte) 1,slot);
                    }
                    if (actionType == ActionType.WORLD_SPAWN_ENTITY) {
                        byte slot = 36;
                        for (byte argNumber = 2; argNumber <= actionType.getArgumentsSlots().length; argNumber++) {
                            if (slot <= 44) {
                                setArgSlotVertical(argNumber,slot);
                                slot++;
                            }
                        }
                        return;
                    }
                    if (actionType.getArgumentsSlots().length > 1) {
                        if (actionType.getArgumentsSlots()[1].isList()) {
                            setRows((byte) 6);
                            for (byte slot = 27; slot < 36; slot++) {
                                setGlass((byte) 2,slot);
                            }
                            for (byte slot = 36; slot < 45; slot++) {
                                setArgSlot((byte) 2,slot);
                            }
                            for (byte slot = 45; slot < 54; slot++) {
                                setGlass((byte) 2,slot);
                            }
                            if (actionType.getArgumentsSlots().length > 2) {
                                setArgSlotHorizontal((byte) 3,(byte)49);
                            }
                        } else {
                            setArgSlotHorizontal((byte) 2,(byte) 22);
                        }
                    }
                    break;
                }
                case 18: {
                    setRows((byte) 4);
                    for (byte slot = 0; slot < 9; slot++) {
                        setGlass((byte) 1,slot);
                    }
                    for (byte slot = 9; slot < 27; slot++) {
                        setArgSlot((byte) 1,slot);
                    }
                    for (byte slot = 27; slot < 36; slot++) {
                        setGlass((byte) 1,slot);
                    }
                    if (actionType.getArgumentsSlots().length > 1 && !actionType.getArgumentsSlots()[1].isList()) {
                        setRows((byte) 6);
                        for (byte slot = 36; slot < 54; slot++) {
                            setItem(slot,DECORATION_PANE_ITEM);
                        }
                        int remainingSlots = actionType.getArgumentsSlots().length-1;
                        byte i = 2;
                        for (byte slot : getCentredSlots((byte) remainingSlots,(byte) 6)) {
                            if (remainingSlots > 3) {
                                setGlass(i,(byte) (slot-9));
                                setArgSlot(i,slot);
                            } else {
                                setArgSlotHorizontal(i,slot);
                            }
                            i++;
                        }
                    }
                    break;
                }
                case 27: {
                    setRows((byte) 5);
                    for (byte slot = 0; slot < 9; slot++) {
                        setGlass((byte) 1,slot);
                    }
                    for (byte slot = 9; slot < 36; slot++) {
                        setArgSlot((byte) 1,slot);
                    }
                    for (byte slot = 36; slot < 45; slot++) {
                        setGlass((byte) 1,slot);
                    }
                    if (actionType.getArgumentsSlots().length > 1 && !actionType.getArgumentsSlots()[1].isList()) {
                        setArgSlotHorizontal((byte) 2,(byte) 40);
                    }
                    break;
                }
                case 45: {
                    setRows((byte) 6);
                    byte number = 1;
                    for (byte slot = 9; slot < 45; slot++) {
                        setArgSlot(number++,slot);
                    }
                    for (byte slot = 45; slot < 54; slot++) {
                        setGlass((byte) 1,slot);
                    }
                }
            }
            return;
        }
        switch (getRequiredSlots().length) {
            case 1:
                setArgSlotVertical((byte) 1,(byte) 13);
                break;
            case 2:
                setArgSlotVertical((byte) 1,(byte) 12);
                setArgSlotVertical((byte) 2,(byte) 14);
                break;
            case 3:
                setArgSlotVertical((byte) 1,(byte) 11);
                setArgSlotVertical((byte) 2,(byte) 13);
                setArgSlotVertical((byte) 3,(byte) 15);
                break;
            case 4:
                setArgSlotVertical((byte) 1,(byte) 10);
                setArgSlotVertical((byte) 2,(byte) 12);
                setArgSlotVertical((byte) 3,(byte) 14);
                setArgSlotVertical((byte) 4,(byte) 16);
                break;
            case 5:
                setArgSlotVertical((byte) 1,(byte) 9);
                setArgSlotVertical((byte) 2,(byte) 11);
                setArgSlotVertical((byte) 3,(byte) 13);
                setArgSlotVertical((byte) 4,(byte) 15);
                setArgSlotVertical((byte) 5,(byte) 17);
                break;
            case 6:
                setArgSlotVertical((byte) 1,(byte) 10);
                setArgSlotVertical((byte) 2,(byte) 11);
                setArgSlotVertical((byte) 3,(byte) 12);
                setArgSlotVertical((byte) 4,(byte) 14);
                setArgSlotVertical((byte) 5,(byte) 15);
                setArgSlotVertical((byte) 6,(byte) 16);
                break;
            case 7:
                setArgSlotVertical((byte) 1,(byte) 10);
                setArgSlotVertical((byte) 2,(byte) 11);
                setArgSlotVertical((byte) 3,(byte) 12);
                setArgSlotVertical((byte) 4,(byte) 13);
                setArgSlotVertical((byte) 5,(byte) 14);
                setArgSlotVertical((byte) 6,(byte) 15);
                setArgSlotVertical((byte) 7,(byte) 16);
                break;
            case 8:
                setArgSlotVertical((byte) 1,(byte) 9);
                setArgSlotVertical((byte) 2,(byte) 10);
                setArgSlotVertical((byte) 3,(byte) 11);
                setArgSlotVertical((byte) 4,(byte) 12);
                setArgSlotVertical((byte) 5,(byte) 14);
                setArgSlotVertical((byte) 6,(byte) 15);
                setArgSlotVertical((byte) 7,(byte) 16);
                setArgSlotVertical((byte) 8,(byte) 17);
                break;
            case 9:
                setArgSlotVertical((byte) 1,(byte) 9);
                setArgSlotVertical((byte) 2,(byte) 10);
                setArgSlotVertical((byte) 3,(byte) 11);
                setArgSlotVertical((byte) 4,(byte) 12);
                setArgSlotVertical((byte) 5,(byte) 13);
                setArgSlotVertical((byte) 6,(byte) 14);
                setArgSlotVertical((byte) 7,(byte) 15);
                setArgSlotVertical((byte) 8,(byte) 16);
                setArgSlotVertical((byte) 9,(byte) 17);
                break;
        }
    }

}
