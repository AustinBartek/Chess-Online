package Chess;

import Chess.UI.Windows.ChessWindow;
import Online.Client;

public class Main {
    private static Client client;

    public static void main(String[] args) {
        client = new Client();
        new ChessWindow();
    }

    public static Client getClient() {
        return client;
    }
}
