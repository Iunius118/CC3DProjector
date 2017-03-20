# CC3DProjector
for ComputerCraft 1.79 (Minecraft 1.8.9 with Forge)

+ **Download:** [[1.8.9]CC3DProjector-0.0.1.jar (from MediaFire)](http://www.mediafire.com/file/858qvuzj4uc966j/%5B1.8.9%5DCC3DProjector-0.0.1.jar)

## Lua sample code
```Lua
local projector = peripheral.find( "3d_projector" )
-- Model script
local model = {{"color",3},{"alpha",0.5},{"translate",{0.5,0.125,0.5}},{"oscillate",1,0,2},{"translate",{0,0.125,0}},{"oscillate",0,0,6},{"rotateY",180},{"face",{-0.41,0.333333,0.2357},{0,0.666667,0.4714},{0,1,0},{-0.41,0.666667,-0.2357}},{"face",{0,0.666667,0.4714},{0.41,0.333333,0.2357},{0.41,0.666667,-0.2357},{0,1,0}},{"face",{0.41,0.333333,0.2357},{0,0,0},{0,0.333333,-0.4714},{0.41,0.666667,-0.2357}},{"face",{0,0,0},{-0.41,0.333333,0.2357},{-0.41,0.666667,-0.2357},{0,0.333333,-0.4714}},{"face",{-0.41,0.666667,-0.2357},{0,1,0},{0.41,0.666667,-0.2357},{0,0.333333,-0.4714}},{"face",{0,0,0},{0.41,0.333333,0.2357},{0,0.666667,0.4714},{-0.41,0.333333,0.2357}}}

if projector then
  -- Write model script and Start drawing model
  projector.write( model )
  sleep( 15 )  --  Draw model for 15 seconds

  -- Read model from peripheral and Save it to a file
  local h = fs.open( "model", "w" )
  h.write( textutils.serialize( projector.read() ) )
  h.close()

  -- Clear model and Stop drawing model
  projector.clear()
else
  print( "Peripheral 3d_projector not found" )
end
```
___
Copyright 2017 Iunius118
