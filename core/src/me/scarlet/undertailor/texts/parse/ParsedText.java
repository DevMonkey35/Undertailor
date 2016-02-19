package me.scarlet.undertailor.texts.parse;

import java.util.List;

public class ParsedText {

    private final List<TextPiece> pieces;

    public ParsedText(List<TextPiece> pieces) {
        this.pieces = pieces;
    }

    public List<TextPiece> getPieces() {
        return this.pieces;
    }

    /**
     * Takes an unadulterated string and properly formats it
     * into a valid ParsedText object, with its proper pieces.
     *
     * @param input The unedited input
     * @return the valid ParsedText object
     */
    public static ParsedText of(String input) {
        return new ParsedText(TextParser.parse(input));
    }
}
