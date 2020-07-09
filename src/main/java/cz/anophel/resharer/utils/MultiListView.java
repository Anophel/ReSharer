package cz.anophel.resharer.utils;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper list structure. Creates a simple view over
 * more lists. It's property depends on passed lists,
 * because it does not copy any values from the lists.
 * 
 * @author Patrik Vesely
 *
 * @param <E>
 */
public class MultiListView<E> extends AbstractList<E> implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private final ArrayList<List<? extends E>> lists;

	public MultiListView(List<? extends E> list1, List<? extends E> list2, List<? extends E> list3) {
		this(list1, list2);
		lists.add(Collections.unmodifiableList(list3));
	}

	public MultiListView(List<? extends E> list1, List<? extends E> list2) {
		this(list1);
		lists.add(Collections.unmodifiableList(list2));
	}

	public MultiListView(List<? extends E> list1) {
		this.lists = new ArrayList<List<? extends E>>();
		lists.add(Collections.unmodifiableList(list1));
	}

	@Override
	public E get(int index) {
		for (int i = 0; i < lists.size(); i++) {
			if (lists.get(i).size() > index)
				return lists.get(i).get(index);
			else
				index -= lists.get(i).size();
		}

		throw new IndexOutOfBoundsException();
	}

	@Override
	public int size() {
		return lists.stream().map(e -> e.size()).reduce(0, (acc, e) -> acc + e);
	}

}
