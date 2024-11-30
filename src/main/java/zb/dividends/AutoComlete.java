package zb.dividends;


import org.apache.commons.collections4.Trie;

public class AutoComlete {
    private Trie trie;
    public AutoComlete(Trie trie) {
        this.trie = trie;
    }

    public void add(String s) {
        this.trie.put(s, "world");
    }

    public Object get(String s) {
        return this.trie.get(s);
    }
}
