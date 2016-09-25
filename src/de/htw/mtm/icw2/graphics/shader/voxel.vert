#version 330 core

layout (location=0) in vec3 position;

smooth out vec3 vUV;
out mat4 mv; 

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    //vertexColor = color;
    mv = view * model;
    mat4 mvp = projection * mv;// * vec4(position.xyz, 1);
    vUV = position + vec3(0.5);
    
    gl_Position = mvp * vec4(position.xyz, 1.0);
}