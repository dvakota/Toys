package dvakota.toys.sweater;

import java.util.Stack;

/**
 * Date: 12/3/14
 */
public class KnitParser {
    static class Sequence {
        String payload = "";

        public Sequence(Sequence s, char op, int times) {
            payload = s.payload;
            for (int i = 0; i < times-1; i++) {
                if (i % 2 == 0) {
                    String str = s.payload;
                    payload += Sequence.reverse(op, str);
                } else payload += s.payload;
            }
        }

        public Sequence(char c, char op, int times) {
            for (int i = 0; i < times; i++) {
                payload += c;
            }
        }

        //copy constructor
        Sequence(Sequence s) {
            payload = s.payload;
        }

        public Sequence concatenate(Sequence s) {
            Sequence ns = new Sequence(this);
            ns.payload += s.payload;
            return ns;
        }

        static String reverse(char op, String str) {
            if (op == '%') {
                String string = new String(str);
                char[] chrs = string.toCharArray();
                for (int i = 0, j = str.length()-1; i < j; ++i, --j) {
                    char temp = chrs[i]; chrs[i] = chrs[j]; chrs[j] = temp;
                }
                return String.valueOf(chrs);
            }
            return str;
        }
    }

    public static String parse(String str) {
        Stack<Sequence> expr = new Stack<Sequence>();
        Stack<Character> op = new Stack<Character>();

        for  (char c : str.toCharArray()) {
            if (c == '(' || c == ',' || c == '*' || c == '%') {
                op.push(c);
                continue;
            }
            if (c == ')') {
                if (op.isEmpty()) throw new RuntimeException("Bad string" + str);
                char next = op.pop();
                Sequence s = expr.pop();
                while (next != '(') {
                    say(next);
                    if (next != ',') throw new RuntimeException("Bad string " + str);
                    s = (expr.pop().concatenate(s));
                    next = op.pop();
                }
                expr.push(s);
                continue;
            }
            if (c >= '0' && c <= '9' ) {
                if (expr.isEmpty() || op.isEmpty()) throw new RuntimeException("Bad string " + str);
                Sequence seq = new Sequence(expr.pop(), op.pop(), c - '0');
                expr.push(seq);
                continue;
            }
            if ((""+c).matches("[a-zA-Z]")) {
                expr.push(new Sequence(c, '*', 1));
            }
            if (expr.isEmpty()) throw new RuntimeException("Bad string " + str);

        }
        if (expr.isEmpty() || expr.size() > 1) throw new RuntimeException("Bad string " + str);
        return (expr.pop().payload);
    }

    public static void main(String[] args) {
        say(new Sequence('a', '*', 5).payload);
        say (new Sequence(new Sequence('s', '*', 3)).payload);
        // String line = "(d*4, (s*2, f*5)) * 2";
        String line = "(s*7, (a*8, (b*5)) * 3) % 2";
        say(parse(line));
    }

    public static void say(Object o) {
        System.out.println(o);
    }
}

