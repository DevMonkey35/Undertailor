package me.scarlet.undertailor.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import me.scarlet.undertailor.texts.TextComponent.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ActorTextRenderer extends Actor {
    
    public static class TextRendererMeta {
        public int x, y, scale, distanceFromAsterisk, lineDistance;
        public TextRendererMeta() {
            this.x = 0;
            this.y = 0;
            this.scale = 2;
            this.lineDistance = 18;
            this.distanceFromAsterisk = 16;
        }
        
        public TextRendererMeta(int x, int y, int scale, int lineDistance, int distanceFromAsterisk) {
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.lineDistance = lineDistance;
            this.distanceFromAsterisk = distanceFromAsterisk;
        }
    }
    
    protected float alpha[];
    protected float toAlpha[];
    protected float alphaSpeed;
    protected boolean finished;
    protected boolean removeNext;
    protected TextRendererMeta meta;
    protected Map<Integer, Text> drawn;
    protected boolean visible, visibleText;
    
    public ActorTextRenderer(TextRendererMeta meta) {
        this.meta = meta;
        this.removeNext = false; // if true, we get advance the action on top of our stack
        this.drawn = new HashMap<>();
        this.visible = false;
        this.visibleText = true;
        this.alphaSpeed = 0.05F;
        this.finished = false;
        
        this.alpha = new float[3];
        this.toAlpha = new float[3];
        
        this.setAlpha(1.0F);
        this.setTextAlpha(1.0F);
    }
    
    public boolean allowNextActor() {
        return false;
    }
    
    public boolean removeOnFinish() {
        return true;
    }
    
    @Override
    public void act(float delta) {
        if(this.hasActions()) {
            this.setFinished(false);
            ActionPlayText act = (ActionPlayText) this.getActions().get(0);
            boolean finished = act.act(delta);
            if(act.isControlled()) {
                if(act.isWaiting()) {
                    this.removeAction(act);
                } else {
                    if(this.removeNext) {
                        this.removeNext = false;
                        act.skip();
                    }
                }
            } else {
                if(finished) {
                    this.removeAction(act);
                }
            }
        } else {
            this.setFinished(true);
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    private void setFinished(boolean flag) {
        this.finished = flag;
    }
    
    public Collection<Text> getCurrentText() {
        return this.drawn.values();
    }
    
    protected Map<Integer, Text> getDrawn() {
        return drawn;
    }
    
    public void setVisible(boolean flag) {
        this.visible = flag;
    }
    
    public void setAlpha(float alpha) {
        this.setAlpha(alpha, false);
    }
    
    public void setAlpha(float alpha, boolean smooth) {
        this.toAlpha[0] = alpha;
        if(!smooth) {
            this.alpha[0] = alpha;
        }
    }
    
    public void setTextVisible(boolean flag) {
        this.visibleText = flag;
    }
    
    public void setTextAlpha(float alpha) {
        this.setTextAlpha(alpha, false);
    }
    
    public void setTextAlpha(float alpha, boolean smooth) {
        this.toAlpha[1] = alpha;
        if(!smooth) {
            this.alpha[1] = alpha;
        }
    }
    
    protected void prepareAlphas() {
        for(int i = 0; i < alpha.length; i++) {
            if(alpha[i] != toAlpha[i]) {
                if(toAlpha[i] > alpha[i]) {
                    alpha[i] = alpha[i] + alphaSpeed > toAlpha[i] ? toAlpha[i] : alpha[i] + alphaSpeed;
                } else {
                    alpha[i] = alpha[i] - alphaSpeed < toAlpha[i] ? toAlpha[i] : alpha[i] - alphaSpeed;
                }
            }
        }
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        prepareAlphas();
        if(visible) {
            if(visibleText) {
                if(this.getDrawn() != null) {
                    SpriteBatch sbatch = new SpriteBatch();
                    for(Entry<Integer, Text> entry : this.getDrawn().entrySet()) {
                        Text text = entry.getValue();
                        String newText = text.getText().startsWith("*") ? text.getText().substring(1) : text.getText();
                        int astX = meta.x;
                        int x = meta.x + (meta.distanceFromAsterisk * meta.scale);
                        int y = meta.y - ((meta.lineDistance * entry.getKey()) * meta.scale);
                        if(text.getText().startsWith("*")) {
                            text.getFont().write(sbatch, "*", text.getStyle(), astX, y, meta.scale, parentAlpha * alpha[0] * alpha[1], text.getColor());
                        }
                        
                        text.getFont().write(sbatch, newText, text.getStyle(), x, y, meta.scale, parentAlpha * alpha[0] * alpha[1], text.getColor());
                    }
                    
                    sbatch.dispose();
                }
            }
        }
    }
}
