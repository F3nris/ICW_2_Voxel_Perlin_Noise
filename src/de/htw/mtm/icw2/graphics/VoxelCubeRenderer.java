package de.htw.mtm.icw2.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import de.htw.mtm.icw2.util.Matrix4f;
import de.htw.mtm.icw2.util.Vector3f;

import static de.htw.mtm.icw2.util.ShaderReader.readShaderFromFile;

public class VoxelCubeRenderer {
	private int vaoID;
	private int vertexBufferID;
	private int colorBufferID;
	
	private int voxelShaderProgram;
	private int voxelVertexShader;
	private int voxelFragmentShader;
	
	private int wireShaderProgram;
	private int wireVertexShader;
	private int wireFragmentShader;
	
	private int texID;
	ByteBuffer texels;
	
	public Matrix4f model;
	
	final float VERTEX_BUFFER_CUBE [] = {
	    -0.5f,-0.5f,-0.5f,
	    -0.5f,-0.5f, 0.5f,
	    -0.5f, 0.5f, 0.5f,
	    0.5f, 0.5f,-0.5f,
	    -0.5f,-0.5f,-0.5f,
	    -0.5f, 0.5f,-0.5f,
	    0.5f,-0.5f, 0.5f,
	    -0.5f,-0.5f,-0.5f,
	    0.5f,-0.5f,-0.5f,
	    0.5f, 0.5f,-0.5f,
	    0.5f,-0.5f,-0.5f,
	    -0.5f,-0.5f,-0.5f,
	    -0.5f,-0.5f,-0.5f,
	    -0.5f, 0.5f, 0.5f,
	    -0.5f, 0.5f,-0.5f,
	    0.5f,-0.5f, 0.5f,
	    -0.5f,-0.5f, 0.5f,
	    -0.5f,-0.5f,-0.5f,
	    -0.5f, 0.5f, 0.5f,
	    -0.5f,-0.5f, 0.5f,
	    0.5f,-0.5f, 0.5f,
	    0.5f, 0.5f, 0.5f,
	    0.5f,-0.5f,-0.5f,
	    0.5f, 0.5f,-0.5f,
	    0.5f,-0.5f,-0.5f,
	    0.5f, 0.5f, 0.5f,
	    0.5f,-0.5f, 0.5f,
	    0.5f, 0.5f, 0.5f,
	    0.5f, 0.5f,-0.5f,
	    -0.5f, 0.5f,-0.5f,
	    0.5f, 0.5f, 0.5f,
	    -0.5f, 0.5f,-0.5f,
	    -0.5f, 0.5f, 0.5f,
	    0.5f, 0.5f, 0.5f,
	    -0.5f, 0.5f, 0.5f,
	    0.5f,-0.5f, 0.5f
	};
	
	public VoxelCubeRenderer(Matrix4f view, Matrix4f projection) {
		model = new Matrix4f();//.multiply(Matrix4f.translate(-1, -1, -2));
		generateAndBindVAO();
		generateVertexBuffer();
		generateColorBuffer();
		loadAndCompileShaders();
		setupShaderParameters(view, projection);
	}

	public VoxelCubeRenderer(Matrix4f view, Matrix4f projection, Matrix4f modelM) {
		model = modelM;
		generateAndBindVAO();
		generateVertexBuffer();
		generateColorBuffer();
		loadAndCompileShaders();
		setupShaderParameters(view, projection);
	}
	
	private void generateAndBindVAO() {
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		System.out.println(vaoID);
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
		colorBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
	}
	
	private void loadAndCompileShaders () {
		/////////////////////////////////////////////////////////////////////////
		//                 Voxel Shader Program
		/////////////////////////////////////////////////////////////////////////
		String vertexSource = readShaderFromFile(getClass().getResource("./shader/voxel.vert").getPath());
		voxelVertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(voxelVertexShader, vertexSource);
		glCompileShader(voxelVertexShader);
		
		int status = glGetShaderi(voxelVertexShader, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetShaderInfoLog(voxelVertexShader));
		}
		
		String fragmentSource = readShaderFromFile(getClass().getResource("./shader/voxel.frag").getPath());
		voxelFragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(voxelFragmentShader, fragmentSource);
		glCompileShader(voxelFragmentShader);
		
