package newpackage;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Set;
import java.util.Collections;

public class Thompson{
    
    //ArrayList to insert the symbol input that used in the regular expression
    public static ArrayList <Character>  inputSymbol= new ArrayList <>();
    
    /*
        Trans - object is used as a tuple of 3 items to depict transitions
            (state from, symbol of tranistion path, state to)
    */
    public static class Trans{
        public int state_from, state_to;
        public char trans_symbol;

        public Trans(int v1, int v2, char sym){
            this.state_from = v1;
            this.state_to = v2;
            this.trans_symbol = sym;
        }//end constructor()
    }//end class Trans

    
    /*
        NFA - serves as the graph that represents the Non-Deterministic
            Finite Automata. Will use this to better combine the states.
    */
    public static class NFA{
        public ArrayList <Integer> states;
        public ArrayList <Trans> transitions;
        public int final_state;
        
        public NFA(){
            this.states = new ArrayList <Integer> ();
            this.transitions = new ArrayList <Trans> ();
            this.final_state = 0;
        }//end constructor()
        public NFA(int size){
            this.states = new ArrayList <Integer> ();
            this.transitions = new ArrayList <Trans> ();
            this.final_state = 0;
            this.setStateSize(size);
        }//end constructor()
        public NFA(char c){
            this.states = new ArrayList<Integer> ();
            this.transitions = new ArrayList <Trans> ();
            this.setStateSize(2);
            this.final_state = 1;
            this.transitions.add(new Trans(0, 1, c));
        }//end constructor()

        public void setStateSize(int size){
            for (int i = 0; i < size; i++)
                this.states.add(i);
        }//end method()

        public int getFinalState(){
            return this.final_state;
        }//end method()
        
        public void display(){
            for (Trans t: transitions){
                System.out.println("("+ t.state_from +", "+ t.trans_symbol +
                    ", "+ t.state_to +")");
            }//end for loop     
        }//end method()
        
        public void printState(){
            for(int s : states){
                System.out.print(s+",");
            }//end for loop
        }//end method()
    }//end NFA class 

    
     /*
        DFA - serves as the graph that represents the Deterministic
            Finite Automata. Will use this to better combine the states.
    */
     public static class DFA{
        public ArrayList <Trans> transitions;
        public int final_state;
        
        public DFA(){
            this.transitions = new ArrayList <Trans> ();
            this.final_state = 0;
        }//end construtor()
        public DFA(int size){
            this.transitions = new ArrayList <Trans> ();
            this.final_state = 0;
        }//end construtor()
        public DFA(char c){
           this.transitions = new ArrayList <Trans> ();
            this.final_state = 1;
            this.transitions.add(new Trans(0, 1, c));
        }//end construtor()

        public void display(){
            for (Trans t: transitions){
                System.out.println("(S"+ t.state_from +", "+ t.trans_symbol +
                    ", S"+ t.state_to +")");
            }//end for loop    
        }//end method()
    }//end DFA class 
   
   //  ---------------------------------------------------------End Classes----------------------------------------------------------------
     
     
    /*
        star() - Highest Precedence regular expression operator. Thompson
            algoritm for kleene star.
    */
    public static NFA star(NFA n){
        NFA result = new NFA(n.states.size()+2);
        result.transitions.add(new Trans(0, 1, 'E')); // new trans for q0
        
        // we add the first state from 0 to 1 then will copy all the transaction of the NFA agian after the first state 
        // copy existing transisitons
        for (Trans t: n.transitions){
            result.transitions.add(new Trans(t.state_from + 1,
            t.state_to + 1, t.trans_symbol));
        }//end for loop 
        
        
        // add empty transition from final n state to new final state.
        result.transitions.add(new Trans(n.states.size(),  n.states.size() + 1, 'E'));
        
        // Loop back from last state of n to initial state of n.
        result.transitions.add(new Trans(n.states.size(), 1, 'E'));

        // Add empty transition from new initial state to new final state.
        result.transitions.add(new Trans(0, n.states.size() + 1, 'E'));

        result.final_state = n.states.size() + 1;
        return result;
    }//end star() method 
    

