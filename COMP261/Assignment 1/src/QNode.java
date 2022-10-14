import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Point;
import java.awt.BasicStroke;

public class QNode {
  private Location centre;
  private double size;

  private QNode[] children = {null, null, null, null};
  private Stop stop = null;

  public QNode(Location centre, double size) {
    this.centre = centre;
    this.size = size;
  }

  /**
  * Adds a stop to the Quad tree.
  * If there's no children, will attempt to add stop directly.
  * Otherwise will recursively attempt to add the stop further down the tree until it finds an empty node.
  */
  public void addStop(Stop s) {
    if (children[0]==null) {
      if (stop==null) {stop = s;}
      else {
        divide();
        int i = stop.getPos().x < centre.x ? 0 : 1;
        i += stop.getPos().y < centre.y ? 0 : 2;
        children[i].addStop(stop);
        i = s.getPos().x < centre.x ? 0 : 1;
        i += s.getPos().y < centre.y ? 0 : 2;
        children[i].addStop(s);
        stop = null;
      }
    }
    else {
      int i = s.getPos().x < centre.x ? 0 : 1;
      i += s.getPos().y < centre.y ? 0 : 2;
      children[i].addStop(s);
    }
  }

  /**
  * Divides the node into four smaller quadrents.
  */
  public void divide() {
    children[0] = new QNode(new Location(centre.x-size/4, centre.y-size/4), size/2);
    children[1] = new QNode(new Location(centre.x+size/4, centre.y-size/4), size/2);
    children[2] = new QNode(new Location(centre.x-size/4, centre.y+size/4), size/2);
    children[3] = new QNode(new Location(centre.x+size/4, centre.y+size/4), size/2);
  }

  /**
  * Searches this node to see if it contains a closer stop than the current closest.
  *
  * Inspiration taken from example provided in "http://bl.ocks.org/patricksurry/6478178". No code was copied. I couldn't, even if I wanted to.
  * It was written in a language that doesn't translate to java easily, and a lot of it was sloppily done anyway.
  * Essentially only the general 'three part structure' of the example method was used
  */
  public Stop findNearest(Location click, Stop closest) {
    //first decide if we need to bother looking
    if (closest!=null) {
      if (Math.abs(click.x-centre.x)-size/2 > closest.getDist() || Math.abs(click.y-centre.y)-size/2 > closest.getDist()) {
        return closest;
      }
    }

    //then check this node
    if (stop!=null) {
      stop.setDist(stop.getPos().distance(click));
      if (closest==null || stop.getDist() < closest.getDist()) {
        closest = stop;
        closest.setDist(stop.getDist());
      }
    }

    //lastly, recursion from closest to furthest child
    if (children[0]==null) {return closest;}
    int i = click.x < centre.x ? 0 : 1;
    int j = click.y < centre.y ? 0 : 2;
    closest = children[i+j].findNearest(click, closest);
    closest = children[i+2-j].findNearest(click, closest);
    closest = children[1-i+j].findNearest(click, closest);
    closest = children[1-i+2-j].findNearest(click, closest);

    return closest;
  }

  public void draw(Graphics2D g, Location origin, double scale) {
    Point f = centre.asPoint(origin, scale);
    double sizeL = size*scale;
    g.setStroke(new BasicStroke(1));
    g.setColor(Color.black);
    g.draw(new Rectangle2D.Double(f.x-sizeL/2, f.y-sizeL/2, sizeL, sizeL));
    if (children[0]!=null) {for (QNode child : children) {child.draw(g, origin, scale);}}
  }
}
