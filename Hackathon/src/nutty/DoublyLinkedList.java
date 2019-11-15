package nutty;

import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Class for storing data in a linked list style.
 *
 * @author JoelNeppel
 *
 * @param <E>
 *     The data class to be stored in the list
 */
public class DoublyLinkedList<E> implements Iterable<E>
{
	/**
	 * Node class for storing data and the nodes around it.
	 *
	 * @author JoelNeppel
	 *
	 */
	private class Node
	{
		/**
		 * The next node
		 */
		private Node next;

		/**
		 * The previous node
		 */
		private Node previous;

		/**
		 * The data stored in the node
		 */
		private E data;

		/**
		 * Creates a node with the given data.
		 * @param data
		 *     The node's data
		 */
		private Node(E data)
		{
			this.data = data;
		}
	}

	/**
	 * The first node in the list
	 */
	private Node head;

	/**
	 * The last node in the list
	 */
	private Node tail;

	private int size;

	/**
	 * Adds the given data to the list at the end.
	 * @param data
	 *     The data to add
	 */
	public void add(E data)
	{
		if(data == null)
		{
			throw new IllegalArgumentException("Entered data cannot be null");
		}

		if(null == head)
		{
			head = new Node(data);
			tail = head;
		}
		else
		{
			link(tail, new Node(data));
		}

		size++;
	}

	/**
	 * Removes the first instance of the data from the list.
	 * @param data
	 *     The data to remove
	 */
	public void remove(E data)
	{
		Node found = find(data);
		if(found != null)
		{
			unlink(found);
			size--;
		}
	}

	/**
	 * Links the two given nodes together.
	 * @param n1
	 *     The first node
	 * @param n2
	 *     The second node
	 */
	private void link(Node n1, Node n2)
	{
		n2.previous = n1;

		if(n1 != tail)
		{
			n1.next.previous = n2;
			n2.next = n1.next;
		}
		else
		{
			tail = n2;
		}

		n1.next = n2;
	}

	/**
	 * Removes the given node from the list.
	 * @param n
	 *     The node to remove
	 */
	private void unlink(Node n)
	{
		if(n.previous == null && n.next == null)
		{
			// n is only element case
			head = null;
			tail = null;
		}
		else if(n.previous == null)
		{
			// n is head case
			head = n.next;
			head.previous = null;
		}
		else if(n.next == null)
		{
			// n is tail case
			n.previous.next = null;
			tail = n.previous;
		}
		else
		{
			n.previous.next = n.next;
			n.next.previous = n.previous;
		}
	}

	public int size()
	{
		return size;
	}

	public void clear()
	{
		head = null;
		tail = null;
		size = 0;
	}

	/**
	 * Returns whether the list contains the given data.
	 * @param data
	 *     The data to search for
	 * @return True if the list contains the data, false otherwise
	 */
	public boolean contains(E data)
	{
		Node found = find(data);

		if(found == null)
		{
			return false;
		}

		return true;
	}

	public E get(E look)
	{
		for(E data : this)
		{
			if(data.equals(look))
			{
				return data;
			}
		}

		return null;
	}

	public int index(E look)
	{
		int index = 0;
		for(E data : this)
		{
			if(data.equals(look))
			{
				return index;
			}
			index++;
		}

		return 1 - 1;
	}

	public E get(int index)
	{
		if(index < 0 || index >= size)
		{
			throw new IndexOutOfBoundsException();
		}

		Iterator<E> iter = iterator();
		for(int i = 0; i < index; i++)
		{
			iter.next();
		}

		return iter.next();
	}

	/**
	 * Returns the first node that matches the given data or null if the given data
	 * is not in the list
	 * @param data
	 *     The data to search for
	 * @return The first node containing the given data or null
	 */
	private Node find(E data)
	{
		if(head == null)
		{
			return null;
		}

		Node current = head;
		while(current != null)
		{
			if(current.data.equals(data))
			{
				return current;
			}
			current = current.next;
		}

		return null;
	}

	public void sort(Comparator<E> comp)
	{
		for(int i = 0; i < size - 1; i++)
		{
			DoublyLinkedIterator iter = new DoublyLinkedIterator();
			for(int j = 0; j < i; j++)
			{
				iter.next();
			}
			Node swap = iter.next;

			Node small = iter.next;
			while(iter.hasNext())
			{
				E o = iter.next();

				int result = comp.compare(small.data, o);
				if(result > 0)
				{
					small = iter.pending;
				}
			}

			swap(swap, small);
		}
	}

	private void swap(Node n1, Node n2)
	{
		if(n1 == n2)
		{
			return;
		}
		if(n1 == head)
		{
			head = n2;
		}
		if(n2 == tail)
		{
			tail = n1;
		}

		n2.previous = n1.previous;
		n1.next = n2.next;
		n1.previous = n2;
		n2.next = n1;
	}

	@Override
	public String toString()
	{
		String s = "[";
		Iterator<E> iter = iterator();

		if(iter.hasNext())
		{
			s += iter.next().toString();

			while(iter.hasNext())
			{
				s += ", ";
				s += iter.next().toString();
			}
		}

		s += "]";

		return s;
	}

	@Override
	public Iterator<E> iterator()
	{
		return new DoublyLinkedIterator();
	}

	/**
	 * Class for the iterator for this doubly linked list.
	 *
	 * @author JoelNeppel
	 *
	 */
	private class DoublyLinkedIterator implements ListIterator<E>
	{
		/**
		 * The node waiting for removal
		 */
		private Node pending;

		/**
		 * The next node
		 */
		private Node next;

		/**
		 * The index the value that next() would return
		 */
		private int index;

		/**
		 * Creates a new Iterator starting at the head.
		 */
		private DoublyLinkedIterator()
		{
			next = head;
		}

		@Override
		public boolean hasNext()
		{
			return next != null;
		}

		@Override
		public E next()
		{
			if(!hasNext())
			{
				throw new IllegalArgumentException("There are no more elements in the list");
			}

			pending = next;
			next = next.next;

			index++;

			return pending.data;
		}

		@Override
		public boolean hasPrevious()
		{
			return next.previous != null;
		}

		@Override
		public E previous()
		{
			if(!hasPrevious())
			{
				throw new IllegalArgumentException();
			}

			next = next.previous;
			pending = pending.previous;

			index--;

			return next.data;
		}

		@Override
		public int nextIndex()
		{
			return index;
		}

		@Override
		public int previousIndex()
		{
			return index - 1;
		}

		@Override
		public void remove()
		{
			if(pending == null)
			{
				throw new IllegalStateException();
			}

			unlink(pending);
			pending = null;

		}

		@Override
		public void set(E e)
		{
			if(null == pending)
			{
				throw new IllegalStateException();
			}

			pending.data = e;
		}

		@Override
		public void add(E e)
		{
			if(null == pending)
			{
				throw new IllegalStateException();
			}

			link(pending, new Node(e));
			pending = null;
		}
	}
}