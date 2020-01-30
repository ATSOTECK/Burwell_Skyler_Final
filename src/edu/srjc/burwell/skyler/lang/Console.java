/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file contains the built in console for the program.
 */

package edu.srjc.burwell.skyler.lang;

import javafx.scene.control.TextArea;

import java.io.OutputStream;

public class Console extends OutputStream
{
    private TextArea mTextArea = null;

    public Console()
    {
        mTextArea = new TextArea();
        mTextArea.setEditable(false);
    }

    @Override
    public void write(int b)
    {
        mTextArea.appendText(String.valueOf((char) b));
    }

    public TextArea getTextArea()
    {
        return mTextArea;
    }
}
