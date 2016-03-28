package de.htw.mtm.icw2.core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import de.htw.mtm.icw2.graphics.VoxelCubeRenderer;
import de.htw.mtm.icw2.util.Matrix4f;

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
	private VoxelCubeRenderer voxCube;
	
	Matrix4f view;
	Matrix4f projection;
	private float angle = 0f;
	private double prevTime = glfwGetTime();
	
	private void init() {
		initGLFW();
		initKeyCallback();
		
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glfwSwapInterval(1);
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		
		glEnable(GL_BLEND);
	    glEnable(GL_CULL_FACE);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		view = new Matrix4f();
		projection = Matrix4f.perspective(140f, 640f/480f, 0.25f, 6f);
		projection = projection.multiply(Matrix4f.translate(0, 0, -1.5f));
		
		voxCube = new VoxelCubeRenderer(view, projection);
	}
	
	private void initGLFW() {
		errorCallback = GLFWErrorCallback.createPrint(System.err);
		glfwSetErrorCallback(errorCallback);
		
		if (glfwInit() != GLFW_TRUE) {
		    throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		
		window = glfwCreateWindow(640, 480, "Voxel Represented Perlin Noise", NULL, NULL);
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
		    }
		};
		glfwSetKeyCallback(window, keyCallback);
	}

	private void loop() {
		while (glfwWindowShouldClose(window) != GLFW_TRUE) {
			glClearColor(0.3f, 0.67f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			
			voxCube.model = voxCube.model.multiply(Matrix4f.rotate(angle, 0f, 1f, 0f));
			voxCube.updateUniModel();
			
			float delta = (float) (glfwGetTime() - prevTime);
			prevTime = glfwGetTime();
			angle += 0.01 * delta;

			voxCube.render();
			
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	private void dispose() {
		voxCube.delete();
		
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
