package message;

import api.timer.Timer;
import message.type.MessageType;
import message.type.MuleNotice;
import message.type.TimeNotice;
import message.type.WorldNotice;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.RemoteMessageListener;
import org.rspeer.runetek.event.types.RemoteMessageEvent;
import org.rspeer.ui.Log;
import store.Store;

public class Messenger implements RemoteMessageListener {

    public Messenger() {
        Log.info("Registering messenger");
        Game.getEventDispatcher().register(this);
    }

    public void dispose() {
        Game.getEventDispatcher().deregister(this);
    }

    @Override
    public void notify(RemoteMessageEvent e) {
        String msg = e.getSource().getMessage();
        Log.info("Received message: " + msg);

        MessageType type = MessageHelper.messageTypeFromUrl(msg);
        if (type == null) {
            Log.severe("Unknown message type: " + msg);
            return;
        }

        switch(type) {
            case REQUEST:
                Log.severe("Got request message for some reason. Ignoring: " + msg);
                return;

            case MULE_NOTICE:
                MuleNotice muleNotice = MuleNotice.fromUrl(msg);
                if (!muleNotice.isValid()) {
                    Log.severe("MuleNotice message not valid");
                    return;
                }
                Log.fine("Muler said to mule");
                if (!Store.isMuling()) {
                    Log.info("Switching to mule");
                    Store.setIsMuling(true);
                    Timer.MULE_TIMEOUT_TIME.restart();
                    Store.getMuleInfo().setRsn(muleNotice.getRsn());
                    Store.getMuleInfo().setWorld(muleNotice.getWorld());
                }
                else {
                    Log.info("Already muling so going to ignore mule request.");
                }
                return;

            case TIME_NOTICE:
                TimeNotice timeNotice = TimeNotice.fromUrl(msg);
                if (!timeNotice.isValid()) {
                    Log.severe("TimeNotice message not valid");
                    return;
                }
                Log.severe("Ignoring time notice");
                return;

            case WORLD_NOTICE:
                WorldNotice worldNotice = WorldNotice.fromUrl(msg);
                if (!worldNotice.isValid()) {
                    Log.severe("WorldNotice message not valid");
                    return;
                }
                if (Game.isLoggedIn() && worldNotice.getRsn().equals(Players.getLocal().getName())) {
                    Log.severe("WorldNotice is from us! Ignoring...");
                    return;
                }
                Log.fine("Updating worlds");
                return;

            default:
                return;
        }
    }
}
