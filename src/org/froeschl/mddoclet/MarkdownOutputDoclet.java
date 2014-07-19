package org.froeschl.mddoclet;

import org.froeschl.mddoclet.formatter.MarkdownFormatter;
import org.froeschl.mddoclet.printer.FilePrinter;

import com.sun.javadoc.RootDoc;

public class MarkdownOutputDoclet {
    private static Editor editor = null;
    
    public static boolean start(RootDoc root) {
        Options options = Options.fromCommandLine(root.options());
        MarkdownOutputDoclet.editor = new Editor(options, new MarkdownFormatter(), new FilePrinter(options.getFullOutputFilePath()));
        MarkdownOutputDoclet.editor.authorDocument(root);
        return true;
    }
    
    public static int optionLength(String option) {
        return Options.getOptionLength(option);
    }
}
