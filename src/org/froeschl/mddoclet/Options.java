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
    
    public class DocumentGroup {
        final String title;
        final String file;
        final String fullFilePath;
        final ArrayList<String> classes;
        
        public DocumentGroup(String title, String file, String fullFilePath, ArrayList<String> classes) {
            this.title = title;
            this.file = file;
            this.classes = classes;
            this.fullFilePath = fullFilePath;
        }
        
        public String getTitle() {
            return this.title;
        }
        
        public String getFile() {
            return this.file;
        }
        
        public String getFullFilePath() {
            return this.fullFilePath;
        }
        
        public ArrayList<String> getClasses() {
            return this.classes;
        }
        
        @Override
        public String toString() {
            boolean first = true;
            String result = this.title;
            // result += " [" + this.fullFilePath + "] ";
            
            for ( String entry : classes ) {
                if ( first ) {
                    first = false;
                    result += BROPEN;
                } else {
                    result += COMMA + WS;
                }
                
                result += entry;
            }
            
            if ( this.classes.size() > 0 ) {
                result += BRCLOSE;
            }
            
            return result;
        }
    }
    
    public static final String KEY_MAIN_FILE = "-mainfile";
    public static final String KEY_OUTPUT_DIR = "-outputdir";
    public static final String KEY_FILE_SUFFIX = "-filesuffix";
    public static final String KEY_INCLUDE_DIR = "-includedir";
    public static final String KEY_LAYOUT_DIR = "-layoutdir";
    public static final String KEY_DOCUMENT_TITLE = "-doctitle";
    public static final String KEY_DOCUMENT_HEADER = "-header";
    public static final String KEY_MINIMUM_VISIBILITY = "-visibility";
    public static final String KEY_NO_INTERFACES = "-nointerfaces";
    public static final String KEY_NO_ENUMS = "-noenums";
    public static final String KEY_NO_NESTED_CLASSES = "-nonested";
    public static final String KEY_INCLUDE_HIDDEN = "-includehidden";
    public static final String KEY_OMMIT_EMPTY_SECTIONS = "-ommitempty";
    public static final String KEY_OMMIT_VOID_RETURN_TYPE = "-ommitvoid";
    public static final String KEY_ANNOTATIONS_TO_BE_REMOVED = "-removeannotations";
    public static final String KEY_DOCUMENT_GROUPS = "-groups";
    
    private static final String LF = "\n";
    private static final String INDENT = "    ";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String SEMICOLON = ";";
    private static final String WS = " ";
    private static final String BROPEN = "(";
    private static final String BRCLOSE = ")";
    private static final String DEFAULT_MAIN_FILE = "index";
    private static final String DEFAULT_OUTPUT_DIR = ".";
    private static final String DEFAULT_FILE_SUFFIX = ".md";
    private static final String DEFAULT_INCLUDE_DIR = "./include";
    private static final String DEFAULT_LAYOUT_DIR = "./layouts/markdown";
    private static final String DEFAULT_DOCUMENT_TITLE = "Documentation";
    private static final String DEFAULT_DOCUMENT_HEADER = "";
    private static final Visibility DEFAULT_MINIMUM_VISIBILITY = Visibility.PUBLIC;
    private static final boolean DEFAULT_NO_ENUMS = false;
    private static final boolean DEFAULT_NO_INTERFACES = false;
    private static final boolean DEFAULT_NO_NESTED_CLASSES = false;
    private static final boolean DEFAULT_INCLUDE_HIDDEN = false;
    private static final boolean DEFAULT_OMMIT_EMPTY_SECTIONS = false;
    private static final boolean DEFAULT_OMMIT_VOID_RETURN_TYPE = false;
    private static final ArrayList<String> DEFAULT_ANNOTATIONS_TO_BE_REMOVED = new ArrayList<String>();
    private static final HashMap<String, DocumentGroup> DEFAULT_DOCUMENT_GROUPS = new HashMap<String, DocumentGroup>();
    
    public static Map<String, Integer> optionToOptionLength;
    
    static {
        Options.optionToOptionLength = new HashMap<String, Integer>();
        Options.optionToOptionLength.put(KEY_MAIN_FILE, 2);
        Options.optionToOptionLength.put(KEY_OUTPUT_DIR, 2);
        Options.optionToOptionLength.put(KEY_FILE_SUFFIX, 2);
        Options.optionToOptionLength.put(KEY_INCLUDE_DIR, 2);
        Options.optionToOptionLength.put(KEY_LAYOUT_DIR, 2);
        Options.optionToOptionLength.put(KEY_DOCUMENT_TITLE, 2);
        Options.optionToOptionLength.put(KEY_DOCUMENT_HEADER, 2);
        Options.optionToOptionLength.put(KEY_MINIMUM_VISIBILITY, 2);
        Options.optionToOptionLength.put(KEY_ANNOTATIONS_TO_BE_REMOVED, 2);
        Options.optionToOptionLength.put(KEY_DOCUMENT_GROUPS, 2);
        Options.optionToOptionLength.put(KEY_NO_ENUMS, 1);
        Options.optionToOptionLength.put(KEY_NO_INTERFACES, 1);
        Options.optionToOptionLength.put(KEY_NO_NESTED_CLASSES, 1);
        Options.optionToOptionLength.put(KEY_INCLUDE_HIDDEN, 1);
        Options.optionToOptionLength.put(KEY_OMMIT_EMPTY_SECTIONS, 1);
        Options.optionToOptionLength.put(KEY_OMMIT_VOID_RETURN_TYPE, 1);
    }
    
    private String mainFile = DEFAULT_MAIN_FILE;
    private String outputDir = DEFAULT_OUTPUT_DIR;
    private String fileSuffix = DEFAULT_FILE_SUFFIX;
    private String includeDir = DEFAULT_INCLUDE_DIR;
    private String layoutDir = DEFAULT_LAYOUT_DIR;
    private String documentTitle = DEFAULT_DOCUMENT_TITLE;
    private String documentHeader = DEFAULT_DOCUMENT_HEADER;
    private Visibility minimumVisibility = DEFAULT_MINIMUM_VISIBILITY;
    private String fullMainFilePath = FileUtils.appendToPath(DEFAULT_OUTPUT_DIR, DEFAULT_MAIN_FILE + DEFAULT_FILE_SUFFIX);
    private boolean noEnums = DEFAULT_NO_ENUMS;
    private boolean noInterfaces = DEFAULT_NO_INTERFACES;
    private boolean noNestedClasses = DEFAULT_NO_NESTED_CLASSES;
    private boolean includeHidden = DEFAULT_INCLUDE_HIDDEN;
    private boolean ommitEmptySections = DEFAULT_OMMIT_EMPTY_SECTIONS;
    private boolean ommitVoidReturnType = DEFAULT_OMMIT_VOID_RETURN_TYPE;
    private ArrayList<String> annotationsToBeRemoved = DEFAULT_ANNOTATIONS_TO_BE_REMOVED;
    private HashMap<String, DocumentGroup> documentGroups = DEFAULT_DOCUMENT_GROUPS;
    
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
        String groups = "";
        
        for (int i = 0; i < options.length; i++) {
            String[] option = options[i];
            
            if ( option[0].equals(KEY_MAIN_FILE) ) {
                this.mainFile = option[1];
            } else if ( option[0].equals(KEY_OUTPUT_DIR) ) {
                this.outputDir = option[1];
            } else if ( option[0].equals(KEY_FILE_SUFFIX) ) {
                this.fileSuffix = option[1];
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
            } else if ( option[0].equals(KEY_DOCUMENT_GROUPS) ) {
                groups = option[1];
            } else if ( option[0].equals(KEY_NO_ENUMS) ) {
                this.noEnums = true;
            } else if ( option[0].equals(KEY_NO_INTERFACES) ) {
                this.noInterfaces = true;
            } else if ( option[0].equals(KEY_NO_NESTED_CLASSES) ) {
                this.noNestedClasses = true;
            } else if ( option[0].equals(KEY_INCLUDE_HIDDEN) ) {
                this.includeHidden = true;
            } else if ( option[0].equals(KEY_OMMIT_EMPTY_SECTIONS) ) {
                this.ommitEmptySections = true;
            } else if ( option[0].equals(KEY_OMMIT_VOID_RETURN_TYPE) ) {
                this.ommitVoidReturnType = true;
            }
        }
        
        this.fullMainFilePath = this.generateFullFilename(this.mainFile);
        this.parseDocumentGroups(groups);
    }
    
    private void parseAnnotationsToBeRemoved(String input) {
        this.annotationsToBeRemoved.clear();
        String[] annotations = input.split(COMMA);
        for ( String annotation : annotations ) {
            if ( !annotation.isEmpty() )  {
                this.annotationsToBeRemoved.add(annotation);
            }
        }
    }
    
    private void parseDocumentGroups(String input) {
        this.documentGroups.clear();
        String[] groups = input.split(SEMICOLON);
        
        for ( String group : groups ) {
            String[] parts = group.split(COLON);
            
            if ( parts == null || parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty() ) {
                System.out.println("Invalid documentGroup: \"" + group + "\"");
                continue;
            }
            
            String title = parts[0];
            String[] classes = parts[1].split(COMMA);
            ArrayList<String> array = new ArrayList<String>();
            
            for ( String entry : classes ) {
                array.add(entry);
            }
            
            String fullFilePath = this.generateFullFilename(title);
            String filePath = title + this.fileSuffix;
            this.documentGroups.put(title, new DocumentGroup(title, filePath, fullFilePath, array));
        }
    }
    
    public String getMainFile() {
        return this.mainFile;
    }
    
    public String getOutputDir() {
        return this.outputDir;
    }
    
    public String getFileSuffix() {
        return this.fileSuffix;
    }
    
    public String generateFullFilename(String filename) {
        return FileUtils.appendToPath(this.outputDir, filename + this.fileSuffix);
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
    
    public String getFullMainFilePath() {
        return this.fullMainFilePath;
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
    
    public boolean getOmmitEmptySections() {
        return this.ommitEmptySections;
    }
    
    public boolean getOmmitVoidReturnType() {
        return this.ommitVoidReturnType;
    }
    
    public List<String> getAnnotationsToBeRemoved() {
        return this.annotationsToBeRemoved;
    }
    
    public HashMap<String, DocumentGroup> getDocumentGroups() {
        return this.documentGroups;
    }
    
    public DocumentGroup findGroupForClass(String className) {
        for ( DocumentGroup group : this.documentGroups.values() ) {
            if ( group.getClasses().contains(className) ) {
                return group;
            }
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        String result = "Options:";
        result += LF + "mainFile = " + this.mainFile;
        result += LF + "outputDir = " + this.outputDir;
        result += LF + "fileSuffix = " + this.fileSuffix;
        result += LF + "fullMainFilePath = " + this.fullMainFilePath;
        result += LF + "documentTitle = " + this.documentTitle;
        result += LF + "documentHeader = " + this.documentHeader;
        result += LF + "minimumVisibility = " + this.minimumVisibility;
        result += LF + "noEnums = " + this.noEnums;
        result += LF + "noInterfaces = " + this.noInterfaces;
        result += LF + "noNestedClasses = " + this.noNestedClasses;
        result += LF + "includeHidden = " + this.includeHidden;
        result += LF + "ommitEmptySections = " + this.ommitEmptySections;
        result += LF + "annotationsToBeRemoved = {";
        
        for ( int i = 0; i < this.annotationsToBeRemoved.size(); i++ ) {
            result += LF + INDENT + this.annotationsToBeRemoved.get(i);
            if ( i < this.annotationsToBeRemoved.size() - 1 ) {
                result += COMMA;
            }
        }
        
        result += LF + "}";
        result += LF + "documentGroups = {";
        
        int i = 0;
        for ( DocumentGroup group : this.documentGroups.values() ) {
            result += LF + INDENT + group.toString();
            
            if ( i < this.documentGroups.size() - 1 ) {
                result += COMMA;
            }
            
            ++i;
        }
        
        result += LF + "}";
        return result;
    }
}
