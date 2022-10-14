package code;

import java.util.ArrayList;
import java.util.List;

/**
* Merge sort.
*/
public class Sorter {
  /**
	 * Sorts a given list using a specified sorting technique
	 *
	 * @param <T> The object type contained within the list to sort
	 * @param list the partially sorted list
	 * @return the sorted list
	 */
  public static <T extends Comparable<? super T>> List<T> sort(List<T> list) {
    if (list == null || list.isEmpty()) return list;
    return mSort(list, 0, list.size()-1);
  }

  /**
  * implements the merge-sort recursive algorithm
  *
  * @param <T> The object type contained within the list to sort
  * @param list the unsorted list
  * @param a the index to start sorting from
  * @param c the final index to sort to
  * @return a sorted list
  */
  public static <T extends Comparable<? super T>> List<T> mSort(List<T> list, int a, int c) {
    if (a >= c) {
      //base case. if only one element, then the one element is sorted already
      return List.of(list.get(a));
    }

    //divide into two sublists, and sort them in sequence
    int b = (a+c)/2;
    List<T> l1 = mSort(list, a, b);
    List<T> l2 = mSort(list, b+1, c);

    //merge the now sorted sublists
    return merge(l1, l2);
  }

  /**
  * merges two sorted sublists into one sorted list
  *
  * @param <T> The object contained within the lists to merge
  * @param list1 the first sorted list
  * @param list2 the second sorted list
  * @return the sorted and merged list
  */
  public static <T extends Comparable<? super T>> List<T> merge(List<T> list1, List<T> list2) {
    int i = 0, j = 0;
    List<T> result = new ArrayList<T>();
    while (i < list1.size() || j < list2.size()) {
      if(j >= list2.size() || (i < list1.size() && list1.get(i).compareTo(list2.get(j)) < 0)) {
        result.add(list1.get(i));
        i++;
        continue;
      }
      result.add(list2.get(j));
      j++;
    }
    return result;
  }
}
