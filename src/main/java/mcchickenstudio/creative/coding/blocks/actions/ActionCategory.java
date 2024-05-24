package mcchickenstudio.creative.coding.blocks.actions;

import org.bukkit.Material;

public enum ActionCategory {

    PLAYER_ACTION(Material.COBBLESTONE),
    WORLD_ACTION(Material.NETHER_BRICKS),
    VARIABLE_ACTION(Material.IRON_BLOCK),
    EXEC_FUNCTION_ACTION(Material.LAPIS_ORE),
    CONTROL_ACTION(Material.OBSIDIAN);

    Material block;

    ActionCategory(Material block) {
        this.block = block;
    }

}
