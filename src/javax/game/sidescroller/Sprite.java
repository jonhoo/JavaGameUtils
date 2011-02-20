package javax.game.sidescroller;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.media.utils.loaders.images.ImageAnimator;

/**
 * A sprite in any x,y coordinate based game
 * 
 * The sprite's image is controlled through a ImageHolder
 * object, and may be changed at any time during execution.
 */
public abstract class Sprite implements KeyListener {

    protected Point position;
    protected ImageAnimator image;
    protected Set<Rectangle> hitboxes;
    
    /**
     * Used to cache the absolute position of all the hitboxes
     */
    private Set<Rectangle> absoluteHitboxes;
    
    /**
     * Used to cache calls to {@link #getRectangle()}
     */
    private Rectangle absoluteRectangle;

    /**
     * How much this sprite's position should be
     * moved on each tick
     */
    protected Point speed;

    /**
     * Panel this sprite belongs to
     */
    protected GamePanel game;

    /**
     * Creates a new sprite
     * 
     * Note that initialPoint should not be null!
     * 
     * @param spriteImage The base image for this sprite
     * @param initialPosition The initial coordinates for the sprite
     */
    public Sprite ( ImageAnimator spriteImage, Point initialPosition, GamePanel game ) {
        this.image = spriteImage;
        this.speed = new Point ( 0, 0 );
        this.position = initialPosition;
        this.game = game;
        this.hitboxes = new HashSet<Rectangle> ( );
    }

    /**
     * Adds the given hitbox to this sprite's collision detection zone
     * 
     * Rectangle coordinates should be relative to the current image!
     * 
     * @param hitbox the hitbox to add to this sprite
     */
    public void addHitbox ( Rectangle hitbox ) {
        this.hitboxes.add ( hitbox );
    }

    /**
     * Removes the given hitbox
     * 
     * @param hitbox
     */
    public void removeHitbox ( Rectangle hitbox ) {
        this.hitboxes.remove ( hitbox );
    }

    /**
     * Removes all hitboxes, making the entire current image the hitbox
     */
    public void clearHitboxes ( ) {
        this.hitboxes.clear ( );
    }

    /**
     * Adds all the hitboxes in the given set
     * 
     * @param hitboxes
     */
    public void addHitboxes ( Set<Rectangle> hitboxes ) {
        this.hitboxes.addAll ( hitboxes );
    }

    /**
     * Returns the current image animator
     * 
     * @return the current image animator
     */
    public ImageAnimator getSpriteImage ( ) {
        return this.image;
    }

    /**
     * Changes the image of this sprite.
     * Will adjust position based on the difference in size between the images.
     * Note that this function will *NOT* stop the animation of the current animator!
     * 
     * @param spriteImage The new sprite image animator
     */
    public void changeImage ( ImageAnimator spriteImage ) {

        BufferedImage before = this.image.getCurrentImage ( );
        BufferedImage after = spriteImage.getCurrentImage ( );

        this.image = spriteImage;

        this.moveBy ( ( before.getWidth ( ) - after.getWidth ( ) ) / 2, ( before.getHeight ( ) - after.getHeight ( ) ) / 2 );
        this.absoluteRectangle = null;
    }

    /**
     * Changes this sprite's position
     * 
     * @param p The new position
     */
    public void moveTo ( Point p ) {
        this.position = p;
        this.positionUpdated ( );
    }

    /**
     * Adjusts this sprite's position by the given amounts
     * 
     * @param dx Adjust x by this much
     * @param dy Adjust y by this much
     */
    public void moveBy ( int dx, int dy ) {
        this.position.translate ( dx, dy );
        this.positionUpdated ( );
    }

    /**
     * Sets how much x and y should change on each tick()
     * 
     * @param x How much x should change
     * @param y How much y should change
     */
    public void setSpeed ( int x, int y ) {
        this.speed.x = x;
        this.speed.y = y;
    }

    /**
     * Sets how much x should change on each tick()
     * 
     * @param x How much x should change
     */
    public void setXSpeed ( int x ) {
        this.speed.x = x;
    }

