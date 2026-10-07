# 🏎️ F1 Stats - Formula 1 Statistics Backend API

A RESTful API providing real-time Formula 1 statistics, standings, race calendars, and in-depth analytics. Built with Spring Boot and MongoDB for optimal performance and scalability.

## ✨ Features

### Current Features
- 📊 **Live Driver Standings API** - Current season driver championship rankings with points and wins
- 🏆 **Constructor Standings API** - Team championship standings and performance metrics
- 📅 **Race Calendar API** - Complete 2024 season schedule with circuit details
- 🗺️ **Circuit Information** - Detailed circuit data including location and track maps
- ⏱️ **Session Times** - Practice, qualifying, and race timings for each Grand Prix
- 💾 **Data Caching** - MongoDB caching for improved performance
- 🔄 **Auto-sync** - Scheduled data synchronization with F1 API
- 🛡️ **Error Handling** - Robust global exception handling

### Upcoming Features
- 🎮 **Prediction Game API** - Endpoints for race predictions and leaderboards
- 🎯 **Fantasy League API** - Fantasy team management system
- 🤖 **AI-Powered Analytics** - Race predictions and driver comparisons
- 📈 **Historical Data** - Previous seasons statistics and trends
- 🔔 **Live Race Updates** - Real-time race results and standings updates
- 🔐 **Authentication** - JWT-based user authentication

## 🛠️ Tech Stack

