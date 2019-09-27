package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
	
	private static boolean isOp(String s) {
		for(int i=0; i<s.length();i++) 
		{
			if(s.charAt(i)=='*' || s.charAt(i)=='+' || 
			   s.charAt(i)=='-' || s.charAt(i)=='/')
			{
				return true;
			}
		}
		return false;
	}
	private static boolean isLetter(String s) {
		for(int i=0; i<s.length();i++) {
			if(Character.isDigit(s.charAt(i))==true)
			{
				return false;
			}
			if(s.charAt(i)==' ' || s.charAt(i)=='\t' ||s.charAt(i)=='*' || s.charAt(i)=='+' || s.charAt(i)=='-' || s.charAt(i)=='/' || s.charAt(i)=='('
					|| s.charAt(i)==')' || s.charAt(i)=='[' || s.charAt(i)==']') {
				return false;
			}
		}
		return true;
	}	
	private static boolean isNumber(String s) {
		for(int i=0; i<s.length();i++) {
			if(Character.isDigit(s.charAt(i))==false)
			{
				return false;
			}
			if(s.charAt(i)==' ' || s.charAt(i)=='\t' ||s.charAt(i)=='*' || s.charAt(i)=='+' || s.charAt(i)=='-' || s.charAt(i)=='/' || s.charAt(i)=='('
					|| s.charAt(i)==')' || s.charAt(i)=='[' || s.charAt(i)==']') {
				return false;
			}
		}
		return true;
	}	
	private static void print (ArrayList<Variable> vars, ArrayList<Array> arrays) {
		System.out.println(vars.toString());
		System.out.println(arrays.toString());
	}
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
	
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	StringTokenizer str=new StringTokenizer(expr, delims, true);
    	String c=null;
    	Array y=null;
    	Variable x=null;
    	while(str.hasMoreTokens()) {
    		String a=str.nextToken();
    		if(a.equals("[")) {
    			y=new Array(c);
    			if(arrays.contains(y)){
    				continue;
    			}
    			arrays.add(y);
    			vars.remove(x);
    		}
    		if(isLetter(a)==true) {
    			x =new Variable(a); 
    			if(vars.contains(x)) {
    				continue;
    			}
    			vars.add(x);
    		}
    		c=a;
    			}
    	print(vars, arrays);
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * @param <T>
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    private static void reverse(Stack<String> O, Stack<Float> V){
    	Stack<String> tempO= new Stack<String>();
    	Stack<String> tempO2= new Stack<String>();
    	Stack<Float> tempV= new Stack<Float>();
    	Stack<Float> tempV2= new Stack<Float>();
    	while(O.isEmpty()==false) {
    		tempO.push(O.pop());
    	}
    	while(tempO.isEmpty()==false) {
    		tempO2.push(tempO.pop());
    	}
    	while(tempO2.isEmpty()==false) {
    		O.push(tempO2.pop());
    	}
    	while(V.isEmpty()==false) {
    		tempV.push(V.pop());
    	}
    	while(tempV.isEmpty()==false) {
    		tempV2.push(tempV.pop());
    	}
    	while(tempV2.isEmpty()==false) {
    		V.push(tempV2.pop());
    	}
    }
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	Stack<String> o=new Stack<String>(), O=new Stack<String>(); // Stack for Operators
    	Stack<Float> values= new Stack<Float>(), newVal= new Stack<Float>(); //Stack for Values
		Stack<Character> bracket= new Stack<Character>(), P= new Stack<Character>();
		ArrayList<Float> r= new ArrayList<Float>();
    	float res=0;
    	int count=0;
    	int p=0;
    	for(int B=0; B<expr.length();B++)
    	{
    		if(expr.charAt(B)=='[')
    		{
    			for(int b=B+1;b<expr.length();b++) 
    			{
    				if(expr.charAt(b)=='[') 
    				{
    					bracket.push(expr.charAt(b));
    				}
    				if(expr.charAt(b)==']' && bracket.isEmpty()==true)
    				{
    					String expr1=expr.substring(B+1, b);
    	    			res = evaluate(expr1, vars, arrays);
    	    			//R.add(res);
    	    			int arrVal=(int)res;
    	    			expr=expr.replace(expr.substring(B, b+1), " " + String.valueOf(arrVal));
    					break;
    				}
    				if(expr.charAt(b)==']') 
    				{
    					bracket.pop();
    				}
    				
    			}
    		}
    	}
    	for(int j=0; j<expr.length();j++)
    	{
    		if(expr.charAt(j)=='(')
    		{
    			for(int k=j+1;k<expr.length();k++) 
    			{
    				if(expr.charAt(k)=='(') 
    				{
    					P.push(expr.charAt(k));
    				}
    				if(expr.charAt(k)==')' && P.isEmpty()==true)
    				{
    					String expr1=expr.substring(j+1, k);
    	    			res = evaluate(expr1, vars, arrays);
    	    			r.add(res);
    	    			expr=expr.replace(expr.substring(j, k+1), "@" );
    					break;
    				}
    				if(expr.charAt(k)==')')
    				{
    					P.pop();
    				}
    				
    			}
    		}
    	}
    	if(expr.equals("@"))
    	{
    		return r.get(p);
    	}
  
    	if(isNumber(expr))
    	{
    		return Float.valueOf(expr);
    	}
    	if(isLetter(expr))
    	{
    		return vars.get(0).value;
    	}
    	StringTokenizer str= new StringTokenizer(expr, delims, true);
    	while(str.hasMoreTokens())
    	{
    		String a=str.nextToken();
    		if(a.equals("@"))
    		{
    			values.push(r.get(p));
    			if(r.size()>1) 
    			{
    				p++;
    			}
    		}
    		if(isNumber(a))  // If the token is a Number
    		{
    			values.push(Float.valueOf(a));
    		}
    		else if(isLetter(a)) // If the token is a Letter 
    		{
    			for(int N=0;N<arrays.size();N++)
    			{
    				if(a.equals(arrays.get(N).name)==true) 
    				{
    					Array x =arrays.get(N);
    					str.nextToken();
    					String c=str.nextToken();
    					values.push((float)x.values[Integer.valueOf(c)]);
    				}
    			}
    			for(int i=0;i<vars.size();i++) 
    			{
    				if(a.equals(vars.get(i).name)==true)
    			{
    					values.push((float)vars.get(i).value);
    				}
    			}
    		} 
    		else if(isOp(a)) //If the token is an operator
    		{
    				if(a.equals("*") || a.equals("/"))
    				{
    					count++;
    				}
    				o.push(a);
    		}
    	}
    	reverse(o, values);
    	while(count>0)
    	{
    		if(o.peek().equals("*") || o.peek().equals("/")) 
    		{
    			float S=values.pop();
        		float F=values.pop();
            	switch(o.pop())
            	{
            	case "*": 
            		values.push(F*S);
            		break;
            	case "/": 
            		values.push(S/F);
            		break;
    		}
            	count--;
    		}
    		else 
    		{
    			O.push(o.pop());
    			newVal.push(values.pop());
    		}
    	}
    	while(O.isEmpty()==false) {
    	o.push(O.pop());
    	}
    	while(newVal.isEmpty()==false) {
    		values.push(newVal.pop());
    	}
    	while(o.isEmpty()==false) {
    		float S=values.pop();
    		float F=values.pop();
        	switch(o.pop())
        	{
        	case "+": 
        		values.push(F+S);
        		break;
        	case "-":
        		values.push(S-F);        		
        		break;
        	}
    	}
    	// following line just a placeholder for compilation
    	return values.pop();
    }
}
