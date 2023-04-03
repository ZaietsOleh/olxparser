package com.olehzaiets.olxparser.parser;

import java.util.List;

public record AdInformation(
        String id,
        String url,
        String name,
        String price,
        String publishTime,
        String description,
        List<String> imagesUrls
) { }
