/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file takes the token list and emits a list of nodes that represent the code.
 */

package edu.srjc.burwell.skyler.lang;

import edu.srjc.burwell.skyler.lang.Token.TokenType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static edu.srjc.burwell.skyler.lang.CompilerSettings.*;
import static edu.srjc.burwell.skyler.lang.ConsoleHelper.*;
import static edu.srjc.burwell.skyler.lang.Node.NodeType.*;
import static edu.srjc.burwell.skyler.lang.StringHelper.*;
import static edu.srjc.burwell.skyler.lang.Token.TokenType.*;

public class Parser
{
    private Lexer mLexer = null;
    private Token mLookahead = null;
    private Token mCurrentToken = null;
    private int mLookaheadLine = -1;
    private int mLine = -1;
    private int mLookaheadPos = -1;
    private int mPos = -1;

    private ArrayList<Node> mNodes = null;

    private Scope mGlobalScope = null;
    private Scope mCurrentScope = null;

    private final int NotInAFunction = 0;
    private final int InAFunction = 1;
    private final int InAFunctionFunction = 2;

    private int mFunctionLevel = NotInAFunction;

    private boolean mShowTokens = true;

    private boolean mFinishedWithErrors = false;
    private boolean mInMacro = false;
    private boolean mInGlobal = false;

    private String mCurrentFunction = "";

    private boolean mFoundStart = false;

    private ArrayList<String> mIncludes = null;

    private void setup()
    {
        mLookaheadLine = 0;
        mLine = 0;
        mLookaheadPos = 0;
        mPos = 0;
        mNodes = new ArrayList<Node>();
        mFunctionLevel = NotInAFunction;
        mShowTokens = true;
        mGlobalScope = new Scope();
        mCurrentScope = mGlobalScope;
        mFinishedWithErrors = false;
        mInGlobal = false;
        mInGlobal = false;
        mCurrentFunction = "";
        mIncludes = new ArrayList<String>();
    }

    public Parser(Lexer lexer)
    {
        setup();
        mLexer = lexer;

        mGlobalScope.setID(0);

        if (mLexer.getSourceFile() != null)
        {
            mIncludes.add(mLexer.getSourceFile().path());
        }
    }

    public ArrayList<Node> getNodes()
    {
        return mNodes;
    }

    private void setIncludes(ArrayList<String> includes)
    {
        mIncludes = includes;
    }

    private ArrayList<String> getIncludes()
    {
        return mIncludes;
    }

    private void firstEat()
    {
        mLookahead = mLexer.nextToken();
        mLookaheadLine = mLexer.line();
        mLookaheadPos = mLexer.linePos();

        eat();
    }

    private void eat()
    {
        mCurrentToken = mLookahead;
        mLine = mLookaheadLine;
        mPos = mLookaheadPos;

        mLookahead = mLexer.nextToken();
        mLookaheadLine = mLexer.line();
        mLookaheadPos = mLexer.linePos();
    }

    public void eat(int amount)
    {
        for (int i = 0; i < amount; ++i)
        {
            eat();
        }
    }

    private void errInit()
    {
        mFinishedWithErrors = true;

        print(mLexer.getSourceFile().name() + ":L" + mCurrentToken.line() + ",C" + mCurrentToken.pos() + ": ");
        setConsoleColor(ErrorColor);
        print("Parse error: ");
        setConsoleColor(TextColor);
    }

    private void warnInit()
    {
        print(mLexer.getSourceFile().name() + ":L" + mCurrentToken.line() + ",C" + mCurrentToken.pos() + ": ");
        setConsoleColor(WarningColor);
        print("Warning ");
        setConsoleColor(TextColor);
    }

    private void printLineWithError()
    {
        String line = "";

        try
        {
            line = Files.readAllLines(Paths.get(mLexer.getSourceFile().path())).get(mCurrentToken.line() - 1);
        }
        catch (Exception ex)
        {
            setConsoleColor(ErrorColor);
            print("Error ");
            setConsoleColor(TextColor);
            print("unable to open '" + mLexer.getSourceFile().path() + "'.");

            return;
        }

        println(line);
        setConsoleColor(GreenColor);

        int i = 0;
        for (i = 0; i < mCurrentToken.pos() - 1; ++i)
        {
            print("~");
        }

        println("^");
        setConsoleColor(TextColor);
    }

