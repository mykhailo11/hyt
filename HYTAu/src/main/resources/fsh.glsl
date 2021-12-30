#version 330

in vec4 outColor;

out vec4 color;

void main()
{
    vec4 position = gl_FragCoord;
    color = outColor;
}