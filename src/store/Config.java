package store;

import org.rspeer.runetek.api.commons.math.Random;

public class Config {

    public static final String MULE_CLIENT_SCRIPT_NAME = "Powafu Progressive Fletcher Mule";
    public static final int LOW_COINS_TO_TRIGGER_MULING = 200000; // Put minimum amount of coins needed for restocking
    public static final int HIGH_COINS_TO_TRIGGER_MULING = 2000000; // This amount of coins or higher triggers muling

    private static boolean isSetup = true;
    private static boolean isStopping = false;

    private static String apiKey;

    public static int getQuickLoopReturn() {
        return Random.nextInt(50, 200);
    }

    public static int getLoopReturn() {
        return Random.nextInt(400, 1000);
    }

    public static boolean isSetup() {
        return isSetup;
    }

    public static void setIsSetup(boolean isSetup) {
        Config.isSetup = isSetup;
    }

    public static boolean isStopping() {
        return isStopping;
    }

    public static void setIsStopping(boolean isStopping) {
        Config.isStopping = isStopping;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        Config.apiKey = apiKey;
    }
}
