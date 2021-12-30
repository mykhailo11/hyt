#version 330

uniform float size = 0.05f;
uniform vec2 window;
uniform float state;
uniform vec4 position;

in float current;

out vec4 outColor;

void main()
{
    switch(int(current)){
        case 7:
        case 0:
            gl_Position = position + vec4(0.0f, size, 0.5f, 1.0f);
        break;
        case 5:
        case 1:
            gl_Position = position + vec4(-size, 0.0f, 0.5f, 1.0f);
        break;
        case 9:
        case 2:
            gl_Position = position + vec4(size, 0.0f, 0.5f, 1.0f);
        break;
        case 3:
            gl_Position = position + vec4(0.0f, -size, 0.5f, 1.0f);
        break;
        case 11:
        case 4:
            gl_Position = position + vec4(0.0f, -size, 0.0f, 1.0f);
        break;
        case 6:
            gl_Position = position + vec4(-size, 0.0f, 0.0f, 1.0f);
        break;
        case 8:
            gl_Position = position + vec4(0.0f, size, 0.0f, 1.0f);
        break;
        case 10:
            gl_Position = position + vec4(size, 0.0f, 0.0f, 1.0f);
    }
    outColor = vec4(1.0f * position.x, 1.0f * position.y , 1.0f, 1.0f);
}