/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file represents scopes of a program.
 */

package edu.srjc.burwell.skyler.lang;

import java.util.ArrayList;
import java.util.Objects;

public class Scope
{
    private int mID = -1;
    private SymbolTable mSymbolTable = null;
    private ArrayList<Scope> mScopes = null;

    public Scope()
    {
        mSymbolTable = new SymbolTable();
        mScopes = new ArrayList<Scope>();
    }

    public void addSymbol(Symbol symbol)
    {
        mSymbolTable.add(symbol);
    }

    public boolean containsSymbol(String name)
    {
        return mSymbolTable.contains(name);
    }

    public SymbolTable getSymbolTable()
    {
        return mSymbolTable;
    }

    public void addScope(Scope scope)
    {
        mScopes.add(scope);
    }

    public int numScopes()
    {
        return mScopes.size();
    }

    public void setID(int id)
    {
        mID = id;
    }

    public void print()
    {
        System.out.println("$sid = " + mID);
        mSymbolTable.print();

        System.out.println();

        for (Scope scope : mScopes)
        {
            scope.print();
        }
    }

    @Override
    public String toString()
    {
        return "Scope{" +
                "mID=" + mID +
                ", mSymbolTable=" + mSymbolTable +
                ", mScopes=" + mScopes +
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

        Scope scope = (Scope) o;

        return mID == scope.mID &&
                Objects.equals(mSymbolTable, scope.mSymbolTable) &&
                Objects.equals(mScopes, scope.mScopes);
    }
}
