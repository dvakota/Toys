import java.util.Stack;

/**
 * Process the source string as it were a stream (no lookahead) -
 * we can only evaluate current symbol and don't know where the stream ends
 */
public class Brainfuck {
    byte[] data;
    Stack<Integer> callStack;
    int lastIp;
    int dataIndex, inputIndex;
    byte[] input; byte skip;
    String source;

    public Brainfuck(String src, String in) {
        data = new byte[2000];
        input = new byte[in.length()];
        callStack = new Stack<Integer>();
        String[] values = in.split("\\s+"); int i = 0;
        for (String v : values) input[i++] = Byte.parseByte(v);
        source = src;
    }

    public void process() {
        callStack.push(0);
        while (!callStack.empty()) {
            int ip = callStack.pop();
            try {
                char c = source.charAt(ip);
                callStack.push(ip+1);
                if (!"<>=;:+-[]".contains(c + "")) {
                    say("Error - invalid chacarter " + c);
                    return;
                }
                repl(c, ip);
            } catch (StringIndexOutOfBoundsException e) {
                say("DONE");
                return;
            }
        }
    }

    void repl(char b, int ip) {
        if (skip == 0) {
            if (b == '>') dataIndex++;
            if (b == '<') dataIndex--;
            if (b == '+') data[dataIndex]++;
            if (b == '-') data[dataIndex]--;
            if (b == ':') say(data[dataIndex]);
            if (b == ';') data[dataIndex] = input[inputIndex++];
            if (b == '[') {
                if (data[dataIndex] == 0) {
                    skip++;
                } else {
                    callStack.push(lastIp = ip + 1);
                }
            }
        }
        if (b == ']') {
            if (skip < 0)
                throw new RuntimeException("Bad syntax at "
                                                       + ip + ": mismatched bracket");
            if (skip > 0) {
                skip--;
                return;
            }
            if (data[dataIndex] != 0) {
                callStack.push(lastIp);
            }
        }
    }

    public static void say(Object o) {
        System.out.println(o);
    }

    public static void main(String[] args) {
        String stream = ";>;<[->+>+<<]>>[-<<+>>]<<+:>-:<+++>;<>++++<[>+<-]" +
                                    "[->++<]>++++<>;<>:<>;<>:<:>;<+:>-:<+:>-:<>;" +
                                    "<[->+<]+++;>++++<[->++<][->+>++<<]+:>-:<[->+<]" +
                                    ">++++<>++++<:>-<+:>-:<>-<>-<>-<+++[->++<]>+++" +
                                    "+<[->+>++<<]::>:";
        String input = "12 16 15 6 15 11 4 16";
        Brainfuck bfi = new Brainfuck(stream, input);
        bfi.process();
    }
}





