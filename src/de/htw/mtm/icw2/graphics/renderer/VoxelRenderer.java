package de.htw.mtm.icw2.graphics.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;

import de.htw.mtm.icw2.util.Vector3f;

public class VoxelRenderer extends AbstractRenderer {
	private ByteBuffer texels;
	private int texID;

	public VoxelRenderer() {
		super("../shader/voxel.vert", "../shader/voxel.frag");
	}
	
	public void generateVolumeTexture() {
		int n = 100;
		texels = BufferUtils.createByteBuffer(n*n*n*4);
		
		byte b = (byte) (255 & 0xFF);
		byte c = 0;
		
		for (int z = 0; z < n; z++) {
			for (int y = 0; y < n; y++) {
				for (int x = 0; x < n; x++) {
					
					Vector3f v = new Vector3f(x,y,z);
					v = v.add(new Vector3f(n/-2, n/-2, n/-2));
					v = v.divide(n/-2);
					float t = 1.f / (float) n;
					v = v.add(new Vector3f(-t, -t, -t));
					
					byte red = (byte)((int)(255.f / n * y) & 0xFF);
					texels.put(red); texels.put(c); texels.put(c);
					if ((v.x * v.x) + (v.y * v.y) + (v.z * v.z) <= 1) {
						 texels.put(b);
					} else {
						texels.put(c);
					}
				}
			}
		}
		texels.rewind();
		
		texID = prepareTexture3d(n, n, n, texels);
		
		int loc = glGetUniformLocation(shaderProgram, "voxels");
	    glUniform1i(loc, 0);
	}
	
	private int prepareTexture3d(final int width, final int height, final int depth, final ByteBuffer texels) {
		int texID = glGenTextures();

		glEnable(GL_TEXTURE_3D);
		glBindTexture(GL_TEXTURE_3D, texID);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_CLAMP);
		glTexImage3D(GL_TEXTURE_3D, 0, GL_RGBA, width, height, depth, 0, GL_RGBA, GL_UNSIGNED_BYTE, texels);
		
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_BASE_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAX_LEVEL, 4);
				
		//generate mipmaps
		glGenerateMipmap(GL_TEXTURE_3D);

		return texID;
	}

	@Override
	public void render() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_3D, texID);
		
		glUseProgram(shaderProgram);
		glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
		
		glDrawArrays(GL_TRIANGLES, 0, 108);
	}

	public void deleteTexture() {
		glDeleteTextures(texID);
	}

}
