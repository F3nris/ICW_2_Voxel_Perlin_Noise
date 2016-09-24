#version 330 core

in vec3 vUV;
in vec3 vertexColor;
in mat4 mv;

out vec4 fragColor;

const int n = 20;
//uniform int voxels[n*n*n];
uniform sampler3D voxels;

const int MAX_SAMPLES = 1000;
const float stepSize = 0.002;

void main() {

	// Nullt alles außer der 3. Spalte
	vec3 camPos = vec3(inverse(mv)*vec4(0,0,0,1));

	vec3 dataPos = vUV;
	vec3 geomDir = normalize((vUV - vec3(0.5)) - camPos);
	vec3 dirStep = geomDir * stepSize;
	
	float alpha = 0.0;
	
	
	fragColor = vec4(0,0,0,0);
	
	for (int i=0; i<MAX_SAMPLES; i++) {
		dataPos = dataPos + dirStep;
		
		int x = int(floor(dataPos.x * n));
		int y = int(floor(dataPos.y * n));
		int z = int(floor(dataPos.z * n));
		
		if (x >= n || y >= n || z >= n || x < 0 || y < 0 || z < 0) {
			alpha = 0;
			break;
		}
		
		vec4 texColor = texture(voxels, dataPos);
		if (texColor.a > 0.5) {
			fragColor = texColor;
		}
		
		int pos = (x * n * n) + (y * n) + z;
		
		//if (voxels[pos] != 0) {
		//	alpha = 1;
		//	break;
		//}
	}
	
	//fragColor = vec4(vUV,1);
	//fragColor = vec4(vUV,alpha);
}

