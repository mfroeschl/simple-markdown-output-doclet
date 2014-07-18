package org.froeschl.mddoclet.formatter;

import java.util.Map;

public interface Formatter {
    public String formatClassList(Map<String, String> classNamesAndDescriptions);
}
