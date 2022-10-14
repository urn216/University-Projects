#include <stdio.h>
#include "dbms.h"

int editor_insert_char(char *editing_buffer, int editing_buflen, char to_insert, int pos) {
  // If there's an out of bounds issue, return false.
  if (pos >= editing_buflen || pos < 0) {return 0;}
  // Insert the character. Shift everything along if not inserting into a '\0' character.
  if (*(editing_buffer + pos) != '\0') {
    for (int i = editing_buflen - 1; i > pos; i--) {
      *(editing_buffer + i) = *(editing_buffer + i - 1);
    }
  }
  *(editing_buffer + pos) = to_insert;
  return 1;
}

int elem_string(const char *input, char *output, const int desired_size) {
  // Copy input into output until either array ends.
  for (int i = 0; i < desired_size; i++) {
    *(output + i) = *(input + i);
    if (*(input + i) == '\0') {break;}
  }
  // Make sure the string is terminated properly.
  *(output + desired_size) = '\0';
  // As long as there's extra space, insert spaces using method from Assignment 1.
  while(*(output + desired_size - 1) == '\0') editor_insert_char(output, desired_size, ' ', 0);
  return 1;
}

/**
 * Function for Task 1.
 */
int db_show_row(const struct db_table *db, unsigned int row) {
  // If the given row is invalid in some way - less than 0 or greater than any row present in the table - return 0.
  if (row < 0 || row >= db->rows_used) {return 0;}
  // Otherwise, the given row is legal and we can print out the given row.
  struct album *album = &db->table[row];
  char id[7] = {"\0"};
  char title[21] = {"\0"};
  char artist[21] = {"\0"};
  char year[5] = {"\0"};
  // Convert non char arrays to char arrays.
  sprintf(id, "%d", album->id);
  sprintf(year, "%d", album->year);
  // Construct the elements.
  elem_string(id, id, 6);
  elem_string(album->title, title, 20);
  elem_string(album->artist, artist, 20);
  elem_string(year, year, 4);
  // Print the elements in the right order.
  printf("%s:%s:%s:%s\n", id, title, artist, year);
  return 1;
}

/**
 * Function for Task 2.
 */
int db_add_row(struct db_table *db, struct album *a) {
  // First, check if we don't need to expand the size of the array.
  if (db->rows_used < db->rows_total) {
    // If we do, we then check to see if the table of albums is null.
    if (db->table == NULL) {
      // If it is, make a new table and enter the element into it.
      struct album *table = {a};
      db->table = table;
    }
    else {
      // Otherwise, simply add the new element.
      db->table[db->rows_used] = *a;
    }
    db->rows_used++;
    return 1; // Success.
  }
  // We need to expand the size of the array.
  // check to see if adding extra space is possible. Return 0 if not.
  unsigned int max_int = -5;
  if (db->rows_total > max_int) return 0;
  // Otherwise, expand the size of the array by 5 and then start the method over again.
  db->rows_total+=5;
  return db_add_row(db, a);
}

/**
 * Function for Task 3.
 */
int db_remove_row(struct db_table *db, unsigned long id) {
  // First, check that the given id exists within the database.
  int found = 0;
  for (int i = 0; i < db->rows_used; i++) {
    if (db->table[i].id == id) {
      found = i;
      break;
    }
  }
  // If we do not find the album in question, return 0.
  if (found <= 0) return 0;
  // Once we know where the album to remove is, remove it by shifting every album to the right of it one step to the left.
  for (int i = found+1; i < db->rows_used; i++) {
    db->table[i-1] = db->table[i];
  }
  db->rows_used -= 1;
  // If there's a gap of five or more between the rows used and total, lover the total rows by five.
  if (db->rows_total-db->rows_used >= 5) db->rows_total -= 5;
  return 1;
}
