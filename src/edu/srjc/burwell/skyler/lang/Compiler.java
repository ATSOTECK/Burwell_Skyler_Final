/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * This file contains the compiler class which calls the lexex, parser, and codegen.
 */

package edu.srjc.burwell.skyler.lang;

import java.util.ArrayList;

import static edu.srjc.burwell.skyler.lang.ConsoleHelper.*;

public class Compiler
{
    private SourceFile mSourceFile;

    Compiler(SourceFile sourceFile)
    {
        mSourceFile = sourceFile;
    }

    public void compile()
    {
        Lexer lexer = new Lexer(mSourceFile);
        Parser parser = new Parser(lexer);
        ArrayList<Node> nodes = parser.parse();

        if (nodes == null)
        {
            return;
        }

        CompilerSettings.instance().setVerbosity(CompilerSettings.Some);

        if (CompilerSettings.instance().getVerbosity() >= CompilerSettings.Some)
        {
            System.out.println("\nParse tree size: " + nodes.size());
        }

        if (CompilerSettings.instance().getVerbosity() == CompilerSettings.All)
        {
            StringBuilder finalString = new StringBuilder();

            for (edu.srjc.burwell.skyler.lang.Node node : parser.getNodes())
            {
                StringBuilder stringBuilder = new StringBuilder(node.toString());
                int tabAmount = 0;

                for (int i = 0; i < stringBuilder.length() - 1; ++i)
                {
                    if (stringBuilder.charAt(i) == '{')
                    {
                        stringBuilder.insert(i++ +1, "\n");
                        ++tabAmount;

                        for (int j = 0; j < tabAmount; ++j)
                        {
                            stringBuilder.insert(i++ + 1, "\t");
                        }
                    }

                    if (stringBuilder.charAt(i) == '}')
                    {
                        stringBuilder.insert(i++, "\n");
                        --tabAmount;

                        for (int j = 0; j < tabAmount; ++j)
                        {
                            stringBuilder.insert(i++, "\t");
                        }
                    }
                }

                finalString.append(stringBuilder);
            }

            System.out.println(finalString);
        }

        if (parser.finishedWithErrors())
        {
            print("\nCompile finished with ");
            setConsoleColor(ErrorColor);
            print("errors");
            setConsoleColor(TextColor);
            println(".");

            setConsoleColor(ResetColor);
        }
        else
        {
            if (!parser.foundStart())
            {
                setConsoleColor(ErrorColor);
                print("Error:");
                setConsoleColor(TextColor);
                println(" no start function found!");
            }

            print("\nCompile finished ");
            setConsoleColor(GreenColor);
            print("successfully");
            println(".");

            Codegen codegen = new Codegen(nodes);
            codegen.codegen();
        }
    }
}
