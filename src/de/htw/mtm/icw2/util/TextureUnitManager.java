package de.htw.mtm.icw2.util;

import static org.lwjgl.opengl.GL13.*;

import java.util.Arrays;

public class TextureUnitManager {
	private static TextureUnitManager instance;
	
	private final int units[] = {
		 GL_TEXTURE0,  GL_TEXTURE1,  GL_TEXTURE2,  GL_TEXTURE3,  GL_TEXTURE4,
		 GL_TEXTURE5,  GL_TEXTURE6,  GL_TEXTURE7,  GL_TEXTURE8,  GL_TEXTURE9,
		GL_TEXTURE10, GL_TEXTURE11, GL_TEXTURE12, GL_TEXTURE13, GL_TEXTURE14, 
		GL_TEXTURE15, GL_TEXTURE16, GL_TEXTURE17, GL_TEXTURE18, GL_TEXTURE19
	};
	
	private boolean unitIsInUse[];
	
	private TextureUnitManager() {
		unitIsInUse = new boolean[units.length];
		Arrays.fill(unitIsInUse, false);
	}
	
	public static TextureUnitManager getTextureUnitManager() {
		if (instance == null) {
			instance = new TextureUnitManager();
		}
		return instance;
	}
	
	public TextureUnit getNextFreeTextureUnit() {
		TextureUnit result = null;
		
		int length = units.length;
		for (int i=0; i<length; i++) {
			if (!unitIsInUse[i]) {
				result = new TextureUnit(i, units[i]);
				unitIsInUse[i] = true;
				break;
			}
		}
		
		if (result == null) {
			throw new RuntimeException("No texture unit free! Couldn't initialize texture!");
		}
		
		return result;
	}
	
	public void freeTextureUnitById (int id) {
		unitIsInUse[id] = false;	
	}
}
