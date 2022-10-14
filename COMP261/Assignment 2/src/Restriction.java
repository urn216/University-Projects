
/**
 * Don't you dare turn right, you bastard.
 */
public class Restriction {

	public final Road road1, road2;
	public final Node start, core, end;

	public Restriction(Graph graph, int road1ID, int road2ID, int node1ID, int node2ID, int node3ID) {

		this.road1 = graph.roads.get(road1ID);
		this.road2 = graph.roads.get(road2ID);
		this.start = graph.nodes.get(node1ID);
		this.core = graph.nodes.get(node2ID);
		this.end = graph.nodes.get(node3ID);
	}

	public final String toString() {
		return "Restriction: " + start + ", " + road1 + ", " + core;
	}
}

// code for COMP261 assignments
