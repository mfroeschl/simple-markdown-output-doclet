package org.froeschl.mddoclet;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.froeschl.mddoclet.Options.DocumentGroup;
import org.froeschl.mddoclet.formatter.Formatter;
import org.froeschl.mddoclet.printer.Printer;
import org.froeschl.mddoclet.utils.FileUtils;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

public class Layouter {
    public enum Mode {
        PREPARE,
        PRINT
    }
    
    private static final String SNAKE = "_";
    private static final String WS = " ";
    private static final String AT = "@";
    private static final String LF = "\n";
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String SHARP = "#";
    private static final String BROPEN = "(";
    private static final String BRCLOSE = ")";
    private static final String INDENT = "    ";
    
    private static final String LAYOUT_CLASS_LIST = "ClassList.layout";
    private static final String LAYOUT_CLASS_LIST_ITEMS_HEADER = "ClassListItemsHeader.layout";
    private static final String LAYOUT_CLASS_LIST_ITEM = "ClassListItem.layout";
    private static final String LAYOUT_CLASS_DESCRIPTION = "ClassDescription.layout";
    private static final String LAYOUT_METHOD_LIST = "MethodList.layout";
    private static final String LAYOUT_METHOD_LIST_ITEMS_HEADER = "MethodListItemsHeader.layout";
    private static final String LAYOUT_METHOD_LIST_ITEM = "MethodListItem.layout";
    private static final String LAYOUT_METHOD_DESCRIPTION = "MethodDescription.layout";
    private static final String LAYOUT_METHOD_RETURN_INFO = "MethodReturnInfo.layout";
    private static final String LAYOUT_METHOD_DETAIL_HEADER = "MethodDetailHeader.layout";
    private static final String LAYOUT_PARAMETER_LIST = "ParameterList.layout";
    private static final String LAYOUT_PARAMETER_LIST_ITEMS_HEADER = "ParameterListItemsHeader.layout";
    private static final String LAYOUT_PARAMETER_LIST_ITEM = "ParameterListItem.layout";
    private static final String LAYOUT_FIELD_LIST = "FieldList.layout";
    private static final String LAYOUT_FIELD_LIST_ITEMS_HEADER = "FieldListItemsHeader.layout";
    private static final String LAYOUT_FIELD_LIST_ITEM = "FieldListItem.layout";
    private static final String LAYOUT_EMPTY_LIST = "EmptyList.layout";
    private static final String LAYOUT_HEADER_AUTHOR = "AuthorHeader.layout";
    private static final String LAYOUT_HEADER_VERSION = "VersionHeader.layout";
    private static final String LAYOUT_HEADER_SINCE = "SinceHeader.layout";
    
