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

public class TextComponent {
    
    public static class DisplayMeta {
        
        public static DisplayMeta defaults() {
            return new DisplayMeta();
        }
        
        public float offX, offY, scaleX, scaleY;
        public Color color;
        
        public DisplayMeta() {
            this(0, 0, 1.0F, 1.0F, Color.WHITE);
        }
        
        public DisplayMeta(float offX, float offY, float scaleX, float scaleY, Color color) {
            this.offX = offX;
            this.offY = offY;
            this.scaleX = scaleX;
            this.scaleY = scaleX;
            this.color = color;
        }
    }
    
    public static class Text extends TextComponent {
        private List<TextComponent> members;
        public Text(Font font, Style style, Color color, Sound sound, Integer speed, Float wait, TextComponent... components) {
            super(null, font, style, color, sound, speed, wait);
            this.members = new ArrayList<>();
            
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
        
        public List<TextComponent> getMembers() {
            return new ArrayList<>(members);
        }
        
        public void addComponents(TextComponent... components) {
            for(TextComponent component : components) {
                component.parent = this;
                members.add(component);
            }
        }
        
        public TextComponent getComponentAtCharacter(int chara) {
            Preconditions.checkArgument(!members.isEmpty(), "no members within this Text object");
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
                return new Text(super.font, super.style, super.color, super.textSound, super.speed, super.wait,
                        new TextComponent("", super.font, super.style, super.color, super.textSound, super.speed, super.wait));
            }
            
            if(members.size() == 1) {
                return new Text(super.font, super.style, super.color, super.textSound, super.speed, super.wait, members.get(0).substring(start, end));
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
            
            return new Text(super.font, super.style, super.color, super.textSound, super.speed, super.wait, compList.toArray(new TextComponent[compList.size()]));
        }
    }
    
    private TextComponent parent;
    private Sound textSound;
    private String text;
    private Color color;
    private Style style;
    private Font font;
    private Integer speed;  // characters per second?
    private Float wait; // delay between text components
    
    public static final int DEFAULT_SPEED = 35;
    
    public static TextComponent of(String text, Font font) {
        return new TextComponent(text, font);
    }
    
    public TextComponent(String text, Font font) {
        this(text, font, null);
    }
    
    public TextComponent(String text, Font font, Style style) {
        this(text, font, style, null);
    }
    public TextComponent(String text, Font font, Style style, Color color) {
        this(text, font, style, color, null);
    }
    
    public TextComponent(String text, Font font, Style style, Color color, Sound textSound) {
        this(text, font, style, color, textSound, DEFAULT_SPEED);
    }
    
    public TextComponent(String text, Font font, Style style, Color color, Sound textSound, Integer speed) {
        this(text, font, style, color, textSound, speed, 0F);
    }
    
    public TextComponent(String text, Font font, Style style, Color color, Sound textSound, Integer speed, Float wait) {
        this.text = text;
        this.textSound =  textSound;
        this.color = color;
        this.speed = speed;
        this.wait = wait;
        this.style = style;
        this.font = font;
    }
    
    public String getText() {
        return text;
    }
    
    public Color getColor() {
        if(color == null) {
            if(parent != null && parent.color != null) {
                return parent.color;
            }
            
            return Color.WHITE;
        }
        
        return color;
    }
    
    public Sound getSound() {
        if(textSound == null && parent != null) {
            return parent.textSound;
        }
        
        return textSound;
    }
    
    public int getSpeed() {
        if(speed == null) {
            if(parent != null && parent.speed != null) {
                return parent.speed;
            }
            
            return DEFAULT_SPEED;
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
        if(wait == null) {
            if(parent != null && parent.wait != null) {
                return parent.wait;
            }
            
            return 0F;
        }
        
        return wait;
    }
    
    public TextComponent substring(int start) {
        return substring(start, this.text.length());
    }
    
    public TextComponent substring(int start, int end) {
        return new TextComponent(text.substring(start, end), font, style, color, textSound, speed, wait);
    }
}
