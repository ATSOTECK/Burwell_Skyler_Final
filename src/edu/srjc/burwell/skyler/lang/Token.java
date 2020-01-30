/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file represents the token information.
 */

package edu.srjc.burwell.skyler.lang;

import java.util.Objects;

import static edu.srjc.burwell.skyler.lang.Token.TokenType.*;

public class Token
{
    public enum TokenType
    {
        TK_UNKNOWN,

        TK_IF,
        TK_ELIF,
        TK_ELSE,
        TK_DO,
        TK_IN,
        TK_STEP,

        TK_FOR,
        TK_FOREVER,
        TK_WHILE,
        TK_UNTIL,
        TK_BREAK,
        TK_CONTINUE,
        TK_RETURN,
        TK_TRUE,
        TK_FALSE,
        TK_VAR,
        TK_VAL,
        TK_WHEN,
        TK_REPEAT,

        TK_USE,

        TK_INT,
        TK_FLOAT,
        TK_DOUBLE,
        TK_STRING_TYPE,
        TK_CHAR_TYPE,
        TK_BOOL,
        TK_U8,
        TK_U16,
        TK_U32,
        TK_U64,
        TK_U128,
        TK_S8,
        TK_S16,
        TK_S32,
        TK_S64,
        TK_S128,

        TK_COLON,
        TK_COMMA,
        TK_SEMICOLON,
        TK_LPAREN,
        TK_RPAREN,
        TK_LBRACE,
        TK_RBRACE,
        TK_LBRACKET,
        TK_RBRACKET,
        TK_DOT,
        TK_RANGE,
        TK_VARY,
        TK_LABEL,
        TK_MACRO,

        TK_ASSIGN,
        TK_EQU,
        TK_AND,
        TK_OR,
        TK_NOT,
        TK_NOTEQU,
        TK_ADD,
        TK_INC,
        TK_PREINC,
        TK_ADDEQU,
        TK_SUB,
        TK_DEC,
        TK_PREDEC,
        TK_SUBEQU,
        TK_MUL,
        TK_MULEQU,
        TK_DIV,
        TK_DIVEQU,
        TK_MOD,
        TK_MODEQU,
        TK_POW,
        TK_POWEQU,

        TK_GREATER,
        TK_GREATEREQU,
        TK_LESS,
        TK_LESSEQU,

        TK_BITNOT,
        TK_BITAND,
        TK_BITOR,


        TK_IDENT,
        TK_STRING,
        TK_CHAR,
        TK_NUMBER
    }

    private String mLexeme = "";
    private TokenType mType = TK_UNKNOWN;
    private int mLine = -1;
    private int mPos = -1;

    public Token()
    {
        mLexeme = "";
        mType = TK_UNKNOWN;
        mLine = -1;
        mPos = -1;
    }

    public Token(TokenType type, int line, int pos)
    {
        mLexeme = "";
        mType = type;
        mLine = line;
        mPos = pos;
    }

    public String lexeme()
    {
        return mLexeme;
    }

    public void setLexeme(String lexeme)
    {
        mLexeme = lexeme;
    }

    public void setLexeme(char c)
    {
        mLexeme = Character.toString(c);
    }

    public TokenType type()
    {
        return mType;
    }

    public void setType(TokenType type)
    {
        mType = type;
    }

    public int line()
    {
        return mLine;
    }

    public void setLine(int line)
    {
        mLine = line;
    }

    public int pos()
    {
        return mPos;
    }

    public void setPos(int pos)
    {
        mPos = pos;
    }

