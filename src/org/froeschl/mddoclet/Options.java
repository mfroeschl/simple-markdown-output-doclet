package org.froeschl.mddoclet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    
    private static class GroupInfo {
        private final String alias;
        private final String description;
        
        public GroupInfo(String alias, String description) {
            this.alias = alias;
            this.description = description;
        }
    }
    
    public static final String KEY_DEFAULT_DOCUMENT_GROUP = "-defaultGroup";
    public static final String KEY_OUTPUT_DIR = "-outputdir";
    public static final String KEY_FILE_SUFFIX = "-filesuffix";
    public static final String KEY_INCLUDE_DIR = "-includedir";
    public static final String KEY_LAYOUT_DIR = "-layoutdir";
    public static final String KEY_DOCUMENT_TITLE = "-doctitle";
    public static final String KEY_MINIMUM_VISIBILITY = "-visibility";
    public static final String KEY_NO_INTERFACES = "-nointerfaces";
    public static final String KEY_NO_ENUMS = "-noenums";
    public static final String KEY_NO_NESTED_CLASSES = "-nonested";
    public static final String KEY_INCLUDE_HIDDEN = "-includehidden";
    public static final String KEY_OMMIT_EMPTY_SECTIONS = "-ommitempty";
    public static final String KEY_OMMIT_VOID_RETURN_TYPE = "-ommitvoid";
    public static final String KEY_SORT_GROUPS = "-sortgroups";
    public static final String KEY_SORT_CLASSES = "-sortclasses";
    public static final String KEY_SORT_METHODS = "-sortmethods";
    public static final String KEY_SORT_FIELDS = "-sortfields";
    public static final String KEY_ANNOTATIONS_TO_BE_REMOVED = "-removeannotations";
    public static final String KEY_GROUP_INFOS = "-groupinfo";
    
    private static final String LF = "\n";
    private static final String INDENT = "    ";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String SEMICOLON = ";";
    private static final String DEFAULT_DEFAULT_DOCUMENT_GROUP = "index";
    private static final String DEFAULT_OUTPUT_DIR = ".";
    private static final String DEFAULT_FILE_SUFFIX = ".md";
    private static final String DEFAULT_INCLUDE_DIR = "./include";
    private static final String DEFAULT_LAYOUT_DIR = "./layouts/markdown";
    private static final String DEFAULT_DOCUMENT_TITLE = "Documentation";
    private static final Visibility DEFAULT_MINIMUM_VISIBILITY = Visibility.PUBLIC;
    private static final boolean DEFAULT_NO_ENUMS = false;
    private static final boolean DEFAULT_NO_INTERFACES = false;
    private static final boolean DEFAULT_NO_NESTED_CLASSES = false;
    private static final boolean DEFAULT_INCLUDE_HIDDEN = false;
    private static final boolean DEFAULT_OMMIT_EMPTY_SECTIONS = false;
    private static final boolean DEFAULT_OMMIT_VOID_RETURN_TYPE = false;
    private static final boolean DEFAULT_SORT_GROUPS = false;
    private static final boolean DEFAULT_SORT_CLASSES = false;
    private static final boolean DEFAULT_SORT_METHODS = false;
    private static final boolean DEFAULT_SORT_FIELDS = false;
    private static final ArrayList<String> DEFAULT_ANNOTATIONS_TO_BE_REMOVED = new ArrayList<String>();
    private static final HashMap<String, GroupInfo> DEFAULT_GROUP_INFOS = new HashMap<String, GroupInfo>();
    
    public static Map<String, Integer> optionToOptionLength;
    
    static {
        Options.optionToOptionLength = new HashMap<String, Integer>();
        Options.optionToOptionLength.put(KEY_DEFAULT_DOCUMENT_GROUP, 2);
        Options.optionToOptionLength.put(KEY_OUTPUT_DIR, 2);
        Options.optionToOptionLength.put(KEY_FILE_SUFFIX, 2);
        Options.optionToOptionLength.put(KEY_INCLUDE_DIR, 2);
        Options.optionToOptionLength.put(KEY_LAYOUT_DIR, 2);
        Options.optionToOptionLength.put(KEY_DOCUMENT_TITLE, 2);
        Options.optionToOptionLength.put(KEY_MINIMUM_VISIBILITY, 2);
        Options.optionToOptionLength.put(KEY_ANNOTATIONS_TO_BE_REMOVED, 2);
        Options.optionToOptionLength.put(KEY_GROUP_INFOS, 2);
        Options.optionToOptionLength.put(KEY_NO_ENUMS, 1);
        Options.optionToOptionLength.put(KEY_NO_INTERFACES, 1);
        Options.optionToOptionLength.put(KEY_NO_NESTED_CLASSES, 1);
        Options.optionToOptionLength.put(KEY_INCLUDE_HIDDEN, 1);
        Options.optionToOptionLength.put(KEY_OMMIT_EMPTY_SECTIONS, 1);
        Options.optionToOptionLength.put(KEY_OMMIT_VOID_RETURN_TYPE, 1);
        Options.optionToOptionLength.put(KEY_SORT_GROUPS, 1);
        Options.optionToOptionLength.put(KEY_SORT_CLASSES, 1);
        Options.optionToOptionLength.put(KEY_SORT_METHODS, 1);
        Options.optionToOptionLength.put(KEY_SORT_FIELDS, 1);
    }
    
    private String defaultDocumentGroup = DEFAULT_DEFAULT_DOCUMENT_GROUP;
    private String outputDir = DEFAULT_OUTPUT_DIR;
    private String fileSuffix = DEFAULT_FILE_SUFFIX;
    private String includeDir = DEFAULT_INCLUDE_DIR;
    private String layoutDir = DEFAULT_LAYOUT_DIR;
    private String documentTitle = DEFAULT_DOCUMENT_TITLE;
    private Visibility minimumVisibility = DEFAULT_MINIMUM_VISIBILITY;
    private boolean noEnums = DEFAULT_NO_ENUMS;
    private boolean noInterfaces = DEFAULT_NO_INTERFACES;
    private boolean noNestedClasses = DEFAULT_NO_NESTED_CLASSES;
    private boolean includeHidden = DEFAULT_INCLUDE_HIDDEN;
    private boolean ommitEmptySections = DEFAULT_OMMIT_EMPTY_SECTIONS;
    private boolean ommitVoidReturnType = DEFAULT_OMMIT_VOID_RETURN_TYPE;
    private boolean sortGroups = DEFAULT_SORT_GROUPS;
    private boolean sortClasses = DEFAULT_SORT_CLASSES;
    private boolean sortMethods = DEFAULT_SORT_METHODS;
    private boolean sortFields = DEFAULT_SORT_FIELDS;
    private ArrayList<String> annotationsToBeRemoved = DEFAULT_ANNOTATIONS_TO_BE_REMOVED;
    private HashMap<String, GroupInfo> groupInfos = DEFAULT_GROUP_INFOS;
    
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
            
            if ( option[0].equals(KEY_DEFAULT_DOCUMENT_GROUP) ) {
                this.defaultDocumentGroup = option[1];
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
            } else if ( option[0].equals(KEY_MINIMUM_VISIBILITY) ) {
                this.minimumVisibility = Visibility.fromString(option[1]);
            } else if ( option[0].equals(KEY_ANNOTATIONS_TO_BE_REMOVED) ) {
                this.parseAnnotationsToBeRemoved(option[1]);
            } else if ( option[0].equals(KEY_GROUP_INFOS) ) {
                this.parseGroupInfos(option[1]);
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
            } else if ( option[0].equals(KEY_SORT_GROUPS) ) {
                this.sortGroups = true;
            } else if ( option[0].equals(KEY_SORT_CLASSES) ) {
                this.sortClasses = true;
            } else if ( option[0].equals(KEY_SORT_METHODS) ) {
                this.sortMethods = true;
            } else if ( option[0].equals(KEY_SORT_FIELDS) ) {
                this.sortFields = true;
            }
        }
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
    
    private void parseGroupInfos(String input) {
        // Group:Alias:Description;
        
        this.groupInfos.clear();
        
        String[] groups = input.split(SEMICOLON);
        for ( String group : groups ) {
            if ( group.isEmpty() ) {
                continue;
            }
            
            String[] tags = group.split(COLON);
            
            if ( tags.length != 3 || tags[0] == null || tags[0].isEmpty() || tags[1] == null || tags[1].isEmpty() || tags[2] == null || tags[2].isEmpty() ) {
                System.out.println("parseGroupInfos() Invalid format: \"" + group + "\"");
                continue;
            }
            
            this.groupInfos.put(tags[0], new GroupInfo(tags[1], tags[2]));
        }
    }
    
    public String getDefaultDocumentGroup() {
        return this.defaultDocumentGroup;
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
    
    public boolean getSortGroups() {
        return this.sortGroups;
    }
    
    public boolean getSortClasses() {
        return this.sortClasses;
    }
    
    public boolean getSortMethods() {
        return this.sortMethods;
    }
    
    public boolean getSortFields() {
        return this.sortFields;
    }
    
    public List<String> getAnnotationsToBeRemoved() {
        return this.annotationsToBeRemoved;
    }
    
    public String getAliasForGroup(String groupId) {
        GroupInfo groupInfo = this.groupInfos.get(groupId);
        
        if ( groupInfo == null ) {
            return groupId;
        } else {
            return groupInfo.alias;
        }
    }
    
    public String getDescriptionForGroup(String groupId) {
        GroupInfo groupInfo = this.groupInfos.get(groupId);
        
        if ( groupInfo == null ) {
            return "";
        } else {
            return groupInfo.description;
        }
    }
    
    @Override
    public String toString() {
        String result = "Options:";
        result += LF + "defaultDocumentGroup = " + this.defaultDocumentGroup;
        result += LF + "outputDir = " + this.outputDir;
        result += LF + "fileSuffix = " + this.fileSuffix;
        result += LF + "documentTitle = " + this.documentTitle;
        result += LF + "minimumVisibility = " + this.minimumVisibility;
        result += LF + "noEnums = " + this.noEnums;
        result += LF + "noInterfaces = " + this.noInterfaces;
        result += LF + "noNestedClasses = " + this.noNestedClasses;
        result += LF + "includeHidden = " + this.includeHidden;
        result += LF + "ommitEmptySections = " + this.ommitEmptySections;
        result += LF + "ommitVoidReturnType = " + this.ommitVoidReturnType;
        result += LF + "sortGroups = " + this.sortGroups;
        result += LF + "sortClasses = " + this.sortClasses;
        result += LF + "sortMethods = " + this.sortMethods;
        result += LF + "sortFields = " + this.sortFields;
        result += LF + "annotationsToBeRemoved = {";
        
        for ( int i = 0; i < this.annotationsToBeRemoved.size(); i++ ) {
            result += LF + INDENT + this.annotationsToBeRemoved.get(i);
            if ( i < this.annotationsToBeRemoved.size() - 1 ) {
                result += COMMA;
            }
        }
        
        result += LF + "}";
        result += LF + "groupInfos = {";
        
        for ( Entry<String, GroupInfo> entry : this.groupInfos.entrySet() ) {
            result += LF + INDENT + entry.getKey() + ": " + entry.getValue().alias + ", " + entry.getValue().description + ";";
        }
        
        result += LF + "}";
        return result;
    }
}
