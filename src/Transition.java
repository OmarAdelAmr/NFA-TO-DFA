
public class Transition
{
	String start;
	String operator;
	String end;

	public Transition(String start, String operator, String end)
	{
		this.start = start;
		this.operator = operator;
		this.end = end;
	}

	public String toString()
	{
		return start + "," + operator + "," + end;
	}

	public boolean isEpsilon()
	{
		if (operator.equals("!"))
		{
			return true;
		}
		return false;
	}
}
