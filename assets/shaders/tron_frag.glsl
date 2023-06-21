#version 330 core


uniform sampler2D texEmit;
uniform sampler2D texDiff;
uniform sampler2D texSpec;

uniform vec3 lightColorPoint;
uniform vec3 lightColorSpot;
uniform float shininess;

uniform float innerAngle;
uniform float outerAngle;

uniform vec3 directionSpot;

uniform float constant;
uniform float linear;
uniform float quadratic;

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
    vec3 lightDirPoint;
} vertexData;

//fragment shader output
out vec4 color;

vec3 brdf (vec4 matDiff, vec4 matSpec, vec3 N, vec3 L, vec3 V, float K) {
    vec3 R = reflect(-L,N);
    vec3 C = max(0.0, dot(N,L)) * matDiff.xyz;
    vec3 D = pow(max(0.0, dot(R,V)), K) * matSpec.xyz;
    return C+D;
}

void emit (vec4 matEmit) {
    color.xyz += matEmit.xyz * staticColor;
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
    vec3 LPoint = normalize(vertexData.lightDirPoint);
    vec3 V = normalize(vertexData.ViewDir);
    vec3 R = normalize(reflect(-LPoint,N));

    //attenuation
    float distanceSpot = length(vertexData.lightDirSpot);
    float distancePoint = length(vertexData.lightDirPoint);
    float attenuationSpot = 1.0 / (constant + linear * distanceSpot + quadratic * (distanceSpot * distanceSpot));
    float attenuationPoint = 1.0 / (constant + linear * distancePoint + quadratic * (distancePoint * distancePoint));

    //pointLight
    color.xyz += brdf(matDiff, matSpec, N, LPoint, V, shininess) * lightColorPoint * attenuationPoint;
    emit(matEmit);

    //spotLight
    float theta = dot(normalize(vertexData.lightDirSpot), normalize(directionSpot));
    float epsilon = innerAngle - outerAngle;
    float intensity = clamp((theta - outerAngle) / epsilon, 0.0, 1.0);

    color.xyz += brdf(matDiff, matSpec, N, LSpot, V, shininess) * lightColorSpot * intensity * attenuationSpot;


    //gamma
    matDiff.xyz = gamma(matDiff.xyz);
    matSpec.xyz = gamma(matSpec.xyz);
    matEmit.xyz = gamma(matEmit.xyz);

}

