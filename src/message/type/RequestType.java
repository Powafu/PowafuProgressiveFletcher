package message.type;

public enum RequestType {
    MULE,
    TIME;

    public static RequestType fromString(String str) {
        for (RequestType value : RequestType.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        return null;
    }
}
