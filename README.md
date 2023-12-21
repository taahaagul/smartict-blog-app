# smartict-blog-app

This project aims to provide a platform where users can register, log in and share text-based posts.

Prepared for SmartICT company.

## Features

- User registration and login with JWT authentication
- Password encryption using BCrypt
- Role-based authorization with Spring Security
- Customized access denied handling
- Logout mechanism
- Refresh token
- Email Validation
- Change Password
- Forget My Password
- Global Exception Handler
- Post Operations
- User Operations

## Technologies

- [Spring Boot 3.0]
- [Spring Security]
- [JSON Web Tokens (JWT)]
- [BCrypt]
- [Maven]
- [mySQL]
- [MailSender]
- [OpenAPI]

## Getting Started

To get started with this project, you will need to have the following installed on your local machine:

- JDK 17+
- Maven 3+
  
To build and run the project, follow these steps:

- Clone the repository: git clone https://github.com/taahaagul/smartict-blog-app.git
- I use the "Mailtrap" for Email Delivery Platform, you can set your credentials in application.yml file.
- Add database "smartict_blog"
- When users sign up, they must click on the link sent to their emails to activate their accounts.
Otherwise, the login process cannot be completed.
- The access token expires in 15 minutes, and the refresh token expires in 30 minutes.
- For the 'Forgot Password' section, a verification token is sent to users' emails.
Users can reset their passwords using this token within 4 minutes. Otherwise, the process will be rejected.
- This platform supports Method Level Security. There are 2 roles --> USER, ADMIN
- Everyone has the USER role by default when first registering on the platform
- When the program runs for the first time, there is no ADMIN role in the system.
You need to manually assign this role to the person of your choice once.
After that, you can perform ADMIN operations with this user and grant ADMIN permissions to other users as needed.

-> The application will be available at http://localhost:8080.

-> All APIs of the platform are documented with Swagger. 
You can access it through this address: http://localhost:8080/swagger-ui/index.html

Thank you for your time.
