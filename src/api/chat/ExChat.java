package api.chat;

import org.rspeer.runetek.api.component.chatter.Chat;

public class ExChat {

    public static boolean send(String msg) {
//        if (Store.getMods() != null && Store.getMods().length > 0) {
//            Log.severe("PMOD FOUND! Can't advertise right now...");
//        } else {
//            return Chat.send(msg);
//        }
//        return false;
        return Chat.send(msg);
    }
}
