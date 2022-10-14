import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.awt.Point;

public class Map extends GUI {

  private static final boolean DRAW_QUAD_TREE = true;
  private static final boolean USE_QUAD_TREE = true;

  private Location pos = new Location(-25, 25);
  private double scale = 7.59375;

  private double prevX;
  private double prevY;

  private boolean dragged = false;
  private int dragCount = 0;

  private HashMap<String, Stop> stops = new HashMap<String, Stop>();
  private HashMap<String, Trip> trips = new HashMap<String, Trip>();

  private TrieNode parent = new TrieNode();
  private QNode qParent = new QNode(pos, 1000);

  private Stop mouseSelect = null;
  private Set<Stop> multiSelect = new HashSet<Stop>();

  public static void main(String[] args) {
    new Map();
  }

  /**
  * Adds a stop to the trie to allow for easy searching
  */
  public void addTrieStop(Stop s) {
    char[] name = s.getName().toCharArray();
    TrieNode node = parent;

    for (char c : name) {node = node.addNode(c);}

    node.addStop(s);
  }

  /**
  * Retrieves all the stops beginning with the input prefix.
  * If the prefix exactly matches the name of one or more stops, only returns that or those stop(s).
  */
  public Set<Stop> retrieveTrieStops(char[] prefix) {
    TrieNode node = parent;

    for (char c : prefix) {node = node.addNode(c);}
    Set<Stop> localStops = node.getLocalStops();
    if (localStops.isEmpty()) {return node.getAllSubStops();}
    else {return localStops;}
  }

  /**
  * Prints the stop selected by the cursor to the text pane, followed by every trip that passes through the stop
  */
  private void printMouseSelect() {
    getTextOutputArea().setText("Selected: " + mouseSelect.getName());
    Set<Trip> hTrips = selectedTrips();
    getTextOutputArea().append("\nTrips:");
    for (Trip t : hTrips) {getTextOutputArea().append("\n    "+t.getId());}
  }

  /**
  * Returns a set containing all the trips that pass through the stop selected by the cursor
  */
  private Set<Trip> selectedTrips() {
    Set<Trip> hTrips = new HashSet<Trip>();
    if (mouseSelect == null) {return hTrips;}
    for (Connection c : mouseSelect.getInbound()) {if (!hTrips.contains(c.getTrip())) {hTrips.add(c.getTrip());}}
    for (Connection c : mouseSelect.getOutbound()) {if (!hTrips.contains(c.getTrip())) {hTrips.add(c.getTrip());}}
    return hTrips;
  }

  @Override
  protected void redraw(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;

    if (DRAW_QUAD_TREE) {qParent.draw(g2, pos, scale);}

    for (Trip t : trips.values()) {for (Connection c : t.getCons()) {c.draw(g2, pos, scale);}}
    for (Stop s : stops.values()) {s.draw(g2, pos, scale);}
    if (mouseSelect!=null) {
      for (Trip t : selectedTrips()) {for (Connection c : t.getCons()) {c.drawHighlight(g2, pos, scale);}}
      mouseSelect.drawHighlight(g2, pos, scale);
    }
    else if (!multiSelect.isEmpty()) {
      for (Stop s : multiSelect) {s.drawHighlight(g2, pos, scale);}
    }
  }

  @Override
  protected void onClick(MouseEvent e) {
    if (dragged) {return;}
    Location click = Location.newFromPoint(new Point(e.getX(), e.getY()), pos, scale);
    Stop closest = null;
    if (USE_QUAD_TREE) {closest = qParent.findNearest(click, closest);}
    else {
      double cDist = Double.POSITIVE_INFINITY;
      for (Stop s : stops.values()) {
        if (s.getPos().isClose(click, 0.05)) {
          double dist = s.getPos().distance(click);
          if (dist<=cDist) {closest = s; cDist = dist;}
        }
      }
    }
    mouseSelect = closest;
    multiSelect.clear();
    if (mouseSelect!=null) {printMouseSelect();}
    else {getTextOutputArea().setText("Selected: Nothing");}
  }

  @Override
  protected void onPress(MouseEvent e) {
    dragged = false;
    dragCount = 0;
    prevX = e.getX();
    prevY = e.getY();
  }

  @Override
  protected void onDrag(MouseEvent e) {
    if (dragCount >= 5) {dragged = true;}
    pos = pos.moveBy((prevX-e.getX())/scale, (e.getY()-prevY)/scale);
    prevX = e.getX();
    prevY = e.getY();
    dragCount++;
  }

  @Override
  protected void onScroll(MouseWheelEvent e) {
    double oldScale = scale;
    scale = e.getWheelRotation()<0 ? scale*1.5 : scale/1.5;
    pos = new Location((e.getX()/oldScale+pos.x)-e.getX()/scale, (pos.y-e.getY()/oldScale)+e.getY()/scale);
  }

  @Override
  protected void onSearch() {
    mouseSelect = null;
    multiSelect = retrieveTrieStops(getSearchBox().getText().toCharArray());
    if (multiSelect.size()==1) {
      for (Stop s : multiSelect) {mouseSelect = s;}
      multiSelect.clear();
      printMouseSelect();
    }
    else if (!multiSelect.isEmpty()) {
      getTextOutputArea().setText("Selected: ");
      for (Stop s : multiSelect) {getTextOutputArea().append("\n    " + s.getName());}
    }
    else {getTextOutputArea().setText("Selected: Nothing");}
  }

  @Override
  protected void onMove(Move m) {
    String e = m.name();
    if (e.equals("ZOOM_IN")) {scale*=1.5;}
    if (e.equals("ZOOM_OUT")) {scale/=1.5;}
    if (e.equals("NORTH")) {pos = pos.moveBy(0, 100/scale);}
    if (e.equals("SOUTH")) {pos = pos.moveBy(0, -100/scale);}
    if (e.equals("EAST")) {pos = pos.moveBy(100/scale, 0);}
    if (e.equals("WEST")) {pos = pos.moveBy(-100/scale, 0);}
  }

  @Override
  protected void onLoad(File stopFile, File tripFile) {
    //loads the stops
    try (BufferedReader br = Files.newBufferedReader(stopFile.toPath())) {
      br.readLine();
      String line;
      while ((line = br.readLine()) != null) {
        Scanner scan = new Scanner(line);
        String id = scan.next();
        StringBuilder sb = new StringBuilder();
        while (scan.hasNext()) {
          if (scan.hasNextDouble() && !scan.hasNextInt()) {break;}
          sb.append(scan.next());
          sb.append(" ");
        }
        Stop s = new Stop(id, sb.toString(), scan.nextDouble(), scan.nextDouble());
        scan.close();
        stops.put(id, s);
        addTrieStop(s);
        qParent.addStop(s);
      }
    } catch (IOException e) {getTextOutputArea().setText("Stops failed to load.");}

    //loads the trips
    try (BufferedReader br = Files.newBufferedReader(tripFile.toPath())) {
      br.readLine();
      String line;
      while ((line = br.readLine()) != null) {
        Scanner scan = new Scanner(line);
        String id = scan.next();
        List<Stop> tStops = new ArrayList<Stop>();
        while (scan.hasNext()) {
          tStops.add(stops.get(scan.next()));
        }
        trips.put(id, new Trip(id, tStops));
        scan.close();
      }
    } catch (IOException e) {getTextOutputArea().setText("Trips failed to load.");}

    getTextOutputArea().setText("Files successfully loaded.");
    pos = new Location(-25, 25);
  }
}
