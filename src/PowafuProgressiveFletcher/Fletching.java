package PowafuProgressiveFletcher;

import api.IsReady;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.Production;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.AnimationListener;
import org.rspeer.runetek.event.listeners.SkillListener;
import org.rspeer.runetek.event.types.AnimationEvent;
import org.rspeer.runetek.event.types.SkillEvent;
import org.rspeer.script.task.Task;

public class Fletching extends Task implements AnimationListener, SkillListener {
    private StopWatch animationTimer;

    @Override
    public boolean validate()
        {return IsReady.isReady() && Main.hasAllMaterials();}

    @Override
    public int execute()
        {
            if (GrandExchange.isOpen() || Bank.isOpen())
            {
                if (Interfaces.closeAll())
                    Time.sleepUntil(() -> !GrandExchange.isOpen() && !Bank.isOpen(), 125, 3000);
                return Random.nextInt(250,500);
            }
            if (canFletch())
                fletch();
            return Random.nextInt(250,500);
        }

    private boolean canFletch(){return animationTimer == null
            || animationTimer.getElapsed().toMillis() >= Random.nextInt(1250,5000);}

    private void fletch()
        {
            if (Production.isOpen())
            {
                handleProduction();
                return;
            }

            if (!Inventory.isItemSelected())
            {
                selectItem();
                return;
            }

            selectSecond();
        }

    private void handleProduction()
        {
            if (Skills.getLevel(Skill.FLETCHING) < 20)
                Production.initiate("Arrow shaft");
            else Production.initiate(Bow.getTargetBow().getProduct());
            Time.sleepUntil(() -> Players.getLocal().isAnimating(), 650, Random.nextInt(1200,1650));
        }

    private void selectSecond()
        {
            if (Skills.getLevel(Skill.FLETCHING) < 20)
                Inventory.getFirst("Logs").interact("Use");
            else Inventory.getFirst(FletchBanking.getUnstrung()).interact("Use");
            Time.sleepUntil(Production::isOpen,175,Random.nextInt(800,1650));
        }

    private void selectItem()
        {
            if (Skills.getLevel(Skill.FLETCHING) < 20)
                Inventory.getFirst("Knife").interact("Use");
            else Inventory.getFirst(FletchBanking.string).interact("Use");
            Time.sleepUntil(Inventory::isItemSelected,150,Random.nextInt(800,1650));
        }

    @Override
    public void notify(AnimationEvent animationEvent)
        {
            if (animationEvent.getSource().equals(Players.getLocal()) || Players.getLocal().isAnimating())
                if (animationTimer != null)
                    animationTimer.reset();
                else animationTimer = StopWatch.start();
        }

    @Override
    public void notify(SkillEvent skillEvent)
        {
            Main.setBowsMade(Main.getBowsMade() + 1);
            Main.setXpGained(Bow.getTargetBow().getXp());
            Main.setMaterialsLeft(Main.getMaterialsLeft() - 1);
        }
}