    private Node parseError(String expected, String after)
    {
        errInit();
        print(" expected ");
        setConsoleColor(PurpleColor);
        print("'" + expected + "'");
        setConsoleColor(TextColor);
        print(" after ");
        setConsoleColor(PurpleColor);
        print( "\"" + after + "\"");
        setConsoleColor(TextColor);
        print(" got ");
        setConsoleColor(PurpleColor);
        print(mCurrentToken.print());
        setConsoleColor(TextColor);
        println(" instead.");
        printLineWithError();

        return null;
    }

    private Node parseErr(String... errors)
    {
        errInit();

        for (String error : errors)
        {
            print(error);
        }

        ln();
        printLineWithError();

        return null;
    }

    private Node error(String error, String token)
    {
        errInit();
        println(error + " \"" + token + "\".");
        printLineWithError();

        return null;
    }

    private Node error(String error)
    {
        errInit();
        println("error on line " + mLine + ": " + error);
        printLineWithError();

        return null;
    }

    private void warning(String warn)
    {
        warnInit();
        println("on line " + mLine + ": " + warn);
        printLineWithError();
    }

    public ArrayList<Node> parse()
    {
        if (mLexer.getSourceFile() == null)
        {
            return null;
        }

        firstEat();

        while (mCurrentToken.type() != TK_UNKNOWN)
        {
            Node expr = parsePrimary();

            if (expr == null)
            {
                if (CompilerSettings.instance().getVerbosity() >= Some)
                {
                    println("\nEnd of parse tree.");
                }

                return mNodes;
            }

            if (expr.nodeType() != EmptyNodeType)
            {
                mNodes.add(expr);
            }
        }

        return mNodes;
    }

    private void getTypeForVarVal(VarNode varNode)
    {
        if (varNode.getValue() == null)
        {
            return;
        }

        if (varNode.getValue().nodeType() == EmptyNodeType)
        {
            varNode.setType(new TypeNode("int"));
            varNode.setValue(new IntNode(DefaultNumBits, DefaultIntValue, DefaultSigned));
            return;
        }

        varNode.setType(getTypeForNode(varNode.getValue()));
    }

    private Node getTypeForNode(Node node)
    {
        switch (node.nodeType())
        {
            case EmptyNodeType:
            case TypelessNodeType:
            case IntNodeType:
            {
                return new TypeNode("int");
            }

            case FloatNodeType:
            {
                return new TypeNode("float");
            }

            case DoubleNodeType:
            {
                return new TypeNode("double");
            }

            case StringNodeType:
            {
                return new TypeNode("String");
            }

            case CharNodeType:
            {
                return new TypeNode("char");
            }

            case BoolNodeType:
            {
                return new TypeNode("bool");
            }

            case CallNodeType:
            {
                if (mGlobalScope.containsSymbol(((CallNode)node).getCallee()))
                {
                    return mGlobalScope.getSymbolTable().returnTypeForSymbol(((CallNode)node).getCallee());
                }

                return new TypelessNode();
            }

            //TODO: Right now it uses lhs, should it take into consideration both types if different?
            case BinaryNodeType:
            {
                return getTypeForNode(((BinaryNode)node).getLHS());
            }

            case VariableNodeType:
            {
                String name = ((VariableNode)node).getName();
                return mCurrentScope.getSymbolTable().typeForSymbol(name);
            }
        }

        return new TypelessNode();
    }

