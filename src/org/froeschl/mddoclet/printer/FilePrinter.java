package org.froeschl.mddoclet.printer;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.froeschl.mddoclet.utils.FileUtils;


public class FilePrinter implements Printer {
    private final String fileName;
    
    public FilePrinter(String fileName) {
        if ( fileName == null ) {
            throw new InvalidParameterException();
        }
        
        this.fileName = fileName;
        FileUtils.deleteFileOrFolder(fileName);
    }
    
    @Override
    public void print(String text) {
        try {
            FileUtils.appendStringToFile(this.fileName, text);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
