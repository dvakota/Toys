package dvakota.toys.candystream;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Date: 11/3/14
 * /r/dailyprogrammer shoutout! Easy Halloween challenge as a kitchen-job
 * Generator implementation. Import and go trick-or-treating.
 */
public class CandyStream {
    static Random r = new Random();

    private Map<String, Integer> stash;
    List<String> candyNames;
    private int amountRange = 20;
    int runningTotal;

    /* Empty Stream - will need to be populated from file/URL to work.
    Call init(fileName) or init(fileURL) for input after instantiation.
     */
    public CandyStream() {
        stash = new HashMap<String, Integer>();
        candyNames = new ArrayList<String>();
    }

    /* Takes an array of candy names you want to be given
    Choose your own diabetic adventure */
    public CandyStream(String[] names) {
        stash = new HashMap<String, Integer>();
        candyNames = Arrays.asList(names);
        init();
    }

    /* Takes an array of your favorite candy names and the max possible amount
    you hope to receive at each house.
     */
    public CandyStream(int maxAmount, String[] names) {
        stash = new HashMap<String, Integer>();
        candyNames = Arrays.asList(names);
        init(maxAmount);
    }


    /* read original input from file */
    public void init (String fileName) throws Exception {
        URL url = null;  Scanner sc = null;
        try {
            url = new URL(fileName);
        }  catch (MalformedURLException e) { }
        if (url == null) {
            File f = new File(fileName);
            sc = new Scanner(f);
        } else {
            InputStream is = url.openStream();
            sc = new Scanner(is);
        }
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String name = line.trim();
            Integer stashAmt = stash.get(name);
            if (stashAmt == null) stashAmt = 0;
            stash.put(name, stashAmt + 1);
            runningTotal += 1;
        }
    }

    public void init(int maxAmount, String fileName) throws Exception {
        amountRange = maxAmount;
        init(fileName);

    }

    /* if there's no input file, check if Names have been initialized.
    generate ramdom amounts within a given range for each Name.
    Keep the stream empty otherwise.
     */
    public void init(int amtRange) {
        if (amtRange > 0) amountRange = amtRange;
        init();
    }

    public void init() {
        if (candyNames.size() == 0) {
            say("Stream is empty. To activate, call init() with the list of desired candy names" +
                            "or with a file URI to read the input from, as argument");
            return;
        }
        for (String name : candyNames) {
            Integer amount = stash.get(name);
            if (amount == null) amount = 0;
            int newAmount = r.nextInt(amountRange);
            stash.put(name, amount + newAmount);
            runningTotal += newAmount;
        }
    }

    static class Tuple {
        String name;
        int amount;
        public Tuple(String n, int a) {
            name = n; amount = a;
        }
    }

    Tuple getNext() {
        if (candyNames.size() == 0) return null;
        String name = candyNames.get(r.nextInt(candyNames.size()));
        int newAmount = r.nextInt(amountRange);
        runningTotal += newAmount;
        stash.put(name, newAmount + stash.get(name));
        return new Tuple(name, newAmount);
    }

    public double percentOf(String name) {
        return 100.0f * (double) stash.get(name) / (double) runningTotal;
    }

    public void stats() {
        say("Current candy stats:");
        for (String name : stash.keySet()) {
            say(String.format("%25s: %5d pieces, %7.2f%%",
                                         name, stash.get(name), percentOf(name)));
        }
    }

    public void trickOrTreat() {
        Tuple t = getNext();
        say(String.format("You got %d pieces of %s", t.amount, t.name));
    }

    public void say(Object o) {
        System.out.println(o);
    }

    public static void main(String[] args) throws Exception {
        String[] names = {"Nerds", "PayDay", "Mars", "Twix", "Hershey's mini"};
        CandyStream c = new CandyStream(names);
        c.say("Original candy stash:");
        c.stats();
        for (int i = 0; i < 100; i++) {
            c.say(String.format("Knocking on door No.0%d", i));
            c.trickOrTreat();
        }
        c.say("Final candy stash");
        c.stats();

        String url = System.getProperty("user.dir")+"/186easy.txt";

        CandyStream fc = new CandyStream();
        fc.init(url);
        fc.say("Original stash as read from file");
        fc.stats();
    }
}
