import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This represents the data structure storing all the roads, nodes, and
 * segments, as well as some information on which nodes and segments should be
 * highlighted.
 *
 * @author tony
 */
public class Graph {
	// map node IDs to Nodes.
	Map<Integer, Node> nodes = new HashMap<>();
	// map road IDs to Roads.
	Map<Integer, Road> roads;
	// just some collection of Segments.
	Collection<Segment> segments;
	// All the restrictions.
	Collection<Restriction> restrictions = new HashSet<Restriction>();

	Node highlightedNode = null;
	Node departNode = null;
	Node destNode = null;
	Collection<Node> highlightedNodes = new HashSet<>();
	Collection<Road> highlightedRoads = new HashSet<>();
	Collection<Segment> highlightedSegs = new HashSet<>();

	public Graph(File nodes, File roads, File segments, File polygons, File restrictions) {
		this.nodes = Parser.parseNodes(nodes, this);
		this.roads = Parser.parseRoads(roads, this);
		this.segments = Parser.parseSegments(segments, this);
		if (restrictions!=null) {this.restrictions = Parser.parseRestrict(restrictions, this);}
	}

	public void draw(Graphics g, Dimension screen, Location origin, double scale) {
		// a compatibility wart on swing is that it has to give out Graphics
		// objects, but Graphics2D objects are nicer to work with. Luckily
		// they're a subclass, and swing always gives them out anyway, so we can
		// just do this.
		Graphics2D g2 = (Graphics2D) g;

		// draw all the segments.
		g2.setColor(Mapper.SEGMENT_COLOUR);
		for (Segment s : segments)
			s.draw(g2, origin, scale);

		// draw the segments of all highlighted roads.
		g2.setColor(Mapper.HIGHLIGHT_COLOUR);
		g2.setStroke(new BasicStroke(3));
		for (Road road : highlightedRoads) {
			for (Segment seg : road.components) {
				seg.draw(g2, origin, scale);
			}
		}
		for (Segment seg : highlightedSegs) {
			seg.draw(g2, origin, scale);
		}

		// draw all the nodes.
		g2.setColor(Mapper.NODE_COLOUR);
		for (Node n : nodes.values())
			n.draw(g2, screen, origin, scale);

		// draw the highlighted node, if it exists.
		if (highlightedNode != null) {
			g2.setColor(Mapper.HIGHLIGHT_COLOUR);
			highlightedNode.draw(g2, screen, origin, scale);
		}
		if (departNode != null) {
			g2.setColor(Mapper.DEPARTFROM_COLOUR);
			departNode.draw(g2, screen, origin, scale);
		}
		g2.setColor(Mapper.DESTINATION_COLOUR);
		if (destNode != null) {
			destNode.draw(g2, screen, origin, scale);
		}
		for (Node node : highlightedNodes) {
			node.draw(g2, screen, origin, scale);
		}
	}

	public Node getHighlightedNode() {return this.highlightedNode;}

	public void setHighlight(Node node) {this.highlightedNode = node;}

	public void setHighlightedNodes(Collection<Node> nodes) {this.highlightedNodes = nodes;}

	public void addHighlightedNode(Node node) {this.highlightedNodes.add(node);}

	public Node getFrom() {return this.departNode;}

	public void setFrom(Node node) {this.departNode = node;}

	public Node getTo() {return this.destNode;}

	public void setTo(Node node) {this.destNode = node;}

	public void setHighlightedRoads(Collection<Road> roads) {
		this.highlightedRoads = roads;
	}

	public void setHighlightedSegs(Collection<Segment> segs) {
		this.highlightedSegs = segs;
	}

	public boolean isBadJourney() {
		return this.departNode == null || this.destNode == null || this.departNode==this.destNode;
	}
}

// code for COMP261 assignments
