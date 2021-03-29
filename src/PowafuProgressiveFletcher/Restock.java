package PowafuProgressiveFletcher;

import api.Banking;
import api.ExGe;
import api.IsReady;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.query.results.GrandExchangeOfferQueryResults;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.util.function.Predicate;

import static PowafuProgressiveFletcher.Bow.getTargetBow;
import static api.Banking.openBank;
import static api.ExGe.canCollectGe;
import static org.rspeer.runetek.providers.RSGrandExchangeOffer.Type.BUY;
import static org.rspeer.runetek.providers.RSGrandExchangeOffer.Type.SELL;

public class Restock extends Task {
    /*Ge handling works but is scuffed to read and modify, I just kind of dont think about this part :)*/
    private static boolean mustRestock;

    public static void setMustRestock(boolean mustRestock) {Restock.mustRestock = mustRestock;}
    public static boolean getMustRestock(){return mustRestock;}
    private boolean hasBanked, hasCounted, mustSellBows, mustBuyKnife;
    private int targetItemCount, stringCount;
    private final Predicate<Item>
            longbow = i -> i.getName().toLowerCase().contains("longbow"),
            shortbow = i -> i.getName().toLowerCase().contains("shortbow"),
            string = i -> i.getName().equals("Bow string");
    private int amountToBuy;

    @Override
    public boolean validate()
        {return IsReady.isReady() && mustRestock;}

    @Override
    public int execute()
        {
            int loopDelay = Random.nextInt(150, 500);
            if (hasBanked)
            {
                if (mustSellBows)
                {
                    if (!GrandExchange.isOpen()) {
                        GrandExchange.open();
                    }

                    if (!ExGe.hasNotAnyFinishedOffers() && !ExGe.collectFinishedOffers(false))
                        return loopDelay;

                    for (Item item : Inventory.getItems(shortbow.or(longbow)))
                    {
                        if (!ExGe.smartExchangeWithPrice(SELL, item.getName(), 0, 5000, (GrandExchangeSetup.getPricePerItem() - 5), 3, 500, 0, false))
                            return Random.nextInt(600,1200);
                    }

                    if (GrandExchange.newQuery().nameContains("bow").results().first() == null
                            && Inventory.getFirst(shortbow.or(longbow)) == null)
                        mustSellBows = false;

                    if (canCollectGe() && GrandExchange.collectAll())
                        Time.sleepUntil(ExGe::hasNotAnyFinishedOffers, 350, 3000);
                    return loopDelay;
                }

                //do buy shit
                if (!buyAllItems())
                    return loopDelay;

                reinitializeVars();
                return loopDelay;
            }

            //we bankin to grab all shit and check amounts
            if (Bank.isOpen())
            {
                if (Bank.contains("Coins") && !Banking.withdrawAllItem("Coins", false))
                    return loopDelay;

                if (!hasCounted)
                    checkCounts();

                if (finishedChecking())
                {
                    hasBanked = true;
                    return loopDelay;
                }

                withdrawToSell();
            }

            if (!Bank.isOpen())
                openBank();

            return loopDelay;
        }

    private boolean buyAllItems()
        {
            if (amountToBuy == 0)
                amountToBuy = getAmountToBuy();

            if (Skills.getLevel(Skill.FLETCHING) < 20)
            {
                if (mustBuyKnife)
                {
                    if (!ExGe.smartExchangeWithPrice(BUY, "Knife", 1, 5000, 500, 1, 10, 0, false))
                        return false;
                    mustBuyKnife = false;
                }
                return buy("Logs", amountToBuy - targetItemCount);
            }

            if (!buy(getTargetBow().getMaterial(), amountToBuy - targetItemCount))
                return false;

            return buy("Bow string", amountToBuy - stringCount);
        }



    private void checkCounts()
        {
            if (Skills.getLevel(Skill.FLETCHING) < 20)
            {
                targetItemCount = Bank.getCount("Logs");
                if (!Bank.contains("Knife") && !Inventory.contains("Knife"))
                    mustBuyKnife = true;
            } else
            {
                targetItemCount = Bank.getCount(getTargetBow().getMaterial());
                stringCount = Bank.getCount(string);
            }
            hasCounted = true;
        }