    private static final String VAR_CLASS_LIST_ITEMS = "%CLASS_LIST_ITEMS%";
    private static final String VAR_CLASS_LIST_ITEMS_HEADER = "%CLASS_LIST_ITEMS_HEADER%";
    private static final String VAR_CLASS_LINK = "%CLASS_LINK%";
    private static final String VAR_CLASS_SHORT_DESCRIPTION = "%CLASS_SHORT_DESCRIPTION%";
    private static final String VAR_CLASS_LONG_DESCRIPTION = "%CLASS_LONG_DESCRIPTION%";
    private static final String VAR_CLASS_TITLE = "%CLASS_TITLE%";
    private static final String VAR_CLASS_SIGNATURE = "%CLASS_SIGNATURE%";
    private static final String VAR_CLASS_HIERARCHY = "%CLASS_HIERARCHY%";
    private static final String VAR_METHOD_LIST_ITEMS = "%METHOD_LIST_ITEMS%";
    private static final String VAR_METHOD_LIST_ITEMS_HEADER = "%METHOD_LIST_ITEMS_HEADER%";
    private static final String VAR_METHOD_LINK = "%METHOD_LINK%";
    private static final String VAR_METHOD_SHORT_DESCRIPTION = "%METHOD_SHORT_DESCRIPTION%";
    private static final String VAR_METHOD_LONG_DESCRIPTION = "%METHOD_LONG_DESCRIPTION%";
    private static final String VAR_METHOD_TITLE = "%METHOD_TITLE%";
    private static final String VAR_METHOD_SIGNATURE = "%METHOD_SIGNATURE%";
    private static final String VAR_METHOD_RETURN_TYPE = "%METHOD_RETURN_TYPE%";
    private static final String VAR_METHOD_RETURN_VALUE_DESCRIPTION = "%METHOD_RETURN_VALUE_DESCRIPTION%";
    private static final String VAR_PARAMETER_LIST_ITEMS = "%PARAMETER_LIST_ITEMS%";
    private static final String VAR_PARAMETER_LIST_ITEMS_HEADER = "%PARAMETER_LIST_ITEMS_HEADER%";
    private static final String VAR_PARAMETER_TYPE_AND_NAME = "%PARAMETER_TYPE_AND_NAME%";
    private static final String VAR_PARAMETER_SHORT_DESCRIPTION = "%PARAMETER_SHORT_DESCRIPTION%";
    private static final String VAR_PARAMETER_LONG_DESCRIPTION = "%PARAMETER_LONG_DESCRIPTION%";
    private static final String VAR_FIELD_LIST_ITEMS = "%FIELD_LIST_ITEMS%";
    private static final String VAR_FIELD_LIST_ITEMS_HEADER = "%FIELD_LIST_ITEMS_HEADER%";
    private static final String VAR_FIELD_NAME = "%FIELD_NAME%";
    private static final String VAR_FIELD_SHORT_DESCRIPTION = "%FIELD_SHORT_DESCRIPTION%";
    private static final String VAR_FIELD_LONG_DESCRIPTION = "%FIELD_LONG_DESCRIPTION%";
    private static final String VAR_TAG_AUTHOR = "%TAG_AUTHOR%";
    private static final String VAR_TAG_VERSION = "%TAG_VERSION%";
    private static final String VAR_TAG_SINCE = "%TAG_SINCE%";
    private static final String VAR_HEADER_AUTHOR = "%HEADER_AUTHOR%";
    private static final String VAR_HEADER_VERSION = "%HEADER_VERSION%";
    private static final String VAR_HEADER_SINCE = "%HEADER_SINCE%";
    
    private static final String HEADING_CLASS = "Class";
    private static final String HEADING_ENUM = "Enum";
    private static final String HEADING_INTERFACE = "Interface";
    private static final String EMPTY_BODY = "-";
    private static final String LAST_UPDATED = "Last updated";
    private static final String EXTENDS = "extends";
    private static final String IMPLEMENTS = "implements";
    private static final String ENUM = "enum";
    private static final String CLASS = "class";
    private static final String RETURN_TYPE_VOID = "void";
    
    private static final String TAG_LINK = "@link";
    private static final String TAG_RETURN = "@return";
    private static final String TAG_INCLUDE = "@include";
    private static final String TAG_AUTHOR = "@author";
    private static final String TAG_VERSION = "@version";
    private static final String TAG_SINCE = "@since";
    
    static private class TagInfo {
        private final String varHeader;
        private final String varValue;
        private final String layoutHeader;
        
        public TagInfo(String varHeader, String varValue, String layoutHeader) {
            this.varHeader = varHeader;
            this.varValue = varValue;
            this.layoutHeader = layoutHeader;
        }
    }
    
    final private Options options;
    final private Formatter formatter;
    final private Printer printer;
    private Mode mode = Mode.PREPARE;
    private Map<String, String> anchors = new HashMap<String, String>();
    private Map<String, TagInfo> tagInfos = new HashMap<String, TagInfo>();
    private String currentOutputFile = "";
    private String currentOutputFileFullPath = "";
    
    public Layouter(Options options, Formatter formatter, Printer printer) {
        if ( formatter == null || printer == null ) {
            throw new InvalidParameterException();
        }
        
        this.options = options;
        this.formatter = formatter;
        this.printer = printer;
        this.mode = Mode.PREPARE;
        this.tagInfos.put(TAG_AUTHOR, new TagInfo(VAR_HEADER_AUTHOR, VAR_TAG_AUTHOR, LAYOUT_HEADER_AUTHOR));
        this.tagInfos.put(TAG_VERSION, new TagInfo(VAR_HEADER_VERSION, VAR_TAG_VERSION, LAYOUT_HEADER_VERSION));
        this.tagInfos.put(TAG_SINCE, new TagInfo(VAR_HEADER_SINCE, VAR_TAG_SINCE, LAYOUT_HEADER_SINCE));
        this.currentOutputFile = this.options.getMainFile() + this.options.getFileSuffix();
        this.currentOutputFileFullPath = this.options.getFullMainFilePath();
    }
    
