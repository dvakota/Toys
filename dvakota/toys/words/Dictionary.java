package dvakota.toys.words;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Date: 1/24/15
 */
public class Dictionary {

    public interface InputProcessor<T> {
        public Map<String, T> process(String fileName);
    }

    Map<String, Word> wordIndex;
    List<Word> wordList;
    private static Dictionary instance;

    private Dictionary(InputProcessor ip, String file) {
        wordIndex = ip.process(file);
        for (String s : wordIndex.keySet()) {
            Word w = wordIndex.get(s);
            wordList.add(w);
        }
    }

    public static Dictionary instance(InputProcessor ip, String file) {
        instance = new Dictionary(ip, file);
        return instance;
    }

    boolean contains(String str) {
        return (wordIndex.containsKey(str));
    }

    List<Word> getAnagrams(Word word) {
        List<Word> result = new ArrayList<Word>();
        for (Word w : wordList) {
           if (w.key == word.key) {
               result.add(w);
           }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Word w : wordList) {
            result.append(w);
            result.append("\n");
        }
        return result.toString();
    }
}