    private Node parsePrimary()
    {
        if (mCurrentToken.type() == TK_UNKNOWN)
        {
            return null;
        }

        if (mCurrentToken.type() == TK_SEMICOLON)
        {
            eat();
            return new EmptyNode();
        }

        if (mCurrentToken.isType())
        {
            return new TypeNode(mCurrentToken.lexeme());
        }

        if (mCurrentToken.isUnaryOp())
        {
            return parseUnary();
        }

        switch (mCurrentToken.type())
        {
            case TK_IDENT:
                return parseIdent();
            case TK_NUMBER:
                return parseNumber();
            case TK_STRING:
                return parseString();
            case TK_CHAR:
                return parseChar();
            case TK_RETURN:
                return parseReturn();
            case TK_IF:
                return parseIfExpr();
            case TK_LPAREN:
                return parseParen();
            case TK_VAR:
                return parseVarVal(true);
            case TK_VAL:
                return parseVarVal(false);
            case TK_LBRACKET:
                return parseArray();
            case TK_WHILE:
                return parseWhileUntil(true);
            case TK_UNTIL:
                return parseWhileUntil(false);
            case TK_DO:
                return parseDo();
            case TK_REPEAT:
                return parseRepeat();
            case TK_FOR:
                return parseFor();
            case TK_FOREVER:
                return parseForever();
            case TK_LBRACE:
                return parseBlock();
            case TK_LABEL:
                return parseLabel();
            case TK_USE:
                return parseUse();
            case TK_MACRO:
                return parseMacro();
            case TK_TRUE:
                return parseBool(true);
            case TK_FALSE:
                return parseBool(false);
            default:
            {
                //Do nothing.
            }
        }

        return error("ParsePrimary: Unknown token", mCurrentToken.lexeme());
    }

    private Node parseUnary()
    {
        if (!mCurrentToken.isOp())
        {
            return parsePrimary();
        }

        if (mCurrentToken.type() == TK_INC)
        {
            eat();
            return new PreIncNode(parseUnary());
        }
        else if (mCurrentToken.type() == TK_DEC)
        {
            eat();
            return new PreDecNode(parseUnary());
        }

        TokenType opcode = mCurrentToken.type();
        eat();

        Node operand = parseUnary();

        if (operand != null)
        {
            return new UnaryNode(opcode, operand);
        }

        return null;
    }

    private Node parseBinOpRhs(int exprPrecedence, Node lhs)
    {
        for (;;)
        {
            int tokenPrec = mCurrentToken.getPrecedence();

            if (tokenPrec < exprPrecedence)
            {
                return lhs;
            }

            Token binOp = mCurrentToken;
            eat();

            //If the expression is x++ it will interpret '++' as a binary op and will try to use the next token, which could be '}'

            if (binOp.isOpPrePostfix())
            {
                if (binOp.type() == TK_INC)
                {
                    return new PostIncNode(lhs);
                }
                else if (binOp.type() == TK_DEC)
                {
                    return new PostDecNode(lhs);
                }
            }

            Node rhs = parsePrimary();
            if (rhs == null)
            {
                return null;
            }

            int nextPrec = mCurrentToken.getPrecedence();
            if (tokenPrec < nextPrec)
            {
                rhs = parseBinOpRhs(tokenPrec + 1, rhs);
                if (rhs == null)
                {
                    return null;
                }
            }

            lhs = new BinaryNode(binOp, lhs, rhs);
        }
    }

    private Node parseExpression()
    {
        Node lhs = parsePrimary();

        if (lhs == null)
        {
            return null;
        }

        return parseBinOpRhs(0, lhs);
    }

    private BlockNode parseBlock()
    {
        //eat the '{'
        eat();

        ArrayList<Node> statements = new ArrayList<Node>();

        while (mCurrentToken.type() != TK_RBRACE)
        {
            Node node = parsePrimary();
            if (node == null)
            {
                return null;
            }

            statements.add(node);
        }

        //eat the '}'
        eat();

        return new BlockNode(statements);
    }

    private Node parseParen()
    {
        //eat the '('
        eat();

        Node node = parseExpression();
        if (node == null)
        {
            return null;
        }

        if (mCurrentToken.type() != TK_RPAREN)
        {
            return error("Expected ')'");
        }

        //eat the ')'
        eat();

        return node;
    }

