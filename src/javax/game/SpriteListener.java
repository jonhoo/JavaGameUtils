package javax.game;

import java.util.Set;

import javax.game.sidescroller.Sprite;

/**
 * A SpriteListener should be notified whenever something of
 * interest happens to a sprite
 */
public interface SpriteListener {
    /**
     * Called when Sprite a and Sprite b collide
     * 
     * @return The set of sprites to remove as a result of this collision
     */
    public Set<Sprite> handleCollision ( Sprite a, Sprite b );
}
