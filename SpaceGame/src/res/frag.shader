uniform sampler2D colorMap;
uniform sampler2D noiseMap;
uniform float timer;


//http://www.ozone3d.net/tutorials/glsl_texturing_p06.php
void main (void)
{
	vec3 noiseVec;
	vec2 displacement;
	float scaledTimer;

	displacement = gl_TexCoord[0].st;

	scaledTimer = timer*0.0001;

	displacement.x += scaledTimer;
	displacement.y -= scaledTimer;

	noiseVec = normalize(texture2D(noiseMap, displacement.xy).xyz);
	noiseVec = (noiseVec * 2.0 - 1.0) * 0.035;
	
	vec4 color = texture2D(colorMap, gl_TexCoord[0].st + noiseVec.xy);
	gl_FragColor = color;
}