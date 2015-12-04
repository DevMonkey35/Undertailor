package me.scarlet.undertailor.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.TextureTilingException;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.texts.Font.FontData;
import me.scarlet.undertailor.texts.TextComponent;
import me.scarlet.undertailor.texts.TextComponent.Text;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FontManager extends Manager<Font> {
    
    public static final String MANAGER_TAG = "fontman";
    
    private Map<String, Font> fonts;
    public FontManager() {
        fonts = new HashMap<>();
    }
    
    public void loadObjects(File dir) {
        loadObjects(dir, null);
        Undertailor.instance.log(MANAGER_TAG, fonts.keySet().size() + " font(s) currently loaded");
    }
    
    public void loadObjects(File dir, String heading) {
        String dirPath = dir.getAbsolutePath();
        if(!dir.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load font directory " + dirPath + " (did not exist)");
            return;
        }
        
        if(!dir.isDirectory()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load font directory " + dirPath + " (not a directory)");
            return;
        }
        
        if(heading == null) {
            heading = "";
        }
        
        Undertailor.instance.log(MANAGER_TAG, "searching for fonts in " + dirPath);
        for(File file : dir.listFiles(file -> {
            return file.isDirectory() || file.getName().endsWith(".png");
        })) {
            if(file.isDirectory()) {
                if(heading.isEmpty() && file.getName().equals("styles")) {
                    continue;
                }
                
                loadObjects(file, heading + (heading.isEmpty() ? "" : ".") + file.getName() + ".");
                continue;
            }
            
            String name = file.getName().substring(0, file.getName().length() - 4);
            String entryName = heading + name;
            File fontDef = new File(dir, name + ".underfont");
            if(!fontDef.exists()) {
                Undertailor.instance.warn(MANAGER_TAG, "ignoring room " + entryName + " (no map file)");
                continue;
            }
            
            if(!fontDef.isFile()) {
                Undertailor.instance.warn(MANAGER_TAG, "ignoring room " + entryName + " (bad map file)");
                continue;
            }
            
            Texture spriteSheet = new Texture(Gdx.files.absolute(file.getAbsolutePath()));
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder()
                    .setFile(fontDef)
                    .build();
            FontData data;
            try {
                data = FontData.fromConfig(name, loader.load());
            } catch(IOException e) {
                Undertailor.instance.error(MANAGER_TAG, "failed to load .underfont config for font " + entryName, e.getStackTrace());
                continue;
            }
            
            Font font;
            try {
                font = new Font(spriteSheet, data);
                fonts.put(font.getFontData().getName(), font);
            } catch(TextureTilingException e) {
                Undertailor.instance.error(MANAGER_TAG, "could not load font " + entryName + "; " + e.getMessage());
            }
        }
    }
    
    public Font getObject(String name) {
        if(fonts.containsKey(name)) {
            return fonts.get(name);
        }
        
        Undertailor.instance.error(MANAGER_TAG, "system requested a non-existing font (" + name + ")");
        return null;
    }
    
    public void write(Text text, float posX, float posY) {
        write(text, posX, posY, 1);
    }
    
    public void write(Text text, float posX, float posY, float scale) {
        write(text, posX, posY, scale, scale);
    }
    
    public void write(Text text, float posX, float posY, float scaleX, float scaleY) {
        write(text, posX, posY, scaleX, scaleY, 1.0F);
    }
    
    public void write(Text text, float posX, float posY, float scaleX, float scaleY, float alpha) {
        if(text == null) {
            return;
        }
        
        if(text.getMembers().size() < 1) {
            return; // ignore empty texts
        }
        
        if(text.getMembers().size() == 1) {
            text.getFont().write(text.getText(), text.getStyle(), text.getColor(), posX, posY, scaleX, scaleY, alpha);
        } else {
            int pos = 0;
            for(int i = 0; i < text.getMembers().size(); i++) {
                TextComponent component = text.getMembers().get(i);
                pos += component.getFont().write(component.getText(), component.getStyle(), component.getColor(), posX + pos, posY, scaleX, scaleY, alpha);
            }
        }
    }
}