    /*
        concatenation() - Thompson algorithm for concatenation. Middle Precedence.
    */
    public static NFA concatenation(NFA n, NFA m){
        ///*
        m.states.remove(0); // delete m's initial state

        // copy NFA m's transitions to n, and handles connecting n & m
        for (Trans t: m.transitions){
            n.transitions.add(new Trans(t.state_from + n.states.size()-1,
                t.state_to + n.states.size() - 1, t.trans_symbol));
        }

        // take m and combine to n after erasing inital m state
        for (Integer s: m.states){
            n.states.add(s + n.states.size() + 1);
        }//end for loop
        
        n.final_state = n.states.size() + m.states.size() - 2;
        return n;
    }//end concatenation() method
    
    /*
        union() - Lowest Precedence regular expression operator. Thompson
            algorithm for union (or). 
    */
    public static NFA union(NFA n, NFA m){
        NFA result = new NFA(n.states.size() + m.states.size() + 2);

        // the branching of q0 to beginning of n
        result.transitions.add(new Trans(0, 1, 'E'));
        
        // copy existing transisitons of n
        for (Trans t: n.transitions){
            result.transitions.add(new Trans(t.state_from + 1,
            t.state_to + 1, t.trans_symbol));
        }
        
        // transition from last n to final state
        result.transitions.add(new Trans(n.states.size(),
            n.states.size() + m.states.size() + 1, 'E'));

        // the branching of q0 to beginning of m
        result.transitions.add(new Trans(0, n.states.size() + 1, 'E'));

        // copy existing transisitons of m
        for (Trans t: m.transitions){
            result.transitions.add(new Trans(t.state_from + n.states.size()
                + 1, t.state_to + n.states.size() + 1, t.trans_symbol));
        }
        
        // transition from last m to final state
        result.transitions.add(new Trans(m.states.size() + n.states.size(),
            n.states.size() + m.states.size() + 1, 'E'));
       
        // 2 new states and shifted m to avoid repetition of last n & 1st m
        result.final_state = n.states.size() + m.states.size() + 1;
        return result;
    }//end union() method 



    // simplify the repeated boolean condition checks
    public static boolean alpha(char c){
        if(c >= 'a' && c <= 'z' == true){
            if(inputSymbol.isEmpty())
            {
                inputSymbol.add(c);
                return true;
            }
            else{
                boolean flag=true;
                for(int i=0; i< inputSymbol.size();++i){
                    if(c==inputSymbol.get(i))
                        flag=false;
            }//end for loop
                if(flag)
                    inputSymbol.add(c);
             return true;
            }
        }
        return false;
    }//end alpha() method
    
    public static boolean alphabet(char c){
        return alpha(c) || c == 'E';
    }//end alphabet() method
    
    public static boolean regexOperator(char c){
        return c == '(' || c == ')' || c == '*' || c == '|';
    }//end regexOperator() method
    
    public static boolean validRegExChar(char c){
        return alphabet(c) || regexOperator(c);
    }//end validRegExChar() method 
    
