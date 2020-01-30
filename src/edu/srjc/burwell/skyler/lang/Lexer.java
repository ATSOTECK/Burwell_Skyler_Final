/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file takes a text file and emits a list of tokens.
 */

package edu.srjc.burwell.skyler.lang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static edu.srjc.burwell.skyler.lang.CharHelper.*;
import static edu.srjc.burwell.skyler.lang.Token.*;
import static edu.srjc.burwell.skyler.lang.Token.TokenType.*;

public class Lexer
{
    private SourceFile mSourceFile = null;
    private HashMap<String, TokenType> mKeywords = null;
    private String mCode = "";
    private int mPos = -1;
    private int mLinePos = -1;
    private int mLine = -1;
    private char mCurrentChar = '\0';

    public Lexer(SourceFile file)
    {
        mCode = "";
        mPos = 0;
        mLinePos = 0;
        mLine = 1;
        mCurrentChar = '\0';

        loadFile(file);

        if (mSourceFile != null)
        {
            mCurrentChar = mCode.charAt(0);
        }

        mKeywords = new HashMap<String, TokenType>()
        {{
            put("if", TK_IF);
            put("elif", TK_ELIF);
            put("else", TK_ELSE);
            put("do", TK_DO);
            put("in", TK_IN);
            put("step", TK_STEP);
            put("for", TK_FOR);
            put("forever", TK_FOREVER);
            put("while", TK_WHILE);
            put("until", TK_UNTIL);
            put("return", TK_RETURN);
            put("true", TK_TRUE);
            put("false", TK_FALSE);
            put("var", TK_VAR);
            put("val", TK_VAL);
            put("repeat", TK_REPEAT);
            put("break", TK_BREAK);
            put("continue", TK_CONTINUE);
            put("when", TK_WHEN);
            put("use", TK_USE);
            put("int", TK_INT);
            put("float", TK_FLOAT);
            put("double", TK_DOUBLE);
            put("string", TK_STRING_TYPE);
            put("char", TK_CHAR_TYPE);
            put("bool", TK_BOOL);
            put("uint8", TK_U8);
            put("uint16", TK_U16);
            put("uint32", TK_U32);
            put("uint64", TK_U64);
            put("uint128", TK_U128);
            put("int8", TK_S8);
            put("int16", TK_S16);
            put("int32", TK_S32);
            put("int64", TK_S64);
            put("int128", TK_S128);
            put("or", TK_OR);
            put("and", TK_AND);
            put("not", TK_NOT);
        }};
    }

