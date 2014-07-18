package org.froeschl.mddoclet;

import java.security.InvalidParameterException;
import java.util.List;

import org.froeschl.mddoclet.formatter.Formatter;
import org.froeschl.mddoclet.printer.Printer;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;

public class Layouter {
    final private Formatter formatter;
    final private Printer printer;
    
    private static final String SNAKE = "_";
    private static final String WS = " ";
    private static final String AT = "@";
    private static final String LF = "\n";
    private static final String COMMA = ",";
    private static final String BROPEN = "(";
    private static final String BRCLOSE = ")";
    
    private static final String HEADING_CLASSES = "Classes";
    private static final String HEADING_CLASS = "Class";
    private static final String HEADING_METHODS = "Methods";
    private static final String HEADING_METHOD = "Method";
    private static final String HEADING_PARAMETERS = "Parameters";
    private static final String HEADING_PARAMETER = "Parameter";
    private static final String HEADING_DESCRIPTION = "Description";
    // private static final String RETURN = "return";
    
    public Layouter(Formatter formatter, Printer printer) {
        if ( formatter == null || printer == null ) {
            throw new InvalidParameterException();
        }
        
        this.formatter = formatter;
        this.printer = printer;
    }
    
    public void createClassList(List<ClassDoc> classes) {
        String layoutedText = this.formatter.createHeading(HEADING_CLASSES, Formatter.HEADING_ONE);
        layoutedText += this.formatter.createTableHeader(HEADING_CLASS, HEADING_DESCRIPTION);
        
        for ( ClassDoc classDoc : classes ) {
            String classLink = this.formatter.createLink(classDoc.name(), classDoc.qualifiedName());
            layoutedText += this.formatter.createTableRow(classLink, classDoc.commentText());
        }
        
        this.printer.print(layoutedText);
    }
    
    public void createClassDescription(ClassDoc classDoc) {
        String layoutedText = this.formatter.createHorizontalRule();
        String anchor = this.formatter.createAnchor(classDoc.name(), classDoc.qualifiedName());
        layoutedText += this.formatter.createHeading(anchor, Formatter.HEADING_ONE);
        layoutedText += this.formatter.createParagraph(classDoc.commentText());
        this.printer.print(layoutedText);
    }
    
    public void createMethodList(List<MethodDoc> methods, List<String> forbiddenAnnotations) {
        String layoutedText = this.formatter.createHeading(HEADING_METHODS, Formatter.HEADING_TWO);
        layoutedText += this.formatter.createTableHeader(HEADING_METHOD, HEADING_DESCRIPTION);
        
        for ( MethodDoc methodDoc : methods ) {
            String rawLink = Layouter.generateSnakeCaseFullMethodSignature(methodDoc);
            String fullMethodSignature = Layouter.generateFullMethodSignatureWithParameterNames(methodDoc, forbiddenAnnotations);
            String formattedMethodLink = this.formatter.createLink(fullMethodSignature, rawLink);
            layoutedText += this.formatter.createTableRow(formattedMethodLink, methodDoc.commentText());
        }
        
        this.printer.print(layoutedText);
    }
    
    public void createMethodDescription(MethodDoc methodDoc, List<String> forbiddenAnnotations) {
        String snakeCaseFullMethodSignature = Layouter.generateSnakeCaseFullMethodSignature(methodDoc);
        String fullMethodSignature = Layouter.generateFullMethodSignatureWithParameterNames(methodDoc, forbiddenAnnotations);
        String formattedMethodAnchor = this.formatter.createAnchor(fullMethodSignature, snakeCaseFullMethodSignature);
        String layoutedText = this.formatter.createHeading(formattedMethodAnchor, Formatter.HEADING_TWO);
        layoutedText += this.formatter.createParagraph(methodDoc.commentText());
        this.printer.print(layoutedText);
    }
    
    public void createParameterList(MethodDoc methodDoc, List<String> forbiddenAnnotations) {
        String layoutedText = this.formatter.createHeading(HEADING_PARAMETERS, Formatter.HEADING_THREE);
        layoutedText += this.formatter.createTableHeader(HEADING_PARAMETER, HEADING_DESCRIPTION);
        
        for ( int i = 0; i < methodDoc.parameters().length && i < methodDoc.paramTags().length; i++ ) {
            Parameter parameter = methodDoc.parameters()[i];
            ParamTag tag = methodDoc.paramTags()[i];
            
            String name = Layouter.generateParameterName(parameter, forbiddenAnnotations);
            String description = (tag == null) ? "" : tag.parameterComment();
            description.replace(LF, WS);
            
            layoutedText += this.formatter.createTableRow(name, description);
        }
        
        /*
        layoutedText = this.formatter.createHeading(HEADING_RETURN_VALUE, Formatter.HEADING_THREE);
        layoutedText += this.formatter.createTableHeader(HEADING_TYPE, HEADING_DESCRIPTION);
        layoutedText += this.formatter.createTableRow(methodDoc.returnType().typeName(), methodDoc.returnType().);
        */
        
        this.printer.print(layoutedText);
    }
    
    private static String generateFullMethodSignatureWithParameterNames(MethodDoc methodDoc, List<String> forbiddenAnnotations) {
        boolean first = true;
        String result = methodDoc.returnType() + WS + methodDoc.name() + BROPEN;
        
        for ( Parameter parameter : methodDoc.parameters() ) {
            if ( first ) {
                first = false;
            } else {
                result += COMMA + WS;
            }
            
            result += Layouter.generateParameterName(parameter, forbiddenAnnotations);
        }
        
        result += BRCLOSE;
        
        return result;
    }
    
    private static String generateParameterName(Parameter parameter, List<String> forbiddenAnnotations) {
        String result = "";
        
        for ( AnnotationDesc annotation : parameter.annotations() ) {
            boolean forbidden = false;
            
            for ( String forbiddenAnnotation : forbiddenAnnotations ) {
                if ( annotation.annotationType().name().compareTo(forbiddenAnnotation) == 0 ) {
                    forbidden = true;
                    break;
                }
            }
            
            if ( !forbidden ) {
                result += AT + annotation.annotationType().name() + WS;
            }
        }
        
        result += parameter.typeName() + WS + parameter.name();
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