    /**
     * Sets how much y should change on each tick()
     * 
     * @param y How much y should change
     */
    public void setYSpeed ( int y ) {
        this.speed.y = y;
    }

    /**
     * Returns the current speed in x direction
     * 
     * @return the current speed in x direction
     */
    public int getXSpeed ( ) {
        return this.speed.x;
    }

    /**
     * Returns the current speed in y direction
     * 
     * @return the current speed in y direction
     */
    public int getYSpeed ( ) {
        return this.speed.y;
    }

    /**
     * Returns the coordinates of this sprite
     * 
     * @return the coordinates of this sprite
     */
    public Point getPosition ( ) {
        return new Point ( this.position.x, this.position.y );
    }

    /**
     * Must be called whenever this sprite's position changes!
     * 
     * Used to invalidate hitbox cache
     */
    protected void positionUpdated ( ) {
        this.absoluteHitboxes = null;
        this.absoluteRectangle = null;
    }

    /**
     * Returns the rectangle representing this sprite's size and position
     * 
     * Will return null if this sprite has no size (i.e. no image)
     * 
     * @return the rectangle representing this sprite's size and position
     */
    public Rectangle getRectangle ( ) {
        if ( this.absoluteRectangle == null ) {
            if ( this.image == null )
                return null;
    
            BufferedImage currentSprite = this.image.getCurrentImage ( );
            this.absoluteRectangle = new Rectangle ( this.position, new Dimension ( currentSprite.getWidth ( ), currentSprite.getHeight ( ) ) );
        }
        return this.absoluteRectangle;
    }

    /**
     * Updates this sprite's position by once its current speed
     * 
     * If this method is overridden, remember to call positionUpdated
     * if the position changes!
     */
    public void tick ( ) {
        this.position.translate ( this.speed.x, this.speed.y );
        this.positionUpdated ( );
    }

    /**
     * Draws the visible part of this sprite using the given Graphics context
     * 
     * The currentGameFrame is used to determine the sprite's relative position
     * from its absolute coordinates.
     * 
     * @param g Graphics context
     * @param currentGameFrame as returned by GamePanel.getVisibleMapRectangle()
     */
    public void draw ( Graphics g ) {

        if ( this.image == null )
            return;

        Rectangle currentGameFrame = this.game.getVisibleMapRectangle ( );

        /**
         * Yes, this replicates the code of getRectangle,
         * but we want to do use currentSprite later on,
         * and not fetch it twice (by calling getRectangle)
         * as that might cause a race-condition in which
         * the image has change in between we fetch it here
         * and in getRectangle, causing the Rectangle not
         * to accurately represent the image.
         */
        BufferedImage currentSprite = this.image.getCurrentImage ( );
        Rectangle sprite = new Rectangle ( this.position, new Dimension ( currentSprite.getWidth ( ), currentSprite.getHeight ( ) ) );

        Rectangle visible = sprite.intersection ( currentGameFrame );

        // First, only draw if the intersection is visible
        if ( visible.isEmpty ( ) )
            return;

        /**
         * destination = this.position - game.position
         */
        Point destination = new Point ( visible.x - currentGameFrame.x, visible.y - currentGameFrame.y );

        Point source = new Point ( 0, 0 );

        /**
         * If we are clipped in the top or left, the source point
         * has to be adjusted by how much we should clip in each
         * direction
         */
        if ( visible.x == currentGameFrame.x )
            source.translate ( currentSprite.getWidth ( ) - visible.width, 0 );
        if ( visible.y == currentGameFrame.y )
            source.translate ( 0, currentSprite.getHeight ( ) - visible.height );

        g.drawImage ( currentSprite,
                destination.x, destination.y,
                destination.x + visible.width, destination.y + visible.height,
                source.x, source.y,
                source.x + visible.width, source.y + visible.height,
                null );

    }

