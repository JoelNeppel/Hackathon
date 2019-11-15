package test;

import static org.junit.Assert.assertEquals;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

import communication.DataTransfer;
import host.Client;
import nutty.DoublyLinkedList;
import nutty.Nut;
import nutty.Squirrel;

/**
 * @author JoelNeppel
 *
 */
class Test
{
	static DoublyLinkedList<Squirrel> squirrels;

	static DoublyLinkedList<Client> clients;

	static DoublyLinkedList<Nut> nuts;

	static Socket s;

	static ServerSocket ss;

	static Socket h;

	public static void main(String[] args) throws Exception
	{
		setUp();
		testNutAdd();
		tearDown();
	}

	/**
	 * @throws java.lang.Exception
	 */
	static void setUp() throws Exception
	{
		squirrels = new DoublyLinkedList<>();
		nuts = new DoublyLinkedList<>();
		clients = new DoublyLinkedList<>();
		squirrels.add(new Squirrel(0, 0, 0));
		squirrels.add(new Squirrel(1, 100, 100));
		squirrels.add(new Squirrel(50, 736, 637));

		for(int i = 0; i < 10; i++)
		{
			nuts.add(new Nut(i * 10, i * 5));
		}

		ss = new ServerSocket(5647);
		s = new Socket("localhost", 5647);
		h = ss.accept();
	}

	/**
	 * @throws java.lang.Exception
	 */
	static void tearDown() throws Exception
	{
		s.close();
		ss.close();
	}

	static void testNutAdd() throws Exception
	{
		for(Nut nu : nuts)
			h.getOutputStream().write(DataTransfer.sendNutAddition(nu));

		DoublyLinkedList<Nut> nuuut = new DoublyLinkedList<>();

		for(int i = 0; i < nuts.size(); i++)
		{
			s.getInputStream().read();
			DataTransfer.receiveNutAddition(s.getInputStream(), nuuut);
		}

		Iterator<Nut> iter = nuuut.iterator();
		System.out.println("TestNutAdd");
		for(Nut nu : nuts)
		{
			System.out.println(nu.equals(iter.next()));
		}
	}

	static void testFull() throws Exception
	{
		h.getOutputStream().write(DataTransfer.sendFullUpdate(nuts, clients));
		DoublyLinkedList<Squirrel> ssss = new DoublyLinkedList<>();
		DoublyLinkedList<Nut> n = new DoublyLinkedList<>();

		DataTransfer.receiveFullUpdate(s.getInputStream(), n, ssss);

		Iterator<Squirrel> iter = squirrels.iterator();
		for(Squirrel sq : ssss)
		{
			Squirrel ne = iter.next();
			assertEquals(sq.getNumNuts(), ne.getNumNuts());
			assertEquals(sq.getX(), ne.getX());
			assertEquals(sq.getY(), ne.getY());
		}

		Iterator<Nut> i = n.iterator();
		for(Nut nu : nuts)
		{
			assertEquals(nu, i.next());
		}
	}

}
