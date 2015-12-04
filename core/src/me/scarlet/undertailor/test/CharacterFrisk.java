package me.scarlet.undertailor.test;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.AnimationSet;
import me.scarlet.undertailor.overworld.WorldObject;
import me.scarlet.undertailor.util.InputRetriever.InputData;

public class CharacterFrisk extends WorldObject {
    
    public static final String[] IDLE_ANIMS = new String[] {"idle_up", "idle_down", "idle_left", "idle_right"};
    public static final String[] MOVING_ANIMS = new String[] {"walk_up", "walk_down", "walk_left", "walk_right"};

    private short direction;
    private AnimationSet set;
    private float movementSpeed;
    public CharacterFrisk() {
        this.direction = 1;
        this.movementSpeed = 80F;
        this.set = Undertailor.getAnimationManager().getAnimationSet("frisk").getReference(this);
        this.setBoundingBoxSize(20F, 20F);
        this.setBoundingBoxOrigin(10F, 8F);
        this.setCurrentAnimation(set.getAnimation(IDLE_ANIMS[direction]));
    }
    
    @Override
    public void process(float delta, InputData input) {
        float xvel = 0F;
        float yvel = 0F;
        if(input != null && !input.isConsumed()) {
            if(input.getPressData(Keys.UP).isPressed()) {
                direction = 0;
                yvel = movementSpeed;
            }
            
            if(input.getPressData(Keys.DOWN).isPressed()) {
                direction = 1;
                yvel = -1 * movementSpeed;
            }
            
            if(input.getPressData(Keys.LEFT).isPressed()) {
                direction = 2;
                xvel = -1 * movementSpeed;
            }
            
            if(input.getPressData(Keys.RIGHT).isPressed()) {
                direction = 3;
                xvel = movementSpeed;
            }
        }
        
        if(xvel == 0F && yvel == 0F) {
            setCurrentAnimation(set.getAnimation(IDLE_ANIMS[direction]));
        } else {
            setCurrentAnimation(set.getAnimation(MOVING_ANIMS[direction]));
            Vector2 pos = getPosition();
            float xpos = pos.x + (xvel * delta);
            float ypos = pos.y + (yvel * delta);
            setPosition(xpos, ypos);
            Undertailor.getOverworldController().setCameraPosition(xpos, ypos);
        }
    }
}
