#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_USERS 100
#define MAX_LINE 256

typedef struct {
    int id;
    char name[50];
    int age;
    float balance;
} User;

void process_user_file(const char* filename) {
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        fprintf(stderr, "Error: Could not open file %s\n", filename);
        return;
    }

    User users[MAX_USERS];
    int userCount = 0;
    char line[MAX_LINE];

    while (fgets(line, sizeof(line), file) != NULL) {
        if (userCount >= MAX_USERS) {
            fprintf(stderr, "Error: Maximum user limit reached.\n");
            break;
        }

        char* token = strtok(line, ",");
        if (token == NULL) continue;
        int id = atoi(token);

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        char name[50];
        strncpy(name, token, sizeof(name));
        name[strcspn(name, "\n")] = 0;

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        int age = atoi(token);

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        float balance = atof(token);

        if (age <= 0 || balance < 0) {
            fprintf(stderr, "Invalid data for user ID %d. Skipping.\n", id);
            continue;
        }

        User user;
        user.id = id;
        strncpy(user.name, name, sizeof(user.name));
        user.age = age;
        user.balance = balance;

        users[userCount++] = user;
    }

    fclose(file);

    printf("Processed %d users successfully.\n", userCount);

    // Display users with balance > 1000
    printf("Users with balance > 1000:\n");
    for (int i = 0; i < userCount; i++) {
        if (users[i].balance > 1000.0f) {
            printf("ID: %d, Name: %s, Age: %d, Balance: %.2f\n",
                   users[i].id, users[i].name, users[i].age, users[i].balance);
        }
    }

    // Calculate average age
    int totalAge = 0;
    for (int i = 0; i < userCount; i++) {
        totalAge += users[i].age;
    }
    float averageAge = userCount > 0 ? (float)totalAge / userCount : 0;
    printf("Average age of users: %.2f\n", averageAge);
}