# Azerite Crypto Airdrop Discovery API

This project is a Spring Boot based backend system that automatically detects crypto airdrop opportunities shared on social media platforms (especially X/Twitter), filters them according to specific rules, and exposes them through a REST API.

## 🎯 Purpose

In today's crypto world, airdrop opportunities are increasingly common, but tracking them manually can be challenging. This project automatically identifies airdrop announcements on social media, allowing users to easily capture these opportunities.

The project is developed as a Spring Boot starter, meaning it can be easily integrated into other projects. This API uses real tweet texts as a data source.

## 🧩 Technical Features

- Developed using **Spring Boot 3.2.x and Java 21**
- Database operations with **Spring Data JPA**
- **PostgreSQL** database support
- Keyword-based airdrop detection
- Easy integration with REST API endpoints

## 📋 API Endpoints

- `POST /airdrops/ingest`: Processes tweet data from external sources and saves it to the database
- `GET /airdrops/filtered`: Lists airdrop tweets with tasks

## 🛠️ Installation and Usage

### Maven Dependency

```xml
<dependency>
    <groupId>com.azerite</groupId>
    <artifactId>crypto-airdrop-discovery-api</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle Dependency

```groovy
implementation "com.azerite:crypto-airdrop-discovery-api:0.1.0"
```

## 🚧 Future Plans

- NLP or AI-powered tweet analysis
- Automatic tweet collection via X API or scraping
- User-based filtering
- Webhook / email / Discord notifications

## 📄 License

This project is licensed under the [MIT License](LICENSE). 