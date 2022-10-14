#pragma once

// GLAD
#include <glad/glad.h>

// GLFW
#include <GLFW/glfw3.h>


#include <iostream>
#include <fstream>
#include <string>
#include <cerrno>

// Window dimensions
extern GLuint WIDTH, HEIGHT;

std::string get_file_contents(const char * filename);
int get_file_contents(const char * filename, GLchar * strdata, int maxlen);

void print_shader_info_log(GLuint shader);
void print_program_info_log(GLuint program);
bool check_shader_errors(GLuint shader);
bool check_program_errors(GLuint program);
GLuint create_quad_vao(void);
GLuint create_quad_program(void);
