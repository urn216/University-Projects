import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
* Node represents an intersection in the road graph. It stores its ID and its
* location, as well as all the segments that it connects to. It knows how to
* draw itself, and has an informative toString method.
*
* @author tony
*/
public class Node {

	public final int nodeID;
	public final Location location;
	public final Collection<Segment> segments;
	public final Collection<Segment> segmentStarts;
	public final Collection<Segment> segmentEnds;

	private Segment prev = null;
	private int depth = -1;
	private int rBack = -1;
	private Collection<Node> children = new HashSet<Node>();

	public Node(int nodeID, double lat, double lon) {
		this.nodeID = nodeID;
		this.location = Location.newFromLatLon(lat, lon);
		this.segments = new HashSet<Segment>();
		this.segmentStarts = new HashSet<Segment>();
		this.segmentEnds = new HashSet<Segment>();
	}

	public void setPrev(Segment prev) {this.prev=prev;}

	public Segment getPrev() {return this.prev;}

	public void setDepth(int depth) {this.depth=depth;}

	public int getDepth() {return this.depth;}

	public void setRBack(int rBack) {this.rBack=rBack;}

	public int getRBack() {return this.rBack;}

	public void setChildren(Collection<Node> children) {this.children=children;}

	public Collection<Node> getChildren() {return this.children;}

	public void addSegment(Segment seg) {
		segments.add(seg);
	}

	public void addSegmentStart(Segment seg) {
		segmentStarts.add(seg);
	}

	public void addSegmentEnd(Segment seg) {
		segmentEnds.add(seg);
	}

	public Collection<Node> getNeighbours() {
		Collection<Node> nbs = new HashSet<Node>();
		for (Segment seg : segments) {
			if (seg.start==this) {nbs.add(seg.end);}
			else {nbs.add(seg.start);}
		}
		return nbs;
	}

	public void draw(Graphics g, Dimension area, Location origin, double scale) {
		Point p = location.asPoint(origin, scale);

		// for efficiency, don't render nodes that are off-screen.
		if (p.x < 0 || p.x > area.width || p.y < 0 || p.y > area.height)
		return;

		int size = (int) (Mapper.NODE_GRADIENT * Math.log(scale) + Mapper.NODE_INTERCEPT);
		g.fillRect(p.x - size / 2, p.y - size / 2, size, size);
	}

	public String toString() {
		Set<String> edges = new HashSet<String>();
		for (Segment s : segments) {
			if (!edges.contains(s.road.name))
				edges.add(s.road.name);
		}

		String str = "ID: " + nodeID + "  loc: " + location + "\nroads: ";
		for (String e : edges) {
			str += e + ", ";
		}
		return str.substring(0, str.length() - 2);
	}
}

// code for COMP261 assignments
