/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file contains some helper functions for char.
 */

package edu.srjc.burwell.skyler.lang;

public class CharHelper
{
    static boolean isNumber(char c)
    {
        return (c >= '0' && c <= '9');
    }

    static boolean isHexnumber(char c)
    {
        return (isNumber(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'));
    }

    static boolean isAlpha(char c)
    {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    static boolean isAlphanumeric(char c)
    {
        return (isAlpha(c) || isNumber(c));
    }

    static boolean isUpper(char c)
    {
        return (c >= 'A' && c <= 'Z');
    }

    static boolean islower(char c)
    {
        return (c >= 'a' && c <= 'z');
    }

    static boolean isWhitespace(char c)
    {
        return (c == ' ' || c == '\n' || c == '\t' || c == '\r');
    }

    static boolean isNewline(char c)
    {
        return (c == '\n' /*|| c == '\r\n'*/);
    }
}
