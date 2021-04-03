package PowafuProgressiveFletcher;

import api.timer.Timer;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.*;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import store.Config;
import store.Store;

public class Muling extends Task {

    private boolean hasBankedForMuling = false;

    @Override
    public boolean validate() {
        return Store.isMuling();
    }

    @Override
    public int execute() {
        if (!hasBankedForMuling && Trade.isOpen()) {
            Store.setTask("Closing trade");
            Trade.decline();
            return Config.getLoopReturn();
        }
        if (Trade.isOpen(false)) {
            Store.setTask("1st Trade Screen");
            if (EnterInput.isOpen()) {
                Store.setTask("Entering input");
                EnterInput.initiate(Random.nextInt(99999999, 99999999));
                return Config.getLoopReturn();
            }
            Item item = Inventory.getFirst("Coins");
            if (item != null) {
                Store.setTask("Offering all " + item.getName());
                Trade.offerAll(Inventory.getFirst(a->true).getName());
                Time.sleepUntil(() -> Trade.contains(true, item.getName()), 2000);
                return Config.getLoopReturn();
            }
            if (Trade.isWaitingForMe()) {
                Store.setTask("Accepting first trade window");
                Trade.accept();
                return Config.getLoopReturn();
            }
            Store.setTask("Waiting for other to accept second");
            return Config.getLoopReturn();
        }
        if (Trade.isOpen(true)) {
            Store.setTask("2nd Trade Screen");
            if (Trade.isWaitingForMe()) {
                Store.setTask("Accepting second");
                Trade.accept();
                return Config.getLoopReturn();
            }
            Store.setTask("Waiting for other to accept second");
            return Config.getLoopReturn();
        }

        if (Bank.isOpen()) {
            if (Bank.isEmpty()) {
                Store.setTask("Waiting for bank to load. Bank Empty");
                return Config.getLoopReturn();
            }
            if (Inventory.containsAnyExcept("Coins")) {
                Store.setTask("Depositing Inventory");
                Bank.depositInventory();
                return Config.getLoopReturn();
            }
            Item item = Bank.getFirst("Coins");
            if (item != null) {
                Store.setTask("Withdrawing coins");
                Bank.withdrawAll(item.getName());
                Time.sleepUntil(() -> Inventory.contains(item.getName()), 1500);
                return Config.getLoopReturn();
            }
            Store.setTask("Closing bank");
            hasBankedForMuling = true;
            Bank.close();
            return Config.getLoopReturn();
        }

        if (!Timer.MULE_TIMEOUT_TIME.isRunning()) {
            Log.severe("Mule timer timed out. Going back to fletching");
            Store.setIsMuling(false);
            hasBankedForMuling = false;
            Timer.MULE_TIMEOUT_TIME.stop();
            return Config.getLoopReturn();
        }
        if (!hasBankedForMuling) {
            Store.setTask("Opening bank");
            Bank.open();
            Time.sleepUntil(() -> Players.getLocal().isMoving(), 2000);
            Time.sleepUntil(() -> Bank.isOpen() || !Players.getLocal().isMoving(), 10000);
            return Config.getLoopReturn();
        }
        if (Time.sleepUntil(Inventory::isEmpty, 5000)) {
            Log.fine("Done muling. Going back to fletching");
            Store.setIsMuling(false);
            hasBankedForMuling = false;
            Timer.MULE_TIMEOUT_TIME.stop();
            return Config.getLoopReturn();
        }
        if (Worlds.getCurrent() != Store.getMuleInfo().getWorld()) {
            Store.setTask("Hopping to Mule World");
            if (Dialog.canContinue()) Dialog.processContinue();
            WorldHopper.hopTo(Store.getMuleInfo().getWorld());
            return Config.getLoopReturn();
        }
        Player muler = Players.getNearest(Store.getMuleInfo().getRsn());
        if (muler != null) {
            Timer.MULE_TIMEOUT_TIME.restart();
            Store.setTask("Offering trade to mule");
            muler.interact("Trade with");
            Time.sleepUntil(() -> Trade.isOpen() || Config.isStopping(), Random.nextInt(5000, 10000));
            return Config.getLoopReturn();
        }
        Store.setTask("Waiting for mule");
        return Config.getLoopReturn();
    }


}
