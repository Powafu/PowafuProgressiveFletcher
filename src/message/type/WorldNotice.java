package message.type;

import message.MessageHelper;

import java.util.Map;


public class WorldNotice extends Message {

    private final MessageType messageType = MessageType.WORLD_NOTICE;
    private final int currentWorld;
    private final int previousWorld;
    private final String rsn;

    public WorldNotice(String messageID, String senderTag, String currentWorld, String previousWorld, String rsn) {
        super(messageID, senderTag);
        this.currentWorld = Integer.parseInt(currentWorld);
        this.previousWorld = Integer.parseInt(previousWorld);
        this.rsn = rsn;
    }

    @Override
    public boolean isValid() {
        return getSender() != null && getMessageID() != null && rsn != null;
    }

    @Override
    public String toUrl() {
        if (!isValid()) return "";
        return "messageType:" + messageType + ";messageID:" + getMessageID() + ";senderTag:" + getSender().getTag() + ";currentWorld:" + currentWorld + ";previousWorld:" + previousWorld + ";rsn:" + rsn;
    }

    public static WorldNotice fromUrl(String url) {
        Map<String, String> map = MessageHelper.mapFromUrl(url);
        return new WorldNotice(map.get("messageID"), map.get("senderTag"), map.get("currentWorld"), map.get("previousWorld"), map.get("rsn"));
    }

    public int getCurrentWorld() {
        return currentWorld;
    }

    public int getPreviousWorld() {
        return previousWorld;
    }

    public String getRsn() {
        return rsn;
    }
}
