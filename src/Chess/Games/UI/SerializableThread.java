package Chess.Games.UI;

import java.io.Serializable;

public class SerializableThread extends Thread implements Serializable {
    public SerializableThread(Runnable run) {
        super(run);
    }
}
