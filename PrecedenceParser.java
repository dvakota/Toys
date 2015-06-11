import java.lang.reflect.Field;
import java.util.*;

/**
 * /r/DailyProgrammer Challenge #196 - Precedence Parser
 * Can process parenthesized, binary and unary operators,
 * post- and pre-increments and decrements
 * Precedence and arity can be defined in addition to
 * the operator grammar and associativity rules
 *
 * Date: 1/10/15
 */
public class PrecedenceParser {

    static class OperatorGrammar {
        private Map<String, Operator> operators;

        private OperatorGrammar() {
            operators = new HashMap<String, Operator>();
        }

        public OperatorGrammar(String name, Collection<String> definitions, char delimiter) {
            this();
            int maxPrecedence = definitions.size(); int i = 0;
            for (String s : definitions) {
                String[] parts = s.split(delimiter + "");
                if (parts.length >= 2) {
                    String symbol = parts[0].trim();
                    boolean right = parts[1].contains("right");
                    int precedence = maxPrecedence - i++; int arity = 2;
                    if (parts.length == 3) precedence = Integer.parseInt(parts[2].trim());
                    if (parts.length == 4) arity = Integer.parseInt(parts[3].trim());
                    Operator op = new Operator(symbol, right, precedence, arity);
                    operators.put(symbol, op);
                }
            }
        }

        public Operator get(String symbol) {
            if (operators.containsKey(symbol)) return operators.get(symbol);
            return null;
        }
    }

    static class Operator {
        boolean isRightAssociative;
        String symbol;
        int precedence;
        int arity;

        public Operator(String sym, boolean rightAssoc, int prec) {
            this(sym, rightAssoc);
            precedence = prec;
        }

        public Operator(String sym, boolean rightAssoc, int prec, int arity) {
            this(sym, rightAssoc, prec);
            this.arity = arity;
        }

        public Operator(String sym, boolean rightAssoc) {
            symbol = sym;
            isRightAssociative = rightAssoc;
            precedence = 0;
            arity = 2;
        }


        public int comparePrecedence(Operator o) {
            if (precedence < o.precedence) return -1;
            if (precedence > o.precedence) return 1;
            return 0;
        }
    }

    static class ASTNode {
        String value;
        ASTNode left;
        ASTNode right;
        private String string;

        public ASTNode(String val, ASTNode leftNode, ASTNode rightNode) {
            value = val;
            left = leftNode;
            right = rightNode;
        }

        @Override
        public String toString() {
            if (string == null) {
                string = "";
                inOrder(this);
            }
            return string + "\n";
        }

        private void inOrder(ASTNode node) {
            if (node == null) return;
            //non-leaf node == operator
            if (node.left != null || node.right != null) {
                string += "(";
            }
            inOrder(node.left);
            string += node.value;
            inOrder(node.right);
            if (node.left != null || node.right != null) {
                string += ")";
            }
        }

    }

    OperatorGrammar grammar;
    private static PrecedenceParser instance;

    /**
     * Creates a default instance of the Parser
     * @param definitions Newline-separated text definitions of the grammar
     *                    in the following format:
     * <operator_symbol>:<associativity>:{<precedence> optional}:{<arity> optional}
     */
    private PrecedenceParser(String definitions) {
        this("DEFAULT", definitions, ':');
    }

    /**
     * Creates a new instance of the Parser
     *
     * @param name name of the grammar used
     * @param definitions Newline-separated text definitions of the grammar rules
     * @param delimiter  character separating rule tokens
     */

    private PrecedenceParser(String name, String definitions, char delimiter) {
        grammar = generateGrammar(name, definitions, delimiter);
    }

    public static PrecedenceParser instance(String grammar) {
        if (instance == null) {
            instance = new PrecedenceParser("DEFAULT", grammar, ':');
        }
        return instance;
    }

    private OperatorGrammar generateGrammar(String name, String source, char delimiter) {
        String[] strings = source.split("\n");
        return new OperatorGrammar(name, Arrays.asList(strings), delimiter);
    }

    /**
     * Generates new grammar rules for the existing Parser instance
     *
     * @param name
     * @param definitions
     * @param delimiter
     */
    public void newGrammar(String name, String definitions, char delimiter) {
        grammar = generateGrammar(name, definitions, delimiter);
    }

    public void newGrammar(String definitions) {
        newGrammar("DEFAULT", definitions, ':');
    }

    private Stack<ASTNode> addNode(Stack<ASTNode> operandStack, String operator) {
        Operator op = grammar.get(operator);
        if (op == null || op.arity > operandStack.size())
            throw new IllegalStateException("Malformed expression");
        ASTNode left, right;
        right = null;
        if (op.arity == 2) {
            right = operandStack.pop();
        }
        left = operandStack.pop();

        ASTNode newNode = new ASTNode(operator, left, right);
        operandStack.push(newNode);
        return operandStack;
    }

