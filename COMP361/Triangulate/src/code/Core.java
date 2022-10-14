package code;

import code.shapes.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;
import java.awt.image.BufferedImage;
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

/**
* Core class for the Polygon Triangulator
*/
public class Core extends JPanel {
  private static final long serialVersionUID = 1;

  public static final int DEFAULT_SCREEN_SIZE_X = 720, DEFAULT_SCREEN_SIZE_Y = 720;

  private JFrame f;
  private boolean maximized = true;

  public int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;

  private double mouseX, mouseY;

  private double screenSizeX;
  private double screenSizeY;
  private double smallScreenX = DEFAULT_SCREEN_SIZE_X;
  private double smallScreenY = DEFAULT_SCREEN_SIZE_Y;

  /**A Limit on the number of positions available to draw polygons to.*/
  public static final int GRID_SIZE = 1;

  private Polygon pol = new Polygon(Color.white);
  private List<Polygon> tris = List.of();

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
    f = new JFrame("Triangulate");
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
  * Triangulates a given Polygon. Finds the smallest total perimeter of a collection of triangles to represent a Polygon
  *
  * @param pol The polygon to triangulate
  *
  * @return the length of this triangulation
  */
  public double triangulate(Polygon pol) {
    if (pol.ps.isEmpty()) return Double.NaN;
    int n = pol.ps.size(); //number of vertices

    int[] ks = new int[n*n];
    Arrays.fill(ks, 0);
    double[] lengths = new double[n*n];
    Arrays.fill(lengths, 0d);
    List<Integer> optimalks = new ArrayList<Integer>();

    //look through each diagonal of the array, starting at 2, because cannot make tris out of 1 or 2 points
    for (int diag = 2; diag < n; diag++) {
      //for each element in the diagonal
      for (int j = diag; j < n; j++) {
        int i = j-diag;
        lengths[i*n+j] = Double.MAX_VALUE;
        //look at each triangle able to be made between elements i and j
        for (int k = i+1; k < j; k++) {
          //get the perimeter of this triangle
          double perim
          = Vector2.dist(pol.ps.get(i), pol.ps.get(j))
          + Vector2.dist(pol.ps.get(j), pol.ps.get(k))
          + Vector2.dist(pol.ps.get(k), pol.ps.get(i));

          //assign this element of the array to this perimeter plus adjacent perimeters if this is the best ikj tri
          double totLen = lengths[i*n+k] + lengths[k*n+j] + perim;

          if (totLen < lengths[i*n+j]) {
            lengths[i*n+j] = totLen;
            ks[i*n+j] = k;

            if (i*n+j == n-1) optimalks.clear();
          }
          if (totLen == lengths[i*n+j] && i*n+j == n-1) optimalks.add(k);
        }
      }
    }

    /*
    //print out array
    for (int i = 0; i <= 5*n; i++) System.out.print("_");
    System.out.println();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        System.out.print(String.format("|%.2f", lengths[i*n+j]));
      }
      System.out.print("|\n");
    }
    for (int i = 0; i <= 5*n; i++) System.out.print("_");
    System.out.println();
    */

    //generate tris list of best option
    tris = List.copyOf(traceBack(ks, pol.ps, 0, n-1, n));

    //print out png files of all optimal triangulations
    for (int i = 0; i < optimalks.size(); i++) {
      BufferedImage img = new BufferedImage(DEFAULT_SCREEN_SIZE_X*2, DEFAULT_SCREEN_SIZE_Y*2, BufferedImage.TYPE_INT_ARGB);
      ks[n-1] = optimalks.get(i);
      drawPoly(img.createGraphics(), DEFAULT_SCREEN_SIZE_X*2, DEFAULT_SCREEN_SIZE_X*2, traceBack(ks, pol.ps, 0, n-1, n).toArray(new Polygon[0]));
      IOHelp.saveImage("../pictures/length_"+String.format("%.2f", lengths[n-1])+"_"+i+".png", img);
    }

    return lengths[n-1]; //upper right element is the optimal value
  }

  /**
  * Recursive method that gathers a list of all the tris used in an optimal triangulation
  *
  * @param ks the array containing all the k values for the second point in triangles for each i, j
  * @param ps the vectors to draw triangles from
  * @param i the index of the first point in a triangle
  * @param j the index of the third point in a triangle
  * @param n the size of each row in the array
  *
  * @return A list of triangles to be drawn, representing a triangulation of a polygon
  */
  public static List<Polygon> traceBack(int[] ks, List<Vector2> ps,  int i, int j, int n) {
    List<Polygon> res = new ArrayList<Polygon>();
    if (ks.length == 0) return res;
    int k = ks[i*n+j];
    if (k==0) return res;
    res.add(new Polygon(Color.green, ps.get(i), ps.get(k), ps.get(j)));
    res.addAll(traceBack(ks, ps, i, k, n));
    res.addAll(traceBack(ks, ps, k, j, n));

    return res;
  }

  public void paintComponent(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, (int)screenSizeX, (int)screenSizeY);
    pol.draw(g, screenSizeX/GRID_SIZE, screenSizeY/GRID_SIZE);
    for (Polygon t : tris) t.draw(g, screenSizeX/GRID_SIZE, screenSizeY/GRID_SIZE);
  }

  /**
  * Draws a list of Polygons to a given Graphics object
  *
  * @param g the Graphics object to draw to
  * @param sX the number of pixels horizontally in the Graphics object
  * @param sY the number of pixels vertically in the Graphics object
  * @param ps the polygons to draw
  */
  public static void drawPoly(Graphics g, int sX, int sY, Polygon... ps) {
    g.setColor(Color.black);
    g.fillRect(0, 0, sX, sY);
    for (Polygon p : ps) p.draw(g, sX, sY);
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
        if (e.getButton() == 1) {
          tris = List.of();
          pol = pol.add(new Vector2((mouseX/(screenSizeX/GRID_SIZE)), (mouseY/(screenSizeY/GRID_SIZE))));
          // System.out.println(pol.ps.get(pol.ps.size()-1));
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
          return;
        }
        tris = List.of();
        if (keyCode == KeyEvent.VK_ENTER) triangulate(pol);
        pol = new Polygon(Color.white);
        if (keyCode == KeyEvent.VK_1) pol = Ngon.generate(1);
        if (keyCode == KeyEvent.VK_2) pol = Ngon.generate(2);
        if (keyCode == KeyEvent.VK_3) pol = Ngon.generate(3);
        if (keyCode == KeyEvent.VK_4) pol = Ngon.generate(4);
        if (keyCode == KeyEvent.VK_5) pol = Ngon.generate(5);
        if (keyCode == KeyEvent.VK_6) pol = Ngon.generate(6);
        if (keyCode == KeyEvent.VK_7) pol = Ngon.generate(7);
        if (keyCode == KeyEvent.VK_8) pol = Ngon.generate(8);
        if (keyCode == KeyEvent.VK_9) pol = Ngon.generate(9);
        if (keyCode == KeyEvent.VK_0) pol = Ngon.generate(100);
        if (keyCode == KeyEvent.VK_H) pol = Hexagram.generate();
        if (keyCode == KeyEvent.VK_O) pol = Octagram.generate();
        repaint();
      }
    });
  }
}
