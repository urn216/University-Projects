public class QueueElem implements Comparable<QueueElem>{

  public final Node node;

  public final Segment prev;

  public final double g;

  public final double f;

  public QueueElem(Node node, Segment prev, double g, double f) {
    this.node = node;
    this.prev = prev;
    this.g = g;
    this.f = f;
  }

  public int compareTo(QueueElem other) {
    return ((Double)this.f).compareTo((Double)other.f);
  }
}
