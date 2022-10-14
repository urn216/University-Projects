import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**A class for the different bus trips on a map*/
public class Trip {

  private String id;
  private List<Stop> stops;
  private Set<Connection> cons = new HashSet<Connection>();

  public Trip(String id, List<Stop> stops) {
    this.id = id;
    this.stops = stops;
    for (int i = 1; i < stops.size(); i++) {
      Connection con = new Connection(this, stops.get(i-1), stops.get(i));
      cons.add(con);
      stops.get(i-1).addConnection(con, false);
      stops.get(i).addConnection(con, true);
    }
  }

  /**
   * @return the id of this trip
   */
  public String getId() {return id;}

  /**
   * @return the list of stops along this trip
   */
  public List<Stop> getStops() {return stops;}

  /**
   * @return the set of connections along this trip
   */
  public Set<Connection> getCons() {return cons;}

  /**
   * @return true if other has the same ID as this trip
   */
  public boolean equals(Trip other) {return id.equals(other.getId());}
}
