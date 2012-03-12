uniform sampler2D sceneTex; // 0
uniform vec2 center; // Mouse position
uniform float time; // effect elapsed time
uniform vec3 shockParams; // 10.0, 0.8, 0.1

void main()
{
  vec2 uv = gl_TexCoord[0].xy;
  vec2 texCoord = uv;

  float distance = distance(uv, center.xy);
  if ( (distance <= (time + shockParams.z)) &&
       (distance >= (time - shockParams.z)) )
  {
    float diff = (distance - time);
    float powDiff = 1.0 - pow(abs(diff*shockParams.x),
                                shockParams.y);
    float diffTime = diff  * powDiff;
    vec2 diffUV = normalize(uv - center.xy);
    texCoord = uv + (diffUV * diffTime);
    
  }
  gl_FragColor = texture2D(sceneTex, texCoord);
}