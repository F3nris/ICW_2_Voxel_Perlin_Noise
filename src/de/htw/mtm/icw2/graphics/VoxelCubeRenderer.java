package de.htw.mtm.icw2.graphics;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import static de.htw.mtm.icw2.util.ShaderReader.readShaderFromFile;

public class VoxelCubeRenderer {
	private int vaoID;
	private int vboID;
	private int vertexShader;
	private int fragmentShader;
	
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
	
	public VoxelCubeRenderer() {
		generateAndBindVAO();
		generateVertexBuffer();
		loadAndCompileShaders();
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
		
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
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
	}
	
	public void delete() {
		 glDeleteVertexArrays(vaoID);
		 glDeleteBuffers(vboID);
	}
}
