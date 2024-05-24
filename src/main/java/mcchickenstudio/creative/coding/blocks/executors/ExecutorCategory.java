package mcchickenstudio.creative.coding.blocks.executors;

import org.bukkit.Material;

/**
 * <h1>ExecutorCategory</h1>
 * This enum defines different categories for coding blocks with executor type.
 * Every category has material of block that player will place. Members are:
 * EVENT_PLAYER, EVENT_WORLD, CYCLE, FUNCTION and etc.
 * @since 1.5
 * @version 1.5
 * @author McChicken Studio
 */
public enum ExecutorCategory {

    EVENT_PLAYER(Material.DIAMOND_BLOCK),
    EVENT_WORLD(Material.NETHER_BRICKS),
    CYCLE(Material.EMERALD_BLOCK),
    FUNCTION(Material.LAPIS_BLOCK);

    private final Material block;

    ExecutorCategory(Material block) {
        this.block = block;
    }

    public Material getBlock() {
        return block;
    }


}