    // validRegEx() - checks if given string is a valid regular expression.
    public static boolean validRegEx(String regex){
        if (regex.isEmpty())
            return false;
        for (char c: regex.toCharArray())
            if (!validRegExChar(c))
                return false;
        return true;
    }//end validRegEx() method 

    
    /*
        thompsonNFA() - compile given regualr expression into a NFA using 
            Thompson Construction Algorithm. Will implement typical compiler
            stack model to simplify processing the string. This gives 
            descending precedence to characters on the right.
    */
    public static NFA thompsonNFA(String regex){
        //check if the string is valid or not
        if (!validRegEx(regex)){
            System.out.println("Invalid Regular Expression Input.");
            return new NFA(); // empty NFA if invalid regex
        }//end if statement 
        
        Stack <Character> operators = new Stack <Character> ();  //push the operator like *, |
        Stack <NFA> operands = new Stack <NFA> ();   //push the NFA alphabatic 
        Stack <NFA> concat_stack = new Stack <NFA> ();
        
        boolean ccflag = false; // concat flag
        char op, c; // current character of string
        int para_count = 0;
        NFA nfa1, nfa2;

        for (int i = 0; i < regex.length(); i++){
            c = regex.charAt(i);
            if (alphabet(c)){
                operands.push(new NFA(c));
                if (ccflag){ // concat this w/ previous
                    operators.push('.'); // '.' used to represent concat.
                }//end inner if 
                else
                    ccflag = true;
            }//end first if statement 
            else{
                if (c == ')'){
                    ccflag = false;
                    if (para_count == 0){
                        System.out.println("Error: More end paranthesis "+
                            "than beginning paranthesis");
                        System.exit(1);
                    }
                    else{ para_count--;}
                    // process stuff on stack till '('
                    while (!operators.empty() && operators.peek() != '('){
                        op = operators.pop();
                        if (op == '.'){
                            nfa2 = operands.pop();
                            nfa1 = operands.pop();
                            operands.push(concatenation(nfa1, nfa2));
                        }//end inner if statement 
                        else if (op == '|'){
                            nfa2 = operands.pop();
                            
                            if(!operators.empty() && operators.peek() == '.'){
                                
                                concat_stack.push(operands.pop());
                                while (!operators.empty() &&  operators.peek() == '.'){
                                    
                                    concat_stack.push(operands.pop());
                                    operators.pop();
                                }//end while loop 
                                nfa1 = concatenation(concat_stack.pop(), concat_stack.pop());
                                while (concat_stack.size() > 0){
                                   nfa1 =  concatenation(nfa1, concat_stack.pop());
                                }//end while loop 
                            }//end if 
                            else{
                                nfa1 = operands.pop();
                            }//end inner else 
                            operands.push(union(nfa1, nfa2));
                        }//end else
                    }//end while loop
                }//end big if 
                else if (c == '*'){
                    operands.push(star(operands.pop()));
                    ccflag = true;
                }//end else 
                else if (c == '('){ // if any other operator: push
                    operators.push(c);
                    para_count++;
                }//end else 
                else if (c == '|'){
                    operators.push(c);
                    ccflag = false;
                }//end else 
            }//end big else 
        }//end for loop 
        
        while (operators.size() > 0){
            if (operands.empty()){
                System.out.println("Error: imbalanace in operands and "
                + "operators");
                System.exit(1);
            }
            op = operators.pop();
            if (op == '.'){
                nfa2 = operands.pop();
                nfa1 = operands.pop();
                operands.push(concatenation(nfa1, nfa2));
            }
            else if (op == '|'){
                nfa2 = operands.pop();
                if( !operators.empty() && operators.peek() == '.'){
                    concat_stack.push(operands.pop());
                    while (!operators.empty() && operators.peek() == '.'){
                        concat_stack.push(operands.pop());
                        operators.pop();
                    }
                    nfa1 = concatenation(concat_stack.pop(),
                        concat_stack.pop());
                    while (concat_stack.size() > 0){
                       nfa1 =  concatenation(nfa1, concat_stack.pop());
                    }
                }
                else{
                    nfa1 = operands.pop();
                }
                operands.push(union(nfa1, nfa2));
            }
        }//end while loop 
        return operands.pop();
    }//end thompsonNFA()method
    
    
    /*
    convertNFAtoDFA(): to convert NFA to DFA in subset construction algorithm
    */
    
