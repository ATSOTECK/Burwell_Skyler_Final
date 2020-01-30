/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file contains some helper functions for the String class.
 */

package edu.srjc.burwell.skyler.lang;

public class StringHelper
{
    public static String stringBeforeFirst(String str, char c)
    {
        String ret = "";
        StringBuilder tmp = new StringBuilder();

        for (int i = 0; i < str.length(); ++i)
        {
            if (str.charAt(i) == c)
            {
                ret = tmp.toString();
                break;
            }

            tmp.append(str.charAt(i));
        }

        return ret;
    }

    public static String stringBeforeLast(String str, char c)
    {
        StringBuilder tmp = new StringBuilder();
        boolean found = false;
        int last = -1;

        for (int i = 0; i < str.length(); ++i)
        {
            if (str.charAt(i) == c)
            {
                last = i;
                found = true;
            }
        }

        for (int j = 0; j < last; ++j)
        {
            tmp.append(str.charAt(j));
        }

        if (found)
        {
            return tmp.toString();
        }
        else
        {
            return "";
        }
    }
}
