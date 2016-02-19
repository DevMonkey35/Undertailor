package me.scarlet.undertailor.texts.parse;

import com.google.common.collect.Maps;

import java.util.Map;

public class TextPiece {

    private final Map<TextParam, String> params;
    private String message;

    public TextPiece(String message, Map<TextParam, String> params) {
        this.message = message;
        this.params = params;
    }

    public Map<TextParam, String> getParams() {
        return this.params;
    }

    public String getMessage() {
        return this.message;
    }

    public static TextPiece of(Map<TextParam, String> params, String message) {
        return new TextPiece(message, params);
    }

    public static TextPiece of(String message) {
        return new TextPiece(message, Maps.newHashMap());
    }
}