    public boolean isType()
    {
        switch (mType)
        {
            case TK_INT:
            case TK_FLOAT:
            case TK_DOUBLE:
            case TK_STRING_TYPE:
            case TK_CHAR_TYPE:
            case TK_BOOL:
            case TK_U8:
            case TK_U16:
            case TK_U32:
            case TK_U64:
            case TK_U128:
            case TK_S8:
            case TK_S16:
            case TK_S32:
            case TK_S64:
            case TK_S128:
            {
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    public boolean isOp()
    {
        switch (mType)
        {
            case TK_ADD:
            case TK_ADDEQU:
            case TK_SUB:
            case TK_SUBEQU:
            case TK_MUL:
            case TK_MULEQU:
            case TK_DIV:
            case TK_DIVEQU:
            case TK_MOD:
            case TK_MODEQU:
            case TK_INC:
            case TK_DEC:
            case TK_ASSIGN:
            case TK_EQU:
            case TK_POW:
            case TK_POWEQU:
            case TK_NOT:
            case TK_NOTEQU:
            case TK_LESS:
            case TK_LESSEQU:
            case TK_GREATER:
            case TK_GREATEREQU:
            case TK_AND:
            case TK_OR:
            case TK_BITAND:
            case TK_BITOR:
            {
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    public boolean isBinOp()
    {
        switch (mType)
        {
            case TK_ADD:
            case TK_ADDEQU:
            case TK_SUB:
            case TK_SUBEQU:
            case TK_MUL:
            case TK_MULEQU:
            case TK_DIV:
            case TK_DIVEQU:
            case TK_MOD:
            case TK_MODEQU:
            case TK_ASSIGN:
            case TK_EQU:
            case TK_POW:
            case TK_POWEQU:
            case TK_NOTEQU:
            case TK_LESS:
            case TK_LESSEQU:
            case TK_GREATER:
            case TK_GREATEREQU:
            case TK_AND:
            case TK_OR:
            case TK_BITAND:
            case TK_BITOR:
            {
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    public boolean isOpPrePostfix()
    {
        switch (mType)
        {
            case TK_INC:
            case TK_DEC:
            {
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    public boolean isUnaryOp()
    {
        switch (mType)
        {
            case TK_INC:
            case TK_DEC:
            case TK_NOT:
            {
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    public int getPrecedence()
    {
        if (!isOp())
        {
            return -1;
        }

        switch (mType)
        {
            case TK_INC:
            case TK_DEC:
            {
                return 100;
            }

            case TK_PREINC:
            case TK_PREDEC:
            case TK_NOT:
            case TK_BITNOT:
            {
                return 90;
            }

            case TK_MUL:
            case TK_DIV:
            case TK_MOD:
            {
                return 80;
            }

            case TK_ADD:
            case TK_SUB:
            {
                return 70;
            }

            case TK_LESS:
            case TK_LESSEQU:
            case TK_GREATER:
            case TK_GREATEREQU:
            {
                return 60;
            }

            case TK_EQU:
            case TK_NOTEQU:
            {
                return 50;
            }

            case TK_BITAND:
            {
                return 40;
            }

            case TK_BITOR:
            {
                return 30;
            }

            case TK_AND:
            {
                return 20;
            }

            case TK_OR:
            {
                return 10;
            }

            case TK_ASSIGN:
            case TK_ADDEQU:
            case TK_SUBEQU:
            case TK_MULEQU:
            case TK_DIVEQU:
            case TK_MODEQU:
            {
                return 9;
            }

            case TK_COMMA:
            {
                return 1;
            }
        }

        return -1;
    }

    public int getNumBits()
    {
        switch (mType)
        {
            case TK_S8:
            case TK_U8:
            case TK_CHAR:
            {
                return 8;
            }

            case TK_S16:
            case TK_U16:
            {
                return 16;
            }

            case TK_S32:
            case TK_U32:
            case TK_INT:
            case TK_FLOAT:
            {
                return 32;
            }

            case TK_S64:
            case TK_U64:
            case TK_DOUBLE:
            {
                return 64;
            }

            case TK_S128:
            case TK_U128:
            {
                return 128;
            }

            default:
            {
                return -1;
            }
        }
    }

    public boolean isSigned()
    {
        switch (mType)
        {
            case TK_U8:
            case TK_U16:
            case TK_U32:
            case TK_U64:
            case TK_U128:
            case TK_CHAR:
            {
                return false;
            }

            case TK_S8:
            case TK_S16:
            case TK_S32:
            case TK_S64:
            case TK_S128:
            case TK_INT:
            case TK_FLOAT:
            case TK_DOUBLE:
            {
                return true;
            }

            default:
            {
                return true;
            }
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Token token = (Token) o;
        return mLine == token.mLine &&
                mPos == token.mPos &&
                Objects.equals(mLexeme, token.mLexeme) &&
                mType == token.mType;
    }

    @Override
    public String toString()
    {
        return "Token{"     +
                "mLexeme='" + mLexeme + '\'' +
                ", mType="  + mType   +
                ", mLine="  + mLine   +
                ", mPos="   + mPos    +
                '}';
    }

    public String print()
    {
        return "<" + mType + ", '" + mLexeme + "'>";
    }
}
