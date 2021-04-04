package PowafuProgressiveFletcher;

import api.IsReady;
import message.Messenger;
import org.rspeer.runetek.api.ClientSupplier;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.TaskScript;
import org.rspeer.ui.Log;
import store.Config;
import store.Store;

import java.awt.*;
import java.awt.Font;

@ScriptMeta(category = ScriptCategory.FLETCHING, name = "Powafu Progressive Fletcher", desc = "Exactly what you think",
        developer =  "WishToBeGone",version = 0.1)
public final class Main extends TaskScript implements RenderListener {
    private static StopWatch timeElapsed;

    private static Messenger messenger;

    private static int bowsMade,startLvl,materialsLeft,storedLvl = 1;
    private static double xpGained;
    public static int getBowsMade(){return bowsMade;}
    public static void setBowsMade(int bowsMade){Main.bowsMade = bowsMade;}
    public static int getStartLvl(){return startLvl;}
    public static void setStartLvl(int startLvl){Main.startLvl = startLvl;}
    public static void setXpGained(double xp){Main.xpGained = xpGained + xp;}
    public static void setMaterialsLeft(int materialsLeft) {Main.materialsLeft = materialsLeft;}
    public static int getMaterialsLeft(){return materialsLeft;}

    public static Messenger getMessenger() {
        return messenger;
    }

    private final int gainedLevel() {
        int currentLvl = Skills.getLevel(Skill.FLETCHING);

        if (storedLvl < currentLvl)
            storedLvl = currentLvl;

        return storedLvl - startLvl;
    }

    /*Commented all this out as none of these errors should ever be hit, plus its better to put error
        along with the code that produces the error imo */

    /*private final void errorMessage() {
        if (Login.getLoginState() == STATE.INGAME) {
            boolean var10001;
            if (!Banking.isInBank()) {
                this.println("Player not in Bank");
                var10001 = false;
            } else if (Inventory.getCount(new String[]{(new Powagressive()).logType()}) < 1) {
                this.println("No logs to fletch. Stopping script.");
                var10001 = false;
            } else if (Inventory.getCount(new String[]{(new Powagressive()).knife}) < 1) {
                this.println("No knife found. Stopping script.");
                var10001 = false;
            } else {
                this.println("Unknown Error");
                var10001 = false;
            }

            this.shouldRun = var10001;
        }

    }*/

    @Override
    public void notify(RenderEvent renderEvent)
        {
            Graphics2D g = (Graphics2D) renderEvent.getSource();
            int alpha = 127;
            int alpha2 = 186;
            Color myColour = new Color(150, 150, 150, alpha);
            Color myColour2 = new Color(1, 1, 125, alpha2);
            g.setColor(myColour);
            g.fillRect(5, 250, 510, 90);
            g.setColor(myColour2);
            g.setFont(new Font("Calibri", Font.BOLD, 24));
            g.drawString("Powagressive Open Source Fletcher", 20, 280);
            g.setFont(new Font("Calibri", Font.BOLD, 16));
            g.setColor(Color.yellow);
            g.drawString("| Experience Gained: " + Math.ceil(xpGained), 280, 300);
            g.drawString("| Time Elapsed: " + timeElapsed.toElapsedString(), 10, 330);
            g.drawString("| Bows Fletched: " + bowsMade, 280, 315);

            /*This check will prevent shit under it from loading if not logged in
                this is very very important, if you start the script logged out and the code below is ran
                it will cause the script to crash due to being unable to grab some values
             */
            if (!IsReady.isReady()) return;
            if (Skills.getLevel(Skill.FLETCHING) < 20)
                g.drawString("| Current Cut: " + "Arrow shaft", 10, 315);
            else
                g.drawString("| Current Cut: " + Bow.getTargetBow().getProduct(), 10, 315);
            g.drawString("| Fletching Level: " + Skills.getLevel(Skill.FLETCHING) + " (" + gainedLevel() + ")", 10, 300);
            /*Uhhhhh do we really want to keep track of total time slept??*/
            /*g.drawString(String.valueOf(this.afkTicks), 5, 25);*/
        }

    @Override
    public void onStart()
        {
            // Messenger
            messenger = new Messenger();

            // Set starting world
            if (Game.isLoggedIn()) {
                Log.fine("Setting currently logged in account as our account.");
                Store.setStartingWorld(Worlds.getCurrent());
            }
            else {
                Store.setStartingWorld(ClientSupplier.get().getCurrentWorld());
            }

            submit(new Initialize(this));
            timeElapsed = StopWatch.start();
        }

    @Override
    public void onStop() {
        if (Main.getMessenger() != null) {
            Main.getMessenger().dispose();
        }
        Config.setIsStopping(true);
        Log.info("Script ended, have a nice day!");
        super.onStop();
    }

    public static boolean hasAllMaterials()
        {
            if (Skills.getLevel(Skill.FLETCHING) < 20)
                return Inventory.getFirst(i -> i.getName().equals("Knife") && !i.isNoted()) != null
                        && Inventory.getFirst(i -> i.getName().equals("Logs") && !i.isNoted()) != null;

            return Inventory.contains(FletchBanking.getUnstrung()) && Inventory.contains(FletchBanking.string);
        }
}