    private Node parseIdent()
    {
        String name = mCurrentToken.lexeme();

        if (mLookahead.type() == TK_ASSIGN)
        {
            if (mCurrentScope.getSymbolTable().symbolForName(name).isConst())
            {
                return parseErr(name + " is read only and cannot be modified");
            }
        }

        if (mLookahead.isBinOp())
        {
            eat();
            return parseBinOpRhs(mCurrentToken.getPrecedence(), new VariableNode(name));
        }

        eat();

        if (mCurrentToken.type() != TK_LPAREN && mCurrentToken.type() != TK_COLON && !mCurrentToken.isOpPrePostfix())
        {
            return new VariableNode(name);
        }

        if (mCurrentToken.isOp())
        {
            if (mCurrentToken.type() == TK_INC)
            {
                eat();
                return new PostIncNode(new VariableNode(name));
            }
            else if (mCurrentToken.type() == TK_DEC)
            {
                eat();
                return new PostDecNode(new VariableNode(name));
            }
        }

        // name : type
        // assume it's a var if ':' is after the ident
        //TODO: make this work only if it is in a function prototype or a for loop or other relevant areas
        if (mCurrentToken.type() == TK_COLON)
        {
            eat();
            Node type = parsePrimary();
            eat();

            if (mCurrentToken.type() == TK_ASSIGN)
            {
                eat();
                Node value = parsePrimary();
                return new VarNode(name, type, value, true);
            }
            else
            {
                return new VarNode(name, type, new EmptyNode(), true);
            }
        }

        // func(name: type, name: type): type {
        //     code
        // }

        // Get the args for call or for function.
        ArrayList<Node> args = new ArrayList<Node>();
        eat();

        while (mCurrentToken.type() != TK_RPAREN)
        {
            if (mCurrentToken.type() == TK_LABEL)
            {
                eat();
            }

            Node arg = parseExpression();
            if (arg != null)
            {
                args.add(arg);
            }
            else
            {
                return null;
            }

            if (mCurrentToken.type() == TK_RPAREN)
            {
                break;
            }

            if (mCurrentToken.type() != TK_COMMA)
            {
                return error("Expected ',' or ')' in argument list.");
            }

            eat();
        }

        eat();

        // if '{' or ':' then it is a function not a call
        // parseFunction (for cmd+f)

        Node returnType = null;

        if (mCurrentToken.type() == TK_LBRACE)
        {
            returnType = new TypeNode("void");
        }

        if (mCurrentToken.type() == TK_LBRACE || mCurrentToken.type() == TK_COLON)
        {
            mCurrentFunction = name;

            if (++mFunctionLevel > InAFunctionFunction)
            {
                return error("Can't have a function in a function in a function!");
            }

            Scope functionScope = new Scope();
            mCurrentScope.addScope(functionScope);
            functionScope.setID(mGlobalScope.numScopes());

            Symbol functionSymbol = new Symbol();
            functionSymbol.setName(name);
            functionSymbol.setFunction(true);

            Scope previousScope = mCurrentScope;
            mCurrentScope = functionScope;

            for (Node node : args)
            {
                Symbol symbol = new Symbol();

                if (node.nodeType() == VarNodeType)
                {
                    VarNode varNode = (VarNode) node;
                    symbol.setName(varNode.getName());
                    symbol.setType(varNode.getType());
                    symbol.setConst(!varNode.isVar());
                }

                mCurrentScope.addSymbol(symbol);
            }

            if (mCurrentToken.type() == TK_COLON)
            {
                eat();
                if (mCurrentToken.type() == TK_IDENT || mCurrentToken.isType())
                {
                    returnType = new TypeNode(mCurrentToken.lexeme());
                    eat();
                }
                else if (mCurrentToken.type() == TK_LBRACKET)
                {
                    Node arrayType = parseArray();
                    if (arrayType == null)
                    {
                        return null;
                    }

                    returnType = arrayType;
                    eat();
                }
            }

            MultiTypeNode type = new MultiTypeNode();
            for (Node node : args)
            {
                if (node.nodeType() == VarNodeType)
                {
                    type.addType(node);
                }
            }

            type.addType(returnType);

            functionSymbol.setType(type);
            functionSymbol.setReturnType(returnType);

            previousScope.addSymbol(functionSymbol);

            if (!mFoundStart)
            {
                mFoundStart = name.equals("start");
            }

            PrototypeNode prototype = new PrototypeNode(name, args, returnType);
            BlockNode body = parseBlock();
            if (body == null)
            {
                return null;
            }

            --mFunctionLevel;
            mCurrentScope = previousScope;
            mCurrentFunction = "";

            return new FunctionNode(prototype, body);
        }

        return new CallNode(name, args);
    }

