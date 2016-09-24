package de.htw.mtm.icw2.core;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

//import Jama.Matrix;
import de.htw.mtm.icw2.graphics.VoxelCubeRenderer;
import de.htw.mtm.icw2.util.Matrix4f;
import de.htw.mtm.icw2.util.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
//import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.MemoryUtil.*;

//import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


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
	    
	    glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
	    //Matrix4f.inverse(new Matrix4f());
		
//	    Vector3f eyePos = new Vector3f(2, 2, 8);
//	    
//	    Vector3f up = new Vector3f(0, 1, 0); 
//	    Vector3f target = new Vector3f();
	    
	    //ViewMatrix = Matrix4::lookAt(EyePosition, target, up);
	    
//	    view = Matrix4f.lookAt(eyePos, target, up);
	    
	    
	    //view = Matrix4f.translate(0.0f, 0.0f, -2);
	    Matrix4f Tr	= Matrix4f.translate(0.0f, 0.0f, -3);
	    Matrix4f Rx	= Tr.multiply(Matrix4f.rotate(0, 1.0f, 0.0f, 0.0f));
	    view = Rx.multiply(Matrix4f.rotate(180, 0.0f, 1.0f, 0.0f));
	    
		//view = new Matrix4f();
		//view = view.multiply(Matrix4f.translate(0, 0, -1.5f));
		projection = Matrix4f.perspective((float) Math.toDegrees(0.7), 800f/800f, 1.f, 100f);
		//projection = projection.multiply(Matrix4f.translate(0, 0, -1.5f));
		
		voxCube = new VoxelCubeRenderer(view, projection);
		//float abc = 6;
//		float focalLength = 1.0f / (float) Math.tan(0.7f / 2f);
//		System.out.println("FL: "+focalLength);
//		int uniFL = glGetUniformLocation(voxCube.shaderProgram, "FocalLength");
//		glUniform1f(uniFL, focalLength);
//	    //SetUniform("FocalLength", focalLength);
//
//		int uniWS = glGetUniformLocation(voxCube.shaderProgram, "WindowSize");
//	    glUniform2f(uniWS, 800f, 800f);
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
			
			//voxCube.model = voxCube.model.multiply(Matrix4f.rotate(angle, 0f, 1f, 0f));
			//voxCube.updateUniModel();
			
			voxCube.updateUniMVP(view, projection);
			
			
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
