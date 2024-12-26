package Online.Packets;

import java.io.Serializable;

public class MoveRequest implements Serializable {
    private final int posX, posY;
    private final boolean rightClick, currentColor;

    public MoveRequest(int posX, int posY, boolean rightClick, boolean currentColor) {
        this.posX = posX;
        this.posY = posY;
        this.rightClick = rightClick;
        this.currentColor = currentColor;
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

    public boolean getCurrentColor() {
        return this.currentColor;
    }
}