    private Node parseNumber()
    {
        String num = mCurrentToken.lexeme();
        int numBits = mCurrentToken.getNumBits();
        boolean isSigned = mCurrentToken.isSigned();

        eat();

        if (num.contains("d") || num.contains("D"))
        {
            try
            {
                return new DoubleNode(numBits, Double.parseDouble(num), isSigned, num.contains("."));
            }
            catch (Exception ex)
            {
                if (CompilerSettings.instance().getVerbosity() >= Some)
                {
                    ex.printStackTrace();
                }

                return error("Expected a valid 'double' got", num);
            }
        }
        else if (num.contains("f") || num.contains("F") || num.contains("."))
        {
            try
            {
                return new FloatNode(numBits, Float.parseFloat(num), isSigned, num.contains("."));
            }
            catch (Exception ex)
            {
                if (CompilerSettings.instance().getVerbosity() >= Some)
                {
                    ex.printStackTrace();
                }

                return error("Expected a valid 'float' got", num);
            }
        }

        try
        {
            return new IntNode(numBits, Integer.parseInt(num), isSigned);
        }
        catch (Exception ex)
        {
            if (CompilerSettings.instance().getVerbosity() >= Some)
            {
                ex.printStackTrace();
            }

            return error("Expected a valid 'int' got", num);
        }
    }

    private Node parseString()
    {
        if (inGlobalScope() && !mInMacro)
        {
            return parseErr("Can't have a String in the global scope!");
        }

        String txt = mCurrentToken.lexeme();
        eat();

        return new StringNode(txt);
    }

    private Node parseChar()
    {
        if (inGlobalScope() && !mInMacro)
        {
            return parseErr("Can't have a char in the global scope!");
        }

        char c = mCurrentToken.lexeme().charAt(0);
        eat();

        return new CharNode(c);
    }

    private Node parseReturn()
    {
        eat();

        Node returnStatement = parseExpression();
        if (returnStatement == null)
        {
            return null;
        }

        return new ReturnNode(returnStatement);
    }

    private Node parseIfExpr()
    {
        eat();

        if (inGlobalScope())
        {
            return parseErr("Can't have an if statement in the global scope!");
        }

        if (mCurrentToken.type() != TK_LPAREN)
        {
            return parseError("(", "if");
        }

        Node condition = parseExpression();
        if (condition == null)
        {
            return null;
        }

        if (mCurrentToken.type() != TK_LBRACE)
        {
            return parseError("{", ")");
        }

        BlockNode body = parseBlock();
        if (body == null)
        {
            return null;
        }

        Node other = null;
        boolean isElseIf = false;

        if (mCurrentToken.type() == TK_ELSE)
        {
            eat();

            if (mCurrentToken.type() == TK_IF)
            {
                other = parseIfExpr();
                if (other == null)
                {
                    return null;
                }

                isElseIf = true;
            }
            else if (mCurrentToken.type() == TK_LBRACE)
            {
                other = parseElse();
            }
            else
            {
                return parseError("if or {", "else");
            }
        }
        else if (mCurrentToken.type() == TK_ELIF)
        {
            other = parseIfExpr();
            if (other == null)
            {
                return null;
            }

            isElseIf = true;
        }

        return new IfNode(condition, body, other, isElseIf);
    }

    private Node parseElse()
    {
        BlockNode body = parseBlock();
        if (body == null)
        {
            return null;
        }

        return new ElseNode(body);
    }

    private Node parseArray()
    {
        return error("Not yet implemented!");
    }

