
public class Tuple {

  public final int offset;
  public final int length;
  public final char ch;

  public Tuple(int offset, int length, char ch) {
    this.offset = offset;
    this.length = length;
    this.ch = ch;
  }

  public String toString() {
    return "["+offset+"|"+length+"|"+ch+"]";
  }
}
