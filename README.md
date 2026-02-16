# Blog App Backend

A production-ready backend system for a Blog Application built with **Spring Boot**, featuring secure JWT authentication, OTP verification, and AWS S3 cloud storage integration.

---

## 📋 Overview

This project implements a complete backend solution with:
- **JWT-based authentication** (Access + Refresh Tokens)
- **Multi-channel OTP verification** (Email + Phone)
- **Cloud media storage** (AWS S3)
- **RESTful APIs** for blog management
- **Secure password handling** with reset flows
- **Profile management** and account deletion

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Java 17+ |
| **Framework** | Spring Boot |
| **Security** | Spring Security + JWT |
| **ORM** | Spring Data JPA / Hibernate |
| **Database** | MySQL 8.0+ |
| **Cloud Storage** | AWS S3 |
| **Build Tool** | Maven |

---

## ✨ Features Implemented

### Authentication & Security
- ✅ User Registration with Aadhaar image upload
- ✅ Email & Phone OTP Verification
- ✅ JWT Authentication (Access + Refresh Tokens)
- ✅ Refresh Token Flow for session continuity
- ✅ Password Reset & Change
- ✅ Secure password encoding (BCrypt)

### User Management
- ✅ Profile Management (Update user details)
- ✅ Hard Account Deletion with password confirmation
- ✅ Email/Phone change with re-verification

### Blog Features
- ✅ Post Creation & Management (CRUD)
- ✅ Comments System
- ✅ Likes System
- ✅ Search & Filter Posts

### Media Handling
- ✅ AWS S3 Media Upload
- ✅ S3 URL storage in database
- ✅ Image validation and processing

---

## 🏗️ Project Structure
```
Blog-App/
│
├── Auth-Service/                    # Authentication microservice
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
│
├── Blog-Service/                    # Blog management microservice
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
│
├── README.md
└── .gitignore
```

---

## ⚙️ Setup Instructions

### Prerequisites
- Java 17+ installed
- Maven 3.8+ installed
- MySQL 8.0+ running
- AWS S3 bucket created

### Step 1: Clone the Repository
```bash
git clone https://github.com/yourusername/blog-app-backend.git
cd blog-app-backend
```

### Step 2: Create Database
```sql
CREATE DATABASE blogdb;
```

### Step 3: Configure Application Properties

#### For Auth Service
Navigate to the Auth-Service resources directory:
```bash
cd Auth-Service/src/main/resources/
```

rename `application.properties.sample` to  `application.properties` 


#### For Blog Service
Navigate to the Blog-Service resources directory:
```bash
cd Blog-Service/src/main/resources/
```

rename `application.properties.sample` to  `application.properties` 


### Step 4: Install Dependencies

For Auth Service:
```bash
cd Auth-Service
mvn clean install
```

For Blog Service:
```bash
cd Blog-Service
mvn clean install
```

---

## 🚀 Running the Application

### Run Auth Service
```bash
cd Auth-Service
mvn spring-boot:run
```
**Default Port:** `8081`  
**Base URL:** `http://localhost:8081/api/v1/auth`

### Run Blog Service
```bash
cd Blog-Service
mvn spring-boot:run
```
**Default Port:** `8082`  
**Base URL:** `http://localhost:8082/api/v1/blog`

---

## 🔐 Authentication Flow

### JWT Token Structure
- **Access Token:** Short-lived (15-30 minutes)
  - Used for API authentication
  - Contains userId as principal
  
- **Refresh Token:** Long-lived (7-30 days)
  - Used to generate new access tokens
  - Stored in database for validation

### Token Usage
```http
Authorization: Bearer <access_token>
```

### Refresh Token Flow
1. When access token expires, client calls `/refresh`
2. Server validates refresh token from database
3. New access token is issued
4. Session continues without re-login

---

## 🔒 Security Features

### Implemented
- ✅ JWT-based stateless authentication
- ✅ BCrypt password hashing
- ✅ Refresh token rotation
- ✅ OTP verification (Email + Phone)
- ✅ Sensitive data excluded from Git
- ✅ SQL injection prevention (JPA)
- ✅ Password reset with OTP verification

### Security Notes
- `application.properties` is ignored via `.gitignore`
- **Never commit secrets** to version control
- **Rotate credentials immediately** if exposed
- JWT uses `userId` as principal for minimal token size
- Refresh token endpoint implemented for secure session continuity

---

## 🔧 Configuration Checklist

Before running the application, ensure:

- [ ] MySQL database created (auth_db and blog_db)
- [ ] `application.properties` created in both services
- [ ] Database credentials configured correctly
- [ ] JWT secret key set (same in both services)
- [ ] AWS S3 bucket created and credentials added
- [ ] SMTP server configured for email OTP
- [ ] SMS service configured for phone OTP (if applicable)
- [ ] Dependencies installed (`mvn clean install`)

---

## 🐛 Troubleshooting

### Common Issues

**Database Connection Failed:**
```
Solution: Verify MySQL is running and credentials are correct
Check if databases auth_db and blog_db exist
```

**JWT Token Invalid:**
```
Solution: Ensure jwt.secret matches exactly between Auth and Blog services
```

**S3 Upload Failed:**
```
Solution: Check AWS credentials and bucket permissions
Verify bucket name and region are correct
```

**OTP Not Received:**
```
Solution: Verify SMTP/SMS service credentials
Check spam folder for email OTP
Ensure email app password is generated (not regular password)
```

**Port Already in Use:**
```
Solution: Change server.port in application.properties
Or stop the process using that port
```

---

## 📚 Future Enhancements

- [ ] API Gateway integration
- [ ] Docker containerization
- [ ] API documentation (Swagger)
- [ ] Role-Based Access Control (RBAC)
- [ ] Redis caching for tokens
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Centralized logging
- [ ] WebSocket for real-time notifications

---

## 👨‍💻 Author

**Sourabh Singh Mandloi**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=flat&logo=linkedin)](https://linkedin.com/in/sourabh-singh-mandloi)

---

## 📄 License

This project is licensed under the MIT License.

---

## 🙏 Acknowledgments

- Spring Boot Team
- AWS SDK Contributors
- JWT.io Community

---

**⭐ Star this repository if you find it helpful!**
