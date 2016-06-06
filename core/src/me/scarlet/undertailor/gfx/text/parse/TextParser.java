/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor.gfx.text.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for scanning parameterized strings to
 * generate {@link TextPiece}s and {@link ParsedText}s.
 * 
 * @author FerusGrim
 */
public final class TextParser {

    private static final Pattern CATCH_PATTERN =
        Pattern.compile("((?<!\\\\)\\[([^\\n\\[\\]]+?)(?<!\\\\)\\])+");

    public static List<TextPiece> parse(String input) {
        List<TextPiece> pieces = Lists.newArrayList();

        Map<TextParam, String> current = Maps.newHashMap();
        for (String piece : separate(input)) {
            if (CATCH_PATTERN.matcher(piece).matches()) {
                current.putAll(parseMatches(piece));
            } else {
                pieces.add(TextPiece.of(Maps.newHashMap(current), piece));
            }
        }

        return pieces;
    }

    public static List<String> separate(String input) {
        List<String> results = Lists.newArrayList();
        Matcher regex = CATCH_PATTERN.matcher(input);
        int lastIndex = 0;

        while (regex.find()) {
            if (!input.substring(lastIndex, regex.start()).isEmpty()) {
                results.add(input.substring(lastIndex, regex.start()));
            }

            results.add(regex.group());
            lastIndex = regex.end();
        }

        results.add(input.substring(lastIndex));
        return results;
    }

    public static Map<TextParam, String> parseMatches(String input) {
        Map<TextParam, String> params = Maps.newHashMap();
        Matcher regex = CATCH_PATTERN.matcher(input);

        while (regex.find()) {
            String[] splits = regex.group().split("(\\]\\[)");
            for (String param : splits) {
                String[] array = param.replaceAll("[\\[\\]]", "").split("=");

                TextParam textParam = TextParam.of(array[0]);
                if (textParam == TextParam.UNDEFINED) {
                    continue;
                }

                String value;
                if (array.length < 2) {
                    value = textParam.getDefaultValue();
                } else if (array[1].isEmpty()) {
                    value = textParam.getDefaultValue();
                } else {
                    value = array[1];
                }

                params.put(textParam, value);
            }
        }
        return params;
    }
}
