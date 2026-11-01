
public class Main
{

    public static void main(String[] args)
    {

        BankAccount account1 = new BankAccount(
                "10000000000000000001",
                "Амир",
                Currency.RUB,
                50_000
        );

        BankAccount account2 = new BankAccount(
                "10000000000000000002",
                "Ксения",
                Currency.USD,
                30_000
        );

        account1.deposit(new Balance(100_000, Currency.RUB));
        account2.deposit(new Balance(20_000, Currency.USD));

        System.out.println("Начальное состояние:");
        System.out.println(account1);
        System.out.println(account2);

        System.out.println("\nПополнение:");
        account1.deposit(
                new Balance(10_000, Currency.RUB)
        );
        System.out.println(account1);

        System.out.println("\nСнятие:");
        account1.withdraw(
                new Balance(5_000, Currency.RUB)
        );
        System.out.println(account1);

        System.out.println("\nПеревод:");
        account1.transferTo(
                account2,
                new Balance(30_000, Currency.RUB)
        );

        System.out.println("Отправитель: " + account1);
        System.out.println("Получатель: " + account2);

        System.out.println("\nКонвертация валют:");
        Balance dollars = new Balance(1_000, Currency.USD);
        System.out.println(
                dollars + " = "
                        + dollars.convertTo(Currency.RUB)
        );

        System.out.println("\nИстория операций Амира:");
        for (String operation :
                account1.getHistory()) {
            System.out.println(operation);
        }

        System.out.println("\nПроверка ошибки:");
        try {
            account1.withdraw(
                    new Balance(100_000, Currency.RUB)
            );
        } catch (
                IllegalArgumentException
                | IllegalStateException e
        )
        {
            System.out.println(
                    "Операция отклонена: " + e.getMessage()
            );
        }
    }
}
