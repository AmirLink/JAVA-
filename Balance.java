
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Balance
{
    private final long amount;
    private final Currency currency;

    public Balance(long amount, Currency currency)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException(
                    "Сумма не может быть отрицательной"
            );
        }

        this.currency = Objects.requireNonNull(
                currency,
                "Валюта не может быть null"
        );
        this.amount = amount;
    }

    public long getAmount()
    {
        return amount;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public Balance add(Balance otherBalance)
    {
        Objects.requireNonNull(
                otherBalance,
                "Баланс не может быть null"
        );

        if (this.currency != otherBalance.currency)
        {
            throw new IllegalArgumentException(
                    "Валюты должны совпадать"
            );
        }

        long newAmount = Math.addExact(
                this.amount,
                otherBalance.amount
        );

        return new Balance(newAmount, this.currency);
    }

    public Balance subtract(Balance otherBalance)
    {
        Objects.requireNonNull(
                otherBalance,
                "Баланс не может быть null"
        );

        if (this.currency != otherBalance.currency)
        {
            throw new IllegalArgumentException(
                    "Валюты должны совпадать"
            );
        }

        if (otherBalance.amount > this.amount)
        {
            throw new IllegalStateException(
                    "Недостаточно средств"
            );
        }

        long newAmount = Math.subtractExact(
                this.amount,
                otherBalance.amount
        );

        return new Balance(newAmount, this.currency);
    }

    public Balance convertTo(Currency newCurrency)
    {
        Objects.requireNonNull(
                newCurrency,
                "Целевая валюта не может быть null"
        );

        if (currency == newCurrency)
        {
            return this;
        }

        BigDecimal newAmount = BigDecimal
                .valueOf(amount)
                .multiply(
                        BigDecimal.valueOf(
                                currency.getPriceInRublesKopecks()
                        )
                )
                .divide(
                        BigDecimal.valueOf(
                                newCurrency.getPriceInRublesKopecks()
                        ),
                        0,
                        RoundingMode.HALF_UP
                );

        return new Balance(
                newAmount.longValueExact(),
                newCurrency
        );
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other == null || getClass() != other.getClass())
        {
            return false;
        }

        Balance otherBalance = (Balance) other;
        return amount == otherBalance.amount
                && currency == otherBalance.currency;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString()
    {
        BigDecimal formattedAmount = BigDecimal.valueOf(
                amount, 2
        );

        return formattedAmount.toPlainString()
                + " " + currency.getSymbol();
    }
}
