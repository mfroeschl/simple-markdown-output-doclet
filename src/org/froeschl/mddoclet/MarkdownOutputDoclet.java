package org.froeschl.mddoclet;

import org.froeschl.mddoclet.formatter.MarkdownFormatter;
import org.froeschl.mddoclet.printer.FilePrinter;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;
import com.sun.media.sound.InvalidFormatException;

public class MarkdownOutputDoclet {
    public static final String VERSION_STRING = "0.2.0";
    private static Editor editor = null;
    
    public static boolean start(RootDoc root) {
        try {
            Options options = Options.fromCommandLine(root.options());
            MarkdownOutputDoclet.editor = new Editor(options, new MarkdownFormatter(), new FilePrinter());
            MarkdownOutputDoclet.editor.authorDocument(root);
        } catch (InvalidFormatException e) {
            System.out.println("Error parsing Options from command line: " + e.getMessage());
        }
        
        return true;
    }
    
    public static int optionLength(String option) {
        return Options.getOptionLength(option);
    }
    
    public static boolean validOptions(String input[][], 
            DocErrorReporter reporter) {
        return Options.validOptions(input, reporter);
    }
}
