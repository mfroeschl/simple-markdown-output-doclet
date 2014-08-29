package org.froeschl.mddoclet.printer;

import java.io.IOException;

import org.froeschl.mddoclet.utils.FileUtils;


public class FilePrinter implements Printer {
    
    public FilePrinter() {
        // FileUtils.deleteFileOrFolder(fileName);
    }
    
    @Override
    public void print(String fileName, String text) {
        try {
            FileUtils.appendStringToFile(fileName, text);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
