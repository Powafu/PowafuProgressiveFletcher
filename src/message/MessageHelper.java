package message;

import message.type.MessageType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class MessageHelper {

    public static Map<String, String> mapFromUrl(String url) {
        Map<String, String> map = new LinkedHashMap<>();
        String[] keyValues = url.split(";");
        for (String keyValue : keyValues) {
            int index = keyValue.indexOf(':');
            String key = keyValue.substring(0, index);
            String value = keyValue.substring(index + 1);
            map.put(key, value);
        }
        return map;
    }

    public static MessageType messageTypeFromUrl(String url) {
        int startIdx = url.indexOf(':');
        int endIdx = url.indexOf(';');
        if (!url.startsWith("messageType:")) {
            return null;
        }
        String msgTypeStr = url.substring(startIdx + 1, endIdx);
        return MessageType.fromString(msgTypeStr);
    }

    public static String generateMessageID() {
        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        return salt.toString();
    }

    public static String parseMessageID(String msg) {
        Map<String, String> map = MessageHelper.mapFromUrl(msg);
        return map.get("messageID");
    }

//    public static boolean isUnique(String msg) {
//        Map<String, String> map = message.MessageHelper.mapFromUrl(msg);
//        String messageID = map.get("messageID");
//        return messageID != null && !Store.getMessageIDs().contains(messageID);
//    }
}
