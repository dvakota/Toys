package gk.training;

/**
 * Date: 11/13/16
 */
public class Bases {

    public static String convert(String number, int srcBase, int targetBase) {
        return toTarget(toBase10(number, srcBase), targetBase);
    }

    static int toBase10(String number, int srcBase) {
        int num = Integer.parseInt(number);
        if (srcBase == 10) return num;

        String result = "";
        for (int i = number.length(); i >= 0; i--) {
            int remainder = number.charAt(i);
            int val;
            if (remainder >= '0' && remainder <= '9')
                val = remainder - '0';
            else
                val = remainder - 'a' + 10;
            result = (val * (int) Math.pow(srcBase, number.length() - i)) + result;

            System.out.println(result);
            num /= 10;
        }
        return Integer.parseInt(result);
    }

    static String toTarget(int numberInBase10, int targetBase) {
        if (targetBase == 10) return numberInBase10 + "";
        String result = "";
        for (int i = 0; numberInBase10 > 0; i++) {
            int remainder = numberInBase10 % targetBase;
            if (remainder > 9) {
                result = String.valueOf((char) ((remainder - 10) + 'a')) + result;
            } else {
                result = remainder + result;
            }
            numberInBase10 /= targetBase;
        }
        return result;
    }

    public static void main(String[] args) {
        String num = "33";
        int src = 10;
        int trg = 16;
        say(convert(num, src, trg));
    }

    static void say(Object o) {
        System.out.println(o);
    }
}
