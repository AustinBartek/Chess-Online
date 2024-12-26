package Chess.Games;
import Chess.BoardStuff.*;

public class ClassicGame extends Variant {

    public ClassicGame(Board board, boolean online) {
        super(board, online);
        this.gameType = GameType.Classic;
    }
}