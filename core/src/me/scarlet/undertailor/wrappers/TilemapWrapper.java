/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.wrappers;

import me.scarlet.undertailor.environment.overworld.map.Tilemap;
import me.scarlet.undertailor.exception.TextureTilingException;

import java.io.File;

public class TilemapWrapper extends DisposableWrapper<Tilemap> {

    private File meta;
    private File texture;
    private String tilemapName;
    public TilemapWrapper(String tilemapName, File texture, File meta) throws TextureTilingException {
        super(null);
        this.meta = meta;
        this.texture = texture;
        this.tilemapName = tilemapName;
    }

    @Override
    public Tilemap newReference() {
        try {
            return new Tilemap(tilemapName, texture, meta);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
