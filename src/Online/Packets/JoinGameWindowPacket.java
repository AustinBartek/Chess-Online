package Online.Packets;

import java.io.Serializable;
import java.util.HashMap;

import Chess.Games.Variant;

public class JoinGameWindowPacket implements Serializable {
    private HashMap<String, Variant> gamesInfo;

    public JoinGameWindowPacket(HashMap<String, Variant> gamesInfo) {
        this.gamesInfo = (gamesInfo != null) ? gamesInfo : new HashMap<>(); // Ensure non-null
    }

    public HashMap<String, Variant> getList() {
        return this.gamesInfo;
    }
}
