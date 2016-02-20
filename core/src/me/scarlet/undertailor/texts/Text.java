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

package me.scarlet.undertailor.texts;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Preconditions;
import me.scarlet.undertailor.audio.SoundWrapper;
import me.scarlet.undertailor.texts.parse.ParsedText;
import me.scarlet.undertailor.texts.parse.TextParam;
import me.scarlet.undertailor.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Text extends TextComponent {
    
    public static Text fromString(String defaultParams, String str) {
        ParsedText components = ParsedText.of(str);
        ParsedText params = ParsedText.of(defaultParams);
        
        Map<TextParam, String> defaults;
        if(params.getPieces().size() < 0) {
            throw new IllegalArgumentException("no parameters specified");
        }
        
        return null;
    }
    
    public static class Builder extends TextComponent.Builder {
        
        private List<TextComponent> components;
        
        public Builder() {
            super();
            this.components = new ArrayList<>();
        }
        
        // overrides
        @Override public Builder setText(String text) { return (Builder) super.setText(text); }
        @Override public Builder setFont(Font font) { return (Builder) super.setFont(font); }
        @Override public Builder setStyle(Style style) { return (Builder) super.setStyle(style); }
        @Override public Builder setColor(Color color) { return (Builder) super.setColor(color); }
        @Override public Builder setTextSound(SoundWrapper textSound) { return (Builder) super.setTextSound(textSound); }
        @Override public Builder setSpeed(int speed) { return (Builder) super.setSpeed(speed); }
        @Override public Builder setSegmentSize(int segmentSize) { return (Builder) super.setSegmentSize(segmentSize); }
        @Override public Builder setDelay(float delay) { return (Builder) super.setDelay(delay); }
        
        public Builder addComponents(TextComponent... components) {
            for(TextComponent component : components) {
                this.components.add(component);
            }
            
            return this;
        }
        
        @Override
        public Text build() {
            Text text = new Text();
            
            if(this.font == null) {
                throw new IllegalStateException("cannot generate a text object with no default font");
            }
            
            text.font = this.font;
            text.style = this.style;
            text.color = this.color;
            text.textSound = this.textSound;
            text.speed = this.speed;
            text.segmentSize = this.segmentSize;
            text.delay = this.delay;
            components.forEach(text::addComponents);
            
            return text;
        }
    }
    
    public static Builder builder() {
        return new Text.Builder();
    }
    
    private List<TextComponent> members;
    
    private Text() {
        this.members = new ArrayList<>();
    }

    /*public Text(Font font) {
        this(font, null);
    }

    public Text(Font font, Style style) {
        this(font, style, Color.WHITE);
    }

    public Text(Font font, Style style, Color color) {
        this(font, style, color, null);
    }

    public Text(Font font, Style style, Color color, SoundWrapper sound) {
        this(font, style, color, sound, DEFAULT_SPEED);
    }

    public Text(Font font, Style style, Color color, SoundWrapper sound, Integer speed) {
        this(font, style, color, sound, speed, 1);
    }

    public Text(Font font, Style style, Color color, SoundWrapper sound, Integer speed, Integer segmentSize) {
        this(font, style, color, sound, speed, segmentSize, 0F);
    }

    public Text(Font font, Style style, Color color, SoundWrapper sound, Integer speed, Integer segmentSize, Float wait, TextComponent... components) {
        super(null, font, style, color, sound, speed, segmentSize, wait);
        this.members = new ArrayList<>();

        for(TextComponent component : components) {
            component.parent = this;
            members.add(component);
        }
    }*/

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        members.forEach(com -> sb.append(com.getText()));

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
        for (TextComponent member : members) {
            int len = member.getText().length();
            if (!compList.isEmpty()) {
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
            throw new IllegalArgumentException("cannot substring into an empty text object");
            /*return new Text(super.font, super.style, super.color, super.textSound, super.speed, super.segmentSize, super.delay,
                    new TextComponent("", super.font, super.style, super.color, super.textSound, super.speed, super.segmentSize, super.delay));*/
        }

        if(members.size() == 1) {
            return Text.builder()
                    .setFont(super.font)
                    .setStyle(super.style)
                    .setColor(super.color)
                    .setTextSound(super.textSound)
                    .setSpeed(super.speed)
                    .setSegmentSize(super.segmentSize)
                    .setDelay(super.delay)
                    .addComponents(members.get(0).substring(start, end))
                    .build();
            //return new Text(super.font, super.style, super.color, super.textSound, super.speed, super.segmentSize, super.delay, members.get(0).substring(start, end));
        }

        Map<TextComponent, Integer> compMap = new LinkedHashMap<>();

        TextComponent last = null;
        for (TextComponent member : members) {
            int len = member.getText().length();
            if (last != null) {
                len = len + compMap.get(last);
            }

            last = member;
            compMap.put(member, len);
        }

        List<TextComponent> compList = new ArrayList<>();
        Map.Entry<TextComponent, Integer> previous = null;
        int processed = 0;
        for(Map.Entry<TextComponent, Integer> entry : compMap.entrySet()){
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

        return Text.builder()
                .setFont(super.font)
                .setStyle(super.style)
                .setColor(super.color)
                .setTextSound(super.textSound)
                .setSpeed(super.speed)
                .setSegmentSize(super.segmentSize)
                .setDelay(super.delay)
                .addComponents(compList.toArray(new TextComponent[compList.size()]))
                .build();
    }
}