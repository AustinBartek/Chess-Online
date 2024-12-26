package Online.Packets;

import java.io.Serializable;

public class UsernamePacket implements Serializable {
    private String username;

    public UsernamePacket(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}