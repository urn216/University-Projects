import java.util.Stack;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;

/**
* This is the main class for the mapping program. It extends the GUI abstract
* class and implements all the methods necessary, as well as having a main
* function.
*
* @author tony
*/
public class Mapper extends GUI {
	public static final Color NODE_COLOUR = new Color(77, 113, 255);
	public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
	public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);
	public static final Color DEPARTFROM_COLOUR = new Color(27, 219, 77);
	public static final Color DESTINATION_COLOUR = new Color(255, 77, 10);

	// these two constants define the size of the node squares at different zoom
	// levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
	// log(scale)
	public static final int NODE_INTERCEPT = 1;
	public static final double NODE_GRADIENT = 0.8;

	// defines how much you move per button press, and is dependent on scale.
	public static final double MOVE_AMOUNT = 100;
	// defines how much you zoom in/out per button press, and the maximum and
	// minimum zoom levels.
	public static final double ZOOM_FACTOR = 1.3;
	public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

	// how far away from a node you can click before it isn't counted.
	public static final double MAX_CLICKED_DISTANCE = 0.15;

	// these two define the 'view' of the program, ie. where you're looking and
	// how zoomed in you are.
	private Location origin;
	private double scale;

	private double prevX;
	private double prevY;

	private boolean dragged = false;
	private int dragCount = 0;

	private boolean calcDist = true;

	// our data structures.
	private Graph graph;
	private Trie trie;
	private HashSet<Node> APs = new HashSet<Node>();

	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
		graph.draw(g, getDrawingAreaDimension(), origin, scale);
	}

	@Override
	protected void onDist() {calcDist = true;}

	@Override
	protected void onTime() {calcDist = false;}

	@Override
	protected void onArtPts() {
		resetAllNodeDepth();
		APs = new HashSet<Node>();
		// search every disconnected graph
		for (Node root : graph.nodes.values()) {
			if (root.getDepth()>=0) {continue;}
			int numSubs = 0;
			root.setDepth(0);
			// once in an unexplored graph, explore it.
			for (Node nb : root.getNeighbours()) {
				if (nb.getDepth()<0) {
					iterativeAP(nb, root, 1);
					numSubs++;
				}
				if (numSubs > 1) {APs.add(root);}
			}
		}

		//draw the articulation points!
		getTextOutputArea().setText("Drawing Critical Points...\n");
		graph.setHighlightedNodes(APs);
		getTextOutputArea().append(APs.size() + " Nodes");
	}

	/**
	* Resets all the fields in the graph's nodes used internally during Articulation Point searching.
	*/
	private void resetAllNodeDepth() {
		for (Node n : graph.nodes.values()) {
			n.setDepth(-1);
			n.setRBack(-1);
			n.setChildren(new HashSet<Node>());
		}
	}

	/**
	* Performs the iterative calculations to find articulation points.
	*/
	private void iterativeAP(Node first, Node root, int depth) {
		Stack<StackElem> stack = new Stack<StackElem>();
		stack.push(new StackElem(first, root, depth));
		while(!stack.isEmpty()) {
			StackElem e = stack.peek();

			//firstly, if we haven't started working on this one, set up all the useful fields within the node.
			if(e.node.getDepth()<0) {
				e.node.setDepth(e.depth);
				e.node.setRBack(e.depth);
				Collection<Node> nbs = e.node.getNeighbours();
				nbs.remove(e.parent);
				e.node.setChildren(nbs);
			}
			//then, if it has children, start whittling them down and adding them to the stack.
			else if(!e.node.getChildren().isEmpty()) {
				Node child = null;
				Collection<Node> children = e.node.getChildren();
				for (Node c : children) {child = c; break;}
				children.remove(child);
				if (child.getDepth()>=0) {e.node.setRBack(Math.min(e.node.getRBack(), child.getDepth()));}
				else {stack.push(new StackElem(child, e.node, e.depth+1));}
			}
			//once we run out of children to check, figure out if this is indeed an articulation point.
			else {
				if (first!=e.node) {
					e.parent.setRBack(Math.min(e.parent.getRBack(), e.node.getRBack()));
					if (e.node.getRBack()>=e.parent.getDepth()) {APs.add(e.parent);}
				}
				stack.pop();
			}
		}
	}

	@Override
	protected void onFrom() {graph.setFrom(graph.getHighlightedNode());}

	@Override
	protected void onTo() {graph.setTo(graph.getHighlightedNode());}

	@Override
	protected void onJourney() {
		if (graph.isBadJourney()) {
			getTextOutputArea().setText("Please specify both a destination and a point of departure. (Cannot be the same location. You got there already, silly)");
			graph.setHighlightedSegs(new HashSet<Segment>());
			return;
		}
		Node start = graph.getFrom();
		Node goal = graph.getTo();
		aStar(start, goal);
		getPath(goal);
	}

	/**
	* Calculate a path from a starting location to a goal location using A* search.
	* Depending on predefined settings, can be based on distance or time.
	*/
	private void aStar(Node start, Node goal) {
		goal.setPrev(null); // reset goal's previous segment in case no path is found
		PriorityQueue<QueueElem> fringe = new PriorityQueue<QueueElem>(graph.nodes.size());
		HashSet<Node> visited = new HashSet<Node>();
		//offer the starting element
		fringe.offer(new QueueElem(start, null, 0, getF(start.location, goal.location, 0)));

		//do the A* search
		while(!fringe.isEmpty()) {
			QueueElem e = fringe.poll();
			if (visited.contains(e.node)) {continue;}
			visited.add(e.node);
			e.node.setPrev(e.prev);

			if (e.node==goal) {break;}

			Collection<Segment> segStarts = new HashSet<Segment>();
			segStarts.addAll(e.node.segmentStarts);

			for (Restriction r : graph.restrictions) {
				if (e.node==r.core && e.prev.road==r.road1) {
					for (Segment seg : e.node.segmentStarts) {
						if (seg.start == r.end || seg.end == r.end) {
							segStarts.remove(seg);
							// graph.addHighlightedNode(e.node);
						}
					}
				}
			}

			for (Segment edge : segStarts) {
				if (!visited.contains(edge.end)) {
					double g = e.g + getG(edge);
					double f = getF(e.node.location, goal.location, g);
					fringe.offer(new QueueElem(edge.end, edge, g, f));
				}
				else if (!visited.contains(edge.start)) {
					double g = e.g + getG(edge);
					double f = getF(e.node.location, goal.location, g);
					fringe.offer(new QueueElem(edge.start, edge, g, f));
				}
			}
		}
	}

	/**
	* @return The g function for use in A*.
	*/
	private double getG(Segment seg) {
		return calcDist ? seg.length : seg.length/(seg.road.speedLim*seg.road.roadclass/4.0);
	}

	/**
	* @return The f function for use in A* (g + h).
	*/
	private double getF(Location start, Location goal, double g) {
		return calcDist ? g + distH(start, goal) : g + timeH(start, goal);
	}

	/**
	* @return The heuristic function for distance from a point. Divide by two to make sure A* doesn't just beeline shortsightedly.
	*/
	private double distH(Location start, Location goal) {
		return start.distance(goal)/2;
	}

	/**
	* @return The heuristic function for time from a point to a goal using 100km/h and the maximum road class to give a low bound.
	*/
	private double timeH(Location start, Location goal) {
		return start.distance(goal)/125;
	}

	/**
	* Once a path has been calculated, work backwards through it to get the actual route taken.
	*/
	private void getPath(Node goal) {
		Node node = goal;
		// Hopefully found a path, now trace back steps to retrieve it
		ArrayList<Segment> segs = new ArrayList<Segment>();
		while(node.getPrev()!=null) {
			segs.add(node.getPrev());
			Node prev = node.getPrev().start;
			node = prev!=node ? prev : node.getPrev().end;
		}
		graph.setHighlightedSegs(segs);
		printPath(goal, node, segs);
	}

	/**
	* Prints out the path obtained from getPath to the textOutputArea.
	*/
	private void printPath(Node goal, Node node, ArrayList<Segment> segs) {
		// Print out the results to the textOutputArea
		if (node==goal) {
			getTextOutputArea().setText("Couldn't find it, Sorry. Maybe catch a flight or something?");
			return;
		}
		getTextOutputArea().setText("Plotting Route:\n");
		printTime(goal, node, segs);
		// if (calcDist) {printDist(goal, node, segs);}
		// else {printTime(goal, node, segs);}
	}

	/**
	* Prints the distance from one point to another.
	*/
	protected void printDist(Node goal, Node node, ArrayList<Segment> segs) {
		String roadName = null;
		double roadLen = 0;
		double totLen = 0;
		for (int i = segs.size()-1; i>=0; i--) {
			if (!segs.get(i).road.name.equals(roadName)) {
				if (roadName!=null) {getTextOutputArea().append(String.format("%.2f", roadLen) + "km");}
				getTextOutputArea().append("\n    " + segs.get(i).road.name + ": ");
				roadName = segs.get(i).road.name;
				roadLen = 0;
			}
			roadLen+=segs.get(i).length;
			totLen+=segs.get(i).length;
		}
		getTextOutputArea().append(String.format("%.2f", roadLen) + "km\n");
		getTextOutputArea().append("\nTotal Distance: " + String.format("%.2f", totLen) + "km");
	}


	/**
	* Prints the time taken to go from one point to another.
	*/
	protected void printTime(Node goal, Node node, ArrayList<Segment> segs) {
		String roadName = null;
		double roadLen = 0;
		double totLen = 0;
		double totTime = 0;
		for (int i = segs.size()-1; i>=0; i--) {
			if (!segs.get(i).road.name.equals(roadName)) {
				if (roadName!=null) {
					double speedLim = segs.get(i).road.speedLim;
					double time = 60*roadLen/speedLim;
					totTime += time;
					getTextOutputArea().append(String.format("%.2f", roadLen) + "km at " + speedLim + "km/h.\n        Will take " + String.format("%.2f", time) + " minutes.");
				}
				getTextOutputArea().append("\n    " + segs.get(i).road.name + ": ");
				roadName = segs.get(i).road.name;
				roadLen = 0;
			}
			roadLen+=segs.get(i).length;
			totLen+=segs.get(i).length;
		}
		double speedLim = segs.get(0).road.speedLim;
		double time = 60*roadLen/speedLim;
		totTime += time;
		getTextOutputArea().append(String.format("%.2f", roadLen) + "km at " + speedLim + "km/h.\n        Will take " + String.format("%.2f", time) + " minutes.\n");
		getTextOutputArea().append("\nTotal Distance: " + String.format("%.2f", totLen) + "km");
		getTextOutputArea().append("\nTotal Time: " + String.format("%.2f", totTime) + " minutes");
	}

	@Override
	protected void onClick(MouseEvent e) {
		if (dragged) {return;}
		Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);
		// find the closest node.
		double bestDist = Double.MAX_VALUE;
		Node closest = null;

		for (Node node : graph.nodes.values()) {
			double distance = clicked.distance(node.location);
			if (distance < bestDist) {
				bestDist = distance;
				closest = node;
			}
		}

		// if it's close enough, highlight it and show some information.
		if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
			graph.setHighlight(closest);
			getTextOutputArea().setText(closest.toString());
		}

		graph.setHighlightedNodes(new HashSet<Node>());
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
		origin = origin.moveBy((prevX-e.getX())/scale, (e.getY()-prevY)/scale);
		prevX = e.getX();
		prevY = e.getY();
		dragCount++;
	}

	@Override
	protected void onScroll(MouseWheelEvent e) {
		double oldScale = scale;
		scale = e.getWheelRotation()<0 ? scale*1.5 : scale/1.5;
		origin = new Location((e.getX()/oldScale+origin.x)-e.getX()/scale, (origin.y-e.getY()/oldScale)+e.getY()/scale);
	}

	@Override
	protected void onSearch() {
		if (trie == null)
		return;

		// get the search query and run it through the trie.
		String query = getSearchBox().getText();
		Collection<Road> selected = trie.get(query);

		// figure out if any of our selected roads exactly matches the search
		// query. if so, as per the specification, we should only highlight
		// exact matches. there may be (and are) many exact matches, however, so
		// we have to do this carefully.
		boolean exactMatch = false;
		for (Road road : selected)
		if (road.name.equals(query))
		exactMatch = true;

		// make a set of all the roads that match exactly, and make this our new
		// selected set.
		if (exactMatch) {
			Collection<Road> exactMatches = new HashSet<>();
			for (Road road : selected)
			if (road.name.equals(query))
			exactMatches.add(road);
			selected = exactMatches;
		}

		// set the highlighted roads.
		graph.setHighlightedRoads(selected);

		// now build the string for display. we filter out duplicates by putting
		// it through a set first, and then combine it.
		Collection<String> names = new HashSet<>();
		for (Road road : selected)
		names.add(road.name);
		String str = "";
		for (String name : names)
		str += name + "; ";

		if (str.length() != 0)
		str = str.substring(0, str.length() - 2);
		getTextOutputArea().setText(str);
	}

	@Override
	protected void onMove(Move m) {
		if (m == GUI.Move.NORTH) {
			origin = origin.moveBy(0, MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.SOUTH) {
			origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.EAST) {
			origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.WEST) {
			origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.ZOOM_IN) {
			if (scale < MAX_ZOOM) {
				// yes, this does allow you to go slightly over/under the
				// max/min scale, but it means that we always zoom exactly to
				// the centre.
				scaleOrigin(true);
				scale *= ZOOM_FACTOR;
			}
		} else if (m == GUI.Move.ZOOM_OUT) {
			if (scale > MIN_ZOOM) {
				scaleOrigin(false);
				scale /= ZOOM_FACTOR;
			}
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons, File restrictions) {
		graph = new Graph(nodes, roads, segments, polygons, restrictions);
		trie = new Trie(graph.roads.values());
		origin = new Location(-250, 250); // close enough
		scale = 1;
	}

	/**
	* This method does the nasty logic of making sure we always zoom into/out
	* of the centre of the screen. It assumes that scale has just been updated
	* to be either scale * ZOOM_FACTOR (zooming in) or scale / ZOOM_FACTOR
	* (zooming out). The passed boolean should correspond to this, ie. be true
	* if the scale was just increased.
	*/
	private void scaleOrigin(boolean zoomIn) {
		Dimension area = getDrawingAreaDimension();
		double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

		int dx = (int) ((area.width - (area.width * zoom)) / 2);
		int dy = (int) ((area.height - (area.height * zoom)) / 2);

		origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
	}

	public static void main(String[] args) {
		new Mapper();
	}
}

// code for COMP261 assignments