    private Node parseVarVal(boolean var)
    {
        eat();

        Node ret = null;

        String name = "";
        Node type = null;
        Node value = null;

        String after = var ? "var" : "val";

        if (inGlobalScope())
        {
            return parseErr("Can't create a \"" + after + "\" in the global scope!");
        }

        Symbol symbol = new Symbol();
        symbol.setConst(!var);

        if (mCurrentToken.type() == TK_IDENT)
        {
            name = mCurrentToken.lexeme();
            symbol.setName(name);
            eat();

            if (mCurrentToken.type() == TK_COLON)
            {
                eat();

                if (mCurrentToken.isType() || mCurrentToken.type() == TK_IDENT || mCurrentToken.type() == TK_LBRACKET)
                {
                    type = parsePrimary();
                    symbol.setType(type);

                    eat();
                    if (mCurrentToken.type() == TK_ASSIGN)
                    {
                        eat();

                        if (mCurrentToken.type() == TK_NUMBER || mCurrentToken.type() == TK_IDENT || mCurrentToken.type() == TK_STRING
                                || mCurrentToken.type() == TK_TRUE || mCurrentToken.type() == TK_FALSE || mCurrentToken.type() == TK_CHAR
                                || mCurrentToken.type() == TK_LBRACKET || mCurrentToken.type() == TK_MACRO)
                        {
                            value = parsePrimary();

                            //ret = new VarNode(name, type, value, var);
                        }
                        else
                        {
                            return parseError("number or identifier", "=");
                        }
                    }
                }
                else
                {
                    return  parseError("type", ":");
                }
            }
            else if (mCurrentToken.type() == TK_ASSIGN)
            {
                eat();

                if (mCurrentToken.type() == TK_NUMBER || mCurrentToken.type() == TK_IDENT || mCurrentToken.type() == TK_STRING
                        || mCurrentToken.type() == TK_TRUE || mCurrentToken.type() == TK_FALSE || mCurrentToken.type() == TK_CHAR
                        || mCurrentToken.type() == TK_LBRACKET || mCurrentToken.type() == TK_MACRO)
                {
                    value = parsePrimary();

                    //ret = new VarNode(name, type, value, var);
                }
                else
                {
                    return parseError("number or identifier", "=");
                }
            }
            else if ((mLookaheadLine != mCurrentToken.line()) || mCurrentToken.type() == TK_SEMICOLON)
            {
                ret = new VarNode(name, type, value, var);
            }
            else
            {
                return parseError(": or =", name);
            }
        }
        else
        {
            return parseError("identifier", after);
        }

        ret = new VarNode(name, type, value, var);

        if (type == null)
        {
            getTypeForVarVal((VarNode)ret);
            symbol.setType(((VarNode)ret).getType());
        }

        if (mCurrentScope.containsSymbol(name))
        {
            return parseErr("Error: Symbol \"" + name + "\" is already in the symbol table!");
        }
        else
        {
            mCurrentScope.addSymbol(symbol);
        }

        return ret;
    }

    private Node parseWhileUntil(boolean isWhile)
    {
        String loopType = isWhile ? "while" : "until";

        if (inGlobalScope())
        {
            if (isWhile)
            {
                return parseErr("Can't have a while loop in the global scope!");
            }
            else
            {
                return parseErr("Can't have an until loop in the global scope!");
            }
        }

        eat();

        if (mCurrentToken.type() != TK_LPAREN)
        {
            return parseError("(", loopType);
        }

        Node condition = parseParen();
        if (condition == null)
        {
            return null;
        }

        if (mCurrentToken.type() != TK_LBRACE)
        {
            return parseError("{", ")");
        }

        BlockNode body = parseBlock();
        if (body == null)
        {
            return null;
        }

        if (isWhile)
        {
            return new WhileNode(condition, body);
        }
        else
        {
            return new UntilNode(condition, body);
        }
    }

    private Node parseDo()
    {
        eat();

        if (inGlobalScope())
        {
            return parseErr("Can't have a do loop in the global scope!");
        }

        BlockNode body = parseBlock();
        boolean isWhile = true;

        if (mCurrentToken.type() == TK_WHILE)
        {
            isWhile = true;
        }
        else if (mCurrentToken.type() == TK_UNTIL)
        {
            isWhile = false;
        }
        else
        {
            return parseError("while or until", "}");
        }

        eat();

        if (mCurrentToken.type() != TK_LPAREN)
        {
            if (isWhile)
            {
                return parseError("(", "while");
            }
            else
            {
                return parseError("(", "until");
            }
        }

        Node condition = parseParen();
        if (condition == null)
        {
            return null;
        }

        return new DoNode(condition, body, isWhile);
    }

