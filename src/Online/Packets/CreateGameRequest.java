package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class CreateGameRequest implements Serializable {
    private Variant game;
    private boolean white;

    public CreateGameRequest(Variant game, boolean white) {
        this.game = game;
        this.white = white;
    }

    public Variant getGame() {
        return this.game;
    }

    public boolean getColor() {
        return this.white;
    }
}