    /**
     * Returns the current sprite's hitboxes.
     * 
     * If no hitboxes are defined,
     * the set contains only one hitbox which
     * represents the entire image.
     * Coordinates in the returned rectangles
     * are all absolute.
     * 
     * @return the current sprite's hitboxes
     */
    public Set<Rectangle> getHitboxes ( ) {
        if ( this.absoluteHitboxes == null ) {
            this.absoluteHitboxes = new HashSet<Rectangle> ( );
            if ( this.hitboxes.size ( ) == 0 ) {
                this.absoluteHitboxes.add ( this.getRectangle ( ) );
                return this.absoluteHitboxes;
            }

            for ( Rectangle r : this.hitboxes ) {
                Point absoluteLocation = new Point ( r.getLocation ( ).x, r.getLocation ( ).y );
                absoluteLocation.translate ( this.position.x, this.position.y );
                this.absoluteHitboxes.add ( new Rectangle ( absoluteLocation, r.getSize ( ) ) );
            }
        }

        return this.absoluteHitboxes;
    }

    /**
     * Returns true if this sprite's rectangle overlaps
     * with the given sprite's rectangle
     * 
     * @param other The other sprite
     * @return true if this sprite overlaps the other sprite
     */
    public boolean collides ( Sprite other ) {
        if ( this == other )
            return false;

        for ( Rectangle r : this.getHitboxes ( ) )
            for ( Rectangle r2 : other.getHitboxes ( ) )
                if ( r.intersects ( r2 ) )
                    return true;
        return false;
    }

    /**
     * Returns true if this sprite is visible in the given Polygon of the game
     * 
     * @param currentGameFrame as returned by GamePanel.getVisibleMapRectangle()
     * @return true if this sprite is visible in the given Polygon of the game
     */
    public boolean isVisibleIn ( Rectangle currentGameFrame ) {
        Rectangle sprite = this.getRectangle ( );

        if ( sprite == null )
            return false;

        return sprite.intersects ( currentGameFrame );
    }

    /**
     * Default implementation is empty
     */
    @Override
    public void keyPressed ( KeyEvent e ) {
    }

    /**
     * Default implementation is empty
     */
    @Override
    public void keyTyped ( KeyEvent e ) {
    }

    /**
     * Default implementation is empty
     */
    @Override
    public void keyReleased ( KeyEvent e ) {
    }

    /**
     * Called if this sprite is about to leave the game area
     * 
     * Default behaviour is to stop the sprite on the edge so
     * that it remains fully visible.
     * @return The set of sprites that should be removed as a result of this operation
     */
    public Set<Sprite> leavingGameArea ( ) {
        Rectangle frame = this.game.getVisibleMapRectangle ( );
        Rectangle sprite = this.getRectangle ( );
        
        if ( this.position.x <= frame.x ) {
            this.position.x = frame.x;
            if ( this.getXSpeed ( ) < 0 )
                this.setXSpeed ( -1 * this.getXSpeed ( ) );
        }
        if ( this.position.y <= frame.y ) {
            this.position.y = frame.y;
            if ( this.getYSpeed ( ) < 0 )
                this.setYSpeed ( -1 * this.getYSpeed ( ) );
        }
        if ( this.position.x >= frame.x + ( frame.width - sprite.width ) ) {
            this.position.x = frame.x + ( frame.width - sprite.width );
            if ( this.getXSpeed ( ) > 0 )
                this.setXSpeed ( -1 * this.getXSpeed ( ) );
        }
        if ( this.position.y >= frame.y + ( frame.height - sprite.height ) ) {
            this.position.y = frame.y + ( frame.height - sprite.height );
            if ( this.getYSpeed ( ) > 0 )
                this.setYSpeed ( -1 * this.getYSpeed ( ) );
        }
        
        /**
         * Cleaner, but less good version
         *
        this.position.x = Math.max ( frame.x, this.position.x );
        this.position.y = Math.max ( frame.y, this.position.y );
        this.position.x = Math.min ( this.position.x, frame.x + ( frame.width - sprite.width ) );
        this.position.y = Math.min ( this.position.y, frame.y + ( frame.height - sprite.height ) );
        */
        
        return null;
    }
}
