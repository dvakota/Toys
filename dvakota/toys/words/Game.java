package dvakota.toys.words;

import java.util.*;

/**
 * Date: 1/24/15
 */
public class Game {
    static final int OK = 0;
    static final int ERROR_AlREADY_PLAYED = 1;
    static final int ERROR_BAD_WORD = 2;
    static final int COMMAND_ENTERED = 3;

    static final char[] commonLetters = "aoei".toCharArray();
    static final char[] regularLetters = "bcdfghjklmnprstu".toCharArray();
    static final char[] rareLetters = "qvwxyz".toCharArray();

    Player currentPlayer;

    public interface Command {
        public void execute(String...args);
    }

    public interface UI {
        public String accept();
        public void display(String message);
        public void updateNewRound();
        public void showScore(Player...players);
        public void showUsedWords(Game game);
    }

    Map<String, Word> played;
    int letterCount;
    Map<String, Player> playerNames;


    static Game instance;
    UI gameUI;

    boolean isRunning;
    Dictionary dict;
    Queue<Player> players;

    private Game(Dictionary d, UI ui, int letters) {
        dict = d;
        players = new LinkedList<Player>();
        gameUI = ui;
        letterCount = letters;
        played = new HashMap<String, Word>();
        playerNames = new HashMap<String, Player>();
    }

    public static Game newGame(Dictionary d, UI ui, int letters, Player...playerList) {
        instance = new Game(d, ui, letters);
        for (Player p : playerList) {
            instance.players.offer(p);
            instance.playerNames.put(p.name, p);
        }
        return instance;
    }

    public void addPlayer(Player p) {
        if (playerNames.containsKey(p.name)) {
            char last = p.name.charAt(p.name.length() - 1);
            if (Character.isDigit(last)) {
                 p.name += (last - '0') + 1;
            } else p.name += 1;
        }
        playerNames.put(p.name, p);
        players.offer(p);
    }


    private String selectLetters(int count) {
        String result = "";

        Random rnd = new Random();
        for (int i = 0; i < count; i++) {
            double probability = Math.random();
            char c;
            if (probability < 0.85) {
                if (probability < 0.4)
                    c = commonLetters[rnd.nextInt(commonLetters.length)];
                else
                    c = regularLetters[rnd.nextInt(regularLetters.length)];
            } else c = rareLetters[rnd.nextInt(rareLetters.length)];
            result += c;
        }
        return result;
    }

    public void newRound() {
        gameUI.updateNewRound();
        for (Player p : players) {
            int letterCount = p.missingLetters();
            p.newLetters(selectLetters(letterCount));
        }
    }

    public void resume() {
        isRunning = true;
    }

    public void skipPlayer() {
        gameUI.display(currentPlayer.name.toUpperCase() + " SKIPS TURN");
    }

    public String next() {
        Player p = players.poll();
        currentPlayer = p;
        players.offer(p);
        gameUI.display(p.name.toUpperCase() + "'S TURN:");
        gameUI.display(p.toString());
        return p.suggestWord();
    }

    private int checkWord(String str) {
        String cmd = str.split("\\s+")[0];
        if (!dict.contains(str) && !Main.commands.containsKey(str)) return ERROR_BAD_WORD;
        if (played.containsKey(str)) return ERROR_AlREADY_PLAYED;
        if (Main.commands.containsKey(cmd)) return COMMAND_ENTERED;
        return OK;
    }

}
