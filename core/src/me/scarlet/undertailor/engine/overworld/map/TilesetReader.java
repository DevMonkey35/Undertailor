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

package me.scarlet.undertailor.engine.overworld.map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import me.scarlet.undertailor.engine.overworld.map.TilesetFactory.Tileset;
import me.scarlet.undertailor.util.Tuple;
import me.scarlet.undertailor.util.XMLUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link DefaultHandler} implementation for reading Tiled
 * .tsx files.
 */
public class TilesetReader extends DefaultHandler {

    /**
     * Metadata for Tiled tilesets.
     */
    public static class TilesetMeta {

        private Map<Integer, List<Tuple<Integer, Long>>> animations;

        public TilesetMeta() {
            this.animations = new HashMap<>();
        }

        /**
         * Returns the mapping of animations assigned to the
         * owning {@link Tileset}.
         * 
         * @return a Map of animations
         */
        public Map<Integer, List<Tuple<Integer, Long>>> getAnimations() {
            return this.animations;
        }
    }

    private TilesetMeta meta;

    // processing vars;
    private int id;
    private List<String> tree;

    public TilesetReader() {
        tree = new ArrayList<>();
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public void startDocument() throws SAXException {
        this.meta = new TilesetMeta();

        this.id = -1;
        this.tree.clear();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
        this.tree.add(qName);

        if (this.checkElement("tileset", "tile", qName)) {
            this.id = Integer.parseInt(attributes.getValue("", "id"));
        }

        if (this.checkElement("animation", "frame", qName)) {
            if (!this.meta.animations.containsKey(this.id)) {
                this.meta.animations.put(this.id, new ArrayList<>());
            }

            List<Tuple<Integer, Long>> animations = this.meta.animations.get(this.id);
            animations.add(new Tuple<>(Integer.parseInt(attributes.getValue("", "tileid")),
                Long.parseLong(attributes.getValue("", "duration"))));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.checkElement("tileset", "tile", qName)) {
            this.id = -1;
        }

        this.tree.remove(qName);
    }

    // ---------------- object ----------------

    /**
     * Reads the provided .tsx file and provides a
     * {@link TilesetMeta} object providing extra
     * information associated with the tileset, should there
     * be any.
     * 
     * @param tsxFile the File pointing to the target .tsx
     *        file
     * 
     * @return the TilesetMeta with data for the tileset
     * 
     * @throws FileNotFoundException if the .tsx file was
     *         not found
     * @throws SAXException if an XML parsing error occurred
     * @throws IOException if a miscellaneous I/O error
     *         occured
     */
    public TilesetMeta read(File tsxFile)
        throws FileNotFoundException, SAXException, IOException {
        FileInputStream stream = null;

        try {
            stream = new FileInputStream(tsxFile);
            InputStreamReader reader = new InputStreamReader(stream);
            InputSource source = new InputSource(reader);
            XMLUtil.getParser().parse(source, this);
        } finally {
            if (stream != null)
                stream.close();
        }

        return this.meta;
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Returns the parent element of the current element,
     * or an empty string if the element has no parent (if
     * it is the root node).</p>
     */
    private String getParentElement() {
        if (this.tree.size() == 1) {
            return "";
        }

        return this.tree.get(this.tree.size() - 2);
    }

    /**
     * Internal method.
     * 
     * <p>Convenience method to quickly check an element
     * before processing it.</p>
     * 
     * @param parent the required parent name
     * @param tag the required element name
     * @param qname the qualified name of the current
     *        element
     */
    private boolean checkElement(String parent, String tag, String qName) {
        if (this.getParentElement().equals(parent) && qName.equals(tag)) {
            return true;
        }

        return false;
    }
}
