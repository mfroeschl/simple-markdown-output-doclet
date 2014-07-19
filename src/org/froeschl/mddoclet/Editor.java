package org.froeschl.mddoclet;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.froeschl.mddoclet.Layouter.Mode;
import org.froeschl.mddoclet.formatter.Formatter;
import org.froeschl.mddoclet.printer.Printer;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

/** 
 * Parses all classes, methods etc. and decides which elements to print to the document.
 * 
 * @author Marcus Froeschl
 */
public class Editor {
    private static final String HIDDEN_TAG = "@hidden";
    
    final private Layouter layouter;
    List<String> forbiddenAnnotations = new ArrayList<String>();
    
    public Editor(Options options, Formatter formatter, Printer printer) {
        if ( options == null || formatter == null || printer == null ) {
            throw new InvalidParameterException();
        }
        
        this.layouter = new Layouter(options, formatter, printer);
        // TODO: Create a setting
        this.forbiddenAnnotations.add("Nullable");
    }
    
    public void authorDocument(RootDoc root) {
        ClassDoc[] classes = root.classes();
        
        this.layouter.setMode(Mode.PREPARE);
        this.layouter.printDocumentTitleAndHeader();
        this.createClassList(classes);
        this.createAllClasses(classes);
        
        this.layouter.setMode(Mode.PRINT);
        this.layouter.printDocumentTitleAndHeader();
        this.createClassList(classes);
        this.createAllClasses(classes);
    }
    
    private List<ClassDoc> filterClasses(ClassDoc[] classes) {
        List<ClassDoc> filteredClasses = new ArrayList<ClassDoc>();
        
        // TODO: Create a setting
        for (int i = 0; i < classes.length; ++i) {
            if (    !classes[i].isPublic() || 
                    !classes[i].isOrdinaryClass() ||
                    classes[i].containingClass() != null
                    ) {
                continue;
            }
            
            boolean hiddenTag = false;
            
            for ( Tag tag : classes[i].tags() ) {
                if ( tag.name().equals(HIDDEN_TAG) ) {
                    hiddenTag = true;
                    break;
                }
            }
            
            if ( hiddenTag ) {
                continue;
            }
            
            filteredClasses.add(classes[i]);
        }
        
        return filteredClasses;
    }
    
    private List<MethodDoc> filterMethods(MethodDoc[] methods) {
        List<MethodDoc> filteredMethods = new ArrayList<MethodDoc>();
        
        // TODO: Create a setting
        for (int i = 0; i < methods.length; ++i) {
            if ( !methods[i].isPublic() ) {
                continue;
            }
            
            boolean hiddenTag = false;
            
            for ( Tag tag : methods[i].tags() ) {
                if ( tag.name().equals(HIDDEN_TAG) ) {
                    hiddenTag = true;
                    break;
                }
            }
            
            if ( hiddenTag ) {
                continue;
            }
            
            filteredMethods.add(methods[i]);
        }
        
        return filteredMethods;
    }
    
    private void createClassList(ClassDoc[] classes) {
        this.layouter.printClassList(this.filterClasses(classes));
    }
    
    private void createAllClasses(ClassDoc[] classes) {
        List<ClassDoc> filteredClasses = this.filterClasses(classes);
        
        for ( ClassDoc classDoc : filteredClasses ) {
            this.createClassDescription(classDoc);
            this.createMethodList(classDoc.methods());
            this.createAllMethods(classDoc.methods());
            this.layouter.printClassIncludes(classDoc);
        }
    }
    
    private void createClassDescription(ClassDoc classDoc) {
        this.layouter.printClassDescription(classDoc);
    }
    
    private void createMethodList(MethodDoc[] methods) {
        this.layouter.printMethodList(this.filterMethods(methods), this.forbiddenAnnotations);
    }
    
    private void createAllMethods(MethodDoc[] methods) {
        List<MethodDoc> filteredMethods = this.filterMethods(methods);
        
        for ( MethodDoc methodDoc : filteredMethods ) {
            this.layouter.printMethodInfo(methodDoc, this.forbiddenAnnotations);
        }
    }
}
