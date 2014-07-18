package org.froeschl.mddoclet;

import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.froeschl.mddoclet.formatter.Formatter;
import org.froeschl.mddoclet.printer.Printer;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

public class DocletParser {
    final private Formatter formatter;
    final private Printer printer;
    
    public DocletParser(Formatter formatter, Printer printer) {
        if ( formatter == null || printer == null ) {
            throw new InvalidParameterException();
        }
        
        this.formatter = formatter;
        this.printer = printer;
    }
    
    public void parse(RootDoc root) {
        ClassDoc[] classes = root.classes();
        this.printClassOverview(classes);
    }
    
    private void printClassOverview(ClassDoc[] classes) {
        Map<String, String> classNamesAndDescriptions = new LinkedHashMap<String, String>();
        
        for (int i = 0; i < classes.length; ++i) {
            if (    !classes[i].isPublic() || 
                    !classes[i].isOrdinaryClass() ||
                    classes[i].containingClass() != null
                    ) {
                continue;
            }
            
            classNamesAndDescriptions.put(classes[i].name(), classes[i].commentText());
        }
        
        String formattedText = this.formatter.formatClassList(classNamesAndDescriptions);
        this.printer.print(formattedText);
    }
}
