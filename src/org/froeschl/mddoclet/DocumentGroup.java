package org.froeschl.mddoclet;

import java.util.HashMap;

public class DocumentGroup {
    private static final String WS = " ";
    private static final String BROPEN = "(";
    private static final String BRCLOSE = ")";
    private static final String COMMA = ",";
    
    final String title;
    final String file;
    final String fullFilePath;
    final HashMap<String, String> classes = new HashMap<String, String>();
    final boolean isDefault;
    
    public DocumentGroup(String title, String file, String fullFilePath, boolean isDefault) {
        this.title = title;
        this.file = file;
        this.fullFilePath = fullFilePath;
        this.isDefault = isDefault;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getFile() {
        return this.file;
    }
    
    public String getFullFilePath() {
        return this.fullFilePath;
    }
    
    public HashMap<String, String> getClasses() {
        return this.classes;
    }
    
    public void addClass(String className) {
        this.classes.put(className, className);
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public boolean contains(String className) {
        return this.classes.containsKey(className);
    }
    
    @Override
    public String toString() {
        boolean first = true;
        String result = this.title;
        // result += " [" + this.fullFilePath + "] ";
        
        for ( String entry : classes.values() ) {
            if ( first ) {
                first = false;
                result += BROPEN;
            } else {
                result += COMMA + WS;
            }
            
            result += entry;
        }
        
        if ( this.classes.size() > 0 ) {
            result += BRCLOSE;
        }
        
        return result;
    }
}