    public void setMode(Mode mode) {
        this.mode = mode;
        
        if ( this.mode == Mode.PREPARE) {
            this.anchors.clear();
        }
    }
    
    private void print(String text) {
        if ( this.mode == Mode.PRINT ) {
            this.printer.print(this.currentOutputFileFullPath, text);
        }
    }
    
    private void setCurrentOutputFileForClass(String className) {
        DocumentGroup group = this.options.findGroupForClass(className);
        if ( group == null ) {
            this.currentOutputFile = this.options.getMainFile() + this.options.getFileSuffix();
            this.currentOutputFileFullPath = this.options.getFullMainFilePath();
        } else {
            this.currentOutputFile = group.getFile();
            this.currentOutputFileFullPath = group.getFullFilePath();
        }
    }
    
    private static String toSingleLine(String string) {
        return string.replace(LF, WS);
    }
    
    private static String onlyFirstLine(String string) {
        String result = string;
        
        int position = string.indexOf(LF);
        if ( position >= 0 ) {
            result = string.substring(0, position);
        }
        
        return result;
    }
    
    private String createAnchor(String title, String anchor) {
        String currentOutputFile = new String(this.currentOutputFile);
        this.anchors.put(anchor, currentOutputFile);
        return this.formatter.anchor(title, anchor);
    }
    
    private String createLinkIfAnchorExists(String title, String anchor) {
        String link = "";
        String anchorOutputFile = this.anchors.get(anchor);
        
        if ( anchorOutputFile == null ) {
            link = title;
        } else {
            String fullAnchor = null;
            
            if ( anchorOutputFile.equals(this.currentOutputFile) ) {
                fullAnchor = SHARP + anchor;
            } else {
                fullAnchor = anchorOutputFile + SHARP + anchor;
            }
            
            link = this.formatter.link(title, fullAnchor);
        }
        
        return link;
    }
    
