package org.froeschl.mddoclet;

import org.froeschl.mddoclet.formatter.MarkdownFormatter;
import org.froeschl.mddoclet.printer.FilePrinter;

import com.sun.javadoc.RootDoc;

public class MarkdownDoclet {
    private static DocletParser docletParser = new DocletParser(new MarkdownFormatter(), new FilePrinter("documentation.md"));
    
    public static boolean start(RootDoc root) {
        MarkdownDoclet.docletParser.parse(root);
        return true;
    }
}
