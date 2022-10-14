import java.util.Collection;
import java.util.HashSet;

/**
 * Road represents ... a road ... in our graph, which is some metadata and a
 * collection of Segments. We have lots of information about Roads, but don't
 * use much of it.
 *
 * @author tony
 */
public class Road {
	public final int roadID;
	public final String name, city;
	public final Collection<Segment> components;
	public final boolean oneWay;
	public final int speedLim;
	public final int roadclass;

	public Road(int roadID, int type, String label, String city, int oneway,
			int speed, int roadclass, int notforcar, int notforpede,
			int notforbicy) {
		this.roadID = roadID;
		this.city = city;
		this.name = label;
		this.components = new HashSet<Segment>();
		this.oneWay = oneway >= 1 ? true : false;
		if (speed <= 0) {this.speedLim=5;}
		else if (speed >= 6) {this.speedLim=110;}
		else {this.speedLim = speed*20;}
		this.roadclass = roadclass+1;
	}

	public void addSegment(Segment seg) {
		components.add(seg);
	}
}

// code for COMP261 assignments
