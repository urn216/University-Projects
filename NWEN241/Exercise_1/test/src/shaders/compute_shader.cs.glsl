#version 430
layout(local_size_x = 1, local_size_y = 1) in;
layout(rg32f, binding = 0) restrict uniform image2D img_input;
// layout(rgba32f, binding = 1) uniform image2D img_output;

// layout(binding=0) uniform sampler2D img_input;



#define LAPLACIAN_SIZE 1

#define STEPSIZE 0.02f


#define A 0.08f
#define B 1.5f
#define DU 1.0f
#define DV 20.0f
#define R 0.2f
#define C 0.38f



#define functionf(v) (A - B * v.x + v.x * v.x / (v.y * (1.0f + C * v.x * v.x)))
#define functiong(v) (v.x * v.x - v.y)

#define functionfg(v) vec2(functionf(v), functiong(v))

void main() {
    vec4 pixel = vec4(0.0, 0.0, 0.0, 1.0);
    ivec2 pixel_coords = ivec2(gl_GlobalInvocationID.xy);

    vec2 uv_vals = imageLoad(img_input, pixel_coords).xy;
    // vec2 uv_vals = texture(img_input, pixel_coords).xy;



    // unrolled 5x5
    vec2 laplacian = vec2(0.0, 0.0);
    ivec2 groupsize = imageSize(img_input);
    ivec2 intermediatevalue = groupsize + pixel_coords;
    // [[0.021428571428571425, 0.028571428571428567,   0.04285714285714285,    0.028571428571428567, 0.021428571428571425],
    //  [0.028571428571428567, 0.04285714285714285,    0.0857142857142857,     0.04285714285714285,  0.028571428571428567],
    //  [0.04285714285714285,  0.0857142857142857,     1.0,                    0.0857142857142857,   0.04285714285714285],
    //  [0.028571428571428567, 0.04285714285714285,    0.0857142857142857,     0.04285714285714285,  0.028571428571428567],
    //  [0.021428571428571425, 0.028571428571428567,   0.04285714285714285,    0.028571428571428567, 0.021428571428571425]]
    laplacian += imageLoad(img_input, (ivec2(-1, 0) + intermediatevalue)%groupsize).xy*0.0857142857142857f;
    laplacian += imageLoad(img_input, (ivec2( 1, 0) + intermediatevalue)%groupsize).xy*0.0857142857142857f;
    laplacian += imageLoad(img_input, (ivec2( 0, 1) + intermediatevalue)%groupsize).xy*0.0857142857142857f;
    laplacian += imageLoad(img_input, (ivec2( 0,-1) + intermediatevalue)%groupsize).xy*0.0857142857142857f;

    laplacian += imageLoad(img_input, (ivec2(-1,-1) + intermediatevalue)%groupsize).xy*0.04285714285714285f;
    laplacian += imageLoad(img_input, (ivec2(-1, 1) + intermediatevalue)%groupsize).xy*0.04285714285714285f;
    laplacian += imageLoad(img_input, (ivec2( 1,-1) + intermediatevalue)%groupsize).xy*0.04285714285714285f;
    laplacian += imageLoad(img_input, (ivec2( 1, 1) + intermediatevalue)%groupsize).xy*0.04285714285714285f;

    laplacian += imageLoad(img_input, (ivec2(-2, 0) + intermediatevalue)%groupsize).xy*0.04285714285714285f;
    laplacian += imageLoad(img_input, (ivec2( 2, 0) + intermediatevalue)%groupsize).xy*0.04285714285714285f;
    laplacian += imageLoad(img_input, (ivec2( 0, 2) + intermediatevalue)%groupsize).xy*0.04285714285714285f;
    laplacian += imageLoad(img_input, (ivec2( 0,-2) + intermediatevalue)%groupsize).xy*0.04285714285714285f;

    laplacian += imageLoad(img_input, (ivec2(-1,-2) + intermediatevalue)%groupsize).xy*0.028571428571428567f;
    laplacian += imageLoad(img_input, (ivec2(-1, 2) + intermediatevalue)%groupsize).xy*0.028571428571428567f;
    laplacian += imageLoad(img_input, (ivec2( 1, 2) + intermediatevalue)%groupsize).xy*0.028571428571428567f;
    laplacian += imageLoad(img_input, (ivec2( 1,-2) + intermediatevalue)%groupsize).xy*0.028571428571428567f;
    laplacian += imageLoad(img_input, (ivec2(-2,-1) + intermediatevalue)%groupsize).xy*0.028571428571428567f;
    laplacian += imageLoad(img_input, (ivec2(-2, 1) + intermediatevalue)%groupsize).xy*0.028571428571428567f;
    laplacian += imageLoad(img_input, (ivec2( 2, 1) + intermediatevalue)%groupsize).xy*0.028571428571428567f;
    laplacian += imageLoad(img_input, (ivec2( 2,-1) + intermediatevalue)%groupsize).xy*0.028571428571428567f;

    laplacian += imageLoad(img_input, (ivec2(-2,-2) + intermediatevalue)%groupsize).xy*0.021428571428571425f;
    laplacian += imageLoad(img_input, (ivec2(-2, 2) + intermediatevalue)%groupsize).xy*0.021428571428571425f;
    laplacian += imageLoad(img_input, (ivec2( 2, 2) + intermediatevalue)%groupsize).xy*0.021428571428571425f;
    laplacian += imageLoad(img_input, (ivec2( 2,-2) + intermediatevalue)%groupsize).xy*0.021428571428571425f;

    laplacian -= uv_vals;



    /*
    * du / dt = R * f(u, v) + Du * \Del^2u
    * dv / dt = R * g(u, v) + Dv * \Del^2v
    */

    vec2 newval = uv_vals + STEPSIZE/2.0f * (R * functionfg(uv_vals) + vec2(DU, DV) * laplacian);
    pixel.xy =    uv_vals + STEPSIZE *      (R * functionfg(newval)  + vec2(DU, DV) * laplacian);


    imageStore(img_input, pixel_coords, pixel);
}



// vec3 hsv2rgb(vec3 c){
//     vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
//     vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
//     return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
// }
