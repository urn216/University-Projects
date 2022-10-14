#include<stdio.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/wait.h>
#include<stdlib.h>

int main()
{
  int pid, ret, sta;
  // 1) Call fork() and assign its return value to pid
  pid = fork();

  switch (pid) {

    case -1:
    if (pid<0) {
      printf("Error\n");
      // 2) Call exit() with status value of 1
      exit(1);
    }
    break;

    case 0:
    // 3) Use execl() to execute command ps -A, and assign its return value to ret
    ret = execl("/bin/ps", "ps", "-A", NULL);
    printf ("Error executing exec\n");
    break;

    default:
    // 4) Call wait(), use sta variable to store status information, and assign return value to pid
    pid = wait(&sta);

    // 5) If WIFEXITED(sta) is set, print parent process id, child process id and termination status of child process.
    //    Do this by replacing the comments below with the appropriate expressions.
    if (WIFEXITED(sta)) {
      printf("%d %d %d\n", getpid(), pid, WEXITSTATUS(sta));
    }
    break;
  }
}
