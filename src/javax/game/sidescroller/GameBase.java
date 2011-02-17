package javax.game.sidescroller;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

@SuppressWarnings ( "serial" )
public abstract class GameBase extends JFrame implements WindowListener {
    protected GamePanel game;

    public GameBase ( String gameName, GamePanel game ) {
        super ( gameName );

        this.game = game;
        this.game.setParent ( this );

        this.setLocationRelativeTo ( null );
        this.setContentPane ( this.game );
        this.addWindowListener ( this );
        this.setSize ( this.game.getSize ( ) );
        this.setResizable ( false );
        this.setVisible ( true );

        this.game.start ( );
    }

    public void windowActivated ( WindowEvent e ) {
        this.game.resume ( );
    }

    public void windowDeactivated ( WindowEvent e ) {
        this.game.pause ( );
    }

    public void windowDeiconified ( WindowEvent e ) {
        this.game.resume ( );
    }

    public void windowIconified ( WindowEvent e ) {
        this.game.pause ( );
    }

    public void windowClosing ( WindowEvent e ) {
        this.game.end ( );
    }

    public void windowClosed ( WindowEvent e ) {
    }

    public void windowOpened ( WindowEvent e ) {
    }
}