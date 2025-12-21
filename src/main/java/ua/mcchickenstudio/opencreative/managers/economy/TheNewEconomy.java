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

import net.tnemc.core.EconomyManager;
import net.tnemc.core.TNECore;
import net.tnemc.core.account.Account;
import net.tnemc.core.account.holdings.modify.HoldingsModifier;
import net.tnemc.core.actions.source.PluginSource;
import net.tnemc.core.api.TNEAPI;
import net.tnemc.core.currency.Currency;
import net.tnemc.core.transaction.Transaction;
import net.tnemc.core.transaction.TransactionResult;
import net.tnemc.core.utils.exceptions.InvalidTransactionException;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.math.BigDecimal;
import java.util.Optional;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;

/**
 * Implementation of economy from The New Economy plugin.
 */
public final class TheNewEconomy implements Economy {

    private TNEAPI theNewEconomy;
    private Currency currency;

    @Override
    public boolean depositMoney(OfflinePlayer offlinePlayer, Number money) {
        if (currency == null) return false;
        Optional<Account> account = TNECore.eco().account().findAccount(offlinePlayer.getUniqueId());
        if (account.isEmpty()) {
            return false;
        }

        HoldingsModifier modifier = new HoldingsModifier("opencreative",
                currency.getUid(),
                BigDecimal.valueOf(money.doubleValue()));


        Transaction transaction = new Transaction("give")
                .to(account.get(), modifier)
                .processor(EconomyManager.baseProcessor())
                .source(new PluginSource("OpenCreative"));

        try {
            TransactionResult result = transaction.process();
            return result.isSuccessful();
        } catch(Exception e) {
            sendCriticalErrorMessage("Failed to deposit money " + money + " with The New Economy to player " + offlinePlayer.getName(), e);
            return false;
        }
    }

    @Override
    public boolean withdrawMoney(OfflinePlayer offlinePlayer, Number money) {
        if (currency == null) return false;
        Optional<Account> account = TNECore.eco().account().findAccount(offlinePlayer.getUniqueId());
        if (account.isEmpty()) return false;

        final HoldingsModifier modifier = new HoldingsModifier("opencreative",
                currency.getUid(),
                BigDecimal.valueOf(money.doubleValue()));

        final Transaction transaction = new Transaction("take")
                .to(account.get(), modifier.counter())
                .processor(EconomyManager.baseProcessor())
                .source(new PluginSource("OpenCreative"));

        try {
            TransactionResult result = transaction.process();
            return result.isSuccessful();
        } catch(InvalidTransactionException e) {
            sendCriticalErrorMessage("Failed to withdraw money " + money + " with The New Economy from balance of " + offlinePlayer.getName(), e);
            return false;
        }
    }

    @Override
    public Number getBalance(OfflinePlayer offlinePlayer) {
        Optional<Account> account = TNECore.eco().account().findAccount(offlinePlayer.getUniqueId());
        return account.<Number>map(value -> value.getHoldingsTotal("opencreative",
                currency.getUid()).doubleValue()).orElse(0);
    }

    @Override
    public void init() {
        theNewEconomy = TNECore.api();
        String currencyId = OpenCreative.getSettings().getEconomySettings().getCurrency();
        if (currencyId.equalsIgnoreCase("default")) {
            currency = theNewEconomy.getDefaultCurrency();
            return;
        }
        currency = getCurrencyByName(currencyId);
        if (currency == null) {
            sendWarningErrorMessage("Unknown economy currency for The New Economy: " + currencyId + ", make sure it exists, or change to default.");
        }
    }

    private @Nullable Currency getCurrencyByName(@NotNull String text) {
        for (Currency currency : theNewEconomy.getCurrencies()) {
            if (currency.getIdentifier().equalsIgnoreCase(text)) {
                return currency;
            }
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return theNewEconomy != null && currency != null;
    }

    @Override
    public String getName() {
        return "The New Economy";
    }
}
