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

package ua.mcchickenstudio.opencreative.managers.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault2.economy.Economy;

import java.math.BigDecimal;

/**
 * Implementation of economy from The New Economy plugin.
 */
public final class TheNewEconomy implements ua.mcchickenstudio.opencreative.managers.economy.Economy {

    private net.milkbowl.vault2.economy.Economy vaultEconomy;

    @Override
    public boolean depositMoney(OfflinePlayer offlinePlayer, Number money) {
        return vaultEconomy.deposit("opencreative", offlinePlayer.getUniqueId(),
                BigDecimal.valueOf(money.doubleValue())).transactionSuccess();
    }

    @Override
    public boolean withdrawMoney(OfflinePlayer offlinePlayer, Number money) {
        return vaultEconomy.withdraw("opencreative",
                offlinePlayer.getUniqueId(), BigDecimal.valueOf(money.doubleValue())).transactionSuccess();
    }

    @Override
    public Number getBalance(OfflinePlayer offlinePlayer) {
        return vaultEconomy.balance("opencreative", offlinePlayer.getUniqueId());
    }

    @Override
    public void init() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            vaultEconomy = rsp.getProvider();
        }
    }

    @Override
    public boolean isEnabled() {
        return vaultEconomy.isEnabled();
    }

    @Override
    public String getName() {
        return "The New Economy";
    }
}
