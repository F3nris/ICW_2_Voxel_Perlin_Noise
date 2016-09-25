package de.htw.mtm.icw2.graphics.renderer;

import static de.htw.mtm.icw2.util.ShaderReader.readShaderFromFile;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import de.htw.mtm.icw2.util.Matrix4f;

public abstract class AbstractRenderer {
	protected int shaderProgram;
	protected int vertexShader;
	protected int fragmentShader;
	
	public AbstractRenderer(String vertexFilePath, String fragmentFilePath) {
		loadAndCompileShaders(vertexFilePath, fragmentFilePath);
	}
	
	public void loadAndCompileShaders(String vertexFilePath, String fragmentFilePath) {
		String vertexSource = readShaderFromFile(getClass().getResource(vertexFilePath).getPath());
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexSource);
		glCompileShader(vertexShader);
		
		int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetShaderInfoLog(vertexShader));
		}
		
		String fragmentSource = readShaderFromFile(getClass().getResource(fragmentFilePath).getPath());
		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentSource);
		glCompileShader(fragmentShader);
		
		status = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetShaderInfoLog(fragmentShader));
		}
		
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		glLinkProgram(shaderProgram);
		
		status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
		}
	}
	
	public void setupBasicShaderParameters(Matrix4f model, Matrix4f view, Matrix4f projection){
		int floatSize = 4;
		int posAttrib = glGetAttribLocation(shaderProgram, "position");
		glEnableVertexAttribArray(posAttrib);
		glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 3 * floatSize, 0);
		
		glUseProgram(shaderProgram);
		int uniModel = glGetUniformLocation(shaderProgram, "model");
		glUniformMatrix4fv(uniModel, false, model.getBuffer());

		int uniView = glGetUniformLocation(shaderProgram, "view");
		glUniformMatrix4fv(uniView, false, view.getBuffer());

		int uniProjection = glGetUniformLocation(shaderProgram, "projection");
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
	}
	
	public void updateUniModel(Matrix4f model) {
		glUseProgram(shaderProgram);
		int uniModel = glGetUniformLocation(shaderProgram, "model");
		Matrix4f scaledModel = Matrix4f.scale(1.01f, 1.01f, 1.01f).multiply(model);
		glUniformMatrix4fv(uniModel, false, scaledModel.getBuffer());
	}
	
	public void updateUniView(Matrix4f view) {
		glUseProgram(shaderProgram);
		int uniView = glGetUniformLocation(shaderProgram, "view");
		glUniformMatrix4fv(uniView, false, view.getBuffer());
	}
	
	public void updateUniProjection(Matrix4f projection) {
		glUseProgram(shaderProgram);
		int uniProjection = glGetUniformLocation(shaderProgram, "projection");
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
	}
	
	public abstract void render();
	
	public void delete () {
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		glDeleteProgram(shaderProgram);
	}
}
