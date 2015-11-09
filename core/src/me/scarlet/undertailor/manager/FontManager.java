package me.scarlet.undertailor.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.texts.Font.FontData;
import me.scarlet.undertailor.util.Pair;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FontManager {
    
    private Map<String, Font> fonts;
    public FontManager() {
        fonts = new HashMap<>();
    }
    
    public byte loadFonts(File dir) {
        byte clear = 0x00;
        byte dirNotFound = 0x01;
        byte notDirectory = 0x02;
        byte exit = clear;
        
        if(!dir.exists()) {
            return dirNotFound;
        }
        
        if(!dir.isDirectory()) {
            return notDirectory;
        }
        
        Gdx.app.log("fontman", "searching for fonts in " + dir.getAbsolutePath());
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
                Gdx.app.log("fontman", "found spritesheet file");
                found.get(name).setFirstElement(file);;
            } else if(file.getName().endsWith(".underfont")) {
                Gdx.app.log("fontman", "found underfont file");
                found.get(name).setSecondElement(file);
            }
        }
        
        for(Entry<String, Pair<File, File>> entry : found.entrySet()) {
            if(!entry.getValue().getFirstElement().isPresent() || !entry.getValue().getSecondElement().isPresent()) {
                Gdx.app.log("fontman", "no font pair");
                continue;
            }
            
            Gdx.app.log("fontman", "loading font " + entry.getKey());
            Texture spriteSheet = new Texture(Gdx.files.absolute(entry.getValue().getFirstElement().get().getAbsolutePath()));
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder()
                    .setFile(entry.getValue().getSecondElement().get())
                    .build();
            FontData data;
            try {
                data = FontData.fromConfig(entry.getKey(), loader.load());
            } catch(IOException e) {
                Gdx.app.error("fontman", "failed to load .underfont config for font " + entry.getKey());
                e.printStackTrace();
                continue;
            }
            
            Font font = new Font(spriteSheet, data);
            this.registerFont(font);
        }
        
        return exit;
    }
    
    public Font getFont(String name) {
        return fonts.get(name);
    }
    
    public void registerFont(Font font) {
        fonts.put(font.getFontData().getName(), font);
        Gdx.app.log("fontman", "registered font " + font.getFontData().getName());
    }
}
