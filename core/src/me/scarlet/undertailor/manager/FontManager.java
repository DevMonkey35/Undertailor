package me.scarlet.undertailor.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.TextureTilingException;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.texts.Font.FontData;
import me.scarlet.undertailor.texts.TextComponent;
import me.scarlet.undertailor.texts.TextComponent.Text;
import me.scarlet.undertailor.util.Pair;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FontManager {
    
    public static final String MANAGER_TAG = "fontman";
    
    private Map<String, Font> fonts;
    public FontManager() {
        fonts = new HashMap<>();
    }
    
    public void loadFonts(File dir) {
        String dirPath = dir.getAbsolutePath();
        if(!dir.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load font directory " + dirPath + " (did not exist)");
            return;
        }
        
        if(!dir.isDirectory()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load font directory " + dirPath + " (not a directory)");
            return;
        }
        
        Undertailor.instance.log(MANAGER_TAG, "searching for fonts in " + dirPath);
        Map<String, Pair<File, File>> found = new HashMap<>();
        for(File file : dir.listFiles()) {
            if(!file.getName().endsWith(".png") && !file.getName().endsWith(".underfont")) {
                continue;
            }
            
            String name = file.getName().substring(0, file.getName().endsWith(".png") ? file.getName().length() - 4 : file.getName().length() - 10);
            if(!found.containsKey(name)) {
                found.put(name, new Pair<>());
            }
            
            if(file.getName().endsWith(".png")) {
                Undertailor.instance.debug(MANAGER_TAG, "found spritesheet file");
                found.get(name).setFirstElement(file);
            } else if(file.getName().endsWith(".underfont")) {
                Undertailor.instance.debug(MANAGER_TAG, "found underfont file");
                found.get(name).setSecondElement(file);
            }
        }
        
        for(Entry<String, Pair<File, File>> entry : found.entrySet()) {
            if(!entry.getValue().getFirstElement().isPresent() || !entry.getValue().getSecondElement().isPresent()) {
                continue;
            }
            
            Undertailor.instance.log(MANAGER_TAG, "loading font " + entry.getKey());
            Texture spriteSheet = new Texture(Gdx.files.absolute(entry.getValue().getFirstElement().get().getAbsolutePath()));
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder()
                    .setFile(entry.getValue().getSecondElement().get())
                    .build();
            FontData data;
            try {
                data = FontData.fromConfig(entry.getKey(), loader.load());
            } catch(IOException e) {
                Undertailor.instance.error(MANAGER_TAG, "failed to load .underfont config for font " + entry.getKey(), e.getStackTrace());
                continue;
            }
            
            Font font;
            try {
                font = new Font(spriteSheet, data);
                this.registerFont(font);
            } catch(TextureTilingException e) {
                Undertailor.instance.error(MANAGER_TAG, "could not load font " + entry.getKey() + "; " + e.getMessage());
            }
        }
    }
    
    public Font getFont(String name) {
        if(fonts.containsKey(name)) {
            return fonts.get(name);
        }
        
        Undertailor.instance.error(MANAGER_TAG, "system requested a non-existing font (" + name + ")");
        return null;
    }
    
    public void registerFont(Font font) {
        fonts.put(font.getFontData().getName(), font);
        Undertailor.instance.log(MANAGER_TAG, "registered font " + font.getFontData().getName());
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
