/**
* Skeleton file for server.c
*
* You are free to modify this file to implement the server specifications
* as detailed in Assignment 3 handout.
*
* As a matter of good programming habit, you should break up your imple-
* mentation into functions. All these functions should contained in this
* file as you are only allowed to submit this file.
*/

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <ctype.h>

FILE *out_file;
int writing = 0;

/**
* Sends a message to our client!
* Takes in client file descriptor and a message to send.
*/
int send_message(int cfd, char *msg)
{
  if (send(cfd, msg, strlen(msg), 0) < 0) {
    printf("Error sending message.\n");
    shutdown(cfd, SHUT_RDWR);
    exit(0);
  }
  return 0;
}

/**
* Recieves a message from our client!
* Takes in client file descriptor and a pointer to a str buffer.
*/
int recieve_message(int cfd, char *buf)
{
  if (recv(cfd, buf, 100, 0) < 0) {
    printf("Error recieving message.\n");
    shutdown(cfd, SHUT_RDWR);
    exit(0);
  }
  return 0;
}

/**
* Puts a line into a file from a client input.
* Takes in client file descriptor and a message to write.
*/
int put_line(int cfd, char *msg)
{
  fprintf(out_file, msg);

  if (*msg == '\n') {
    writing--;
    if (!writing) {
      send_message(cfd, "SERVER 201 Created\n");
      fclose(out_file);
    }
    return writing;
  }
  writing = 2;
  return writing;
}

/**
* Opens and displays a file.
* Takes in client file descriptor and a pointer to a file name.
*/
int get_file(int cfd, char *fname)
{
  //If no name was given, abort
  if (*fname == '\0') {
    return send_message(cfd, "SERVER 500 Get Error\n");
  }

  FILE *in = fopen(fname, "r");
  //If file reading fails, abort
  if (!in) {
    return send_message(cfd, "SERVER 404 Not Found\n");
  }

  //Nothing went wrong, read out the file
  char output[500] = {'\0'};
  char *roaming = output;
  while(!feof(in)) {
    *roaming++ = fgetc(in);
  }
  send_message(cfd, "SERVER 200 OK\n\n");
  send_message(cfd, output);
  send_message(cfd, "\n\n\n");
  fclose(in);
  return 1;
}

/**
* Opens or creates a file for writing.
* Takes in client file descriptor and a pointer to a file name.
*/
int put_file(int cfd, char *fname)
{
  //If no name was given, abort
  if (*fname=='\0') {
    return send_message(cfd, "SERVER 501 Put Error\n");
  }

  FILE *out = fopen(fname, "w");
  //If file writing fails, abort
  if (!out) {
    return send_message(cfd, "SERVER 501 Put Error\n");
  }
  out_file = out;
  writing = 2;
  return writing;
}

/**
* Looks for a given 3 letter word in a string.
* Takes in the substring to look for, the string to look in, and the offset to start with.
*/
int look_for(char *goal, char *str, int offset)
{
  if (tolower(str[offset]) == goal[0]) {
    if (tolower(str[offset+1]) == goal[1]) {
      if (tolower(str[offset+2]) == goal[2]) {
        return 1;
      }
    }
  }
  return 0;
}

/**
* Performs a command based on an input.
* Takes in client file descriptor and the string to perform.
*/
int perform_command(int cfd, char *str)
{
  //get rid of any newline characters. Annoying.
  char *i = str;
  while(*i != '\0') {
    if (*i == '\n') {
      *i = '\0';
    }
    i++;
  }

  //check for any commands to execute
  for (int i = 0; i < 98; i++) {
    if (look_for("bye", str, i)) {
      return -1;
    }
    if (look_for("ext", str, i)) {
      return -2;
    }
    if (look_for("get", str, i)) {
      return get_file(cfd, str+i+4);
    }
    if (look_for("put", str, i)) {
      return put_file(cfd, str+i+4);
    }
  }
}

/**
* Helper function to create our server socket.
* Takes in a desired int port.
* Returns the socket file descriptor.
*/
int create_server(int port)
{
  printf("Establishing server...\n");
  //create the socket for IPv4
  int sfd = socket(AF_INET, SOCK_STREAM, 0);
  if (sfd == -1) {
    printf("There was an error creating socket.\n");
    exit(0);
  }

  //construct our desired address with the port given in.
  struct sockaddr_in addr;
  addr.sin_family = AF_INET;
  addr.sin_addr.s_addr = INADDR_ANY;
  addr.sin_port = htons(port);

  //bind our socket to the address.
  if (bind(sfd, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
    printf("There was an error binding socket.\n");
    shutdown(sfd, SHUT_RDWR);
    exit(0);
  }

  printf("Established!\n");

  return sfd;
}

/**
* Helper function to find our client socket.
* Takes in the socket file descriptor.
* Returns the client file descriptor.
*/
int greet_client(int sfd)
{
  //listen for potential clients
  if (listen(sfd, SOMAXCONN) < 0) {
    printf("Error listening for connections.\n");
    shutdown(sfd, SHUT_RDWR);
    exit(0);
  }

  struct sockaddr_in client_addr;
  int addrlen = sizeof(client_addr);

  //connect to our client
  int cfd = accept(sfd, (struct sockaddr *)&client_addr, (socklen_t *)&addrlen);

  //something went horribly wrong
  if (cfd , 0) {
    printf("Error accepting connection.\n");
    shutdown(sfd, SHUT_RDWR);
    exit(0);
  }

  //otherwise, carry on
  send_message(cfd, "HELLO\n");
  return cfd;
}

/**
* The main function should be able to accept a command-line argument
* argv[0]: program name
* argv[1]: port number
*
* Read the assignment handout for more details about the server program
* design specifications.
*/
int main(int argc, char *argv[])
{
  if (argc != 2 || atoi(argv[1])<1024) {
    return -1;
  }

  int sfd = create_server(atoi(argv[1]));
  printf("Server started with sfd = %d\n", sfd);

  int run_server = 1;
  //run the server loop until manually closed
  while(run_server) {
    //look for new client connections
    int cfd = greet_client(sfd);
    printf("Client connected with cfd = %d\n", cfd);

    int hold_client = 1;
    //run the client loop until manually closed
    while (hold_client) {
      //recieve a command from the client and perform it
      char rec[100] = {'\0'};
      recieve_message(cfd, rec);

      //when put command in effect, until double lines entered
      if (writing) {
        put_line(cfd, rec);
        continue;
      }

      //normally, look for commands to perform
      int cont = perform_command(cfd, rec);
      //if the command disconnects the user, disconect them
      if (cont < 0) {
        hold_client = 0;
        //if the command shuts down the server, shut it down
        if (cont == -2) {
          run_server = 0;
        }
      }

    }
    printf("Client disconnected.\n");
    shutdown(cfd, SHUT_RDWR);
  }

  printf("Closing server...\n");
  return shutdown(sfd, SHUT_RDWR);
}
