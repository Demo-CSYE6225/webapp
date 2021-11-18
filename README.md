# webapp
Web Application - Assignment 5

Prerequisites:
1. Postgres SQL database
2. IDE to run the java application

Instructions to run:

1. Clone the repo to a local machine and open the folder in your IDE.
2. Run SpringMvcRestApplication
3. Hit the API's with GET, PUT, POST on URL - /v1/user/self
4. GET - to get user information(authenticated) 
            No request body required
5. PUT - to update user information(authenticated)  
   Request body example : {
   "first_name": "Jane",
   "last_name": "Doe",
   "password": "skdjfhskdfjhg",
   "username": "jane.doe@example.com"
   }
6. POST - to create a user(no authentication required)
   Request body example : {
   "first_name": "Jane",
   "last_name": "Doe",
   "password": "skdjfhskdfjhg",
   "username": "jane.doe@example.com"
   }
