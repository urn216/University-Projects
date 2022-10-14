#include "editor.h"

int editor_insert_char(char editing_buffer[], int editing_buflen, char to_insert, int pos) {
  //if there's an out of bounds issue, return false
  if (pos >= editing_buflen || pos < 0) {return 0;}
  //insert the character. Shift everything along if not inserting into a '\0' character
  if (editing_buffer[pos] != '\0') {
    for (int i = editing_buflen-1; i > pos; i--) {
      editing_buffer[i] = editing_buffer[i-1];
    }
  }
  editing_buffer[pos] = to_insert;
  return 1;
}

int editor_delete_char(char editing_buffer[], int editing_buflen, char to_delete, int offset) {
  //if there's an out of bounds issue, return false
  if (offset >= editing_buflen || offset < 0) {return 0;}
  int boolean = 0;
  for (int i = offset; i < editing_buflen; i++) {
    //first, find the location of the first occurence of to_delete.
    if (boolean==0) {
      if (editing_buffer[i] == to_delete) {
        boolean = 1;
      }
    }
    //then, continue from that point, moving each char back a space, to the end of the array.
    //Assuming all strings input have '\0' as a final character.
    else {
      editing_buffer[i-1] = editing_buffer[i];
    }
  }
  //return whether or not it successfully found a to_delete.
  return boolean;
}

int editor_replace_str(char editing_buffer[], int editing_buflen, const char *str, const char *replacement, int offset) {
  //if there's an out of bounds issue, return -1.
  if (offset >= editing_buflen || offset < 0) {return -1;}
  int ans = -1;
  int boolean = 0;
  int str_len = 0;
  int rep_len = 0;
  int test_offset = -1;
  for (int i = offset; i < editing_buflen; i++) {
    if (boolean == 0) {
      if (test_offset == -1) {
        //Firstly, find a possible match for str in the buffer.
        if (editing_buffer[i] == str[0]) {test_offset = i;}
      }
      else {
        //Then, check to see if it's an exact match, otherwise go back and start again.
        if (str[i-test_offset] == '\0') {
          boolean = 1;
          i = test_offset;
        }
        else if (str[i-test_offset] != editing_buffer[i]) {
          i = test_offset;
          test_offset = -1;
          str_len = 0;
        }
        str_len++;
      }
    }
    if (boolean == 0) {continue;}
    //Once we have an exact match, start replacing characters.
    if (replacement[i-test_offset] == '\0') { //if we run out of replacement characters to add.
      ans = test_offset+rep_len-1;
      if (rep_len >= str_len) {return ans;} //Success, no more work required. All parts of str are gone.
      //Otherwise, remove characters from the end of str instance in buffer, until only replacement remains.
      else {
        for (int j = 0; j < str_len-rep_len; j++) {
          for (int k = i+1; k < editing_buflen; k++) {
            editing_buffer[k-1] = editing_buffer[k];
          }
        }
        return ans;
      }
    }
    else {
      //If the replacement is longer than str, then move all the remaining characters over by one to make space for replacement charaters.
      if (i-test_offset >= str_len && editing_buffer[i] != '\0') {
        for (int j = editing_buflen-1; j > i; j--) {
          editing_buffer[j] = editing_buffer[j-1];
        }
      }
      editing_buffer[i] = replacement[i-test_offset];
      rep_len++;
    }
  }
  return ans; //Return the index of the last character of replacement. Or -1 if something goes wrong.
}

//Using my compiler, it did not like assigning vewing_buffer[rows][cols] with non constant array sizes. To get around this, I used a fixed size of [8][11].
//This should work the same for questions in the format of the examples provided, but in case of any other array size, simply replace 8 with rows and 11 with cols
//I spent a long time googling and asking people, I tried gcc on another's computer, nothing allowed me to compile. This was the best I could do. I'm very sorry.
//
//In the event I've reverted the change for submission, this may explain away some weird behavior. I was unable to test adequately.
void editor_view(int rows, int cols, char viewing_buffer[rows][cols], const char editing_buffer[], int editing_buflen, int wrap) {
  int r = 0; //active row
  int c = 0; //active col
  for (int i = 0; i < editing_buflen; i++) {
    //In case of new line character, go to next row. Abort if out of new rows.
    if (editing_buffer[i]=='\n') {
      r++;
      if (r >= rows) {return;}
      c = 0;
    }
    //otherwise, add characters to the viewing_buffer
    else {
      if (wrap == 0) {
        //when not wrapping, only need to stop appending characters once the end of the line is reached.
        if (c < cols-1) {
          viewing_buffer[r][c] = editing_buffer[i];
          c++;
        }
      }
      else{
        //when wrapping, once off the end of the line, move to a new row. Otherwise just keep appending characters
        if (c >= cols-1) {
          r++;
          if (r >= rows) {return;}
          c = 0;
        }
        viewing_buffer[r][c] = editing_buffer[i];
        c++;
      }
    }
  }
}