    public static DFA convertNFAtoDFA(NFA nfa){
        DFA dfa = new DFA();
        ArrayList<Integer> mainstate= new ArrayList<Integer>();  //insert the main state that will get from them the submain state
        HashMap<Integer, ArrayList<Integer>> DS=new HashMap<>(); //DS set: to insert the new set like S0
        HashMap<Integer, ArrayList<Integer>> tempDS=new HashMap<>(); // temprory DS
        
        //1- call method computestate()- return a stack with the number of state of e-closure
        Stack<Integer> stack = computeState(nfa,0);
        for(int s: stack){
            mainstate.add(s);
        }//end for loop 
        
        //2- put first state in DS
        DS.put(0, mainstate);
        tempDS.put(0, mainstate);
        
        //3- Start the while loop 
        int stateCounter=0; //state counter
        int currentStateCounter=0; //loop count for removing the state
        while(!DS.isEmpty()){
            mainstate= (ArrayList<Integer>) DS.remove(currentStateCounter).clone();
             //mark State
            
//            System.out.println("counter: "+stateCounter);
//            System.out.println("Input symbol: "+inputSymbol);
//            System.out.println("count: "+currentStateCounter);
//            System.out.println("cuurent compared state: "+mainstate);
            
            //start loop to move the state to their spesific input symbol
            for(int i=0; i<inputSymbol.size();++i){
                
                 ArrayList<Integer> temparray= new ArrayList<Integer>();  //define a temprory array to insert the state and compare it
                 
               //send to method closureForInputSymbol() input syamol and the state array to compute the state for the input symbol
               Stack<Integer> symbolstack = closureForInputSymbol(nfa, mainstate, inputSymbol.get(i));
               
               //check if the symbolstack is null or not: if null then no state in the input symbol
               if(symbolstack == null){
                   continue;
               }//end if statement 
               
               //else: compute the state of the input symbol
               for(int s: symbolstack){
                   //send to method computeState() the state of the input to find the e-closure state for it
                   Stack<Integer> tumpstack= computeState(nfa, s);
                   ArrayList<Integer> tempstack= new ArrayList<Integer>();
                   //merge
                   while(!tumpstack.empty()){
                       tempstack.add(tumpstack.pop());
                   }//end inner for loop
                   temparray= merge(temparray,tempstack);
                  // System.out.println("print tumparray after merging: "+temparray);
               }//end for loop
          
                //call mehtod checkDSset(): To check if temparray is a new state or in the DS
                int newstate= checkDSset(tempDS, temparray);
                if(newstate== -1){
                    int index= stateCounter+1; //new state index
                    //add in the DS set
                    DS.put(index, temparray);
                    tempDS.put(index, temparray);
                    //mainstate= (ArrayList<Integer>) temparray.clone();
                   // System.out.println("print mainstate "+mainstate);
                    
                    //Add the transition: move state to another state
                    Trans t= new Trans(currentStateCounter,index,inputSymbol.get(i));
                    dfa.transitions.add(t);
                    
                    //increment the state Conuter
                    stateCounter++;
                }//end if statement
                else{
                    //Add the transition: move state to another state 
                    Trans t= new Trans(currentStateCounter,newstate,inputSymbol.get(i));
                    dfa.transitions.add(t);
                }//end else statement
            }//end for loop 
            currentStateCounter++;
        }//end end while loop
        return dfa;
    }//end convertNFAtoDFA() method
    
    public static ArrayList<Integer> merge(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        Set<Integer> set = new HashSet<Integer>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<Integer>(set);
    }//end merge() method
    
    /*
    checkDSset(): check in the state of the input state in the DS set or not 
    */
    public static int checkDSset(HashMap<Integer, ArrayList<Integer>> DS , ArrayList<Integer> statearray){
        Collections.sort(statearray);  //sort the array elements
        //System.out.println("sorted array comparing: "+statearray);
        boolean flag=false;
        int i;
        for( i=0 ;i<DS.size();++i){
            ArrayList<Integer> temp= DS.get(i);
            Collections.sort(temp);
           // System.out.println("DS array sorted: "+temp);
            if(temp.size()== statearray.size()){
                for(int j=0; j<temp.size(); ++j){
                   // System.out.println("get compared array"+statearray.get(j));
                    //System.out.println("get compared array"+temp.get(j));
                      if(temp.get(j)!=statearray.get(j)){
                            flag=false;
                            break;
                        }//end if statement 
                      else
                          flag=true;
                }//end inner for loop 
                if (flag==true){
                     // System.out.println("print result the check method true: "+i);
                           return i;
                }//end if statement
            }//big if statement
            else
                flag=false;
        }//end for loop
        if(flag==false){
                   // System.out.println("print result the check method: false ");
                    return -1;
        }
        //System.out.println("print result the check method true out of loop: "+i);
        return i;
    }//end checkDSset() method
    
