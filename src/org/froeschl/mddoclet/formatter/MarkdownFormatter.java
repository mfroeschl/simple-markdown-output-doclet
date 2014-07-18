package org.froeschl.mddoclet.formatter;

import java.util.Map;
import java.util.Map.Entry;

public class MarkdownFormatter implements Formatter {
    private static final String LF = "\n";
    private static final String SHARP = "#";
    private static final String OPENING_BRACKETS = "(";
    private static final String CLOSING_BRACKETS = ")";
    private static final String OPENING_SQUARE_BRACKETS = "[";
    private static final String CLOSING_SQUARE_BRACKETS = "]";
    private static final String FORMATTER_HEADING = "===";
    private static final String FORMATTER_SUB_HEADING = "---";
    private static final String FORMATTER_TABLE_SEPARATOR = "|";
    private static final String FORMATTER_BOLD = "**";
    private static final String SECTION_SEPARATOR = LF + LF + FORMATTER_SUB_HEADING + LF + LF;
    
    private static final String HEADING_CLASSES = "Classes";
    private static final String HEADING_CLASS = "Class";
    private static final String HEADING_DESCRIPTION = "Description";
    private static final String EMPTY_CELL = "-";
    
    @Override
    public String formatClassList(Map<String, String> classNamesAndDescriptions) {
        String result = MarkdownFormatter.createHeading(HEADING_CLASSES);
        result += MarkdownFormatter.createTableHeader(HEADING_CLASS, HEADING_DESCRIPTION);
        
        for ( Entry<String, String> entry : classNamesAndDescriptions.entrySet() ) {
            String classLink = MarkdownFormatter.createLink(entry.getKey(), entry.getKey());
            result += MarkdownFormatter.createTableRow(classLink, entry.getValue());
        }
        
        result += SECTION_SEPARATOR;
        return result;
    }
    
    private static String createHeading(String text) {
        return text + LF + FORMATTER_HEADING + LF + LF;
    }
    
    private static String createTableHeader(String ... elements) {
        String result = FORMATTER_TABLE_SEPARATOR;
        
        for( String element : elements ) {
            result += FORMATTER_BOLD + element + FORMATTER_BOLD + FORMATTER_TABLE_SEPARATOR;
        }
        
        result += LF + FORMATTER_TABLE_SEPARATOR;
        
        for( int i = 0; i < elements.length; ++i ) {
            result += FORMATTER_SUB_HEADING + FORMATTER_TABLE_SEPARATOR;
        }
        
        result += LF;
        return result;
    }
    
    private static String createTableRow(String ... elements) {
        String result = FORMATTER_TABLE_SEPARATOR;
        
        for( String element : elements ) {
            if ( element == null || element.isEmpty() ) {
                element = EMPTY_CELL;
            }
            
            // LF will destroy a markdown table. As a convention, we will cut off all text after LF.
            int position = element.indexOf(LF);
            if ( position >= 0 ) {
                element = element.substring(0, position);
            }
            
            result += element + FORMATTER_TABLE_SEPARATOR;
        }
        
        result += LF;
        return result;
    }
    
    private static String createLink(String title, String link) {
        // [AccountAccess](#AccountAccess)
        return OPENING_SQUARE_BRACKETS + title + CLOSING_SQUARE_BRACKETS + OPENING_BRACKETS + SHARP + link + CLOSING_BRACKETS;
    }
}
