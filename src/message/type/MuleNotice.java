package message.type;

import message.MessageHelper;

import java.util.Map;


public class MuleNotice extends Message {

    private final MessageType messageType = MessageType.MULE_NOTICE;
    private final String rsn;
    private final int world;

    public MuleNotice(String messageID, String senderTag, String rsn, String world) {
        super(messageID, senderTag);
        this.rsn = rsn;
        this.world = Integer.parseInt(world);
    }

    @Override
    public boolean isValid() {
        return getSender() != null && getMessageID() != null && rsn != null && world > 0;
    }

    @Override
    public String toUrl() {
        if (!isValid()) return "";
        return "messageType:" + messageType + ";messageID:" + getMessageID() + ";senderTag:" + getSender().getTag() + ";rsn:" + rsn + ";world:" + world;
    }

    public static MuleNotice fromUrl(String url) {
        Map<String, String> map = MessageHelper.mapFromUrl(url);
        return new MuleNotice(map.get("messageID"), map.get("senderTag"), map.get("rsn"), map.get("world"));
    }

    public String getRsn() {
        return rsn;
    }

    public int getWorld() {
        return world;
    }
}
