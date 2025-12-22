/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2025, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.managers.economy.DisabledEconomy;
import ua.mcchickenstudio.opencreative.managers.economy.TheNewEconomy;
import ua.mcchickenstudio.opencreative.managers.economy.VaultEconomy;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;

public class EconomySettings {

    private String economyType = "auto";
    private String currency = "default";

    /**
     * Loads settings of economy from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("economy");

        if (section == null) {
            OpenCreative.getPlugin().getConfig().set("economy.type", "auto");
            OpenCreative.getPlugin().getConfig().set("economy.currency", "default");
            OpenCreative.getPlugin().saveConfig();
            economyType = "auto";
            currency = "default";
        } else {
            economyType = section.getString("type", "auto");
            currency = section.getString("currency", "default");
        }

        OpenCreative.setEconomy(new DisabledEconomy());
        if (economyType.equalsIgnoreCase("the-new-economy")) {
            setupTheNewEconomy();
        } else if (economyType.equalsIgnoreCase("vault")) {
            setupVault();
        } else {
            if (HookUtils.isTheNewEconomyEnabled) {
                setupTheNewEconomy();
            } else if (HookUtils.isVaultEnabled) {
                setupVault();
            } else {
                OpenCreative.getPlugin().getLogger().info("Didn't detect Vault or The New Economy, action Request Purchase and like rewards will be not available.");
            }
        }
    }

    private void setupVault() {
        if (HookUtils.isVaultEnabled) {
            OpenCreative.getPlugin().getLogger().info("Successfully integrated to Vault: Economy actions are working.");
            OpenCreative.setEconomy(new VaultEconomy());
        } else {
            OpenCreative.getPlugin().getLogger().info("Didn't detect Vault, action Request Purchase and like rewards will be not available.");
        }
    }

    private void setupTheNewEconomy() {
        if (HookUtils.isTheNewEconomyEnabled) {
            OpenCreative.getPlugin().getLogger().info("Successfully integrated to The New Economy: Economy actions are working.");
            OpenCreative.setEconomy(new TheNewEconomy());
        } else {
            OpenCreative.getPlugin().getLogger().info("Didn't detect TheNewEconomy, action Request Purchase and like rewards will be not available.");
        }
    }

    /**
     * Returns economy type (none, vault, the-new-economy).
     * @return economy type.
     */
    public String getEconomyType() {
        return economyType;
    }

    /**
     * Returns currency of economy.
     * @return currency of economy.
     */
    public String getCurrency() {
        return currency;
    }
}
