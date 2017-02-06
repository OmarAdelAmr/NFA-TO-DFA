import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class NFA_DFA_CONVERSION
{

	static String file_name = "NFA3.in";
	static NFA input_nfa = new NFA();
	static ArrayList<String> dfa_states = new ArrayList<String>();
	static DFA output_dfa = new DFA();

	public static void construct_NFA() throws IOException
	{
		FileReader fr = new FileReader(file_name);
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
					Transition tempTrans = new Transition(tempArray[0], tempArray[1], tempArray[2]);
					input_nfa.transitions.add(tempTrans);
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
		for (int i = 0; i < input_nfa.transitions.size(); i++)
		{
			if (x.equals(input_nfa.transitions.get(i).start) && input_nfa.transitions.get(i).operator.equals("!"))
			{
				has_epsilon = true;
				if (!temp_res.contains(input_nfa.transitions.get(i).end))
				{
					temp_res += input_nfa.transitions.get(i).end + "|";
					temp_res += get_all_epsilons(input_nfa.transitions.get(i).end);
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
		String epsilon_res = get_all_epsilons(input_nfa.start_state);
		output_dfa.start_state = input_nfa.start_state + epsilon_res;

		if (!epsilon_res.equals(""))
		{
			dfa_states.add(input_nfa.start_state + "|" + epsilon_res);
		} else
		{
			for (int i = 0; i < input_nfa.alphabet.size(); i++)
			{
				String next_state = "";
				for (int j = 0; j < input_nfa.transitions.size(); j++)
				{
					if (input_nfa.start_state.equals(input_nfa.transitions.get(j).start)
							&& input_nfa.alphabet.get(i).equals(input_nfa.transitions.get(j).operator))
					{
						next_state += input_nfa.transitions.get(j).end + "|";

					}
				}
				dfa_states.add(next_state);
			}
			Set<String> remove_redundant = new LinkedHashSet<>(dfa_states);
			dfa_states.clear();
			dfa_states.addAll(remove_redundant);
		}

	}

	public static void construct_DFA_states()
	{
		DFA_start_state();

		for (int i = 0; i < dfa_states.size(); i++)
		{

			String[] set_elements = dfa_states.get(i).split("\\|");

			for (int j2 = 0; j2 < input_nfa.alphabet.size(); j2++)
			{
				String next_state = "";
				for (int j = 0; j < set_elements.length; j++)
				{
					for (int k = 0; k < input_nfa.transitions.size(); k++)
					{

						if (set_elements[j].equals(input_nfa.transitions.get(k).start)
								&& input_nfa.alphabet.get(j2).equals(input_nfa.transitions.get(k).operator))
						{
							if (!next_state.contains(input_nfa.transitions.get(k).end))
							{
								next_state += input_nfa.transitions.get(k).end + "|";
								next_state += get_all_epsilons(input_nfa.transitions.get(k).end);
							}

						}
					}

					if (!dfa_states.contains(next_state) && !next_state.matches("\\s*"))
					{
						dfa_states.add(next_state);
					}

				}
			}
		}
		set_dfa_accept_states();
	}

	public static void check_for_reject_state()
	{
		boolean has_reject_state = false;
		A: for (int i = 0; i < output_dfa.transitions.size(); i++)
		{
			if (output_dfa.transitions.get(i).end.matches("\\s*"))
			{
				has_reject_state = true;
				break A;
			}
		}

		if (has_reject_state)
		{
			dfa_states.add("");
			for (int i = 0; i < input_nfa.alphabet.size(); i++)
			{
				output_dfa.transitions.add(new Transition("", input_nfa.alphabet.get(i), ""));
			}
		}
		construct_dfa_obj();
	}

	public static void construct_dfa_obj()
	{
		output_dfa.states = dfa_states;
		create_output_file();
	}

	public static void create_output_file()
	{
		try
		{
			String output_file = file_name.replaceAll("\\D+", "");
			PrintWriter fw = new PrintWriter(new FileWriter("DFA" + output_file + ".out"));
			fw.println("States:");
			fw.print(output_dfa.states_toString());
			fw.println();
			fw.println("Start state:");
			fw.println(output_dfa.start_states_toString());
			fw.println("Final states");
			fw.println(output_dfa.accept_states_toString());
			fw.println("Transitions:");
			fw.println(output_dfa.transitions_toString());

			fw.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
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
					for (int k2 = 0; k2 < input_nfa.transitions.size(); k2++)
					{
						if (set_elements[k].equals(input_nfa.transitions.get(k2).start)
								&& input_nfa.alphabet.get(j).equals(input_nfa.transitions.get(k2).operator))
						{
							if (!trans_res.contains(input_nfa.transitions.get(k2).end))
							{
								trans_res += input_nfa.transitions.get(k2).end + "|";
								trans_res += get_all_epsilons(input_nfa.transitions.get(k2).end);
							}

						}
					}
				}
				Transition temp_trans = new Transition(dfa_states.get(i), input_nfa.alphabet.get(j), trans_res);
				output_dfa.transitions.add(temp_trans);
			}
		}
		check_for_reject_state();
	}

	public static void main(String[] args) throws IOException
	{
		construct_NFA();
		System.out.println("States:");
		System.out.println(output_dfa.states_toString());

		System.out.println("Start state:");
		System.out.println(output_dfa.start_states_toString());

		System.out.println("Final states:");
		System.out.println(output_dfa.accept_states_toString());

		System.out.println("Transitions");
		System.out.println(output_dfa.transitions_toString());

	}

}
