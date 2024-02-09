package bp.gui.controller;

import javax.swing.*;
import javax.swing.text.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bp.constants.Constants.*;

public final class TextPaneController {

    private JTextPane jTextPane;

    private final StyleContext context = StyleContext.getDefaultStyleContext();

    private final AttributeSet keywordsAtr = context.addAttribute(context.getEmptySet(), StyleConstants.Foreground, KEYWORD_COLOR);
    private final AttributeSet normalAtr = context.addAttribute(context.getEmptySet(), StyleConstants.Foreground, ATTRIBUTE_COLOR);
    private final AttributeSet specialAtr = context.addAttribute(context.getEmptySet(), StyleConstants.Foreground, SPECIAL_ATTRIBUTE_COLOR);
    private final AttributeSet aggrAtr = context.addAttribute(context.getEmptySet(), StyleConstants.Foreground, AGGR_ATTRIBUTE_COLOR);
    private final AttributeSet numAtr = context.addAttribute(context.getEmptySet(), StyleConstants.Foreground, NUM_ATTRIBUTE_COLOR);

    private final String numberRegex = "\\b\\d+\\b";

    private final String key = "(SELECT|FROM|WHERE|JOIN|USING|ON|HAVING|GROUP|ORDER|BETWEEN|LIKE|BY|ASC|DESC|IN|AND|OR|DISTINCT|IS|NOT|NULL)";
    private final String aggrKey = "(MIN|MAX|AVG|COUNT)";

    public TextPaneController() {
    }

    public int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) break;
        }
        return index;
    }

    public int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) break;
            index++;
        }
        return index;
    }

    public DefaultStyledDocument generateStyle() {
        return new DefaultStyledDocument() {
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                if (str.equals("'")) {
                    super.insertString(offset, "''", a);
                    setCharacterAttributes(offset, 2, specialAtr, false);
                    jTextPane.setCaretPosition(offset + 1);
                }
                else {
                    super.insertString(offset, str, a);
                }

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offset);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offset + str.length());
                int left = before, right = before;
                boolean insideQuotes = false;

                while (right <= after) {
                    if (right == after || String.valueOf(text.charAt(right)).matches("\\W")) {
                        String word = text.substring(left, right);

                        if (word.startsWith("'") && word.endsWith("'")) {
                            setCharacterAttributes(left, right - left, specialAtr, false);
                        }
                        else if (word.startsWith("'")) {
                            insideQuotes = true;
                            setCharacterAttributes(left, right - left, specialAtr, false);
                        }
                        else if (insideQuotes) {
                            setCharacterAttributes(left, right - left, specialAtr, false);
                            if (word.endsWith("'")) {
                                insideQuotes = false;
                            }
                        }
                        else if (word.matches("(?i)(\\W)*" + key)) {
                            setCharacterAttributes(left, right - left, keywordsAtr, false);
                        }
                        else if (word.matches("(?i)(\\W)*" + aggrKey)) {
                            setCharacterAttributes(left, right - left, aggrAtr, false);
                        }
                        else {
                            setCharacterAttributes(left, right - left, normalAtr, false);
                        }

                        compilePattern(text);
                        left = right;
                    }
                    right++;
                }
            }

            public void remove(int offs, int len) throws BadLocationException {
                    super.remove(offs, len);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offs);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offs);

                boolean insideQuotes = false;

                if (after < text.length()) {
                    String beforeChar = String.valueOf(text.charAt(before));
                    String afterChar = String.valueOf(text.charAt(after));
                    if (beforeChar.equals("'") && afterChar.equals("'")) {
                        insideQuotes = true;
                    }
                }

                if (insideQuotes)
                    setCharacterAttributes(before, after - before + 1, specialAtr, false);
                else if (text.substring(before, after).matches("(?i)(\\W)*" + key))
                    setCharacterAttributes(before, after - before, keywordsAtr, false);
                else if (text.substring(before, after).matches("(?i)(\\W)*" + aggrKey))
                    setCharacterAttributes(before, after - before, aggrAtr, false);
                else
                    setCharacterAttributes(before, after - before, normalAtr, false);

                compilePattern(text);
            }

            private void compilePattern(String text) {
                Pattern pattern = Pattern.compile(numberRegex);
                Matcher matcher = pattern.matcher(text);

                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    setCharacterAttributes(start, end - start, numAtr, false);
                }
            }
        };
    }

    public void setTextPane(JTextPane jTextPane) {
        this.jTextPane = jTextPane;
    }
}
