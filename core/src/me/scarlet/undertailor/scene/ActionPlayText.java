package me.scarlet.undertailor.scene;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.texts.TextComponent;
import me.scarlet.undertailor.texts.TextComponent.Text;

import java.util.List;

public class ActionPlayText extends Action {
    
    private Text currentText;
    private List<Text> texts;
    private int ctid, currentChar;
    private ActorTextRenderer actor;
    private boolean controlled, isWaiting;
    private long lastCharacterTime, charDelay, nextCharDelay, waitBetween, lastTextTime;
    public ActionPlayText(ActorTextRenderer actor, List<Text> texts, boolean controlled) {
        this.ctid = -1;
        this.texts = texts;
        this.actor = actor;
        this.currentText = null;
        this.currentChar = 1;
        this.lastTextTime = -1;
        this.lastCharacterTime = -1;
        this.controlled = controlled;
        this.waitBetween = 0;
        this.charDelay = 0;
        this.isWaiting = false;
    }
    
    @Override
    public boolean act(float delta) {
        boolean waitTimeDone = lastTextTime == -1 || TimeUtils.timeSinceMillis(lastTextTime + waitBetween) > 0;
        boolean lastTextDone = currentText == null || currentChar == this.getTextMax(currentText);
        if(waitTimeDone && lastTextDone) {
            ctid++;
            if(ctid < texts.size() && texts.get(ctid) != null) {
                currentText = texts.get(ctid);
                currentChar = 1;
                
                this.lastTextTime = TimeUtils.millis();
                this.waitBetween = (long) (1000.0 * currentText.getDelay());
            } else {
                if(controlled) {
                    this.isWaiting = true;
                    return false; // wait to be removed
                } else {
                    return true;
                }
            }
        }
        
        if(!lastTextDone) {
            Text toDraw = currentText.substring(0, currentChar + 1);
            if(canWriteCharacter(toDraw) && currentChar + 1 <= this.getTextMax(currentText)) {
                if(currentText.substring(0, currentChar).getText().endsWith(" ")) {
                    actor.getDrawn().put(ctid, currentText.substring(0, currentChar + 2));
                    currentChar += 2;
                } else {
                    actor.getDrawn().put(ctid, currentText.substring(0, currentChar + 1));
                    currentChar += 1;
                }
                
                Sound sound = currentText.getComponentAtCharacter(currentChar).getSound();
                if(sound != null && currentChar != this.getTextMax(currentText)) {
                    sound.play();
                }
                
                this.lastCharacterTime = TimeUtils.millis();
            }
        }
        
        return false;
    }
    
    public boolean isControlled() {
        return this.controlled;
    }
    
    public boolean isWaiting() {
        return this.isWaiting; // waiting for removal?
    }
    
    public void skip() {
        for(int i = 0; i < texts.size(); i++) {
            actor.drawn.put(i, texts.get(i));
        }
        
        int lastIndex = texts.size() - 1;
        this.ctid = texts.size();
        this.currentText = texts.get(lastIndex);
        this.currentChar = this.getTextMax(currentText);
    }
    
    private boolean canWriteCharacter(Text toDraw) {
        String currentComp = currentText
                .getComponentAtCharacter(currentChar)
                .getText().trim();
        TextComponent currentDrawn = toDraw.getComponentAtCharacter(currentChar);
        if(currentDrawn.getText().trim().length() == currentComp.length() && this.charDelay <= 0) {
            this.nextCharDelay = (long) (1000.0 * currentDrawn.getDelay());
        }
        
        long textSpeed = ((long) 1000.0 / (currentText.getSpeed() <= 0 ? 1 : currentText.getSpeed()));
        long compare = TimeUtils.timeSinceMillis(this.lastCharacterTime + textSpeed + charDelay);
        if(this.lastCharacterTime != -1 && compare < 0) {
            return false;
        }
        
        if(currentText == null) {
            return false;
        }
        
        boolean returned = lastCharacterTime <= -1 || compare >= 0;
        if(returned == true) {
            if(charDelay == 0) {
                this.charDelay = nextCharDelay;
                this.nextCharDelay = 0;
            } else {
                this.charDelay = 0;
            }
        }
        
        return returned;
    }
    
    private int getTextMax(Text text) {
        return text.getText().length();
    }
}
