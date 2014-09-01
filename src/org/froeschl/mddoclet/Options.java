package org.froeschl.mddoclet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.froeschl.mddoclet.utils.FileUtils;

import com.sun.javadoc.DocErrorReporter;
import com.sun.media.sound.InvalidFormatException;

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
    
    private static class OptionInfo {
        private final int argumentCount;
        private final String argument;
        private final String helpText;
        
        public OptionInfo(int argumentCount, String argument, String helpText) {
            this.argumentCount = argumentCount;
            this.argument = argument;
            this.helpText = helpText;
        }
    }
    
    private static final String KEY_DEFAULT_DOCUMENT_GROUP = "-defaultGroup";
    private static final String KEY_OUTPUT_DIR = "-outputdir";
    private static final String KEY_FILE_SUFFIX = "-filesuffix";
    private static final String KEY_INCLUDE_DIR = "-includedir";
    private static final String KEY_LAYOUT_DIR = "-layoutdir";
    private static final String KEY_MINIMUM_VISIBILITY = "-visibility";
    private static final String KEY_NO_INTERFACES = "-nointerfaces";
    private static final String KEY_NO_ENUMS = "-noenums";
    private static final String KEY_NO_NESTED_CLASSES = "-nonested";
    private static final String KEY_INCLUDE_HIDDEN = "-includehidden";
    private static final String KEY_OMMIT_EMPTY_SECTIONS = "-ommitempty";
    private static final String KEY_OMMIT_VOID_RETURN_TYPE = "-ommitvoid";
    private static final String KEY_SORT_GROUPS = "-sortgroups";
    private static final String KEY_SORT_CLASSES = "-sortclasses";
    private static final String KEY_SORT_METHODS = "-sortmethods";
    private static final String KEY_SORT_FIELDS = "-sortfields";
    private static final String KEY_SHOW_HELP = "-help";
    private static final String KEY_ANNOTATIONS_TO_BE_REMOVED = "-removeannotations";
    private static final String KEY_GROUP_INFOS = "-groupinfo";
    private static final int INDENT_POSITION = 26;
    
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
    private static final boolean DEFAULT_SHOW_HELP = false;
    private static final ArrayList<String> DEFAULT_ANNOTATIONS_TO_BE_REMOVED = new ArrayList<String>();
    private static final HashMap<String, GroupInfo> DEFAULT_GROUP_INFOS = new HashMap<String, GroupInfo>();
    
    public static HashMap<String, OptionInfo> options;
    
    static {
        Options.options = new HashMap<String, OptionInfo>();
        Options.options.put(KEY_DEFAULT_DOCUMENT_GROUP, new OptionInfo(2, "<name>", "Name of the default document group."));
        Options.options.put(KEY_OUTPUT_DIR, new OptionInfo(2, "<path>", "Path to the output directory."));
        Options.options.put(KEY_FILE_SUFFIX, new OptionInfo(2, "<extension>", "File extension for output files."));
        Options.options.put(KEY_INCLUDE_DIR, new OptionInfo(2, "<path>", "Path to the input directory."));
        Options.options.put(KEY_LAYOUT_DIR, new OptionInfo(2, "<path>", "Path to the layout directory."));
        Options.options.put(KEY_MINIMUM_VISIBILITY, new OptionInfo(2, "<visibility>", "Minimum visibility for elements to be included."));
        Options.options.put(KEY_ANNOTATIONS_TO_BE_REMOVED, new OptionInfo(2, "<list>", "List of annotations to be removed."));
        Options.options.put(KEY_GROUP_INFOS, new OptionInfo(2, "<list>", "Semi-colon sepatared list of group infos."));
        Options.options.put(KEY_NO_ENUMS, new OptionInfo(1, "", "When specified, enums will not be included."));
        Options.options.put(KEY_NO_INTERFACES, new OptionInfo(1, "", "When specified, interfaces will not be included."));
        Options.options.put(KEY_NO_NESTED_CLASSES, new OptionInfo(1, "", "When specified, nested classes will not be included."));
        Options.options.put(KEY_INCLUDE_HIDDEN, new OptionInfo(1, "", "When specified, elements containing \"@hidden'\" tag will not be included."));
        Options.options.put(KEY_OMMIT_EMPTY_SECTIONS, new OptionInfo(1, "", "When specified, empty document sections will be ommitted."));
        Options.options.put(KEY_OMMIT_VOID_RETURN_TYPE, new OptionInfo(1, "", "When specified, void return types will not be documented."));
        Options.options.put(KEY_SORT_GROUPS, new OptionInfo(1, "", "When specified, document groups will be sorted alphabetycally."));
        Options.options.put(KEY_SORT_CLASSES, new OptionInfo(1, "", "When specified, classes will be sorted alphabetycally."));
        Options.options.put(KEY_SORT_METHODS, new OptionInfo(1, "", "When specified, methods will be sorted alphabetycally."));
        Options.options.put(KEY_SORT_FIELDS, new OptionInfo(1, "", "When specified, fields will be sorted alphabetycally."));
        Options.options.put(KEY_SHOW_HELP, new OptionInfo(1, "", "When specified, displays this help text."));
    }
    
    private String defaultDocumentGroup = DEFAULT_DEFAULT_DOCUMENT_GROUP;
    private String outputDir = DEFAULT_OUTPUT_DIR;
    private String fileSuffix = DEFAULT_FILE_SUFFIX;
    private String includeDir = DEFAULT_INCLUDE_DIR;
    private String layoutDir = DEFAULT_LAYOUT_DIR;
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
    private boolean showHelp = DEFAULT_SHOW_HELP;
    private ArrayList<String> annotationsToBeRemoved = DEFAULT_ANNOTATIONS_TO_BE_REMOVED;
    private HashMap<String, GroupInfo> groupInfos = DEFAULT_GROUP_INFOS;
    
    private Options() {
    }
    
    public static Options fromCommandLine(String[][] input) throws InvalidFormatException {
        Options options = new Options();
        options.parseCommandLine(input);
        return options;
    }
    
    public static int getOptionLength(String option) {
        OptionInfo optionInfo = Options.options.get(option);
        
        if ( optionInfo == null ) {
            return 0;
        } else if ( KEY_SHOW_HELP.equals(option) ) {
            System.out.println("Printing options (usage)");
            Options.printHelp();
            return 0;
        } else {
            return optionInfo.argumentCount;
        }
    }
    
    public static boolean validOptions(String input[][], 
            DocErrorReporter reporter) {
        try {
            Options options = new Options();
            options.parseCommandLine(input);
        } catch ( InvalidFormatException e ) {
            reporter.printError(e.getMessage());
            System.out.println("Printing options (format exception)");
            Options.printHelp();
            return false;
        }
        
        return true;
    }
    
    public static void printHelp() {
        System.out.println("MarkdownOutputDoclet options:");
        for ( Entry<String, OptionInfo> entry : Options.options.entrySet() ) {
            System.out.println(Options.formatHelpText(entry.getKey(), entry.getValue().argument, entry.getValue().helpText));
        }
    }
    
    private static String formatHelpText(String option, String argument, String helpText) {
        boolean hasSpacing = false;
        String output = option + " " + argument;
        int missing = INDENT_POSITION - output.length();
        
        for ( int i = 0; i < missing; i++ ) {
            hasSpacing = true;
            output += " ";
        }
        
        if ( !hasSpacing ) {
            output += " ";
        }
        
        output += helpText;
        return output;
    }
    
    public void parseCommandLine(String[][] options) throws InvalidFormatException {
        
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
            } else if ( option[0].equals(KEY_SHOW_HELP) ) {
                this.showHelp = true;
            }
        }
    }
    
    private void parseAnnotationsToBeRemoved(String input) throws InvalidFormatException {
        this.annotationsToBeRemoved.clear();
        String[] annotations = input.split(COMMA);
        
        if ( annotations.length == 0 ) {
            throw new InvalidFormatException("No Annotation.\nAnnotations must be specified as a comma separated list. Example: NonNull,Nullable");
        }
        
        for ( String annotation : annotations ) {
            if ( annotation.isEmpty() ) {
                throw new InvalidFormatException("Empty Annotation.\nAnnotations must be specified as a comma separated list. Example: NonNull,Nullable");
            }
            
            this.annotationsToBeRemoved.add(annotation);
        }
    }
    
    private void parseGroupInfos(String input) throws InvalidFormatException {
        // Group:Alias:Description;
        
        this.groupInfos.clear();
        
        String[] groups = input.split(SEMICOLON);
        if ( groups.length == 0 ) {
            throw new InvalidFormatException("No group infos.\nGroup infos must be specified as a semi-colon separated list of group id, alias and description (divided by colon). Example: MyGroupId:My Group Alias:My Group Description;");
        }
        
        for ( String group : groups ) {
            if ( group.isEmpty() ) {
                throw new InvalidFormatException("Empty group info.\nGroup infos must be specified as a semi-colon separated list of group id, alias and description (divided by colon). Example: MyGroupId:My Group Alias:My Group Description;");
            }
            
            String[] tags = group.split(COLON);
            
            if ( tags.length != 3 || tags[0] == null || tags[0].isEmpty() || tags[1] == null || tags[1].isEmpty() || tags[2] == null || tags[2].isEmpty() ) {
                throw new InvalidFormatException("Invalid GroupInfo Format: \"" + group + "\"\nGroup infos must be specified as a semi-colon separated list of group id, alias and description (divided by colon). Example: MyGroupId:My Group Alias:My Group Description;");
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
    
    public boolean getShowHelp() {
        return this.showHelp;
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
