package org.froeschl.mddoclet.formatter;


public interface Formatter {
    public static final int HEADING_TITLE = 1;
    public static final int HEADING_ONE = 2;
    public static final int HEADING_TWO = 3;
    public static final int HEADING_THREE = 4;
    
    public String createHeading(String text, int headingLevel);
    public String createParagraph(String text);
    public String createTableHeader(String ... elements);
    public String createTableRow(String ... elements);
    public String createLink(String text, String link);
    public String createAnchor(String text, String anchor);
    public String createHorizontalRule();
}
