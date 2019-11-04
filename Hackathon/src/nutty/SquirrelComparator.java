package nutty;

import java.util.Comparator;

public class SquirrelComparator implements Comparator<Squirrel>
{
	@Override
	public int compare(Squirrel s1, Squirrel s2)
	{
		if(s1.getNumNuts() < s2.getNumNuts())
		{
			return 1;
		}
		else if(s1.getNumNuts() > s2.getNumNuts())
		{
			return -1;
		}
		else
		{
			return s1.getID() - s2.getID();
		}
	}
}