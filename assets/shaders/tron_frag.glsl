#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 viewSpaceNormal;
} vertexData;

//fragment shader output
out vec4 color;

void main(){
    // TODO This is currently only a color passthrough to the screen. For tasks 2.1.3 and 2.4.2 you have to implement a
    //      meaningful visualization of the surface normals. Think about how to map the coordinates of the normal vectors
    //      to RGB values and how you're going to handle negative coordinates.
    //vec4 objectSpaceNorm = vec4( normal, 0.0);
    float r = sqrt(pow(vertexData.viewSpaceNormal.x,2.0));
    float g = sqrt(pow(vertexData.viewSpaceNormal.y,2.0));
    float b = sqrt(pow(vertexData.viewSpaceNormal.z,2.0));

    color = vec4(r,g,b, 1.0f);
}