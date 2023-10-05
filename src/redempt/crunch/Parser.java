package redempt.crunch;

import redempt.crunch.data.CharTree;
import redempt.crunch.data.Pair;
import redempt.crunch.exceptions.ExpressionCompilationException;

public class Parser {
    
    public final String str;
    public int cur = 0;
    
    public Parser(String str) {
        this.str = str;
    }
    
    public char peek() {
        return str.charAt(cur);
    }
    
    public char advance() {
        return str.charAt(cur++);
    }
    
    public void advanceCursor() {
        cur++;
    }
    
    public void expectChar(char c) {
        if (advance() != c) {
            throw new ExpressionCompilationException(this, "Expected '" + c + "'");
        }
    }
    
    public void whitespace() {
        while (Character.isWhitespace(peek())) {
            cur++;
        }
    }
    
    public boolean strMatches(String prefix, boolean advance) {
        boolean matches = str.regionMatches(cur, prefix, 0, prefix.length());
        if (matches && advance) {
            cur += prefix.length();
        }
        return matches;
    }
    
    public <T> T getWith(CharTree<T> tree) {
        Pair<T, Integer> result = tree.getFrom(str, cur);
        T parsed = result.getFirst();
        if (parsed == null) {
            return null;
        }
        int offset = result.getSecond();
        cur += offset;
        return parsed;
    }
    
}