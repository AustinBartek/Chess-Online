package Online.Packets;

import Chess.Games.Variant;
import java.io.Serializable;
import java.util.HashMap;

public class GameInfoListPacket implements Serializable {
    private HashMap<String, Variant> gamesInfo;

    public GameInfoListPacket(HashMap<String, Variant> gamesInfo) {
        this.gamesInfo = (gamesInfo != null) ? gamesInfo : new HashMap<>(); // Ensure non-null
    }

    public HashMap<String, Variant> getList() {
        return this.gamesInfo;
    }
}