#include "shader_utils.hpp"
#include <cstdlib>
#include <chrono>

GLuint imgWidth = 512, imgHeight = 512;


// This example is taken from https://learnopengl.com/
// https://learnopengl.com/code_viewer.php?code=getting-started/fishPatternMain
// The code originally used GLEW, I replaced it with Glad

// Compile:
// g++ example/c++/fishPatternMain.cpp -Ibuild/include build/src/glad.c -lglfw -ldl


// Function prototypes
void key_callback(GLFWwindow* window, int key, int scancode, int action, int mode);
void framebuffer_size_callback(GLFWwindow* window, int width, int height);


// The MAIN function, from here we start the application and run the game loop
int main(){
    std::cout << "Starting GLFW context, OpenGL 4.3" << std::endl;
    // Init GLFW
    glfwInit();
    // Set all the required options for GLFW
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3); // compute shaders added in 4.3
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    // glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

    // Create a GLFWwindow object that we can use for GLFW's functions
    GLFWwindow* window = glfwCreateWindow(imgWidth, imgHeight, "Attempt One", NULL, NULL);
    glfwMakeContextCurrent(window);
    if (window == NULL)
    {
        std::cerr << "Failed to create GLFW window" << std::endl;
        glfwTerminate();
        return -1;
    }

    // Set the required callback functions
    glfwSetKeyCallback(window, key_callback);
    glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);



    if (!gladLoadGLLoader((GLADloadproc) glfwGetProcAddress))
    {
        std::cerr << "Failed to initialize OpenGL context" << std::endl;
        return -1;
    }

    // Define the viewport dimensions
    glViewport(0, 0, imgWidth, imgHeight);

    const GLubyte* renderer = glGetString(GL_RENDERER );
    const GLubyte* version  = glGetString(GL_VERSION );
    fprintf(stdout, "Renderer: %s\n", renderer );
    fprintf(stdout, "OpenGL version %s\n", version );


    //========================================================================
    GLuint quad_vao     = create_quad_vao();
    GLuint quad_program = create_quad_program();

    GLuint fish_program = 0;
    { // create the compute shader
        GLuint fish_shader = glCreateShader(GL_COMPUTE_SHADER );

        GLchar *compute_shader_str = ( GLchar *)malloc(sizeof(GLchar)*8192);

        try{
            get_file_contents((char const *)"src/shaders/compute_shader.cs.glsl", compute_shader_str, 8192);
            // compute_shader_str =  (const GLchar *)get_file_contents((char const *)"src/shaders/compute_shader.cs.glsl").c_str();

        }catch(int e){
            fprintf( stderr, "FAILED TO READ FILE CORRECTLY: %d\n", e);
        }
        // std::cerr << compute_shader_str << std::endl << std::endl << std::endl;

        glShaderSource(fish_shader, 1, &compute_shader_str, NULL );
        glCompileShader(fish_shader );
        ( check_shader_errors(fish_shader) );
        fish_program = glCreateProgram();
        glAttachShader(fish_program, fish_shader );
        glLinkProgram(fish_program );
        ( check_program_errors(fish_program ) );

        free(compute_shader_str);//??
    }

    // texture handle and dimensions
    GLuint tex_pattern [] = {0};
    { // create the texture
        glGenTextures(2, tex_pattern);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tex_pattern[0] );
        GLint const Swizzle[] = {GL_RED, GL_BLUE, GL_GREEN, GL_ALPHA};
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_RGBA, Swizzle);
        // linear allows us to scale the window up retaining reasonable quality
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // same internal format as compute shader input

        // //second texture
        // glActiveTexture(GL_TEXTURE1);
        // glBindTexture(GL_TEXTURE_2D, tex_pattern[1] );
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_RGBA, Swizzle);
        // // linear allows us to scale the window up retaining reasonable quality
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // // same internal format as compute shader input

        // create the inital texture!!!!
        {
        glActiveTexture(GL_TEXTURE0);
        float *noisetexture;
        noisetexture = (float *)malloc(sizeof(float)*2*imgWidth*imgHeight);
        if (noisetexture == NULL) {
            std::cout << "Failed to allocate memory" << std::endl;
            exit(EXIT_FAILURE);
        }
        // generate noise texture
        std::srand(std::time(nullptr));
        for(int i=0; i<imgWidth; i++){
            for(int j=0; j<imgHeight; j++){
                float * ind = noisetexture + (j*imgWidth+i)*2;
                *(ind  ) = ((float)(std::rand())/RAND_MAX)*0.6+0.2;
                *(ind+1) = ((float)(std::rand())/RAND_MAX)*0.6+0.2;
            }
        }
        // bind to image unit so can write to specific pixels from the shader
        glTexImage2D( GL_TEXTURE_2D, 0, GL_RG32F, imgWidth, imgHeight, 0, GL_RG, GL_FLOAT, noisetexture);
        free(noisetexture);
        }

        // glActiveTexture(GL_TEXTURE1);
        // glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, imgWidth, imgHeight, 0, GL_RGBA, GL_FLOAT, NULL );
        // // bind to image unit so can write to specific pixels from the shader
    }


    //========================================================================
    glBindImageTexture( 0, tex_pattern[0], 0, GL_FALSE, 0, GL_READ_WRITE, GL_RG32F );


    glUseProgram( fish_program );
    glFinish();
    auto t1 = std::chrono::high_resolution_clock::now();
    for(int i=0; i<100; i++){
        glDispatchCompute(imgWidth, imgHeight, 1 );
    }
    glFinish();
    auto t2 = std::chrono::high_resolution_clock::now();

    std::cout << std::chrono::duration_cast<std::chrono::milliseconds>(t2 - t1).count() << std::endl;


    while (!glfwWindowShouldClose(window )){ // drawing loop

        for(int i=0; i<10; i++)
        {                                          // launch compute shader
            // glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT );
            // Unroll loop - do two steps at a time(ish)
            glUseProgram( fish_program );
            // glBindImageTexture( 0, tex_pattern[0], 0, GL_FALSE, 0, GL_READ_ONLY, GL_RGBA32F );
            // glBindImageTexture( 1, tex_pattern[1], 0, GL_FALSE, 0, GL_WRITE_ONLY, GL_RGBA32F );
            glDispatchCompute(imgWidth, imgHeight, 1 );
            // glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT );

            // glBindImageTexture( 1, tex_pattern[0], 0, GL_FALSE, 0, GL_READ_ONLY, GL_RGBA32F );
            // glBindImageTexture( 0, tex_pattern[1], 0, GL_FALSE, 0, GL_WRITE_ONLY, GL_RGBA32F );
            glDispatchCompute(imgWidth, imgHeight, 1 );
        }

        // Check if any events have been activated (key pressed, mouse moved etc.) and call corresponding response functions
        glfwPollEvents();

        // prevent sampling before all writes to image are done

        // Render
        // glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT );
        glUseProgram(quad_program );
        glBindVertexArray(quad_vao );
        glActiveTexture(GL_TEXTURE0 );
        glBindTexture(GL_TEXTURE_2D, tex_pattern[0] );
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4 );

        // Swap the screen buffers
        glfwSwapBuffers(window);
    }

    // Terminates GLFW, clearing any resources allocated by GLFW.
    glfwTerminate();
    return 0;
}

// Is called whenever a key is pressed/released via GLFW
void key_callback(GLFWwindow* window, int key, int scancode, int action, int mode){
    std::cout << key << std::endl;
    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
    glfwSetWindowShouldClose(window, GL_TRUE);
}

void framebuffer_size_callback(GLFWwindow* window, int width, int height){
    glViewport(0, 0, width, height);
    glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F,  width, height, 0, GL_RGBA, GL_FLOAT, NULL );
    imgWidth  = width;
    imgHeight = height;
    std::cout << "Changed window size" << std::endl;

}
