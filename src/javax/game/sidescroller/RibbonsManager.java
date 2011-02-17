package javax.game.sidescroller;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages multiple ribbons
 * 
 * Ribbons are drawn first to last
 */
public class RibbonsManager {

    private List<Ribbon> ribbons;
    private Rectangle frame;

    public RibbonsManager ( ) {
        this.ribbons = new ArrayList<Ribbon> ( );
    }

    public void updatePosition ( Rectangle newFrame ) {
        this.frame = newFrame;
    }

    public Rectangle getFrame ( ) {
        return this.frame;
    }

    public void display ( Graphics g ) {
        for ( Ribbon r : this.ribbons )
            r.display ( g );
    }

    public void addRibbon ( Ribbon r ) {
        r.setManager ( this );
        this.ribbons.add ( r );
    }
}