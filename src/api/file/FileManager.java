package api.file;

import org.rspeer.script.Script;
import org.rspeer.ui.Log;
import store.Config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    private static final String DEFAULT_SAVE_PATH = Script.getDataDirectory().toString() + "/";
    private static final String API_KEY_FILE_NAME = "api_key.txt";

    private static final String CONFIG_SAVE_PATH = Script.getDataDirectory().toString() + "/ADivorcedFork/BurntFoodBuyer/";

    public static boolean fileExists(String fileLocation) {
        return new File(fileLocation).isFile();
    }

    public static boolean createFile(String fileLocation) {
        try {
            boolean created = new File(fileLocation).createNewFile();
            if (created) {
                Log.fine("Created file: " + fileLocation);
                return true;
            }
        } catch (IOException e) {
            Log.severe("Can't create file | " + e.getMessage());
            return false;
        }
        Log.severe("Can't create file: " + fileLocation);
        return false;
    }

    private static boolean createBaseIfNotExists(String savePath){
        Path path = Paths.get(savePath);
        if(!Files.exists(path)){
            Log.info("Attempting to create base at: " + savePath);
            try {
                return Files.createDirectories(path) != null;
            } catch (IOException e) {
                Log.severe("Error while creating base: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public static void setApiKey() {
        String fileLocation = DEFAULT_SAVE_PATH + API_KEY_FILE_NAME;
        if (!fileExists(fileLocation)) {
            Log.severe("File does not exist!");
            return;
        }
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileLocation));
            Config.setApiKey(new String(encoded, StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            Log.severe(e.getMessage());
        }
    }
}
