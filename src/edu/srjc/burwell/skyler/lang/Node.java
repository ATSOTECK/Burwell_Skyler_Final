/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file contains the data structures that represent the code being compiled.
 */

package edu.srjc.burwell.skyler.lang;

import edu.srjc.burwell.skyler.lang.Token.TokenType;

import java.util.ArrayList;

import static edu.srjc.burwell.skyler.lang.Node.NodeType.*;
import static edu.srjc.burwell.skyler.lang.Token.TokenType.*;

class Node
{
    public enum NodeType
    {
        EmptyNodeType,
        TypelessNodeType,
        MultiTypeNodeType,
        IntNodeType,
        FloatNodeType,
        DoubleNodeType,
        BoolNodeType,
        StringNodeType,
        CharNodeType,
        VariableNodeType,
        BinaryNodeType,
        PostIncNodeType,
        PreIncNodeType,
        PostDecNodeType,
        PreDecNodeType,
        TypeNodeType,
        ArrayNodeType,
        CallNodeType,
        PrototypeNodeType,
        BlockNodeType,
        FunctionNodeType,
        ReturnNodeType,
        IfNodeType,
        ElseNodeType,
        VarNodeType,
        UnaryNodeType,
        WhileNodeType,
        UntilNodeType,
        DoNodeType,
        RepeatNodeType,
        ForNodeType,
        ForRangeNodeType,
        ForeverNodeType,
        LabelNodeType
    }

    NodeType mNodeType = EmptyNodeType;

    public static Node nodeError(String error)
    {
        System.err.println(error);

        return null;
    }

    public NodeType nodeType()
    {
        return mNodeType;
    }
}

class EmptyNode extends Node
{
    public EmptyNode()
    {
        mNodeType = EmptyNodeType;
    }
}

class TypelessNode extends Node
{
    public TypelessNode()
    {
        mNodeType = TypelessNodeType;
    }
}

class MultiTypeNode extends Node
{
    private ArrayList<Node> mTypes = null;

    public MultiTypeNode()
    {
        mNodeType = MultiTypeNodeType;

        mTypes = new ArrayList<Node>();
    }

    public void addType(Node type)
    {
        mTypes.add(type);
    }

    public ArrayList<Node> getTypes()
    {
        return mTypes;
    }

    @Override
    public String toString()
    {
        return "MultiTypeNode{" +
                "mTypes=" + mTypes +
                '}';
    }
}

class IntNode extends Node
{
    private int mNumBits = -1;
    private int mValue = -1;
    private boolean mIsSigned = true;

    public IntNode(int size, int value, boolean signed)
    {
        mNodeType = IntNodeType;

        mNumBits = size;
        mValue = value;
        mIsSigned = signed;
    }

    public int getNumBits()
    {
        return mNumBits;
    }

    public int getValue()
    {
        return mValue;
    }

    public boolean isSigned()
    {
        return mIsSigned;
    }

    @Override
    public String toString()
    {
        return "IntNode{" +
                "mNumBits=" + mNumBits +
                ", mValue=" + mValue +
                ", mIsSigned=" + mIsSigned +
                '}';
    }
}

class FloatNode extends Node
{
    private int mNumBits = -1;
    private float mValue = -1;
    private boolean mIsSigned = true;
    private boolean mHasDecimal = true;

    public FloatNode(int size, float value, boolean signed, boolean hasDecimal)
    {
        mNodeType = FloatNodeType;

        mNumBits = size;
        mValue = value;
        mIsSigned = signed;
        mHasDecimal = hasDecimal;
    }

    public int getNumBits()
    {
        return mNumBits;
    }

    public float getValue()
    {
        return mValue;
    }

    public boolean isSigned()
    {
        return mIsSigned;
    }

    public boolean hasDecimal()
    {
        return mHasDecimal;
    }

    @Override
    public String toString()
    {
        return "FloatNode{" +
                "mNumBits=" + mNumBits +
                ", mValue=" + mValue +
                ", mIsSigned=" + mIsSigned +
                '}';
    }
}

