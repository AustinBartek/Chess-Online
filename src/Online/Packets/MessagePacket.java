package Online.Packets;

import java.io.Serializable;

public class MessagePacket implements Serializable {
    private String message;

    public MessagePacket(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
