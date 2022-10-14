public class StackElem implements Comparable<StackElem>{

  public final Node node;

  public final Node parent;

  public final int depth;

  public StackElem(Node node, Node parent, int depth) {
    this.node = node;
    this.parent = parent;
    this.depth = depth;
  }

  public int compareTo(StackElem other) {
    return ((Integer)this.depth).compareTo((Integer)other.depth);
  }
}
