/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file contains helper functions for printing to the console.
 */

package edu.srjc.burwell.skyler.lang;

public class ConsoleHelper
{
    public static final String ResetColor   = "\u001B[0m";
    public static final String ErrorColor   = "\u001B[31m";
    public static final String WarningColor = "\u001B[33m";
    public static final String GreenColor   = "\u001B[32m";
    public static final String TextColor    =  "\u001B[37m";
    public static final String PurpleColor  = "\u001B[35m";

    public static void setConsoleColor(String color)
    {
        //Don't do anything because System.out has been redirected to a javafx TextArea.
        //System.out.print(color);
    }

    public static void print(String str)
    {
        System.out.print(str);
    }

    public static void println(String str)
    {
        System.out.println(str);
    }

    public static void ln()
    {
        System.out.println();
    }
}
