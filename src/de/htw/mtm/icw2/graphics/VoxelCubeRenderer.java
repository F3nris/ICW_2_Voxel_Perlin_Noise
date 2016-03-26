package de.htw.mtm.icw2.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import de.htw.mtm.icw2.util.Matrix4f;

import static de.htw.mtm.icw2.util.ShaderReader.readShaderFromFile;

public class VoxelCubeRenderer {
	private int vaoID;
	private int vertexBufferID;
	private int colorBufferID;
	private int vertexShader;
	private int fragmentShader;
	private int shaderProgram;
	
	static final float VERTEX_BUFFER_CUBE [] = {
		    -1.0f,-1.0f,-1.0f,
		    -1.0f,-1.0f, 1.0f,
		    -1.0f, 1.0f, 1.0f,
		    1.0f, 1.0f,-1.0f,
		    -1.0f,-1.0f,-1.0f,
		    -1.0f, 1.0f,-1.0f,
		    1.0f,-1.0f, 1.0f,
		    -1.0f,-1.0f,-1.0f,
		    1.0f,-1.0f,-1.0f,
		    1.0f, 1.0f,-1.0f,
		    1.0f,-1.0f,-1.0f,
		    -1.0f,-1.0f,-1.0f,
		    -1.0f,-1.0f,-1.0f,
		    -1.0f, 1.0f, 1.0f,
		    -1.0f, 1.0f,-1.0f,
		    1.0f,-1.0f, 1.0f,
		    -1.0f,-1.0f, 1.0f,
		    -1.0f,-1.0f,-1.0f,
		    -1.0f, 1.0f, 1.0f,
		    -1.0f,-1.0f, 1.0f,
		    1.0f,-1.0f, 1.0f,
		    1.0f, 1.0f, 1.0f,
		    1.0f,-1.0f,-1.0f,
		    1.0f, 1.0f,-1.0f,
		    1.0f,-1.0f,-1.0f,
		    1.0f, 1.0f, 1.0f,
		    1.0f,-1.0f, 1.0f,
		    1.0f, 1.0f, 1.0f,
		    1.0f, 1.0f,-1.0f,
		    -1.0f, 1.0f,-1.0f,
		    1.0f, 1.0f, 1.0f,
		    -1.0f, 1.0f,-1.0f,
		    -1.0f, 1.0f, 1.0f,
		    1.0f, 1.0f, 1.0f,
		    -1.0f, 1.0f, 1.0f,
		    1.0f,-1.0f, 1.0f
		};
	
	static final float COLOR_BUFFER_CUBE [] = {
	    0.583f,  0.771f,  0.014f,
	    0.609f,  0.115f,  0.436f,
	    0.327f,  0.483f,  0.844f,
	    0.822f,  0.569f,  0.201f,
	    0.435f,  0.602f,  0.223f,
	    0.310f,  0.747f,  0.185f,
	    0.597f,  0.770f,  0.761f,
	    0.559f,  0.436f,  0.730f,
	    0.359f,  0.583f,  0.152f,
	    0.483f,  0.596f,  0.789f,
	    0.559f,  0.861f,  0.639f,
	    0.195f,  0.548f,  0.859f,
	    0.014f,  0.184f,  0.576f,
	    0.771f,  0.328f,  0.970f,
	    0.406f,  0.615f,  0.116f,
	    0.676f,  0.977f,  0.133f,
	    0.971f,  0.572f,  0.833f,
	    0.140f,  0.616f,  0.489f,
	    0.997f,  0.513f,  0.064f,
	    0.945f,  0.719f,  0.592f,
	    0.543f,  0.021f,  0.978f,
	    0.279f,  0.317f,  0.505f,
	    0.167f,  0.620f,  0.077f,
	    0.347f,  0.857f,  0.137f,
	    0.055f,  0.953f,  0.042f,
	    0.714f,  0.505f,  0.345f,
	    0.783f,  0.290f,  0.734f,
	    0.722f,  0.645f,  0.174f,
	    0.302f,  0.455f,  0.848f,
	    0.225f,  0.587f,  0.040f,
	    0.517f,  0.713f,  0.338f,
	    0.053f,  0.959f,  0.120f,
	    0.393f,  0.621f,  0.362f,
	    0.673f,  0.211f,  0.457f,
	    0.820f,  0.883f,  0.371f,
	    0.982f,  0.099f,  0.879f
	};
	
	public VoxelCubeRenderer() {
		generateAndBindVAO();
		generateVertexBuffer();
		generateColorBuffer();
		loadAndCompileShaders();
		setupShaderParameters();
	}

	private void generateAndBindVAO() {
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
	}
	
	private void generateVertexBuffer () {
		// 12 Triangles to draw a cube ==> 12 * 3 = 36 * 3 = 108
		FloatBuffer vertices = BufferUtils.createFloatBuffer(108);
		vertices.put(VERTEX_BUFFER_CUBE);
		vertices.flip();
		
		vertexBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
	}
	
	private void generateColorBuffer () {
		// 12 Triangles to draw a cube ==> 12 * 3 = 36 * 3 = 108
		FloatBuffer colors = BufferUtils.createFloatBuffer(108);
		colors.put(COLOR_BUFFER_CUBE);
		colors.flip();
		
		colorBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
		glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
	}
	
	private void loadAndCompileShaders () {
		String vertexSource = readShaderFromFile(getClass().getResource("./shader/voxel.vert").getPath());
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexSource);
		glCompileShader(vertexShader);
		
		int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetShaderInfoLog(vertexShader));
		}
		
		String fragmentSource = readShaderFromFile(getClass().getResource("./shader/voxel.frag").getPath());
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
		glBindFragDataLocation(shaderProgram, 0, "fragColor");
		glLinkProgram(shaderProgram);
		
		status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
		}
		
		glUseProgram(shaderProgram);
	}
	
	private void setupShaderParameters() {
		int floatSize = 4;

		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
		int posAttrib = glGetAttribLocation(shaderProgram, "position");
		glEnableVertexAttribArray(posAttrib);
		glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 3 * floatSize, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
		int colAttrib = glGetAttribLocation(shaderProgram, "color");
		glEnableVertexAttribArray(colAttrib);
		glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 3 * floatSize, 0);
		
		int uniModel = glGetUniformLocation(shaderProgram, "model");
		Matrix4f model = Matrix4f.scale(0.2f, 0.2f, 0.2f);
		model = model.multiply(Matrix4f.rotate(50f, 40f, 50f, 1f));
		model = model.multiply(Matrix4f.translate(0.5f, 0.5f, 0.5f));
		
		glUniformMatrix4fv(uniModel, false, model.getBuffer());

		int uniView = glGetUniformLocation(shaderProgram, "view");
		Matrix4f view = new Matrix4f();
		glUniformMatrix4fv(uniView, false, view.getBuffer());

		int uniProjection = glGetUniformLocation(shaderProgram, "projection");
		float ratio = 640f / 480f;
		Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
	}
	
	public void delete() {
		 glDeleteVertexArrays(vaoID);
		 glDeleteBuffers(vertexBufferID);
		 glDeleteBuffers(colorBufferID);
		 glDeleteShader(vertexShader);
		 glDeleteShader(fragmentShader);
		 glDeleteProgram(shaderProgram);
	}
}
