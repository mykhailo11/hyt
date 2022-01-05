#version 330

uniform float size = 0.1f;
uniform vec2 window;
uniform float state;
uniform vec4 position;

in vec4 outColor;

out vec4 color;

flat in vec4 staticPosition;
in vec4 varyingPosition;

float operateAxis(float staticValue, float varyingValue){
    return abs(staticValue) - abs(varyingValue);
}



void main()
{
    vec4 fposition = gl_FragCoord;
    vec4 distance = varyingPosition - staticPosition;
    float distanceLength = length(distance);
    float xContribution = operateAxis(staticPosition.x, varyingPosition.x);
    float yContribution = operateAxis(staticPosition.y, varyingPosition.y);
    float zContribution = operateAxis(staticPosition.z, varyingPosition.z);
    float contribution = length(vec4(xContribution, yContribution, zContribution, 1.0f));
    //vec3 baseColor = (vec3(outColor.rgb) * (1.0f - fposition.z)) * 2.0f / contribution - cos(zContribution) * 0.6f;
    vec3 baseColor = vec3(-0.3f + zContribution, (0.4f - (position.x + 1.0f) / 2.0f) - zContribution, 1.0f - cos(pow((length(vec2(xContribution, yContribution))) * (1.0f - zContribution) / size, zContribution))) + abs(varyingPosition.z);
    color = vec4(baseColor, abs(varyingPosition.z));
}