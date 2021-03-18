package api;

import org.tribot.api2007.*;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;

public class Banking {

    public static boolean withdrawAllItem(String name, boolean noted) {
        return withdrawItem(name, Integer.MAX_VALUE, noted);
    }

    public static boolean withdrawItem(int itemID, int amount, boolean noted) {
        if (Banking.isBankLoaded) {
            int currentItemCount = Bank.getCount(itemID);
            Item targetItemInInventory = Inventory.getFirst(itemID);

            if (currentItemCount == 0 && targetItemInInventory != null)
                return true;

            if (currentItemCount == 0)
                return false;

            if (Inventory.isFull() && targetItemInInventory == null || (targetItemInInventory != null && !targetItemInInventory.isStackable() && Inventory.getFreeSlots() < amount))
                return false;

            if (Bank.getCount(itemID) == currentItemCount) {

                if (noted && Bank.getWithdrawMode().equals(Bank.WithdrawMode.ITEM)) {
                    Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
                    Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE),75, 5000);
                } else if (!noted && Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE)) {
                    Bank.setWithdrawMode(Bank.WithdrawMode.ITEM);
                    Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.ITEM),75, 5000);
                }

                if (amount > currentItemCount)
                    Bank.withdrawAll(itemID);
                else
                    Bank.withdraw(itemID, amount);

                Time.sleepUntil(() -> Bank.getCount(itemID) != currentItemCount,75, Random.nextInt(800,1600));
            }
            return false;
        }
        return false;
    }

    public static boolean withdrawItem(String item, int amount, boolean noted) {
        if (Bank.isOpen()) {

            int inventItemCount = Inventory.getCount(noted, item);
            int currentItemCount = Bank.getCount(item);
            Item targetItemInInventory = Inventory.getFirst(item);

            if (inventItemCount > amount && Bank.deposit(item, inventItemCount - amount)) {
                Time.sleepUntil(() -> Inventory.getCount(noted, item) == amount,75, 3000);
                return false;
            }

            if (Inventory.getCount(true,item) == amount || (Inventory.getCount(true, item) >1 && Bank.getCount(item) <1))
                return true;

            if (Inventory.isFull() && targetItemInInventory == null || (targetItemInInventory != null && !targetItemInInventory.isStackable() && Inventory.getFreeSlots() < amount)) {
                Bank.depositInventory();
                Time.sleepUntil(Inventory::isEmpty,75, Random.nextInt(800,1600));
            }


            if (noted && Bank.getWithdrawMode().equals(Bank.WithdrawMode.ITEM)) {
                Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
                Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE),75, Random.nextInt(800,1600));
            } else if (!noted && Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE)) {
                Bank.setWithdrawMode(Bank.WithdrawMode.ITEM);
                Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.ITEM),75, Random.nextInt(800,1600));
            }

            if (amount < currentItemCount)
                Bank.withdraw(item, amount - inventItemCount);
            else
                Bank.withdrawAll(item);

            Time.sleepUntil(() -> Inventory.getCount(true, item) != inventItemCount,75, Random.nextInt(800,1600));
            return Inventory.getCount(true, item) == amount || (Inventory.getCount(true, item) > 1 && Bank.getCount(item) < 1);
        }
        return false;
    }

    public static void openBank()
        {
            if (Bank.open() && Time.sleepUntil(Bank::isOpen, 600, Random.nextInt(8000, 15000)))
                if (!Inventory.isEmpty() && Bank.depositInventory())
                    Time.sleepUntil(Inventory::isEmpty, 500, Random.nextInt(1200, 2500));
        }

    public static int closeBank()
        {
            if (Bank.isOpen() && Bank.close())
                Time.sleepUntil(Bank::isClosed, 75, 1800);
            return 75;
        }

    public static int depositLoot()
        {
            if (Bank.depositInventory())
                Time.sleepUntil(Inventory::isEmpty, 75, Random.nextInt(800, 1600));
            return 75;
        }
}
