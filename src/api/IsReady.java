package api;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.scene.Players;

public class IsReady {
    private static final InterfaceComponent PLAY_BUTTON = Interfaces.getFirst(378, ic -> ic.containsAction("Play") && ic.isVisible());

    public static boolean isReady() {
        if (PLAY_BUTTON == null && Game.isLoggedIn() && !Game.isLoadingRegion() && Game.getState() == Game.STATE_IN_GAME && Players.getLocal() != null) return true;
        if (PLAY_BUTTON != null) {
            InterfaceComponent playButton = Interfaces.getFirst(378, ic ->ic.containsAction("Play"));
            Time.sleep(600);
            playButton.click();
            Time.sleepUntil(() -> Game.isLoggedIn() && !Game.isLoadingRegion() && Game.getState() == Game.STATE_IN_GAME && Players.getLocal() != null, 350,10000);
        }
        return PLAY_BUTTON == null && Game.isLoggedIn() && !Game.isLoadingRegion() && Game.getState() == Game.STATE_IN_GAME && Players.getLocal() != null;
    }
}
