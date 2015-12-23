require("scripts/mathhelper.lua")

function snapOnCollide(blocker, collider)
	local blockerX, blockerY = blocker:getPosition()
	local colliderX, colliderY = collider:getPosition()
	local velocityX, velocityY = collider:getVelocity()
	
	local up;
	local right;
	local distanceX;
	local distanceY;
	local bdX, bdY = blocker:getBoundingBox():getDimensions()
	local cdX, cdY = collider:getBoundingBox():getDimensions()
	bdX = bdX/2
	bdY = bdY/2
	cdX = cdX/2
	cdY = cdY/2
	if(blockerX < colliderX) then
		right = collider;
	else
		right = blocker;
	end
	
	if(blockerY < colliderY) then
		up = collider;
	else
		up = blocker;
	end
	
	distanceX = math.abs(blockerX - colliderX)
	distanceY = math.abs(blockerY - colliderY)
	
	local npx = colliderX
	local npy = colliderY
	if(distanceX < bdX + cdX + 1) then
		npx = colliderX - velocityX
	end
	
	if(distanceY < bdY + cdY + 1) then
		npy = colliderY - velocityY
	end
	
	collider:setPosition(npx, npy)
	collider:setVelocity(0, 0)
end