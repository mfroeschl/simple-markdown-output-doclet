package org.froeschl.mddoclet;

import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;

public class DocHelper {
    private static final String HIDDEN_TAG = "@hidden";
    private static final String ENUM_CLASS_NAME = "Enum";
    
    public static boolean isHidden(ProgramElementDoc element) {
        for ( Tag tag : element.tags() ) {
            if ( tag.name().equals(HIDDEN_TAG) ) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean isEnum(ClassDoc classDoc) {
        if ( classDoc.isEnum() || classDoc.name().equals(ENUM_CLASS_NAME) ) {
            return true;
        }
        
        ClassDoc superclass = classDoc.superclass();
        
        while ( superclass != null ) {
            if ( superclass.isEnum() || superclass.name().equals(ENUM_CLASS_NAME) ) {
                return true;
            }
            
            superclass = superclass.superclass();
        }
        
        return false;
    }
    
    public static boolean isDocumented(MethodDoc methodDoc) {
        return (methodDoc.paramTags().length > 0 || methodDoc.tags().length > 0 || methodDoc.inlineTags().length > 0);
    }
    
    public static boolean isDocumented(FieldDoc fieldDoc) {
        return ( fieldDoc.tags().length > 0 || fieldDoc.inlineTags().length > 0);
    }
    
    public static boolean hasParameters(MethodDoc methodDoc) {
        return (methodDoc.parameters().length > 0 && methodDoc.paramTags().length > 0);
    }
    
    public static int countDocumentedMethods(List<MethodDoc> methods) {
        int count = 0;
        
        for ( MethodDoc methodDoc : methods ) {
            if ( DocHelper.isDocumented(methodDoc)) {
                count++;
            }
        }
        
        return count;
    }
    
    public static int countDocumentedFields(List<FieldDoc> fields) {
        int count = 0;
        
        for ( FieldDoc fieldDoc : fields ) {
            if ( DocHelper.isDocumented(fieldDoc)) {
                count++;
            }
        }
        
        return count;
    }
}
