package org.froeschl.mddoclet.printer;


public class SystemPrinter implements Printer {
    @Override
    public void print(String text) {
        System.out.println(text);
    }
}
