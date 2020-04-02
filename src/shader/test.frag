#version 400 core

in vec2 texCoord;
in vec3 normal;
in vec3 fragPos;
in vec4 viewSpace;

uniform sampler2D texUnit;

//light properties
uniform vec3 pointLightPosArr[2];
uniform vec3 pointLightColorArr[2];
uniform float Kc;
uniform float Kl;
uniform float Kq;

uniform vec3 dirLightDirArr[2];
uniform vec3 dirLightColorArr[2];

//material properties
uniform vec3 Ka;
uniform vec3 Kd;
uniform vec3 Ks;
uniform float specularExponent;

uniform vec3 viewPos;

out vec4 outColor;

void main()
{
    vec3 ambientLight = vec3(0,0,0);
    vec3 diffuseLight = vec3(0,0,0);
    vec3 specularLight = vec3(0,0,0);
    
    //======================================================
    //Point lights
    for(int i = 0; i < pointLightPosArr.length(); i++)
    {
        float distance = length(pointLightPosArr[i] - fragPos);
        float attenuation = 1 / (Kc + Kl * distance + Kq * (distance * distance));

        //ambient light
        ambientLight += attenuation * Ka * pointLightColorArr[i];

    	//diffuse lighting
        vec3 lightDir = normalize(pointLightPosArr[i] - fragPos);
        
        float diff = max(0.0, dot(normalize(normal), lightDir));
        diffuseLight += attenuation * Kd * diff * pointLightColorArr[i];

        //specular lighting
        vec3 viewDir = normalize(viewPos - fragPos);
        vec3 reflectDir = reflect(-lightDir, normalize(normal));

        float spec = pow(max(0.0, dot(viewDir, reflectDir)), specularExponent);
        specularLight += attenuation * Ks * spec * pointLightColorArr[i];
    }

    //===================================================
    //Directional lights
    for(int i = 0; i < dirLightDirArr.length(); i++)
    {
        vec3 lightDir = dirLightDirArr[i];
        
        //diffuse lighting
        float diff = max(0.0, dot(normalize(normal), lightDir));
        diffuseLight += Kd * diff * dirLightColorArr[i];
    }
    
    // borde vara en shader för modeller med textur och en för modeller utan
    //vec3 lightColor = (ambientLight + diffuseLight + specularLight) * vec3(texture(texUnit, texCoord));
    vec3 lightColor = (ambientLight + diffuseLight + specularLight);
 
    //=====================================================
    //compute distance used in fog equations
    float dist = abs(viewSpace.z);
    
    //linear fog
    //if you change the start and stop points, dont forget to change in the other shaders as well!.
    float fogStart = 300; 
    float fogEnd = 800;
    float fogFactor = (fogEnd - dist)/(fogEnd - fogStart);
    fogFactor = clamp( fogFactor, 0.0, 1.0 );
    vec3 fogColor = vec3(91.0/255.0, 142/255.0, 194.0/255.0);
 
    vec3 finalColor = mix(fogColor, lightColor, fogFactor);

    outColor = vec4(finalColor, 1);
}
