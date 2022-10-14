package code;

import java.awt.Graphics;
import java.awt.Color;

import java.util.List;

public class Polygon {
  /**The line colour of this Polygon*/
  public final Color lineC;
  /**The fill colour of this Polygon*/
  public final Color fillC;
  /**The list of vertices representing this Polygon*/
  public final List<Vector2> ps;

  /**
  * Creates an immutable Polygon of any size
  *
  * @param c The colour of this Polygon
  * @param ps The vertices to represent the Polygon
  */
  public Polygon(Color c, Vector2... ps) {
    this.lineC = c;
    this.fillC = new Color(c.getRed(), c.getGreen(), c.getBlue(), 32);
    this.ps = List.of(ps);
  }

  /**
  * Adds a new vertex to this Polygon
  *
  * @param p The Vector2 vertex to add
  *
  * @return A new Polygon with one additional vertex
  */
  public Polygon add(Vector2 p) {
    Vector2[] ps = new Vector2[this.ps.size()+1];
    this.ps.toArray(ps);
    ps[ps.length-1] = p;
    return new Polygon(lineC, ps);
  }

  /**
  * Draws a Polygon to a given Graphics object
  *
  * @param g the Graphics object to draw to
  * @param sX the number of pixels horizontally in the Graphics object
  * @param sY the number of pixels vertically in the Graphics object
  */
  public void draw(Graphics g, double sX, double sY) {
    if (ps.isEmpty()) return;
    if (ps.size() > 2) fill(g, sX, sY);

    g.setColor(lineC);
    for (int i = 1; i < ps.size(); i++) {
      g.drawLine((int)(ps.get(i-1).x*sX), (int)(ps.get(i-1).y*sY), (int)(ps.get(i).x*sX), (int)(ps.get(i).y*sY));
    }
    g.setColor(fillC);
    g.drawLine((int)(ps.get(0).x*sX), (int)(ps.get(0).y*sY), (int)(ps.get(ps.size()-1).x*sX), (int)(ps.get(ps.size()-1).y*sY));
  }

  /**
  * Fills in a Polygon with this Polygon's fillC
  *
  * @param g the Graphics object to draw to
  * @param sX the number of pixels horizontally in the Graphics object
  * @param sY the number of pixels vertically in the Graphics object
  */
  private void fill(Graphics g, double sX, double sY) {
    g.setColor(fillC);
    Vector2 scale = new Vector2(sX, sY);
    for (int y = 0; y < sY*Core.GRID_SIZE; y++) {
      // System.out.println("Row " + y);
      double x1 = 0;
      boolean fill = false;
      int prevLine = -1;

      while(x1 < sX*Core.GRID_SIZE) {
        // System.out.println("  Col " + x1);
        double x2 = sX*Core.GRID_SIZE;
        int bestLine = -1;

        for (int l = 0; l < ps.size(); l++) {
          // System.out.println("    Line " + l);
          if (l == prevLine) continue;

          // get line
          Vector2 p1 = l == 0 ? ps.get(ps.size()-1) : ps.get(l-1);
          p1 = p1.scale(scale);
          Vector2 p2 = ps.get(l).scale(scale);
          if (y >= Math.max(p1.y, p2.y) || y < Math.min(p1.y, p2.y)) continue; //if we aren't intersecting the line, continue

          //get line features
          double m = (p2.x-p1.x)/(p2.y-p1.y); //gradient of line
          double c = p2.x-m*p2.y; //x intersept of the line

          //get intersection
          int xN = (int)(m*y+c);
          if (xN == x1) {xN+=0.01; fill = !fill;}
          if (xN <= x1) continue;
          if (xN < x2) { //if this point is the smallest jump, use it
            bestLine = l;
            x2 = xN;
          }
        }
        if (fill && x2 != sX) g.drawLine((int)x1, y, (int)x2, y); //draw a horizontal line between x1 and x2

        x1 = x2;
        prevLine = bestLine;
        fill = !fill;
      }
    }
  }
}