    /*
    closureForInputSymbol(): insert the state for the input symbol
    */
    public static Stack<Integer> closureForInputSymbol(NFA nfa, ArrayList<Integer> state, char symbol){
        Stack<Integer> stack= new Stack<Integer>();  //define a stack
        
        //start loop to find the state for the spesific input symbol
        for(int i=0; i<state.size();++i){
            int tump= state.get(i);
            for(int j=0; j< nfa.transitions.size(); ++j){
                if(nfa.transitions.get(j).state_from==tump && nfa.transitions.get(j).trans_symbol==symbol){
                    stack.push(nfa.transitions.get(j).state_to);
                }//end if statement
            }//end inner for loop 
        }//end for loop
        
        if(stack.empty()){
         //   System.out.println("null");
            return null;
        }
        
       // System.out.println("Print the closure stack if not null:");
//        Stack<Integer> r= new Stack<Integer>();
//        while(!stack.empty()){
//           r.push(stack.pop());
//         }
//        while(!r.empty()){
//            int t= r.pop();
//            stack.push(t);
//            System.out.print(t+"-");
//        }
//        System.out.println("");
        return stack;
    }//end closureForInputSymbol() method
    
    /*
    computeState(): insert the e-closure of the specisic state and then return a stack of the state
    */
    public static Stack<Integer> computeState(NFA nfa,int state){
        Stack<Integer> tranStack = new Stack(); //inside the stack will bw the state 
        Stack<Integer> tump = new Stack();
        

        tump.push(state);
        tranStack.push(state);
        
        
        while( !tump.empty()){
            int from = tump.pop();
            
            for (int i=0; i< nfa.transitions.size() ;++i){
                if(nfa.transitions.get(i).state_from ==from && nfa.transitions.get(i).trans_symbol == 'E'){
                    tranStack.push(nfa.transitions.get(i).state_to);
                    tump.push(nfa.transitions.get(i).state_to);
                }//end if statement
            }//end for loop
        }//end while loop
        
       // System.out.println("Print the compute state stack:");
//        Stack<Integer> r= new Stack<Integer>();
//        while(!tranStack.empty()){
//           r.push(tranStack.pop());
//         }
//        while(!r.empty()){
//            int t= r.pop();
//            tranStack.push(t);
//            System.out.print(t+"-");
//        }
//        System.out.println("");
        
        return tranStack;
    }//end computeState() method
    
    
    //-----------------------------------------------END METHOD------------------------------------------------------------------------
    

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);  //define scanner
        String line; //define the string that will for it the regular expression
        
        System.out.println("Enter a regular expression or Enter :q to Quit: ");
        
        //start while loop for reading the regular expression
        while(sc.hasNextLine()){
//            System.out.println("Enter a regular expression with the " +
//                "alphabet ['a','z'] & E for empty "+"\n* for Star" + 
//                "\nelements with nothing between them indicates " +
//                "concatenation "+ "\n| for Union \n\":q\" to quit");
            
            line = sc.nextLine();  //user insert
            
            //check if the user insert quit or not
            if (line.equals(":q") || line.equals("QUIT"))
                break;
            
            //call the thomson method to compute the NFA 
            NFA nfa_of_input = thompsonNFA(line);
            System.out.println("\nConverting A Regular Expression into A NFA (Thomson’s Construction):");
            
            // //call to convert NFA to DFA
            DFA dfa_of_input = convertNFAtoDFA(nfa_of_input);
            
            //Display NFA and DFA 
            //System.out.println("NFA");
            nfa_of_input.display();
            
            System.out.println("Converting a NFA into a DFA (subset construction):");
            dfa_of_input.display();   
        }//end while loop
    }//end main 
}//end class 