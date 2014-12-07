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
        data = new byte[src.length()];
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
                if (skip == 0) repl(c, ip);
            } catch (StringIndexOutOfBoundsException e) {
                say("DONE");
                return;
            }
        }
    }

    void repl(char b, int ip) {
        if (b == '>') {say("Move one right"); dataIndex++;}
        if (b == '<') {say("Move one left"); dataIndex--;}
        if (b == '+') {say("Increment data"); data[dataIndex]++;}
        if (b == '-') {say("Decrement data"); data[dataIndex]--;}
        if (b == ':') {say("Printing"); say(data[dataIndex]);}
        if (b == ';') {data[dataIndex] = input[inputIndex++]; say("Input data " +data[dataIndex]);}
        if (b == '[') {

            if (data[dataIndex] == 0) {
                skip++;
            } else {
                callStack.push(ip + 1);
                lastIp = ip + 1;
                say("Entering loop");
            }
        }
        if (b == ']') {
            if (skip < 0)
                throw new RuntimeException("Bad syntax at " + ip +": mismatched bracket");
            if (skip-- > 0) return;
            if (data[dataIndex] != 0) {
                say("Repeating loop");
                callStack.push(lastIp);

            } else {
                say("Exiting loop");
            }
        }
    }


    public static void say(Object o) {
        System.out.println(o);
    }

    public static void main(String[] args) {
        Brainfuck brain = new Brainfuck(";>;<[->+<[++>--<]]:>:", "2 5");
        brain.process();
    }
}





