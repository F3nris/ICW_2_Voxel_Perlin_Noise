package de.htw.mtm.icw2.core;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import de.htw.mtm.icw2.graphics.VoxelCube;
import de.htw.mtm.icw2.util.Matrix4f;
import de.htw.mtm.icw2.util.VolumeGenerator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

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
	private VoxelCube voxCube;
	private VoxelCube voxCube2;
	
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
		
		voxCube = new VoxelCube(view, projection);
		voxCube.setVoxelData(VolumeGenerator.generateSphere(128), 128);
		
		voxCube2 = new VoxelCube(view, projection, new Matrix4f().multiply(Matrix4f.translate(1.f, 1.f, 0.f)));
		voxCube2.setVoxelData(VolumeGenerator.generateCylinder(128), 128);
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
		        if ((key == GLFW_KEY_UP || key == GLFW_KEY_W) && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0, 0, 0.03f).multiply(view);
		        } else if ((key == GLFW_KEY_DOWN || key == GLFW_KEY_S) && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0, 0, -0.03f).multiply(view);
		        }
		        if ((key == GLFW_KEY_RIGHT || key == GLFW_KEY_D) && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(-0.03f, 0, 0).multiply(view);
		        } else if ((key == GLFW_KEY_LEFT || key == GLFW_KEY_A) && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0.03f, 0, 0).multiply(view);
		        }
		        
		        if (key == GLFW_KEY_X && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0, -0.03f, 0).multiply(view);
		        } else if (key == GLFW_KEY_Z && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.translate(0, 0.03f, 0).multiply(view);
		        }
		        
		        if (key == GLFW_KEY_Q && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.rotate(-0.25f, 0, 1, 0).multiply(view);
		        } else if (key == GLFW_KEY_E && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
		        	view = Matrix4f.rotate(0.25f,0, 1, 0).multiply(view);
		        }
		        
		        
		    }
		};
		glfwSetKeyCallback(window, keyCallback);
	}

	private void loop() {
		while (glfwWindowShouldClose(window) != GLFW_TRUE) {
			glClearColor(0.3f, 0.67f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			
			
			
			voxCube2.updateUniMVP(view, projection);
			voxCube2.render();
			
			voxCube.updateUniMVP(view, projection);
			voxCube.render();
		
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	private void dispose() {
		voxCube.delete();
		voxCube2.delete();
		
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