    public Token nextToken()
    {
        Token token = new Token(TK_UNKNOWN, mLine, mLinePos);

        while (mCurrentChar != '\0' && mPos < mCode.length())
        {
            if (isAlpha(mCurrentChar))
            {
                StringBuilder word = new StringBuilder();

                while (!isWhitespace(mCurrentChar) && isAlphanumeric(mCurrentChar) || mCurrentChar == '_')
                {
                    word.append(mCurrentChar);
                    eat();
                }

                token.setLine(mLine);
                token.setPos(mLinePos);
                token.setLexeme(word.toString());
                token.setType(mKeywords.getOrDefault(word.toString(), TK_IDENT));

                return token;
            }

            if (isNumber(mCurrentChar))
            {
                StringBuilder word = new StringBuilder();
                while ((mCurrentChar == '_' || mCurrentChar == '.' || isHexnumber(mCurrentChar) || mCurrentChar == 'x' || mCurrentChar == 'x') ||
                        mCurrentChar == 'o' || mCurrentChar == 'O' || mCurrentChar == 'b' || mCurrentChar == 'B' ||
                        (!isWhitespace(mCurrentChar) && (isNumber(mCurrentChar) && !(isAlpha(mCurrentChar)))))
                {
                    if (mCurrentChar != '_')
                    {
                        word.append(mCurrentChar);
                    }

                    eat();
                }

                token.setType(TK_NUMBER);
                token.setLexeme(word.toString());

                return token;
            }

            if (isWhitespace(mCurrentChar))
            {
                eatWhitespace();
                continue;
            }

            switch (mCurrentChar)
            {
                case '"':
                {
                    StringBuilder str= new StringBuilder();
                    eat();

                    boolean overwrite = false;

                    while (mCurrentChar != '"' || overwrite)
                    {
                        overwrite = false;

                        if (mCurrentChar == '\\' && mCode.charAt(mPos + 1) == '"')
                        {
                            overwrite = true;
                        }

                        if (isNewline(mCurrentChar))
                        {
                            ++mLine;
                            str.append("\\n");
                            eat();
                            continue;
                        }

                        str.append(mCurrentChar);
                        eat();
                    }

                    token.setType(TK_STRING);
                    token.setLexeme(str.toString());

                    eat();
                    return token;
                }

                case '\'':
                {
                    eat();
                    token.setLexeme(mCurrentChar);
                    eat(2);

                    token.setType(TK_CHAR);
                    return token;
                }

                case ':':
                {
                    token.setType(TK_COLON);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case ',':
                {
                    token.setType(TK_COMMA);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case ';':
                {
                    token.setType(TK_SEMICOLON);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case '(':
                {
                    token.setType(TK_LPAREN);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case ')':
                {
                    token.setType(TK_RPAREN);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case '{':
                {
                    token.setType(TK_LBRACE);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case '}':
                {
                    token.setType(TK_RBRACE);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case '[':
                {
                    token.setType(TK_LBRACKET);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case ']':
                {
                    token.setType(TK_RBRACKET);
                    token.setLexeme(mCurrentChar);
                    eat();

                    return token;
                }

                case '.':
                {
                    if (mCode.charAt(mPos + 1) == '.')
                    {
                        if (mCode.charAt(mPos + 2) == '.')
                        {
                            token.setType(TK_VARY);
                            token.setLexeme("...");
                            eat(3);

                            return token;
                        }
                        else
                        {
                            token.setType(TK_RANGE);
                            token.setLexeme("..");
                            eat(2);

                            return token;
                        }
                    }
                    else
                    {
                        token.setType(TK_DOT);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '#':
                {
                    token.setType(TK_LABEL);
                    eat();

                    StringBuilder lexeme = new StringBuilder();

                    while (!isWhitespace(mCurrentChar) && (isAlphanumeric(mCurrentChar) || mCurrentChar == '_'))
                    {
                        lexeme.append(mCurrentChar);
                        eat();
                    }

                    token.setLexeme(lexeme.toString());

                    return token;
                }

                case '$':
                {
                    token.setType(TK_MACRO);
                    eat();

                    StringBuilder lexeme = new StringBuilder();

                    while (!isWhitespace(mCurrentChar) && (isAlphanumeric(mCurrentChar) || mCurrentChar == '_'))
                    {
                        lexeme.append(mCurrentChar);
                        eat();
                    }

                    token.setLexeme(lexeme.toString());

                    return token;
                }

                case '=':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_EQU);
                        token.setLexeme("==");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_ASSIGN);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '!':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_NOTEQU);
                        token.setLexeme("!=");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_NOT);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '+':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_ADDEQU);
                        token.setLexeme("+=");
                        eat(2);

                        return token;
                    }
                    else if (mCode.charAt(mPos + 1) == '+')
                    {
                        token.setType(TK_INC);
                        token.setLexeme("++");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_ADD);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '-':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_SUBEQU);
                        token.setLexeme("-=");
                        eat(2);

                        return token;
                    }
                    else if (mCode.charAt(mPos + 1) == '-')
                    {
                        token.setType(TK_DEC);
                        token.setLexeme("--");
                        eat(2);

                        return token;
                    }
                    else if (isNumber(mCode.charAt(mPos + 1)))
                    {
                        eat();
                        StringBuilder word = new StringBuilder("-");
                        while ((mCurrentChar == '_' || mCurrentChar == '.' || isHexnumber(mCurrentChar) || mCurrentChar == 'x' || mCurrentChar == 'X') ||
                                mCurrentChar == 'o' || mCurrentChar == 'O' || mCurrentChar == 'b' || mCurrentChar == 'B' ||
                                (!isWhitespace(mCurrentChar) && (isNumber(mCurrentChar) && !(isAlpha(mCurrentChar)))))
                        {
                            if (mCurrentChar != '_')
                            {
                                word.append(mCurrentChar);
                            }
                            eat();
                        }

                        token.setType(TK_NUMBER);
                        token.setLexeme(word.toString());

                        return token;
                    }
                    else
                    {
                        token.setType(TK_SUB);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '*':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_MULEQU);
                        token.setLexeme("*=");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_MUL);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '/':
                {
                    if (mCode.charAt(mPos + 1) == '/')
                    {
                        eatLine();
                    }
                    else if (mCode.charAt(mPos + 1) == '-' || mCode.charAt(mPos + 1) == '*')
                    {
                        eatBlock();
                    }
                    else if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_DIVEQU);
                        token.setLexeme("/=");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_DIV);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                } break;
                //Note to self: The above break is needed.

                case '%':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_MODEQU);
                        token.setLexeme("%=");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_MOD);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '^':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_POWEQU);
                        token.setLexeme("^=");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_POW);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '>':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_GREATEREQU);
                        token.setLexeme(">=");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_GREATER);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '<':
                {
                    if (mCode.charAt(mPos + 1) == '=')
                    {
                        token.setType(TK_LESSEQU);
                        token.setLexeme("<=");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_LESS);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '&':
                {
                    if (mCode.charAt(mPos + 1) == '&')
                    {
                        token.setType(TK_AND);
                        token.setLexeme("&&");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_BITAND);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                case '|':
                {
                    if (mCode.charAt(mPos + 1) == '|')
                    {
                        token.setType(TK_OR);
                        token.setLexeme("||");
                        eat(2);

                        return token;
                    }
                    else
                    {
                        token.setType(TK_BITOR);
                        token.setLexeme(mCurrentChar);
                        eat();

                        return token;
                    }
                }

                default:
                {
                    eat();
                }
            }
        }

        return token;
    }

    public void eat()
    {
        ++mLinePos;
        if (++mPos >= mCode.length())
        {
            mCurrentChar = '\0';
        }
        else
        {
            mCurrentChar = mCode.charAt(mPos);
        }
    }

    public void eat(int amount)
    {
        mLinePos += amount;
        mPos += amount;

        if (mPos >= mCode.length())
        {
            mCurrentChar = '\0';
        }
        else
        {
            mCurrentChar = mCode.charAt(mPos);
        }
    }

    public void eatLine()
    {
        while (!isNewline(mCurrentChar))
        {
            eat();
        }
    }

    public void eatBlock()
    {
        boolean endFound = false;
        int nestedFound = 0;
        while (!endFound && mCurrentChar != '\0')
        {
            eat();

            if (isNewline(mCurrentChar))
            {
                ++mLine;
            }

            if (mCurrentChar == '/' && (mCode.charAt(mPos + 1) == '-' || mCode.charAt(mPos + 1) == '*'))
            {
                ++nestedFound;
            }

            if ((mCurrentChar == '-' || mCurrentChar == '*') && mCode.charAt(mPos + 1) == '/')
            {
                if (nestedFound > 0)
                {
                    --nestedFound;
                }
                else
                {
                    endFound = true;
                }
            }
        }

        eat(2);
    }

    public void eatWhitespace()
    {
        while (isWhitespace(mCurrentChar))
        {
            if (isNewline(mCurrentChar))
            {
                mLinePos = 0;
                ++mLine;
            }

            eat();
        }
    }

    private void loadFile(SourceFile sourceFile)
    {
        mSourceFile = sourceFile;

        try
        {
            mCode = new String(Files.readAllBytes(Paths.get(mSourceFile.path())));
        }
        catch (IOException ex)
        {
            System.out.println("Lexer: Could not find " + sourceFile.path() + ". Abort compile.");
            mSourceFile = null;
        }
    }

    public SourceFile getSourceFile()
    {
        return mSourceFile;
    }

    public int line()
    {
        return mLine;
    }

    public int linePos()
    {
        return mLinePos;
    }
}
