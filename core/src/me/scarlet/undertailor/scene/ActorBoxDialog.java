package me.scarlet.undertailor.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.scarlet.undertailor.manager.FontManager;
import me.scarlet.undertailor.util.RenderUtil;
import me.scarlet.undertailor.util.RenderUtil.Rectangle;
import me.scarlet.undertailor.util.TextComponent.Text;

import java.util.Map.Entry;

public class ActorBoxDialog extends ActorTextRenderer {
    
    public static class BoxDialogMeta extends ActorTextRenderer.TextRendererMeta {
        
        public int x2, y2;
        public int lineDistance; // pixels between lines (by the first character's bottom-left pixel)
        public int asteriskDistance; // pixels between the left border and the beginning asterisk
        public int distanceFromTopBorder; // pixels between top border and first line's bottom-left pixel
        public int distanceFromLeftBorder; // pixels between left border all lines' left-most pixels
        
        public BoxDialogMeta() {
            this.x = 31; // decides box boundaries instead of text placement
            this.y = 160;
            this.x2 = 609;
            this.y2 = 8;
            
            this.lineDistance = 18;
            this.asteriskDistance = 11;
            this.distanceFromTopBorder = 20;
            this.distanceFromLeftBorder = 27;
        }
        
        public BoxDialogMeta(int x1, int y1, int x2, int y2) {
            this();
            this.x = x1;
            this.y = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        public BoxDialogMeta(int x1, int y1, int x2, int y2, int lineDistance, int asteriskDistance, int distanceFromTopBorder, int distanceFromLeftBorder) {
            this(x1, y1, x2, y2);
            this.lineDistance = lineDistance;
            this.asteriskDistance = asteriskDistance;
            this.distanceFromTopBorder = distanceFromTopBorder;
            this.distanceFromLeftBorder = distanceFromLeftBorder;
        }
    }
    
    private boolean visibleBorder; 
    private BoxDialogMeta meta;
    
    public ActorBoxDialog(BoxDialogMeta meta) {
        super(meta);
        this.meta = meta;
        this.visibleBorder = true;
        
        this.setBoxAlpha(1.0F);
    }
    
    public void setBoxAlpha(float alpha) {
        this.setBoxAlpha(alpha, false);
    }
    
    public void setBoxAlpha(float alpha, boolean smooth) {
        this.alpha[2] = alpha;
        if(!smooth) {
            this.alpha[2] = alpha;
        }
    }
    
    public void setBoxVisible(boolean flag) {
        this.visibleBorder = flag;
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        prepareAlphas();
        if(visible) {
            if(visibleBorder) {
                ShapeRenderer render = new ShapeRenderer();
                render.setProjectionMatrix(this.getStage().getCamera().combined);
                int width, height;
                width = Math.abs(meta.x - meta.x2);
                height = Math.abs(meta.y - meta.y2);
                RenderUtil.drawRectangle(render, Rectangle.builder()
                        .lineThickness(6)
                        .posX(meta.x)
                        .posY(meta.y2)
                        .width(width)
                        .height(height)
                        .build(), parentAlpha * alpha[0] * alpha[2]);
                render.dispose();
            }
            
            if(visibleText) {
                if(this.getDrawn() != null) {
                    SpriteBatch sbatch = new SpriteBatch();
                    int x = (meta.x + (meta.distanceFromLeftBorder * 2)) + 6;
                    int y = (meta.y - (meta.distanceFromTopBorder * 2)) - 6;
                    for(Entry<Integer, Text> entry : this.getDrawn().entrySet()) {
                        Text text = entry.getValue();
                        FontManager.write(sbatch, text.getFont(), text.getText(), text.getText().startsWith("*") ? x - (meta.asteriskDistance * 2): x, y - (entry.getKey() * (meta.lineDistance * 2)), 2, parentAlpha * alpha[0] * alpha[1], text.getColor());
                    }
                    
                    sbatch.dispose();
                }
            }
        }
    }
}
