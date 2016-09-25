#version 330 core

in vec3 vUV;
in vec3 vertexColor;
in mat4 mv;

out vec4 fragColor;

uniform sampler3D voxels;

const int MAX_SAMPLES = 500;
const float stepSize = 0.004;

void main() {

	// Nullt alles außer der 3. Spalte
	vec3 camPos = vec3(inverse(mv)*vec4(0,0,0,1));

	vec3 dataPos = vUV;
	vec3 geomDir = normalize((vUV - vec3(0.5)) - camPos);
	vec3 dirStep = geomDir * stepSize;
	
	float alpha = 0.0;
	
	
	fragColor = vec4(0,0,0,0);
	
	bool stop = false;
	
	for (int i=0; i<MAX_SAMPLES; i++) {
		dataPos = dataPos + dirStep;
		
		if (dataPos.x >= 1 || dataPos.y >= 1 || dataPos.z >= 1 || dataPos.x < 0 || dataPos.y < 0 || dataPos.z < 0) {
		//if(dot(sign(dataPos - texMin), sign(texMax-dataPos)) < 3.0) {
			break;
		}
		
		vec4 texColor = texture(voxels, dataPos);
		if (texColor.a != 0) {
			fragColor = texColor;
		}
		
		//int pos = (x * n * n) + (y * n) + z;
		
		//if (voxels[pos] != 0) {
		//	alpha = 1;
		//	break;
		//}
	}
	
	//fragColor = vec4(vUV,1);
	//fragColor = vec4(vUV,alpha);
}

