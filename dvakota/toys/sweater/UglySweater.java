package dvakota.toys.sweater;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Date: 11/8/14
 */
public class UglySweater {

    static class Sequence {
        String payload;

        public Sequence(String str, int t) {
            payload = repeat(str, t, false);
        }

        public String mirror(String s, int t) {
            String result = s;
            for (int i = 0; i < t; i++) {
                String mirror = reverse(s);
                result += mirror;
            }
            return result;
        }

        public Sequence(String str, int t, boolean reverse) {
            payload = repeat(str, t, reverse);
        }

        public Sequence(Sequence s, int t, boolean reverse) {
            this(s.payload, t, reverse);
        }

        public String repeat(String s, int times, boolean reverse) {
            if (times < 1) return s;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < times; i++) {
                if (reverse) {
                    result.append(reverse(s));
                }
                else result.append(s);
            }
            return result.toString();
        }

        private String reverse(String str) {
            char[] chars = str.toCharArray();
            for (int i = 0, j = str.length()-1; i < j; ++i, --j) {
                char swap = chars[i];
                chars[i] = chars[j];
                chars[j] = swap;
            }
            return String.valueOf(chars);
        }

        public Sequence concatenate(Sequence s) {
            return new Sequence(payload += s.payload, 1);
        }

        public String toString() {
            return payload;
        }

    }

    static void input() {

    }


    private static Sequence parse1(char[] chars, Stack<Integer> returnp) {
        int times = 0; String stiches = ""; boolean reverse = false;
        Sequence seq = new Sequence("", 0); int d = 0;
        int i = 0;
        while (!returnp.isEmpty()) {
            i = returnp.pop();
            if (i < 0) break;
            returnp.push(i - 1);

            char c = chars[i]; String s = String.valueOf(c);
            if (s.matches("[0-9]"))
                times +=  (c - '0') * (int) Math.pow(10, d++);

            if (c == '%') { reverse = true; d = 0; }
            if (c == '*') { reverse = false; d = 0; }
            if (s.matches("[A-Za-z]"))
                stiches = s + stiches;
            if (c == ',') {
                seq = new Sequence(stiches, times, reverse).concatenate(seq);
                times = 0; stiches = ""; reverse = false; d = 0;
            }
            if (c == ')') {
                returnp.push(i - 1);
                Sequence subseq = parse1(chars, returnp);
                seq = new Sequence(subseq, times, reverse).concatenate(seq);
                times = 0; d = 0; stiches = ""; reverse = false;
            }
            if (c == '(') {
                if (stiches != "") {
                    seq = new Sequence(stiches, times, reverse).concatenate(seq);
                }
                stiches = ""; times = 0; d = 0; reverse = false;
                returnp.push(i - 1);
            }
        }
        if (i <= 0 && times > 0) {
            if (stiches != "") {
                seq = new Sequence(stiches, times, reverse).concatenate(seq);
            }
        }
        return seq;
    }


    static void say(Object o) {
        System.out.println(o);
    }


    public static void main(String[] args) {
        String token = "(rb * 2, g * 5), x*3) % 2";
        token = "((a *3, b * 2) * 2, x*4) % 2";
        token = "(abcd * 1, x*4) % 2";
        Stack<Integer> returnPoint = new Stack<Integer>();
        returnPoint.push(token.length() - 1);
        Sequence s = parse1(token.toCharArray(), returnPoint);
        say(s.toString());
    }
}
