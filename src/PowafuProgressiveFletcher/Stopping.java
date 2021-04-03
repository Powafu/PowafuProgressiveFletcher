package PowafuProgressiveFletcher;

import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import store.Config;

public class Stopping extends Task {
    @Override
    public boolean validate() {
        return Config.isStopping();
    }

    @Override
    public int execute() {
        Log.severe("Stopping");
        if (Main.getMessenger() != null) {
            Main.getMessenger().dispose();
        }
        Log.fine("Thanks for using Powafu Progressive Fletcher!");
        return -1;
    }
}
