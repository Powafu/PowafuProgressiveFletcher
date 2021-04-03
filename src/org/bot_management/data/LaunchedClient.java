package org.bot_management.data;

import okhttp3.*;
import org.bot_management.BotManagementFileHelper;
import org.rspeer.ui.Log;

import java.io.IOException;
import java.net.Proxy;

public class LaunchedClient {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().proxy(Proxy.NO_PROXY).build();

    private String lastUpdate;
    private String proxyIp;
    private String machineName;
    private String scriptName;
    private String rsn;
    private String runescapeEmail;
    private String tag;

    public LaunchedClient(String lastUpdate, String proxyIp, String machineName, String scriptName, String rsn, String runescapeEmail, String tag) {
        this.lastUpdate = lastUpdate;
        this.proxyIp = proxyIp;
        this.machineName = machineName;
        this.scriptName = scriptName;
        this.rsn = rsn;
        this.runescapeEmail = runescapeEmail;
        this.tag = tag;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getRsn() {
        return rsn;
    }

    public String getRunescapeEmail() {
        return runescapeEmail;
    }

    public String getTag() {
        return tag;
    }

    public boolean kill() throws IOException {
        final String apiKey = BotManagementFileHelper.getApiKeyOrThrow();

        final Request request = new Request.Builder()
                .url("https://services.rspeer.org/api/botLauncher/sendNew?message=:kill&tag=" + tag)
                .header("ApiClient", apiKey)
                .post(RequestBody.create(MediaType.parse(""), ""))
                .build();

        final Response response = HTTP_CLIENT.newCall(request).execute();
        return response.isSuccessful();
    }

    public boolean sendMessage(String message) {
        Log.fine("Sending message: " + message);
        try {
            final String apiKey;

            apiKey = BotManagementFileHelper.getApiKeyOrThrow();

            final Request request = new Request.Builder()
                    .url("https://services.rspeer.org/api/botLauncher/sendNew?message=" + message + "&tag=" + tag)
                    .header("ApiClient", apiKey)
                    .post(RequestBody.create(MediaType.parse(""), ""))
                    .build();

            final Response response = HTTP_CLIENT.newCall(request).execute();
            return response.isSuccessful();

        } catch (IOException e) {
            Log.severe("Unable to send message: " + message);
            Log.severe(e.getMessage());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Client{" +
                "lastUpdate='" + lastUpdate + '\'' +
                ", proxyIp='" + proxyIp + '\'' +
                ", machineName='" + machineName + '\'' +
                ", scriptName='" + scriptName + '\'' +
                ", rsn='" + rsn + '\'' +
                ", runescapeEmail='" + runescapeEmail + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
