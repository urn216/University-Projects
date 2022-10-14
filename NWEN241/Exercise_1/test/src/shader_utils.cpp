#include "shader_utils.hpp"


#if 0
// Equivelent to GLFW_USE_HYBRID_HPG, but for the dll version
// (use the most powerful graphics processor on window systems with multiple)
#ifndef _WINDEF_
typedef unsigned long DWORD;
extern "C"{
    __declspec(dllexport) int AmdPowerXpressRequestHighPerformance = 1;
    __declspec(dllexport) DWORD NvOptimusEnablement = 0x00000001;
}
#endif
#endif


std::string get_file_contents(const char * filename){ // nasty: pointers being lost by this funtion
    std::ifstream in(filename, std::ios::in | std::ios::binary);
    if (in){
        std::string contents;
        in.seekg(0, std::ios::end);
        contents.resize(in.tellg());
        in.seekg(0, std::ios::beg);
        in.read(&contents[0], contents.size());
        in.close();
        return(contents);
    }
    throw(errno);
}


int get_file_contents(const char * filename, GLchar * strdata, int maxlen){
    std::ifstream in(filename, std::ios::in | std::ios::binary);
    if (in){
        std::string contents;
        in.seekg(0, std::ios::end);
        contents.resize(in.tellg());
        in.seekg(0, std::ios::beg);
        in.read(&contents[0], contents.size());
        in.close();
        if(maxlen < (int)contents.size()){
            throw(errno);
        }
        memcpy(strdata, contents.c_str(), sizeof(GLchar)*std::max(maxlen, (int)contents.size()));
        return(maxlen - (int)contents.size()); // return remaining space in buffer
    }
    throw(errno);
}



void print_shader_info_log(GLuint shader){
    const int max_length    = 4096;
    int actual_length = 0;
    char slog[max_length];
    glGetShaderInfoLog( shader, max_length, &actual_length, slog );
    fprintf( stderr, "shader info log for GL index %u\n%s\n", shader, slog );
}

void print_program_info_log(GLuint program){
    const int max_length    = 4096;
    int actual_length = 0;
    char plog[max_length];
    glGetProgramInfoLog( program, max_length, &actual_length, plog );
    fprintf( stderr, "program info log for GL index %u\n%s\n", program, plog );
}

bool check_shader_errors(GLuint shader){
    GLint params = -1;
    glGetShaderiv( shader, GL_COMPILE_STATUS, &params );
    if ( GL_TRUE != params ) {
        fprintf( stderr, "ERROR: shader %u did not compile\n", shader );
        print_shader_info_log( shader );
        return false;
    }
    return true;
}

bool check_program_errors(GLuint program){
    GLint params = -1;
    glGetProgramiv( program, GL_LINK_STATUS, &params );
    if ( GL_TRUE != params ) {
        fprintf( stderr, "ERROR: program %u did not link\n", program );
        print_program_info_log( program );
        return false;
    }
    return true;
}


GLuint create_quad_vao(void) {
    GLuint vao = 0, vbo = 0;
    float verts[] = { -1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f };
    glGenBuffers( 1, &vbo );
    glBindBuffer( GL_ARRAY_BUFFER, vbo );
    glBufferData( GL_ARRAY_BUFFER, 16 * sizeof( float ), verts, GL_STATIC_DRAW );
    glGenVertexArrays( 1, &vao );
    glBindVertexArray( vao );
    glEnableVertexAttribArray( 0 );
    GLintptr stride = 4 * sizeof( float );
    glVertexAttribPointer( 0, 2, GL_FLOAT, GL_FALSE, stride, NULL );
    glEnableVertexAttribArray( 1 );
    GLintptr offset = 2 * sizeof( float );
    glVertexAttribPointer( 1, 2, GL_FLOAT, GL_FALSE, stride, (GLvoid*)offset );
    return vao;
}


GLuint create_quad_program(void) {
    GLuint program     = glCreateProgram();


    GLuint vert_shader = glCreateShader( GL_VERTEX_SHADER );
    const GLchar *vert_shader_str;
    try{
        vert_shader_str =  (const GLchar *)get_file_contents((char const *)"src/shaders/vert_shader.vs.glsl").c_str();
    }catch(int e){ fprintf( stderr, "FAILED TO READ FILE CORRECTLY: %d\n", e); }
    glShaderSource( vert_shader, 1, &vert_shader_str, NULL );
    glCompileShader( vert_shader );
    check_shader_errors( vert_shader );
    glAttachShader( program, vert_shader );


    GLuint frag_shader = glCreateShader( GL_FRAGMENT_SHADER );
    const GLchar *frag_shader_str;
    try{
        frag_shader_str =  (const GLchar *)get_file_contents((char const *)"src/shaders/frag_shader.fs.glsl").c_str();
    }catch(int e){ fprintf( stderr, "FAILED TO READ FILE CORRECTLY: %d\n", e); }
    glShaderSource( frag_shader, 1, &frag_shader_str, NULL );
    glCompileShader( frag_shader );
    check_shader_errors( frag_shader );
    glAttachShader( program, frag_shader );

    glLinkProgram( program );
    check_program_errors( program );
    return program;
}
