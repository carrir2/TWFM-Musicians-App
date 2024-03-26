# Backend
This is the backend of the Market Musician Application

### Contributor: 
[Ryan Carrido](https://github.com/carrir2)

# Installation

## First Steps:
### 1: Install the following
1. [Spring Boot](https://docs.spring.io/spring-boot/docs/1.0.0.RC5/reference/html/getting-started-installing-spring-boot.html)
1. [Maven](https://maven.apache.org/install.html)
1. [Java 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
1. MailDev (npm install -g maildev) (Local Method)
1. [PostgreSQL](https://www.postgresql.org/download/) (Local Method)
1. [pgAdmin](https://www.pgadmin.org/download/)

### 2: Edit application.yml file
1. The application.yml file is used for configuring what database and email server the application to use. Can be found here (\src\main\resources\application.yml)
1. Change the spring->datasource->url to the the database of your choice.   
For example, if you created a local database through PostgreSQL, change the url to  
jdbc:postgresql://localhost:5432/\[table name here\]
1. Next change the username and password used to access the database. It's important the user has full permissions to make changes to the database.
1. Under spring->jpa->hibernate->ddl-auto, change none to create-drop when running for the first time. This will auto generate all the tables in the database.
1. For the mail server, we go to spring->mail, where we make changes to the host, port, username, and password.  
If we wanted to use maildev, we can make the following changes:  
    host: localhost  
    port: 1025  
    username: hello  
    password: hello  
If we wanted to use the Gmail servers, please follow [here](https://www.youtube.com/watch?v=ugIUObNHZdo).

### 3: Change email in Email Sender
1. Go to (src\main\java\com\example\login\email\EmailSender.java)
1. Change the emails in both functions to the email that will be used for sending. If using maildev, the email can be whatever.

### 4: Change Client ID for Google OAuth
1. Go to (src\main\java\com\example\login\appuser\AppUserService.java)
1. Find the function googleLogin().
1. Change the Client ID on line 185 for Google OAuth to work.
.setAudience(Collections.singletonList("\[CLIENT ID\]"))
1. For more info follow [here](https://developers.google.com/identity/gsi/web/guides/overview)

## Running the Application
1. If using MailDev, make sure it's running.
1. If using PostgreSQL locally, make sure it's running. 
1. Start the application through (src\main\java\com\example\login\LoginApplication.java)  
Make sure there are no errors and the following message appears:  
com.example.login.LoginApplication       : Started LoginApplication in 6.791 seconds (JVM running for 7.513)
1. If successful, go back to application.yml and change value of ddl-auto to none.

## Deployment
Please follow this [tutorial](https://www.youtube.com/watch?v=q25jgAZTTsU&list=WL&index=105) for deployment.