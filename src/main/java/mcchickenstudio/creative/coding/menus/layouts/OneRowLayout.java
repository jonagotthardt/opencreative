package mcchickenstudio.creative.coding.menus.layouts;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import org.bukkit.block.Block;

public class OneRowLayout extends Layout {

    public OneRowLayout(ActionType action, Block chestBlock) {
        super((byte) 3, action, chestBlock);
    }

    @Override
    protected void fillVarsItems() {
        if (actionType.getArgumentsSlots().length > 0 && actionType.getArgumentsSlots()[0].getListSize() > 1) {
            switch (actionType.getArgumentsSlots()[0].getListSize()) {
                case 9: {
                    setRows((byte) 4);
                    byte number = 1;
                    for (byte slot = 9; slot < 17; slot++) {
                        setArgSlotWithFrame(number++,slot);
                    }
                    break;
                }
                case 45: {
                    setRows((byte) 4);
                    byte number = 1;
                    for (byte slot = 9; slot < 45; slot++) {
                        setArgSlotWithFrame(number++,slot);
                    }
                    for (byte slot = 9; slot < 45; slot++) {
                        setArgSlotWithFrame(number++,slot);
                    }
                }
            }
        }
        switch (getRequiredSlots().length) {
            case 1:
                setArgSlotWithFrame((byte) 1,(byte) 13);
                break;
            case 2:
                setArgSlotWithFrame((byte) 1,(byte) 12);
                setArgSlotWithFrame((byte) 2,(byte) 14);
                break;
            case 3:
                setArgSlotWithFrame((byte) 1,(byte) 11);
                setArgSlotWithFrame((byte) 2,(byte) 13);
                setArgSlotWithFrame((byte) 3,(byte) 15);
                break;
            case 4:
                setArgSlotWithFrame((byte) 1,(byte) 10);
                setArgSlotWithFrame((byte) 2,(byte) 12);
                setArgSlotWithFrame((byte) 3,(byte) 14);
                setArgSlotWithFrame((byte) 4,(byte) 16);
                break;
            case 5:
                setArgSlotWithFrame((byte) 1,(byte) 9);
                setArgSlotWithFrame((byte) 2,(byte) 11);
                setArgSlotWithFrame((byte) 3,(byte) 13);
                setArgSlotWithFrame((byte) 4,(byte) 15);
                setArgSlotWithFrame((byte) 5,(byte) 17);
                break;
        }
    }

}
