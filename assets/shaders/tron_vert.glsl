#version 330 core

// todo 2.1.2
layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;

// --- uniforms
// object to world (the default value will be used unless you upload some other matrix using glUniformMatrix4fv)
uniform mat4 model_matrix = mat4(1.0, 0.0, 0.0, 0.0,
                                 0.0, 1.0, 0.0, 0.0,
                                 0.0, 0.0, 1.0, 0.0,
                                 0.0, 0.0, 0.0, 1.0);
// world to camera (2.4.2)
// uniform mat4 view_matrix;
// camera to clipping (2.4.2)
// uniform mat4 proj_matrix;

// Hint: Packing your data passed to the fragment shader into a struct like this helps to keep the code readable!
out struct VertexData
{
    vec3 color;
} vertexData;

void main(){
    // This code should output something similar to Figure 1 in the exercise sheet.
    // TODO Modify this to solve the remaining tasks (2.1.2 and 2.4.2).
    // Change to homogeneous coordinates
    vec4 objectSpacePos = vec4(position, 1.0);
    // Calculate world space position by applying the model matrix
    vec4 worldSpacePos = model_matrix * objectSpacePos;

    // Write result to gl_Position
    // Note: z-coordinate must be flipped to get valid NDC coordinates. This will later be hidden in the projection matrix.
    gl_Position = worldSpacePos * vec4(1.0, 1.0, -1.0, 1.0);
    // Green color with some variation due to z coordinate
    vec4 objectSpaceNorm = vec4( normal, 0.0);
    vec4 worldSpaceNorm = model_matrix * objectSpaceNorm;
    float r = sqrt(pow(worldSpaceNorm.x,2.0));
    float g = sqrt(pow(worldSpaceNorm.y,2.0));
    float b = sqrt(pow(worldSpaceNorm.z,2.0));
    vertexData.color = vec3(r,g,b);
}
