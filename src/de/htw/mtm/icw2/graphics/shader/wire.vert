#version 330 core

layout (location=0) in vec3 position;

out vec3 vertexColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vertexColor = vec3(1.0, 1.0, 1.0);
    
    gl_Position =  projection * view * model * vec4(position.xyz, 1.0);
}