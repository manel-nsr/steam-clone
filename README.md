# 🎮 Steam Clone

A full-featured Steam-inspired gaming platform web application built with Java Spring Boot.

## 📋 Table of Contents

- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [API Endpoints](#-api-endpoints)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### 🛒 Core Functionality

- **User Authentication & Authorization** - Secure login/register system with Spring Security
- **Game Store** - Browse and discover games with detailed information
- **Shopping Cart** - Add games to cart and manage purchases
- **User Library** - View purchased games in personal library
- **Community Features** - User posts and community interaction
- **User Profiles** - Personalized user profiles and settings

### 🎯 Key Components

- **Game Management** - Complete CRUD operations for games and categories
- **Purchase System** - Handle game purchases and transaction history
- **Cart Management** - Real-time cart updates and management
- **Responsive Design** - Mobile-friendly interface
- **Data Import Utility** - Automated data seeding functionality

## 🛠 Technologies Used

### Backend

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database operations
- **Maven** - Dependency management

### Frontend

- **Thymeleaf** - Server-side templating
- **HTML5 & CSS3**
- **Bootstrap** (likely for responsive design)

### Database

- **JPA/Hibernate** - ORM
- **Database** MySQL

## 📁 Project Structure

```
steam-clone/
├── docs/                          # Project documentation & images
├── src/
│   ├── main/
│   │   ├── java/com/example/steam_clone/
│   │   │   ├── config/            # Security configuration
│   │   │   ├── controllers/       # REST controllers
│   │   │   │   ├── GameController.java
│   │   │   │   ├── CartController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── LibraryController.java
│   │   │   │   └── ...
│   │   │   ├── model/            # JPA entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Game.java
│   │   │   │   ├── CartItem.java
│   │   │   │   └── ...
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── service/          # Business logic
│   │   │   ├── util/            # Utility classes
│   │   │   └── SteamCloneApplication.java
│   │   └── resources/
│   │       ├── static/          # CSS, JS, images
│   │       ├── templates/       # Thymeleaf templates
│   │       ├── data/           # Sample data files
│   │       ├── application.properties
│   │       └── application-example.properties
│   └── test/                   # Unit tests
└── target/                     # Maven build output
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Your preferred IDE (IntelliJ IDEA, Eclipse, VS Code)

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/manel-nsr/steam-clone.git
   cd steam-clone
   ```

2. **Set up the database configuration**

   ```bash
   # Copy the example configuration
   cp src/main/resources/application-example.properties src/main/resources/application.properties

   # Edit application.properties with your database settings
   ```

3. **Build the project**

   ```bash
   mvn clean install
   ```

4. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   Open your browser and navigate to `http://localhost:8080`

## ⚙️ Configuration

### Database Setup

Configure your database connection in `application.properties`:

```properties
# Database Configuration (Example)
spring.datasource.url=jdbc:mysql://localhost:3306/steam_clone
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# Other configurations...
```

### Security Configuration

The application uses Spring Security for authentication. Default configuration can be found in `SecurityConfig.java`.

## 🛣 API Endpoints

### Game Management

- `GET /` - Home page with featured games
- `GET /game/{id}` - Game details page
- `GET /store` - Browse all games

### User & Authentication

- `GET /login` - Login page
- `POST /login` - Process login
- `GET /register` - Registration page
- `POST /register` - Process registration
- `GET /profile` - User profile

### Cart & Purchases

- `GET /cart` - View cart
- `POST /cart/add` - Add game to cart
- `POST /purchase` - Process purchase
- `GET /library` - User's game library

### Community

- `GET /community` - Community posts
- `POST /community/post` - Create new post

## 📸 Screenshots

_Add your screenshots from the `docs/` folder here_

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by Steam's user interface and functionality
- Built with Spring Boot framework
- Thanks to the open-source community for the amazing tools and libraries

---

**⭐ If you found this project helpful, please give it a star!**

**🔗 Connect with me:** [GitHub](https://github.com/manel-nsr)
