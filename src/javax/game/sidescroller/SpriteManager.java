package javax.game.sidescroller;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.game.SpriteListener;

/**
 * Keeps track of multiple Sprites in a game
 * Sprites are drawn in order to allow putting some
 * behind others.
 * Whenever a tick occurs, all sprites are checked for
 * collisions, and if one is detected, all registered
 * CollisionWatcher objects are notified.
 * 
 * Also, all key events the sprite manager receives are
 * forwarded to all sprites.
 */
public class SpriteManager implements KeyListener {

    private Set<SpriteListener> spriteWatchers;
    private List<Sprite> sprites;
    private Rectangle worldArea;

    /**
     * Create a new Sprite manager
     */
    public SpriteManager ( ) {
        this.spriteWatchers = new HashSet<SpriteListener> ( );
        this.sprites = new LinkedList<Sprite> ( );
    }

    /**
     * Adds a Sprite to the end of the Sprite list.
     * This Sprite will be drawn after all previous Sprites
     * (i.e. on top of them)
     * 
     * @param s The sprite to add
     */
    public void addSprite ( Sprite s ) {
        this.sprites.add ( s );
    }

    /**
     * Adds a Sprite at the given index.
     * This Sprite will be drawn after all Sprites
     * with index less than the given index
     * (i.e. on top of them), but before any Sprite
     * with index greater than the given index.
     * 
     * @param s The sprite to add
     * @param index The index at which to put the Sprite
     */
    public void addSpriteBefore ( Sprite s, int index ) {
        this.sprites.add ( index, s );
    }

    /**
     * Should be called once for every game tick to update
     * the position of sprites
     */
    public void tick ( ) {
        for ( Sprite s : this.sprites )
            s.tick ( );

        /**
         * Sprite event detection
         */
        int sprite = 0;
        for ( Sprite s : this.sprites ) {
            if ( this.worldArea != null && !this.worldArea.contains ( s.getRectangle ( ) ) )
                s.leavingGameArea();
            
            for ( int j = sprite++; j < this.sprites.size ( ); j++ ) {
                Sprite s2 = this.sprites.get ( j );
                if ( s.collides ( s2 ) )
                    for ( SpriteListener c : this.spriteWatchers )
                        c.handleCollision ( s, s2 );
            }
        }
    }

    /**
     * Draws all sprites using the given Graphics context
     * 
     * The visibleGameArea parameter is used to determine
     * what position each sprite should have on screen.
     * This should normally be GamePanel.getVisibleMapRectangle()
     * 
     * @param g Graphics context
     * @param visibleGameArea The currently visible area of the map
     */
    public void display ( Graphics g, Rectangle visibleGameArea ) {
        for ( Sprite s : this.sprites ) {
            s.draw ( g );
        }
    }

    /**
     * Adds an object that should be notified when a collision is detected
     * 
     * @param c The object that should be notified
     */
    public void addCollisionWatcher ( SpriteListener c ) {
        this.spriteWatchers.add ( c );
    }

    /**
     * Removes the given collision listener
     * 
     * @param c Object to remove
     */
    public void removeCollisionWatcher ( SpriteListener c ) {
        this.spriteWatchers.remove ( c );
    }

    @Override
    public void keyPressed ( KeyEvent e ) {
        for ( Sprite s : this.sprites )
            s.keyPressed ( e );
    }

    @Override
    public void keyTyped ( KeyEvent e ) {
        for ( Sprite s : this.sprites )
            s.keyTyped ( e );
    }

    @Override
    public void keyReleased ( KeyEvent e ) {
        for ( Sprite s : this.sprites )
            s.keyReleased ( e );
    }

    /**
     * Sets the current game world.
     * Used to determine when a sprite leaves the game world
     * 
     * @param worldArea
     */
    public void setWorldArea ( Rectangle worldArea ) {
        this.worldArea = worldArea;
    }
}
