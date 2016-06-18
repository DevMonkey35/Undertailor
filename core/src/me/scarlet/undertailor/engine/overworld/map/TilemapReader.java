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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.util.XMLUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link DefaultHandler} implementation for reading Tiled
 * .tmx files.
 */
public class TilemapReader extends DefaultHandler {

    static final Logger log = LoggerFactory.getLogger(TilemapReader.class);

    private TilesetManager tilesets;

    // processing vars;
    private List<String> tree;

    private Tilemap tilemap;
    private File currentFile;
    private TileLayer currentTileLayer;
    private int[] currentTiles;
    private int currentTile;

    public TilemapReader(TilesetManager tilesets) {
        tree = new ArrayList<>();
        this.tilesets = tilesets;

        this.tilemap = null;
        this.currentTileLayer = null;
        this.currentTiles = null;
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public void startDocument() throws SAXException {
        this.tilemap = new Tilemap();

        this.tree.clear();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
        this.tree.add(qName);

        if (this.checkElement("", "map", qName)) {
            tilemap.width = Integer.parseInt(attributes.getValue("", "width"));
            tilemap.height = Integer.parseInt(attributes.getValue("", "height"));
        }

        if (this.checkElement("map", "tileset", qName)) {
            String name = attributes.getValue("", "name");
            if (name == null) {
                String[] split = attributes.getValue("", "source").split("/");
                name = split[split.length - 1];
                name = name.substring(0, name.length() - 4);
            }

            this.tilemap.tilesets.put(Integer.parseInt(attributes.getValue("", "firstgid")),
                this.tilesets.getTileset(name));
        }

        if (this.checkElement("map", "layer", qName)) {
            String layerName = attributes.getValue("", "name");
            try {
                short layer = Short.parseShort(layerName);
                this.currentTileLayer = new TileLayer();
                this.currentTileLayer.parent = this.tilemap;
                this.currentTileLayer.layer = layer;
            } catch (NumberFormatException ignored) {
                log.warn("ignoring tile layer " + layerName + " in tilemap file "
                    + this.currentFile.getAbsolutePath());
            }
        }

        if (this.checkElement("layer", "data", qName)) {
            if (!attributes.getValue("", "encoding").equalsIgnoreCase("csv")) {
                throw new UnsupportedOperationException(
                    "Undertailor will only support Tiled maps saved in CSV format");
            } else {
                if (this.currentTileLayer != null) {
                    this.currentTiles = new int[this.tilemap.height * this.tilemap.width];
                    this.currentTile = 0;
                }
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.currentTiles != null) {
            String number = "";

            for (int i = 0; i < length; i++) {
                char character = ch[i + start];
                if (character == '\n') {
                    continue;
                } else if (character == ',') {
                    this.currentTiles[currentTile] = Integer.parseInt(number);
                    number = "";
                    currentTile++;
                    if (currentTile >= this.currentTiles.length) {
                        break;
                    }
                } else {
                    number += character;
                }
            }

            if (!number.isEmpty()) {
                this.currentTiles[this.currentTiles.length - 1] = Integer.parseInt(number);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.checkElement("layer", "data", qName)) {
            if (this.currentTileLayer != null && this.currentTiles != null) {
                this.currentTileLayer.tiles = this.currentTiles;
                this.currentTiles = null;

                this.tilemap.layers.add(this.currentTileLayer);
                this.currentTileLayer = null;
            }
        }

        this.tree.remove(qName);
    }

    // ---------------- object ----------------

    /**
     * Reads the provided .tmx file and provides a
     * {@link Tilemap} object providing the loaded tilemap.
     * 
     * @param tmxFile the File pointing to the target .tmx
     *        file
     * 
     * @return the loaded Tilemap
     * 
     * @throws FileNotFoundException if the .tmx file was
     *         not found
     * @throws SAXException if an XML parsing error occurred
     * @throws IOException if a miscellaneous I/O error
     *         occured
     */
    public Tilemap read(File tmxFile) throws FileNotFoundException, SAXException, IOException {
        FileInputStream stream = null;

        try {
            stream = new FileInputStream(tmxFile);
            InputStreamReader reader = new InputStreamReader(stream);
            InputSource source = new InputSource(reader);

            this.currentFile = tmxFile;
            XMLUtil.getParser().parse(source, this);
        } finally {
            if (stream != null)
                stream.close();
        }

        this.tilemap.layers.sort((l1, l2) -> {
            return Short.compare(l1.getLayer(), l2.getLayer());
        });

        return this.tilemap;
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
