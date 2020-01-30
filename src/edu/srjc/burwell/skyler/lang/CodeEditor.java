/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This class allows the user to edit the code.
 */

package edu.srjc.burwell.skyler.lang;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.srjc.burwell.skyler.lang.CharHelper.*;

public class CodeEditor extends Node
{
    private Tab mTab = null;
    private CodeArea mCodeArea = null;
    private SourceFile mFile = null;

    private static final String[] KEYWORDS = new String[]
    {
        "while", "until", "for", "in", "step", "do", "forever", "repeat", "if", "else", "elif", "return", "true",
        "false", "break", "continue", "when", "use", "or", "and", "not"
    };

    private static final String[] TYPES = new String[]
    {
        "int", "float", "double", "string", "char", "bool", "int", "int8", "int16", "int32", "int64", "int128",
        "uint8", "uint16", "uint32", "uint64", "uint128"
    };

    private static final String[] VARVAL = new String[]
    {
        "var", "val"
    };

    private static final String KEYWORD_PATTERN   = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String TYPES_PATTERN     = "\\b(" + String.join("|", TYPES) + ")\\b";
    private static final String VARVAL_PATTERN    = "\\b(" + String.join("|", VARVAL) + ")\\b";
    private static final String PAREN_PATTERN     = "\\(|\\)";
    private static final String BRACE_PATTERN     = "\\{|\\}";
    private static final String BRACKET_PATTERN   = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN    = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN   = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/" + "|" + "/-(.|\\R)*?-/";
    private static final String MACRO_PATTERN     = "\\$\\b[A-Za-z0-9_]+\\b";
    private static final String LABEL_PATTERN     = "#\\b[A-Za-z0-9_]+\\b";
    private static final String FUNCTION_PATTERN  = "\\b[a-zA-Z_]{1,}[A-Za-z0-9_]+(?=\\()";
    private static final String NUMBER_PATTERN    = "\\b[0-9_]+\\b" + "|" + "\\bnull\\b" + "|" + "\\btrue\\b" + "|"
                                                  + "\\bfalse\\b" + "|" + "\\b[A-Z_]{1,}[A-Z0-9_]+\\b" + "|"
                                                  + "\\b0[xX][0-9a-fA-F]+\\b" + "|" + "\\b[0-9]{1,}[lLfFuU]+\\b";
    private static final String CHAR_PATTERN      = "\'[^\']*(\\.[^\']*)*\'";

    private static final Pattern PATTERN = Pattern.compile
    (
        "(?<KEYWORD>"    + KEYWORD_PATTERN   + ")"
        + "|(?<TYPE>"      + TYPES_PATTERN     + ")"
        + "|(?<VARVAL>"    + VARVAL_PATTERN    + ")"
        + "|(?<PAREN>"     + PAREN_PATTERN     + ")"
        + "|(?<BRACE>"     + BRACE_PATTERN     + ")"
        + "|(?<BRACKET>"   + BRACKET_PATTERN   + ")"
        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
        + "|(?<STRING>"    + STRING_PATTERN    + ")"
        + "|(?<COMMENT>"   + COMMENT_PATTERN   + ")"
        + "|(?<MACRO>"     + MACRO_PATTERN     + ")"
        + "|(?<LABEL>"     + LABEL_PATTERN     + ")"
        + "|(?<FUNCTION>"  + FUNCTION_PATTERN  + ")"
        + "|(?<NUMBER>"    + NUMBER_PATTERN    + ")"
        + "|(?<CHAR>"      + CHAR_PATTERN      + ")"
    );

