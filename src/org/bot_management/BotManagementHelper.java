package org.bot_management;

import org.bot_management.data.LaunchedClient;
import org.rspeer.RSPeer;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.Log;
import store.Config;
import store.Store;

import java.util.List;

public class BotManagementHelper {


    public static LaunchedClient getClient(String senderTag) {
        if (senderTag == null) return null;
        List<LaunchedClient> clients = Store.getLaunchedClients();
        for (LaunchedClient client : clients) {
            if (client.getTag().equals(senderTag)) {
                return client;
            }
        }
        Store.setLaunchedClients(BotManagement.getRunningClients());
        clients = Store.getLaunchedClients();
        for (LaunchedClient client : clients) {
            if (client.getTag().equals(senderTag)) {
                return client;
            }
        }
        return null;
    }

    public static void broadcastMessage(String msg) {
        if (Store.getOurClient() == null) {
            Log.severe("Can't broadcast message. Our client null.");
            return;
        }
        List<LaunchedClient> clients = Store.getLaunchedClients();
        for (LaunchedClient client : clients) {
            if (!client.getTag().equals(RSPeer.getClientTag())) {
                client.sendMessage(msg);
            }
        }
    }

    public static boolean findMuleClient() {
        for (LaunchedClient launchedClient : Store.getLaunchedClients()) {
            String scriptName = launchedClient.getScriptName();
            if (scriptName != null && scriptName.trim().equals(Config.MULE_CLIENT_SCRIPT_NAME)) {
                Log.fine("Found the account client.");
                Store.setMuleClient(launchedClient);
                return true;
            }
        }
        Log.severe("Couldn't find the account client");
        Store.setLaunchedClients(null);
        return false;
    }

    public static void findOurLaunchedClientByTag(String tag) {
        Log.fine("Finding client by tag " + tag);
        for (LaunchedClient launchedClient : Store.getLaunchedClients()) {
            String clientTag = launchedClient.getTag();
            if (clientTag != null && clientTag.equals(tag)) {
                Log.fine("Found our client.");
                Store.setOurClient(launchedClient);
                return;
            }
        }
        Log.severe("Couldn't find our client by tag. Fetching clients again after 5 seconds.");
        Store.setLaunchedClients(null);
        Time.sleep(5000);
    }
}
