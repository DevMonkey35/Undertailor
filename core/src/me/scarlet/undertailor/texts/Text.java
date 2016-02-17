package me.scarlet.undertailor.texts;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Preconditions;
import me.scarlet.undertailor.audio.SoundWrapper;
import me.scarlet.undertailor.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Text extends TextComponent {
    private List<TextComponent> members;

    public Text(Font font) {
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
    }

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
            return new Text(super.font, super.style, super.color, super.textSound, super.speed, super.segmentSize, super.wait,
                    new TextComponent("", super.font, super.style, super.color, super.textSound, super.speed, super.segmentSize, super.wait));
        }

        if(members.size() == 1) {
            return new Text(super.font, super.style, super.color, super.textSound, super.speed, super.segmentSize, super.wait, members.get(0).substring(start, end));
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

        return new Text(super.font, super.style, super.color, super.textSound, super.speed, super.segmentSize, super.wait, compList.toArray(new TextComponent[compList.size()]));
    }
}