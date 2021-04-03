package message.type;

import org.bot_management.BotManagementHelper;
import org.bot_management.data.LaunchedClient;

public abstract class Message {

    private LaunchedClient sender;
    private String messageID;

    public Message(String messageID, String senderTag) {
        this.messageID = messageID;
        sender = BotManagementHelper.getClient(senderTag);
    }

    public boolean isValid() {
        return messageID != null && sender != null;
    }

    public String toUrl() {
        // Ex: message.type:MESSAGE;messageID:ASDFG1234;senderTag:6c51e7f6-877b-4e3a-a322-a37793ded40c;
        if (!isValid()) return "";
        return "messageType:MESSAGE;messageID:" + messageID + ";senderTag:" + sender.getTag();
    }

    public LaunchedClient getSender() {
        return sender;
    }

    public void setSender(LaunchedClient sender) {
        this.sender = sender;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
