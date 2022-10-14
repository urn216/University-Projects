package code;

import java.util.List;
import java.util.ArrayList;

public class OrderMap<K,V>{
  private List<K> keyList;
  private List<V> valueList;

  /**
  * A map with additional functionality to suit my needs. Namely, keeps order of elements
  */
  public OrderMap() {
    keyList = new ArrayList<K>();
    valueList = new ArrayList<V>();
  }

  public void put(K key, V value) {
    keyList.add(key);
    valueList.add(value);
  }

  public V get(K key) {
    for (int i = 0; i < keyList.size(); i++) if (key.equals(keyList.get(i))) return valueList.get(i);
    return null;
  }

  public V getVal(int i) {
    return valueList.get(i);
  }

  public K getKey(int i) {
    return keyList.get(i);
  }

  public int indexOf(K key) {
    for (int i = 0; i < keyList.size(); i++) if (key.equals(keyList.get(i))) return i;
    return -1;
  }

  public List<K> getKeys() {
    return keyList;
  }

  public List<V> getVals() {
    return valueList;
  }
}
