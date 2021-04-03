package message.type;

public enum MessageType {
    REQUEST,
    MULE_NOTICE,
    TIME_NOTICE,
    WORLD_NOTICE;

    public static MessageType fromString(String str) {
        for (MessageType value : MessageType.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        return null;
    }
}
