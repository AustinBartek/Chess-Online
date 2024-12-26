package Online.Packets;

import java.io.Serializable;

public class JoinGameDeniedPacket implements Serializable {
    private final long ID;

    public JoinGameDeniedPacket(long ID) {
        this.ID = ID;
    }

    public long getID() {
        return this.ID;
    }
}
