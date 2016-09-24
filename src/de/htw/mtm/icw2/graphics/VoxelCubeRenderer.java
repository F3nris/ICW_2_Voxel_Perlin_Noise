package de.htw.mtm.icw2.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import de.htw.mtm.icw2.util.Matrix4f;
import de.htw.mtm.icw2.util.Vector3f;
import de.htw.mtm.icw2.util.Vector4f;

import static de.htw.mtm.icw2.util.ShaderReader.readShaderFromFile;

public class VoxelCubeRenderer {
	private int vaoID;
	private int vertexBufferID;
	private int colorBufferID;
	
	// TODO Set private
	public int voxelShaderProgram;
	private int voxelVertexShader;
	private int voxelFragmentShader;
	
	public int wireShaderProgram;
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
	
	final float COLOR_BUFFER_CUBE [] = {
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
	
	public VoxelCubeRenderer(Matrix4f view, Matrix4f projection) {
		generateAndBindVAO();
		generateVertexBuffer();
		generateColorBuffer();
		loadAndCompileShaders();
		setupShaderParameters(view, projection);
	}

	private int prepareTexture3d(final int width, final int height, final int depth, final ByteBuffer texels) {
		//Texture3D tex = new Texture3D();
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
		// 12 Triangles to draw a cube ==> 12 * 3 = 36 * 3 = 108
		FloatBuffer colors = BufferUtils.createFloatBuffer(108);
		colors.put(COLOR_BUFFER_CUBE);
		colors.flip();
		
		colorBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
		glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
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
		// TODO remove later
		glBindFragDataLocation(voxelShaderProgram, 0, "fragColor");
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
		model = new Matrix4f();//.multiply(Matrix4f.translate(-1, -1, -2));
		glUniformMatrix4fv(uniModel, false, model.getBuffer());

		int uniView = glGetUniformLocation(voxelShaderProgram, "view");
		glUniformMatrix4fv(uniView, false, view.getBuffer());

		int uniProjection = glGetUniformLocation(voxelShaderProgram, "projection");
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
		
		int n = 100;
		texels = BufferUtils.createByteBuffer(n*n*n*4);
		
//		int dimEnd = (n / 2);
//		int dimStart = -dimEnd;

		//byte a = 127;
		byte b = (byte) (255 & 0xFF);
		byte c = 0;
		//texels.put(b); texels.put(c); texels.put(c); texels.put(b);
		for (int z = 0; z < n; z++) {
			for (int y = 0; y < n; y++) {
				for (int x = 0; x < n; x++) {
					
					Vector3f v = new Vector3f(x,y,z);
					v = v.add(new Vector3f(n/-2, n/-2, n/-2));
					v = v.divide(n/-2);
					float t = 1.f / (float) n;
					v = v.add(new Vector3f(-t, -t, -t));
					
					
					texels.put(b); texels.put(c); texels.put(c);
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
	    //First of all, we retrieve the location of the sampler in memory.
	    glUniform1i(loc, 0);
		
//		IntBuffer ib = BufferUtils.createIntBuffer(n*n*n);
//		
//		for (int x=0; x<n; x++) {
//			for (int y=0; y<n; y++) {
//				for (int z=0; z<n; z++) {
//					Vector3f v = new Vector3f(x,y,z);
//					v = v.add(new Vector3f(n/-2, n/-2, n/-2));
//					v = v.divide(n/-2);
//					float t = 1.f / (float) n;
//					v = v.add(new Vector3f(-t, -t, -t));
//					int pos = (x * n * n) + (y * n) + z;
//					if ((v.x * v.x) + (v.y * v.y) + (v.z * v.z) <= 1) {
//						ib.put(pos, 1);
//					}
//				}
//			}
//		}
		
		
		
		
		
		
		
//		int uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels");
//		glUniform1iv(uniVoxels, ib);
		
//		Vector3f v000 = new Vector3f (1,0,0);
//		Vector3f v001 = new Vector3f (1,0,0);
//		Vector3f v010 = new Vector3f (1,0,0);
//		Vector3f v011 = new Vector3f (0,0,0); 
//		Vector3f v100 = new Vector3f (1,0,0);
//		Vector3f v101 = new Vector3f (0,0,0);
//		Vector3f v110 = new Vector3f (0,0,0);
//		Vector3f v111 = new Vector3f (0,0,0);
//		
//		int uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels[0]");
//		glUniform3fv(uniVoxels, v000.getBuffer());
//		uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels[1]");
//		glUniform3fv(uniVoxels, v001.getBuffer());
//		uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels[2]");
//		glUniform3fv(uniVoxels, v010.getBuffer());
//		uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels[3]");
//		glUniform3fv(uniVoxels, v011.getBuffer());
//		uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels[4]");
//		glUniform3fv(uniVoxels, v100.getBuffer());
//		uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels[5]");
//		glUniform3fv(uniVoxels, v101.getBuffer());
//		uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels[6]");
//		glUniform3fv(uniVoxels, v110.getBuffer());
//		uniVoxels = glGetUniformLocation(voxelShaderProgram, "voxels[7]");
//		glUniform3fv(uniVoxels, v111.getBuffer());
		
		
		glUseProgram(wireShaderProgram);
		uniModel = glGetUniformLocation(wireShaderProgram, "model");
		model = new Matrix4f();//.multiply(Matrix4f.translate(-1, -1, -2));
		glUniformMatrix4fv(uniModel, false, model.getBuffer());

		uniView = glGetUniformLocation(wireShaderProgram, "view");
		glUniformMatrix4fv(uniView, false, view.getBuffer());

		uniProjection = glGetUniformLocation(wireShaderProgram, "projection");
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
		
//		int uniModelView = glGetUniformLocation(shaderProgram, "Modelview");
//		Matrix4f mv = view.multiply(model);
//		glUniformMatrix4fv(uniModelView, false, mv.getBuffer());
		
//		Vector3f cam = Matrix4f.inverse(mv).extractCameraPosition();
		
		
//		int uniRO = glGetUniformLocation(shaderProgram, "campos");
//		glUniform3f(uniRO, cam.x, cam.y, cam.z);
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
		Matrix4f scaledModel = Matrix4f.scale(1.001f, 1.001f, 1.001f).multiply(model);
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
