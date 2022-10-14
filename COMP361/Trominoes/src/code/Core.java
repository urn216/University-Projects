package code;

//import java.util.*;
import java.awt.Color;
// import java.awt.Font;
import java.awt.Insets;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
* Core class for the tromino tiler
*/
public class Core extends JPanel {
  private static final long serialVersionUID = 1;

  public static final int DEFAULT_SCREEN_SIZE_X = 1280, DEFAULT_SCREEN_SIZE_Y = 720;
  public static final int n = 16;

  private JFrame f;
  private boolean maximized = true;

  public int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;

  private double mouseX, mouseY;

  private double screenSizeX;
  private double screenSizeY;
  private double smallScreenX = DEFAULT_SCREEN_SIZE_X;
  private double smallScreenY = DEFAULT_SCREEN_SIZE_Y;

  private Board board;

  /**
  * Main method. Called on execution. Performs basic startup
  *
  * @param args Ignored
  */
  public static void main(String[] args) {
    new Core().start();
  }

  /**
  * Performs initialisation of the program. Only to be run on startup
  */
  public void start() {
    f = new JFrame("Trominoes");
    f.getContentPane().add(this);
    f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    f.setResizable(true);
    f.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    f.addComponentListener( new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        screenSizeX = f.getWidth() - toolBarLeft - toolBarRight;
        screenSizeY = f.getHeight() - toolBarTop - toolBarBot;
      }
    });
    doFull();
    screenSizeX = f.getWidth();
    screenSizeY = f.getHeight();

    initialiseControls();

    newBoard(n, 0, 0);
    f.setVisible(true);
  }

  /**
  * A helper method that updates the window insets to match their current state
  */
  public void updateInsets() {
    Insets i = f.getInsets();
    toolBarLeft = i.left;
    toolBarRight = i.right;
    toolBarTop = i.top;
    toolBarBot = i.bottom;
  }

  /**
  * A helper method that toggles fullscreen for the window
  */
  public void doFull() {
    f.removeNotify();
    if (maximized) {
      f.setExtendedState(JFrame.NORMAL);
      f.setUndecorated(false);
      f.addNotify();
      updateInsets();
      f.setSize((int)smallScreenX + toolBarLeft + toolBarRight, (int)smallScreenY + toolBarTop + toolBarBot);
    }
    else {
      smallScreenX = screenSizeX;
      smallScreenY = screenSizeY;
      f.setVisible(false);
      f.setExtendedState(JFrame.MAXIMIZED_BOTH);
      f.setUndecorated(true);
      f.setVisible(true);
      updateInsets();
      f.addNotify();
    }
    f.requestFocus();
    maximized = !maximized;
  }

  /**
  * A helper method that generates a new tromino board, prints out the trominoes, and redraws the screen
  */
  public void newBoard(int n, int x, int y) {
    board = new Board(n, x, y);
    System.out.println(board);
    repaint();
  }

  public void paintComponent(Graphics gra) {
    Graphics2D g = (Graphics2D) gra;

    g.setColor(Color.black);
    g.fillRect(0, 0, (int)screenSizeX, (int)screenSizeY);
    board.draw(g, screenSizeY);
  }

  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  private void initialiseControls() {

    //Mouse Controls
    f.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e){
        mouseX = e.getX() - toolBarLeft;
        mouseY = e.getY() - toolBarTop;
        // mouseDown[e.getButton()] = false;
        if (e.getButton() == 1) {
          board = new Board(n, (int)(mouseX/(screenSizeY/n)), n-1-(int)(mouseY/(screenSizeY/n)));
          repaint();
        }
      }
    });

    //Keyboard Controls
    f.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_F11) {
          doFull();
        }
      }
    });
  }
}
