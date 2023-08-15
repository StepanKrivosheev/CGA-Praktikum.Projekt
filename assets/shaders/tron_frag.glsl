#version 330 core


uniform sampler2D texEmit;
uniform sampler2D texDiff;
uniform sampler2D texSpec;


#define MAX_LIGHTS 5

uniform vec3 lightColorPoint[MAX_LIGHTS];
uniform vec3 lightColorSpot;
uniform float shininess;

uniform float innerAngle;
uniform float outerAngle;

uniform vec3 directionSpot;

uniform float gammaValue = 2.2f;

uniform vec3 staticColor;


//input from vertex shader
in struct VertexData
{
    vec3 viewSpaceNormal;
    vec3 Normal;
    vec3 ViewDir;
    vec2 tc;
    vec3 lightDirSpot;
    vec3 lightDirPoint[MAX_LIGHTS];

} vertexData;

//fragment shader output
out vec4 color;

vec3 brdf (vec4 matDiff, vec4 matSpec, vec3 N, vec3 L, vec3 V, float K) {
    vec3 R = normalize(L + V);
    vec3 C = max(0.0, dot(N,L)) * matDiff.xyz;
    vec3 D = pow(max(dot(N,R), 0.0), K) * matSpec.xyz;
    return C+D;
}


vec3 gamma(vec3 C_linear) {
    return vec3(pow(C_linear.x, 1/gammaValue), pow(C_linear.y, 1/gammaValue), pow(C_linear.z, 1/gammaValue));
}vec3;


vec3 invgamma(vec3 C_gamma) {
    return vec3(pow(C_gamma.x, gammaValue), pow(C_gamma.y, gammaValue), pow(C_gamma.z, gammaValue));
} vec3 ;


void main(){

    vec4 matDiff = texture(texDiff, vertexData.tc);
    vec4 matSpec = texture(texSpec, vertexData.tc);
    vec4 matEmit = texture(texEmit, vertexData.tc);

    matDiff.xyz = invgamma(matDiff.xyz);
    matSpec.xyz = invgamma(matSpec.xyz);
    matEmit.xyz = invgamma(matEmit.xyz);


    color = vec4(0f, 0f, 0f, 1f);

    // normalize everything necessary //
    vec3 N = normalize(vertexData.Normal);
    vec3 LSpot = normalize(vertexData.lightDirSpot);
    vec3 V = normalize(vertexData.ViewDir);


    // pointLight //
    for (int i = 0; i < MAX_LIGHTS; i++ ){
        float distancePoint = length(vertexData.lightDirPoint[i]);
        float attenuationPoint = 1.0 / (distancePoint * distancePoint);

        vec3 LPoint = normalize(vertexData.lightDirPoint[i]);
        color.xyz += brdf(matDiff, matSpec, N, LPoint, V, shininess) * lightColorPoint[i] * attenuationPoint;
    }

    color.xyz += (matEmit.xyz * staticColor);

    // spotLight //
    float distanceSpot = length(vertexData.lightDirSpot);
    float attenuationSpot = 1.0 / (distanceSpot * distanceSpot);

    float theta = dot(normalize(vertexData.lightDirSpot), normalize(directionSpot));
    float epsilon = innerAngle - outerAngle;
    float intensity = clamp((theta - outerAngle) / epsilon, 0.0, 1.0);

    color.xyz += brdf(matDiff, matSpec, N, LSpot, V, shininess) * lightColorSpot * intensity * attenuationSpot;

    // gamma //
    color.xyz = gamma(color.xyz);

}

