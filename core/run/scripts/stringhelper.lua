function endsWith(string, ending)
	return ending == "" or string:sub(1, -ending:len()) == ending
end

function startsWith(string, starting)
	return string:sub(1, starting:len()) == starting
end