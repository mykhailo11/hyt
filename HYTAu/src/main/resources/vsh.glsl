#version 330

uniform float size = 0.1f;
uniform vec2 window;
uniform float state;
uniform vec4 position;

out vec4 outColor;
out vec4 varyingPosition;
flat out vec4 staticPosition;

mat4 getTranslation(float x, float y, float z){
    return mat4(
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
           x,    y,    z, 1.0f
    );
}

mat4 getRotationX(float angle){
    float c = cos(radians(angle));
    float s = sin(radians(angle));
    return mat4(
    1.0f, 0.0f, 0.0f, 0.0f,
    0.0f,    c,   -s, 0.0f,
    0.0f,    s,    c, 0.0f,
    0.0f, 0.0f, 0.0f, 1.0f
    );
}

mat4 getRotationZ(float angle){
    float c = cos(radians(angle));
    float s = sin(radians(angle));
    return mat4(
       c,   -s, 0.0f, 0.0f,
       s,    c, 0.0f, 0.0f,
    0.0f, 0.0f, 1.0f, 0.0f,
    0.0f, 0.0f, 0.0f, 1.0f
    );
}

mat4 getRotationY(float angle){
    float c = cos(radians(angle));
    float s = sin(radians(angle));
    return mat4(
       c, 0.0f,    s, 0.0f,
    0.0f, 1.0f, 0.0f, 0.0f,
      -s, 0.0f,    c, 0.0f,
    0.0f, 0.0f, 0.0f, 1.0f
    );
}

void main()
{
    vec4 vertex;
    float far = (1.0f / (abs(position.y) + 1.0f));
    switch(int(gl_VertexID)){
        case 0:
        vertex = vec4(size, size * far, -state, 1.0f);
        break;
        case 1:
        vertex = vec4(-size, size * far, -state, 1.0f);
        break;
        case 2:
        vertex = vec4(size, size, 0.0f, 1.0f);
        break;
        case 3:
        vertex = vec4(-size, size, 0.0f, 1.0f);
        break;
        case 4:
        vertex = vec4(size, -size * far, -state, 1.0f);
        break;
        case 5:
        vertex = vec4(-size, -size * far, -state, 1.0f);
        break;
        case 6:
        vertex = vec4(size, -size, 0.0f, 1.0f);
        break;
        case 7:
        vertex = vec4(-size, -size, 0.0f, 1.0f);
        break;
    }
    mat4 plateRotation =  getRotationY(-10.0f) * getRotationX(-30.0f) * getRotationX(position.y * 30.0f + 35.0f);
    staticPosition = vertex;
    varyingPosition = vertex;
    gl_Position = plateRotation * (position + vertex + vec4(0.0f, (0.5f + position.y) / 1.0f, 0.0f, 0.0f)) + vec4(0.3f, -0.3f, 1.0f, 0.0f);
    outColor = vec4((position.z + vertex.z + 1.0f) / 2.0f, (position.x + vertex.x + 1.0f) / 2.0f, 1.0f, 1.0f) + state;
    //outColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);
}