    private Node parseRepeat()
    {
        eat();

        if (inGlobalScope())
        {
            return parseErr("Can't have a repeat loop in the global scope!");
        }

        if (mCurrentToken.type() != TK_LPAREN)
        {
            return parseError("(", "repeat");
        }

        //TODO: Check type, value
        Node condition = parseParen();
        if (condition == null)
        {
            return null;
        }

        if (mCurrentToken.type() != TK_LBRACE)
        {
            return parseError("{", ")");
        }

        BlockNode body = parseBlock();
        if (body == null)
        {
            return null;
        }

        return new RepeatNode(condition, body);
    }

    private Node parseFor()
    {
        eat();

        if (inGlobalScope())
        {
            return parseErr("Can't have a for loop in the global scope!");
        }

        if (mCurrentToken.type() != TK_LPAREN)
        {
            return parseError("(", "for");
        }

        eat();

        //This must be a range.
        //For now start, stop, and step must be known at compile time.
        if (mCurrentToken.type() == TK_NUMBER)
        {
            return parseForRange();
        }

        if (mCurrentToken.type() == TK_IDENT)
        {
            String name = mCurrentToken.lexeme();
            Node start = parsePrimary();
            if (start == null)
            {
                return null;
            }

            if (mCurrentToken.type() == TK_IN)
            {
                eat();

                ForRangeNode forRangeNode = (ForRangeNode)parseForRange();
                if (forRangeNode == null)
                {
                    return null;
                }

                forRangeNode.setName(name);

                return forRangeNode;
            }
            else
            {
                if (mCurrentToken.type() != TK_SEMICOLON)
                {
                    return parseError(";", "for_start");
                }

                Node condition = parsePrimary();
                if (condition == null)
                {
                    return null;
                }

                if (mCurrentToken.type() != TK_SEMICOLON)
                {
                    return parseError(";", "for_cond");
                }

                Node expression = parsePrimary();
                if (expression == null)
                {
                    return null;
                }

                if (mCurrentToken.type() != TK_RPAREN)
                {
                    return parseError(")", "for_expr");
                }

                eat();

                if (mCurrentToken.type() != TK_LBRACE)
                {
                    return parseError("{", ")");
                }

                BlockNode body = parseBlock();
                if (body == null)
                {
                    return null;
                }

                return new ForNode(start, condition, expression, body);
            }
        }
        else
        {
            Node start = parsePrimary();
            if (start == null)
            {
                return null;
            }

            if (mCurrentToken.type() != TK_SEMICOLON)
            {
                return parseError(";", "for_start");
            }

            eat();
            Node condition = parsePrimary();
            if (condition == null)
            {
                return null;
            }

            if (mCurrentToken.type() != TK_SEMICOLON)
            {
                return parseError(";", "for_cond");
            }

            eat();
            Node expression = parsePrimary();
            if (expression == null)
            {
                return null;
            }

            if (mCurrentToken.type() != TK_RPAREN)
            {
                return parseError(")", "for_expr");
            }

            eat();

            if (mCurrentToken.type() != TK_LBRACE)
            {
                return parseError("{", ")");
            }

            BlockNode body = parseBlock();
            if (body == null)
            {
                return  null;
            }

            return new ForNode(start, condition, expression, body);
        }
    }

    private Node parseForRange()
    {
        Node start = null;
        Node stop = null;
        Node step = new EmptyNode();

        start = parsePrimary();
        if (start == null)
        {
            return null;
        }

        if (mCurrentToken.type() != TK_RANGE)
        {
            return parseError("..", "range_start");
        }

        eat();

        if (mCurrentToken.type() != TK_NUMBER && mCurrentToken.type() != TK_IDENT)
        {
            return parseError("range_end", "..");
        }

        stop = parsePrimary();
        if (stop == null)
        {
            return null;
        }

        if (mCurrentToken.type() == TK_STEP)
        {
            eat();
            if (mCurrentToken.type() != TK_NUMBER && mCurrentToken.type() != TK_IDENT)
            {
                return parseError("range_step", "step");
            }

            step = parsePrimary();
            if (step == null)
            {
                return null;
            }

            if (mCurrentToken.type() != TK_RPAREN)
            {
                return parseError(")", "number");
            }

            eat();
        }
        else if (mCurrentToken.type() == TK_RPAREN)
        {
            //step = 1
            eat();
        }
        else
        {
            return parseError("step or )", "number");
        }

        if (mCurrentToken.type() != TK_LBRACE)
        {
            return parseError("{", ")");
        }

        BlockNode body = parseBlock();
        if (body == null)
        {
            return null;
        }

        //"i" is the default name for the index variable.
        return new ForRangeNode(start, stop, step, body, "i");
    }

