# 🏎️ Formula Grid - F1 Statistics Platform

A full-stack Formula 1 statistics platform. A **Spring Boot + MongoDB** backend pulls data from the [Jolpica F1 API](https://api.jolpi.ca/ergast/f1) (the Ergast replacement) and serves standings, race calendars, race and qualifying results, driver statistics and historical season data. A **React (Vite)** frontend consumes the API.

## 🌐 Live Demo

|                  | Link                                                              |
| ---------------- | ----------------------------------------------------------------- |
| **Frontend**     | https://formula1grid.netlify.app                                  |
| **Backend API**  | https://formula-grid-backend-production.up.railway.app            |
| **Health check** | https://formula-grid-backend-production.up.railway.app/api/health |

## ✨ Features

### Backend

- 📊 **Driver Standings** - Championship rankings for any season, with points and wins
- 🏆 **Constructor Standings** - Team championship standings for any season
- 📅 **Race Calendar** - Schedule for any season with circuit details and session times
- 🏁 **Race Results** - Full classification, grid, status, race time and fastest lap data
- ⏱️ **Qualifying Results** - Q1, Q2 and Q3 times for every race
- 👤 **Driver Statistics** - Career and current-season stats, win/podium/pole rates, last-5-races form
- ⚔️ **Driver Comparison** - Head-to-head stats between two drivers
- 📚 **Historical Seasons** - Season summaries with champions, most wins and most poles
- 📥 **Bulk Data Import** - Import a full season, a single race, or 2020-2024 in one call
- 💾 **MongoDB Caching** - Data is fetched from the external API once, then served from the database
- 🛡️ **Consistent error responses** with correct HTTP status codes, and configurable **CORS**

### Frontend (React + Vite)

- Drivers page
- Driver standings page
- Constructor standings page

### Planned

- 🎮 Prediction game with JWT authentication and leaderboards
- ⚡ Redis caching and scheduled data sync
- 🤖 AI-powered race predictions and natural-language stats queries
- 🎯 Fantasy league
- 📅 Race calendar, results, season browser and driver statistics pages in the frontend

## 🛠️ Tech Stack

### Backend
- **Framework:** [Spring Boot 3.2](https://spring.io/projects/spring-boot)
- **Language:** Java 25
- **Database:** [MongoDB Atlas](https://www.mongodb.com/atlas)
- **HTTP Client:** Spring WebFlux (WebClient)
- **Data Source:** [Jolpica F1 API](https://api.jolpi.ca/ergast/f1) (Ergast replacement)
- **Build Tool:** Maven
- **Utilities:** Lombok, Jackson

**Database:** MongoDB Atlas

**Frontend:** React, Vite

### Prerequisites
- **Java** 25 or higher
- **Maven** 3.6+
- **MongoDB Atlas** account (free)
- **Git**

**Hosting:** Railway (backend), Netlify (frontend), MongoDB Atlas (database)

## 📁 Project Structure

```
Formula-Grid/
├── backend/
│   ├── src/main/java/com/formulagrid/FormulaGrid/
│   │   ├── controller/
│   │   │   ├── HealthController.java
│   │   │   ├── DriverController.java
│   │   │   ├── ConstructorController.java
│   │   │   ├── RaceController.java
│   │   │   ├── RaceResultController.java
│   │   │   ├── QualifyingResultController.java
│   │   │   ├── SeasonController.java
│   │   │   └── DataImportController.java
│   │   ├── service/
│   │   │   ├── DriverService.java
│   │   │   ├── ConstructorService.java
│   │   │   ├── RaceService.java
│   │   │   ├── RaceResultService.java
│   │   │   ├── QualifyingResultsService.java
│   │   │   ├── SeasonService.java
│   │   │   └── DataImportService.java
│   │   ├── repository/               # Spring Data MongoDB repositories
│   │   ├── model/                    # Driver, Constructor, Race, Circuit, standings, results
│   │   ├── dto/response/             # Jolpica response DTOs + API DTOs
│   │   ├── client/                   # JoplicaApiClient (Jolpica HTTP client)
│   │   ├── config/                   # WebConfig (CORS)
│   │   └── exception/                # GlobalExceptionHandler + custom exceptions
│   ├── src/main/resources/application.yml
│   └── pom.xml
├── frontend/                         # React + Vite app
│   ├── src/
│   ├── index.html
│   └── package.json
└── README.md
```

## 🚀 Quick Start

### Prerequisites

- Java 25+
- Maven 3.6+
- Node.js 18+
- A free [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) cluster

### 1. Clone

```bash
git clone https://github.com/vj2970/Formula-Grid.git
cd Formula-Grid
```

### 2. Run the backend

```bash
cd backend

export MONGODB_URI="mongodb+srv://<user>:<password>@cluster0.xxxxx.mongodb.net/f1stats?retryWrites=true&w=majority"
export PORT=8080
export ALLOWED_ORIGINS="http://localhost:5173"

mvn clean install
mvn spring-boot:run
```

> The connection string **must include a database name** (`/f1stats` before the `?`). Without it the app fails on startup with `Database name must not be empty`.

Verify:

```bash
curl http://localhost:8080/api/health
```

### 3. Run the frontend

```bash
cd frontend
npm install
```

Create `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080
```

```bash
npm run dev
```

Open `http://localhost:5173`.

### 4. Load historical data

Standings and calendars load automatically the first time they are requested. Race results and qualifying are loaded per race on first request, or in bulk with the import endpoints:

```bash
# one race (results + qualifying)
curl -X POST http://localhost:8080/api/import/race/2023/1

# a full season (calendar, standings, results, qualifying) - long-running
curl -X POST http://localhost:8080/api/import/season/2023

# 2020-2024 in the background
curl -X POST http://localhost:8080/api/import/historical
```

## 🔌 API Reference

**Base URL**

```
Local:      http://localhost:8080
Production: https://formula-grid-backend-production.up.railway.app
```

**35 endpoints** across 8 controllers.

### Health

| Method | Endpoint      | Description           |
| ------ | ------------- | --------------------- |
| GET    | `/api/health` | Service health status |

```json
{
  "status": "UP",
  "timestamp": "2026-01-01T12:00:00",
  "service": "Formula Grid API is running"
}
```

### Drivers

| Method | Endpoint                                       | Description                                   |
| ------ | ---------------------------------------------- | --------------------------------------------- |
| GET    | `/api/drivers`                                 | Current season drivers                        |
| GET    | `/api/drivers/{driverId}`                      | A single driver by ID (e.g. `max_verstappen`) |
| POST   | `/api/drivers/refresh`                         | Re-fetch current drivers from Jolpica         |
| GET    | `/api/drivers/standings`                       | Current driver standings                      |
| GET    | `/api/drivers/standings/{season}`              | Driver standings for any season               |
| POST   | `/api/drivers/standings/refresh`               | Re-fetch current driver standings             |
| GET    | `/api/drivers/{driverId}/statistics`           | Career and season statistics                  |
| GET    | `/api/drivers/compare/{driverId1}/{driverId2}` | Head-to-head comparison                       |

### Constructors

| Method | Endpoint                               | Description                                  |
| ------ | -------------------------------------- | -------------------------------------------- |
| GET    | `/api/constructors`                    | Current season constructors                  |
| GET    | `/api/constructors/{constructorId}`    | A single constructor by ID (e.g. `red_bull`) |
| GET    | `/api/constructors/standings`          | Current constructor standings                |
| GET    | `/api/constructors/standings/{season}` | Constructor standings for any season         |
| POST   | `/api/constructors/standings/refresh`  | Re-fetch current constructor standings       |

### Races

| Method | Endpoint              | Description                   |
| ------ | --------------------- | ----------------------------- |
| GET    | `/api/races`          | Current season calendar       |
| GET    | `/api/races/{season}` | Calendar for any season       |
| POST   | `/api/races/refresh`  | Re-fetch the current calendar |

### Race Results

| Method | Endpoint                                 | Description                        |
| ------ | ---------------------------------------- | ---------------------------------- |
| GET    | `/api/results/last`                      | Most recent race                   |
| GET    | `/api/results/{season}/{round}`          | A specific race                    |
| GET    | `/api/results/season/{season}`           | All results for a season           |
| GET    | `/api/results/current`                   | All results for the current season |
| GET    | `/api/results/driver/{driverId}`         | A driver's race history            |
| GET    | `/api/results/driver/{driverId}/wins`    | A driver's wins                    |
| GET    | `/api/results/driver/{driverId}/podiums` | A driver's podiums (1st to 3rd)    |
| POST   | `/api/results/{season}/{round}/refresh`  | Re-fetch results for a race        |

### Qualifying

| Method | Endpoint                                   | Description                   |
| ------ | ------------------------------------------ | ----------------------------- |
| GET    | `/api/qualifying/{season}/{round}`         | Qualifying for a race         |
| GET    | `/api/qualifying/driver/{driverId}`        | A driver's qualifying history |
| GET    | `/api/qualifying/driver/{driverId}/poles`  | A driver's pole positions     |
| POST   | `/api/qualifying/{season}/{round}/refresh` | Re-fetch qualifying           |

### Seasons

| Method | Endpoint                        | Description                               |
| ------ | ------------------------------- | ----------------------------------------- |
| GET    | `/api/seasons/{season}/summary` | Champions, most wins, most poles and more |
| GET    | `/api/seasons/available`        | Seasons present in the database           |

### Data Import

| Method | Endpoint                            | Description                                                                    |
| ------ | ----------------------------------- | ------------------------------------------------------------------------------ |
| POST   | `/api/import/season/{season}`       | Import calendar, standings, results and qualifying for a season (long-running) |
| POST   | `/api/import/race/{season}/{round}` | Import results and qualifying for one race                                     |
| POST   | `/api/import/historical`            | Import 2020-2024 in a background thread                                        |
| GET    | `/api/import/progress`              | Import status                                                                  |

> ⚠️ Refresh and import endpoints are currently **unauthenticated**. They are intended for admin use and will be protected once JWT roles are added.

### Example Responses

**`GET /api/drivers/standings/2023`**

```json
[
  {
    "season": 2023,
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

**`GET /api/drivers/{driverId}/statistics`**

```json
{
  "driverId": "max_verstappen",
  "driverName": "Max Verstappen",
  "currentTeam": "Red Bull",
  "totalRaces": 110,
  "totalWins": 45,
  "totalPodiums": 70,
  "totalPoles": 25,
  "winRate": 40.91,
  "podiumRate": 63.64,
  "last5Races": 5,
  "last5Wins": 4,
  "last5AvgPosition": 1.4
}
```

**`GET /api/seasons/{season}/summary`**

```json
{
  "season": 2023,
  "totalRaces": 22,
  "completedRaces": 22,
  "driverChampion": "Max Verstappen",
  "constructorChampion": "Red Bull",
  "differentWinners": 3,
  "mostWinsDriver": "Max Verstappen",
  "mostWinsCount": 19
}
```

> Example values are illustrative. Real numbers depend on the data imported into your database.

## 🧠 Data Loading and Caching

- **First request per season** fetches from Jolpica and stores the result in MongoDB. Later requests are served from MongoDB.
- **Past seasons** never change, so their cached data is kept permanently.
- **The current season** is cached until you call the matching `POST .../refresh` endpoint. Refresh after each race (scheduled sync is on the roadmap).
- **Race results and qualifying** are fetched per race on first request, or in bulk through the import endpoints.
- **Season summaries** load the calendar and standings automatically. Win and pole statistics need that season's results, so run `POST /api/import/season/{season}` first.
- Refreshing replaces data for that season only. Other seasons are untouched.

## 🚦 Error Responses

All errors use the same JSON shape:

```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Driver not found: max_verstapen"
}
```

| Status | When                                                  |
| ------ | ----------------------------------------------------- |
| 400    | Invalid path value, e.g. `/api/results/abc/1`         |
| 404    | Unknown driver, constructor, season or endpoint       |
| 405    | Wrong HTTP method, e.g. `GET /api/import/race/2023/1` |
| 503    | Jolpica is unavailable or rate-limiting the request   |
| 500    | Unexpected server error                               |

## ⚙️ Configuration

### Environment variables

| Variable          | Description                                      | Example                                                  |
| ----------------- | ------------------------------------------------ | -------------------------------------------------------- |
| `MONGODB_URI`     | MongoDB connection string (must include DB name) | `mongodb+srv://.../f1stats?...`                          |
| `PORT`            | Server port                                      | `8080`                                                   |
| `LOG_LEVEL`       | Application log level                            | `INFO`                                                   |
| `ALLOWED_ORIGINS` | Comma-separated list of allowed CORS origins     | `http://localhost:5173,https://formula1grid.netlify.app` |

### `application.yml`

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/f1stats}

server:
  port: ${PORT:8080}

logging:
  level:
    com.formulagrid: ${LOG_LEVEL:INFO}

f1:
  ergast-api:
    base-url: https://api.jolpi.ca/ergast/f1
    timeout: 5000

cors:
  allowed-origins: ${ALLOWED_ORIGINS:http://localhost:5173}
```

## 🚢 Deployment

### Backend on Railway

1. Push the repo to GitHub and create a Railway project from it
2. If the backend lives in a subfolder, set the service **Root Directory** to `backend`
3. Add the environment variables above, including the Netlify URL in `ALLOWED_ORIGINS`
4. **Settings → Networking → Generate Domain**
5. Test `https://<your-domain>/api/health`

**Troubleshooting**

- `Database name must not be empty` → add `/f1stats` to `MONGODB_URI`
- Railway log-rate warnings → set `LOG_LEVEL=INFO`
- Browser CORS errors → add the frontend's exact origin to `ALLOWED_ORIGINS`
- HTTP 503 during an import → Jolpica is rate-limiting; wait a minute and retry
- Special characters in the MongoDB password must be URL-encoded (`@` → `%40`)

### Frontend on Netlify

- Base directory: `frontend`
- Build command: `npm run build`
- Publish directory: `dist`
- Environment variable: `VITE_API_URL=https://formula-grid-backend-production.up.railway.app`
- For client-side routing, add `frontend/public/_redirects` containing `/* /index.html 200`

## 🧪 Testing

```bash
cd backend
mvn test
```

Quick smoke test against any environment:

```bash
BASE=https://formula-grid-backend-production.up.railway.app
curl $BASE/api/health
curl $BASE/api/drivers/standings
curl $BASE/api/drivers/standings/2023
curl $BASE/api/constructors/standings/2023
curl $BASE/api/races/2023
curl $BASE/api/results/last
curl $BASE/api/seasons/available
```

## 📊 Roadmap

### ✅ Phase 1: Core API

- [x] Spring Boot + MongoDB setup
- [x] Driver and constructor standings
- [x] Race calendar
- [x] Jolpica integration with MongoDB caching
- [x] Global error handling and CORS
- [x] Railway deployment

### ✅ Phase 2: Results and Historical Data

- [x] Race results
- [x] Qualifying results
- [x] Driver statistics and comparison
- [x] Season summaries
- [x] Standings and calendar for any season
- [x] Bulk import for historical seasons

### 🚧 Phase 3: Frontend

- [x] React + Vite setup, deployed on Netlify
- [x] Drivers page
- [x] Driver standings page
- [x] Constructor standings page
- [ ] Race calendar page
- [ ] Race and qualifying results pages
- [ ] Driver detail and comparison pages
- [ ] Season browser

### 📅 Phase 4: User Features

- [ ] JWT authentication (also protects import and refresh endpoints)
- [ ] Prediction game and scoring
- [ ] Leaderboards
- [ ] Fantasy league

### 🔮 Phase 5: Performance and AI

- [ ] Scheduled data sync for the current season
- [ ] Redis caching
- [ ] Retry with backoff for Jolpica rate limits
- [ ] AI race predictions
- [ ] Natural-language stats assistant
- [ ] Swagger / OpenAPI docs

## 🤝 Contributing

1. Fork the repository
2. Create a branch: `git checkout -b feature/amazing-feature`
3. Commit using [Conventional Commits](https://www.conventionalcommits.org/): `git commit -m "feat: add amazing feature"`
4. Push and open a Pull Request

## 👨‍💻 Author

**Vaibhav Jha**

- GitHub: [@vj2970](https://github.com/vj2970)
- LinkedIn: [Vaibhav Kumar Jha](https://www.linkedin.com/in/vaibhav-kumar-jha-1a68b0222/)
- Email: vaibhavjha83@gmail.com

## 🙏 Acknowledgments

- Data from the [Jolpica F1 API](https://api.jolpi.ca/ergast/f1), the community-maintained Ergast replacement
- Inspired by the official Formula 1 website

---

⭐ If you find this project useful, consider giving it a star!
