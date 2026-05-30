# SavorySteps

An Android recipe browsing app powered by the Spoonacular API. Browse thousands of recipes, save favorites offline, filter by dietary preference, and plan meals by type.

## Screenshots

| Home | Recipe Detail | Meal Ideas | Settings |
|------|--------------|------------|---------|
| ![Home](docs/screenshots/home.png) | ![Detail](docs/screenshots/detail.png) | ![Meals](docs/screenshots/meals.png) | ![Settings](docs/screenshots/settings.png) |

## Features

- **Browse & Search** — Search by keyword or tap a category chip (Beef, Chicken, Dessert, Seafood, Vegetarian)
- **Recipe Detail** — Ingredients with interactive checkboxes, step-by-step instructions, nutrition facts (calories, protein, fat, carbs)
- **Favorites** — Save recipes offline with Room; persists across app restarts
- **Meal Ideas** — Filter recipes by Breakfast, Lunch, or Dinner
- **Dietary Filters** — Set a diet (Vegetarian, Vegan, Gluten-Free, Dairy-Free) and intolerances in Settings; every search respects your preferences
- **Share Ingredients** — Share a recipe's full ingredient list to any app via the Android share sheet

## Tech Stack

| Layer | Library / Tool |
|-------|---------------|
| Language | Kotlin |
| UI | XML Views (AppCompat) |
| Networking | Retrofit 2 + OkHttp 4 |
| Image loading | Coil 3 |
| Local database | Room 2 |
| Preferences | DataStore 1 |
| Build | AGP 8.13, KSP 2.3, JVM 11 |
| API | [Spoonacular Food API](https://spoonacular.com/food-api) |

## Setup

1. **Clone the repo**
   ```sh
   git clone https://github.com/LoukasKougiatsos/food-recipe.git
   cd food-recipe
   ```

2. **Get a free API key** at [spoonacular.com/food-api](https://spoonacular.com/food-api)

3. **Add the key to `local.properties`** (this file is gitignored and never committed):
   ```
   SPOONACULAR_API_KEY=your_api_key_here
   ```

4. **Open in Android Studio**, let Gradle sync, then run on an emulator or physical device (Android 7.0 / API 24+).

> **Test login:** username `test`, password `test`

## License

TBD
