# Simon Says Game

**OTHM Level 5 Diploma in IT Management – Software Engineering Assignment**

A JavaFX implementation of the classic Simon Says memory game with JSON persistence for high scores.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Game Description](#game-description)
3. [Technical Architecture](#technical-architecture)
4. [Class Structure](#class-structure)
5. [Key Features](#key-features)
6. [Technologies Used](#technologies-used)
7. [Design Patterns & Principles](#design-patterns--principles)
8. [Testing Strategy](#testing-strategy)
9. [Build & Run Instructions](#build--run-instructions)
10. [File Structure](#file-structure)

---

## Project Overview

This project is a desktop implementation of the Simon Says memory game, demonstrating modern Java development practices including:
- Object-Oriented Programming (OOP) principles
- Model-View-Controller (MVC) architecture
- Java Collections and Generics
- File I/O with JSON serialization
- JavaFX GUI development
- Unit and Integration testing with JUnit 5

---

## Game Description

Simon Says is a memory game where players must repeat increasingly longer sequences of colored button flashes:

1. **Game Start**: The game displays a sequence of one colored button flash
2. **Player Turn**: The player must click the buttons in the same order
3. **Round Progression**: Each successful round adds one more color to the sequence
4. **Game Over**: If the player clicks the wrong color, the game ends
5. **High Scores**: Top 10 scores are saved to a leaderboard with JSON persistence

**Colors**: Red, Blue, Green, Yellow (4 buttons in a 2x2 grid)

---

## Technical Architecture

### MVC Architecture

The project follows a clear separation of concerns:

```
┌─────────────────────────────────────────────────┐
│                  SimonSaysApp                   │
│              (Application Entry)                 │
└────────────────────┬────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
    ┌────▼─────┐          ┌─────▼──────┐
    │   UI     │          │   Logic    │
    │ (GameUI) │◄────────►│(GameEngine)│
    └────┬─────┘          └─────┬──────┘
         │                      │
    ┌────▼─────┐          ┌─────▼──────┐
    │  Model   │          │Persistence │
    │ Classes  │          │   (JSON)   │
    └──────────┘          └────────────┘
```

---

## Class Structure

### 1. **Main Application**
- **`SimonSaysApp`**: JavaFX Application entry point, launches the UI

### 2. **UI Layer** (`com.simonsays.ui`)
- **`GameUI`**:
  - Manages the JavaFX user interface
  - Handles user input and button clicks
  - Animates color sequences with flash effects
  - Displays scores, rounds, and messages
  - Shows leaderboard dialog

### 3. **Logic Layer** (`com.simonsays.logic`)
- **`GameEngine`**:
  - Core game logic and state management
  - Generates random color sequences
  - Validates player input in real-time
  - Tracks rounds and calculates scores
  - **Game States**: `NOT_STARTED`, `SHOWING_SEQUENCE`, `WAITING_FOR_INPUT`, `GAME_OVER`

### 4. **Model Layer** (`com.simonsays.model`)
- **`GameColour`** (Enum):
  - Defines the 4 game colors: RED, BLUE, GREEN, YELLOW
  - Each with an ID (0-3) and hex color code
  - Type-safe color handling

- **`Score`**:
  - Represents a single high score entry
  - Fields: `playerName`, `score`, `timestamp`
  - Implements `Comparable` for sorting (descending by score)

- **`Leaderboard`**:
  - Manages top 10 high scores
  - Uses `ArrayList<Score>` with generics
  - Automatically maintains top 10 entries
  - Provides methods to check if score qualifies

### 5. **Persistence Layer** (`com.simonsays.persistence`)
- **`JsonPersistence`**:
  - Saves/loads leaderboard data to/from JSON file
  - Uses Google Gson library for serialization
  - Handles file I/O exceptions gracefully
  - File: `scores.json` (in project root)

---

## Key Features

### 1. **Visual Feedback**
- Buttons flash **white** during sequence playback
- Buttons flash **white** when clicked by player
- Smooth animations using JavaFX `PauseTransition`
- Color-coded messages (orange = instructions, blue = your turn, green = correct, red = wrong)

### 2. **Game Mechanics**
- Random sequence generation using `java.util.Random`
- Immediate input validation (fail-fast)
- Progressive difficulty (sequence length increases each round)
- Score = number of completed rounds

### 3. **Leaderboard System**
- Automatically saves top 10 scores
- Prompts for player name on high score
- Displays timestamp for each entry
- Persistent storage using JSON

### 4. **Error Handling**
- Graceful handling of missing score files
- Exception handling for file I/O operations
- Input validation and state checking

---

## Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 11 | Core programming language |
| **JavaFX** | 17.0.2 | GUI framework for desktop UI |
| **Maven** | 3.x | Build automation and dependency management |
| **Gson** | 2.10.1 | JSON serialization/deserialization |
| **JUnit 5** | 5.9.2 | Unit and integration testing |

### Maven Dependencies
```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.2</version>
    </dependency>

    <!-- Gson for JSON -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>

    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.9.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Design Patterns & Principles

### 1. **Separation of Concerns**
- UI logic separated from game logic
- Business logic independent of presentation layer
- Persistence layer abstracted from models

### 2. **Encapsulation**
- Private fields with public getters
- Return defensive copies of collections (`getSequence()`, `getTopScores()`)
- State management hidden behind public API

### 3. **Enumerations for Type Safety**
- `GameColour` enum prevents invalid color values
- `GameState` enum ensures valid state transitions
- Compile-time type checking

### 4. **Generics**
- `List<Integer>` for color sequences
- `List<Score>` for leaderboard entries
- `TypeToken<List<Score>>` for JSON deserialization

### 5. **Exception Handling**
- Try-with-resources for automatic file closing
- Graceful fallback for missing files
- User-friendly error messages

### 6. **Immutability**
- Enum constants are immutable
- Defensive copying of collections
- Unmodifiable list views where appropriate

---

## Testing Strategy

### Unit Tests (`GameEngineTest`)
Tests individual components in isolation:
- Initial state verification
- Game start and reset functionality
- Sequence generation and growth
- Input validation (correct/incorrect)
- Score calculation
- State transitions
- Boundary conditions

**Test Coverage**:
- ✅ Valid color IDs (0-3)
- ✅ Correct input acceptance
- ✅ Incorrect input rejection
- ✅ Input timing (state-dependent)
- ✅ Multiple rounds handling
- ✅ Game reset functionality

### Integration Tests (`JsonPersistenceTest`)
Tests file I/O and data persistence:
- Save and load operations
- Non-existent file handling
- Exactly 10 scores boundary
- More than 10 scores (truncation)
- Multiple save operations
- Empty leaderboard handling
- File deletion

**Test Coverage**:
- ✅ JSON serialization/deserialization
- ✅ File existence checking
- ✅ Data integrity across saves
- ✅ Top 10 score maintenance
- ✅ Score sorting (highest first)

### Testing Techniques Used
1. **Boundary Testing**: Edge cases (0 scores, exactly 10, more than 10)
2. **State Testing**: Valid/invalid state transitions
3. **Integration Testing**: File I/O with actual filesystem
4. **Unit Testing**: Isolated component behavior

---

## Build & Run Instructions

### Prerequisites
- Java JDK 11 or higher
- Maven 3.x
- JavaFX SDK (included via Maven)

### Commands

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Run the application
mvn javafx:run

# Package as JAR
mvn package
```

### Run from IDE (IntelliJ IDEA)
1. Open project in IntelliJ IDEA
2. Right-click on `SimonSaysApp.java`
3. Select "Run SimonSaysApp"

---

## File Structure

```
SimonSaysProject/
├── pom.xml                              # Maven configuration
├── scores.json                          # High scores (generated at runtime)
├── README.md                            # This file
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── simonsays/
│   │               ├── SimonSaysApp.java           # Main entry point
│   │               │
│   │               ├── ui/
│   │               │   └── GameUI.java             # User interface
│   │               │
│   │               ├── logic/
│   │               │   └── GameEngine.java         # Game logic
│   │               │
│   │               ├── model/
│   │               │   ├── GameColour.java         # Color enum
│   │               │   ├── Score.java              # Score model
│   │               │   └── Leaderboard.java        # Leaderboard model
│   │               │
│   │               └── persistence/
│   │                   └── JsonPersistence.java    # JSON I/O
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── simonsays/
│                   ├── logic/
│                   │   └── GameEngineTest.java      # Unit tests
│                   │
│                   └── persistence/
│                       └── JsonPersistenceTest.java # Integration tests
│
└── target/                              # Compiled classes (generated)
```

---

## Class Diagram Description

### Core Relationships

1. **SimonSaysApp** → creates → **GameUI**
2. **GameUI** → uses → **GameEngine** (game logic)
3. **GameUI** → uses → **JsonPersistence** (save/load)
4. **GameUI** → uses → **Leaderboard** (score management)
5. **GameEngine** → uses → **GameColour** (enum)
6. **Leaderboard** → contains → `List<Score>`
7. **JsonPersistence** → serializes → **Leaderboard**
8. **Score** → implements → `Comparable<Score>`

### Key Associations
- **Composition**: GameUI has-a GameEngine, JsonPersistence, Leaderboard
- **Aggregation**: Leaderboard has-many Scores
- **Dependency**: All classes depend on GameColour enum
- **Implementation**: Score implements Comparable interface

---

## JSON Data Format

### Example `scores.json`
```json
[
  {
    "playerName": "Alice",
    "score": 15,
    "timestamp": "2024-01-15T14:30:00"
  },
  {
    "playerName": "Bob",
    "score": 12,
    "timestamp": "2024-01-15T15:45:00"
  },
  {
    "playerName": "Charlie",
    "score": 10,
    "timestamp": "2024-01-15T16:20:00"
  }
]
```

Scores are stored as a JSON array, sorted in descending order by score value.

---

## Future Enhancements

Potential improvements for future versions:
- Sound effects for button presses
- Difficulty levels (speed variations)
- Multiplayer mode
- Online leaderboard
- Game statistics (average score, total games)
- Colorblind mode (patterns/symbols on buttons)
- Customizable color schemes
- Achievement system

---

## Learning Outcomes Demonstrated

This project demonstrates the following OTHM Level 5 Software Engineering competencies:

1. **Object-Oriented Programming**: Classes, objects, inheritance, encapsulation, polymorphism
2. **Java Collections**: ArrayList, List interface, Collections utility methods
3. **Generics**: Type-safe collections (`List<Score>`, `List<Integer>`)
4. **Enumerations**: Type-safe constants with behavior (GameColour, GameState)
5. **File I/O**: Reading and writing files with exception handling
6. **JSON Processing**: Serialization/deserialization with Gson
7. **GUI Development**: JavaFX layouts, controls, events, animations
8. **Software Testing**: Unit tests, integration tests, boundary testing
9. **Build Automation**: Maven project structure and dependency management
10. **Design Patterns**: MVC architecture, separation of concerns
11. **Exception Handling**: Try-with-resources, graceful error recovery
12. **Version Control**: Git repository structure (implied)

---

## Author & License

**Author**: [Your Name]
**Assignment**: OTHM Level 5 Diploma in IT Management – Software Engineering
**Date**: 2024
**Version**: 1.0

---

## Acknowledgments

- JavaFX documentation: https://openjfx.io/
- Gson library by Google: https://github.com/google/gson
- JUnit 5 documentation: https://junit.org/junit5/
- Simon Says game concept: Classic memory game
