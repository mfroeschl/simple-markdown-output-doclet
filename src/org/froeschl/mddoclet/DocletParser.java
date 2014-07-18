package org.froeschl.mddoclet;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.froeschl.mddoclet.formatter.Formatter;
import org.froeschl.mddoclet.printer.Printer;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

/** 
 * Parses all classes, methods etc. and decides which elements to print to the document.
 * 
 * @author Marcus Froeschl
 */
public class DocletParser {
    final private Layouter layouter;
    
    public DocletParser(Formatter formatter, Printer printer) {
        if ( formatter == null || printer == null ) {
            throw new InvalidParameterException();
        }
        
        this.layouter = new Layouter(formatter, printer);
    }
    
    public void parse(RootDoc root) {
        ClassDoc[] classes = root.classes();
        this.createClassList(classes);
        this.createAllClasses(classes);
    }
    
    private void createClassList(ClassDoc[] classes) {
        List<ClassDoc> filteredClasses = new ArrayList<ClassDoc>();
        
        // TODO: Create a setting
        for (int i = 0; i < classes.length; ++i) {
            if (    !classes[i].isPublic() || 
                    !classes[i].isOrdinaryClass() ||
                    classes[i].containingClass() != null
                    ) {
                continue;
            }
            
            filteredClasses.add(classes[i]);
        }
        
        this.layouter.createClassList(filteredClasses);
    }
    
    private void createAllClasses(ClassDoc[] classes) {
        List<ClassDoc> filteredClasses = new ArrayList<ClassDoc>();
        
        // TODO: Create a setting
        for (int i = 0; i < classes.length; ++i) {
            if (    !classes[i].isPublic() || 
                    !classes[i].isOrdinaryClass() ||
                    classes[i].containingClass() != null
                    ) {
                continue;
            }
            
            filteredClasses.add(classes[i]);
        }
        
        for ( ClassDoc classDoc : filteredClasses ) {
            this.createClassDescription(classDoc);
            this.createMethodList(classDoc.methods());
            this.createAllMethods(classDoc.methods());
        }
    }
    
    private void createClassDescription(ClassDoc classDoc) {
        this.layouter.createClassDescription(classDoc);
    }
    
    private void createMethodList(MethodDoc[] methods) {
        List<MethodDoc> filteredMethods = new ArrayList<MethodDoc>();
        List<String> forbiddenAnnotations = new ArrayList<String>();
        
        // TODO: Create a setting
        for (int i = 0; i < methods.length; ++i) {
            if ( !methods[i].isPublic() ) {
                continue;
            }
            
            filteredMethods.add(methods[i]);
        }
        
        // TODO: Create a setting
        forbiddenAnnotations.add("Nullable");
        
        this.layouter.createMethodList(filteredMethods, forbiddenAnnotations);
    }
    
    private void createAllMethods(MethodDoc[] methods) {
        List<MethodDoc> filteredMethods = new ArrayList<MethodDoc>();
        List<String> forbiddenAnnotations = new ArrayList<String>();
        
        // TODO: Create a setting
        for (int i = 0; i < methods.length; ++i) {
            if ( !methods[i].isPublic() ) {
                continue;
            }
            
            filteredMethods.add(methods[i]);
        }
        
        // TODO: Create a setting
        forbiddenAnnotations.add("Nullable");
        
        for ( MethodDoc methodDoc : filteredMethods ) {
            this.createMethodDescription(methodDoc, forbiddenAnnotations);
            this.createParameterList(methodDoc, forbiddenAnnotations);
        }
    }
    
    private void createMethodDescription(MethodDoc methodDoc, List<String> filteredAnnotations) {
        this.layouter.createMethodDescription(methodDoc, filteredAnnotations);
    }
    
    private void createParameterList(MethodDoc methodDoc, List<String> filteredAnnotations) {
        this.layouter.createParameterList(methodDoc, filteredAnnotations);
    }
}
