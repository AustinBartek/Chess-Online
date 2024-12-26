package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class UpdateGameRequest implements Serializable {
    private Variant game;

    public UpdateGameRequest(Variant game) {
        this.game = game;
    }

    public Variant getGame() {
        return this.game;
    }
}
