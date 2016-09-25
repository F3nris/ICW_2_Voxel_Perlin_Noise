package de.htw.mtm.icw2.core;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import de.htw.mtm.icw2.graphics.VoxelCube;
import de.htw.mtm.icw2.util.Matrix4f;
import de.htw.mtm.icw2.util.Vector4f;
import de.htw.mtm.icw2.util.VolumeGenerator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Inspired by: https://github.com/SilverTiger/lwjgl3-tutorial/blob/master/src/silvertiger/tutorial/lwjgl/Introduction.java
 * 
 * @author Michael Thiele-Maas
 *
 */
public class Core {
	// Callbacks 
	private GLFWKeyCallback keyCallback;
	private GLFWErrorCallback errorCallback;
	private long window;
	private List<VoxelCube> voxCubes;
	
	Matrix4f view;
	Matrix4f projection;
	
	private void init() {
		initGLFW();
		initKeyCallback();
		
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glfwSwapInterval(1);
		
		glEnable(GL_BLEND);
	    glEnable(GL_CULL_FACE);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
	    
	    view = Matrix4f.translate(0.0f, 0.0f, -3.0f);
	    projection = Matrix4f.perspective((float) Math.toDegrees(0.7), 800f/800f, 1.f, 100f);
		
	    
	    voxCubes = new ArrayList<VoxelCube>();
	    
	    for (int x =-1; x<=1; x++) {
	    	for (int z =-1; z<=1; z++) {
	    		VoxelCube tmp = new VoxelCube(view, projection, new Matrix4f().multiply(Matrix4f.translate(x, 0.0f, z)));
	    		tmp.setVoxelData(VolumeGenerator.generateSphere(128), 128);
	    		tmp.setDistanceToCamera(view);
		    	voxCubes.add(tmp);
		    }
	    }
	    
	    Collections.sort(voxCubes);
	}
	
	private void initGLFW() {
		errorCallback = GLFWErrorCallback.createPrint(System.err);
		glfwSetErrorCallback(errorCallback);
		
		if (glfwInit() != GLFW_TRUE) {
		    throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		
		window = glfwCreateWindow(800, 800, "Voxel Represented Perlin Noise", NULL, NULL);
		if (window == NULL) {
		    glfwTerminate();
		    throw new RuntimeException("Failed to create the GLFW window");
		}
	}
	
	private void initKeyCallback() {
		keyCallback = new GLFWKeyCallback() {
		    @Override
		    public void invoke(long window, int key, int scancode, int action, int mods) {
		        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
		            glfwSetWindowShouldClose(window, GLFW_TRUE);
		        }
		        if (key == GLFW_KEY_W && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0, 0, 0.03f).multiply(view);
		        } else if (key == GLFW_KEY_S && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0, 0, -0.03f).multiply(view);
		        }
		        if (key == GLFW_KEY_D && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(-0.03f, 0, 0).multiply(view);
		        } else if (key == GLFW_KEY_A && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0.03f, 0, 0).multiply(view);
		        }
		        
		        if (key == GLFW_KEY_X && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0, -0.03f, 0).multiply(view);
		        } else if (key == GLFW_KEY_Z && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0, 0.03f, 0).multiply(view);
		        }
		        
		        if (key == GLFW_KEY_LEFT && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.rotate(-0.35f, 0, 1, 0).multiply(view);
		        } else if (key == GLFW_KEY_RIGHT && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.rotate(0.35f,0, 1, 0).multiply(view);
		        } else if (key == GLFW_KEY_UP && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.rotate(0.35f,1, 0, 0).multiply(view);
		        } else if (key == GLFW_KEY_DOWN && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.rotate(0.35f,1, 0, 0).multiply(view);
		        }
		        
		        
		        Vector4f cameraPos = view.multiply(new Vector4f(1,1,1,1));
		        for (ListIterator<VoxelCube> iter = voxCubes.listIterator(); iter.hasNext(); ) {
				    VoxelCube element = iter.next();
				    
				    element.setDistanceToCamera(view);
				}
			    
			    Collections.sort(voxCubes);
		        
		    }
		};
		glfwSetKeyCallback(window, keyCallback);
	}

	private void loop() {
		while (glfwWindowShouldClose(window) != GLFW_TRUE) {
			glClearColor(0.3f, 0.67f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			
			
			for (ListIterator<VoxelCube> iter = voxCubes.listIterator(); iter.hasNext(); ) {
			    VoxelCube element = iter.next();
			    
			    element.updateUniMVP(view, projection);
			    element.render();
			}
		
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	private void dispose() {
		for (ListIterator<VoxelCube> iter = voxCubes.listIterator(); iter.hasNext(); ) {
		    VoxelCube element = iter.next();
		    
		    element.delete();
		}
		
		glfwDestroyWindow(window);
		keyCallback.release();
		
		glfwTerminate();
		errorCallback.release();
	}
	
	private void run() {
		init();
		loop();
		dispose();
	}
	
	public static void main(String[] args) {
		new Core().run();
	}
}
