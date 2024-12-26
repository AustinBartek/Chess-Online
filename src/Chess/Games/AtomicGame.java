package Chess.Games;

import Chess.BoardStuff.*;

public class AtomicGame extends Variant {

    public AtomicGame(Board board, boolean online) {
        super(board, online);
        this.gameType = GameType.Atomic;
    }
}