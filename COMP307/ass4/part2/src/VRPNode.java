public class VRPNode {
  public final int ID; // the ID of the node
  public final double x; // the x-axis position
  public final double y; // the y-axis position
  private double demand; // the demand of the node

  public VRPNode(int ID, double x, double y, double demand) {
    this.ID = ID;
    this.x = x;
    this.y = y;
    this.demand = demand;
  }

  public VRPNode(int ID, double x, double y) {
    this.ID = ID;
    this.x = x;
    this.y = y;
    this.demand = 0;
  }

  public double getDemand() {
    return demand;
  }

  public void setDemand(double demand) {
    this.demand = demand;
  }
}
