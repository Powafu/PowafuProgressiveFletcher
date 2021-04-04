package store;

import api.mule.MuleInfo;
import org.bot_management.data.LaunchedClient;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.ui.Log;

import java.util.List;

public class Store {

    public static final Position[] VALID_POSITIONS = {
            new Position(3162, 3487, 0),
            new Position(3163, 3486, 0),
            new Position(3164, 3486, 0),
            new Position(3165, 3486, 0),
            new Position(3166, 3486, 0),
            new Position(3167, 3487, 0),
            new Position(3168, 3488, 0),
            new Position(3169, 3488, 0),
            new Position(3168, 3487, 0),
            new Position(3169, 3487, 0),
            new Position(3167, 3486, 0),
            new Position(3168, 3486, 0),
            new Position(3169, 3486, 0),
            new Position(3169, 3485, 0),
            new Position(3168, 3485, 0),
            new Position(3166, 3485, 0),
            new Position(3167, 3484, 0),
            new Position(3167, 3485, 0),
            new Position(3165, 3485, 0),
            new Position(3164, 3485, 0),
            new Position(3163, 3485, 0),
            new Position(3162, 3485, 0),
            new Position(3162, 3485, 0),
            new Position(3162, 3486, 0),
            new Position(3161, 3486, 0),
            new Position(3161, 3487, 0),
            new Position(3160, 3487, 0),
            new Position(3160, 3486, 0),
            new Position(3160, 3485, 0),
            new Position(3161, 3485, 0),
            new Position(3161, 3484, 0),
            new Position(3162, 3484, 0),
            new Position(3164, 3484, 0),
            new Position(3160, 3488, 0),
            new Position(3161, 3488, 0),
            new Position(3163, 3484, 0),
            new Position(3165, 3484, 0),
            new Position(3166, 3484, 0),
            new Position(3168, 3484, 0)
    };

    private static String task = "Starting";

    private static LaunchedClient ourClient;
    private static LaunchedClient muleClient;
    private static List<LaunchedClient> launchedClients;

    private static MuleInfo muleInfo = new MuleInfo();

    private static boolean isMuling = false;

    private static int startingWorld = 302;

    public static String getTask() {
        return task;
    }

    public static void setTask(String task) {
        Log.info("TASK: " + task);
        Store.task = task;
    }

    public static LaunchedClient getOurClient() {
        return ourClient;
    }

    public static void setOurClient(LaunchedClient ourClient) {
        Store.ourClient = ourClient;
    }

    public static LaunchedClient getMuleClient() {
        return muleClient;
    }

    public static void setMuleClient(LaunchedClient muleClient) {
        Store.muleClient = muleClient;
    }

    public static List<LaunchedClient> getLaunchedClients() {
        return launchedClients;
    }

    public static void setLaunchedClients(List<LaunchedClient> launchedClients) {
        Store.launchedClients = launchedClients;
    }

    public static MuleInfo getMuleInfo() {
        return muleInfo;
    }

    public static void setMuleInfo(MuleInfo muleInfo) {
        Store.muleInfo = muleInfo;
    }

    public static int getStartingWorld() {
        return startingWorld;
    }

    public static void setStartingWorld(int startingWorld) {
        Store.startingWorld = startingWorld;
    }

    public static boolean isMuling() {
        return isMuling;
    }

    public static void setIsMuling(boolean isMuling) {
        Store.isMuling = isMuling;
    }
}
