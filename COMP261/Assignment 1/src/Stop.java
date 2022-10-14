import java.util.Set;
import java.util.HashSet;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
//import java.awt.geom.Line2D;
import java.awt.Color;
import java.awt.Point;
import java.awt.BasicStroke;

/**A class for the different bus stops on a map*/
public class Stop {

  private String id;
  private String name;
  private Location pos;

  private double dist;

  private Set<Connection> inbound = new HashSet<Connection>();
  private Set<Connection> outbound = new HashSet<Connection>();

  /**
   * Creates a new Stop object to be drawn on and used by a map
   */
  public Stop(String id, String name, double lat, double lon) {
    this.id = id;
    this.name = name;
    pos = Location.newFromLatLon(lat, lon);
  }

  /**
   * @return the id of the bus stop
   */
  public String getId() {return id;}

  public Set<Connection> getInbound() {return inbound;}

  public Set<Connection> getOutbound() {return outbound;}

  /**
   * @return the name of the bus stop
   */
  public String getName() {return name;}

  /**
   * @return the Location object representing the bus stop
   */
  public Location getPos() {return pos;}

  /**
   * @return distance to a click. Only used internally
   */
  public double getDist() {return dist;}

  public void setDist(double dist) {this.dist = dist;}

  /**
   * @param con
   *          a connection between two stops, one of which is this stop
   * @param in
   *          whether this is an inbound connection
   */
  public void addConnection(Connection con, boolean in) {
    if (in) {inbound.add(con);}
    else {outbound.add(con);}
  }

  public void drawHighlight(Graphics2D g, Location origin, double scale) {
    g.setStroke(new BasicStroke(3));
    g.setColor(Color.orange);
    diamond(g, origin, scale);
  }

  public void draw(Graphics2D g, Location origin, double scale) {
    g.setStroke(new BasicStroke(1));
    g.setColor(Color.blue);
    diamond(g, origin, scale);
  }

  private void diamond(Graphics2D g, Location origin, double scale) {
    Point f = pos.asPoint(origin, scale);
    double size = scale/10;
    // g.draw(new Line2D.Double(f.x-size/2, f.y, f.x, f.y-size/2));
    // g.draw(new Line2D.Double(f.x-size/2, f.y, f.x, f.y+size/2));
    // g.draw(new Line2D.Double(f.x+size/2, f.y, f.x, f.y-size/2));
    // g.draw(new Line2D.Double(f.x+size/2, f.y, f.x, f.y+size/2));
    g.draw(new Ellipse2D.Double(f.x-size/2, f.y-size/2, size, size));
  }
}