class DoubleNode extends Node
{
    private int mNumBits = -1;
    private double mValue = -1;
    private boolean mIsSigned = true;
    private boolean mHasDecimal = true;

    public DoubleNode(int size, double value, boolean signed, boolean hasDecimal)
    {
        mNodeType = DoubleNodeType;

        mNumBits = size;
        mValue = value;
        mIsSigned = signed;
        mHasDecimal = hasDecimal;
    }

    public int getNumBits()
    {
        return mNumBits;
    }

    public double getValue()
    {
        return mValue;
    }

    public boolean isSigned()
    {
        return mIsSigned;
    }

    public boolean hasDecimal()
    {
        return mHasDecimal;
    }

    @Override
    public String toString()
    {
        return "DoubleNode{" +
                "mNumBits=" + mNumBits +
                ", mValue=" + mValue +
                ", mIsSigned=" + mIsSigned +
                '}';
    }
}

class BoolNode extends Node
{
    private boolean mValue;

    public BoolNode(boolean value)
    {
        mNodeType = BoolNodeType;

        mValue = value;
    }

    public boolean value()
    {
        return mValue;
    }

    @Override
    public String toString()
    {
        return "BoolNode{" +
                "mValue=" + mValue +
                '}';
    }
}

class StringNode extends Node
{
    private String mStr = "";

    public StringNode(String str)
    {
        mNodeType = StringNodeType;

        mStr = str;
    }

    public String getStr()
    {
        return mStr;
    }

    @Override
    public String toString()
    {
        return "StringNode{" +
                "mStr='" + mStr + '\'' +
                '}';
    }
}

class CharNode extends Node
{
    private char mChar = '\0';

    public CharNode(char c)
    {
        mNodeType = CharNodeType;

        mChar = c;
    }

    public char getChar()
    {
        return mChar;
    }

    @Override
    public String toString()
    {
        return "CharNode{" +
                "mChar=" + mChar +
                '}';
    }
}

class VariableNode extends Node
{
    private String mName = "";

    public VariableNode(String name)
    {
        mNodeType = VariableNodeType;

        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    @Override
    public String toString()
    {
        return "VariableNode{" +
                "mName='" + mName + '\'' +
                '}';
    }
}

class BinaryNode extends Node
{
    private Token mOP = null;
    private Node mLHS = null;
    private Node mRHS = null;

    public BinaryNode(Token op, Node lhs, Node rhs)
    {
        mNodeType = BinaryNodeType;

        mOP = op;
        mRHS = rhs;
        mLHS = lhs;
    }

    public Token getOP()
    {
        return mOP;
    }

    public Node getLHS()
    {
        return mLHS;
    }

    public Node getRHS()
    {
        return mRHS;
    }

    @Override
    public String toString()
    {
        return "BinaryNode{" +
                "mOP=" + mOP +
                ", mLHS=" + mLHS +
                ", mRHS=" + mRHS +
                '}';
    }
}

class PostIncNode extends Node
{
    private Node mExpression = null;

    public PostIncNode(Node expression)
    {
        mNodeType = PostIncNodeType;

        mExpression = expression;
    }

    public Node getExpression()
    {
        return mExpression;
    }

    @Override
    public String toString()
    {
        return "PostIncNode{" +
                "mExpression=" + mExpression +
                '}';
    }
}

class PreIncNode extends Node
{
    private Node mExpression = null;

    public PreIncNode(Node expression)
    {
        mNodeType = PreIncNodeType;

        mExpression = expression;
    }

    public Node getExpression()
    {
        return mExpression;
    }

    @Override
    public String toString()
    {
        return "PreIncNode{" +
                "mExpression=" + mExpression +
                '}';
    }
}

class PostDecNode extends Node
{
    private Node mExpression = null;

    public PostDecNode(Node expression)
    {
        mNodeType = PostDecNodeType;

        mExpression = expression;
    }

    public Node getExpression()
    {
        return mExpression;
    }

