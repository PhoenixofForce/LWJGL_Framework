## LWJGL Engine
This project serves as a codebase for my games
### Todo
#### Must Haves
* Entity Component System
  * Want to look into an InputProvider System for controls
  * Collisions
* Gui
  * Make Basic Elements (Buttons, slider, checkboxes)
* Text Rendering
  * Colored Text
  * Text Effects (wobble, ...)
  * Sprites
  * TypeWriter Effect
  * Intruduce Text format ex: `[color: #FF0000, effects: [wobble]] Hello` for a wobbly red Hello
* Particles
  * Animated Particles
  * Intruduce functions to change the values
  * Death Emitter
  * Find faster way to create float[] for instanced vbo => more particles 
  * Fix DirectionChange (it is not supposed to be an addiion, more like an rotation in the given direction) 

#### Will probably do
* MapLoader for maps from the [Level Editor](https://github.com/PhoenixofForce/Level_Editor)
  * Script language, which is also used in many of my other games
* Lights

#### Have to think about
* (Maybe have RenderStates, that set BlendFunc, DepthTest and so on)
* (Audio)
* Load obj with mtl
* 3d-Animation
* System to have multiple Views (Main Menu, Options Menu, Game)
* State Machine (for Animations, ...)  
* i18n
* Dialogue System

### Notes
* Games wont work on macOS (because of OpenGL version)

### Required Libraries
* [LWJGL 3.3.0 (LWJGL, GLFW, OpenGL)](https://www.lwjgl.org/download)
* [JOML 1.10.3](https://github.com/JOML-CI/JOML)

### Sources
* [Pawn](https://opengameart.org/content/chess-pawn)
* [Skybox from Zachery “skiingpenguins” Slocum, raw and edited](https://opengameart.org/content/skiingpenguins-skybox-pack)
* [Grass Texture](https://kenney.nl/assets/voxel-pack)
* [glsl-conditionals](https://github.com/dmnsgn/glsl-conditionals)
* [glsl-blend](https://github.com/jamieowen/glsl-blend)
