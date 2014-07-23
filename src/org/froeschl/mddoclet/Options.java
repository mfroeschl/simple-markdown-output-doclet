package org.froeschl.mddoclet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.froeschl.mddoclet.utils.FileUtils;

public class Options {
    public enum Visibility {
        PRIVATE("private"),
        PROTECTED("protected"),
        PACKAGE("package"),
        PUBLIC("public");
        
        private static final Visibility DEFAULT_VALUE = Visibility.PUBLIC;
        private String value;
        
        private Visibility(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public static Visibility fromString(String value) {
            if ( value == null ) {
                return DEFAULT_VALUE;
            }
            
            for ( Visibility visibility : Visibility.values() ) {
                if ( visibility.getValue().equals(value) ) {
                    return visibility;
                }
            }
            
            return DEFAULT_VALUE;
        }
    }
    
    public static final String KEY_OUTPUT_FILE = "-outputfile";
    public static final String KEY_OUTPUT_DIR = "-outputdir";
    public static final String KEY_INCLUDE_DIR = "-includedir";
    public static final String KEY_LAYOUT_DIR = "-layoutdir";
    public static final String KEY_DOCUMENT_TITLE = "-doctitle";
    public static final String KEY_DOCUMENT_HEADER = "-header";
    public static final String KEY_MINIMUM_VISIBILITY = "-visibility";
    public static final String KEY_NO_INTERFACES = "-nointerfaces";
    public static final String KEY_NO_ENUMS = "-noenums";
    public static final String KEY_NO_NESTED_CLASSES = "-nonested";
    public static final String KEY_INCLUDE_HIDDEN = "-includehidden";
    public static final String KEY_ANNOTATIONS_TO_BE_REMOVED = "-removeannotations";
    
    private static final String LF = "\n";
    private static final String INDENT = "    ";
    private static final String COMMA = ",";
    private static final String DEFAULT_OUTPUT_FILE = "output.md";
    private static final String DEFAULT_OUTPUT_DIR = ".";
    private static final String DEFAULT_INCLUDE_DIR = "./include";
    private static final String DEFAULT_LAYOUT_DIR = "./layouts/markdown";
    private static final String DEFAULT_DOCUMENT_TITLE = "Documentation";
    private static final String DEFAULT_DOCUMENT_HEADER = "";
    private static final Visibility DEFAULT_MINIMUM_VISIBILITY = Visibility.PUBLIC;
    private static final boolean DEFAULT_NO_ENUMS = false;
    private static final boolean DEFAULT_NO_INTERFACES = false;
    private static final boolean DEFAULT_NO_NESTED_CLASSES = false;
    private static final boolean DEFAULT_INCLUDE_HIDDEN = false;
    private static final List<String> DEFAULT_ANNOTATIONS_TO_BE_REMOVED = new ArrayList<String>();;
    
    public static Map<String, Integer> optionToOptionLength;
    
    static {
        Options.optionToOptionLength = new HashMap<String, Integer>();
        Options.optionToOptionLength.put(KEY_OUTPUT_FILE, 2);
        Options.optionToOptionLength.put(KEY_OUTPUT_DIR, 2);
        Options.optionToOptionLength.put(KEY_INCLUDE_DIR, 2);
        Options.optionToOptionLength.put(KEY_LAYOUT_DIR, 2);
        Options.optionToOptionLength.put(KEY_DOCUMENT_TITLE, 2);
        Options.optionToOptionLength.put(KEY_DOCUMENT_HEADER, 2);
        Options.optionToOptionLength.put(KEY_MINIMUM_VISIBILITY, 2);
        Options.optionToOptionLength.put(KEY_ANNOTATIONS_TO_BE_REMOVED, 2);
        Options.optionToOptionLength.put(KEY_NO_ENUMS, 1);
        Options.optionToOptionLength.put(KEY_NO_INTERFACES, 1);
        Options.optionToOptionLength.put(KEY_NO_NESTED_CLASSES, 1);
        Options.optionToOptionLength.put(KEY_INCLUDE_HIDDEN, 1);
    }
    
