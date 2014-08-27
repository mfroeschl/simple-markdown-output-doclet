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
    private static final String VAR_TAG_AUTHOR = "%TAG_AUTHOR%";
    private static final String VAR_TAG_VERSION = "%TAG_VERSION%";
    private static final String VAR_TAG_SINCE = "%TAG_SINCE%";
    
    private static final String HEADING_CLASS = "Class";
    private static final String HEADING_ENUM = "Enum";
    private static final String HEADING_INTERFACE = "Interface";
    private static final String HEADING_PARAMETERS = "Parameters";
    private static final String HEADING_PARAMETER = "Parameter";
    private static final String HEADING_FIELD_SUMMARY = "Field Summary";
    private static final String HEADING_FIELD = "Field";
    private static final String HEADING_RETURNS = "Returns";
    private static final String HEADING_RETURN_TYPE = "Return Type";
    private static final String HEADING_DESCRIPTION = "Description";
    private static final String EMPTY_BODY = "-";
    private static final String LAST_UPDATED = "Last updated";
    private static final String EXTENDS = "extends";
    private static final String IMPLEMENTS = "implements";
    private static final String ENUM = "enum";
    private static final String CLASS = "class";
    
    private static final String TAG_LINK = "@link";
    private static final String TAG_RETURN = "@return";
    private static final String TAG_INCLUDE = "@include";
    private static final String TAG_AUTHOR = "@author";
    private static final String TAG_VERSION = "@version";
    private static final String TAG_SINCE = "@since";
    
    final private Options options;
    final private Formatter formatter;
    final private Printer printer;
    private Mode mode = Mode.PREPARE;
    private Map<String, String> anchors = new HashMap<String, String>();
    private Map<String, String> tagToVar = new HashMap<String, String>();
    
    public Layouter(Options options, Formatter formatter, Printer printer) {
        if ( formatter == null || printer == null ) {
            throw new InvalidParameterException();
        }
        
        this.options = options;
        this.formatter = formatter;
        this.printer = printer;
        this.mode = Mode.PREPARE;
        this.tagToVar.put(TAG_AUTHOR, VAR_TAG_AUTHOR);
        this.tagToVar.put(TAG_VERSION, VAR_TAG_VERSION);
        this.tagToVar.put(TAG_SINCE, VAR_TAG_SINCE);
    }
    
    public void setMode(Mode mode) {
        this.mode = mode;
        
        if ( this.mode == Mode.PREPARE) {
            this.anchors.clear();
        }
    }
    
    private void print(String text) {
        if ( this.mode == Mode.PRINT ) {
            this.printer.print(text);
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
        this.anchors.put(anchor, title);
        return this.formatter.anchor(title, anchor);
    }
    
    private String createLinkIfAnchorExists(String title, String anchor) {
        String link = "";
        String label = this.anchors.get(anchor);
        if ( label == null ) {
            link = title;
        } else {
            link = this.formatter.link(title, anchor);
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
        String layout = this.loadLayoutFile(LAYOUT_CLASS_LIST);
        
        if ( classes.size() > 0 ) {
            String classListItemsHeader = this.loadLayoutFile(LAYOUT_CLASS_LIST_ITEMS_HEADER);
            layout = layout.replace(VAR_CLASS_LIST_ITEMS_HEADER, classListItemsHeader);
        } else {
            layout = layout.replace(VAR_CLASS_LIST_ITEMS_HEADER, "");
        }
        
        if ( layout.contains(VAR_CLASS_LIST_ITEMS) ) {
            layout = layout.replace(VAR_CLASS_LIST_ITEMS, this.createClassListItems(classes));
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
            String tagVar = this.tagToVar.get(tag.name());
            
            if ( tagVar == null ) {
                continue;
            }
            
            if ( layout.contains(tagVar) ) {
                layout = layout.replace(tagVar, tag.text());
            }
        }
        
        for ( String tagVar : this.tagToVar.values() ) {
            layout = layout.replace(tagVar, "");
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
        String layout = this.loadLayoutFile(LAYOUT_METHOD_LIST);
        
        if ( documentedMethodCount > 0 ) {
            String methodListItemsHeader = this.loadLayoutFile(LAYOUT_METHOD_LIST_ITEMS_HEADER);
            layout = layout.replace(VAR_METHOD_LIST_ITEMS_HEADER, methodListItemsHeader);
        } else {
            layout = layout.replace(VAR_METHOD_LIST_ITEMS_HEADER, "");
        }
        
        if ( layout.contains(VAR_METHOD_LIST_ITEMS) ) {
            layout = layout.replace(VAR_METHOD_LIST_ITEMS, this.createMethodListItems(methods, annotationsToRemove));
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
        String layoutedText = this.formatter.heading(HEADING_FIELD_SUMMARY, Formatter.HEADING_TWO);
        
        int documentedFieldCount = DocHelper.countDocumentedFields(fields);
        
        if ( documentedFieldCount > 0 ) {
            layoutedText += this.formatter.tableHeader(HEADING_FIELD, HEADING_DESCRIPTION);
        } else {
            layoutedText += this.formatter.paragraph(EMPTY_BODY);
        }
        
        for ( FieldDoc fieldDoc : fields ) {
            if ( !DocHelper.isDocumented(fieldDoc) ) {
                continue;
            }
            
            String rawLink = fieldDoc.qualifiedName();
            String formattedFieldLink = this.createLinkIfAnchorExists(fieldDoc.name(), rawLink);
            String fieldDescription = Layouter.toSingleLine(this.createTagDescription(fieldDoc.inlineTags(), true));
            layoutedText += this.formatter.tableRow(formattedFieldLink, fieldDescription);
        }
        
        this.print(layoutedText);
    }
    
    public void printMethodInfo(MethodDoc methodDoc, List<String> annotationsToRemove) {
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
        boolean indent = true;
        boolean createLinks = false;
        String snakeCaseFullMethodSignature = Layouter.generateSnakeCaseFullMethodSignature(methodDoc);
        String fullMethodSignature = this.generateFullMethodSignatureWithParameterNames(methodDoc, annotationsToRemove, indent, createLinks);
        String formattedMethodAnchor = this.createAnchor(methodDoc.name(), snakeCaseFullMethodSignature);
        String layoutedText = this.formatter.heading(formattedMethodAnchor, Formatter.HEADING_TWO);
        layoutedText += this.formatter.codeBlock(fullMethodSignature);
        layoutedText += this.formatter.paragraph(this.createTagDescription(methodDoc.inlineTags(), false));
        return layoutedText;
    }
    
    private String createParameterList(MethodDoc methodDoc, List<String> annotationsToRemove) {
        String layoutedText = "";
        
        if ( methodDoc.parameters().length > 0 && methodDoc.paramTags().length > 0 ) {
            layoutedText += this.formatter.heading(HEADING_PARAMETERS, Formatter.HEADING_THREE);
            layoutedText += this.formatter.tableHeader(HEADING_PARAMETER, HEADING_DESCRIPTION);
        }
        
        for ( int i = 0; i < methodDoc.parameters().length && i < methodDoc.paramTags().length; i++ ) {
            Parameter parameter = methodDoc.parameters()[i];
            ParamTag paramTag = methodDoc.paramTags()[i];
            String name = this.generateParameterName(parameter, annotationsToRemove, true);
            String description = this.createTagDescription(paramTag.inlineTags(), true);
            description = Layouter.toSingleLine(description);
            layoutedText += this.formatter.tableRow(name, description);
        }
        
        return layoutedText;
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
        String layoutedText = this.formatter.heading(HEADING_RETURNS, Formatter.HEADING_THREE);
        layoutedText += this.formatter.tableHeader(HEADING_RETURN_TYPE, HEADING_DESCRIPTION);
        String returnDescription = "";
        
        for ( Tag tag : methodDoc.tags() ) {
            if ( tag.name().equals(TAG_RETURN) ) {
                returnDescription = tag.text();
                break;
            }
        }
        
        layoutedText += this.formatter.tableRow(methodDoc.returnType().typeName(), returnDescription);
        return layoutedText;
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