### Backend
- **Framework:** [Spring Boot 3.2](https://spring.io/projects/spring-boot)
- **Language:** Java 25
- **Database:** [MongoDB Atlas](https://www.mongodb.com/atlas)
- **HTTP Client:** Spring WebFlux (WebClient)
- **Data Source:** [Jolpica F1 API](https://api.jolpi.ca/ergast/f1) (Ergast replacement)
- **Build Tool:** Maven
- **Utilities:** Lombok, Jackson

### DevOps & Deployment
- **Hosting:** [Railway](https://railway.app/)
- **Database:** MongoDB Atlas (Free Tier)
- **Version Control:** Git & GitHub
- **CI/CD:** Railway Auto-deploy

## 🚀 Quick Start

### Prerequisites
- **Java** 25 or higher
- **Maven** 3.6+
- **MongoDB Atlas** account (free)
- **Git**

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/vj2970/Formula-Grid.git
   cd f1-stats-backend
   ```

2. **Configure MongoDB**
   - Create a free cluster on [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
   - Create a database user with read/write permissions
   - Whitelist your IP address (or use 0.0.0.0/0 for development)
   - Get your connection string
   - Create `src/main/resources/application-dev.yml`:
   ```yaml
   spring:
     data:
       mongodb:
         uri: mongodb+srv://username:password@cluster.mongodb.net/f1stats?retryWrites=true&w=majority
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **API runs at:** `http://localhost:8080`

6. **Test the API**
   ```bash
   curl http://localhost:8080/api/health
   ```

## 📁 Project Structure

```
f1-stats-backend/
├── src/main/java/com/formulagrid/
│   ├── controller/           # REST API endpoints
│   │   ├── DriverController.java
│   │   ├── ConstructorController.java
│   │   ├── RaceController.java
│   │   └── HealthController.java
│   ├── service/              # Business logic layer
│   │   ├── DriverService.java
│   │   ├── ConstructorService.java
│   │   └── RaceService.java
│   ├── repository/           # MongoDB repositories
│   │   ├── DriverRepository.java
│   │   ├── ConstructorRepository.java
│   │   ├── RaceRepository.java
│   │   ├── DriverStandingRepository.java
│   │   └── ConstructorStandingRepository.java
│   ├── model/                # Domain models
│   │   ├── Driver.java
│   │   ├── Constructor.java
│   │   ├── Race.java
│   │   ├── Circuit.java
│   │   ├── DriverStanding.java
│   │   └── ConstructorStanding.java
│   ├── dto/                  # Data Transfer Objects
│   │   ├── request/
│   │   └── response/
│   │       ├── ErgastDriverResponse.java
│   │       ├── ErgastConstructorStandingsResponse.java
│   │       ├── ErgastDriverStandingsResponse.java
│   │       └── ErgastRaceScheduleResponse.java
│   ├── client/               # External API clients
│   │   └── ErgastApiClient.java
│   ├── config/               # Configuration classes
│   │   └── WebConfig.java
│   └── exception/            # Exception handling
│       ├── GlobalExceptionHandler.java
│       ├── ResourceNotFoundException.java
│       └── ExternalApiException.java
├── src/main/resources/
│   ├── application.yml       # Main configuration
│   ├── application-dev.yml   # Development config
│   └── application-prod.yml  # Production config
├── src/test/                 # Unit and integration tests
├── pom.xml                   # Maven dependencies
└── README.md
```

## 🔌 API Endpoints

### Base URL
```
Local: http://localhost:8080
Production: https://formula-grid-backend-production.up.railway.app
```

### Drivers

#### Get All Current Drivers
```http
GET /api/drivers
```

**Response:**
```json
[
  {
    "id": "1",
    "driverId": "max_verstappen",
    "code": "VER",
    "permanentNumber": "1",
    "givenName": "Max",
    "familyName": "Verstappen",
    "dateOfBirth": "1997-09-30",
    "nationality": "Dutch"
  }
]
```

#### Get Driver Standings
```http
GET /api/drivers/standings
```

**Response:**
```json
[
  {
    "position": 1,
    "positionText": "1",
    "points": 575,
    "wins": 19,
    "driver": {
      "driverId": "max_verstappen",
      "givenName": "Max",
      "familyName": "Verstappen",
      "code": "VER"
    },
    "constructor": {
      "constructorId": "red_bull",
      "name": "Red Bull"
    }
  }
]
```

#### Refresh Driver Standings
```http
POST /api/drivers/standings/refresh
```

### Constructors

#### Get Constructor Standings
```http
GET /api/constructors/standings
```

**Response:**
```json
[
  {
    "position": 1,
    "positionText": "1",
    "points": 860,
    "wins": 21,
    "constructor": {
      "constructorId": "red_bull",
      "name": "Red Bull",
      "nationality": "Austrian"
    }
  }
]
```

#### Refresh Constructor Standings
```http
POST /api/constructors/standings/refresh
```

### Races

#### Get Race Calendar
```http
GET /api/races
```

**Response:**
```json
[
  {
    "season": 2024,
    "round": 1,
    "raceName": "Bahrain Grand Prix",
    "circuit": {
      "circuitId": "bahrain",
      "circuitName": "Bahrain International Circuit",
      "locality": "Sakhir",
      "country": "Bahrain",
      "lat": 26.0325,
      "lng": 50.5106
    },
    "date": "2024-03-02",
    "time": "15:00:00",
    "qualifyingDate": "2024-03-01",
    "qualifyingTime": "15:00:00"
  }
]
```

#### Refresh Race Calendar
```http
POST /api/races/refresh
```

### Health Check

#### Service Health Status
```http
GET /api/health
```

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2024-11-10T18:00:00",
  "service": "F1 Stats Backend"
}
```

## 🚢 Deployment

### Deploy to Railway

1. **Create MongoDB Atlas Database**
   - Sign up at [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas)
   - Create a free M0 cluster
   - Create database user with password
   - Whitelist IP: `0.0.0.0/0` (allow from anywhere)
   - Get connection string

2. **Push Code to GitHub**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/yourusername/f1-stats-backend.git
   git push -u origin main
   ```

3. **Deploy on Railway**
   - Create account at [railway.app](https://railway.app)
   - Click **"New Project"** → **"Deploy from GitHub repo"**
   - Select your repository
   - Railway auto-detects Spring Boot

4. **Add Environment Variables**

   Go to your service → **Variables** tab → Add:
   ```
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/f1stats?retryWrites=true&w=majority
   PORT=8080
   LOG_LEVEL=INFO
   ALLOWED_ORIGINS=http://localhost:3000
   ```

5. **Generate Domain**
   - Go to **Settings** → **Networking**
   - Click **"Generate Domain"**
   - You'll get a URL like: `https://f1-stats-backend-production.up.railway.app`

6. **Test Deployment**
   ```bash
   curl https://your-railway-url.up.railway.app/api/health
   ```

### Deploy to Render (Alternative)

1. Create account at [render.com](https://render.com)
2. **New** → **Web Service**
3. Connect GitHub repository
4. Configure:
   - **Name:** f1-stats-backend
   - **Environment:** Java
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/*.jar`
   - **Instance Type:** Free

5. Add Environment Variables (same as Railway)
6. Click **"Create Web Service"**

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=DriverServiceTest
```

### Run with Coverage
```bash
mvn clean test jacoco:report
```

### Integration Testing
```bash
mvn verify
```

### Test API with cURL

**Health Check:**
```bash
curl http://localhost:8080/api/health
```

**Get Driver Standings:**
```bash
curl http://localhost:8080/api/drivers/standings
```

**Get Race Calendar:**
```bash
curl http://localhost:8080/api/races
```

**Refresh Data:**
```bash
curl -X POST http://localhost:8080/api/races/refresh
```

## 📊 Development Roadmap

### Phase 1: Core API ✅ (Completed)
- [x] Spring Boot setup with MongoDB
- [x] Driver standings endpoint
- [x] Constructor standings endpoint
- [x] Race calendar endpoint
- [x] External API integration (Jolpica F1)
- [x] Data caching with MongoDB
- [x] Error handling & logging
- [x] CORS configuration
- [x] Deployment to Railway

### Phase 2: Enhanced Features 🚧 (In Progress)
- [ ] Race results endpoint
- [ ] Qualifying results endpoint
- [ ] Driver details & statistics
- [ ] Historical seasons data (2020-2024)
- [ ] Redis caching layer
- [ ] Scheduled data sync jobs

### Phase 3: User Features 📅 (Planned)
- [ ] User authentication (JWT)
- [ ] User registration & login
- [ ] Prediction game system
- [ ] Leaderboard endpoints
- [ ] User profile management
- [ ] Fantasy league API

### Phase 4: Advanced Analytics 🔮 (Future)
- [ ] AI-powered race predictions
- [ ] Driver comparison analytics
- [ ] Historical trend analysis
- [ ] Live race updates (WebSocket)
- [ ] Performance metrics
- [ ] Advanced statistics endpoints

### Phase 5: Frontend Integration 🎨 (Future)
- [ ] Build React/Next.js frontend
- [ ] Interactive dashboards
- [ ] Mobile-responsive design
- [ ] Real-time updates
- [ ] User interface for predictions
- [ ] Mobile app (React Native)

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Code Style
- **Backend:** Follow Java conventions, use Lombok
- **Frontend:** Use ESLint & Prettier
- **Commits:** Use [Conventional Commits](https://www.conventionalcommits.org/)

## 👨‍💻 Author

**Your Name**
- GitHub: (https://github.com/vj2970)
- LinkedIn: (https://www.linkedin.com/in/vaibhav-kumar-jha-1a68b0222/)
- Email: vaibhavjha83@gmail.com

## 🙏 Acknowledgments

- **Data Source:** [Jolpica F1 API](https://api.jolpi.ca/ergast/f1) (Ergast API replacement)
- **Inspiration:** Formula 1 official website
- **Icons:** [Lucide Icons](https://lucide.dev/)
- **Styling:** [Tailwind CSS](https://tailwindcss.com/)

## 📞 Support

If you have any questions or issues, please:
- 🐛 [Open an issue](https://github.com/vj2970/Formula-Grid/issues)
- 📧 Email: vaibhavjha83@gmail.com

## ⭐ Show Your Support

If you like this project, please give it a ⭐ on GitHub!

---

**Built with ❤️ by [Vaibhav Jha]**

*Last Updated: November 2024*
