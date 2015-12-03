-- Text player UI component
-- Plays the text given to it.
-- Doesn't care how it's registered.

local _component;
local _currentText;
local _controllable;
local _texts;

function create(component, texts, controllable)
	_component = component;
	if(type(texts) ~= "table")
		error("bad argument #2: expected table, got "..type(texts))
	end
	
	if(type(controllable) ~= "boolean") then
		error("bad argument #2: expected table, got "..type(texts))
	end
	_controllable = controllable
	
	for k,v in ipairs(texts) do
		if(type(v) ~= "tailor-text") then
			error("bad argument #2: table contained entries not of the type tailor-text")
		end
	end
	
	_texts = texts;
end

function process(delta)
	
end