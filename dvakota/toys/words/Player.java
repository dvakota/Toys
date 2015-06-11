package dvakota.toys.words;

import java.util.Arrays;
import java.util.Map;

/**
 * Date: 1/24/15
 */
public abstract class Player {

    char[] letters;
    int points;
    Game.UI ui;
    String name;

    public Player(String playerName) {
        name = playerName;
        letters = new char[Game.instance.letterCount];
        ui = Game.instance.gameUI;
        Arrays.fill(letters, (char) -1);
    }

    public abstract String suggestWord();

    public void confirmWord(String word) {
        for (char cw : word.toCharArray()) {
            for (int i = 0; i < letters.length; i++) {
                if (cw == letters[i]) {
                    letters[i] = (char) -1;
                    break;
                }
            }
        }
        points += letters.length - word.length();
    }

    public int missingLetters() {
        int result = 0;
        for (char c : letters) {
            if (c == -1) result++;
        }
        return result;
    }

    public void newLetters(String newLetters) {
        int missing = missingLetters();

        if (missing != newLetters.length())
            throw new IllegalArgumentException("Number of letters mismatch: " + missing + " needed, " +
                                                           newLetters + " received");

        String remaining = "";
        for (char c : letters) {
            if (c != -1) remaining += c;
        }

        remaining += newLetters;
        letters = remaining.toCharArray();
    }

    @Override
    public String toString() {
        String result = name.toUpperCase() + "\n";
        for (char c : letters) {
            result += "[";
            if (c != -1) result += c;
            else result += ' ';
            result += "] ";
        }
        result += "\nCurrent score:\t" + points + "\n";
        return result;
    }
}

class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public String suggestWord() {
        return ui.accept();
    }
}

class Robot extends Player {
    int type;
    Dictionary dict;

    public Robot(String name, int t) {
        super(name);
        type = t;
        dict = Main.game.dict;
        //todo: add obscene words if type = 4 (Rude robot)
    }

    void addDictionary(Dictionary.InputProcessor<Word> ip, String fileName) {
        Map<String, Word> toAdd = ip.process(fileName);
        for (String s : toAdd.keySet()) {
            if (dict.contains(s)) continue;
            dict.wordIndex.put(s, toAdd.get(s));
        }
    }

    @Override
    public String suggestWord() {
        return "ABCD";
    }
}
