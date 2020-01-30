/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file represents symbols for the symbol table.
 */

package edu.srjc.burwell.skyler.lang;

import java.util.Objects;

public class Symbol
{
    private String mName = "";
    private Node mType = null;
    private Node mReturnType = null;
    private boolean mIsConst = false;
    private boolean mIsFunction = false;

    public Symbol()
    {
        mName = "";
        mType = null;
        mReturnType = null;
        mIsConst = false;
        mIsFunction = false;
    }

    public Symbol(String name, Node type, Node returnType, boolean isConst, boolean isFunction)
    {
        mName = name;
        mType = type;
        mReturnType = returnType;
        mIsConst = isConst;
        mIsFunction = isFunction;
    }

    public void setName(String name)
    {
        mName = name;
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

    public void setReturnType(Node returnType)
    {
        mReturnType = returnType;
    }

    public Node getReturnType()
    {
        return mReturnType;
    }

    public void setConst(boolean isConst)
    {
        mIsConst = isConst;
    }

    public boolean isConst()
    {
        return mIsConst;
    }

    public void setFunction(boolean isFunction)
    {
        mIsFunction = isFunction;
    }

    public boolean isFunction()
    {
        return mIsFunction;
    }

    @Override
    public String toString()
    {
        return "Symbol{" +
                "mName='" + mName + '\'' +
                ", mType=" + mType +
                ", mReturnType=" + mReturnType +
                ", mIsConst=" + mIsConst +
                ", mIsFunction=" + mIsFunction +
                '}';
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

        Symbol symbol = (Symbol) o;

        return mIsConst == symbol.mIsConst &&
                mIsFunction == symbol.mIsFunction &&
                Objects.equals(mName, symbol.mName) &&
                Objects.equals(mType, symbol.mType) &&
                Objects.equals(mReturnType, symbol.mReturnType);
    }
}
