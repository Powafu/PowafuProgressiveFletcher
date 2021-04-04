package PowafuProgressiveFletcher;

import api.IsReady;
import message.MessageHelper;
import message.type.Request;
import message.type.RequestType;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import store.Config;
import store.Store;

import java.util.function.Predicate;

import static PowafuProgressiveFletcher.Main.hasAllMaterials;
import static PowafuProgressiveFletcher.Main.setMaterialsLeft;
import static api.Banking.closeBank;
import static api.Banking.depositLoot;


public class FletchBanking extends Task {
    private static final String bowstring = "Bow string";
    private static Predicate<Item> unstrung = i -> i.getName().equals(material()) && !i.isNoted();
    public static final Predicate<Item> string = i -> i.getName().equals(bowstring) && !i.isNoted();
    public static Predicate<Item> getUnstrung(){return unstrung;}

    @Override
    public boolean validate()
        {
            return IsReady.isReady() && !hasAllMaterials() && !Restock.getMustRestock();
        }

    @Override
    public int execute()
        {
            if (Bank.isOpen())
            {
                // Check if muling is needed
                int totalNumOfCoins = Bank.getCount("Coins") + Inventory.getCount(true, "Coins");
                if (totalNumOfCoins <= Config.LOW_COINS_TO_TRIGGER_MULING || totalNumOfCoins >= Config.HIGH_COINS_TO_TRIGGER_MULING) {
                    Log.fine("Time to mule! Sending message to mule.");
                    Store.getMuleClient().sendMessage(new Request(MessageHelper.generateMessageID(), Store.getOurClient().getTag(), RequestType.MULE.toString(), Players.getLocal().getName()).toUrl());
                    Log.fine("Waiting 30 seconds for response");
                    boolean muling = Time.sleepUntil(() -> Store.isMuling() || Config.isStopping(), 30000);
                    if (muling) {
                        return Config.getLoopReturn();
                    }
                    Log.severe("Failed to activate muling");
                    return Config.getLoopReturn();
                }


                //we have everything we need to string, we out this hoe
                if (hasAllMaterials())
                    return closeBank();

                /*we need to dump items before grabbing more
                  uhhhhhhhhhhhh might need to add some check here for cannot withdraw
                  both materials due to mostly full inv*/
                if (mustDepositShit())
                    return depositLoot();

                //fletching is less than 10 we need to make shafts
                if (Skills.getLevel(Skill.FLETCHING) < 20)
                    return withdrawShaftMaterials();

                //poggers my dude we are above 10 fletch lets string some fuckin bows
                if (Bank.contains(material()) && !Inventory.contains(material()) && Bank.contains(bowstring) && !Inventory.contains((string)))
                {
                    api.Banking.withdrawItem(material(), 14, false);
                    api.Banking.withdrawItem(bowstring, 14, false);
                    return 75;
                }
                //set mustrestock shit here bc dont have some item
                Restock.setMustRestock(true);
                return 75;
            }
            if (Bank.open())
                Time.sleepUntil(Bank::isOpen, 125, 5000);
            return 75;
        }

    private int withdrawShaftMaterials()
        {
            if ((!Bank.contains("Knife") && !Inventory.contains("Knife")) || (!Bank.contains("Logs") && !Inventory.contains("Logs")))
            {
            Log.info("Set restock true because no knife or logs ");
                Restock.setMustRestock(true);
            }
                if (Bank.contains("Knife") && !Inventory.contains("Knife"))
                {
                    if (!api.Banking.withdrawItem("Knife", 1, false))
                    return 75;
                }

            setMaterialsLeft(Bank.getCount("Logs"));
            if (Bank.contains("Logs") && !Inventory.contains("Logs"))
                if (!api.Banking.withdrawItem("Logs", 27, false))
                    return 75;

            return 75;
        }

    private static String material()
        { return Bow.getTargetBow().getMaterial(); }



    private boolean mustDepositShit()
        {
            if (Skills.getLevel(Skill.FLETCHING) < 20)
                return Inventory.getFirst(Item::isNoted) != null || !hasAllMaterials() && Inventory.isFull();
            return Inventory.containsAnyExcept(string.or(unstrung));
        }


}
