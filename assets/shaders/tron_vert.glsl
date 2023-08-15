#version 330 core


layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoords;
layout(location = 2) in vec3 normal;

// --- uniforms
// object to world (the default value will be used unless you upload some other matrix using glUniformMatrix4fv)
uniform mat4 model_matrix = mat4(1.0, 0.0, 0.0, 0.0,
                                 0.0, 1.0, 0.0, 0.0,
                                 0.0, 0.0, 1.0, 0.0,
                                 0.0, 0.0, 0.0, 1.0);
// world to camera (2.4.2)
uniform mat4 view_matrix;
// camera to clipping (2.4.2)
uniform mat4 proj_matrix;

#define MAX_LIGHTS 5

uniform vec2 tcMultiplier;

uniform vec3 positionPoint[MAX_LIGHTS];

uniform vec3 directionSpot;
uniform vec3 positionSpot;
uniform vec3 lightColorSpot;
uniform float innerAngle;
uniform float outerAngle;

// Hint: Packing your data passed to the fragment shader into a struct like this helps to keep the code readable!
out struct VertexData
{
    vec3 viewSpaceNormal;
    vec3 Normal;
    vec3 ViewDir;
    vec2 tc;
    vec3 lightDirSpot;
    vec3 lightDirPoint[MAX_LIGHTS];
} vertexData;


void main(){
    // This code should output something similar to Figure 1 in the exercise sheet.

    // Change to homogeneous coordinates
    vec4 objectSpacePos = vec4(position, 1.0);
    // Calculate world space position by applying the model matrix
    vec4 worldSpacePos = model_matrix * objectSpacePos;
    // Write result to gl_Position
    // Note: z-coordinate must be flipped to get valid NDC coordinates. This will later be hidden in the projection matrix.
    gl_Position = proj_matrix * view_matrix * worldSpacePos;
    // Green color with some variation due to z coordinate
    //vertexData.color = vec3(0.0, worldSpacePos.z + 0.5, 0.0);
    vec4 objectSpaceNorm = vec4( normal, 0.0);
    vertexData.viewSpaceNormal = vec3(view_matrix * model_matrix * objectSpaceNorm);

    vertexData.tc = texCoords*tcMultiplier;
    // compute normal in camera space //
    mat4 normalMat = transpose(inverse(view_matrix * model_matrix));
    vertexData.Normal = (normalMat * objectSpaceNorm).xyz;

    // compute light direction in camera space //
    vec4 P = view_matrix * worldSpacePos;
    for(int i = 0; i < MAX_LIGHTS; i++ ){
        vec4 lp = view_matrix * vec4(positionPoint[i], 1.0);
        vertexData.lightDirPoint[i] = (lp - P).xyz;
    }

    // compute light direction in camera space for Spotlight //
    vertexData.lightDirSpot = positionSpot - P.xyz;

    // specular term //
    vertexData.ViewDir = -P.xyz;

}