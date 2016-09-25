package de.htw.mtm.icw2.graphics.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class WireframeRenderer extends AbstractRenderer{
	
	public WireframeRenderer () {
		super("../shader/wire.vert", "../shader/wire.frag");
	}
	
	public void render() {
		glUseProgram(shaderProgram);
		glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
		glDrawArrays(GL_TRIANGLES, 0, 108);
	}
}
