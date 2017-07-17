package com.xiyuan.msgCheck.filter.impl.keyWords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xiyuan_fengyu on 2017/4/26.
 */
public class KeyWordsTree {

    private CharNode start = new CharNode('\0');

    private static final Finder notFound = new Finder(null);

    public KeyWordsTree() {

    }

    public KeyWordsTree(Iterable<String> word) {
        addWords(word);
    }

    public void addWord(String word) {
        if (word != null) {
            CharNode cur = start;
            for (int i = 0, len = word.length(); i < len; i++) {
                cur = cur.addNext(word.charAt(i));
            }
            cur.addNext('\0');
        }
    }

    public void addWords(Iterable<String> words) {
        if (words != null) {
            for (String word : words) {
                addWord(word);
            }
        }
    }

    public boolean contains(String word) {
        if (word != null) {
            CharNode cur = start;
            for (int i = 0, len = word.length(); i < len; i++) {
                cur = cur.nexts.get(word.charAt(i));
                if (cur == null) {
                    return false;
                }
            }
            return cur.nexts.containsKey('\0');
        }
        return false;
    }

    public String firstKeyWord(String longWord) {
        if (longWord != null) {
            int len = longWord.length();
            loopI:
            for (int i = 0; i < len - 1; i++) {
                CharNode cur = start;
                for (int j = i; j < len; j++) {
                    cur = cur.nexts.get(longWord.charAt(j));
                    if (cur == null) {
                        continue loopI;
                    }
                    else if (cur.nexts.containsKey('\0')) {
                        return longWord.substring(i, j + 1);
                    }
                }
            }
        }
        return null;
    }

    public List<Integer> search(String word, int from) {
        List<Integer> result = new ArrayList<>();
        if (word != null) {
            CharNode cur = start;
            for (int i = from, len = word.length(); i < len; i++) {
                cur = cur.nexts.get(word.charAt(i));
                if (cur == null) {
                    break;
                }
                else if (cur.nexts.containsKey('\0')) {
                    result.add(i);
                }
            }
        }
        return result;
    }

    public Finder finder(String prefix) {
        if (prefix == null) return notFound;
        else if (prefix.isEmpty()) return new Finder(start);
        else {
            CharNode cur = start;
            for (int i = 0, len = prefix.length(); i < len; i++) {
                cur = cur.nexts.get(prefix.charAt(i));
                if (cur == null) {
                    return notFound;
                }
            }
            return new Finder(cur);
        }
    }

    private class CharNode {

        private Character val;

        private HashMap<Character, CharNode> nexts = new HashMap<>();

        private CharNode(Character val) {
            this.val = val;
        }

        private CharNode addNext(char c) {
            if (!nexts.containsKey(c)) {
                CharNode next = new CharNode(c);
                nexts.put(c, next);
                return next;
            }
            return nexts.get(c);
        }

    }

    public static class Finder {

        private final CharNode curNode;

        public final boolean notNull;

        public final boolean found;

        private Finder(CharNode node) {
            this.curNode = node;
            this.notNull = node != null;
            this.found = this.notNull && node.nexts.containsKey('\0');
        }

        public Finder next(char c) {
            return curNode != null ? new Finder(curNode.nexts.get(c)) : notFound;
        }

    }

    public static void main(String[] args) {
        KeyWordsTree tree = new KeyWordsTree();
        tree.addWord("美女");
        tree.addWord("上门");
        tree.addWord("上啊");
        System.out.println(tree.firstKeyWord("哪里有妹子上门服务"));
    }

}
