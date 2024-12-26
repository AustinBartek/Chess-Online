package Online.Packets;

import java.io.Serializable;

public class MovePacket implements Serializable {
    private final int posX, posY;
    private final boolean rightClick;

    public MovePacket(int posX, int posY, boolean rightClick) {
        this.posX = posX;
        this.posY = posY;
        this.rightClick = rightClick;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public boolean getRightClick() {
        return this.rightClick;
    }
}
