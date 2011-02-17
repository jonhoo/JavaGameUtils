package javax.game.sidescroller;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * A single ribbon layer for any side-scrolling game
 * Requires the given image to be larger than the dimensions of the game panel
 * Draws the ribbon based on the current position of the game world in the
 * game panel with wrap-around.
 */
public class Ribbon {
    /**
     * The image used for the ribbon
     */
    private BufferedImage image;

    /**
     * @see #Ribbon(BufferedImage, double, double, Point)
     */
    private double xScale, yScale;

    /**
     * @see #Ribbon(BufferedImage, double, double, Point)
     */
    private Point origo;

    /**
     * Pointer to the manager to get frame information
     */
    private RibbonsManager manager;

    /**
     * Creates a new Ribbon object with the given settings
     * 
     * xScale and yScale give the scaling of game delta-x to ribbon
     * delta-x (similarly for y). For instance, if xScale and yScale
     * is 0.5, a position of (100,200) in the game will cause a (50,100)
     * offset of the ribbon from the origo value. A higher scale will
     * give the impression of the ribbon moving faster
     * 
     * @param image Image to use as ribbon. Must be larger than game panel
     * @param xScale See method javadoc
     * @param yScale See method javadoc
     * @param origo Defines what position (0, 0) corresponds to in the ribbon coordinate system
     */
    public Ribbon ( BufferedImage image, double xScale, double yScale, Point origo ) {
        this.image = image;
        this.xScale = xScale;
        this.yScale = yScale;
        this.origo = origo;

        System.out.format ( "Ribbon loaded from image with dimensions %d x %d\n", image.getWidth ( ), image.getHeight ( ) );
    }

    /**
     * Sets the RibbonsManager object used to manage this ribbon
     * 
     * @param m The manager
     */
    public void setManager ( RibbonsManager m ) {
        this.manager = m;
    }

    public void display ( Graphics g ) {
        if ( this.manager == null )
            return;

        Rectangle frame = this.manager.getFrame ( );

        /**
         * Position should be made relative to the logical origo of the ribbon
         * Also, points wrap around, and x/y position is scaled to simulate
         * different ribbons moving at different speeds.
         */
        Point position = (Point) frame.getLocation ( ).clone ( );

        position.translate ( this.origo.x, this.origo.y );
        position.x = (int) ( position.x * this.xScale );
        position.y = (int) ( position.y * this.yScale );
        while ( position.x < 0 )
            position.x += this.image.getWidth ( );
        while ( position.y < 0 )
            position.y += this.image.getHeight ( );

        position.setLocation (
                ( position.x ) % this.image.getWidth ( ),
                ( position.y ) % this.image.getHeight ( ) );

        /**
         * We have to divide the image in to up to four pieces (if it wraps around both axes)
         * We simply calculate the Rectangle for each piece, and skip drawing those that
         * are empty or have negative width/height.
         */
        Rectangle[] source = new Rectangle[4];
        Rectangle[] destination = new Rectangle[4];

        /**
         * <pre>
         * +<-------------iWidth-------------->+-----------------------------------+
         * ^                                   |                                   |
         * |                                   |                                   |
         * |                                   |                                   |
         * |iHeight              (x,y)         |                                   |
         * |                         +<-----fWidth------>+                         |
         * |                         |         ^         |                         |
         * |                         |   TL    b   TR    |                         |
         * v                         |         v         |                         |
         * +------------------fHeight|<---a---> <---c--->+-------------------------+
         * |                         |         ^         |                         |
         * |                         |   BL    d   BR    |                         |
         * |                         |         v         |                         |
         * |                         +---------|---------+                         |
         * |                                   |                                   |
         * |                                   |                                   |
         * |                                   |                                   |
         * |                                   |                                   |
         * +-----------------------------------+-----------------------------------+
         * </pre>
         * 
         * iWidth/iHeight = image width/height
         * fWidth/fHeight = frame width/height
         * 
         * a = iWidth - x
         * b = iHeight - y
         * c = fWidth - a
         * d = fHeight - b
         */
        int a = contain ( this.image.getWidth ( ) - position.x, 0, frame.width );
        int b = contain ( this.image.getHeight ( ) - position.y, 0, frame.height );
        int c = contain ( frame.width - a, 0, frame.width );
        int d = contain ( frame.height - b, 0, frame.height );

        /**
         * TL
         */
        source[0] = new Rectangle ( position.x, position.y, a, b );
        destination[0] = new Rectangle ( 0, 0, a, b );
        /**
         * TR
         */
        source[1] = new Rectangle ( 0, position.y, c, b );
        destination[1] = new Rectangle ( a, 0, c, b );
        /**
         * BL
         */
        source[2] = new Rectangle ( position.x, 0, a, d );
        destination[2] = new Rectangle ( 0, b, a, d );
        /**
         * BR
         */
        source[3] = new Rectangle ( 0, 0, c, d );
        destination[3] = new Rectangle ( a, b, c, d );

        for ( int i = 0; i < source.length; i++ ) {
            Rectangle src = source[i];
            Rectangle dst = destination[i];
            if ( src.getWidth ( ) > 0 && src.getHeight ( ) > 0 )
                g.drawImage (
                        this.image,
                        dst.x, dst.y, dst.x + dst.width, dst.y + dst.height,
                        src.x, src.y, src.x + src.width, src.y + src.height,
                        null );
        }
    }

    public static int contain ( int value, int min, int max ) {
        if ( value > max )
            return value;
        if ( value < min )
            return min;
        return value;
    }
}
