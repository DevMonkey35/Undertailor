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

import com.badlogic.gdx.physics.box2d.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import me.scarlet.undertailor.engine.overworld.map.ObjectLayer.ShapeData;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.util.XMLUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;

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
    private SAXParser parser;
    private TileLayer currentTileLayer;
    private ObjectLayer currentObjectLayer;

    private ShapeData currentShape;
    private String tileData;

    public TilemapReader(TilesetManager tilesets) {
        tree = new ArrayList<>();
        this.tilesets = tilesets;

        this.tilemap = null;
        this.currentTileLayer = null;
        this.tileData = null;
        this.parser = XMLUtil.generateParser();
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public void startDocument() throws SAXException {
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
                    this.tileData = "";
                }
            }
        }

        if (this.checkElement("map", "objectgroup", qName)) {
            this.currentObjectLayer = new ObjectLayer();
            this.currentObjectLayer.name = attributes.getValue("", "name");
        }

        if (this.checkElement("objectgroup", "object", qName)) {
            // will always have these
            this.currentShape = new ShapeData();
            this.currentShape.name = attributes.getValue("", "name");
            this.currentShape.position.x = Float.parseFloat(attributes.getValue("", "x"));
            this.currentShape.position.y = Float.parseFloat(attributes.getValue("", "y"));

            // only circle/rectangle have these
            String shapeWidth = attributes.getValue("", "width");
            this.currentShape.shapeWidth = shapeWidth == null ? 0 : Float.valueOf(shapeWidth);

            String shapeHeight = attributes.getValue("", "height");
            this.currentShape.shapeHeight = shapeHeight == null ? 0 : Float.valueOf(shapeHeight);
        }

        if (this.checkElement("object", "polygon", qName)
            || this.checkElement("object", "polyline", qName)) {
            String[] points = attributes.getValue("", "points").split(" ");
            for (int i = 0; i < points.length; i++) {
                String[] point = points[i].split(",");
                this.currentShape.shapeVertices = new float[points.length * 2];
                this.currentShape.shapeVertices[i * 2] = Float.parseFloat(point[0]);
                this.currentShape.shapeVertices[(i * 2) + 1] = Float.parseFloat(point[1]);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.tileData != null) {
            for (int i = 0; i < length; i++) {
                char character = ch[i + start];
                if (character == '\n') {
                    continue;
                } else {
                    this.tileData += character;
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.checkElement("layer", "data", qName)) {
            if (this.currentTileLayer != null && this.tileData != null) {
                String[] tileSplit = this.tileData.split(",");
                int[] tiles = new int[tileSplit.length];

                for (int i = 0; i < tiles.length; i++) {
                    tiles[i] = Integer.parseInt(tileSplit[i]);
                }

                this.currentTileLayer.tiles = tiles;
                this.tileData = null;

                this.tilemap.layers.add(this.currentTileLayer);
                this.currentTileLayer = null;
            }
        }

        if (this.checkElement("object", "polygon", qName)) {
            if (this.currentShape.shapeVertices.length > 16) {
                throw new UnsupportedOperationException(
                    "libGDX's Box2D will not permit polygons with more than 8 vertices");
            }

            this.currentShape.type = Shape.Type.Polygon;
        }

        if (this.checkElement("object", "polyline", qName)) {
            this.currentShape.type = Shape.Type.Chain;
        }

        if (this.checkElement("object", "ellipse", qName)) {
            this.currentShape.type = Shape.Type.Circle;
        }

        if (this.checkElement("objectgroup", "object", qName)) {
            if (this.currentShape.shapeWidth == 0 && this.currentShape.shapeHeight == 0) { // width/height of 0
                if (this.currentShape.name != null && (this.currentShape.type == null
                    || this.currentShape.type == Shape.Type.Circle)) { // has a name and is a circle/rectangle
                    this.currentObjectLayer.points.put(this.currentShape.name,
                        this.currentShape.position);
                }
            } else {
                this.currentShape.generateVertices();
                this.currentObjectLayer.shapes.add(this.currentShape);
            }

            this.currentShape = null;
        }

        if (this.checkElement("map", "objectgroup", qName)) {
            this.tilemap.objects.put(this.currentObjectLayer.name, this.currentObjectLayer);
            this.currentObjectLayer = null;
        }

        this.tree.remove(qName);
    }

    @Override
    public void endDocument() throws SAXException {
        this.tilemap.layers.sort((l1, l2) -> {
            return Short.compare(l1.getLayer(), l2.getLayer());
        });
    }

    // ---------------- object ----------------

    /**
     * Reads the provided .tmx file and provides a
     * {@link Tilemap} object providing the loaded tilemap.
     * 
     * @param tmxFile the File pointing to the target .tmx
     *        file
     * @param tilemap the Tilemap to load the map data into
     * 
     * @return the provided Tilemap, now loaded
     * 
     * @throws FileNotFoundException if the .tmx file was
     *         not found
     * @throws SAXException if an XML parsing error occurred
     * @throws IOException if a miscellaneous I/O error
     *         occured
     */
    public Tilemap read(File tmxFile, Tilemap tilemap)
        throws FileNotFoundException, SAXException, IOException {
        FileInputStream stream = null;
        this.tilemap = tilemap;

        try {
            stream = new FileInputStream(tmxFile);
            InputStreamReader reader = new InputStreamReader(stream);
            InputSource source = new InputSource(reader);

            this.currentFile = tmxFile;
            parser.parse(source, this);
        } finally {
            if (stream != null)
                stream.close();
        }

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