    CodeEditor(String file)
    {
        mFile = new SourceFile(file);
        mCodeArea = new CodeArea();
        mCodeArea.setId("codeArea");

        IntFunction<Node> lineNumberFactory = LineNumberFactory.get(mCodeArea);
        mCodeArea.setParagraphGraphicFactory(lineNumberFactory);

        mCodeArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if (event.getCode() == KeyCode.TAB)
                {
                    mCodeArea.insertText(mCodeArea.getCaretPosition(), "    ");
                    event.consume();
                }
                else if (event.getCode() == KeyCode.BACK_SPACE)
                {
                    int cursorPos = mCodeArea.getCaretPosition();
                    int start = cursorPos - 4;
                    if (start <= 0)
                    {
                        return;
                    }

                    String txt = mCodeArea.getText(start, cursorPos);
                    if (txt.equals("    "))
                    {
                        mCodeArea.replaceText(start, cursorPos, "");
                        event.consume();
                    }
                }
                else if (event.getCode() == KeyCode.ENTER)
                {
                    String line = mCodeArea.getText(mCodeArea.getCurrentParagraph());
                    System.out.println(line);
                    StringBuilder indent = new StringBuilder("\n");

                    for (int i = 0; i < line.length(); ++i)
                    {
                        char c = line.charAt(i);
                        if (c == ' ')
                        {
                            indent.append(c);
                        }
                        else
                        {
                            break;
                        }
                    }

                    mCodeArea.insertText(mCodeArea.getCaretPosition(), indent.toString());
                    event.consume();
                }
                else if (event.getCode() == KeyCode.LEFT)
                {
                    int cursorPos = mCodeArea.getCaretPosition();
                    int start = cursorPos - 4;
                    if (start <= 0)
                    {
                        return;
                    }

                    String txt = mCodeArea.getText(start, cursorPos);
                    if (txt.equals("    "))
                    {
                        mCodeArea.moveTo(start);
                        event.consume();
                    }
                }
                else if (event.getCode() == KeyCode.RIGHT)
                {
                    int cursorPos = mCodeArea.getCaretPosition();
                    int end = cursorPos + 4;
                    if (end >= mCodeArea.getText().length())
                    {
                        return;
                    }

                    String txt = mCodeArea.getText(cursorPos, end);
                    if (txt.equals("    "))
                    {
                        mCodeArea.moveTo(end);
                        event.consume();
                    }
                }
            }
        });

        Subscription needed = mCodeArea
            .multiPlainChanges()
            .successionEnds(Duration.ofMillis(500))
            .subscribe(ignore -> mCodeArea.setStyleSpans(0, computeHighlighting(mCodeArea.getText())));

        mTab = new Tab(mFile.name(), new VirtualizedScrollPane<>(mCodeArea));

        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle(
            "-fx-background-color: #484848;" +
            "-fx-text-fill: afafaf;" +
            "-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        mCodeArea.setMouseOverTextDelay(Duration.ofSeconds(1));
        mCodeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e ->
        {
            int chPos = e.getCharacterIndex();
            int searchPos = chPos;
            int startPos = 0;
            int endPos = 0;

            char testChar = mCodeArea.getText(searchPos, searchPos + 1).charAt(0);

            while (isAlpha(testChar))
            {
                startPos = searchPos--;
                testChar = mCodeArea.getText(searchPos, searchPos + 1).charAt(0);
            }

            searchPos = chPos;
            testChar = mCodeArea.getText(searchPos, searchPos + 1).charAt(0);

            while (isAlpha(testChar))
            {
                endPos = ++searchPos;
                testChar = mCodeArea.getText(searchPos, searchPos + 1).charAt(0);
            }

            String word = mCodeArea.getText(startPos, endPos);
            String def = isWordImportant(word);

            if (def.isEmpty())
            {
               return;
            }

            Point2D pos = e.getScreenPosition();
            popupMsg.setText(word + ": " + def);
            popup.show(mCodeArea, pos.getX(), pos.getY() + 10);
        });

        mCodeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e ->
        {
            popup.hide();
        });

        openFile();
    }

    public Tab getTab()
    {
        return mTab;
    }

    public String getName()
    {
        return mFile.name();
    }

    public String getPath()
    {
        return mFile.path();
    }

    public String getText()
    {
        return mCodeArea.getText();
    }

    @Override
    public void requestFocus()
    {
        mCodeArea.requestFocus();
    }

    private void openFile()
    {
        try
        {
            mCodeArea.replaceText(new String(Files.readAllBytes(Paths.get(mFile.path()))));
            mCodeArea.scrollToPixel(0, 0);
            mCodeArea.moveTo(0);
        }
        catch (IOException e)
        {
            System.out.println("CodeEditor: Could not open " + mFile.path() + ".");
        }
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text)
    {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find())
        {
            String styleClass =
            matcher.group("KEYWORD")   != null ? "keyword"   :
            matcher.group("TYPE")      != null ? "type"      :
            matcher.group("VARVAL")    != null ? "varval"    :
            matcher.group("PAREN")     != null ? "paren"     :
            matcher.group("BRACE")     != null ? "brace"     :
            matcher.group("BRACKET")   != null ? "bracket"   :
            matcher.group("SEMICOLON") != null ? "semicolon" :
            matcher.group("STRING")    != null ? "string"    :
            matcher.group("COMMENT")   != null ? "comment"   :
            matcher.group("MACRO")     != null ? "macro"     :
            matcher.group("LABEL")     != null ? "label"     :
            matcher.group("FUNCTION")  != null ? "function"  :
            matcher.group("NUMBER")    != null ? "number"    :
            matcher.group("CHAR")      != null ? "char"      :
            null;

            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private String isWordImportant(String word)
    {
        if (word.equals("print"))
        {
            return "Print to the console.";
        }
        else if (word.equals("println"))
        {
            return "Print to the console with a new line.";
        }

        return "";
    }

    @Override
    protected NGNode impl_createPeer()
    {
        return null;
    }

    @Override
    public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx)
    {
        return null;
    }

    @Override
    protected boolean impl_computeContains(double localX, double localY)
    {
        return false;
    }

    @Override
    public Object impl_processMXNode(MXNodeAlgorithm alg, MXNodeAlgorithmContext ctx)
    {
        return null;
    }
}