    private boolean finishedChecking()
        { return hasCounted && !Bank.contains(shortbow.or(longbow).or(string)) && !Bank.contains("Coins"); }

    private void withdrawToSell()
        {
            //Dont grab arrow shafts because they will not sell and will stall the script if you try to sell
            for (Item i : Bank.getItems(shortbow.or(longbow).or(string)))
            {
                Banking.withdrawAllItem(i.getName(), true);
                Time.sleep(Random.nextInt(75, 350));
                mustSellBows = true;
            }
        }

        /*Bow.getPrice() + 15% + price of bow string and then some. guarantees never getting stuck trying to make
            a ge offer that cannot be afforded*/
    private int stringBowPrice() {return (int) (getTargetBow().getPrice() + (getTargetBow().getPrice() * 0.15)) + 130;}

    private int getAmountToNextLvl()
        {
            if (Skills.getLevel(Skill.FLETCHING) < 20)
                return (Skills.getExperienceAt(20) - Skills.getExperience(Skill.FLETCHING)) / 5;

            return (Skills.getExperienceAt(Bow.getTargetLvl()) - Skills.getExperience(Skill.FLETCHING))
                    / (int) getTargetBow().getXp();
        }

    private int getAmountToBuy()
        {
            if (Skills.getLevel(Skill.FLETCHING) < 20)
                return Math.min(Inventory.getCount(true, "Coins")
                        / 55, Math.min((getAmountToNextLvl() + 1), Random.nextInt(800,1800)));

            return Math.min(Inventory.getCount(true, "Coins")
                    / stringBowPrice(), Math.min((getAmountToNextLvl() + 1), Random.nextInt(800,1800)));
        }

    private boolean buy(String name, int quantity)
        {
            if (!GrandExchange.isOpen())
            {
                if (GrandExchange.open())
                    Time.sleepUntil(GrandExchange::isOpen,250,5000);
                return false;
            }

            RSGrandExchangeOffer currentOffer = GrandExchange.newQuery().names(name).results().first();

            if (Inventory.getCount(true, name) >= quantity && currentOffer == null)
                return true;

            if (currentOffer != null)
            {
                if (currentOffer.getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) && GrandExchange.collectAll())
                    Time.sleepUntil(() -> !canCollectGe(), 175, Random.nextInt(800, 1600));
                Time.sleep(Random.nextInt(2500, 5000));
                return false;
            }

            if (!GrandExchangeSetup.isOpen() && !ExGe.createOffer(BUY))
                return false;

            if (GrandExchangeSetup.isOpen())
            {
                if (GrandExchangeSetup.getItem() == null)
                {
                    Time.sleep(150,350);
                    if (GrandExchangeSetup.setItem(name))
                        Time.sleepUntil(() -> GrandExchangeSetup.getItem() != null, 250, 5000);
                    return false;
                }

                if (GrandExchangeSetup.getQuantity() != quantity && !ExGe.setItemQuantity(quantity))
                    return false;

                setPrice();
                ExGe.confirmOffer();
            }
            return false;
        }

    private void setPrice()
        {
            int startPrice = 0;
            if (Skills.getLevel(Skill.FLETCHING) >= 20 && !GrandExchangeSetup.getItem().getName().equals("Bow string"))
            {
                //set ge price to Bow.getPrice()
                startPrice = getTargetBow().getPrice();
                if (GrandExchangeSetup.getPricePerItem() != startPrice && GrandExchangeSetup.setPrice(startPrice))
                    Time.sleepUntil(() -> GrandExchangeSetup.getPricePerItem() == getTargetBow().getPrice(), 125, 3000);

            }
            //increase price by 15%. Might be preferable to lower this when doing profitable bows
            GrandExchangeSetup.increasePrice(3);
            final int finalStartPrice = startPrice;
            Time.sleepUntil(() -> GrandExchangeSetup.getPricePerItem() != finalStartPrice, 250, 1200);
        }

    private void reinitializeVars()
        {
            amountToBuy = 0;
            mustRestock = false;
            hasBanked = false;
        }
}
