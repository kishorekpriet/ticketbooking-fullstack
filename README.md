# 🎬 Full-Stack Cinema Booking System 

A complete, enterprise-grade full-stack web application for booking movie tickets. Built with React on the frontend and a Java Spring Boot REST API on the backend, integrated with a MySQL database.

## 🚀 Key Features

* **Concurrency Protection (Optimistic Locking):** Engineered the database layer to actively prevent race conditions. If two users attempt to book the exact same seat at the exact same millisecond, the backend rejects the duplicate transaction and prevents data corruption.
* **Automated Data Management (Cron Scheduling):** Implemented a Spring Boot `@Scheduled` worker that automatically purges old ticket receipts and resets all cinema seats to "available" on a daily 24-hour cycle.
* **Dynamic State Management:** The React frontend uses `useEffect` and asynchronous `fetch` calls to sync perfectly with the live database, instantly graying out booked seats to prevent user errors.
* **Responsive UI/UX:** A fully responsive, mobile-friendly seat matrix built from scratch using CSS Flexbox, mirroring the authentic BookMyShow cinematic UI.

## 🛠️ Tech Stack

* **Frontend:** React.js, Vite, JavaScript, Custom CSS
* **Backend:** Java Spring Boot, Spring Data JPA, Hibernate REST API
* **Database:** MySQL

## ⚙️ How to Run Locally

### 1. Database Setup
Create a MySQL database named `ticketbooking_db` running on `localhost:3306`. Update the `application.properties` file in the backend with your MySQL username and password.

### 2. Start the Backend (Spring Boot)
Navigate to the `backend` folder and run your application via your IDE (Eclipse/IntelliJ) or using Maven:
\`\`\`bash
cd backend
./mvnw spring-boot:run
\`\`\`
*The API will start on Port 8081.*

### 3. Start the Frontend (React)
Open a new terminal, navigate to the `frontend` folder, install the dependencies, and start the Vite development server:
\`\`\`bash
cd frontend
npm install
npm run dev
\`\`\`
*The UI will start on Port 5173.*
