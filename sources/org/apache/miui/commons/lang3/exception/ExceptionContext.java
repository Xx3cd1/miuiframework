package org.apache.miui.commons.lang3.exception;

import java.util.List;
import java.util.Set;
import org.apache.miui.commons.lang3.tuple.Pair;

public interface ExceptionContext {
    ExceptionContext addContextValue(String str, Object obj);

    List<Pair<String, Object>> getContextEntries();

    Set<String> getContextLabels();

    List<Object> getContextValues(String str);

    Object getFirstContextValue(String str);

    String getFormattedExceptionMessage(String str);

    ExceptionContext setContextValue(String str, Object obj);
}
