package Online.Packets;

import java.io.Serializable;

public class UsernameRequest implements Serializable {
    private boolean first;

    public UsernameRequest(boolean first) {
        this.first = first;
    }

    public boolean getFirst() {
        return this.first;
    }
}
