package code;

import java.util.ArrayList;
import code.math.IOHelp;
import java.awt.FontMetrics;
import java.awt.Font;

import java.util.List;

import code.math.Vector2;

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

  private static double MAX_VALUE = 300;

  private List<Vector2> points = new ArrayList<Vector2>();

  private static final double t0 = 100;
  private static final double k = 10;
  private static final double a = 0.5;
  private static final int STOP_POINT = 3000;
  private int it = 0;
  private List<Vector2> curS = new ArrayList<Vector2>();

  private int method = 0;

  private double[] results = new double[STOP_POINT];

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
    f = new JFrame("Travelling Salesman Problem");
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

  public String getMethod() {return method == 0 ? "Simulated Annealing" : "Genetic Algorithm";}

  public void setMethod(int m) {method = m;}

  public void resetTSP() {points.clear(); it = 0;}

  public void setTSP(String filename) {
    points = IOHelp.parsePoints(filename);
    curS.clear();
    curS.addAll(points);
    it = 0;
    results = new double[STOP_POINT];
  }

  public void SAStep() {
    List<Vector2> newS = new ArrayList<Vector2>();
    newS.addAll(curS);

    int i = (int)(Math.random()*newS.size());
    int j = i;
    while(j==i) j = (int)(Math.random()*newS.size());

    newS.set(i, curS.get(j));
    newS.set(j, curS.get(i));

    double eNew = evaluate(newS);
    double eCur = evaluate(curS);

    if (eNew <= eCur || Math.random() < Math.exp(-(eNew-eCur)/(k*coolTemp()))) {curS = newS; eCur = eNew;}

    System.out.println(it + " " + eCur);
    results[it] = eCur;

    it++;
  }

  public double evaluate(List<Vector2> ps) {
    double length = 0;
    for (int i = 0; i < ps.size(); i++) {
      length += Vector2.dist(ps.get(i), ps.get((i+1)%ps.size()));
    }
    return length;
  }

  public double coolTemp() {
    return t0/(1+a*it);
  }

  public void run() {
    while (true) {
      long tickTime = System.currentTimeMillis();

      if (!points.isEmpty() && it < STOP_POINT) {
        SAStep();
      }
      if (it >= STOP_POINT) {
        String res = "";
        int i = 1;
        for (double j : results) res += (i++) + ", " + j +"\n";
        IOHelp.saveToFile("../output.txt", res);
      }

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

    if (uiCon.getMode() == UIState.DISPLAY && !points.isEmpty()) {
      Font font = new Font("Cascadia Code", Font.PLAIN, (int) Math.round((20.0*screenSizeY/DEFAULT_SCREEN_SIZE_Y)));
      // FontMetrics metrics = g.getFontMetrics(font);
      g.setFont(font);
      g.setColor(Color.white);

      for (Vector2 p : points) {
        g.drawLine((int)((p.x-2)*screenSizeX/MAX_VALUE), (int)((p.y-2)*screenSizeX/MAX_VALUE), (int)((p.x+2)*screenSizeX/MAX_VALUE), (int)((p.y+2)*screenSizeX/MAX_VALUE));
        g.drawLine((int)((p.x+2)*screenSizeX/MAX_VALUE), (int)((p.y-2)*screenSizeX/MAX_VALUE), (int)((p.x-2)*screenSizeX/MAX_VALUE), (int)((p.y+2)*screenSizeX/MAX_VALUE));
      }
      g.setColor(Color.green);
      for (int i = 0; i < curS.size(); i++) {
        Vector2 one = curS.get(i);
        Vector2 two = curS.get((i+1)%curS.size());
        g.drawLine((int)((one.x)*screenSizeX/MAX_VALUE), (int)((one.y)*screenSizeX/MAX_VALUE), (int)((two.x)*screenSizeX/MAX_VALUE), (int)((two.y)*screenSizeX/MAX_VALUE));
      }

      // for (int line = 0; line < lines.length; line++) {
      //   g.drawString(lines[line], (screenSizeX-metrics.stringWidth(lines[line]))/2, line*metrics.getHeight()+(metrics.getHeight()/2) + metrics.getAscent());
      // }
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
          resetTSP();
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
