
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BankAccount
{
    private final String accountNumber;
    private final String owner;
    private Balance balance;
    private final long limit;
    private final List<String> history = new ArrayList<>();

    public BankAccount(
            String accountNumber,
            String owner,
            Currency currency,
            long limit
    ) {
        if (accountNumber == null || accountNumber.length() != 20)
        {
            throw new IllegalArgumentException(
                    "Номер счёта должен состоять из 20 цифр"
            );
        }

        for (int i = 0; i < accountNumber.length(); i++)
        {
            if (!Character.isDigit(accountNumber.charAt(i)))
            {
                throw new IllegalArgumentException(
                        "Номер счёта должен состоять из 20 цифр"
                );
            }
        }

        if (owner == null || owner.isBlank())
        {
            throw new IllegalArgumentException(
                    "Имя владельца не должно быть пустым"
            );
        }

        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = new Balance(
                0,
                Objects.requireNonNull(
                        currency,
                        "Валюта не может быть null"
                )
        );

        if (limit < 0) {
            throw new IllegalArgumentException(
                    "Лимит не может быть отрицательным"
            );
        }

        this.limit = limit;
    }

    public Balance getBalance()
    {
        return balance;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public String getOwnerName()
    {
        return owner;
    }

    public void deposit(Balance amount)
    {
        checkAmount(amount);

        Balance newBalance = balance.add(amount);

        balance = newBalance;

        history.add(
                "Пополнение: " + amount
        );
    }

    public void withdraw(Balance amount)
    {
        checkAmount(amount);

        if (amount.getAmount() > limit)
        {
            throw new IllegalStateException(
                    "Превышен лимит снятия"
            );
        }

        Balance newBalance = balance.subtract(amount);

        balance = newBalance;

        history.add(
                "Снятие: " + amount
        );
    }

    public void transferTo(
            BankAccount recipient,
            Balance amount
    ) {
        Objects.requireNonNull(
                recipient,
                "Получатель не может быть null"
        );

        if (recipient == this)
        {
            throw new IllegalArgumentException(
                    "Нельзя переводить деньги самому себе"
            );
        }

        checkPositiveAmount(amount);

        Balance senderAmount = amount.convertTo(balance.getCurrency());
        Balance recipientAmount = amount.convertTo(
                recipient.balance.getCurrency()
        );

        if (senderAmount.getAmount() == 0
                || recipientAmount.getAmount() == 0)
        {
            throw new IllegalArgumentException(
                    "Сумма слишком мала для перевода в этой валюте"
            );
        }

        if (senderAmount.getAmount() > limit)
        {
            throw new IllegalStateException(
                    "Превышен лимит перевода"
            );
        }

        Balance senderBalance = balance.subtract(senderAmount);

        Balance recipientBalance =
                recipient.balance.add(recipientAmount);

        balance = senderBalance;
        recipient.balance = recipientBalance;

        history.add(
                "Перевод получателю "
                        + recipient.owner + ": " + senderAmount
        );

        recipient.history.add(
                "Получен перевод от "
                        + owner + ": " + recipientAmount
        );
    }

    private void checkAmount(Balance amount)
    {
        checkPositiveAmount(amount);

        if (amount.getCurrency() != balance.getCurrency())
        {
            throw new IllegalArgumentException(
                    "Валюты счёта и операции должны совпадать"
            );
        }
    }

    private void checkPositiveAmount(Balance amount)
    {
        Objects.requireNonNull(
                amount,
                "Сумма операции не может быть null"
        );

        if (amount.getAmount() <= 0)
        {
            throw new IllegalArgumentException(
                    "Сумма операции должна быть положительной"
            );
        }
    }

    public List<String> getHistory()
    {
        return List.copyOf(history);
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

        BankAccount otherAccount = (BankAccount) other;
        return accountNumber.equals(otherAccount.accountNumber);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString()
    {
        Balance formattedLimit = new Balance(
                limit,
                balance.getCurrency()
        );

        return "Владелец: " + owner
                + ", баланс: " + balance
                + ", лимит операции: " + formattedLimit;
    }
}
