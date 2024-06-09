import java.util.*;
import java.io.*;

public class Main3 {
    private static List<Boolean> reachableStates;

    public static void main(String[] args) {
        try {
            // Create a File object representing input.txt
            File file = new File("C:/Users/User/IdeaProjects/untitled12/src/input.txt");
//            File file = new File("input.txt");


            // Create a Scanner to read from the file
            Scanner scanner = new Scanner(file);
            // Read input
            Map<String, String> input = readInput(scanner);

            try {
                // Extract input data
                String type = input.get("type").replaceAll("\\[|\\]", "");

                String statesStr = input.get("states").replaceAll("\\[|\\]", "");
                List<String> states = Arrays.asList(statesStr.split(",")); // Split by comma with optional whitespace
                if (states.size() == 1 && states.get(0).equals("")) {
                    System.out.println("E1: Input file is malformed");
                    System.exit(0);
                }
//                Collections.sort(states);

                String alphabetStr = input.get("alphabet").replaceAll("\\[|\\]", "");
                List<String> alphabet = Arrays.asList(alphabetStr.split(","));
                if (alphabet.size() == 1 && alphabet.get(0).equals("")) {
                    System.out.println("E1: Input file is malformed");
                    System.exit(0);
                }
//                Collections.sort(alphabet);

                String initial = input.get("initial").replaceAll("\\[|\\]", "");
                if (initial.equals("")){
                    System.out.println("E2: Initial state is not defined");
                    System.exit(0);
                }
                boolean is_found_init = false;
                for (String state : states) {
                    if (initial.equals(state)) {
                        is_found_init = true;
                        break;
                    }
                }
                if (!is_found_init) {
                    System.out.println("E4: A state '" + initial + "' is not in the set of states");
                    System.exit(0);
                }

                String acceptingStr = input.get("accepting").replaceAll("\\[|\\]", "");
                List<String> accepting = Arrays.asList(acceptingStr.split(","));
                if (accepting.size() == 1 && accepting.get(0).equals("")) {
                    accepting = new ArrayList<>();
                }
                Collections.sort(accepting);

                String transitionsStr = input.get("transitions").replaceAll("\\[|\\]", "");
                List<String> transitions = Arrays.asList(transitionsStr.split(","));
                if (transitions.size() == 1 && transitions.get(0).equals("")) {
                    transitions = new ArrayList<>();
                }

                // Generate regular expression
                String validationResult = validateInput(input);
                if (!validationResult.equals("Valid")) {
                    System.out.println(validationResult);
                    return;
                }
                List<String> ss = new ArrayList<>();
                for (String state : states) {
                    if (!ss.contains(state)) {
                        ss.add(state);
                    }
                }
//                Collections.sort(ss);
                List<String> saa = new ArrayList<>();
                for (String accept : accepting) {
                    if (!saa.contains(accept)) {
                        saa.add(accept);
                    }
                }
//                Collections.sort(saa);

                String regex = generateRegExp(type, ss, alphabet, initial, saa, transitions);
                System.out.println(regex);


                // Close the scanner
                scanner.close();
            } catch (NullPointerException e) {
                System.out.printf("E1: Input file is malformed");
                System.exit(0);
            }

        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
        }

    }

