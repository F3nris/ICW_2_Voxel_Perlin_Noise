package de.htw.mtm.icw2.core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import de.htw.mtm.icw2.graphics.VoxelCubeRenderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;


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
	private VoxelCubeRenderer test;
	
	private void init() {
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
		
		keyCallback = new GLFWKeyCallback() {
		    @Override
		    public void invoke(long window, int key, int scancode, int action, int mods) {
		        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
		            glfwSetWindowShouldClose(window, GLFW_TRUE);
		        }
		    }
		};
		glfwSetKeyCallback(window, keyCallback);
		
		glfwMakeContextCurrent(window);
		
		GL.createCapabilities();
		glfwSwapInterval(1);
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		
		
		test = new VoxelCubeRenderer();
	}
	
	private void loop() {
		while (glfwWindowShouldClose(window) != GLFW_TRUE) {
			glClearColor(0.3f, 0.67f, 1.0f, 1.0f);
			
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			
			glEnable(GL_DEPTH_TEST);
			// Accept fragment if it closer to the camera than the former one
			glDepthFunc(GL_LESS);
			
			//test.update();
			//Matrix4f model = Matrix4f.rotate(angle, 0f, 0f, 1f);
		    //glUniformMatrix4fv(uniModel, false, model.getBuffer());

		    glDrawArrays(GL_TRIANGLES, 0, 108);
			
			
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	private void dispose() {
		test.delete();	
		
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
