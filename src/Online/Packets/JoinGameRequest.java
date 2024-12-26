package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class JoinGameRequest implements Serializable {
    private Variant game;

    public JoinGameRequest(Variant game) {
        this.game = game;
    }

    public Variant getGame() {
        return this.game;
    }
}
