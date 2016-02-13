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

package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.lua.lib.ColorsLib;
import me.scarlet.undertailor.lua.lib.GameLib;
import me.scarlet.undertailor.lua.lib.StoreLib;
import me.scarlet.undertailor.lua.lib.TextLib;
import me.scarlet.undertailor.lua.lib.UtilLib;
import me.scarlet.undertailor.lua.lib.game.AnimationLib;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.game.EnvironmentLib;
import me.scarlet.undertailor.lua.lib.game.GraphicsLib;
import me.scarlet.undertailor.lua.lib.game.LoggerLib;
import me.scarlet.undertailor.lua.lib.meta.LuaBoundingCircleMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaBoundingRectangleMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaEntrypointMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaInputDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaOverworldControllerMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaPressDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaRoomMapMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaSchedulerMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaStyleMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaUIComponentMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaUIControllerMeta;
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
    
    // typenames of objects with library metatables
    public static final String TYPENAME_TEXTCOMPONENT = "tailor-textcomponent";
    public static final String TYPENAME_DISPLAYMETA = "tailor-displaymeta";
    public static final String TYPENAME_ANIMATION = "tailor-animation";
    public static final String TYPENAME_TEXT = "tailor-text";
    public static final String TYPENAME_COLOR = "tailor-color";
    public static final String TYPENAME_SOUND = "tailor-audio-sound";
    public static final String TYPENAME_MUSIC = "tailor-audio-music";
    public static final String TYPENAME_ENVIRONMENT = "tailor-environment";
    
    // typenames of objects with normal metatables
    public static final String TYPENAME_WORLDOBJECT = "tailor-worldobj";
    public static final String TYPENAME_WORLDROOM = "tailor-room";
    public static final String TYPENAME_UICOMPONENT = "tailor-uicomponent";
    public static final String TYPENAME_BOUNDINGBOX_CIRCLE = "tailor-boundingbox-circle";
    public static final String TYPENAME_BOUNDINGBOX_RECTANGLE = "tailor-boundingbox-rectangle";
    public static final String TYPENAME_ENTRYPOINT = "tailor-entrypoint";
    public static final String TYPENAME_PRESSDATA = "tailor-pressdata";
    public static final String TYPENAME_INPUTDATA = "tailor-inputdata";
    public static final String TYPENAME_STYLE = "tailor-textstyle";
    public static final String TYPENAME_UIOBJECT = "tailor-uiobj";
    public static final String TYPENAME_WORLDMAP = "tailor-roommap";
    public static final String TYPENAME_OVERWORLDCONTROLLER = "tailor-ovwcontroller";
    public static final String TYPENAME_UICONTROLLER = "tailor-uicontroller";
    public static final String TYPENAME_SCHEDULER = "tailor-scheduler";
    
    // metatables
    public static final LuaValue META_WORLDOBJECT = LuaLibrary.asMetatable(new LuaWorldObjectMeta());
    public static final LuaValue META_WORLDROOM = LuaLibrary.asMetatable(new LuaWorldRoomMeta());
    public static final LuaValue META_UICOMPONENT = LuaLibrary.asMetatable(new LuaUIComponentMeta());
    public static final LuaValue META_BOUNDINGBOX_CIRCLE = LuaLibrary.asMetatable(new LuaBoundingCircleMeta());
    public static final LuaValue META_BOUNDINGBOX_RECTANGLE = LuaLibrary.asMetatable(new LuaBoundingRectangleMeta());
    public static final LuaValue META_ENTRYPOINT = LuaLibrary.asMetatable(new LuaEntrypointMeta());
    public static final LuaValue META_INPUTDATA = LuaLibrary.asMetatable(new LuaInputDataMeta());
    public static final LuaValue META_PRESSDATA = LuaLibrary.asMetatable(new LuaPressDataMeta());
    public static final LuaValue META_STYLE = LuaLibrary.asMetatable(new LuaStyleMeta());
    public static final LuaValue META_UIOBJECT = LuaLibrary.asMetatable(new LuaUIObjectMeta());
    public static final LuaValue META_WORLDMAP = LuaLibrary.asMetatable(new LuaRoomMapMeta());
    public static final LuaValue META_OVERWORLDCONTROLLER = LuaLibrary.asMetatable(new LuaOverworldControllerMeta());
    public static final LuaValue META_UICONTROLLER = LuaLibrary.asMetatable(new LuaUIControllerMeta());
    public static final LuaValue META_SCHEDULER = LuaLibrary.asMetatable(new LuaSchedulerMeta());
    
    // shared libs -- non-parental top-level
    public static final ColorsLib LIB_COLORS = new ColorsLib();
    public static final UtilLib LIB_UTIL = new UtilLib();
    
    // shared libs -- text children
    public static final TextComponentLib LIB_TEXTCOMPONENT = new TextComponentLib();
    
    // shared libs -- game children
    public static final AudioLib LIB_AUDIO = new AudioLib();
    public static final AnimationLib LIB_ANIMATION = new AnimationLib();
    public static final GraphicsLib LIB_GRAPHICS = new GraphicsLib();
    public static final LoggerLib LIB_LOGGER = new LoggerLib();
    public static final StoreLib LIB_STORE = new StoreLib();
    public static final EnvironmentLib LIB_ENVIRONMENT = new EnvironmentLib();

    // shared libs -- parental top level
    public static final TextLib LIB_TEXT = new TextLib();
    public static final GameLib LIB_GAME = new GameLib();
}
