<h1 align="center">📧 Email Client / Email Wrapper</h1>

<p align="center">
A simple web-based Email Client that allows users to register, log in, and manage emails with a clean and modern interface.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-Backend-red">
  <img src="https://img.shields.io/badge/SpringBoot-Framework-green">
  <img src="https://img.shields.io/badge/HTML-Frontend-orange">
  <img src="https://img.shields.io/badge/CSS-UI-blue">
  <img src="https://img.shields.io/badge/JavaScript-Logic-yellow">
  <img src="https://img.shields.io/badge/Status-Completed-brightgreen">
</p>

---

# 📌 Project Description

This project is a simple **Email Client System** developed using **Java and Spring Boot**.  
The system allows users to register, log in, and manage their emails through a user-friendly web interface.

Users can compose new emails, view received emails in the inbox, check sent emails, and save drafts.

The goal of this project is to simulate the basic functionality of modern email platforms such as Gmail using a simplified architecture.

---

# 🚀 Features

✔ User Registration  
✔ User Login Authentication  
✔ Compose Email  
✔ Inbox Mail View  
✔ Sent Mail Section  
✔ Draft Mail Section  
✔ Logout System  
✔ Clean and Modern UI  

---

# 🖼 Project Screenshots

### Login Page

![Login Page](screenshots/login.png)

### Email Dashboard

![Inbox](screenshots/inbox.png)

---

## 🛠 Technologies Used

* **Backend:** Java, Spring Boot
* **Frontend:** HTML, CSS, JavaScript
* **Data Storage:** JSON (used for local data storage)
* **Build Tool:** Maven

---

# ⚙ Installation Guide

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/SoniaAkterown/Email-Client.git

2️⃣ Build the Project
mvn clean install

3️⃣ Run the Application
mvn spring-boot:run

4️⃣ Open in Browser
After running the application, open your browser and go to:
http://localhost:8080



## 📂 Project Structure
Email-Client
│
├── src
│   └── main
│       ├── java
│       │   ├── controllers
│       │   ├── services
│       │   └── models
│       │
│       └── resources
│           ├── static (CSS/JS files)
│           ├── templates (HTML files)
│           └── application.properties
│
├── users.json
├── emails.json
├── pom.xml
├── LICENSE
└── README.md

