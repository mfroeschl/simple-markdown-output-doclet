package org.froeschl.mddoclet.printer;


public class SystemPrinter implements Printer {
    @Override
    public void print(String fileName, String text) {
        System.out.println(text);
    }
}
