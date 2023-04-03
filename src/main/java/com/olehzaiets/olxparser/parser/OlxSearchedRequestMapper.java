package com.olehzaiets.olxparser.parser;

import com.olehzaiets.olxparser.common.Constants;
import org.springframework.stereotype.Component;

@Component
public class OlxSearchedRequestMapper {

    private static final String SEARCH_URL_TEMPLATE  = "https://www.olx.ua/d/uk/list/q-";

    private static final String EMPTY = " ";
    private static final String REPLACEMENT = "-";

    public String map(String requestedText) {
        if (requestedText.startsWith(Constants.OLX_URL_PATTERN)) {
            return requestedText;
        }

        return SEARCH_URL_TEMPLATE + requestedText.replaceAll(EMPTY, REPLACEMENT);
    }

}
