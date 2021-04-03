package api;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.EnterInput;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.chatter.BefriendedPlayers;
import org.rspeer.runetek.api.component.chatter.Chat;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.providers.RSBefriendedPlayer;
import org.rspeer.ui.Log;

public class ExFriends {

    private static final int FRIENDS_PARENT_INDEX = 429;
    public static final int FRIENDS_LIST_INDEX = 11;

    public static final InterfaceAddress ADD_FRIEND_BUTTON = new InterfaceAddress(() ->
            Interfaces.getFirst(FRIENDS_PARENT_INDEX, comp -> comp.containsAction("Add Friend"))); // 14
    public static final InterfaceAddress REMOVE_FRIEND_BUTTON = new InterfaceAddress(() ->
            Interfaces.getFirst(FRIENDS_PARENT_INDEX, comp -> comp.containsAction("Delete Friend"))); // 16


    public static boolean isFriend(String friendName) {
        return BefriendedPlayers.getFirst(friendName) != null;
    }

    public static boolean addFriend(String name) {
        if (Tabs.getOpen() != Tab.FRIENDS_LIST) {
            Tabs.open(Tab.FRIENDS_LIST);
            Time.sleepUntil(() -> Tabs.getOpen() != Tab.FRIENDS_LIST, 1500);
        }

        InterfaceComponent addFriendButton = Interfaces.lookup(ADD_FRIEND_BUTTON);
        if (addFriendButton == null) {
            Log.severe("Can't find add friend button");
            return false;
        }
        addFriendButton.interact("Add Friend");
        boolean enterInputOpen = Time.sleepUntil(EnterInput::isOpen, 2000);
        if (!enterInputOpen) {
            Log.severe("Enter input isn't open. Can't add friend");
            return false;
        }
        EnterInput.initiate(name);
        return Time.sleepUntil(() -> BefriendedPlayers.getFirst(player -> player.getName().equals(name)) != null, 3000);
    }

    public static boolean removeFriend(String name) {
        if (Tabs.getOpen() != Tab.FRIENDS_LIST) {
            Tabs.open(Tab.FRIENDS_LIST);
            Time.sleepUntil(() -> Tabs.getOpen() != Tab.FRIENDS_LIST, 1500);
        }

        InterfaceComponent addFriendButton = Interfaces.lookup(REMOVE_FRIEND_BUTTON);
        if (addFriendButton == null) {
            Log.severe("Can't find add friend button");
            return false;
        }
        addFriendButton.interact("Remove Friend");
        boolean enterInputOpen = Time.sleepUntil(EnterInput::isOpen, 2000);
        if (!enterInputOpen) {
            Log.severe("Enter input isn't open. Can't add friend");
            return false;
        }
        EnterInput.initiate(name);
        return Time.sleepUntil(() -> BefriendedPlayers.getFirst(player -> player.getName().equals(name)) == null, 3000);
    }

    public static boolean isOnline(String friendName) {
        RSBefriendedPlayer friend = BefriendedPlayers.getFirst(friendName);
        return friend != null && friend.getWorld() > 0;
    }

    public static boolean message(String friendName, String msg) {
        Chat.send(friendName, msg);
        boolean enterInputOpen = Time.sleepUntil(EnterInput::isOpen, 2000);
        if (!enterInputOpen) {
            Log.severe("Enter input isn't open. Can't add friend");
            return false;
        }
        EnterInput.initiate(msg);
        return Time.sleepUntil(() -> !EnterInput.isOpen(), 3000);
    }
}