    // Read input from scanner and store it in a map
    private static Map<String, String> readInput(Scanner scanner) {
        Map<String, String> input = new HashMap<>();
        int lineCount = 0; // Счетчик строк
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            lineCount++;
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                // Проверка, существует ли уже строка с таким же parts[0]
                if (input.containsKey(parts[0].trim())) {
                    return Collections.singletonMap("error", "E1: Input file is malformed");
                }
                input.put(parts[0].trim(), parts[1].trim());
            } else {
                return Collections.singletonMap("error", "E1: Input file is malformed");
            }
        }
        // Проверка, что количество входных строк равно 6
        if (lineCount != 6) {
            return Collections.singletonMap("error", "E1: Input file is malformed");
        }
        return input;
    }


    // Validate the input according to the specified rules
    private static String validateInput(Map<String, String> input) {
        if (!input.containsKey("type") || !input.containsKey("states") || !input.containsKey("alphabet")
                || !input.containsKey("initial") || !input.containsKey("accepting") || !input.containsKey("transitions")) {
            return "E1: Input file is malformed";
        }

        String type = input.get("type").replaceAll("\\[|\\]", "");
        if (!(type.equals("non-deterministic") || type.equals("deterministic"))) {
            return "E1: Input file is malformed";
        }

        String statesStr = input.get("states").replaceAll("\\[|\\]", "");
        List<String> states = Arrays.asList(statesStr.split(","));
        if (states.size() == 1 && states.get(0).equals("")) {
            return "E1: Input file is malformed";
        }
        int commas = 0;
        for (int i = 0; i < statesStr.length(); i++) {
            if (statesStr.charAt(i) == ',') {
                commas++;
            }
        }
        if (commas != states.size() - 1) {
            return "E1: Input file is malformed";
        }
        List<String> ss = new ArrayList<>();
        for (String state : states) {
            if (!ss.contains(state)) {
                ss.add(state);
            }
        }
        for (String state : states) {
            if (!containsOnly(state, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")) {
                return "E1: Input file is malformed";
            }
        }
//        Collections.sort(ss);
        states = ss;

        String alphabetStr = input.get("alphabet").replaceAll("\\[|\\]", "");
        List<String> alphabet = Arrays.asList(alphabetStr.split(","));
        if (alphabet.size() == 1 && alphabet.get(0).equals("")) {
            return "E1: Input file is malformed";
        }
        commas = 0;
        for (int i = 0; i < alphabetStr.length(); i++) {
            if (alphabetStr.charAt(i) == ',') {
                commas++;
            }
        }
        if (commas != alphabet.size() - 1) {
            return "E1: Input file is malformed";
        }
        List<String> sa = new ArrayList<>();
        for (String a : alphabet) {
            if (!sa.contains(a)) {
                sa.add(a);
            }
        }
        for (String alpha : alphabet) {
            if (!containsOnly(alpha, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_")) {
                return "E1: Input file is malformed";
            }
        }
//        Collections.sort(sa);
        alphabet = sa;

        String initialStr = input.get("initial").replaceAll("\\[|\\]", "");
        List<String> initials = Arrays.asList(initialStr.split(","));
        if (initials.size() > 1) {
            return "E1: Input file is malformed";
        }
        String initial = initials.get(0);
        if (!containsOnly(initial, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")) {
            return "E1: Input file is malformed";
        }
        if (initial.equals("")){
            return "E2: Initial state is not defined";
        }
        boolean is_found_init = false;
        for (String state : states) {
            if (initial.equals(state)) {
                is_found_init = true;
                break;
            }
        }
        if (!is_found_init) {
            return "E4: A state '" + initial + "' is not in the set of states";
        }

        String acceptingsStr = input.get("accepting").replaceAll("\\[|\\]", "");
        if (acceptingsStr.equals("")) {
            return ("E3: Set of accepting states is empty");
        }
        List<String> acceptings = Arrays.asList(acceptingsStr.split(","));
        List<String> saa = new ArrayList<>();
        for (String accept : acceptings) {
            if (!saa.contains(accept)) {
                saa.add(accept);
            }
        }
        for (String accepting : acceptings) {
            boolean is_found = false;
            for (String state : states) {
                if (accepting.equals(state)) {
                    is_found = true;
                    break;
                }
            }
            if (!is_found) {
                return "E4: A state '" + accepting + "' is not in the set of states";
            }
        }

        Collections.sort(saa);


        String transitionsStr = input.get("transitions").replaceAll("\\[|\\]", "");
        List<String> transitions = Arrays.asList(transitionsStr.split(","));
//        commas = 0;
//        for (int i = 0; i < transitionsStr.length(); i++) {
//            if (transitionsStr.charAt(i) == ',') {
//                commas++;
//            }
//        }
//        if (commas != transitions.size() - 1) {
//            return "E1: Input file is malformed";
//        }
        if (transitions.size() == 1 && transitions.get(0).equals("")) {
            return "E6: Some states are disjoint";
        }
        List<String> st = new ArrayList<>();
        for (String transition : transitions) {
            if (!st.contains(transition)) {
                st.add(transition);
            } else {
                return "E1: Input file is malformed";
            }
            String[] parts = transition.split(">");
            if (parts[0].equals("") || parts[1].equals("") || parts[2].equals("")) {
                return "E1: Input file is malformed";
            }
            if (!containsOnly(parts[0], "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")) {
                return "E1: Input file is malformed";
            }
            if (!containsOnly(parts[2], "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")) {
                return "E1: Input file is malformed";
            }
            if (!containsOnly(parts[1], "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_")) {
                return "E1: Input file is malformed";
            }
        }


        String validateStatesResult = validateStates(st, states);
        if (!validateStatesResult.equals("Valid")) {
            return validateStatesResult;
        }
        for (String transition : st) {
            String[] parts = transition.split(">");
            boolean is_found = false;
            for (String a : alphabet) {
                if (parts[1].equals(a)) {
                    is_found = true;
                    break;
                }
            }
            if (!is_found) {
                return "E5: A transition '" + parts[1] + "' is not represented in the alphabet";
            }
        }
        // Check connectivity from the initial state
//        isConnected(initial, states, st);

        // Check if any state is not reachable
        if (!isFSAConnected(st, initial)) {
            return "E6: Some states are disjoint";
        }
        if (type.equals("deterministic")) {
            for (int i=0;i<st.size();i++) {
                for (int j=0;j<st.size();j++) {
                    String[] parts1 = st.get(i).split(">");
                    String[] parts2 = st.get(j).split(">");
                    if(i!=j) {
                        if (parts1[0].equals(parts2[0]) && parts1[1].equals(parts2[1])) {
                            return "E7: FSA is non-deterministic";
                        }
                    }
                }
            }
        }
        return "Valid";
    }

    public static boolean isFSAConnected(List<String> transitions, String initialState) {
        Set<String> visitedStates = new HashSet<>();

        dfs(initialState, transitions, visitedStates);

        // Check if all states are visited
        return visitedStates.size() == countDistinctStates(transitions);
    }

    private static void dfs(String state, List<String> transitions, Set<String> visitedStates) {
        visitedStates.add(state);
        for (String transition : transitions) {
            String[] parts = transition.split(">");
            if (parts[0].equals(state) && !visitedStates.contains(parts[2])) {
                dfs(parts[2], transitions, visitedStates);
            }
        }
    }

    private static int countDistinctStates(List<String> transitions) {
        Set<String> states = new HashSet<>();
        for (String transition : transitions) {
            String[] parts = transition.split(">");
            states.add(parts[0]);
            states.add(parts[2]);
        }
        return states.size();
    }


    private static boolean isConnected(String initialState, List<String> states, List<String> transitions) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(initialState);
        visited.add(initialState);

        while (!queue.isEmpty()) {
            String currentState = queue.poll();
            visited.add(currentState);

            for (String transition : transitions) {
                String[] parts = transition.split(">");
                if (parts[0].trim().equals(currentState)) {
                    String nextState = parts[2].trim();
                    if (!visited.contains(nextState)) {
                        queue.offer(nextState);
                    }
                }
            }
        }

        return visited.size() == states.size();
    }




    private static String validateStates(List<String> transitions, List<String> states) {
        for (String transition : transitions) {
            String[] parts = transition.split(">");
            String from = parts[0].trim();
            String to = parts[2].trim();
            boolean is_found_from = false, is_found_to = false;
            for (String state : states) {
                if (from.equals(state)) {
                    is_found_from = true;
                }
                if (to.equals(state)) {
                    is_found_to = true;
                }
            }
            if (!is_found_from) {
                return "E4: A state '" + from + "' is not in the set of states";
            }
            if (!is_found_to) {
                return "E4: A state '" + to + "' is not in the set of states";
            }

        }
        return "Valid";
    }

    private static boolean containsOnly(String str, String validChars) {
        for (int i = 0; i < str.length(); i++) {
            if (validChars.indexOf(str.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }


    private static String generateRegExp(String type, List<String> states, List<String> alphabet,
                                         String initial, List<String> accepting, List<String> transitions) {
        int n = states.size();
        String[][][] regexMatrix = new String[n][n][n + 1];
        Map<String, String> elements = new HashMap<>();

        // Step 1: Create an adjacency matrix
        List<String>[][] adjacencyMatrix = new List[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adjacencyMatrix[i][j] = new ArrayList<>();
            }
        }

        // Step 2: Populate adjacency matrix with transitions
        for (String transition : transitions) {
            String[] parts = transition.trim().split(">");
            int i = states.indexOf(parts[0].trim());
            int j = states.indexOf(parts[2].trim());
            String a = parts[1].trim();
            adjacencyMatrix[i][j].add(a);
        }

        // Step 3: Construct regular expressions
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                StringBuilder regex = new StringBuilder();
                boolean hasConnection = false; // Flag to check if there are connections

                // Add transitions to the regex
                for (String element : adjacencyMatrix[i][j]) {
                    regex.append(element).append("|");
                    hasConnection = true;
                }

                // If there are no connections, use {}
                if (!hasConnection) {
                    regex.append("{}");
                } else {
                    regex.deleteCharAt(regex.length() - 1); // Remove the last '|'
                }

                // If i == j, add eps unless it's already in the regex
                if (i == j && !regex.toString().contains("eps")) {
                    if (hasConnection) {
                        regex.append("|eps");
                    } else {
                        regex = new StringBuilder("eps");
                    }
                }

                regexMatrix[i][j][0] = "(" + regex.toString() + ")";
                elements.put("R" + i + j, "(" + regex.toString() + ")");
            }
        }


        // Step 4: Floyd-Warshall algorithm for regular expression computation
        for (int k = 0; k < n; k++) {
            String[][] buff = new String[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    String left = regexMatrix[i][k][k];
                    String right = regexMatrix[k][j][k];
                    String middle = regexMatrix[k][k][k];
                    String after = regexMatrix[i][j][k];
                    buff[i][j] = "(" + left + middle + "*" + right + "|" + after + ")";
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    regexMatrix[i][j][k + 1] = buff[i][j];
                }
            }
        }

        // Step 5: Extract final regular expression for accepting states
        StringBuilder regexBuilder = new StringBuilder();
        for (String acceptState : accepting) {
            int i = states.indexOf(acceptState);
            int j = states.indexOf(initial);
            regexBuilder.append(regexMatrix[j][i][n]).append("|");
        }

        return regexBuilder.toString().replaceAll("\\|$", "");
    }



}