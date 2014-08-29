package org.froeschl.mddoclet;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.froeschl.mddoclet.Layouter.Mode;
import org.froeschl.mddoclet.Options.DocumentGroup;
import org.froeschl.mddoclet.formatter.Formatter;
import org.froeschl.mddoclet.printer.Printer;
import org.froeschl.mddoclet.utils.FileUtils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;

/** 
 * Parses all classes, methods etc. and decides which elements to print to the document.
 * 
 * @author Marcus Froeschl
 */
public class Editor {
    final private Layouter layouter;
    final private Options options;
    
    public Editor(Options options, Formatter formatter, Printer printer) {
        if ( options == null || formatter == null || printer == null ) {
            throw new InvalidParameterException();
        }
        
        this.layouter = new Layouter(options, formatter, printer);
        this.options = options;
    }
    
    public void authorDocument(RootDoc root) {
        ClassDoc[] classes = root.classes();
        System.out.println("\n\nSimple Markdown Output Doclet");
        System.out.println("=============================");
        System.out.println("");
        System.out.println(this.options.toString());
        System.out.println("");
        
        this.deleteFiles();
        
        this.layouter.setMode(Mode.PREPARE);
        this.layouter.printDocumentTitleAndHeader();
        this.createGroupList();
        this.createGroupHeaders();
        this.createClassList(classes);
        this.createAllClasses(classes);
        
        this.layouter.setMode(Mode.PRINT);
        this.layouter.printDocumentTitleAndHeader();
        this.createGroupList();
        this.createGroupHeaders();
        this.createClassList(classes);
        this.createAllClasses(classes);
    }
    
    private boolean meetsMinimumVisbility(ProgramElementDoc element) {
        boolean meetsCondition = true;
        
        switch ( options.getMinimumVisibility() ) {
            case PRIVATE: 
                meetsCondition = true;
                break;
            case PROTECTED:
                meetsCondition = !element.isPrivate();
                break;
            case PACKAGE:
                meetsCondition = (!element.isProtected() && !element.isPrivate());
                break;
            case PUBLIC:
                meetsCondition = (!element.isProtected() && !element.isPrivate() && !element.isPackagePrivate());
                break;
            default:
                meetsCondition = false;
                break;
        }
        
        if ( !meetsCondition ) {
            System.out.println("Element " + element.name() + " is filrered because it does not meet minimum visibility requirement.");
        }
        
        return meetsCondition;
    }
    
    private boolean isPermittedClassType(ClassDoc classDoc) {
        if ( options.getNoEnums() ) {
            if ( DocHelper.isEnum(classDoc) ) {
                System.out.println("Class " + classDoc.name() + " was filtered because it is an enum.");
                return false;
            }
        }
        
        if ( options.getNoInterfaces() ) {
            if ( classDoc.isInterface() ) {
                System.out.println("Class " + classDoc.name() + " was filtered because it is an interface.");
                return false;
            }
        }
        
        if ( options.getNoNestedClasses() ) {
            if ( classDoc.containingClass() != null ) {
                System.out.println("Class " + classDoc.name() + " was filtered because it is a nested class.");
                return false;
            }
        }
        
        return true;
    }
    
    private boolean shouldBeHidden(ProgramElementDoc element) {
        if ( this.options.getIncludeHidden() ) {
            return false;
        }
        
        boolean isHidden = DocHelper.isHidden(element);
        if ( isHidden ) {
            System.out.println("Element " + element.name() + " was filtered because it contains the @hidden tag.");
            return true;
        }
        
        return false;
    }
    
    private List<ClassDoc> filterClasses(ClassDoc[] classes) {
        List<ClassDoc> filteredClasses = new ArrayList<ClassDoc>();
        
        for ( int i = 0; i < classes.length; ++i ) {
            if ( ! this.meetsMinimumVisbility(classes[i]) ) {
                continue;
            }
            
            if ( !this.isPermittedClassType(classes[i]) ) {
                continue;
            }
            
            if ( this.shouldBeHidden(classes[i]) ) {
                continue;
            }
            
            filteredClasses.add(classes[i]);
        }
        
        return filteredClasses;
    }
    
    private List<MethodDoc> filterMethods(MethodDoc[] methods) {
        List<MethodDoc> filteredMethods = new ArrayList<MethodDoc>();
        
        for ( int i = 0; i < methods.length; ++i ) {
            if ( !this.meetsMinimumVisbility(methods[i]) ) {
                continue;
            }
            
            if ( this.shouldBeHidden(methods[i]) ) {
                continue;
            }
            
            filteredMethods.add(methods[i]);
        }
        
        return filteredMethods;
    }
    
    private List<FieldDoc> filterFields(FieldDoc[] fields) {
        List<FieldDoc> filteredFields = new ArrayList<FieldDoc>();
        
        for ( int i = 0; i < fields.length; ++i ) {
            if ( !this.meetsMinimumVisbility(fields[i]) ) {
                continue;
            }
            
            if ( this.shouldBeHidden(fields[i]) ) {
                continue;
            }
            
            filteredFields.add(fields[i]);
        }
        
        return filteredFields;
    }
    
    private void deleteFiles() {
        for ( DocumentGroup group : this.options.getDocumentGroups().values() ) {
            FileUtils.deleteFileOrFolder(group.fullFilePath);
        }
        
        FileUtils.deleteFileOrFolder(this.options.getFullMainFilePath());
    }
    
    private void createGroupList() {
        this.layouter.printGroupList();
    }
    
    private void createGroupHeaders() {
        this.layouter.printGroupHeaders();
    }
    
    private void createClassList(ClassDoc[] classes) {
        this.layouter.printClassList(this.filterClasses(classes));
    }
    
    private void createAllClasses(ClassDoc[] classes) {
        List<ClassDoc> filteredClasses = this.filterClasses(classes);
        
        for ( ClassDoc classDoc : filteredClasses ) {
            this.createClassDescription(classDoc);
            this.createFieldList(classDoc.fields());
            this.createMethodList(classDoc.methods());
            this.createAllMethods(classDoc.methods());
            this.layouter.printClassIncludes(classDoc);
        }
    }
    
    private void createClassDescription(ClassDoc classDoc) {
        this.layouter.printClassDescription(classDoc);
    }
    
    private void createFieldList(FieldDoc[] fields) {
        this.layouter.printFieldList(this.filterFields(fields), this.options.getAnnotationsToBeRemoved());
    }
    
    private void createMethodList(MethodDoc[] methods) {
        this.layouter.printMethodList(this.filterMethods(methods), this.options.getAnnotationsToBeRemoved());
    }
    
    private void createAllMethods(MethodDoc[] methods) {
        this.layouter.printAllMethods(this.filterMethods(methods), this.options.getAnnotationsToBeRemoved());
    }
}
