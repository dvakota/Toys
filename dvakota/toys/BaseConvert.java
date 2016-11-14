package dvakota.toys;

/**
 * Date: 11/13/16
 * Exercise: convert an integer from base X to base Y (bases ranging from 2 to 36)
 */
public class BaseConvert {

    public static String convert(String number, int srcBase, int targetBase) {
        return toTargetBase(toValue(number.toLowerCase(), srcBase), targetBase);
    }

    static int toValue(String number, int srcBase) {

        if (srcBase == 10) return Integer.parseInt(number);

        int result = 0;
        for (int i = number.length(); i >= 0; i--) {
            int remainder = number.charAt(i);
            int val;
            if (remainder >= '0' && remainder <= '9')
                val = remainder - '0';
            else
                val = remainder - 'a' + 10;
            result = (val * (int) Math.pow(srcBase, number.length() - i)) + result;

            System.out.println(result);
        }
        return result;
    }

    static String toTargetBase(int numberInBase10, int targetBase) {
        if (targetBase == 10) return numberInBase10 + "";
        String result = "";
        for (int i = 0; numberInBase10 > 0; i++) {
            int remainder = numberInBase10 % targetBase;
            if (remainder > 9) {
                result = (char) ((remainder - 10) + 'a') + result;
            } else {
                result = remainder + result;
            }
            numberInBase10 /= targetBase;
        }
        return result;
    }

    public static void main(String[] args) {
        String num = "433";
        int src = 10;
        int trg = 16;
        say(convert(num, src, trg));
    }

    static void say(Object o) {
        System.out.println(o);
    }
}
