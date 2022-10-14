package code;

import code.math.IOHelp;
import java.awt.FontMetrics;
import java.awt.Font;
import code.knapsacks.*;

import code.ui.UICreator;
import code.ui.UIState;
import code.ui.UIController;

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
import java.awt.Color;
import java.awt.Graphics2D;

/**
* Core class for the Polygon Triangulator
*/
public class Core extends JPanel {
  private static final long serialVersionUID = 1;

  public static final int DEFAULT_SCREEN_SIZE_X = 1920, DEFAULT_SCREEN_SIZE_Y = 1080;

  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;

  private JFrame f;
  private boolean maximized = true;

  public int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;

  private UIController uiCon = new UIController();

  private boolean[] keyDown = new boolean[65536];

  private int screenSizeX;
  private int screenSizeY;
  private int smallScreenX = 720;
  private int smallScreenY = 720;

  private Knapsack knapsack;
  private String[] lines;

  /**
  * Main method. Called on execution. Performs basic startup
  *
  * @param args Ignored
  */
  public static void main(String[] args) {
    new Core().run();
  }

  /**
  * Performs initialisation of the program. Only to be run on startup
  */
  public Core() {
    f = new JFrame("Knapsack");
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

    uiCon.putPane("Menu", UICreator.createMain(this, uiCon));
    uiCon.setCurrent("Menu");

    initialiseControls();

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

  public void runKnapsack(Item[] items, int limit) {
    if (knapsack == null) return;
    knapsack.calculate(items, limit);
    lines = analyseKnapsack();
  }

  public void runKnapsackRandom() {
    if (knapsack == null) return;
    int n = (int)(4+Math.random()*4);
    int m = (int)(10+Math.random()*90);

    Item[] items = new Item[n];
    for (int i = 0; i < n; i++) {
      items[i] = new Item((int)(5+Math.random()*(m-5) / (knapsack instanceof DP01 ? 1 : 10)), (int)(1+Math.random()*99), knapsack instanceof DP01 ? 1 : (int)(1+Math.random()*14));
    }

    runKnapsack(items, m);
  }

  public void setKnapsack(int version, int method) {
    knapsack = version == 0 ? new DP01() :
    method == 0 ? new BF0N() :
    method == 1 ? new DP0N() :
    method == 2 ? new GT0N() :
    null;
  }

  public String getKnapsack() {
    return knapsack==null ? "" : knapsack.getClass().getSimpleName();
  }

  public String[] analyseKnapsack() {
    String ans = knapsack.analyse();
    IOHelp.saveToFile("output.txt", ans);
    return ans.split("\n");
  }

  public void run() {
    while (true) {
      long tickTime = System.currentTimeMillis();

      repaint();

      tickTime = System.currentTimeMillis() - tickTime;
      try {
        Thread.sleep(Math.max((long)(MILLISECONDS_PER_TICK - tickTime), 0));
      } catch(InterruptedException e){System.out.println(e); System.exit(0);}
    }
  }

  public void paintComponent(Graphics gra) {
    Graphics2D g = (Graphics2D) gra;
    g.setColor(Color.black);
    g.fillRect(0, 0, (int)screenSizeX, (int)screenSizeY);

    if (uiCon.getMode() == UIState.DISPLAY && knapsack != null) {
      Font font = new Font("Cascadia Code", Font.PLAIN, (int) Math.round((20.0*screenSizeY/DEFAULT_SCREEN_SIZE_Y)));
      FontMetrics metrics = g.getFontMetrics(font);
      g.setFont(font);
      g.setColor(Color.white);

      for (int line = 0; line < lines.length; line++) {
        g.drawString(lines[line], (screenSizeX-metrics.stringWidth(lines[line]))/2, line*metrics.getHeight()+(metrics.getHeight()/2) + metrics.getAscent());
      }
    }

    uiCon.draw(g, screenSizeX, screenSizeY);
  }

  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  private void initialiseControls() {

    //Mouse Controls
    f.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        uiCon.cursorMove(x, y);
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        uiCon.cursorMove(x, y);
        uiCon.useSlider(x);
      }
    });
    f.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        if (e.getButton() == 1) {
          uiCon.cursorMove(x, y);
          uiCon.press();
        }
      }

      @Override
      public void mouseReleased(MouseEvent e){
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        if (e.getButton() == 1) {
          uiCon.cursorMove(x, y);
          uiCon.release();
        }
      }
    });

    //Keyboard Controls
    f.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (uiCon.getActiveTextfield() != null && !keyDown[KeyEvent.VK_CONTROL]) uiCon.typeKey(e);

        if(keyDown[keyCode]) return; //Key already in
        keyDown[keyCode] = true;

        // System.out.print(keyCode);
        if (keyCode == KeyEvent.VK_F11) {
          doFull();
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
          uiCon.back();
        }
        if (keyCode == KeyEvent.VK_ENTER) {
          uiCon.press();
        }
      }

      @Override
      public void keyReleased(KeyEvent e){
        int keyCode = e.getKeyCode();
        keyDown[keyCode] = false;

        if (keyCode == KeyEvent.VK_ENTER) {
          uiCon.release();
        }
      }
    });
  }
}