    private Node parseForever()
    {
        eat();

        if (inGlobalScope())
        {
            return parseErr("Can't have a forever loop in the global scope!");
        }

        if (mCurrentToken.type() != TK_RBRACE)
        {
            return parseError("{", "forever");
        }

        BlockNode body = parseBlock();
        if (body == null)
        {
            return null;
        }

        return new ForeverNode(body);
    }

    private Node parseLabel()
    {
        String name = mCurrentToken.lexeme();
        eat();

        return new LabelNode(name);
    }

    private Node parseUse()
    {
        if (!inGlobalScope())
        {
            return parseErr("\"use\" must be used in the global scope!");
        }

        eat();
        if (mCurrentToken.type() != TK_STRING)
        {
            return parseError("String", "use");
        }

        if (mCurrentToken.lexeme().equals(stringBeforeFirst(mLexer.getSourceFile().name(), '.')))
        {
            return parseErr("A file can't use itself!");
        }

        String path = stringBeforeLast(mLexer.getSourceFile().path(), '/');
        String filename = mCurrentToken.lexeme() + ".yava";

        if (!path.isEmpty())
        {
            filename = path + "/" + filename;
        }

        File file = new File(filename);
        if (!file.exists())
        {
            return error("The file \"" + filename + "\" does not exist!");
        }

        if (mIncludes.contains(filename))
        {
            if (CompilerSettings.instance().showWarnings())
            {
                warning("The file \"" + filename + "\" has already been included.");
            }

            eat();

            return new EmptyNode();
        }

        eat();

        mIncludes.add(filename);

        SourceFile sourceFile = new SourceFile(filename);
        Lexer lexer = new Lexer(sourceFile);
        Parser parser = new Parser(lexer);
        parser.setIncludes(mIncludes);
        parser.parse();

        if (parser.finishedWithErrors())
        {
            mFinishedWithErrors = true;
            return null;
        }

        mNodes.addAll(parser.getNodes());

        for (String include : parser.getIncludes())
        {
            if (!mIncludes.contains(include))
            {
                mIncludes.add(include);
            }
        }

        return new EmptyNode();
    }

    private Node parseMacro()
    {
        String lexeme = mCurrentToken.lexeme();
        eat();

        switch (lexeme)
        {
            case "file":
                return new StringNode(mLexer.getSourceFile().name());
            case "path":
                return new StringNode(mLexer.getSourceFile().path());
            case "line":
                return new IntNode(DefaultNumBits, mCurrentToken.line(), false);
            case "function":
                return new StringNode(mCurrentFunction);
            case "version":
                return new StringNode("0.0.0");
            case "build":
                return new StringNode("12");
            case "os":
            {
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win"))
                {
                    return new StringNode("Windows");
                }
                else if (os.contains("mac"))
                {
                    return new StringNode("Mac OS X");
                }
                else if (os.contains("linux"))
                {
                    return new StringNode("linux");
                }
                else
                {
                    return new StringNode("unknown");
                }
            }
            default:
            {
                mInMacro = true;

                if (mCurrentToken.type() == TK_LPAREN)
                {
                    parseParen();
                }

                mInMacro = false;
                return new EmptyNode();
            }
        }
    }

    private Node parseBool(boolean value)
    {
        eat();
        return new BoolNode(value);
    }

    public boolean finishedWithErrors()
    {
        return mFinishedWithErrors;
    }

    public boolean foundStart()
    {
        return mFoundStart;
    }

    private boolean inGlobalScope()
    {
        return mCurrentScope.equals(mGlobalScope);
    }
}
