package dvakota.toys.words;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static dvakota.toys.words.Dictionary.InputProcessor;
import dvakota.toys.words.Game.*;

/**
 * Date: 1/24/15
 */
public class Main {


    static class GameOption implements Command {
        String name;
        String description;
        Command command;

        public GameOption(String str) {
            String[] s = str.split(":");
            name = s[0];
            description = s[1];
            Command cmd = parseCommand(name);
            if (cmd != null) command = cmd;
        }

        private Command parseCommand(String s) {
            if (!s.startsWith("/"))
                throw new IllegalArgumentException(("Invalid command definition: " + s
                                                                + "must start with '/"));
            Command cmd = null;

            if (s.equals("/quit")) {
                cmd = new Command() {
                    @Override
                    public void execute(String... args) {
                        Main.finishGame(args.length > 0);
                    }
                };
            }

            if (s.equals("/skip")) {
                cmd = new Command() {
                    @Override
                    public void execute(String... args) {
                        Main.skipTurn();
                    }
                };
            }

            if (s.equals("/used_words")) {
                cmd = new Command() {
                    @Override
                    public void execute(String... args) {
                        Main.showUsedWords();
                    }
                };
            }

            if (s.equals("/score")) {
                cmd = new Command() {
                    @Override
                    public void execute(String... args) {
                        if (args.length == 0) Main.ui.showScore();
                        else {
                            List<Player> list = new ArrayList<Player>();
                            for (String arg : args) {
                                Player p = Main.game.playerNames.get(arg);
                                if (p != null) list.add(p);
                            }
                            Main.ui.showScore(list.toArray(new Player[list.size()]));
                        }
                    } };
            }

            if (s.equals("/add")) {
                cmd = new Command() {
                    @Override
                    public void execute(String... args) {
                        if (args.length == 0) return;
                        for (String arg : args) {
                            Player player = null;
                            if (arg.startsWith("ai") && (arg.length() >= 2)) {
                                player = new Robot(arg, arg.charAt(2) - '0');
                            } else {
                                player = new HumanPlayer(arg);
                            }
                            if (player != null) {
                                Main.addPlayer(player);
                            }
                        }
                    } };
            }

            if (s.equals("/new")) {
                cmd = new Command() {
                    @Override
                    public void execute(String... args) {
                        final int NEXT_L = 0;
                        final int NEXT_R = 1;
                        int next = -1;
                        for (String arg : args) {
                            if (arg.contains(".txt")) {
                                Main.dictionaryFileName = arg.trim();
                                continue;
                            }
                            if (arg.startsWith("-l")) {
                                next = NEXT_L;
                                continue;
                            }
                            if (arg.startsWith("-r")) {
                                next = NEXT_R;
                                continue;
                            }
                            if (next == NEXT_L) {
                                Main.letterCount = Integer.parseInt(arg.trim());
                                next = -1;
                                continue;
                            }
                            if (next == NEXT_R) {
                                Main.roundCount = Integer.parseInt(arg.trim());
                                next = -1;
                            }
                        }

                        Main.finishGame(false);
                        //Main.newGame(Main.dictionaryFileName);
                    } };
            }
            return cmd;
        }

        @Override
        public void execute(String... args) {
            command.execute(args);
        }
    }

    private static void addPlayer(Player player) {
        Main.game.addPlayer(player);
    }

    private static void showUsedWords() {
       Main.ui.showUsedWords(Main.game);
    }

    private static void skipTurn() {
        Main.game.skipPlayer();
    }

    public static boolean processCommand(String cmd) {
        String[] full = cmd.split("\\s+");
        String key = full[0].toLowerCase().trim();
        GameOption command = Main.commands.get(key);
        if (command == null) {
            Main.ui.display("Command not recognized. Type /help to see the full list");
            return false;
        }
        if (full.length == 0) command.execute();
        else command.execute(Arrays.copyOfRange(full, 1, full.length));
        return true;
    }

    private static boolean finishGame(boolean showBest) {
        Main.ui.display("Are you sure you want to end this game? (Y/N)");
        String answer = Main.ui.accept();
        if ("Yy".contains(answer.substring(0))) {
            if (showBest) showCurrentLeads();
            return true;
        }
        return false;
    }

    private static void showCurrentLeads() {
        Main.ui.display("Current leading player(s):");
        int maxPoints = -1;
        for (Player p : Main.game.players) {
            if (p.points >= maxPoints) maxPoints = p.points;
        }

        for (Player p : Main.game.players) {
            if (p.points == maxPoints) {
                Main.ui.display(p.toString());
            }
        }
    }

    static Game game;
    static Map<String, GameOption> commands;
    static UI ui = ConsoleUI.getInstance();
    static int roundCount = 5;
    static int letterCount = 12;
    static String dictionaryFileName;
    static boolean isGameRunning;


    private static InputProcessor<Word> input = new InputProcessor<Word>() {
        @Override
        public Map<String, Word> process (String fileName) {
            Map<String, Word> result = null;
            Scanner sc = null;
            try {
                result = new HashMap<String, Word>();
                sc = new Scanner(new File(fileName));
                while (sc.hasNextLine()) {
                    Word w = new Word(sc.nextLine().toLowerCase().trim());
                    result.put(w.data, w);
                }
            } catch (FileNotFoundException e) {
                ui.display("FATAL ERROR: Dictionary file not found\n" +
                                       e.getMessage() + ": Terminating");
                System.exit(1);
            }
            if (sc != null) sc.close();
            return result;
        }
    };

    private static InputProcessor<GameOption> ipCommands = new InputProcessor<GameOption>() {

        @Override
        public Map<String, GameOption> process(String fileName) {
            Map<String, GameOption> result = null;
            Scanner sc = null;
            try {
                result = new HashMap<String, GameOption>();
                sc = new Scanner(new File(fileName));
                while (sc.hasNextLine()) {
                    String[] s = sc.nextLine().split(":");
                    GameOption command = new GameOption(s[0]);
                    result.put(command.name, command);
                }
            } catch (FileNotFoundException e) {
                Main.ui.display("FATAL ERROR: Dictionary file not found\n" +
                                       e.getMessage() + ": Terminating");
                System.exit(1);
            }
            if (sc != null) sc.close();
            return result;
        }
    };


    public static void init(String...args) throws Exception {
        dictionaryFileName = System.getProperty("user.dir") + "/dict.txt";
        String cmdFileName = System.getProperty("user.dir") + "/commands.txt";
        commands = ipCommands.process(cmdFileName);
        newGame(dictionaryFileName);
    }

    public static Game newGame(String dictFileName) throws Exception {
        Main.ui.display("Starting new game");
        Scanner sc = new Scanner(new File("rules.txt"));
        while (sc.hasNextLine()) {
            ui.display(sc.nextLine());
        }
       return null;
    }
}
