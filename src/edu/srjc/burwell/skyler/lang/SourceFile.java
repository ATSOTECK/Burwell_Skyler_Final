/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file represents the file being compiled.
 */

package edu.srjc.burwell.skyler.lang;

public class SourceFile
{
    private String mName = "";
    private String mPath = "";

    public SourceFile()
    {
        mName = "";
        mPath = "";
    }

    public SourceFile(String path)
    {
        mPath = path;
        int i;

        for (i = mPath.length() - 1; i >= 0; --i)
        {
            if (mPath.charAt(i) == '/' || mPath.charAt(i) == '\\')
            {
                break;
            }
        }

        for (int j = ++i; j < mPath.length(); ++j)
        {
            mName += mPath.charAt(j);
        }
    }

    public SourceFile(SourceFile file)
    {
        mName = file.name();
        mPath = file.path();
    }

    public String name()
    {
        return mName;
    }

    public String path()
    {
        return mPath;
    }
}