    @Override
    public String toString()
    {
        return "PostDecNode{" +
                "mExpression=" + mExpression +
                '}';
    }
}

class PreDecNode extends Node
{
    private Node mExpression = null;

    public PreDecNode(Node expression)
    {
        mNodeType = PreDecNodeType;

        mExpression = expression;
    }

    public Node getExpression()
    {
        return mExpression;
    }

    @Override
    public String toString()
    {
        return "PreDecNode{" +
                "mExpression=" + mExpression +
                '}';
    }
}

class TypeNode extends Node
{
    private String mType = "";

    public TypeNode(String type)
    {
        mNodeType = TypeNodeType;

        mType = type;
    }

    public String getType()
    {
        return mType;
    }

    @Override
    public String toString()
    {
        return "TypeNode{" +
                "mType='" + mType + '\'' +
                '}';
    }
}

class ArrayNode extends Node
{
    private Node mType = null;
    private ArrayList<Node> mData = null;

    public ArrayNode(Node type, ArrayList<Node> data)
    {
        mNodeType = ArrayNodeType;

        mType = type;
        mData = data;
    }

    public Node getType()
    {
        return mType;
    }

    public ArrayList<Node> getData()
    {
        return mData;
    }

    @Override
    public String toString()
    {
        return "ArrayNode{" +
                "mType=" + mType +
                ", mData=" + mData +
                '}';
    }
}

class CallNode extends Node
{
    private String mCallee = "";
    private ArrayList<Node> mArgs = null;

    public CallNode(String callee, ArrayList<Node> args)
    {
        mNodeType = CallNodeType;

        mCallee = callee;
        mArgs = args;
    }

    public String getCallee()
    {
        return mCallee;
    }

    public ArrayList<Node> getArgs()
    {
        return mArgs;
    }

    @Override
    public String toString()
    {
        return "CallNode{" +
                "mCallee='" + mCallee + '\'' +
                ", mArgs=" + mArgs +
                '}';
    }
}

class PrototypeNode extends Node
{
    private String mName = "";
    private ArrayList<Node> mArgs = null;
    private Node mReturnType = null;

    public PrototypeNode(String name, ArrayList<Node> args, Node returnType)
    {
        mNodeType = PrototypeNodeType;

        mName = name;
        mArgs = args;
        mReturnType = returnType;
    }

    public String getName()
    {
        return mName;
    }

    public ArrayList<Node> getArgs()
    {
        return mArgs;
    }

    public Node getReturnType()
    {
        return mReturnType;
    }

    @Override
    public String toString()
    {
        return "PrototypeNode{" +
                "mName='" + mName + '\'' +
                ", mArgs=" + mArgs +
                ", mReturnType=" + mReturnType +
                '}';
    }
}

class BlockNode extends Node
{
    private ArrayList<Node> mStatements = null;

    public BlockNode(ArrayList<Node> statements)
    {
        mNodeType = BlockNodeType;

        mStatements = statements;
    }

    public ArrayList<Node> getStatements()
    {
        return mStatements;
    }

    @Override
    public String toString()
    {
        return "BlockNode{" +
                "mStatements=" + mStatements +
                '}';
    }
}

class FunctionNode extends Node
{
    private PrototypeNode mPrototype = null;
    private BlockNode mBody = null;

    public FunctionNode(PrototypeNode prototype, BlockNode body)
    {
        mNodeType = FunctionNodeType;

        mPrototype = prototype;
        mBody = body;
    }

    public PrototypeNode getPrototype()
    {
        return mPrototype;
    }

    public BlockNode getBody()
    {
        return mBody;
    }

    @Override
    public String toString()
    {
        return "FunctionNode{" +
                "mPrototype=" + mPrototype +
                ", mBody=" + mBody +
                '}';
    }
}

class ReturnNode extends Node
{
    private Node mReturnStatement = null;

    public ReturnNode(Node returnStatement)
    {
        mNodeType = ReturnNodeType;

        mReturnStatement = returnStatement;
    }

