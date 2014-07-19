package org.froeschl.mddoclet.formatter;


public interface Formatter {
    public static final int HEADING_TITLE = 1;
    public static final int HEADING_ONE = 2;
    public static final int HEADING_TWO = 3;
    public static final int HEADING_THREE = 4;
    
    public String heading(String text, int headingLevel);
    public String paragraph(String text);
    public String bold(String text);
    public String codeBlock(String text);
    public String tableHeader(String ... elements);
    public String tableRow(String ... elements);
    public String link(String text, String link);
    public String anchor(String text, String anchor);
    public String horizontalRule();
    public String lineFeed();
}
