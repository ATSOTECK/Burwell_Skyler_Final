/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file contains global settings for the compiler.
 */


package edu.srjc.burwell.skyler.lang;

public class CompilerSettings
{
    public static final int None = 0;
    public static final int Some = 1;
    public static final int All  = 2;

    public static final int     DefaultNumBits  = 32;
    public static final int     DefaultIntValue = 0;
    public static final boolean DefaultSigned   = true;

    private boolean mShowWarnings = true;

    private static CompilerSettings mInstance = null;

    private int mVerbosity = None;

    //TODO:
    //private ArrayList<String> mSourceSearchPaths = null;

    public static CompilerSettings instance()
    {
        if (mInstance == null)
        {
            mInstance = new CompilerSettings();
        }

        return mInstance;
    }

    public void setVerbosity(int verbosity)
    {
        mVerbosity = verbosity;

        if (mVerbosity == None)
        {
            mShowWarnings = false;
        }
        else if (mVerbosity == All)
        {
            mShowWarnings = true;
        }
    }

    public int getVerbosity()
    {
        return mVerbosity;
    }

    public void setShowWarnings(boolean show)
    {
        mShowWarnings = show;
    }

    public boolean showWarnings()
    {
        return mShowWarnings;
    }

    private CompilerSettings()
    {
        mVerbosity = None;
        mShowWarnings = true;
    }
}
