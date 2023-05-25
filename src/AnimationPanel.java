import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import static java.awt.Font.BOLD;

// a subclass of JPanel; this panel has been designed entirely in code (not using the UI designer)
public class AnimationPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private int movingMessageX;
    private int enemyX;
    private int enemyY;
    private boolean isClickingOrangeRect;
    private boolean won;
    private boolean gameOver;
    private Timer timer;
    private Rectangle orangeRect;
    private Rectangle redEnemy;
    private Point delta;

    // constructor
    public AnimationPanel() {
        // initialize variables
        movingMessageX = 0;
        enemyX = 150;
        enemyY = 0;
        isClickingOrangeRect = false;
        won = false;
        gameOver = false;

        // initialize Timer object, responsible for the animation
        timer = new Timer(10, this); // set timer to have 10ms pulses; each pulse triggers an ActionEvent
        timer.start();

        // initialize and set initial positions of red enemy rectangle and orange rectangle
        orangeRect = new Rectangle(70, 30); // create 70x30 user-movable orange rectangle
        orangeRect.setLocation(20, 20); // start orange rectangle at (20, 20)
        redEnemy = new Rectangle(20, 20); // create 20x20 red enemy rectangle
        redEnemy.setLocation(enemyX, enemyY); // start enemy location at (150, 0)
        addMouseListener(this);  // this panel should listen for mouse clicks
        addMouseMotionListener(this);  // this panel should listen for mouse drags
    }

    // important method that is inherited from JComponent and overridden;
    // this method automatically gets called by the graphics engine
    // when the engine it detects that the panel needs to be redrawn/updated
    @Override
    public void paint(Graphics gp) {
        super.paint(gp); // must do this!
        Graphics2D g2d = (Graphics2D) gp; // cast gp to a 2D graphics object so we can do more advanced stuff

        // draw blue message on screen
        g2d.setColor(Color.blue);
        Font myFont = new Font("Arial", BOLD, 14);
        g2d.setFont(myFont);
        String message = "Move orange square to gray! Dodge the red enemy!";
        if (won) {
            message = "YOU WIN!!!!";
        }
        if (gameOver) {
            message = "YOU LOSE!!!";
        }
        g2d.drawString(message, movingMessageX, 100); // write message at location: (x, 100)

        // draw gray target rectangle on the screen
        // since it doesn't move, we can use a simple fillRect rather than create a Rectangle object
        g2d.setColor(Color.darkGray);
        g2d.fillRect(300, 350, 70, 30);  // draw a 70x30 gray rectangle at (300, 350)

        // draw red enemy rectangle and orange rectangle on the screen
        g2d.setStroke(new BasicStroke(3)); // change pen thickness
        g2d.setColor(Color.orange);
        g2d.draw(orangeRect);  // draw the orange rectangle
        g2d.setColor(Color.red);
        g2d.draw(redEnemy);  // draw the orange rectangle
    }

    // updates the x value for the string message by adding a small increment each time,
    // and resetting back to the left edge when it moves off the screen
    private void moveMessage() {
        movingMessageX += 1;
        if (movingMessageX > getWidth()) {
            movingMessageX = 0; // reset message location's x-coordinate
        }
    }

    // updates the x and y values for the red rectangle by adding a small increment each time,
    // and resetting to edges when it goes off the screen
    private void moveEnemy() {
        enemyX += 1;
        enemyY += 2;
        if (enemyX > getWidth()) {
            enemyX = 0; // reset enemy location's x-coordinate
        }
        if (enemyY > getHeight()) {
            enemyY = 0; // reset enemy location's y-coordinate
        }
        redEnemy.setLocation(enemyX, enemyY);  // update enemy rect to new position
        checkCollision();  // check for a collision with orange rectangle after each movement
    }

    // there is no easy way to automatically detect if two rectangles touch,
    // so we must write a method to do that
    private void checkCollision() {
        double orangeLeft = orangeRect.getX();
        double orangeRight = orangeLeft + orangeRect.getWidth();
        double orangeTop = orangeRect.getY();
        double orangeBottom = orangeTop + orangeRect.getHeight();
        double redLeft = redEnemy.getX();
        double redRight = redLeft + redEnemy.getWidth();
        double redTop = redEnemy.getY();
        double redBottom = redTop + redEnemy.getHeight();
        // if the bottom of the red box is between the orange box's top and bottom
        if (redBottom > orangeTop && redBottom < orangeBottom) {
            // check if left/right edges of the red rectangle are "between" the left/right edges of orange rectangle
            if ((redRight > orangeLeft && redRight < orangeRight) || (redLeft < orangeRight && redLeft > orangeLeft)) {
                gameOver = true;
            }
        }
    }

    // -------------- ActionListener interface method --------------
    // called for each Timer event (occurs every 10ms)
    // The timer + this method causes the animation to occur!
    @Override
    public void actionPerformed(ActionEvent e) {
        // only move the message and the red enemy if the game is not over
        if (!gameOver && !won) {
            moveMessage();
            moveEnemy();
        }
        repaint(); // forces the "paint" method above to be re-queued in the graphics engine
    }

    // -------------- MouseListener interface methods (5 required, only 2 used) --------------
    // called when the mouse button is pressed in
    @Override
    public void mousePressed(MouseEvent e) {
        // if the mouse click's coordinate occurred in the orange rectangle, then the user has clicked it
        if (orangeRect.contains(e.getPoint())) {
            // calculate offset for orange rectangle's movement
            int xDiff = e.getX() - orangeRect.x;
            int yDiff = e.getY() - orangeRect.y;
            delta = new Point(xDiff, yDiff);
            isClickingOrangeRect = true; // track that user has clicked orange rectangle (used below in mouseDragged)
        }
    }

    // called when the mouse button is released
    @Override
    public void mouseReleased(MouseEvent e) {
        Point centerOfGraySquare = new Point(335, 365);  // (335, 365) is center point of gray target rect

        // check if the orange rectangle contains the center point of the gray target, if so, the player
        // has won and the orange rectangle should "snap" around the gray square
        if (orangeRect.contains(centerOfGraySquare)) {
            won = true;
            orangeRect.setLocation(300, 350); // "snap" orange rect directly over gray square
        }
        isClickingOrangeRect = false;
    }
    @Override
    public void mouseClicked(MouseEvent e) { } // unused but needed for interface

    @Override
    public void mouseEntered(MouseEvent e) { } // unused but needed for interface

    @Override
    public void mouseExited(MouseEvent e) { } // unused but needed for interface

    // -------------- MouseMotionListener interface methods (2 required, only 1 used) --------------
    // called when user has clicked in and is dragging the mouse
    @Override
    public void mouseDragged(MouseEvent e) {
        // if the user is clicking the orange rectangle when the dragging begins, the rectangle should move!
        if (isClickingOrangeRect) {
            int newRectX = e.getX() - delta.x;
            int newRectY = e.getY() - delta.y;
            orangeRect.setLocation(newRectX, newRectY);
            repaint(); // forces the "paint" method above to be re-queued in the graphics engine
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) { } // unused but needed for interface
}