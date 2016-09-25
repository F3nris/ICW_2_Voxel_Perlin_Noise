package de.htw.mtm.icw2.graphics;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import de.htw.mtm.icw2.graphics.renderer.VoxelRenderer;
import de.htw.mtm.icw2.graphics.renderer.WireframeRenderer;
import de.htw.mtm.icw2.util.Matrix4f;
import de.htw.mtm.icw2.util.Vector4f;

public class VoxelCube implements Comparable<VoxelCube>{
	private String name;
	private int vaoID;
	private int vertexBufferID;
	private int colorBufferID;
	
	private float distanceToCamera = 0;
	
	private VoxelRenderer vr;
	private WireframeRenderer wfr;
	
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
	
	public VoxelCube(Matrix4f view, Matrix4f projection) {
		model = new Matrix4f();//.multiply(Matrix4f.translate(-1, -1, -2));
		init(view, projection);
	}

	public VoxelCube(Matrix4f view, Matrix4f projection, Matrix4f modelM) {
		model = modelM;
		init(view, projection);
	}
	
	public VoxelCube(String name, Matrix4f view, Matrix4f projection, Matrix4f modelM) {
		model = modelM;
		this.name = name;
		init(view, projection);
	}
	
	private void init(Matrix4f view, Matrix4f projection) {
		generateAndBindVAO();
		generateVertexBuffer();
		generateColorBuffer();
		
		// Re-bind vertex buffer, so that the vertex positions can be loaded into the shaders
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
		
		wfr = new WireframeRenderer();
		wfr.setupBasicShaderParameters(model, view, projection);
		
		vr = new VoxelRenderer();
		vr.setupBasicShaderParameters(model, view, projection);
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
	
	
	public void updateUniMVP(Matrix4f view, Matrix4f projection) {
		updateUniModel();
		updateUniView(view);
		updateUniProjection(projection);
	}
	
	
	public void updateUniModel() {
		vr.updateUniModel(model);
		wfr.updateUniModel(model);
	}
	
	
	public void updateUniView(Matrix4f view) {
		vr.updateUniView(view);
		wfr.updateUniView(view);
	}
	
	
	public void updateUniProjection(Matrix4f projection) {
		vr.updateUniProjection(projection);
		wfr.updateUniProjection(projection);
	}
	
	public void setVoxelData(ByteBuffer buffer, int dim) {
		vr.generateVolumeTexture(buffer, dim);
	}
	
	public void render() {
		glBindVertexArray(vaoID);
		
		vr.render();
		wfr.render();
		
		glBindVertexArray(0);
	}
	
	public void delete() {
		 glDeleteVertexArrays(vaoID);
		 glDeleteBuffers(vertexBufferID);
		 glDeleteBuffers(colorBufferID);
		 
		 wfr.delete();
		 vr.delete();
		 vr.deleteTexture();
	}
	
	public void setDistanceToCamera(Matrix4f view) {
		Vector4f pos = view.multiply(model).multiply(new Vector4f(1,1,1,1));
		
		distanceToCamera = ((pos.x + pos.x) * (pos.x + pos.x)) 
				+ ((pos.y + pos.y) * (pos.y + pos.y)) 
				+ ((pos.z + pos.z) * (pos.z + pos.z));
	}
	
	public float getDistanceToCamera() {
		return distanceToCamera;
	}

	@Override
	public int compareTo(VoxelCube o) {
		int result = 0;
		float otherDist = o.getDistanceToCamera();
		if (otherDist < distanceToCamera) {
			result = -1;
		} else if (otherDist > distanceToCamera) {
			result = 1;
		}
		return result;
	}
}
