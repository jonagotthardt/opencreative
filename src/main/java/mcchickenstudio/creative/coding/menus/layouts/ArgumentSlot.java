package mcchickenstudio.creative.coding.menus.layouts;

import mcchickenstudio.creative.coding.menus.variables.VariableType;

public class ArgumentSlot {

    private VariableType varType;
    private byte minParameter = 0;
    private byte maxParameter = 0;
    private byte listSize = 1;

    public ArgumentSlot(VariableType varType) {
        this.varType = varType;
    }

    public ArgumentSlot(VariableType varType, byte listSize) {
        this.varType = varType;
        this.listSize = listSize;
    }

    public ArgumentSlot(byte minParam, byte maxParam) {
        this.varType = VariableType.PARAMETER;
        this.minParameter = minParam;
        this.maxParameter = maxParam;
    }

    public byte getMinParameter() {
        return minParameter;
    }

    public byte getMaxParameter() {
        return maxParameter;
    }

    public VariableType getVarType() {
        return varType;
    }

    public byte getListSize() {
        return listSize;
    }
}