    public Node getReturnStatement()
    {
        return mReturnStatement;
    }

    @Override
    public String toString()
    {
        return "ReturnNode{" +
                "mReturnStatement=" + mReturnStatement +
                '}';
    }
}

class IfNode extends Node
{
    private Node mCondition = null;
    private BlockNode mCode = null;
    private Node mOther = null;

    //Whether other is elseIf
    private boolean mIsElseIf = true;

    public IfNode(Node condition, BlockNode code, Node other, boolean isElseIf)
    {
        mNodeType = IfNodeType;

        mCondition = condition;
        mCode = code;
        mOther = other;
        mIsElseIf = isElseIf;
    }

    public Node getCondition()
    {
        return mCondition;
    }

    public BlockNode getCode()
    {
        return mCode;
    }

    public Node getOther()
    {
        return mOther;
    }

    public boolean isElseIf()
    {
        return mIsElseIf;
    }

    @Override
    public String toString()
    {
        return "IfNode{" +
                "mCondition=" + mCondition +
                ", mCode=" + mCode +
                ", mOther=" + mOther +
                ", mIsElseIf=" + mIsElseIf +
                '}';
    }
}

class ElseNode extends Node
{
    private BlockNode mCode = null;

    public ElseNode(BlockNode code)
    {
        mNodeType = ElseNodeType;

        mCode = code;
    }

    public BlockNode getCode()
    {
        return mCode;
    }

    @Override
    public String toString()
    {
        return "ElseNode{" +
                "mCode=" + mCode +
                '}';
    }
}

class VarNode extends Node
{
    private String mName = null;
    private Node mType = null;
    private Node mValue = null;
    boolean mIsVar = true;

    public VarNode(String name, Node type, Node value, boolean isVar)
    {
        mNodeType = VarNodeType;

        mName = name;
        mType = type;
        mValue = value;
        mIsVar = isVar;
    }

    public String getName()
    {
        return mName;
    }

    public void setType(Node type)
    {
        mType = type;
    }

    public Node getType()
    {
        return mType;
    }

    public void setValue(Node value)
    {
        mValue = value;
    }

    public Node getValue()
    {
        return mValue;
    }

    public boolean isVar()
    {
        return mIsVar;
    }

    @Override
    public String toString()
    {
        return "VarNode{" +
                "mName='" + mName + '\'' +
                ", mType=" + mType +
                ", mValue=" + mValue +
                ", mIsVar=" + mIsVar +
                '}';
    }
}

class UnaryNode extends Node
{
    private TokenType mOpcode = TK_UNKNOWN;
    private Node mOperand = null;

    public UnaryNode(TokenType opcode, Node operand)
    {
        mNodeType = UnaryNodeType;

        mOpcode = opcode;
        mOperand = operand;
    }

    public TokenType getOpcode()
    {
        return mOpcode;
    }

    public Node getOperand()
    {
        return mOperand;
    }

    @Override
    public String toString()
    {
        return "UnaryNode{" +
                "mOpcode=" + mOpcode +
                ", mOperand=" + mOperand +
                '}';
    }
}

class WhileNode extends Node
{
    private Node mCondition = null;
    private BlockNode mBody = null;

    public WhileNode(Node condition, BlockNode body)
    {
        mNodeType = WhileNodeType;

        mCondition = condition;
        mBody = body;
    }

    public Node getCondition()
    {
        return mCondition;
    }

    public BlockNode getBody()
    {
        return mBody;
    }

    @Override
    public String toString()
    {
        return "WhileNode{" +
                "mCondition=" + mCondition +
                ", mBody=" + mBody +
                '}';
    }
}

class UntilNode extends Node
{
    private Node mCondition = null;
    private BlockNode mBody = null;

    public UntilNode(Node condition, BlockNode body)
    {
        mNodeType = UntilNodeType;

        mCondition = condition;
        mBody = body;
    }

    public Node getCondition()
    {
        return mCondition;
    }

