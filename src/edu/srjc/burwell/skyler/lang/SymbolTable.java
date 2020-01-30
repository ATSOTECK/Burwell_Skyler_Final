/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file represents the symbol table of a scope.
 */

package edu.srjc.burwell.skyler.lang;

import java.util.ArrayList;
import java.util.Objects;

public class SymbolTable
{
    private ArrayList<Symbol> mSymbols = null;

    public SymbolTable()
    {
        mSymbols = new ArrayList<Symbol>();
    }

    public void add(Symbol symbol)
    {
        mSymbols.add(symbol);
    }

    public boolean contains(String name)
    {
        for (Symbol symbol : mSymbols)
        {
            if (symbol.getName().equals(name))
            {
                return true;
            }
        }

        return false;
    }

    public int indexOf(String name)
    {
        for (int i = 0; i < mSymbols.size(); ++i)
        {
            if (mSymbols.get(i).getName().equals(name))
            {
                return i;
            }
        }

        return -1;
    }

    public Node typeForSymbol(String name)
    {
        if (contains(name))
        {
            return mSymbols.get(indexOf(name)).getType();
        }

        return new TypelessNode();
    }

    public Node returnTypeForSymbol(String name)
    {
        if (contains(name))
        {
            return mSymbols.get(indexOf(name)).getReturnType();
        }

        return new TypelessNode();
    }

    public Symbol symbolForName(String name)
    {
        if (contains(name))
        {
            return mSymbols.get(indexOf(name));
        }

        return new Symbol();
    }

    public void print()
    {
        for (Symbol symbol : mSymbols)
        {
            System.out.println(symbol);
        }
    }

    @Override
    public String toString()
    {
        return "SymbolTable{" +
                "mSymbols=" + mSymbols +
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

        SymbolTable that = (SymbolTable) o;

        return Objects.equals(mSymbols, that.mSymbols);
    }
}
