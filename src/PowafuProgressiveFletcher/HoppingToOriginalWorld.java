package PowafuProgressiveFletcher;

import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.component.*;
import org.rspeer.script.task.Task;
import store.Config;
import store.Store;

public class HoppingToOriginalWorld extends Task {


    @Override
    public boolean validate() {
        return Worlds.getCurrent() != Store.getStartingWorld();
    }

    @Override
    public int execute() {
        if (GrandExchange.isOpen() || Bank.isOpen()) {
            Store.setTask("Closing interfaces");
            Interfaces.closeAll();
            return Config.getLoopReturn();
        }
        Store.setTask("Hopping to original world");
        if (Dialog.canContinue()) Dialog.processContinue();
        WorldHopper.hopTo(Store.getStartingWorld());
        return Config.getLoopReturn();
    }
}
