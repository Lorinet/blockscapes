#version 400 core
#define HURTING 1
#define UNDERWATER 2

//in vec2 pass_textureCoords;

out vec4 out_Color;
uniform int overlayState;

void main() {
  //  out_Color = vec4(1, 1, 1, 1);
  vec4 col = vec4(0, 0, 0, 0);
  switch (overlayState) {
    case HURTING:
      col = vec4(1.0, 0.5, 0.5, 0.4);
    break;
    case UNDERWATER:
      col = vec4(0.07, 0.14, 0.24, 0.5);
    break;
  }
  out_Color = col;
}