    private String includeFile(String includeFile) {
        if ( includeFile != null && includeFile.length() > 0 ) {
            String fullPath = FileUtils.appendToPath(this.options.getIncludeDir(), includeFile);
            String includeData = "";
            try {
                includeData = FileUtils.readFileIntoString(fullPath);
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            
            return this.formatter.paragraph(includeData);
        }
        
        return "";
    }
    
    private String loadLayoutFile(String layoutFile) {
        if ( layoutFile != null && layoutFile.length() > 0 ) {
            String fullPath = FileUtils.appendToPath(this.options.getLayoutDir(), layoutFile);
            String data = "";
            try {
                data = FileUtils.readFileIntoString(fullPath);
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            
            return data;
        }
        
        return "";
    }
    
    public void printDocumentTitleAndHeader() {
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd");
        
        String layoutedText = this.formatter.heading(this.options.getDocumentTitle(), Formatter.HEADING_TITLE);
        layoutedText += this.formatter.paragraph(LAST_UPDATED + WS + dateFormatter.format(date));
        layoutedText += this.formatter.horizontalRule();
        layoutedText += this.includeFile(this.options.getDocumentHeader()); 
        this.print(layoutedText);
    }
    
    public void printClassList(List<ClassDoc> classes) {
        if ( classes.size() == 0 ) {
            if ( this.options.getOmmitEmptySections() ) {
                return;
            }
        }
        
        String layout = this.loadLayoutFile(LAYOUT_CLASS_LIST);
        String header = "";
        String items = "";
        
        if ( classes.size() > 0 ) {
            header = this.loadLayoutFile(LAYOUT_CLASS_LIST_ITEMS_HEADER);
            items = this.createClassListItems(classes);
        } else {
            header = this.loadLayoutFile(LAYOUT_EMPTY_LIST);
            items = "";
        }
        
        if ( layout.contains(VAR_CLASS_LIST_ITEMS_HEADER) ) {
            layout = layout.replace(VAR_CLASS_LIST_ITEMS_HEADER, header);
        }
        
        if ( layout.contains(VAR_CLASS_LIST_ITEMS) ) {
            layout = layout.replace(VAR_CLASS_LIST_ITEMS, items);
        }
        
        this.print(layout);
    }
    
    private String createClassListItems(List<ClassDoc> classes) {
        String itemLayout = this.loadLayoutFile(LAYOUT_CLASS_LIST_ITEM);
        String classListItems = "";
        
        for ( ClassDoc classDoc : classes ) {
            String item = itemLayout;
            if ( item.contains(VAR_CLASS_LINK) ) {
                item = item.replace(VAR_CLASS_LINK, this.createLinkIfAnchorExists(classDoc.name(), classDoc.qualifiedName()));
            }
            
            if ( item.contains(VAR_CLASS_SHORT_DESCRIPTION) ) {
                item = item.replace(VAR_CLASS_SHORT_DESCRIPTION,  this.createTagDescription(classDoc.inlineTags(), true));
            }
            
            if ( item.contains(VAR_CLASS_LONG_DESCRIPTION) ) {
                item = item.replace(VAR_CLASS_LONG_DESCRIPTION, this.createTagDescription(classDoc.inlineTags(), false));
            }
            
            classListItems += item;
        }
        
        return classListItems;
    }
    
    public void printClassDescription(ClassDoc classDoc) {
        this.setCurrentOutputFileForClass(classDoc.name());
        String layout = this.loadLayoutFile(LAYOUT_CLASS_DESCRIPTION);
        
        if ( layout.contains(VAR_CLASS_TITLE) ) {
            layout = layout.replace(VAR_CLASS_TITLE, this.createClassTitle(classDoc));
        }
        
        if ( layout.contains(VAR_CLASS_SIGNATURE) ) {
            layout = layout.replace(VAR_CLASS_SIGNATURE, this.createClassSignature(classDoc));
        }
        
        if ( layout.contains(VAR_CLASS_HIERARCHY) ) {
            layout = layout.replace(VAR_CLASS_HIERARCHY, this.createClassHierarchy(classDoc));
        }
        
        if ( layout.contains(VAR_CLASS_SHORT_DESCRIPTION) ) {
            layout = layout.replace(VAR_CLASS_SHORT_DESCRIPTION, this.createTagDescription(classDoc.inlineTags(), true));
        }
        
        if ( layout.contains(VAR_CLASS_LONG_DESCRIPTION) ) {
            layout = layout.replace(VAR_CLASS_LONG_DESCRIPTION, this.createTagDescription(classDoc.inlineTags(), false));
        }
        
        for ( Tag tag : classDoc.tags() ) {
            TagInfo tagInfo = this.tagInfos.get(tag.name());
            
            if ( tagInfo == null ) {
                continue;
            }
            
            if ( layout.contains(tagInfo.varHeader) ) {
                layout = layout.replace(tagInfo.varHeader, this.loadLayoutFile(tagInfo.layoutHeader));
            }
            
            if ( layout.contains(tagInfo.varValue) ) {
                layout = layout.replace(tagInfo.varValue, tag.text());
            }
        }
        
        for ( TagInfo tagInfo : this.tagInfos.values() ) {
            if ( layout.contains(tagInfo.varHeader) ) {
                if ( this.options.getOmmitEmptySections() ) {
                    layout = layout.replace(tagInfo.varHeader, "");
                } else {
                    layout = layout.replace(tagInfo.varHeader, this.loadLayoutFile(tagInfo.layoutHeader));
                }
            }
            
            if ( layout.contains(tagInfo.varValue) ) {
                layout = layout.replace(tagInfo.varValue, "");
            }
        }
        
        this.print(layout);
    }
    
    private String createClassTitle(ClassDoc classDoc) {
        String descriptor = "";
        
        if ( DocHelper.isEnum(classDoc) ) {
            descriptor = HEADING_ENUM;
        } else if ( classDoc.isInterface() ) {
            descriptor = HEADING_INTERFACE;
        } else {
            descriptor = HEADING_CLASS;
        }
        
        String classTitle = descriptor + WS + classDoc.name();
        String anchor = this.createAnchor(classTitle, classDoc.qualifiedName());
        
        return anchor;
    }
    
    public void printClassIncludes(ClassDoc classDoc) {
        String includeFile = "";
        
        for ( Tag tag : classDoc.tags() ) {
            if ( tag.name().equals(TAG_INCLUDE) ) {
                includeFile = tag.text();
                break;
            }
        }
        
        this.print(this.includeFile(includeFile));
    }
    
    private String createClassSignature(ClassDoc classDoc) {
        boolean first = true;
        String signature = classDoc.modifiers() + WS;
        
        if ( classDoc.isEnum() ) {
            signature += ENUM + WS ;
        } else if ( classDoc.isInterface() ) {
            // signature += INTERFACE;
        } else {
            signature += CLASS + WS;
        }
        
        signature += classDoc.name();
        
        if ( classDoc.superclass() != null ) {
            signature += WS + EXTENDS + WS + classDoc.superclass().name();
        }
        
        for ( ClassDoc interfaceDoc : classDoc.interfaces() ) {
            if ( first ) {
                first = false;
                signature += WS + IMPLEMENTS + WS;
            } else {
                signature += COMMA + WS;
            }
            
            signature += interfaceDoc.name();
        }
        
        return signature;
    }
    
    private String createClassHierarchy(ClassDoc classDoc) {
        ArrayList<String> classNames = new ArrayList<String>();
        ClassDoc superclass = classDoc.superclass();
        String hierarchy = "";
        int indentCount = 0;
        boolean first = true;
        
        classNames.add(classDoc.qualifiedName());
        
        while ( superclass != null ) {
            classNames.add(superclass.qualifiedName());
            superclass = superclass.superclass();
        }
        
        ListIterator<String> iterator = classNames.listIterator(classNames.size());
        
        // Iterate in reverse.
        while( iterator.hasPrevious() ) {
            if ( first ) {
                first = false;
            } else {
                hierarchy += this.formatter.lineFeed();
            }
            
            for ( int i = 0; i < indentCount; i++ ) {
                hierarchy += INDENT;
            }
            
            hierarchy += iterator.previous();
            indentCount++;
        }
        
        return hierarchy;
    }
    
    public void printMethodList(List<MethodDoc> methods, List<String> annotationsToRemove) {
        int documentedMethodCount = DocHelper.countDocumentedMethods(methods);
        
        if ( documentedMethodCount == 0 ) {
            if ( this.options.getOmmitEmptySections() ) {
                return;
            }
        }
        
        String layout = this.loadLayoutFile(LAYOUT_METHOD_LIST);
        String header = "";
        String items = "";
        
        if ( documentedMethodCount > 0 ) {
            header = this.loadLayoutFile(LAYOUT_METHOD_LIST_ITEMS_HEADER);
            items = this.createMethodListItems(methods, annotationsToRemove);
        } else {
            header = this.loadLayoutFile(LAYOUT_EMPTY_LIST);
            items = "";
        }
        
        if ( layout.contains(VAR_METHOD_LIST_ITEMS_HEADER) ) {
            layout = layout.replace(VAR_METHOD_LIST_ITEMS_HEADER, header);
        }
        
        if ( layout.contains(VAR_METHOD_LIST_ITEMS) ) {
            layout = layout.replace(VAR_METHOD_LIST_ITEMS, items);
        }
        
        this.print(layout);
    }
    
    private String createMethodListItems(List<MethodDoc> methods, List<String> annotationsToRemove) {
        String itemLayout = this.loadLayoutFile(LAYOUT_METHOD_LIST_ITEM);
        String classListItems = "";
        
        for ( MethodDoc methodDoc : methods ) {
            if ( !DocHelper.isDocumented(methodDoc) ) {
                continue;
            }
            
            String item = itemLayout;
            if ( item.contains(VAR_METHOD_LINK) ) {
                boolean indent = false;
                boolean createLinks = false;
                String rawLink = Layouter.generateSnakeCaseFullMethodSignature(methodDoc);
                String fullMethodSignature = this.generateFullMethodSignatureWithParameterNames(methodDoc, annotationsToRemove, indent, createLinks);
                String formattedMethodLink = this.createLinkIfAnchorExists(fullMethodSignature, rawLink);
                item = item.replace(VAR_METHOD_LINK, formattedMethodLink);
            }
            
            if ( item.contains(VAR_METHOD_SHORT_DESCRIPTION) ) {
                item = item.replace(VAR_METHOD_SHORT_DESCRIPTION,  this.createTagDescription(methodDoc.inlineTags(), true));
            }
            
            if ( item.contains(VAR_METHOD_LONG_DESCRIPTION) ) {
                item = item.replace(VAR_METHOD_LONG_DESCRIPTION, this.createTagDescription(methodDoc.inlineTags(), false));
            }
            
            classListItems += item;
        }
        
        return classListItems;
    }
    
    public void printFieldList(List<FieldDoc> fields, List<String> annotationsToRemove) {
        int documentedFieldCount = DocHelper.countDocumentedFields(fields);
        
        if ( documentedFieldCount == 0 ) {
            if ( this.options.getOmmitEmptySections() ) {
                return;
            }
        }
        
        String layout = this.loadLayoutFile(LAYOUT_FIELD_LIST);
        String header = "";
        String items = "";
        
        if ( documentedFieldCount > 0 ) {
            header = this.loadLayoutFile(LAYOUT_FIELD_LIST_ITEMS_HEADER);
            items = this.createFieldListItems(fields, annotationsToRemove);
        } else {
            header = this.loadLayoutFile(LAYOUT_EMPTY_LIST);
            items = "";
        }
        
        if ( layout.contains(VAR_FIELD_LIST_ITEMS_HEADER) ) {
            layout = layout.replace(VAR_FIELD_LIST_ITEMS_HEADER, header);
        }
        
        if ( layout.contains(VAR_FIELD_LIST_ITEMS) ) {
            layout = layout.replace(VAR_FIELD_LIST_ITEMS, items);
        }
        
        this.print(layout);
    }
    
    private String createFieldListItems(List<FieldDoc> fields, List<String> annotationsToRemove) {
        String itemLayout = this.loadLayoutFile(LAYOUT_FIELD_LIST_ITEM);
        String classListItems = "";
        
        for ( FieldDoc fieldDoc : fields ) {
            if ( !DocHelper.isDocumented(fieldDoc) ) {
                continue;
            }
            
            String item = itemLayout;
            if ( item.contains(VAR_FIELD_NAME) ) {
                String rawLink = fieldDoc.qualifiedName();
                String formattedFieldLink = this.createLinkIfAnchorExists(fieldDoc.name(), rawLink);
                item = item.replace(VAR_FIELD_NAME, formattedFieldLink);
            }
            
            if ( item.contains(VAR_FIELD_SHORT_DESCRIPTION) ) {
                item = item.replace(VAR_FIELD_SHORT_DESCRIPTION,  Layouter.toSingleLine(this.createTagDescription(fieldDoc.inlineTags(), true)));
            }
            
            if ( item.contains(VAR_FIELD_LONG_DESCRIPTION) ) {
                item = item.replace(VAR_FIELD_LONG_DESCRIPTION, Layouter.toSingleLine(this.createTagDescription(fieldDoc.inlineTags(), false)));
            }
            
            classListItems += item;
        }
        
        return classListItems;
    }
    
    public void printAllMethods(List<MethodDoc> methodDocs, List<String> annotationsToRemove) {
        int documentedMethodCount = DocHelper.countDocumentedMethods(methodDocs);
        
        if ( documentedMethodCount == 0 ) {
            if ( this.options.getOmmitEmptySections() ) {
                return;
            }
        }
        
        String layout = this.loadLayoutFile(LAYOUT_METHOD_DETAIL_HEADER);
        
        if ( documentedMethodCount == 0 ) {
            layout += this.loadLayoutFile(LAYOUT_EMPTY_LIST);
        }
        
        this.print(layout);
        
        for ( MethodDoc methodDoc : methodDocs ) {
            this.printMethodInfo(methodDoc, this.options.getAnnotationsToBeRemoved());
        }
    }
    
    private void printMethodInfo(MethodDoc methodDoc, List<String> annotationsToRemove) {
        if ( !DocHelper.isDocumented(methodDoc) ) {
            return;
        }
        
        String layoutedText = this.createMethodDescription(methodDoc, annotationsToRemove);
        layoutedText += this.createParameterList(methodDoc, annotationsToRemove);
        layoutedText += this.createReturnInfo(methodDoc);
        layoutedText += this.createMethodIncludes(methodDoc);
        this.print(layoutedText);
    }
    
    private String createMethodDescription(MethodDoc methodDoc, List<String> annotationsToRemove) {
        String layout = this.loadLayoutFile(LAYOUT_METHOD_DESCRIPTION);
        
        if ( layout.contains(VAR_METHOD_TITLE) ) {
            String snakeCaseFullMethodSignature = Layouter.generateSnakeCaseFullMethodSignature(methodDoc);
            String formattedMethodAnchor = this.createAnchor(methodDoc.name(), snakeCaseFullMethodSignature);
            layout = layout.replace(VAR_METHOD_TITLE, formattedMethodAnchor);
        }
        
        if ( layout.contains(VAR_METHOD_SIGNATURE) ) {
            boolean indent = true;
            boolean createLinks = false;
            String fullMethodSignature = this.generateFullMethodSignatureWithParameterNames(methodDoc, annotationsToRemove, indent, createLinks);
            layout = layout.replace(VAR_METHOD_SIGNATURE, fullMethodSignature);
        }
        
        if ( layout.contains(VAR_METHOD_SHORT_DESCRIPTION) ) {
            layout = layout.replace(VAR_METHOD_SHORT_DESCRIPTION, this.createTagDescription(methodDoc.inlineTags(), true));
        }
        
        if ( layout.contains(VAR_METHOD_LONG_DESCRIPTION) ) {
            layout = layout.replace(VAR_METHOD_LONG_DESCRIPTION, this.createTagDescription(methodDoc.inlineTags(), false));
        }
        
        return layout;
    }
    
    public String createParameterList(MethodDoc methodDoc, List<String> annotationsToRemove) {
        if ( !DocHelper.hasParameters(methodDoc) ) {
            if ( this.options.getOmmitEmptySections() ) {
                return "";
            }
        }
        
        String layout = this.loadLayoutFile(LAYOUT_PARAMETER_LIST);
        String header = "";
        String items = "";
        
        if ( DocHelper.hasParameters(methodDoc) ) {
            header = this.loadLayoutFile(LAYOUT_PARAMETER_LIST_ITEMS_HEADER);
            items = this.createParameterListItems(methodDoc, annotationsToRemove);
        } else {
            header = this.loadLayoutFile(LAYOUT_EMPTY_LIST);
            items = "";
        }
        
        if ( layout.contains(VAR_PARAMETER_LIST_ITEMS_HEADER) ) {
            layout = layout.replace(VAR_PARAMETER_LIST_ITEMS_HEADER, header);
        }
        
        if ( layout.contains(VAR_PARAMETER_LIST_ITEMS) ) {
            layout = layout.replace(VAR_PARAMETER_LIST_ITEMS, items);
        }
        
        return layout;
    }
    
    private String createParameterListItems(MethodDoc methodDoc, List<String> annotationsToRemove) {
        String itemLayout = this.loadLayoutFile(LAYOUT_PARAMETER_LIST_ITEM);
        String classListItems = "";
        
        for ( int i = 0; i < methodDoc.parameters().length && i < methodDoc.paramTags().length; i++ ) {
            String item = itemLayout;
            Parameter parameter = methodDoc.parameters()[i];
            ParamTag paramTag = methodDoc.paramTags()[i];
            
            if ( item.contains(VAR_PARAMETER_TYPE_AND_NAME) ) {
                String name = this.generateParameterName(parameter, annotationsToRemove, true);
                item = item.replace(VAR_PARAMETER_TYPE_AND_NAME, name);
            }
            
            if ( item.contains(VAR_PARAMETER_SHORT_DESCRIPTION) ) {
                item = item.replace(VAR_PARAMETER_SHORT_DESCRIPTION,  this.createTagDescription(paramTag.inlineTags(), true));
            }
            
            if ( item.contains(VAR_PARAMETER_LONG_DESCRIPTION) ) {
                item = item.replace(VAR_PARAMETER_LONG_DESCRIPTION, this.createTagDescription(paramTag.inlineTags(), false));
            }
            
            classListItems += item;
        }
        
        return classListItems;
    }
    
    private String createTagDescription(Tag[] inlineTags, boolean shortDescription) {
        String description = "";
        
        for ( Tag inlineTag : inlineTags ) {
            if ( inlineTag.name().equals(TAG_LINK) ) {
                SeeTag linkTag = (SeeTag) inlineTag;
                ClassDoc classDoc = linkTag.referencedClass();
                MemberDoc memberDoc = linkTag.referencedMember();
                String label = linkTag.label();
                
                if ( memberDoc != null ) {
                    if ( label == null || label.isEmpty() ) {
                        label = memberDoc.containingClass().name() + DOT + memberDoc.name();
                    }
                    
                    if ( memberDoc.isMethod() ) {
                        label += BROPEN + BRCLOSE;
                        String anchor = Layouter.generateSnakeCaseFullMethodSignature((MethodDoc) memberDoc);
                        description += this.createLinkIfAnchorExists(label, anchor);
                    } else if ( memberDoc.isField() ) {
                        String anchor = memberDoc.qualifiedName();
                        description += this.createLinkIfAnchorExists(label, anchor);
                    } else {
                        description += linkTag.label();
                    }
                } else if ( classDoc != null ) {
                    if ( label == null || label.isEmpty() ) {
                        label = classDoc.name();
                    }
                    
                    String anchor = classDoc.qualifiedName();
                    description += this.createLinkIfAnchorExists(label, anchor);
                } else {
                    description += label;
                }
            } else {
                description += inlineTag.text();
            }
        }
        
        if ( shortDescription ) {
            description = Layouter.onlyFirstLine(description);
        } else {
            description = Layouter.toSingleLine(description);
        }
        
        return description;
    }
    
    private String createReturnInfo(MethodDoc methodDoc) {
        String returnType = methodDoc.returnType().typeName();
        
        if ( this.options.getOmmitVoidReturnType() ) {
            if ( returnType.equals(RETURN_TYPE_VOID) ) {
                return "";
            }
        }
        
        String layout = this.loadLayoutFile(LAYOUT_METHOD_RETURN_INFO);
        
        if ( layout.contains(VAR_METHOD_RETURN_TYPE) ) {
            layout = layout.replace(VAR_METHOD_RETURN_TYPE, returnType);
        }
        
        if ( layout.contains(VAR_METHOD_RETURN_VALUE_DESCRIPTION) ) {
            String returnDescription = "";
            
            for ( Tag tag : methodDoc.tags() ) {
                if ( tag.name().equals(TAG_RETURN) ) {
                    returnDescription = tag.text();
                    break;
                }
            }
            
            if ( returnDescription.isEmpty() ) {
                returnDescription = EMPTY_BODY;
            }
            
            layout = layout.replace(VAR_METHOD_RETURN_VALUE_DESCRIPTION, returnDescription);
        }
        
        return layout;
    }
    
    private String createMethodIncludes(MethodDoc methodDoc) {
        String includeFile = "";
        
        for ( Tag tag : methodDoc.tags() ) {
            if ( tag.name().equals(TAG_INCLUDE) ) {
                includeFile = tag.text();
                break;
            }
        }
        
        return this.includeFile(includeFile);
    }
    
    private String generateFullMethodSignatureWithParameterNames(MethodDoc methodDoc, List<String> annotationsToRemove, boolean indent, boolean generateLinks) {
        boolean first = true;
        String result = methodDoc.modifiers() + WS + methodDoc.returnType() + WS + methodDoc.name() + BROPEN;
        int indentSize = result.length();
        String indentText = "";
        
        for ( int i = 0; i < indentSize; i++) {
            indentText += WS;
        }
        
        for ( Parameter parameter : methodDoc.parameters() ) {
            if ( first ) {
                first = false;
            } else {
                result += COMMA;
                
                if ( indent ) {
                    result += this.formatter.lineFeed() + indentText;
                } else {
                    result += WS;
                }
            }
            
            result += this.generateParameterName(parameter, annotationsToRemove, generateLinks);
        }
        
        result += BRCLOSE;
        
        return result;
    }
    
    private String generateParameterName(Parameter parameter, List<String> annotationsToRemove, boolean generateLinks) {
        String result = "";
        
        for ( AnnotationDesc annotation : parameter.annotations() ) {
            boolean remove = false;
            
            for ( String annotationToRemove : annotationsToRemove ) {
                if ( annotation.annotationType().name().equals(annotationToRemove) ) {
                    remove = true;
                    break;
                }
            }
            
            if ( !remove ) {
                result += AT + annotation.annotationType().name() + WS;
            }
        }
        
        if ( generateLinks ) {
            result += this.createLinkIfAnchorExists(parameter.type().typeName(), parameter.type().qualifiedTypeName());
        } else {
            result += parameter.type().typeName();
        }
        
        result += WS + parameter.name();
        return result;
    }
    
    private static String generateSnakeCaseFullMethodSignature(MethodDoc methodDoc) {
        String fullMethodSignature = methodDoc.containingClass().qualifiedName() + WS + methodDoc.returnType() + WS + methodDoc.name() + methodDoc.flatSignature();
        fullMethodSignature = fullMethodSignature.replace(WS, SNAKE);
        fullMethodSignature = fullMethodSignature.replace(".", SNAKE);
        fullMethodSignature = fullMethodSignature.replace(" ", SNAKE);
        fullMethodSignature = fullMethodSignature.replace("(", SNAKE);
        fullMethodSignature = fullMethodSignature.replace(")", "");
        fullMethodSignature = fullMethodSignature.replace(",", "");
        
        return fullMethodSignature;
    }
}
