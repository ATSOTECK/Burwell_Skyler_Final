/*
 * Skyler Burwell
 * skburwell@gmail.com
 * 19 - May - 2018
 * Burwell_Skyler_Final A simple c-like programming language that allows for variables, functions, and multiple files.
 * It also has a very basic editor to edit the files and compile them.
 * CS 17.11 6991
 * The Main file. This sets up the editor and allows for the compiler to be called.
 */

package edu.srjc.burwell.skyler.lang;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application
{
    private HashMap<Tab, CodeEditor> mCodeEditors = null;
    private CodeEditor mCurrentEditor = null;
    private TreeView<File> mTreeView = null;
    private BorderPane mBorderPane = null;
    private Console mConsole = null;
    private TextField mCommands = null;
    private TabPane mTabs = null;

    private Stage mStage = null;

    private void showError(String msg)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showHelp()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Double click items on the tree view to the left to open and edit them.\n" +
                "The compile option will compile the code in the open tab.\n" +
                "The run option will run the compiled code.");
        alert.showAndWait();
    }
    
    @Override
    public void start(Stage stage)
    {
        mStage = stage;
        setupUI();

        Scene scene = new Scene(mBorderPane, 1024, 600);
        scene.getStylesheets().add(Main.class.getResource("css.css").toExternalForm());
        mStage.setScene(scene);
        mStage.setTitle("Yava Editing Environment");
        mStage.show();
        mStage.setMaximized(true);

        mConsole.getTextArea().requestFocus();
    }

    private void setupUI()
    {
        mCodeEditors = new HashMap<Tab, CodeEditor>();

        final Menu fileMenu = new Menu("File");
        final Menu codeMenu = new Menu("Code");
        final Menu helpMenu = new Menu("Help");

        String os = System.getProperty("os.name").toLowerCase();

        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(event -> openFile());
        if (os.contains("mac"))
        {
            openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
        }
        else
        {
            openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        }
        fileMenu.getItems().add(openItem);

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(event -> saveFile());
        if (os.contains("mac"))
        {
            saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
        }
        else
        {
            saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        }
        fileMenu.getItems().add(saveItem);

        MenuItem clearItem = new MenuItem("Clear Console");
        clearItem.setOnAction(event -> clearConsole());
        if (os.contains("mac"))
        {
            clearItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.META_DOWN));
        }
        else
        {
            clearItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        }
        fileMenu.getItems().add(clearItem);

        MenuItem compile = new MenuItem("Compile");
        compile.setOnAction(event -> compile());
        if (os.contains("mac"))
        {
            compile.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.META_DOWN));
        }
        else
        {
            compile.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
        }
        codeMenu.getItems().add(compile);

        MenuItem compileAndRun = new MenuItem("Compile and Run");
        compileAndRun.setOnAction(event -> compileAndRun());
        if (os.contains("mac"))
        {
            compileAndRun.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN));
        }
        else
        {
            compileAndRun.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        }
        codeMenu.getItems().add(compileAndRun);

        MenuItem run = new MenuItem("Run");
        run.setOnAction(event -> run());
        if (os.contains("mac"))
        {
            run.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.META_DOWN));
        }
        else
        {
            run.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
        }
        codeMenu.getItems().add(run);

        MenuItem help = new MenuItem("Info");
        help.setOnAction(event -> showHelp());
        helpMenu.getItems().add(help);

        MenuBar mMenuBar = new MenuBar(fileMenu, codeMenu, helpMenu);

        mTreeView = new TreeView<>();
        mTreeView.setRoot(createTree(new File(".")));
        mTreeView.setCellFactory((e) -> new TreeCell<File>()
        {
            @Override
            protected void updateItem(File item, boolean empty)
            {
                super.updateItem(item, empty);

                if(item != null)
                {
                    setText(item.getName());
                    setGraphic(getTreeItem().getGraphic());
                }
                else
                {
                    setText("");
                    setGraphic(null);
                }
            }
        });

        mTreeView.setOnMouseClicked(event ->
        {
            if (event.getClickCount() == 2)
            {
                TreeItem<File> item = mTreeView.getSelectionModel().getSelectedItem();

                if (item != null && !item.getValue().getPath().equals("."))
                {
                    File file = new File(item.getValue().getPath());
                    if (file.isFile())
                    {
                        openFile(item.getValue().getPath());
                    }
                }
            }
        });

        mBorderPane = new BorderPane();
        mBorderPane.setTop(mMenuBar);

        mTabs = new TabPane();
        mTabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
        {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
            {
                mCurrentEditor = mCodeEditors.get(newValue);
            }
        });

        SplitPane leftSplitPane = new SplitPane(mTreeView, mTabs);
        leftSplitPane.setDividerPositions(0.1f, 0.9f);

        mConsole = new Console();
        System.setOut(new PrintStream(mConsole));
        mCommands = new TextField();

        mCommands.setOnKeyPressed(event ->
        {
            if (event.getCode() == KeyCode.ENTER)
            {
                if (mCommands.getText().isEmpty())
                {
                    return;
                }

                switch (mCommands.getText())
                {
                    case "help":
                    case "?":
                        mConsole.getTextArea().appendText("Valid commands:\n");
                        mConsole.getTextArea().appendText("c - compile\n");
                        mConsole.getTextArea().appendText("s - save\n");
                        break;
                    case "c":
                        compile();
                        break;
                    case "s":
                        saveFile();
                        break;
                    default:
                        mConsole.getTextArea().appendText("?\n");
                        break;
                }

                mCommands.clear();
            }
        });

        SplitPane bottomSplitPane = new SplitPane(leftSplitPane, mConsole.getTextArea());
        bottomSplitPane.setOrientation(Orientation.VERTICAL);
        bottomSplitPane.setDividerPositions(.8f, .2f);

        mBorderPane.setCenter(bottomSplitPane);
        mBorderPane.setBottom(mCommands);
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    private TreeItem<File> createTree(File file)
    {
        TreeItem<File> item = new TreeItem<File>(file);
        File[] children = file.listFiles();

        if (children != null)
        {
            for (File child : children)
            {
                item.getChildren().add(createTree(child));
            }
        }

        return item;
    }

    private void compile()
    {
        if (mCurrentEditor == null)
        {
            showError("No code editor selected. Select a code editor to compile.");
            return;
        }

        mConsole.getTextArea().clear();

        SourceFile source = new SourceFile(mCurrentEditor.getPath());
        Compiler compiler = new Compiler(source);
        compiler.compile();

        selectAndCloseTabByNameIfOpen("Main.java");

        openFile("Main.java");
    }

    private void compileAndRun()
    {
        compile();

        if (mCurrentEditor != null)
        {
            run();
        }
    }

    private void run()
    {
        Process process = null;

        try
        {
            process = new ProcessBuilder("javac", "Main.java").start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = "";

            while ((line = br.readLine()) != null)
            {
                System.out.println(line);
            }
        }
        catch (IOException ex)
        {
            showError("Unable to run javac.");
            return;
        }

        try
        {
            process = new ProcessBuilder("java", "Main").start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = "";

            while ((line = br.readLine()) != null)
            {
                System.out.println(line);
            }
        }
        catch (IOException ex)
        {
            showError("Unable to run java.");
        }
    }

    private void openFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(mStage);
        if (file == null)
        {
            return;
        }

        selectAndCloseTabByPathIfOpen(file.getPath());

        setUpCodeEditor(file.getPath());
    }

    private void openFile(String file)
    {
        selectAndCloseTabByPathIfOpen(file);

        setUpCodeEditor(file);
    }

    private void setUpCodeEditor(String file)
    {
        CodeEditor editor = new CodeEditor(file);
        mTabs.getTabs().add(editor.getTab());
        mTabs.getSelectionModel().select(editor.getTab());
        mCodeEditors.put(editor.getTab(), editor);
        mCurrentEditor = editor;
    }

    private void saveFile()
    {
        PrintWriter file = null;

        if (mCurrentEditor == null)
        {
            showError("No code editor selected. Select a code editor to save.");
            return;
        }

        try
        {
            file = new PrintWriter(mCurrentEditor.getPath());
        }
        catch (FileNotFoundException e)
        {
            showError("Could not save " + mCurrentEditor.getPath() + ".");
        }

        if (file != null)
        {
            file.print(mCurrentEditor.getText());
            file.flush();
            file.close();
        }
    }

    private void clearConsole()
    {
        mConsole.getTextArea().clear();
    }

    private void selectAndCloseTabByNameIfOpen(String name)
    {
        for (Map.Entry<Tab, CodeEditor> entry : mCodeEditors.entrySet())
        {
            if (entry.getValue().getName().equals(name))
            {
                mTabs.getSelectionModel().select(entry.getKey());
                mTabs.getTabs().remove(entry.getKey());
            }
        }
    }

    private void selectAndCloseTabByPathIfOpen(String path)
    {
        for (Map.Entry<Tab, CodeEditor> entry : mCodeEditors.entrySet())
        {
            if (entry.getValue().getPath().equals(path))
            {
                mTabs.getSelectionModel().select(entry.getKey());
                mTabs.getTabs().remove(entry.getKey());
            }
        }
    }
}