    /**
     * Generates an Abstract Syntax Tree from the provided expression
     * @param expression
     * @return
     */
    public ASTNode parseToAST(String expression) {

        Stack<String> operators = new Stack<String>();
        Stack<ASTNode> nodes = new Stack<ASTNode>();

        String[] expr = tokenize(expression);

        for (String token : expr) {
            if (token.equals("(")) {
                operators.push(token);
                continue;
            }
            if (token.equals(")")) {
                String popped;
                while (!operators.isEmpty() && !(popped = operators.pop()).equals("(")) {
                    addNode(nodes, popped);
                }
                continue;
            }

            Operator op1 = grammar.get(token);
            //token is an operator
            if (op1 != null) {
                Operator op2;
                wh:while (!operators.isEmpty() && (op2 = grammar.get(operators.peek())) != null) {
                    if ((op1.comparePrecedence(op2) <= 0 && !op1.isRightAssociative)
                                    || (op1.comparePrecedence(op2) < 0 && op1.isRightAssociative)) {
                        operators.pop();
                        addNode(nodes, op2.symbol);
                    } else break wh;
                }
                operators.push(token);
                //token is an operand
            } else {
                nodes.push(new ASTNode(token, null, null));
            }
        }
        while (!operators.isEmpty()) {
            addNode(nodes, operators.pop());
        }

        return nodes.pop();
    }

    private  String[] tokenize(String expression) {
        String result = "";
        while (expression.length() > 0) {
            String token = nextToken(expression);
            expression = expression.substring(token.length());
            result += token + ' ';
        }
        return result.split("\\s+");
    }

    private String nextToken(String expr) {
        String result = "";
        char last = expr.charAt(expr.length() - 1);
        int i = 0;
        char c = expr.charAt(i);
        if (c == '(' || c == ')') return c + "";
        Operator op;
        while ((op = grammar.get(c + "")) == null && i < expr.length()
                           && !"()".contains(c + "")) {
            result += c;  i++;
            if (i < expr.length()) c = expr.charAt(i);
        }
        if (result.length() > 0) return result;
        while ((op = grammar.get(result.trim() + c)) != null && i < expr.length()) {
            result += c; i++;
            if (i < expr.length()) c = expr.charAt(i);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String caption1 = "Grammar rules:";
        String caption2 = "Parsing expression ";

        //Example1: A grammar with unary operators and arbitrary precedence rules
        String grammar1 =  "+:left:0\n" +
                                       "-:left:0\n" +
                                       "*:left:1\n" +
                                       "/:left:1\n" +
                                       "%:left:1\n" +
                                       "^:right:2\n" +
                                       "!:right:4:1\n" +  //negation
                                       "++:right:3:1\n" + //pre-increment
                                       "++:left:4:1\n" +  //post-increment
                                       "--:right:3:1\n" + //pre-decrement
                                       "--:left:4:1\n";   //pre-decrement

        PrecedenceParser parser = PrecedenceParser.instance(grammar1);
        say(caption1);
        say(grammar1);
        String expr = "5+ 7++%4 *(!3---2)+7^3)";
        say(caption2 + expr);
        say(parser.parseToAST(expr));

        //Examle 2: sample input #1
        say(caption1);
        String grammar2 = "^:right\n" +
                                      "*:left\n" +
                                      "+:left";
        parser.newGrammar(grammar2);
        say(grammar2);
        expr = "1+2*(3+4)^5+6+7*8";
        say(caption2 + expr);
        say(parser.parseToAST(expr));

        //Example 3: sample input #2:
        say(caption1);
        String grammar3 =  "&:left\n" +
                                       "|:left\n" +
                                       "^:left\n" +
                                       "<:right\n" +
                                       ">:right";
        parser.newGrammar(grammar3);;
        say(grammar3);
        expr = "3|2&7<8<9^4|5";
        say(caption2 + expr);
        say(parser.parseToAST(expr));

        //Example 4 : sample input #3:
        say(caption1);
        String grammar4 = "<:left\n>:right\n.:left";
        say(grammar4);
        parser.newGrammar(grammar4);
        expr = "1<1<1<1<1.1>1>1>1>1";
        say(caption2 + expr);
        say(parser.parseToAST(expr));

        //Example 5: sample input #4:
        say(caption1);
        String grammar5 = "*:left\n+:left";
        parser.newGrammar(grammar5);
        say(grammar5);
        expr = "1+1*(1+1*1)";
        say(caption2 + expr);
        say(parser.parseToAST(expr));

        Field f = Integer.class.getDeclaredField("value");
        f.setAccessible(true);
        f.set(1, 2);
        for (int i = 0; i < 127; i++) {
            say("Value of " + i + " = " + Integer.valueOf(i));
        }

    }

    public static void say(Object o) {
        System.out.println(o);
    }
}
