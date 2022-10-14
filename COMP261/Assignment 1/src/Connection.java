import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.Color;
import java.awt.Point;
import java.awt.BasicStroke;

/**A class for the different bus connections on a map*/
public class Connection {

  private Trip trip;
  private Stop from, to;

  public Connection(Trip trip, Stop from, Stop to) {
    this.trip = trip;
    this.from = from;
    this.to = to;
  }

  /**
   * @return the trip this connection belongs to
   */
  public Trip getTrip() {return trip;}

  /**
   * @return the stop this connection leaves from
   */
  public Stop getFrom() {return from;}

  /**
   * @return the stop this connection goes to
   */
  public Stop getTo() {return to;}

  public void drawHighlight(Graphics2D g, Location origin, double scale) {
    g.setStroke(new BasicStroke(2));
    g.setColor(Color.yellow);
    line(g, origin, scale);
  }

  public void draw(Graphics2D g, Location origin, double scale) {
    g.setStroke(new BasicStroke(1));
    g.setColor(Color.gray);
    line(g, origin, scale);
  }

  public void line(Graphics2D g, Location origin, double scale) {
    Point f = from.getPos().asPoint(origin, scale);
    Point t = to.getPos().asPoint(origin, scale);
    g.draw(new Line2D.Double(f.x, f.y, t.x, t.y));
  }
}
