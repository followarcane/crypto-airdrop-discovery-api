# Azerite Crypto Airdrop Discovery API

A Spring Boot starter library that automatically detects crypto airdrop opportunities shared on social media platforms (
especially X/Twitter), filters them according to specific rules, and exposes them through a REST API.

## 🎯 Purpose

In today's crypto world, airdrop opportunities are increasingly common, but tracking them manually can be challenging. This project automatically identifies airdrop announcements on social media, allowing users to easily capture these opportunities.

The project is developed as a Spring Boot starter, meaning it can be easily integrated into other projects. This API uses real tweet texts as a data source.

## 🧩 Key Features

- **Automatic Airdrop Detection**: Uses keyword matching to identify potential airdrops in tweet content
- **Content Extraction**: Identifies and extracts tasks required for airdrops
- **Filtering System**: Excludes scams, ended events, or other irrelevant content
- **Configurable**: All aspects of the detection system can be customized
- **Database Integration**: Stores airdrop information with Spring Data JPA
- **REST API**: Provides endpoints for accessing and searching airdrops
- **Webhook Support**: Optional notification when new airdrops are detected

## 📋 API Endpoints

| Endpoint             | Method | Description                                                        |
|----------------------|--------|--------------------------------------------------------------------|
| `/airdrops/ingest`   | POST   | Process tweet data and save to the database if it matches criteria |
| `/airdrops/filtered` | GET    | Get all airdrop tweets that match the filtering criteria           |
| `/airdrops/search`   | GET    | Search for airdrops by keyword                                     |

## 🛠️ Installation and Usage

### Add the Dependency

#### Maven

```xml
<dependency>
    <groupId>com.azerite</groupId>
    <artifactId>crypto-airdrop-discovery-api</artifactId>
    <version>0.1.0</version>
</dependency>
```

#### Gradle

```groovy
implementation "com.azerite:crypto-airdrop-discovery-api:0.1.0"
```

### Configuration

Add the following to your `application.yml` or `application.properties` file to customize the behavior:

```yaml
azerite:
  airdrop-discovery:
    enabled: true  # Enable/disable the entire module
    keywords:
      positive:    # Keywords indicating a tweet is about an airdrop
        - follow
        - mint
        - galxe
        - zk
        - claim
        - airdrops
      negative:    # Keywords indicating a tweet should be excluded
        - scam
        - ended
        - fake
    database:
      table-name: airdrop_tweets  # Custom table name
    integration:
      webhook-enabled: false
      webhook-url: https://your-webhook-url.com
```

### Basic Usage

The starter will automatically set up the necessary beans and endpoints. You can use them directly in your application:

```java
@RestController
@RequestMapping("/api")
public class YourController {
    
    private final AirdropDiscoveryService airdropService;
    
    public YourController(AirdropDiscoveryService airdropService) {
        this.airdropService = airdropService;
    }
    
    @PostMapping("/process-tweet")
    public ResponseEntity<?> processTweet(@RequestBody TweetData data) {
        AirdropTweet airdrop = airdropService.processTweet(data.getText(), data.getSource(), data.getLink());
        return ResponseEntity.ok(airdrop);
    }
}
```

## 🚧 Roadmap

- **Enhanced Analysis**: Implement NLP or AI-powered tweet analysis for better detection
- **More Data Sources**: Expand to support Discord, Telegram, and other platforms
- **User Notifications**: Implement email, Discord, or Telegram notifications
- **Advanced Filtering**: Allow users to create custom filtering rules
- **Integration with Blockchain Data**: Verify airdrop legitimacy and track on-chain activity

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 