    private String outputFile = DEFAULT_OUTPUT_FILE;
    private String outputDir = DEFAULT_OUTPUT_DIR;
    private String includeDir = DEFAULT_INCLUDE_DIR;
    private String layoutDir = DEFAULT_LAYOUT_DIR;
    private String documentTitle = DEFAULT_DOCUMENT_TITLE;
    private String documentHeader = DEFAULT_DOCUMENT_HEADER;
    private Visibility minimumVisibility = DEFAULT_MINIMUM_VISIBILITY;
    private String fullOutputFilePath = FileUtils.appendToPath(DEFAULT_OUTPUT_DIR, DEFAULT_OUTPUT_FILE);
    private boolean noEnums = DEFAULT_NO_ENUMS;
    private boolean noInterfaces = DEFAULT_NO_INTERFACES;
    private boolean noNestedClasses = DEFAULT_NO_NESTED_CLASSES;
    private boolean includeHidden = DEFAULT_INCLUDE_HIDDEN;
    private List<String> annotationsToBeRemoved = DEFAULT_ANNOTATIONS_TO_BE_REMOVED;
    
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
            } else if ( option[0].equals(KEY_LAYOUT_DIR) ) {
                this.layoutDir = option[1];
            } else if ( option[0].equals(KEY_DOCUMENT_TITLE) ) {
                this.documentTitle = option[1];
            } else if ( option[0].equals(KEY_DOCUMENT_HEADER) ) {
                this.documentHeader = option[1];
            } else if ( option[0].equals(KEY_MINIMUM_VISIBILITY) ) {
                this.minimumVisibility = Visibility.fromString(option[1]);
            } else if ( option[0].equals(KEY_ANNOTATIONS_TO_BE_REMOVED) ) {
                this.parseAnnotationsToBeRemoved(option[1]);
            } else if ( option[0].equals(KEY_NO_ENUMS) ) {
                this.noEnums = true;
            } else if ( option[0].equals(KEY_NO_INTERFACES) ) {
                this.noInterfaces = true;
            } else if ( option[0].equals(KEY_NO_NESTED_CLASSES) ) {
                this.noNestedClasses = true;
            } else if ( option[0].equals(KEY_INCLUDE_HIDDEN) ) {
                this.includeHidden = true;
            }
        }
        
        this.fullOutputFilePath = FileUtils.appendToPath(this.outputDir, this.outputFile);
    }
    
    private void parseAnnotationsToBeRemoved(String input) {
        this.annotationsToBeRemoved.clear();
        String[] annotations = input.split(",");
        for ( String annotation : annotations ) {
            if ( !annotation.isEmpty() )  {
                this.annotationsToBeRemoved.add(annotation);
            }
        }
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
    
    public String getLayoutDir() {
        return this.layoutDir;
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
    
    public Visibility getMinimumVisibility() {
        return this.minimumVisibility;
    }
    
    public boolean getNoEnums() {
        return this.noEnums;
    }
    
    public boolean getNoInterfaces() {
        return this.noInterfaces;
    }
    
    public boolean getNoNestedClasses() {
        return this.noNestedClasses;
    }
    
    public boolean getIncludeHidden() {
        return this.includeHidden;
    }
    
    public List<String> getAnnotationsToBeRemoved() {
        return this.annotationsToBeRemoved;
    }
    
    @Override
    public String toString() {
        String result = "Options:";
        result += LF + "outputFile = " + this.outputFile;
        result += LF + "outputDir = " + this.outputDir;
        result += LF + "fullOutputFilePath = " + this.fullOutputFilePath;
        result += LF + "documentTitle = " + this.documentTitle;
        result += LF + "documentHeader = " + this.documentHeader;
        result += LF + "minimumVisibility = " + this.minimumVisibility;
        result += LF + "noEnums = " + this.noEnums;
        result += LF + "noInterfaces = " + this.noInterfaces;
        result += LF + "noNestedClasses = " + this.noNestedClasses;
        result += LF + "includeHidden = " + this.includeHidden;
        result += LF + "annotationsToBeRemoved = {";
        
        for ( int i = 0; i < this.annotationsToBeRemoved.size(); i++ ) {
            result += LF + INDENT + this.annotationsToBeRemoved.get(i);
            if ( i < this.annotationsToBeRemoved.size() - 1 ) {
                result += COMMA;
            }
        }
        
        result += LF + "}";
        return result;
    }
}
