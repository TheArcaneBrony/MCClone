package net.TADT.MCClone;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
	public boolean gameRunning = true;
	private long window;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hi");
		new Main().init();
		
	}
	public void init() {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		window = glfwCreateWindow(300, 300, "Hello World!", 0, 0);
		if ( window == 0 )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
	}
	

}