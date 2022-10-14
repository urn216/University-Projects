import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class TrieNode {
  private Set<Stop> stops = new HashSet<Stop>();
  private Map<Character, TrieNode> childNodes = new HashMap<Character, TrieNode>();

  public TrieNode addNode(char c) {
    childNodes.putIfAbsent(c, new TrieNode());
    return childNodes.get(c);
  }

  public void addStop(Stop s) {stops.add(s);}

  public TrieNode getNode(char c) {return childNodes.get(c);}

  public Set<Stop> getLocalStops() {return stops;}

  public Set<Stop> getAllSubStops() {
    Set<Stop> childStops = new HashSet<Stop>();
    childStops.addAll(stops);
    for (TrieNode node : childNodes.values()) {childStops.addAll(node.getAllSubStops());}
    return childStops;
  }
}
