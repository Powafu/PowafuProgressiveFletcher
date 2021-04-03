package message.type;

import message.MessageHelper;

import java.util.Map;

public class Request extends Message {

    private final MessageType messageType = MessageType.REQUEST;
    private final RequestType requestType;
    private final String rsn;

    public Request(String messageID, String senderTag, String requestType, String rsn) {
        super(messageID, senderTag);
        this.requestType = RequestType.fromString(requestType);
        this.rsn = rsn;
    }

    @Override
    public boolean isValid() {
        return getSender() != null && getMessageID() != null && requestType != null && rsn != null;
    }

    @Override
    public String toUrl() {
        if (!isValid()) return "";
        return "messageType:" + messageType + ";messageID:" + getMessageID() + ";senderTag:" + getSender().getTag() + ";requestType:" + requestType + ";rsn:" + rsn;
    }

    public static Request fromUrl(String url) {
        Map<String, String> map = MessageHelper.mapFromUrl(url);
        return new Request(map.get("messageID"), map.get("senderTag"), map.get("requestType"), map.get("rsn"));
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getRsn() {
        return rsn;
    }
}
