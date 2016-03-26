package de.htw.mtm.icw2.core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
 
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
		
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		FloatBuffer vertices = BufferUtils.createFloatBuffer(3 * 6);
		vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
		vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
		vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
		vertices.flip();
		
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		
	}
	
	private void loop() {
		while (glfwWindowShouldClose(window) != GLFW_TRUE) {
		    /* Do something */
			glClearColor(0.3f, 0.67f, 1.0f, 1.0f);
			
			/* Set viewport and clear screen */
            glViewport(0, 0, 640, 480);
            glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

            /* Set ortographic projection */
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(-(640/480), (640/480), -1f, 1f, 1f, -1f);
            glMatrixMode(GL_MODELVIEW);

            /* Rotate matrix */
            glLoadIdentity();
            glRotatef((float) glfwGetTime() * 50f, 0f, 0f, 1f);

            /* Render triangle */
            glBegin(GL_TRIANGLES);
            glColor3f(1f, 0f, 0f);
            glVertex3f(-0.5f, -0.5f, 0f);
            glColor3f(0f, 1f, 0f);
            glVertex3f(-0.5f, 0.5f, 0f);
            glColor3f(0f, 1f, 0.f);
            glVertex3f(0.5f, 0.5f, 0f);
            glColor3f(1f, 0f, 0f);
            glVertex3f(-0.5f, -0.5f, 0f);
            glColor3f(0f, 1f, 0f);
            glVertex3f(0.5f, 0.5f, 0f);
            glColor3f(1f, 0f, 0f);
            glVertex3f(0.5f, -0.5f, 0f);
            glEnd();
			
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	private void dispose() {
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