    public BlockNode getBody()
    {
        return mBody;
    }

    @Override
    public String toString()
    {
        return "UntilNode{" +
                "mCondition=" + mCondition +
                ", mBody=" + mBody +
                '}';
    }
}

class DoNode extends Node
{
    private Node mCondition = null;
    private BlockNode mBody = null;
    private boolean mIsWhile = true;

    public DoNode(Node condition, BlockNode body, boolean isWhile)
    {
        mNodeType = DoNodeType;

        mCondition = condition;
        mBody = body;
        mIsWhile = isWhile;
    }

    public Node getCondition()
    {
        return mCondition;
    }

    public BlockNode getBody()
    {
        return mBody;
    }

    public boolean isWhile()
    {
        return mIsWhile;
    }

    @Override
    public String toString()
    {
        return "DoNode{" +
                "mCondition=" + mCondition +
                ", mBody=" + mBody +
                ", mIsWhile=" + mIsWhile +
                '}';
    }
}

class RepeatNode extends Node
{
    private Node mCondition = null;
    private BlockNode mBody = null;

    public RepeatNode(Node condition, BlockNode body)
    {
        mNodeType = RepeatNodeType;

        mCondition = condition;
        mBody = body;
    }

    public Node getCondition()
    {
        return mCondition;
    }

    public BlockNode getBody()
    {
        return mBody;
    }

    @Override
    public String toString()
    {
        return "RepeatNode{" +
                "mCondition=" + mCondition +
                ", mBody=" + mBody +
                '}';
    }
}

class ForeverNode extends Node
{
    private BlockNode mBody = null;

    public ForeverNode(BlockNode body)
    {
        mNodeType = ForeverNodeType;

        mBody = body;
    }

    public BlockNode getBody()
    {
        return mBody;
    }

    @Override
    public String toString()
    {
        return "ForeverNode{" +
                "mBody=" + mBody +
                '}';
    }
}

class ForNode extends Node
{
    private Node mStart = null;
    private Node mCondition = null;
    private Node mExpression = null;
    private BlockNode mBody = null;

    public ForNode(Node start, Node condition, Node expression, BlockNode body)
    {
        mNodeType = ForNodeType;

        mStart = start;
        mCondition = condition;
        mExpression = expression;
        mBody = body;
    }

    public Node getStart()
    {
        return mStart;
    }

    public Node getCondition()
    {
        return mCondition;
    }

    public Node getExpression()
    {
        return mExpression;
    }

    public BlockNode getBody()
    {
        return mBody;
    }

    @Override
    public String toString()
    {
        return "ForNode{" +
                "mStart=" + mStart +
                ", mCondition=" + mCondition +
                ", mExpression=" + mExpression +
                ", mBody=" + mBody +
                '}';
    }
}

class ForRangeNode extends Node
{
    private Node mStart = null;
    private Node mStop = null;
    private Node mStep = null;
    private BlockNode mBody = null;
    private String mName = "";

    public ForRangeNode(Node start, Node stop, Node step, BlockNode body, String name)
    {
        mNodeType = ForRangeNodeType;

        mStart = start;
        mStop = stop;
        mStep = step;
        mBody = body;
        mName = name;
    }

    public Node getStart()
    {
        return mStart;
    }

    public Node getStop()
    {
        return mStop;
    }

    public Node getStep()
    {
        return mStep;
    }

    public BlockNode getBody()
    {
        return mBody;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    @Override
    public String toString()
    {
        return "ForRangeNode{" +
                "mStart=" + mStart +
                ", mStop=" + mStop +
                ", mStep=" + mStep +
                ", mBody=" + mBody +
                ", mName='" + mName + '\'' +
                '}';
    }
}

class LabelNode extends Node
{
    private String mName = "";

    public LabelNode(String name)
    {
        mNodeType = LabelNodeType;

        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    @Override
    public String toString()
    {
        return "LabelNode{" +
                "mName='" + mName + '\'' +
                '}';
    }
}
