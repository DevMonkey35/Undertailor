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

import me.scarlet.undertailor.gfx.text.Text;
import me.scarlet.undertailor.gfx.text.TextComponent;

/**
 * Enumeration of possible parameters to assign within a
 * string to pass for generating a {@link Text} or
 * {@link TextComponent}.
 * 
 * @author FerusGrim
 */
public enum TextParam {

    FONT("FONT"), STYLE("STYLE"), COLOR("COLOR"), SOUND("SOUND"), SPEED("SPEED"), DELAY(
        "DELAY"), SEGMENTSIZE("SEGMENTSIZE"), UNDEFINED("UNDEFINED");

    private final String name;

    TextParam(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public static TextParam of(String string) {
        for (TextParam param : values()) {
            if (param.getName().equalsIgnoreCase(string)) {
                return param;
            }
        }

        return UNDEFINED;
    }
}
