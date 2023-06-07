#version 330 core


uniform sampler2D texEmit;



//input from vertex shader
in struct VertexData
{
    vec3 viewSpaceNormal;
    vec2 tc;
} vertexData;

//fragment shader output
out vec4 color;

void main(){

//    float r = sqrt(pow(vertexData.viewSpaceNormal.x,2.0));
//    float g = sqrt(pow(vertexData.viewSpaceNormal.y,2.0));
//    float b = sqrt(pow(vertexData.viewSpaceNormal.z,2.0));

//    float l = abs(vertexData.viewSpaceNormal.z);



    color = texture(texEmit, vertexData.tc);
    //color = vec4(r, g, b, 1.0f);
    //color = vec4(1.0f, 1.0f, 1.0f, 1.0f);
}