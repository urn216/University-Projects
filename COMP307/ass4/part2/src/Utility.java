import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Utility {

  /**
  * Calculate the Euclidean distance between two VRP nodes
  * @param node1 the first VRP node
  * @param node2 the second VRP node
  * @return the Euclidean distance between node1 and node2
  */
  public static double calculateEuclideanDistance(VRPNode node1, VRPNode node2) {
    return Math.sqrt(Math.pow(node1.x-node2.x,2)+Math.pow(node1.y-node2.y,2));
  }

  /**
  * Calculate the total cost of a VRP solution under a VRP instance.
  * The total cost is the sum of all the Euclidean distance between adjacent nodes in the routes.
  * @param solution the VRP solution.
  * @param instance the VRP instance.
  * @return the total cost of the solution.
  */
  public static double calculateTotalCost(VRPSolution solution, VRPInstance instance) {
    double cost = 0;
    for (List<Integer> route : solution.getRoutes()) {
      cost += calculateEuclideanDistance(instance.getDepot(), instance.getNodes().get(route.get(0)));

      for (int i = 0; i < route.size()-1; i++) {
        cost += calculateEuclideanDistance(instance.getNodes().get(route.get(i)), instance.getNodes().get(route.get(i+1)));
      }

      cost += calculateEuclideanDistance(instance.getNodes().get(route.get(route.size()-1)), instance.getDepot());
    }
    return cost;
  }

  /**
  * Generate a VRP solution for a VRP instance using the nearest neighbour heuristic.
  * @param instance the VRP instance.
  * @return the VRP solution generated by the nearest neighbour heuristic.
  */
  public static VRPSolution nearestNeighbourHeuristic(VRPInstance instance) {
    List<List<Integer>> routes = new ArrayList<List<Integer>>();
    List<Integer> route = new ArrayList<Integer>();
    List<VRPNode> toExplore = new ArrayList<VRPNode>();
    toExplore.addAll(instance.getNodes().values());

    //create routes
    while(true) {
      VRPNode current = instance.getDepot();
      double space = instance.getCapacity();

      //add nodes to this route
      while(true) {
        toExplore.remove(current);
        VRPNode nearest = findNearest(current, toExplore, space);

        if (nearest == null) break;

        space -= nearest.getDemand();
        current = nearest;
        route.add(current.ID);
      }

      routes.add(route);

      if (toExplore.isEmpty()) break;

      route = new ArrayList<Integer>();
    }
    return new VRPSolution(routes);
  }

  private static VRPNode findNearest(VRPNode current, List<VRPNode> toExplore, double limit) {
    VRPNode nearest = null;
    double shortDist = Double.MAX_VALUE;
    for (VRPNode node : toExplore) {
      if (node.getDemand() > limit) continue;
      double dist = calculateEuclideanDistance(current, node);
      if (dist < shortDist) {
        shortDist = dist;
        nearest = node;
      }
    }
    return nearest;
  }

  /**
  * Generate a VRP solution for a VRP instance using the savings heuristic.
  * @param instance the VRP instance.
  * @return the VRP solution generated by the savings heuristic.
  */
  public static VRPSolution savingsHeuristic(VRPInstance instance) {
    List<List<Integer>> routes = new ArrayList<List<Integer>>();

    //initialise all routes to a single stop trip
    for (VRPNode node : instance.getNodes().values()) {
      if (node.ID==1) continue;
      routes.add(List.of(node.ID));
    }

    double[][] savings = new double[instance.getNodes().size()+1][instance.getNodes().size()+1];
    for (double[] line : savings) Arrays.fill(line, 0);

    for (int i = 2; i < instance.getNodes().size(); i++) {
      for (int j = i+1; j < instance.getNodes().size()+1; j++) {
        VRPNode node1 = instance.getNodes().get(i);
        VRPNode node2 = instance.getNodes().get(j);

        savings[i][j] = calculateEuclideanDistance(node1, instance.getDepot()) + calculateEuclideanDistance(instance.getDepot(), node2) - calculateEuclideanDistance(node1, node2);
        savings[j][i] = savings[i][j];
      }
    }


    while(true) {
      double bestSaving = 0;
      List<Integer> bestMerge = null;

      for (List<Integer> route1 : routes) {
        for (List<Integer> route2 : routes) {
          if (route1==route2) continue;

          double saving = savings[route1.get(route1.size()-1)][route2.get(0)];
          if (saving <= bestSaving) continue;

          List<Integer> merge = new ArrayList<Integer>();
          merge.addAll(route1);
          merge.addAll(route2);

          double totDemand = 0;
          for (int i : merge) {
            totDemand += instance.getNodes().get(i).getDemand();
          }
          if (totDemand > instance.getCapacity()) continue;

          bestSaving = saving;
          bestMerge = merge;
        }
      }

      if (bestMerge == null) break;
      for (int i = routes.size()-1; i >= 0; i--) {
        if (bestMerge.containsAll(routes.get(i))) routes.remove(i);
      }
      routes.add(bestMerge);
    }

    return new VRPSolution(routes);
  }

}
