# Cloud storage  

![Cloud storage](https://wallpapers.com/images/hd/informational-cloud-storage-technology-3d-chart-3vp1xzlt95g64f3x.jpg)


## REST service
REST interface for uploading files and listing the user's uploaded files.
All requests to the service require authorization.
A web application (FRONT) is connected to the developed service and provides a UI for authorization, downloading and displaying a list of user files.  
*[Front application link](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend)* Cloud API Specification is also available on this link or in project file *specification.yml*   


## User authorization
The FRONT application uses the auth-token header, in which it sends a token (key-string) to identify the user on the BACKEND. To get a token, you need to go through authorization on BACKEND and send login and password to the /login method. In case of successful validation, the BACKEND response should return a json object with the auth-token field and the value of the token. All further requests from FRONTEND, except for the /login method, are sent with this header. To exit the application, you need to call the BACKEND /logout method, which will delete / deactivate the token. Subsequent requests with this token will be unauthorized and will return a 401 unauthorized code.

**JWT Token**  
The REST service (backend) uses a JWT token for authorization and checks the rights and validity of the token on each request of the Web application, except for the login endpoint

#### Available JWT settings

| setting | description                           | default value                                                                         |
|---------|---------------------------------------|---------------------------------------------------------------------------------------|
| secret   | app passphrase to generate token key  | application-cloud-storage-secure-jwt-auth-secret-word-for-user-authentication-service |
| expired   | key operation time in milliseconds    | 3600000                                                                               |
| header   | expected token name in request header | auth-token                                                                            |
| bearer   | Bearer name in token                  | Bearer                                                                         |
| issuer   | issuer name for token                 | Cloud Storage                                                                        |


### Endpoint's list
| endpoint | method | description                         |
|----------|--------|--------------------------------|
| login    | post   | авторизация по логину и паролю |
| logout   | post   | выход из аккаунта пользователя |
| file     | get    | Dowload file from cloud        |
| file     | put    | Edit file name                 |
| file     | post   | Upload file to server          |
| file     | delete | Delete file                    |
| list     | get    | Get all user files             |


### File upload by User

Depending on the settings, 
files can be uploaded both to the database and to folders. 
Duplicate names of uploaded files are not allowed. 
To upload a file twice, user must first rename it

####  Available Upload settings
| setting     | value                | default value | description                     |
|-------------|----------------------|---------------|---------------------------------|
| to-database | true/false           | true          | uploading files to the database |
| to-folder   | true/false           | true          | uploading files to folder       |
| upload-folder  | folder name (String) | user-files    |    folder name for uploading files of all users   |
| remove-on-delete  | true/false           | false         |      |
| allow-types | list or empty      | empty         |   delete files or hide from the user   |

**Upload-folder**
In the application settings, only the shared folder for saving files uploaded by users is specified. 
When uploading files to a folder, first a folder with a user ID will be created, then a subfolder with the current date in which the uploaded file will be saved

**Remove-on-delete**
The REST service provides for storing the status of a file and, if necessary, files can be marked as 'Deleted' without removing the record from the database.

**Allow-types**
If nothing is specified in this setting, the application will fart (allow) all types of files by the user (bad practice). To limit the types of files you can upload, list all file types here. which can be downloaded

### Errors
The backend application has a preset set of errors

#### List of identifiers for error handling

| ERROR                      | ID  |
|----------------------------|-----|
| LOGIN_CREDENTIALS          | 101 |
| LOGIN_EMPTY_LOGIN_PASSWORD | 102 | 
| USER_NOT_FOUND             | 103 | 
| TOKEN_INVALID              | 104 | 
| TOKEN_EXPIRED              | 105 | 
| UNSUPPORTED_TOKEN          | 106 | 
| TOKEN_SIGN_INVALID         | 107 |
| TOKEN_IS_NOT_JWT           | 108 | 
| JWT_AUTHENTICATION_INVALID | 109 |
| INVALID_ARGUMENTS_DATA     | 110 | 
| UPLOAD_FILE_SIZE_EXCEEDED  | 111 |
| UPLOAD_FILE_TO_FOLDER_ERROR| 112 |



### Technologies
- Spring Boot 
- Hibernate
- JPA
- Jsonwebtoken (JWT)
- Liquibase
- Docker
- JUnit5
#### Databases
- Postgresql to securely store users and users files. *The better solution for production would be to add an authorization server)*
- Redis for quick validation of authorization tokens and storage of redeemed tokens after logout *(fast and simple - in-memory blacklist )**

### Authorization steps using the Blacklist (principles and logic)
Login
- Frontend  
  - send json with login and password to endpoint login
- Backend
    - validate json data -> authenticate user -> return new authorization token
  
Crud operations
- Frontend
    - send authorization token and indication of the desired operation
- Backend
  - validate json data
  - validate token 
  - checks token in the black list 
  - return operation method result or specified error

Logout
- Frontend
    - send post request with empty body and authorization token to endpoint */logout*
- Backend
    - authenticate user
    - validate token 
    - logout user 
    - on success logout - adds the received token to the blacklist *(from this moment current token will not be valid for authorization even if it has not yet expired)*

  
### First start
The first time you run the application, 
Liquibase will create all the necessary tables for work and add demo user data.

#### Database tables
| Table                 | Description                  |
|:----------------------|:-----------------------------|
| users                 | User data                    |   
| roles                 | Application user roles       |   
| user_roles            | User-Role relationship Table |   
| user_files            | File data with user id       |   
| databasechangelog     | Liquibase service table      |  
| databasechangeloglock | Liquibase service table      |  


### Demo user data for tests
| id  | login          | password  |
|:---:|:---------------|:----------|
|  1  | user@user.com  | password  |
|  2  | test@test.com  | password  |


### REST service Install (backend)

**Must have to start:**
- Docker [(docs)](https://www.docker.com) 
- Maven ([docs](https://www.baeldung.com/install-maven-on-windows-linux-mac))
- Download or clone current project

**How to install Application**

- Go to folder with this Project on your computer and open Terminal  
- Create docker image with Application - run this command 
  - <code>mvn clean package -Dmaven.test.skip=true</code>  
- Create docker container with Application and Databases - type in terminal
  - <code>docker-compose up</code>

**That's all! Open Web Application in your browser and start clouding!**  

*Front Web Application [install guide](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend)*

 

