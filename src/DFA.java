import java.util.ArrayList;

public class DFA
{
	ArrayList<String> states;
	ArrayList<String> accept_states = new ArrayList<String>();
	ArrayList<Transition> transitions = new ArrayList<Transition>();
	String start_state;

	public DFA()
	{

	}

	public String states_toString()
	{
		construct_dfa();
		String res = "";
		for (int i = 0; i < states.size(); i++)
		{
			res += states.get(i).replaceAll("\\|", "") + ",";
		}
		if (res.endsWith(","))
		{
			res = res.substring(0, res.length() - 1);
		}
		return res;
	}

	public String start_states_toString()
	{
		return start_state.replaceAll("\\|", "");
	}

	public void construct_dfa()
	{
		for (int i = 2; i < transitions.size(); i++)
		{
			boolean exist = false;
			A: for (int j = 0; j < transitions.size(); j++)
			{
				if (transitions.get(i).start.equals(transitions.get(j).end))
				{
					exist = true;
					break A;
				}
			}
			if (!exist)
			{
				states.remove((transitions.get(i).start));
				accept_states.remove(transitions.get(i).start);
				transitions.remove(i);
				i--;

			}
		}
	}

	public String accept_states_toString()
	{
		String res = "";
		for (int i = 0; i < accept_states.size(); i++)
		{
			res += accept_states.get(i).replaceAll("\\|", "") + ",";
		}
		if (res.endsWith(","))
		{
			res = res.substring(0, res.length() - 1);
		}
		return res;
	}

	public String transitions_toString()
	{
		String res = "";

		for (int i = 0; i < transitions.size(); i++)
		{
			res += transitions.get(i).start.replaceAll("\\|", "");
			res += ",";
			res += transitions.get(i).operator;
			res += ",";
			res += transitions.get(i).end.replaceAll("\\|", "");
			res += "\n";
		}
		return res;
	}
}
