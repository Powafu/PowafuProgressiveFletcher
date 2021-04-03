package message.type;

import message.MessageHelper;

import java.util.Map;


public class TimeNotice extends Message {

    private final MessageType messageType = MessageType.TIME_NOTICE;
    private final int sleepHour;
    private final int wakeHour;

    public TimeNotice(String messageID, String senderTag, String sleepHour, String wakeHour) {
        super(messageID, senderTag);
        this.sleepHour = Integer.parseInt(sleepHour);
        this.wakeHour = Integer.parseInt(wakeHour);
    }

    @Override
    public boolean isValid() {
        return getSender() != null && getMessageID() != null && sleepHour >= 0 && wakeHour >= 0;
    }

    @Override
    public String toUrl() {
        if (!isValid()) return "";
        return "messageType:" + messageType + ";messageID:" + getMessageID() + ";senderTag:" + getSender().getTag() + ";sleepHour:" + sleepHour + ";wakeHour:" + wakeHour;
    }

    public static TimeNotice fromUrl(String url) {
        Map<String, String> map = MessageHelper.mapFromUrl(url);
        return new TimeNotice(map.get("messageID"), map.get("senderTag"), map.get("sleepHour"), map.get("wakeHour"));
    }

    public int getSleepHour() {
        return sleepHour;
    }

    public int getWakeHour() {
        return wakeHour;
    }
}
