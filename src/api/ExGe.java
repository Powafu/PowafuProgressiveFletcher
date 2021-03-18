package api;

import net.jodah.failsafe.internal.util.RandomDelay;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSItemDefinition;
import org.rspeer.ui.Log;

import java.util.Arrays;

/**
 * @author burak
 */
public final class ExGe {
    private static final int SELL_ALL = 0;

    //Vars for smartExchanges
    private static int attemptsTried = 0, amountTransferred = 0;
    private static StopWatch geOfferTimer;
    private static boolean offerFailed;


    //TODO honestly this whole class is fucked spaghetti but it works.
    private static void reinitializeVars() {
        //smartExchange offer has completed, reset vars to 0/null
        attemptsTried = 0;
        geOfferTimer = null;
        offerFailed = false;
        amountTransferred = 0;
    }


    public static boolean smartExchangeWithPrice(RSGrandExchangeOffer.Type type, String name, int quantity, int offerTimerInterval, int price, int maxAttempts, int changeInterval, int panicPrice, boolean toBank) {
        RSGrandExchangeOffer currentOffer = GrandExchange.newQuery().nameContains(name).results().first();

        if (!GrandExchange.isOpen() && GrandExchange.open() && Time.sleepUntil(GrandExchange::isOpen, 350, 5000))
            return false;

        if (currentOffer == null && exchangeSuccess(type,name,quantity)){
            reinitializeVars();
            return true;
        }

        if (!hasNotAnyFinishedOffers() && !offerFailed && collectFinishedOffers(false))
            return false;

        if (offerFailed) {
            if (currentOffer != null) {
                if (Interfaces.isVisible(465, 4)) {
                    pressGEBackButton();
                    return false;
                }
                if (currentOffer.getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) && collectFinishedOffers(false)) {
                    if (hasNotAnyFinishedOffers()){
                        geOfferTimer = null;
                        attemptsTried++;
                        if (attemptsTried > maxAttempts) attemptsTried = 0;
                        offerFailed = false;
                        amountTransferred = 0;
                    }
                    return false;
                }
                if (!currentOffer.getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED)) {
                    Time.sleep(75, 500);
                    currentOffer.abort();
                    Time.sleepUntil(() -> !hasNotAnyFinishedOffers(), 1500, 3000);
                    return false;
                }
            }
            return false;
        }

        if (canCollectGe()) {
            if (!hasNotAnyFinishedOffers() && !offerFailed) {
                collectFinishedOffers(false);
            }

            if (hasNotAnyFinishedOffers()) {
                collectOffers(false);
                if (geOfferTimer == null) geOfferTimer = StopWatch.start();
                else geOfferTimer.reset();
            }
            return false;
        }


        if (currentOffer != null) {
            if (geOfferTimer == null) {
                geOfferTimer = StopWatch.start();
            }
            if (geOfferTimer.getElapsed().toMillis() >= offerTimerInterval) {
                offerFailed = true;
                return false;
            }
            if (currentOffer.getTransferred() != amountTransferred){
                amountTransferred = currentOffer.getTransferred();
                geOfferTimer.reset();
                return false;
            }
            if (Interfaces.isVisible(465, 4))
                pressGEBackButton();

            if (currentOffer.getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED)) {
                if (collectFinishedOffers(toBank)) {
                    reinitializeVars();
                    return true;
                }
            }
            return false;
        }

        if (currentOffer == null) {
            if (type.equals(RSGrandExchangeOffer.Type.BUY) && Inventory.getCount(true,name) > 0) quantity = quantity - Inventory.getCount(true,name);
            if (!GrandExchange.isOpen()) {
                GrandExchange.open();
                Time.sleepUntil(GrandExchange::isOpen, 350, 5000);
                return false;
            }
            if (canCollectGe() && GrandExchange.getView().equals(GrandExchange.View.OVERVIEW)){
                collectOffers(toBank);
                return false;
            }
            if (!GrandExchangeSetup.isOpen() && createOffer(type))
                return false;

            if (GrandExchangeSetup.getItem() == null) {
                if (type.equals(RSGrandExchangeOffer.Type.BUY))
                    GrandExchangeSetup.setItem(name);
                else GrandExchangeSetup.setItem(Inventory.getFirst(name).getDefinition().getId());
                Time.sleepUntil(() -> GrandExchangeSetup.getItem() != null, 350, 5000);
                return false;
            }

            setItemPrice(getPrice(type, price,maxAttempts, changeInterval, panicPrice));
            if (!isItemQuantitySettled(quantity))
                setItemQuantity(quantity);

            confirmOffer();
        }
        return false;
    }

    public static boolean confirmOffer() {
        GrandExchangeSetup.confirm();
        Time.sleepUntil(() -> !GrandExchangeSetup.isOpen(), 1500, 10000);
        if (geOfferTimer == null) {
            geOfferTimer = StopWatch.start();
        } else geOfferTimer.reset();
        return true;
    }

    private static boolean setItem(RSItemDefinition item) {
        return GrandExchangeSetup.setItem(item.getUnnotedId());
    }

    public static boolean collectFinishedOffers(boolean toBank) {
        if (hasNotAnyFinishedOffers()) return true;
        GrandExchange.collectAll(toBank);
        Time.sleepUntil(ExGe::hasNotAnyFinishedOffers, 1500, 3500);
        return hasNotAnyFinishedOffers();
    }

    public static void collectOffers(boolean toBank){
        GrandExchange.collectAll(toBank);
        Time.sleepUntil(() -> !canCollectGe(),350,5000);

    }

    public static boolean createOffer(RSGrandExchangeOffer.Type offerType) {
        if (GrandExchangeSetup.isOpen()) return true;
        return GrandExchange.createOffer(offerType)
                && Time.sleepUntil(GrandExchangeSetup::isOpen, 350, 5000);
    }

    public static boolean hasNotAnyFinishedOffers() {
        return Arrays.stream(GrandExchange.getOffers())
                .noneMatch(it -> it.getProgress() == RSGrandExchangeOffer.Progress.FINISHED);
    }

    private static boolean isItemPriceSettled(int desired) {
        if (desired == 0) return true;
        return GrandExchangeSetup.getPricePerItem() == desired;
    }

    private static boolean isItemQuantitySettled(int desired) {
        return GrandExchangeSetup.getQuantity() == desired;
    }

    public static boolean setItemPrice(int price) {
        return GrandExchangeSetup.setPrice(price)
                && Time.sleepUntil(() -> isItemPriceSettled(price), 350, 5000);
    }

    public static boolean setItemQuantity(int quantity) {
        if (quantity == SELL_ALL) return true;

        return GrandExchangeSetup.setQuantity(quantity)
                && Time.sleepUntil(() -> isItemQuantitySettled(quantity), 350, 5000);
    }

    public static boolean canCollectGe() {
        InterfaceComponent collectBox = Interfaces.newQuery().groups(465).includeSubcomponents().visible().actions(s -> s.contains("Collect to")).results().first();
        return !GrandExchangeSetup.isOpen() && collectBox != null;
    }

    public static void pressGEBackButton() {
        InterfaceComponent Back = Interfaces.getComponent(465, 4);
        if (Back == null || !Back.isVisible()) {
            Log.info("Could not find GE back button");
            return;
        }
        if (Back.interact("Back"))
            Time.sleepUntil(() -> !GrandExchangeSetup.isOpen(), 350, 2500);
    }

    private static int getPrice(RSGrandExchangeOffer.Type type, int price, int maxAttempts, int priceChangeInterval, int panicItemPrice) {
        if (attemptsTried > 0) {
            if (type.equals(RSGrandExchangeOffer.Type.BUY)) {
                if (attemptsTried > maxAttempts) {
                    if (panicItemPrice == 0) panicItemPrice = price + (priceChangeInterval * 10);
                    price = panicItemPrice;
                } else price = price + (attemptsTried * priceChangeInterval);
            }
            if (type.equals(RSGrandExchangeOffer.Type.SELL)) {
                if (attemptsTried > maxAttempts) {
                    if (panicItemPrice == 0) panicItemPrice = price - (priceChangeInterval * 10);
                    price = panicItemPrice;
                } else price = price - (attemptsTried * priceChangeInterval);
            }
        }
        return price;
    }

    private static boolean exchangeSuccess(RSGrandExchangeOffer.Type type, String name, int quantity){
        if (type.equals(RSGrandExchangeOffer.Type.BUY))
            return buySuccess(name,quantity);
        else return sellSuccess(name);
    }

    private static boolean buySuccess(String name, int quantity){
        return Inventory.getCount(true,name) >= quantity;
    }
    private static boolean sellSuccess(String name){
        return !Inventory.contains(name);
    }
}
