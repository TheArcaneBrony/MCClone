package net.TADT.MCClone;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

public class Main {

	// The window handle
	private long window; 

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}
    GLFWErrorCallback errorCallback;
    GLFWKeyCallback   keyCallback;
    GLFWFramebufferSizeCallback fbCallback;

    int width = 640;
    int height = 480;

    // JOML matrices
    Matrix4f projMatrix = new Matrix4f();
    Matrix4f viewMatrix = new Matrix4f();
    Matrix4f modelMatrix = new Matrix4f();
    Matrix4f modelViewMatrix = new Matrix4f();

    // FloatBuffer for transferring matrices to OpenGL
    FloatBuffer fb = BufferUtils.createFloatBuffer(16);
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
	//	glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
	    glClearColor(0.6f, 0.7f, 0.8f, 1.0f);
        // Enable depth testing
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        // Remember the current time.
        long firstTime = System.nanoTime();


		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glfwSwapBuffers(window); // swap the color buffers
			// Build time difference between this and first time. 
            long thisTime = System.nanoTime();
            float diff = (thisTime - firstTime) / 1E9f;
            // Compute some rotation angle.
            float angle = diff;

            // Make the viewport always fill the whole window.
            glViewport(0, 0, width, height);

            // Build the projection matrix. Watch out here for integer division
            // when computing the aspect ratio!
            projMatrix.setPerspective((float) Math.toRadians(40),
                                      (float)width/height, 0.01f, 100.0f);
            glMatrixMode(GL_PROJECTION);
            glLoadMatrixf(projMatrix.get(fb));

            // Set lookat view matrix
            viewMatrix.setLookAt(0.0f, 4.0f, 10.0f,
                                 0.0f, 0.0f, 0.0f,
                                 0.0f, 1.0f, 0.0f);
            glMatrixMode(GL_MODELVIEW);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Render some grid of cubes at different x and z positions
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    modelMatrix.translation(x * 2.0f, 0, z * 2.0f)
                               .rotateY(angle * (float) Math.toRadians(90));
                    glLoadMatrixf(viewMatrix.mul(modelMatrix, modelViewMatrix).get(fb));
                    GL11.glTranslatef(0f,0.0f,-7f);             
       	         GL11.glRotatef(45f,0.0f,1.0f,0.0f);               
       	         GL11.glColor3f(0.5f,0.5f,1.0f);  
       	         glBegin(GL_QUADS);
       	         glColor3f(   0.0f,  0.0f,  0.2f );
       	         glVertex3f(  0.5f, -0.5f, -0.5f );
       	         glVertex3f( -0.5f, -0.5f, -0.5f );
       	         glVertex3f( -0.5f,  0.5f, -0.5f );
       	         glVertex3f(  0.5f,  0.5f, -0.5f );
       	         glColor3f(   0.0f,  0.0f,  1.0f );
       	         glVertex3f(  0.5f, -0.5f,  0.5f );
       	         glVertex3f(  0.5f,  0.5f,  0.5f );
       	         glVertex3f( -0.5f,  0.5f,  0.5f );
       	         glVertex3f( -0.5f, -0.5f,  0.5f );
       	         glColor3f(   1.0f,  0.0f,  0.0f );
       	         glVertex3f(  0.5f, -0.5f, -0.5f );
       	         glVertex3f(  0.5f,  0.5f, -0.5f );
       	         glVertex3f(  0.5f,  0.5f,  0.5f );
       	         glVertex3f(  0.5f, -0.5f,  0.5f );
       	         glColor3f(   0.2f,  0.0f,  0.0f );
       	         glVertex3f( -0.5f, -0.5f,  0.5f );
       	         glVertex3f( -0.5f,  0.5f,  0.5f );
       	         glVertex3f( -0.5f,  0.5f, -0.5f );
       	         glVertex3f( -0.5f, -0.5f, -0.5f );
       	         glColor3f(   0.0f,  1.0f,  0.0f );
       	         glVertex3f(  0.5f,  0.5f,  0.5f );
       	         glVertex3f(  0.5f,  0.5f, -0.5f );
       	         glVertex3f( -0.5f,  0.5f, -0.5f );
       	         glVertex3f( -0.5f,  0.5f,  0.5f );
       	         glColor3f(   0.0f,  0.2f,  0.0f );
       	         glVertex3f(  0.5f, -0.5f, -0.5f );
       	         glVertex3f(  0.5f, -0.5f,  0.5f );
       	         glVertex3f( -0.5f, -0.5f,  0.5f );
       	         glVertex3f( -0.5f, -0.5f, -0.5f );
       	         glEnd();
       	        // System.out.println("aa");
                }
            }
            glfwSwapBuffers(window);
            glfwPollEvents();
	       
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public static void main(String[] args) {
		new Main().run();
	}

}