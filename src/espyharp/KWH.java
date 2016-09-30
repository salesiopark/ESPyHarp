/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espyharp;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * KWH : KeyWord Highlighting 관련 변수/메서드를 저장하는 클래스
 * @author salesiopark
 */
public class KWH {
    
    private static final String[] PYKEYWORDS = new String[] {
        "False", "class", "finally", "is", "return",
        "None", "continue", "for", "lambda", "try",
        "True", "def", "from", "nonlocal", "while",
        "and", "del", "global", "not", "with",
        "as", "elif", "if", "or", "yield",
        "assert", "else", "import", "pass",
        "break", "except", "in",  "raise"
    };

    private static final String[] PYBUILTINS = new String[] {
        "__build_class__", "__import__", "__repl_print__", "bool", "bytes",
        "bytearray", "dict", "enumerate", "filter", "float",
        "frozenset", "int", "list", "map", "memoryview",
        "object", "property", "range", "reversed", "set", "str",
        "super", "tuple", "type", "zip", "classmethod", "staticmethod",
        "Ellipsis", "abs", "all", "any", "bin", "callable", "chr", "dir",
        "divmod", "eval", "exec", "getattr", "setattr", "globals", "hasattr",
        "hash", "hex", "id", "isinstance", "issubclass", "iter", "len", "locals",
        "max", "min", "next", "oct", "ord", "pow", "print", "repr", "round",
        "sorted", "sum", "BaseException", "ArithmeticError", "AssertionError",
        "AttributeError", "EOFError", "Exception", "GeneratorExit", "ImportError",
        "IndentationError", "IndexError", "KeyboardInterrupt", "KeyError",
        "LookupError", "MemoryError", "NameError", "NotImplementedError",
        "OSError", "OverflowError", "RuntimeError", "StopAsyncIteration", "StopIteration",
        "SyntaxError", "SystemExit", "TypeError", "UnicodeError", "ValueError",
        "ZeroDivisionError", "help", "input", "open"
    };
    
    
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", PYKEYWORDS) + ")\\b";
    private static final String BUILTIN_PATTERN = "\\b(" + String.join("|", PYBUILTINS) + ")\\b";
    private static final String STRING_PATTERN = "(\"([^\"\\\\]|\\\\.)*\")|('([^'\\\\]|\\\\.)*')";
    private static final String COMMENT_PATTERN = "#[^\n]*";
    private static final String NUMBER_PATTERN = "\\b((0b|0x|0o)?(\\d)+(\\.\\d*)?(e[+-]?\\d+)?)\\b";

    private static final Pattern PYPATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<BUILTIN>" + BUILTIN_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    
    //------------------------------------------------------------------
    //     * From RichTextFX demo
    //     * @see <a href="">소스</a>
    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PYPATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("BUILTIN") != null ? "builtin" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("NUMBER") != null ? "number" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
//          spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton("code"), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.singleton("code"), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
