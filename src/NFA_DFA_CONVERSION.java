import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class NFA_DFA_CONVERSION
{

	static NFA input_nfa = new NFA();
	static ArrayList<String> dfa_states = new ArrayList<String>();
	static DFA output_dfa = new DFA();

	public static void construct_NFA() throws IOException
	{
		FileReader fr = new FileReader("NFA3.in");
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		while ((currentLine = br.readLine()) != null)
		{
			if (currentLine.equals("#set of states"))
			{
				String setOfStates = br.readLine();
				input_nfa.states = new ArrayList<String>(Arrays.asList(setOfStates.split(",")));
			} else if (currentLine.equals("#alphabet"))
			{
				String setOfAlphabet = br.readLine();
				input_nfa.alphabet = new ArrayList<String>(Arrays.asList(setOfAlphabet.split(",")));

			} else if (currentLine.equals("#transitions"))
			{
				String temp = "";
				while (!(temp = br.readLine()).equals("#start state"))
				{
					String[] tempArray = temp.split(",");
					Transaction tempTrans = new Transaction(tempArray[0], tempArray[1], tempArray[2]);
					input_nfa.transactions.add(tempTrans);
				}

				input_nfa.start_state = br.readLine();
			} else if (currentLine.equals("#set of accept states"))
			{
				String setOfAcceptedStates = br.readLine();
				input_nfa.accept_states = new ArrayList<String>(Arrays.asList(setOfAcceptedStates.split(",")));
			}

		}
		br.close();
		construct_DFA_states();
	}

	public static String get_all_epsilons(String x)
	{
		String temp_res = "";
		boolean has_epsilon = false;
		for (int i = 0; i < input_nfa.transactions.size(); i++)
		{
			if (x.equals(input_nfa.transactions.get(i).start) && input_nfa.transactions.get(i).operator.equals("!"))
			{
				has_epsilon = true;
				if (!temp_res.contains(input_nfa.transactions.get(i).end))
				{
					temp_res += input_nfa.transactions.get(i).end + "|";
					temp_res += get_all_epsilons(input_nfa.transactions.get(i).end);
				}

			}
		}

		if (!has_epsilon)
		{
			return "";
		}

		return temp_res;
	}

	public static void DFA_start_state()
	{
		System.out.println(">>>>>>>>>>" + get_all_epsilons(input_nfa.start_state));
		// boolean has_epsilon = false;
		// String epsilon_res = "";
		// for (int i = 0; i < input_nfa.transactions.size(); i++)
		// {
		// if (input_nfa.start_state.equals(input_nfa.transactions.get(i).start)
		// && input_nfa.transactions.get(i).operator.equals("!"))
		// {
		// has_epsilon = true;
		// if (!epsilon_res.contains(input_nfa.transactions.get(i).end))
		// {
		// epsilon_res += input_nfa.transactions.get(i).end + "|";
		// }
		//
		// }
		// }
		// System.out.println(has_epsilon);
		String epsilon_res = get_all_epsilons(input_nfa.start_state);
		if (!epsilon_res.equals(""))
		{
			dfa_states.add(input_nfa.start_state + "|" + epsilon_res);
		} else
		{
			for (int i = 0; i < input_nfa.alphabet.size(); i++)
			{
				String next_state = "";
				for (int j = 0; j < input_nfa.transactions.size(); j++)
				{
					if (input_nfa.start_state.equals(input_nfa.transactions.get(j).start)
							&& input_nfa.alphabet.get(i).equals(input_nfa.transactions.get(j).operator))
					{
						next_state += input_nfa.transactions.get(j).end + "|";

					}
				}
				dfa_states.add(next_state);
			}
			Set<String> remove_redundant = new LinkedHashSet<>(dfa_states);
			dfa_states.clear();
			dfa_states.addAll(remove_redundant);
		}

		System.out.println("START STATE IS DONE");
	}

	public static void construct_DFA_states()
	{
		// Start state
		DFA_start_state();
		for (int i = 0; i < dfa_states.size(); i++)
		{

			// System.out.println("?????????" + dfa_states.size());
			// System.out.println("COUNTER: " + i);
			// System.out.println(dfa_states.get(i));
			String[] set_elements = dfa_states.get(i).split("\\|");
			// System.out.println(dfa_states.get(i));
			// for (int j = 0; j < set_elements.length; j++)
			// {
			// System.out.println(set_elements[j]);
			// }

			for (int j2 = 0; j2 < input_nfa.alphabet.size(); j2++)
			{
				String next_state = "";
				for (int j = 0; j < set_elements.length; j++)
				{
					for (int k = 0; k < input_nfa.transactions.size(); k++)
					{
						// System.out.println(set_elements[j]);
						// System.out.println(input_nfa.transactions.get(k).start);
						// System.out.println(input_nfa.alphabet.get(j2));
						// System.out.println(input_nfa.transactions.get(k).operator);
						// System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						if (set_elements[j].equals(input_nfa.transactions.get(k).start)
								&& input_nfa.alphabet.get(j2).equals(input_nfa.transactions.get(k).operator))
						{
							if (!next_state.contains(input_nfa.transactions.get(k).end))
							{
								next_state += input_nfa.transactions.get(k).end + "|";
								next_state += get_all_epsilons(input_nfa.transactions.get(k).end);
							}

							// System.out.println("TRUE ==========>");
						}
					}

					// System.out.println("NEXT STATE: " + next_state);
					if (!dfa_states.contains(next_state) && !next_state.matches("\\s*"))
					{
						// System.out.println("REDUNTANT");
						dfa_states.add(next_state);
					}

				}
			}
		}
		set_dfa_accept_states();
	}

	public static void set_dfa_accept_states()
	{
		for (int i = 0; i < dfa_states.size(); i++)
		{
			String[] set_elements = dfa_states.get(i).split("\\|");
			A: for (int j = 0; j < set_elements.length; j++)
			{
				for (int j2 = 0; j2 < input_nfa.accept_states.size(); j2++)
				{
					// System.out.println(set_elements[j]);
					// System.out.println(input_nfa.accept_states.get(j2));
					if (set_elements[j].equals(input_nfa.accept_states.get(j2)))
					{
						output_dfa.accept_states.add(dfa_states.get(i));
						break A;
					}
				}
			}
		}
		set_dfa_tranactions();
	}

	public static void set_dfa_tranactions()
	{
		for (int i = 0; i < dfa_states.size(); i++)
		{
			String[] set_elements = dfa_states.get(i).split("\\|");
			for (int j = 0; j < input_nfa.alphabet.size(); j++)
			{
				String trans_res = "";
				for (int k = 0; k < set_elements.length; k++)
				{
					for (int k2 = 0; k2 < input_nfa.transactions.size(); k2++)
					{
						if (set_elements[k].equals(input_nfa.transactions.get(k2).start)
								&& input_nfa.alphabet.get(j).equals(input_nfa.transactions.get(k2).operator))
						{
							if (!trans_res.contains(input_nfa.transactions.get(k2).end))
							{
								trans_res += input_nfa.transactions.get(k2).end + "|";
							}

						}
					}
				}
				Transaction temp_trans = new Transaction(dfa_states.get(i), input_nfa.alphabet.get(j), trans_res);
				output_dfa.transactions.add(temp_trans);
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		construct_NFA();
		for (int i = 0; i < dfa_states.size(); i++)
		{
			System.out.println(dfa_states.get(i));
		}
		// System.out.println("ACCEPTED");
		// for (int i = 0; i < output_dfa.accept_states.size(); i++)
		// {
		// System.out.println(output_dfa.accept_states.get(i));
		// }
		System.out.println("TRANSACTIONS");
		for (int i = 0; i < output_dfa.transactions.size(); i++)
		{
			System.out.println(output_dfa.transactions.get(i));
		}
	}

}