		status = glGetShaderi(voxelFragmentShader, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetShaderInfoLog(voxelFragmentShader));
		}
		
		voxelShaderProgram = glCreateProgram();
		glAttachShader(voxelShaderProgram, voxelVertexShader);
		glAttachShader(voxelShaderProgram, voxelFragmentShader);
		glLinkProgram(voxelShaderProgram);
		
		status = glGetProgrami(voxelShaderProgram, GL_LINK_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetProgramInfoLog(voxelShaderProgram));
		}
		
		/////////////////////////////////////////////////////////////////////////
		//                 Wire Shader Program
		/////////////////////////////////////////////////////////////////////////
		
		vertexSource = readShaderFromFile(getClass().getResource("./shader/wire.vert").getPath());
		wireVertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(wireVertexShader, vertexSource);
		glCompileShader(wireVertexShader);
		
		status = glGetShaderi(wireVertexShader, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetShaderInfoLog(wireVertexShader));
		}
		
		fragmentSource = readShaderFromFile(getClass().getResource("./shader/wire.frag").getPath());
		wireFragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(wireFragmentShader, fragmentSource);
		glCompileShader(wireFragmentShader);
		
		status = glGetShaderi(wireFragmentShader, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetShaderInfoLog(wireFragmentShader));
		}
		
		wireShaderProgram = glCreateProgram();
		glAttachShader(wireShaderProgram, wireVertexShader);
		glAttachShader(wireShaderProgram, wireFragmentShader);
		glLinkProgram(wireShaderProgram);
		
		status = glGetProgrami(wireShaderProgram, GL_LINK_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetProgramInfoLog(wireShaderProgram));
		}
	}
	
	private void setupShaderParameters(Matrix4f view, Matrix4f projection) {
		glUseProgram(voxelShaderProgram);
		int floatSize = 4;

		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
		int posAttrib = glGetAttribLocation(voxelShaderProgram, "position");
		glEnableVertexAttribArray(posAttrib);
		glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 3 * floatSize, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
		int colAttrib = glGetAttribLocation(voxelShaderProgram, "color");
		glEnableVertexAttribArray(colAttrib);
		glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 3 * floatSize, 0);
		
		int uniModel = glGetUniformLocation(voxelShaderProgram, "model");
		glUniformMatrix4fv(uniModel, false, model.getBuffer());

		int uniView = glGetUniformLocation(voxelShaderProgram, "view");
		glUniformMatrix4fv(uniView, false, view.getBuffer());

		int uniProjection = glGetUniformLocation(voxelShaderProgram, "projection");
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
		
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
		
		int loc = glGetUniformLocation(voxelShaderProgram, "voxels");
	    glUniform1i(loc, 0);
		
		glUseProgram(wireShaderProgram);
		uniModel = glGetUniformLocation(wireShaderProgram, "model");
		//model = new Matrix4f();//.multiply(Matrix4f.translate(-1, 0, 0));
		glUniformMatrix4fv(uniModel, false, model.getBuffer());

		uniView = glGetUniformLocation(wireShaderProgram, "view");
		glUniformMatrix4fv(uniView, false, view.getBuffer());

		uniProjection = glGetUniformLocation(wireShaderProgram, "projection");
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
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
	
	public void updateUniMVP(Matrix4f view, Matrix4f projection) {
		updateUniModel();
		updateUniView(view);
		updateUniProjection(projection);
	}
	
	public void updateUniModel() {
		glUseProgram(voxelShaderProgram);
		int uniModel = glGetUniformLocation(voxelShaderProgram, "model");
		glUniformMatrix4fv(uniModel, false, model.getBuffer());
		
		glUseProgram(wireShaderProgram);
		uniModel = glGetUniformLocation(wireShaderProgram, "model");
		Matrix4f scaledModel = Matrix4f.scale(1.01f, 1.01f, 1.01f).multiply(model);
		glUniformMatrix4fv(uniModel, false, scaledModel.getBuffer());
	}
	
	public void updateUniView(Matrix4f view) {
		glUseProgram(voxelShaderProgram);
		int uniView = glGetUniformLocation(voxelShaderProgram, "view");
		glUniformMatrix4fv(uniView, false, view.getBuffer());
		
		glUseProgram(wireShaderProgram);
		uniView = glGetUniformLocation(wireShaderProgram, "view");
		glUniformMatrix4fv(uniView, false, view.getBuffer());
	}
	
	public void updateUniProjection(Matrix4f projection) {
		glUseProgram(voxelShaderProgram);
		int uniProjection = glGetUniformLocation(voxelShaderProgram, "projection");
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
		
		glUseProgram(wireShaderProgram);
		uniProjection = glGetUniformLocation(wireShaderProgram, "projection");
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
	}
	
	public void render() {
		glBindVertexArray(vaoID);
//		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
//		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_3D, texID);
		
		glUseProgram(voxelShaderProgram);
		glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
		glBindVertexArray(vaoID);
		glDrawArrays(GL_TRIANGLES, 0, 108);
		
		glUseProgram(wireShaderProgram);
		glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
		glBindVertexArray(vaoID);
		glDrawArrays(GL_TRIANGLES, 0, 108);
		
		glBindVertexArray(0);
	}
	
	public void delete() {
		 glDeleteVertexArrays(vaoID);
		 glDeleteBuffers(vertexBufferID);
		 glDeleteBuffers(colorBufferID);
		 glDeleteShader(voxelVertexShader);
		 glDeleteShader(voxelFragmentShader);
		 glDeleteProgram(voxelShaderProgram);
		 glDeleteShader(wireVertexShader);
		 glDeleteShader(wireFragmentShader);
		 glDeleteProgram(wireShaderProgram);
		 glDeleteTextures(texID);
	}
}
