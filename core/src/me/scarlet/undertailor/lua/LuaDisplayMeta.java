package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaDisplayMeta extends LuaValue {
    
    public static final String TYPENAME = "tailor-displaymeta";
    public static LuaValue METATABLE;
    
    public static LuaDisplayMeta checkDisplayMeta(LuaValue value) {
        if(!isDisplayMeta(value)) {
            throw new LuaError("bad argument: expected a " + LuaDisplayMeta.TYPENAME + "; got " + value.typename());
        }
        
        return (LuaDisplayMeta) value;
    }
    
    public static boolean isDisplayMeta(LuaValue value) {
        return value.typename().equals(LuaDisplayMeta.TYPENAME);
    }
    
    static class _getPositionOffsets extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            checkDisplayMeta(args.arg1());
            
            DisplayMeta meta = ((LuaDisplayMeta) args.arg1()).getDisplayMeta();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(meta.offX),
                    LuaValue.valueOf(meta.offY)
            });
        }
    }
    
    static class _getScaleOffsets extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            checkDisplayMeta(args.arg1());
            
            DisplayMeta meta = ((LuaDisplayMeta) args.arg1()).getDisplayMeta();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(meta.scaleX),
                    LuaValue.valueOf(meta.scaleY)
            });
        }
    }
    
    static class _getColor extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            checkDisplayMeta(arg);
            
            DisplayMeta meta = ((LuaDisplayMeta) arg).getDisplayMeta();
            return new LuaColor(meta.color);
        }
    }
    
    static {
        LuaTable meta = new LuaTable();
        meta.set("getColor", new _getColor());
        meta.set("getScaleOffsets", new _getScaleOffsets());
        meta.set("getPositionOffsets", new _getPositionOffsets());
        
        METATABLE = LuaValue.tableOf(new LuaValue[] {
                LuaValue.INDEX,
                meta
        });
    }
    
    private DisplayMeta meta;
    public LuaDisplayMeta() {
        this(null);
    }
    
    public LuaDisplayMeta(DisplayMeta meta) {
        this.meta = meta;
    }
    
    public DisplayMeta getDisplayMeta() {
        return meta;
    }
    
    @Override
    public int type() {
        return LuaValue.TVALUE;
    }

    @Override
    public String typename() {
        return LuaDisplayMeta.TYPENAME;
    }
}
