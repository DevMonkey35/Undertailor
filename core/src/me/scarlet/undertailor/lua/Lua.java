package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.lua.lib.ColorsLib;
import me.scarlet.undertailor.lua.lib.GameLib;
import me.scarlet.undertailor.lua.lib.SchedulerLib;
import me.scarlet.undertailor.lua.lib.TextLib;
import me.scarlet.undertailor.lua.lib.UtilLib;
import me.scarlet.undertailor.lua.lib.game.AnimationLib;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.game.GraphicsLib;
import me.scarlet.undertailor.lua.lib.game.LoggerLib;
import me.scarlet.undertailor.lua.lib.game.OverworldLib;
import me.scarlet.undertailor.lua.lib.game.StoreLib;
import me.scarlet.undertailor.lua.lib.game.UILib;
import me.scarlet.undertailor.lua.lib.meta.LuaBoundingBoxMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaEntrypointMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaInputDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaPressDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaStyleMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaUIComponentMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaUIObjectMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldObjectMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldRoomMeta;
import me.scarlet.undertailor.lua.lib.text.TextComponentLib;
import org.luaj.vm2.LuaValue;

/**
 * Class of constants to hold anything in relation to custom Lua objects that would not change throughout runtime.
 * 
 * <p>All Lua object typenames should be stored in and referenced through this
 * class.</p>
 * 
 * <p>This class should only contain metatables for Lua objects that exist as
 * proxies for Java objects. Any other metatables should be retrieved in an
 * alternative method.</p>
 */
public class Lua {
    
    // typenames of objects without non-library metatables
    public static final String TYPENAME_TEXTCOMPONENT = "tailor-textcomponent";
    public static final String TYPENAME_DISPLAYMETA = "tailor-displaymeta";
    public static final String TYPENAME_ANIMATION = "tailor-animation";
    public static final String TYPENAME_TEXT = "tailor-text";
    public static final String TYPENAME_COLOR = "gdx-color";
    public static final String TYPENAME_SOUND = "tailor-audio-sound";
    public static final String TYPENAME_MUSIC = "tailor-audio-music";
    
    // typenames of objects with non-library metatables
    public static final String TYPENAME_WORLDOBJECT = "tailor-worldobj";
    public static final String TYPENAME_WORLDROOM = "tailor-room";
    public static final String TYPENAME_UICOMPONENT = "tailor-uicomponent";
    public static final String TYPENAME_BOUNDINGBOX = "tailor-boundingbox";
    public static final String TYPENAME_ENTRYPOINT = "tailor-entrypoint";
    public static final String TYPENAME_PRESSDATA = "tailor-pressdata";
    public static final String TYPENAME_INPUTDATA = "tailor-inputdata";
    public static final String TYPENAME_STYLE = "tailor-textstyle";
    public static final String TYPENAME_UIOBJECT = "tailor-uiobj";
    
    // metatables
    public static final LuaValue META_WORLDOBJECT = LuaLibrary.asMetatable(new LuaWorldObjectMeta());
    public static final LuaValue META_WORLDROOM = LuaLibrary.asMetatable(new LuaWorldRoomMeta());
    public static final LuaValue META_UICOMPONENT = LuaLibrary.asMetatable(new LuaUIComponentMeta());
    public static final LuaValue META_BOUNDINGBOX = LuaLibrary.asMetatable(new LuaBoundingBoxMeta());
    public static final LuaValue META_ENTRYPOINT = LuaLibrary.asMetatable(new LuaEntrypointMeta());
    public static final LuaValue META_INPUTDATA = LuaLibrary.asMetatable(new LuaInputDataMeta());
    public static final LuaValue META_PRESSDATA = LuaLibrary.asMetatable(new LuaPressDataMeta());
    public static final LuaValue META_STYLE = LuaLibrary.asMetatable(new LuaStyleMeta());
    public static final LuaValue META_UIOBJECT = LuaLibrary.asMetatable(new LuaUIObjectMeta());
    
    // shared libs -- non-parental top-level
    public static final SchedulerLib LIB_SCHEDULER = new SchedulerLib();
    public static final ColorsLib LIB_COLORS = new ColorsLib();
    public static final UtilLib LIB_UTIL = new UtilLib();
    
    // shared libs -- text children
    public static final TextComponentLib LIB_TEXTCOMPONENT = new TextComponentLib();
    
    // shared libs -- game children
    public static final AudioLib LIB_AUDIO = new AudioLib();
    public static final AnimationLib LIB_ANIMATION = new AnimationLib();
    public static final GraphicsLib LIB_GRAPHICS = new GraphicsLib();
    public static final LoggerLib LIB_LOGGER = new LoggerLib();
    public static final OverworldLib LIB_OVERWORLD = new OverworldLib();
    public static final StoreLib LIB_STORE = new StoreLib();
    public static final UILib LIB_UI = new UILib();

    // shared libs -- parental top level
    public static final TextLib LIB_TEXT = new TextLib();
    public static final GameLib LIB_GAME = new GameLib();
}
