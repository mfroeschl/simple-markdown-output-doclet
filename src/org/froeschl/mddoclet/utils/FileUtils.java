package org.froeschl.mddoclet.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Collection of useful tools for dealing with file storage.
 *  
 * @author Marcus Froeschl
 * @version 1.0
 * @since 2014-07-18
 */
public class FileUtils {
    public static final String DIRECTORY_SEPARATOR = "/";
    public static final String FILE_EXTENSION_SEPARATOR = ".";
    public static final int MAXIMUM_FILENAME_LENGTH = 50;
    
    /**
     * Appends a suffix to a path
     * @param path Original path.
     * @param append Suffix to be appended.
     * @return Appended path.
     * */
    public static String appendToPath(String path, String append) {
        String result = path;
        
        if ( result.length() > 0 && append.length() > 0 ) {
            if ( !result.endsWith(DIRECTORY_SEPARATOR) ) {
                result += DIRECTORY_SEPARATOR;
            }
        }
        
        result += append;
        return result;
    }
    
    /**
     * Checks if the file at the given path exists.
     * @param path Path to file.
     * @return true if file at path exists, false otherwise.
     * */
    public static boolean doesFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }
    
    /**
     * Deletes a file at a given path
     * @param path Path to file.
     * */
    public static void deleteFileOrFolder(String path) {
        File file = new File(path);
        file.delete();
    }
    
    /**
     * Reads a file into a String buffer
     * @param path Path to file.
     * @return String Contents of the file.
     * */
    public static String readFileIntoString(String path) throws IOException {
        String result = null;
        File file = null;
        BufferedReader reader = null;
        
        try {
            file = new File(path);
            reader = new BufferedReader(new FileReader (file));
            String         line = null;
            StringBuilder  stringBuilder = new StringBuilder();
            String         ls = System.getProperty("line.separator");
            
            while( ( line = reader.readLine() ) != null ) {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }
            
            result = stringBuilder.toString();
        } finally {
            if ( reader != null ) {
                try {
                    reader.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
        
        if ( result == null ) {
            result = "";
        }
        
        return result;
    }
    
    /**
     * Appends a String to a File
     * @param path Path to file.
     * @param text Text to be appended to file.
     * */
    public static void appendStringToFile(String path, String text) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));
        out.println(text);
        out.close();
    }
}
