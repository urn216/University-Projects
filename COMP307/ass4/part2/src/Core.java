import java.io.File;

public class Core {

  public static void main(String[] args) {
    String inst = "n32-k5";
    // String inst = "n80-k10";

    File instFile = new File("files/" + inst + ".vrp");
    VRPInstance instance = VRPIO.loadInstance(instFile);

    VRPSolution nnSol = Utility.nearestNeighbourHeuristic(instance);
    nnSol.setTotalCost(Utility.calculateTotalCost(nnSol, instance));

    VRPSolution svSol = Utility.savingsHeuristic(instance);
    svSol.setTotalCost(Utility.calculateTotalCost(svSol, instance));

    VRPIO.writeSolution(nnSol, "files/" + inst + "nn.sol");
    VRPIO.writeSolution(svSol, "files/" + inst + "sv.sol");
  }
}
