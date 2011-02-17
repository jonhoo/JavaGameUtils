package javax.game;

import javax.game.sidescroller.Sprite;

/**
 * A SpriteListener should be notified whenever something of
 * interest happens to a sprite
 */
public interface SpriteListener {
    /**
     * Called when Sprite a and Sprite b collide
     */
    public void handleCollision ( Sprite a, Sprite b );
}
