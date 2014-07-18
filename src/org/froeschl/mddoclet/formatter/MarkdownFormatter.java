package org.froeschl.mddoclet.formatter;


public class MarkdownFormatter implements Formatter {
    private static final String LF = "\n";
    private static final String DLF = "\n\n";
    private static final String WS = " ";
    private static final String SHARP = "#";
    private static final String LINK_FORMAT = "[%s](#%s)";
    private static final String ANCHOR_FORMAT = "<a name=\"%s\"> %s </a>";
    private static final String TABLE_HEADER = "---";
    private static final String TABLE_SEPARATOR = "|";
    private static final String FORMATTER_BOLD = "**";
    private static final String HORIZONTAL_RULE = LF + LF + "-------------------------" + LF + LF;
    
    private static final String EMPTY_CELL = "-";
    
    @Override
    public String createHeading(String text, int headingLevel) {
        String formatterHeading = "";
        
        if ( headingLevel < 1 ) {
            headingLevel = 1;
        }
        
        for ( int i = 0; i < headingLevel; ++i ) {
            formatterHeading += SHARP;
        }
        
        return DLF + formatterHeading + WS + text + WS + formatterHeading + DLF;
    }
    
    @Override
    public String createParagraph(String text) {
        return DLF + text + DLF; 
    }
    
    @Override
    public String createTableHeader(String ... elements) {
        String result = TABLE_SEPARATOR;
        
        for( String element : elements ) {
            result += FORMATTER_BOLD + element + FORMATTER_BOLD + TABLE_SEPARATOR;
        }
        
        result += LF + TABLE_SEPARATOR;
        
        for( int i = 0; i < elements.length; ++i ) {
            result += TABLE_HEADER + TABLE_SEPARATOR;
        }
        
        result += LF;
        return result;
    }
    
    @Override
    public String createTableRow(String ... elements) {
        String result = TABLE_SEPARATOR;
        
        for( String element : elements ) {
            if ( element == null || element.isEmpty() ) {
                element = EMPTY_CELL;
            }
            
            // LF will destroy a markdown table. As a convention, we will cut off all text after LF.
            int position = element.indexOf(LF);
            if ( position >= 0 ) {
                element = element.substring(0, position);
            }
            
            result += element + TABLE_SEPARATOR;
        }
        
        result += LF;
        return result;
    }
    
    @Override
    public String createLink(String text, String link) {
        // [AccountAccess](#AccountAccess)
        return String.format(LINK_FORMAT, text, link);
    }
    
    @Override
    public String createAnchor(String text, String anchor) {
        // <a name="Fresvii.startWithAppIdentifier"> startWithAppIdentifier: </a>
        return String.format(ANCHOR_FORMAT, anchor, text);
    }
    
    @Override
    public String createHorizontalRule() {
        return HORIZONTAL_RULE;
    }
}
