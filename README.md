# CC3DProjector

CC3DProjector is a programmable rendering peripheral for ComputerCraft.
Using this peripheral, players can render points, lines and quadrangles on the World.

## Requirements

+ Minecraft 1.12.2
+ Minecraft Forge 1.12.2-14.23.5.2859+
+ ComputerCraft 1.80pr1/CC: Tweaked for 1.12.2

## Download

+ [v1.12.2-1.1.0.1](https://github.com/Iunius118/CC3DProjector/releases/tag/v1.12.2-1.1.0.1)

## Recipe

e = Eye of Ender, d = Diamond, # = Stone

```text
#d#
#e#
###
```

## Lua sample code

```Lua
local projector = peripheral.find( "3d_projector" )
-- Model script
local model = {
  {"color",3},
  {"alpha",0.5},
  {"translate",{0.5,0.125,0.5}},
  {"oscillate",1,0,2},
  {"translate",{0,0.125,0}},
  {"oscillate",0,0,6},
  {"rotateY",180},
  {"face",{-0.41,0.333333,0.2357},{0,0.666667,0.4714},{0,1,0},{-0.41,0.666667,-0.2357}},
  {"face",{0,0.666667,0.4714},{0.41,0.333333,0.2357},{0.41,0.666667,-0.2357},{0,1,0}},
  {"face",{0.41,0.333333,0.2357},{0,0,0},{0,0.333333,-0.4714},{0.41,0.666667,-0.2357}},
  {"face",{0,0,0},{-0.41,0.333333,0.2357},{-0.41,0.666667,-0.2357},{0,0.333333,-0.4714}},
  {"face",{-0.41,0.666667,-0.2357},{0,1,0},{0.41,0.666667,-0.2357},{0,0.333333,-0.4714}},
  {"face",{0,0,0},{0.41,0.333333,0.2357},{0,0.666667,0.4714},{-0.41,0.333333,0.2357}}
  }

if projector then
  -- Write model script and Start drawing model
  projector.write( model )
  sleep( 15 )  -- for 15 seconds

  -- Read model script from peripheral and Save to file
  local h = fs.open( "model", "w" )
  h.write( textutils.serialize( projector.read() ) )
  h.close()

  -- Clear model and Stop drawing model
  projector.clear()
else
  print( "Peripheral 3d_projector not found" )
end
```

## Script Reference

```Lua
model = { tCommand1, tCommand2, tCommand3, ... }
```

### Color commands

#### Color

```Lua
{ "color", nColor }
```

+ nColor ( 0 <= nColor <= 15 ): Same as 16 colors of Advanced Computers

#### Alpha

```Lua
{ "alpha", nAlpha }
```

+ nAlpha ( 0.0 <= nAlpha <= 1.0 ): Opacity

### Drawing commands

```Lua
tVertex = { nX, nY, nZ }
```

#### Point

```Lua
{ "point", tVertex }
{ "point", tVertex_1, tVertex_2, ... }  -- up to tVertex_255
```

+ Same as GL_POINTS mode
+ { "point", v1, v2, v3 } will draw points at v1, v2 and v3

#### Line

```Lua
{ "line", tVertex_1, tVertex_2 }
{ "line", tVertex_1, tVertex_2, tVertex_3, ... }  -- up to tVertex_255
```

+ Same as GL_LINE_STRIP mode
+ { "line", v1, v2, v3 } will draw lines: v1 - v2 - v3

#### Loop

```Lua
{ "loop", tVertex_1, tVertex_2, tVertex_3 }
{ "loop", tVertex_1, tVertex_2, tVertex_3, tVertex_4, ... }  -- up to tVertex_255
```

+ Same as GL_LINE_LOOP mode
+ { "loop", v1, v2, v3 } will draw lines: v1 - v2 - v3 - **v1**

#### Face

```Lua
{ "face", tVertex_1, tVertex_2, tVertex_3, tVertex_4 }
```

+ Same as GL_QUADS mode, but each face command draws only one quadrilateral

### Transformation commands

```Lua
{ "translate", { nX, nY, nZ } }
```

#### Rotate

```Lua
{ "rotateX", nAngle }
{ "rotateY", nAngle }
{ "rotateZ", nAngle }
```

+ nAngle: Angle in degrees

#### Scale

```Lua
{ "scale", { nX, nY, nZ } }
```

### Oscillation command

```Lua
{ "oscillate", nType, nPhase, nPeriod }
```

+ nType ( 0 <= nType <= 3 ): Waveform type: 0 sawtooth, 1 sine, 2 square, 3 triangle
+ nPhase ( 0.0 <= nPhase < 1.0 ): Phase
+ nPeriod ( 0.0 < nPeriod ): Wave period in seconds
+ Generated amplitude is -1 to +1
+ This command works only with transformation command or alpha command

#### With Transformation command

```Lua
{ ... , { "oscillate", nType, nPhase, nPeriod }, { "translate", { nX, nY, nZ } }, ... }
-- means { "translate", { nX * oscillate(), nY * oscillate(), nZ * oscillate() } }

{ ... , { "oscillate", nType, nPhase, nPeriod }, { "rotateX", nAngle }, ... }
-- means { "rotateX", nAngle * oscillate() }

{ ... , { "oscillate", nType, nPhase, nPeriod }, { "scale", { nX, nY, nZ } }, ... }
-- means { "scale", { nX * oscillate(), nY * oscillate(), nZ * oscillate() } }
```

#### With Alpha command

```Lua
{ ... , { "oscillate", nType, nPhase, nPeriod }, { "alpha", nAlpha }, ... }
-- means { "alpha", nAlpha * oscillate() + nConstAlpha }
-- nConstAlpha: Alpha value set by Alpha command without Oscillation command
```

## License

- MIT License

----
Copyright © 2017 Iunius118
