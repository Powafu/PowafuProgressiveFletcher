package api.timer;

import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.ui.Log;

import java.time.Duration;
import java.util.logging.Level;

public enum Timer {

    TRADE_CHANGE(5), // Time they have to change trade
    ADVERT(6),
    TRADE(60), // Seconds for trade
    CHANGE_POSITION(10 * 60), //(60 * 10); // Seconds between world hop
    WORLD_HOP(20 * 60),
    FIND_ACCOUNT_CLIENT(5 * 60), // find account client
    MULE_TIMEOUT_TIME(4 * 60),
    SEND_CURRENT_WORLD(3 * 60),
    ;

    private StopWatch timer;
    private int cooldown;
    private long timeLeftAtPause;

    Timer(final int cooldown) {
        this.timer = StopWatch.fixed(Duration.ofSeconds(0));
        this.cooldown = cooldown;
    }

    public StopWatch getTimer() {
        return timer;
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    public long getRemainingSeconds() {
        return timer.getRemaining().getSeconds();
    }

    public String getRemainingString() {
        return timer.toRemainingString();
    }

    public void restart() {
        restart(0);
    }

    public void restart(int deviation) {
        timer = StopWatch.fixed(Duration.ofSeconds(Random.nextInt(cooldown - deviation, cooldown + deviation + 1)));
    }

    public void pause() {
        timeLeftAtPause = timer.getRemaining().getSeconds() * -1;
        timer = StopWatch.fixed(Duration.ofSeconds(0));
        Log.log(Level.WARNING, this.toString() + " Timer", "Pausing with " + timeLeftAtPause + " seconds left.");
    }

    public void resume() {
        Log.log(Level.WARNING, this.toString() + " Timer","Continuing timer with " + timeLeftAtPause + " seconds left.");
        timer = StopWatch.fixed(Duration.ofSeconds(this.timeLeftAtPause));
    }

    public void stop() {
        timer = StopWatch.fixed(Duration.ofSeconds(0));
        timeLeftAtPause = 0;
    }
}
