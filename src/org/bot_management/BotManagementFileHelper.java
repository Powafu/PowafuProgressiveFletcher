package org.bot_management;

import org.rspeer.script.Script;
import store.Config;

import java.io.File;

public class BotManagementFileHelper {

    public static File getCurrentVersionFile() {
        return getFile("cache" + File.separator + "current_version");
    }

    public static File getFile(String path) {
        return new File(Script.getDataDirectory().getParent().getParent() + File.separator + path);
    }

    public static String getApiKeyOrThrow() {
        return Config.getApiKey();
    }
}
