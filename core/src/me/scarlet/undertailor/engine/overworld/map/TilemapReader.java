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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import me.scarlet.undertailor.AssetManager;
import me.scarlet.undertailor.engine.overworld.map.ObjectLayer.ShapeData;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.util.Wrapper;
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

    private short layerId;
    private Tilemap tilemap;
    private SAXParser parser;
    private MultiRenderer renderer;
    private TileLayer currentTileLayer;
    private ImageLayer currentImageLayer;
    private ObjectLayer currentObjectLayer;

    private ShapeData currentShape;
    private Wrapper<Object> wrapper;
    private String layerName;
    private String tileData;

    public TilemapReader(TilesetManager tilesets, MultiRenderer renderer) {
        tree = new ArrayList<>();
        this.tilesets = tilesets;
        this.renderer = renderer;

        this.tilemap = null;
        this.currentTileLayer = null;
        this.tileData = null;
        this.parser = XMLUtil.generateParser();
        this.wrapper = new Wrapper<>();
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public void startDocument() throws SAXException {
        this.layerId = 0;
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
            this.currentTileLayer = new TileLayer();
            this.currentTileLayer.name = attributes.getValue("", "name");
            this.currentTileLayer.id = this.layerId++;
            this.currentTileLayer.parent = this.tilemap;
            this.layerName =
                this.currentTileLayer.name == null ? "unnamed layer" : this.currentTileLayer.name;
        }

        if (this.checkElement("map", "imagelayer", qName)) {
            this.currentImageLayer = new ImageLayer();
            this.currentImageLayer.id = this.layerId++;

            String name = attributes.getValue("", "name");
            String sX = attributes.getValue("", "offsetx");
            String sY = attributes.getValue("", "offsety");
            float x = sX == null ? 0 : Float.parseFloat(sX);
            float y = sY == null ? 0 : this.toGDXPoint(Float.parseFloat(sY));

            this.layerName = name == null ? "unnamed" : name;
            this.currentImageLayer.setPosition(x, y);
        }

        if (this.currentTileLayer != null && this.checkElement("layer", "data", qName)) {
            if (!attributes.getValue("", "encoding").equalsIgnoreCase("csv")) {
                throw new UnsupportedOperationException(
                    "Undertailor will only support Tiled maps saved in CSV format");
            } else {
                this.tileData = "";
            }
        }

        if (this.currentImageLayer != null && this.checkElement("imagelayer", "image", qName)) {
            this.wrapper.set(null);
            String[] sourceNameSplit = attributes.getValue("", "source").split("/");
            File file = new File(new File(AssetManager.rootDirectory.getAbsolutePath(), AssetManager.DIR_TILEMAP_IMAGES),
                sourceNameSplit[sourceNameSplit.length - 1]);
            AssetManager.addTask(() -> {
                synchronized (wrapper) {
                    this.wrapper.set(new Texture(Gdx.files.absolute(file.getAbsolutePath())));
                    this.wrapper.notifyAll();
                }
            });

            synchronized (wrapper) {
                while (wrapper.get() == null) {
                    try {
                        wrapper.wait();
                    } catch (InterruptedException ignore) {
                    }
                }
            }

            this.currentImageLayer.image = (Texture) this.wrapper.get();
            this.currentImageLayer.position.y =
                this.currentImageLayer.position.y - ((Texture) this.wrapper.get()).getHeight();
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
            this.currentShape.position.y = this.toGDXPoint(Float.parseFloat(attributes.getValue("", "y")));

            // only circle/rectangle have these
            String shapeWidth = attributes.getValue("", "width");
            this.currentShape.shapeWidth = shapeWidth == null ? 0 : Float.valueOf(shapeWidth);

            String shapeHeight = attributes.getValue("", "height");
            this.currentShape.shapeHeight = shapeHeight == null ? 0 : Float.valueOf(shapeHeight);
        }

        if (this.checkElement("object", "polygon", qName)
            || this.checkElement("object", "polyline", qName)) {
            String[] points = attributes.getValue("", "points").split(" ");
            this.currentShape.shapeVertices = new float[points.length * 2];
            for (int i = 0; i < points.length; i++) {
                String[] point = points[i].split(",");
                this.currentShape.shapeVertices[i * 2] = Float.parseFloat(point[0]);
                this.currentShape.shapeVertices[(i * 2) + 1] = Float.parseFloat(point[1]);
            }
        }

        // tile layer custom props
        if (this.currentTileLayer != null && this.checkElement("properties", "property", qName)) {
            if (attributes.getValue("", "name").equals("layer")) {
                String value = attributes.getValue("", "value");
                try {
                    this.currentTileLayer.layer = Short.parseShort(value);
                    this.currentTileLayer.layerSet = true;
                } catch (NumberFormatException e) {
                    log.warn("Invalid layer value \"" + value + "\" for tile layer " + layerName);
                    log.warn("Skipping tile layer \"" + layerName + "\"");
                    this.currentTileLayer = null;
                    this.layerName = null;
                    this.tileData = null;
                }
            }
        }

        // image layer custom props
        if (this.currentImageLayer != null && this.checkElement("properties", "property", qName)) {
            String key = attributes.getValue("", "name");
            String value = attributes.getValue("", "value");
            if (key.equals("layer")) {
                try {
                    this.currentImageLayer.layer = Short.parseShort(value);
                    this.currentImageLayer.layerSet = true;
                } catch (NumberFormatException e) {
                    log.warn("Invalid layer value \"" + value + "\" for image layer " + layerName);
                    log.warn("Skipping image layer \"" + layerName + "\"");
                    this.currentImageLayer = null;
                }
            }

            if(this.currentImageLayer != null && key.equals("threshold")) {
                try {
                    float threshold = Float.parseFloat(value);
                    if(threshold > 1.0) threshold = 1F;
                    if(threshold < 0) threshold = 0F;
                    this.currentImageLayer.threshold = threshold;
                } catch(NumberFormatException e) {
                    log.warn("Invalid threshold value " + value + " for image layer " + layerName);
                    log.warn(layerName + " threshold value defaulted to 0");
                }
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
            String[] tileSplit = this.tileData.split(",");
            int[] tiles = new int[tileSplit.length];

            for (int i = 0; i < tiles.length; i++) {
                tiles[i] = Integer.parseInt(tileSplit[i]);
            }

            this.currentTileLayer.tiles = tiles;
            this.tileData = null;
        }

        if (this.checkElement("map", "layer", qName)) {
            if (this.currentTileLayer != null) {
                if (!this.currentTileLayer.layerSet) {
                    log.warn("Layer \"" + layerName + "\" is missing layer property");
                    log.warn("Skipping tile layer \"" + layerName + "\"");
                } else {
                    this.tilemap.tileLayers.add(this.currentTileLayer);
                }

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
            if (this.currentShape.name != null
                && (this.currentShape.type == null || this.currentShape.type == Shape.Type.Circle)
                && (this.currentShape.shapeWidth == 0 && this.currentShape.shapeHeight == 0)) {
                this.currentObjectLayer.points.put(this.currentShape.name,
                    this.currentShape.position);
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

        if(this.checkElement("map", "imagelayer", qName)) {
            if (this.currentImageLayer != null) {
                if (!this.currentImageLayer.layerSet) {
                    log.warn("Layer \"" + layerName + "\" is missing layer property");
                    log.warn("Skipping image layer \"" + layerName + "\"");
                } else {
                    this.currentImageLayer.renderer = this.renderer;
                    this.tilemap.imageLayers.add(this.currentImageLayer);
                }

                this.currentImageLayer = null;
            }
        }

        this.tree.remove(qName);
    }

    @Override
    public void endDocument() throws SAXException {
        this.tilemap.tileLayers.sort((l1, l2) -> {
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

    float toGDXPoint(float y) {
        return this.tilemap.getOccupiedHeight() - y;
    }
}
