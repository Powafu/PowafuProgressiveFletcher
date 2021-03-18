package PowafuProgressiveFletcher;

import api.IsReady;
import org.rspeer.networking.dax.walker.models.RSBank;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

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
            main.submit(new Restock(), new FletchBanking(), new Fletching());
            Log.info("Tasks submitted, remove initializer");
            main.remove(this);
            return 1000;
        }
}
