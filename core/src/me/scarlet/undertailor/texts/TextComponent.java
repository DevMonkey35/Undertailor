package me.scarlet.undertailor.texts;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Preconditions;
import me.scarlet.undertailor.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class TextComponent {
    
    public static class DisplayMeta {
        
        public static DisplayMeta defaults() {
            return new DisplayMeta();
        }
        
        public float offX, offY, scale, rotation;
        public Color color;
        
        public DisplayMeta() {}
        
        public DisplayMeta(float offX, float offY, float scale, float rotation, Color color) {
            this.offX = offX;
            this.offY = offY;
            this.scale = 1.0F;
            this.rotation = 0F;
            this.color = Color.WHITE;
        }
        
    }
    
    public static class Text extends TextComponent {
        private Sound sound;
        private List<TextComponent> members;
        public Text(Color color, Style style, Font font, Sound sound, int speed, float wait, TextComponent... components) {
            super(null, color, style, font, speed, wait);
            this.members = new ArrayList<>();
            this.sound = sound;
            
            for(TextComponent component : components) {
                component.parent = this;
                members.add(component);
            }
        }
        
        @Override
        public String getText() {
            StringBuilder sb = new StringBuilder();
            members.forEach(com -> {
                sb.append(com.getText());
            });
            
            return sb.toString();
        }
        
        public Optional<Sound> getCharacterSound() {
            return Optional.ofNullable(sound);
        }
        
        public List<TextComponent> getMembers() {
            return members;
        }
        
        public TextComponent getComponentAtCharacter(int chara) {
            Preconditions.checkArgument(this.getText().length() >= chara || chara < 0, "char index out of bounds");
            
            if(members.size() == 1) {
                return members.get(0);
            }
            
            List<Pair<TextComponent, Integer>> compList = new ArrayList<>();
            for(int i = 0; i < members.size(); i++) {
                TextComponent member = members.get(i);
                int len = member.getText().length();
                if(!compList.isEmpty()) {
                    len = len + compList.get(compList.size() - 1).getFirstElement().get().getText().length();
                }
                
                compList.add(new Pair<>(member, len));
            }
            
            for(int i = 0; i < compList.size(); i++) {
                if(i == 0) {
                    continue;
                }
                
                if(compList.get(i - 1).getSecondElement().get() > chara && chara <= compList.get(i).getSecondElement().get()) {
                    return compList.get(i - 1).getFirstElement().get();
                }
            }
            
            return compList.get(compList.size() - 1).getFirstElement().get();
        }
        
        @Override
        public Text substring(int start) {
            return substring(start, getText().length());
        }
        
        @Override
        public Text substring(int start, int end) {
            if(start == end) {
                return new Text(super.color, super.style, super.font, sound, super.speed, super.wait, new TextComponent("", super.color, super.style, super.font, super.speed, 0F));
            }
            
            if(members.size() == 1) {
                return new Text(super.color, super.style, super.font, sound, super.speed, super.wait, members.get(0).substring(start, end));
            }
            
            Map<TextComponent, Integer> compMap = new LinkedHashMap<>();
            
            TextComponent last = null;
            for(int i = 0; i < members.size(); i++) {
                TextComponent member = members.get(i);
                int len = member.getText().length();
                if(last != null) {
                    len = len + compMap.get(last);
                }
                
                last = member;
                compMap.put(member, len);
            }
            
            List<TextComponent> compList = new ArrayList<>();
            Entry<TextComponent, Integer> previous = null;
            int processed = 0;
            for(Entry<TextComponent, Integer> entry : compMap.entrySet()){
                TextComponent added = null;
                if(compList.isEmpty()) { // first one
                    if(start < entry.getValue()) {
                        int val = processed == 0 ? start : start - previous.getValue();
                        added = entry.getKey().substring(val);
                    }
                } else {
                    added = entry.getKey();
                    if(end <= entry.getValue()) {
                        if(end < previous.getValue()) {
                            int lastIndex = compList.size() - 1;
                            TextComponent comp = compList.get(lastIndex);
                            compList.remove(lastIndex);
                            if(processed == 1) {
                                compList.add(comp.substring(start, end));
                            } else {
                                compList.add(comp.substring(0, previous.getValue() - end));
                            }
                            
                            break;
                        } else {
                            int val = end - previous.getValue();
                            
                            added = entry.getKey().substring(0, val);
                            compList.add(added);
                            break;
                        }
                    }
                }
                
                if(added != null) {
                    compList.add(added);
                }
                
                previous = entry;
                processed++;
            }
            
            return new Text(super.color, super.style, super.font, sound, super.speed, super.wait, compList.toArray(new TextComponent[compList.size()]));
        }
    }
    
    public static interface Style {
        public DisplayMeta apply(char character); // called for every letter, including spaces cuz why not
    }
    
    private TextComponent parent;
    private String text;
    private Color color;
    private Style style;
    private Font font;
    private int speed;
    private float wait; // default 0, if there's a delay between text components
    
    public static TextComponent of(String text, Font font) {
        return new TextComponent(text, Color.WHITE, null, font, 5, 0F);
    }
    
    public TextComponent(String text, Color color, Style style, Font font, Integer speed, float wait) {
        this.text = text;
        this.color = color == null ? Color.WHITE : color;
        this.style = style;
        this.speed = speed == null ? 5 : speed;
        this.font = font;
        this.wait = wait;
    }
    
    public String getText() {
        return text;
    }
    
    public Color getColor() {
        if(color == null && parent != null) {
            return parent.color;
        }
        
        return color;
    }
    
    public int getSpeed() {
        if(speed <= -1 && parent != null) {
            return parent.speed;
        }
        
        return speed;
    }
    
    public Style getStyle() {
        if(style == null && parent != null) {
            return parent.style;
        }
        
        return style;
    }
    
    public Font getFont() {
        if(font == null && parent != null) {
            return parent.font;
        }
        
        return font;
    }
    
    public float getDelay() {
        return wait;
    }
    
    public TextComponent substring(int start) {
        return substring(start, this.text.length());
    }
    
    public TextComponent substring(int start, int end) {
        return new TextComponent(text.substring(start, end), color, style, font, speed, wait);
    }
}