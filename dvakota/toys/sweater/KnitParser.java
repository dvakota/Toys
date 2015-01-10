package dvakota.toys.sweater;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class KnitParser {
    static class Sequence {
        String payload = "";
        int rows = 1;

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

        Sequence(Sequence s) {
            payload = s.payload;
            rows = s.rows;
        }

        public void setRowCount(int rc) {
            rows = rc;
        }

        public Sequence concatenate(Sequence s) {
            Sequence ns = new Sequence(this);
            ns.payload += s.payload;
            return ns;
        }

        @Override
        public String toString() {
            String result = "";
            for (int i = 0; i < rows; i++) {
                result += payload + "\n";
            }
            return result.substring(0, result.length() - 1);
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

    public static Sequence parse(String str) {
        if (str.length() == 0) return new Sequence(' ', '*', 1);
        int r = str.indexOf('#');
        String rowstr = (r >= 0) ? str.substring(0, r).trim() : "";

        if (!rowstr.matches("[0-9]+"))
            throw new RuntimeException("Invalid start of string - missing row number");

        Integer rows = Integer.parseInt(rowstr);
        str = str.substring(r + 1).trim();
        Stack<Sequence> expr = new Stack<Sequence>();
        Stack<Character> op = new Stack<Character>();
        Stack<Integer> pointer = new Stack<Integer>();

        int index = 0;
        pointer.push(index);

        while (!pointer.isEmpty()) {
            index = pointer.pop();
            char c = str.charAt(index);
            index++;

            if (index < str.length()) {
                pointer.push(index);
            }

            if (c == '(' || c == ',' || c == '*' || c == '%') {
                op.push(c);
                continue;
            }

            if (c == ')') {
                if (op.isEmpty()) throw new RuntimeException("Bad string " + str);
                char next = op.pop();
                Sequence s = expr.pop();
                while (next != '(') {
                    if (next != ',') throw new RuntimeException("Bad string " + str);
                    s = (expr.pop().concatenate(s));
                    next = op.pop();
                }
                expr.push(s);
                continue;
            }

            if (c >= '0' && c <= '9' ) {
                if (expr.isEmpty() || op.isEmpty()) throw new RuntimeException("Bad string " + str);
                String digits = "";  int i = index - 1;
                digit:while (c >= '0' && c <= '9') {
                    digits += c;
                    i++;
                    if (i < str.length())
                        c = str.charAt(i);
                    else break digit;
                }
                if (!pointer.isEmpty()) pointer.pop();
                if (i < str.length()) pointer.push(i);
                int n = Integer.parseInt(digits);

                Sequence seq = new Sequence(expr.pop(), op.pop(), n);
                expr.push(seq);
                continue;
            }

            if ((""+c).matches("[a-z]")) {
                Sequence s = new Sequence(c, '*', 1);
                expr.push(s);
            }
        }

        if (expr.isEmpty())
            throw new RuntimeException("Bad string " + str);

        Sequence result = expr.pop();
        while (!expr.isEmpty()) {
            result = expr.pop().concatenate(result);
        }

        result.setRowCount(rows);
        return result;
    }

    public static <T> void met(Iterable<? super T> it) {


    }

    public static void main(String[] args) throws Exception {
        String s = "1# b*2, ((a*2, b*1, c*2)%2)*2, b*2";
        say(KnitParser.parse(s));
        String fileName = "";

        List<String> li = new ArrayList<String>();
        KnitParser.met(li);

        if (args.length== 0) {
            say("Input file required, please specify name and full path to the file and press Enter");
            Scanner sc = new Scanner(System.in);
            fileName = sc.nextLine().trim();
        } else {
            fileName = args[0].trim();
        }

        Scanner sc = new Scanner(new File(fileName));

        while(sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            say(KnitParser.parse(line).toString().replace('w', ' ')
                            .replace('b', '#').replace('y', '*')
                            .replace('g', '@'));
        }
    }

    public static void say(Object o) {
        System.out.println(o);
    }
}

