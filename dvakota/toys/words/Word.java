package dvakota.toys.words;

/**
 * Date: 1/24/15
 */
public class Word {
    String data;
    long key;

    public Word(String s) {
        data = s.toLowerCase().trim();
        key = generateKey(data);
    }

    /**
     * Generates a special key to identify all anagrams of the word
     * @param s original word
     * @return anagram hash of the word
     */
    static long generateKey(String s) {
        long sum = 0;
        for (char c : s.toCharArray()) {
           sum += c * Math.pow(3, c - 'a');
        }
        return sum;
    }

    static boolean isWord(String w) {
        for (char c : w.toLowerCase().toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Word) || o == null) return false;
        Word w = (Word) o;
        return data.hashCode() == w.hashCode();
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public String toString() {
        return data;
    }
}
