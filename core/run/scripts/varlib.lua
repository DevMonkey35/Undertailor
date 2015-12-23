local function argError(index, expected, got)
	if(index == nil) then
		error("bad argument: expected "..expected..", got "..got, 4)
	else
		error("bad argument #"..tostring(index)..": expected "..expected..", got "..got, 4)
	end
end

function checkarg(index, expected, arg, default)
	if(arg == nil) then
		if(default == nil) then
			argError(index, expected, "nil")
		else
			return default
		end
	else
		if(type(arg) ~= expected) then
			argError(index, expected, type(arg))
		end
		
		return arg
	end
end

function checkitable(index, expected, table)
	for k,v in pairs(table) do
		if(type(v) ~= expected) then
			if(index == nil) then
				error("bad argument: table contained entries not of the type "..expected, 3)
			else
				error("bad argument #"..index..": table contained entries not of the type "..expected, 3)
			end
		end
	end
end

function checktablesize(index, table)
	if(#table <= 0) then
		if(index == nil) then
			error("bad argument: table was empty", 3)
		else
			error("bad argument #"..index..": table was empty", 3)
		end
	end
end
