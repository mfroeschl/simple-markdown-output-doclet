package org.froeschl.mddoclet;

import java.util.HashMap;
import java.util.Map;

import org.froeschl.mddoclet.utils.FileUtils;

public class Options {
    public static final String KEY_OUTPUT_FILE = "-outputfile";
    public static final String KEY_OUTPUT_DIR = "-outputdir";
    public static final String KEY_INCLUDE_DIR = "-includedir";
    public static final String KEY_DOCUMENT_TITLE = "-doctitle";
    public static final String KEY_DOCUMENT_HEADER = "-header";
    
    private static final String DEFAULT_OUTPUT_FILE = "output.md";
    private static final String DEFAULT_OUTPUT_DIR = ".";
    private static final String DEFAULT_INCLUDE_DIR = "./include";
    private static final String DEFAULT_DOCUMENT_TITLE = "Documentation";
    private static final String DEFAULT_DOCUMENT_HEADER = "";
    
    public static Map<String, Integer> optionToOptionLength;
    
    static {
        Options.optionToOptionLength = new HashMap<String, Integer>();
        Options.optionToOptionLength.put(KEY_OUTPUT_FILE, 2);
        Options.optionToOptionLength.put(KEY_OUTPUT_DIR, 2);
        Options.optionToOptionLength.put(KEY_INCLUDE_DIR, 2);
        Options.optionToOptionLength.put(KEY_DOCUMENT_TITLE, 2);
        Options.optionToOptionLength.put(KEY_DOCUMENT_HEADER, 2);
    }
    
    private String outputFile = DEFAULT_OUTPUT_FILE;
    private String outputDir = DEFAULT_OUTPUT_DIR;
    private String includeDir = DEFAULT_INCLUDE_DIR;
    private String documentTitle = DEFAULT_DOCUMENT_TITLE;
    private String documentHeader = DEFAULT_DOCUMENT_HEADER;
    private String fullOutputFilePath = FileUtils.appendToPath(DEFAULT_OUTPUT_DIR, DEFAULT_OUTPUT_FILE);
    
    private Options() {
    }
    
    public static Options fromCommandLine(String[][] input) {
        Options options = new Options();
        options.parseCommandLine(input);
        return options;
    }
    
    public static int getOptionLength(String option) {
        Integer length = Options.optionToOptionLength.get(option);
        
        if ( length == null ) {
            return 0;
        } else {
            return length;
        }
    }
    
    public void parseCommandLine(String[][] options) {
        for (int i = 0; i < options.length; i++) {
            String[] option = options[i];
            
            if ( option[0].equals(KEY_OUTPUT_FILE) ) {
                this.outputFile = option[1];
            } else if ( option[0].equals(KEY_OUTPUT_DIR) ) {
                this.outputDir = option[1];
            } else if ( option[0].equals(KEY_INCLUDE_DIR) ) {
                this.includeDir = option[1];
            } else if ( option[0].equals(KEY_DOCUMENT_TITLE) ) {
                this.documentTitle = option[1];
            } else if ( option[0].equals(KEY_DOCUMENT_HEADER) ) {
                this.documentHeader = option[1];
            }
        }
        
        this.fullOutputFilePath = FileUtils.appendToPath(this.outputDir, this.outputFile);
    }
    
    public String getOutputFile() {
        return this.outputFile;
    }
    
    public String getOutputDir() {
        return this.outputDir;
    }
    
    public String getIncludeDir() {
        return this.includeDir;
    }
    
    public String getDocumentTitle() {
        return this.documentTitle;
    }
    
    public String getDocumentHeader() {
        return this.documentHeader;
    }
    
    public String getFullOutputFilePath() {
        return this.fullOutputFilePath;
    }
}
