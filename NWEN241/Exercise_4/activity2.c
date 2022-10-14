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
#include <unistd.h>

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
  char *s = str;
  int new_line = -1;
  int str_end = -1;
  while(*s != '\0') {
    // if (*s == '\n') {
    //   *s = '\0';
    //   str_end--;
    //   new_line++;
    // }
    str_end++;
    s++;
  }

  //check for any commands to execute
  for (int i = 0; i < 98; i++) {
    if (look_for("bye", str, i)) {
      return -1;
    }
    if (look_for("ext", str, i)) {
      return -2;
    }
  }

  char res[100] = {'\0'};
  for (int i = str_end; i >= 0; i--) {
    res[str_end-i] = str[i];
  }
  // if (new_line > 0) {
    // res[str_end+1] = '\n';
  // }
  send_message(cfd, res);
  printf("%s", &res);
  return -2;
}

/**
* Helper function to create our server socket.
* Takes in a desired int port.
* Returns the socket file descriptor.
*/
int create_server(int port)
{
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
  // send_message(cfd, "HELLO\n");
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
int main(void)
{

  int sfd = create_server(23456);

  int run_server = 1;
  //run the server loop until manually closed
  while(run_server) {
    //look for new client connections
    int cfd = greet_client(sfd);

    int hold_client = 1;
    //run the client loop until manually closed
    while (hold_client) {
      //recieve a command from the client and perform it
      char rec[100] = {'\0'};
      recieve_message(cfd, rec);

      //look for commands to perform
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
    shutdown(cfd, SHUT_RDWR);
  }

  // shutdown(sfd, SHUT_WR);
  close(sfd);
  exit(0);
}
