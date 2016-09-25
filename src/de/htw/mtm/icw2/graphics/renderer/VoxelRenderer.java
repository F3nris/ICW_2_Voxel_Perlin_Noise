package de.htw.mtm.icw2.graphics.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import de.htw.mtm.icw2.util.TextureUnit;
import de.htw.mtm.icw2.util.TextureUnitManager;

public class VoxelRenderer extends AbstractRenderer {
	private ByteBuffer texels;
	
	private TextureUnitManager tuManager;
	private int texID;
	private TextureUnit texUnit;

	public VoxelRenderer() {
		super("../shader/voxel.vert", "../shader/voxel.frag");
		tuManager = TextureUnitManager.getTextureUnitManager();
	}
	
	public void generateVolumeTexture(ByteBuffer texelBuffer, int dim) {
		texels = texelBuffer;
		
		prepareTexture3d(dim, dim, dim, texels);
		
		int loc = glGetUniformLocation(shaderProgram, "voxels");
	    glUniform1i(loc, texUnit.getId());
	}
	
	private void prepareTexture3d(final int width, final int height, final int depth, final ByteBuffer texels) {
		texUnit = tuManager.getNextFreeTextureUnit();
		texID = glGenTextures();

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
	}

	@Override
	public void render() {
		glActiveTexture(texUnit.getValue());
		glBindTexture(GL_TEXTURE_3D, texID);
		
		glUseProgram(shaderProgram);
		glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
		
		glDrawArrays(GL_TRIANGLES, 0, 108);
	}

	public void deleteTexture() {
		glDeleteTextures(texID);
		tuManager.freeTextureUnitById(texUnit.getId());
	}

}
