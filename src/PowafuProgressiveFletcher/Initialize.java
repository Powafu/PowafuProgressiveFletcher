package PowafuProgressiveFletcher;

import api.IsReady;
import api.file.FileManager;
import org.bot_management.BotManagement;
import org.bot_management.BotManagementHelper;
import org.rspeer.RSPeer;
import org.rspeer.networking.dax.walker.models.RSBank;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import store.Config;
import store.Store;

public class Initialize extends Task {
    private final Main main;

    public Initialize(Main main) {this.main = main;}

    private final Area GE_AREA = Area.rectangular(3162, 3492, 3167, 3487);
    private boolean hasSetStart;

    @Override
    public boolean validate() { return true; }

    @Override
    public int execute()
        {
            if (!IsReady.isReady())
                return 1000;

            if (Config.getApiKey() == null || Config.getApiKey().length() != 70) {
                Store.setTask("Setting API key");
                FileManager.setApiKey();
                return Config.getLoopReturn();
            }
            if (Store.getLaunchedClients() == null) {
                Store.setTask("Finding launched clients.");
                Store.setLaunchedClients(BotManagement.getRunningClients());
                Time.sleepUntil(() -> Store.getLaunchedClients() != null || Config.isStopping(), 30000);
                return Config.getQuickLoopReturn();
            }
            if (Store.getOurClient() == null) {
                Store.setTask("Finding our launched client");
                BotManagementHelper.findOurLaunchedClientByTag(RSPeer.getClientTag());
                return Config.getQuickLoopReturn();
            }
            if (Store.getMuleClient() == null) {
                Store.setTask("Finding mule client.");
                boolean found = BotManagementHelper.findMuleClient();
                if (!found) {
                    Log.severe("Waiting to find mule client");
                    Time.sleep(15000, 20000);
                    return Config.getQuickLoopReturn();
                }
                return Config.getQuickLoopReturn();
            }

            if (Main.getStartLvl() <= 1 && !hasSetStart)
            {
                Main.setStartLvl(Skills.getLevel(Skill.FLETCHING));
                hasSetStart = true;
            }
            if (!GE_AREA.contains(Players.getLocal()))
            {
                Movement.getDaxWalker().walkToBank(RSBank.GRAND_EXCHANGE);
                return 300;
            }
            //does this even work??
            Log.info("Submit the rest of the tasks and remove initializer");
            main.submit(new Stopping(), new Muling(), new HoppingToOriginalWorld(), new Restock(), new FletchBanking(), new Fletching());
            Log.info("Tasks submitted, remove initializer");
            main.remove(this);
            return 1000;
        }
}
