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
    private static final String COMMA = ",";
    private static final String BROPEN = "(";
    private static final String BRCLOSE = ")";
    private static final String INDENT = "    ";
    
    private static final String HEADING_CLASSES = "Classes";
    private static final String HEADING_CLASS = "Class";
    private static final String HEADING_METHOD_SUMMARY = "Method Summary";
    private static final String HEADING_METHOD_DETAIL = "Method Detail";
    private static final String HEADING_METHOD = "Method";
    private static final String HEADING_PARAMETERS = "Parameters";
    private static final String HEADING_PARAMETER = "Parameter";
    private static final String HEADING_RETURNS = "Returns";
    private static final String HEADING_RETURN_TYPE = "Return Type";
    private static final String HEADING_DESCRIPTION = "Description";
    private static final String HEADING_HIERARCHY = "Hierarchy:";
    private static final String LAST_UPDATED = "Last updated";
    private static final String EXTENDS = "extends";
    private static final String IMPLEMENTS = "implements";
    private static final String ENUM = "enum";
    private static final String INTERFACE = "interface";
    private static final String CLASS = "class";
    private static final String LINK_TAG = "@link";
    private static final String RETURN_TAG = "@return";
    private static final String INCLUDE_TAG = "@include";
    
    final private Options options;
    final private Formatter formatter;
    final private Printer printer;
    private Mode mode = Mode.PREPARE;
    private Map<String, String> tagToName = new HashMap<String, String>();
    private Map<String, String> anchors = new HashMap<String, String>();
    
    public Layouter(Options options, Formatter formatter, Printer printer) {
        if ( formatter == null || printer == null ) {
            throw new InvalidParameterException();
        }
        
        this.options = options;
        this.formatter = formatter;
        this.printer = printer;
        this.mode = Mode.PREPARE;
        this.tagToName.put("@author", "Author:");
        this.tagToName.put("@version", "Version:");
        this.tagToName.put("@since", "Since:");
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
    
    private static boolean isDocumented(MethodDoc methodDoc) {
        return (methodDoc.paramTags().length > 0 || methodDoc.tags().length > 0 || methodDoc.inlineTags().length > 0);
    }
    
    private static int countDocumentedMethods(List<MethodDoc> methods) {
        int count = 0;
        
        for ( MethodDoc methodDoc : methods ) {
            if ( Layouter.isDocumented(methodDoc)) {
                count++;
            }
        }
        
        return count;
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
        String layoutedText = this.formatter.horizontalRule();
        layoutedText += this.formatter.heading(HEADING_CLASSES, Formatter.HEADING_ONE);
        layoutedText += this.formatter.tableHeader(HEADING_CLASS, HEADING_DESCRIPTION);
        
        for ( ClassDoc classDoc : classes ) {
            String classLink = this.createLinkIfAnchorExists(classDoc.name(), classDoc.qualifiedName());
            layoutedText += this.formatter.tableRow(classLink, this.createTagDescription(classDoc.inlineTags()));
        }
        
        this.print(layoutedText);
    }
    
    public void printClassDescription(ClassDoc classDoc) {
        String layoutedText = this.formatter.horizontalRule();
        String classTitle = HEADING_CLASS + WS + classDoc.name();
        
        String anchor = this.createAnchor(classTitle, classDoc.qualifiedName());
        layoutedText += this.formatter.heading(anchor, Formatter.HEADING_ONE);
        layoutedText += createClassSignature(classDoc);
        layoutedText += createClassHierarchy(classDoc);
        
        String classDescription = this.formatter.bold(HEADING_DESCRIPTION + ":") + this.formatter.lineFeed();
        classDescription += this.createTagDescription(classDoc.inlineTags());
        layoutedText += this.formatter.paragraph(classDescription);
        
        layoutedText += createClassTags(classDoc);
        
        this.print(layoutedText);
    }
    
    public void printClassIncludes(ClassDoc classDoc) {
        String includeFile = "";
        
        for ( Tag tag : classDoc.tags() ) {
            if ( tag.name().equals(INCLUDE_TAG) ) {
                includeFile = tag.text();
                break;
            }
        }
        
        this.print(this.includeFile(includeFile));
    }
    
    private String createClassTags(ClassDoc classDoc) {
        String layoutedText = "";
        
        for ( Tag tag : classDoc.tags() ) {
            String tagName = this.tagToName.get(tag.name());
            
            if ( tagName == null || tagName.isEmpty() ) {
                tagName = tag.name();
            }
            
            if ( tagName.equals(INCLUDE_TAG) ) {
                continue;
            }
            
            String tagDescription = this.formatter.bold(tagName) + this.formatter.lineFeed();
            tagDescription += tag.text();
            layoutedText += this.formatter.paragraph(tagDescription);
        }
        
        return layoutedText;
    }
    
    private String createClassSignature(ClassDoc classDoc) {
        boolean first = true;
        String signature = classDoc.modifiers() + WS;
        
        if ( classDoc.isEnum() ) {
            signature += ENUM;
        } else if ( classDoc.isInterface() ) {
            signature += INTERFACE;
        } else {
            signature += CLASS;
        }
        
        signature += WS + classDoc.name();
        
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
        
        return this.formatter.codeBlock(signature);
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
        
        String layoutedText = this.formatter.bold(HEADING_HIERARCHY) + this.formatter.lineFeed();
        layoutedText += this.formatter.codeBlock(hierarchy);
        return layoutedText;
    }
    
    public void printMethodList(List<MethodDoc> methods, List<String> forbiddenAnnotations) {
        String layoutedText = this.formatter.heading(HEADING_METHOD_SUMMARY, Formatter.HEADING_TWO);
        
        int documentedMethodCount = Layouter.countDocumentedMethods(methods);
        
        if ( documentedMethodCount > 0 ) {
            layoutedText += this.formatter.tableHeader(HEADING_METHOD, HEADING_DESCRIPTION);
        }
        
        for ( MethodDoc methodDoc : methods ) {
            if ( !Layouter.isDocumented(methodDoc) ) {
                continue;
            }
            
            String rawLink = Layouter.generateSnakeCaseFullMethodSignature(methodDoc);
            String fullMethodSignature = this.generateFullMethodSignatureWithParameterNames(methodDoc, forbiddenAnnotations, false);
            String formattedMethodLink = this.createLinkIfAnchorExists(fullMethodSignature, rawLink);
            layoutedText += this.formatter.tableRow(formattedMethodLink, this.createTagDescription(methodDoc.inlineTags()));
        }
        
        layoutedText += this.formatter.heading(HEADING_METHOD_DETAIL, Formatter.HEADING_TWO);
        
        this.print(layoutedText);
    }
    
    public void printMethodInfo(MethodDoc methodDoc, List<String> forbiddenAnnotations) {
        if ( !Layouter.isDocumented(methodDoc) ) {
            return;
        }
        
        String layoutedText = this.createMethodDescription(methodDoc, forbiddenAnnotations);
        layoutedText += this.createParameterList(methodDoc, forbiddenAnnotations);
        layoutedText += this.createReturnInfo(methodDoc);
        layoutedText += this.createMethodIncludes(methodDoc);
        this.print(layoutedText);
    }
    
    private String createMethodDescription(MethodDoc methodDoc, List<String> forbiddenAnnotations) {
        String snakeCaseFullMethodSignature = Layouter.generateSnakeCaseFullMethodSignature(methodDoc);
        String fullMethodSignature = this.generateFullMethodSignatureWithParameterNames(methodDoc, forbiddenAnnotations, true);
        String formattedMethodAnchor = this.createAnchor(methodDoc.name(), snakeCaseFullMethodSignature);
        String layoutedText = this.formatter.heading(formattedMethodAnchor, Formatter.HEADING_TWO);
        layoutedText += this.formatter.codeBlock(fullMethodSignature);
        layoutedText += this.formatter.paragraph(this.createTagDescription(methodDoc.inlineTags()));
        return layoutedText;
    }
    
    private String createParameterList(MethodDoc methodDoc, List<String> forbiddenAnnotations) {
        String layoutedText = "";
        
        if ( methodDoc.parameters().length > 0 && methodDoc.paramTags().length > 0 ) {
            layoutedText += this.formatter.heading(HEADING_PARAMETERS, Formatter.HEADING_THREE);
            layoutedText += this.formatter.tableHeader(HEADING_PARAMETER, HEADING_DESCRIPTION);
        }
        
        for ( int i = 0; i < methodDoc.parameters().length && i < methodDoc.paramTags().length; i++ ) {
            Parameter parameter = methodDoc.parameters()[i];
            ParamTag paramTag = methodDoc.paramTags()[i];
            String name = this.generateParameterName(parameter, forbiddenAnnotations);
            String description = this.createTagDescription(paramTag.inlineTags());
            description.replace(LF, WS);
            layoutedText += this.formatter.tableRow(name, description);
        }
        
        return layoutedText;
    }
    
    private String createTagDescription(Tag[] inlineTags) {
        String description = "";
        
        for ( Tag inlineTag : inlineTags ) {
            if ( inlineTag.name().equals(LINK_TAG) ) {
                SeeTag linkTag = (SeeTag) inlineTag;
                ClassDoc classDoc = linkTag.referencedClass();
                MemberDoc memberDoc = linkTag.referencedMember();
                
                if ( classDoc != null ) {
                    String anchor = classDoc.qualifiedName();
                    description += this.createLinkIfAnchorExists(linkTag.label(), anchor);
                } else if ( memberDoc != null && memberDoc.isMethod() ) {
                    String anchor = Layouter.generateSnakeCaseFullMethodSignature((MethodDoc) memberDoc);
                    description += this.createLinkIfAnchorExists(linkTag.label(), anchor);
                } else {
                    description += linkTag.label();
                }
            } else {
                description += inlineTag.text();
            }
        }
        
        return description;
    }
    
    private String createReturnInfo(MethodDoc methodDoc) {
        String layoutedText = this.formatter.heading(HEADING_RETURNS, Formatter.HEADING_THREE);
        layoutedText += this.formatter.tableHeader(HEADING_RETURN_TYPE, HEADING_DESCRIPTION);
        String returnDescription = "";
        
        for ( Tag tag : methodDoc.tags() ) {
            if ( tag.name().equals(RETURN_TAG) ) {
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
            if ( tag.name().equals(INCLUDE_TAG) ) {
                includeFile = tag.text();
                break;
            }
        }
        
        return this.includeFile(includeFile);
    }
    
    private String generateFullMethodSignatureWithParameterNames(MethodDoc methodDoc, List<String> forbiddenAnnotations, boolean indent) {
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
            
            result += this.generateParameterName(parameter, forbiddenAnnotations);
        }
        
        result += BRCLOSE;
        
        return result;
    }
    
    private String generateParameterName(Parameter parameter, List<String> forbiddenAnnotations) {
        String result = "";
        
        for ( AnnotationDesc annotation : parameter.annotations() ) {
            boolean forbidden = false;
            
            for ( String forbiddenAnnotation : forbiddenAnnotations ) {
                if ( annotation.annotationType().name().equals(forbiddenAnnotation) ) {
                    forbidden = true;
                    break;
                }
            }
            
            if ( !forbidden ) {
                result += AT + annotation.annotationType().name() + WS;
            }
        }
        
        result += this.createLinkIfAnchorExists(parameter.type().typeName(), parameter.type().qualifiedTypeName());
        result += WS + parameter.name();
        return result;
    }
    
    private static String generateSnakeCaseFullMethodSignature(MethodDoc methodDoc) {
        String fullMethodSignature = methodDoc.returnType() + WS + methodDoc.name() + methodDoc.flatSignature();
        fullMethodSignature = fullMethodSignature.replace(WS, SNAKE);
        fullMethodSignature = fullMethodSignature.replace(".", SNAKE);
        fullMethodSignature = fullMethodSignature.replace(" ", SNAKE);
        fullMethodSignature = fullMethodSignature.replace("(", SNAKE);
        fullMethodSignature = fullMethodSignature.replace(")", "");
        fullMethodSignature = fullMethodSignature.replace(",", "");
        
        return fullMethodSignature;
    }
}
