## Functionalities Implemented

### 1. **MainActivity (Entry & Session Manager)**

- **Player setup**
    - User enters their name.
    - Mode switch decides **Letters** or **Numbers** mode.
    - Statistics object is created (or restored) and updated with this player.
- **Loop logic (perpetual Intent passing)**
    - Always receives back `Statistics`, `PlayerName`, `Mode`, and optionally `GameState` when returning from other activities.
    - If a `GameState` exists, **Continue Game** option is enabled.
- **Navigation**
    - Can launch:
        - **GameActivity** → starts/continues a game.
        - **StatisticsActivity** → views statistics summary.
    - Can also **reset player** (start new user → clears state).

------

### 2. **GameActivity (Core Gameplay)**

- **GameController-driven logic**
    - Handles guesses for numbers (1–100) or letters (a–z).
    - Tracks attempts with a hard cap of **6 guesses**.
    - Automatically records a **win** or **loss** into statistics when conditions are met.
- **UI dynamics**
    - Input and guess button disabled when game is over.
    - Reset button:
        - Immediately records a **loss** (for abandoning).
        - Starts a fresh new round.
    - Feedback system:
        - Tells the player if guess is too high/low (numbers) or above/below (letters).
        - Shows "Correct!" or "Out of guesses!" on end.
- **GameState management**
    - On navigation (e.g., going to StatisticsActivity), current **GameState** is packaged into the Intent without counting it as a loss.
    - This allows the player to **resume** exactly where they left off.

------

### 3. **StatisticsActivity (Session Data View)**

- **Statistics handling**
    - Displays the **session’s accumulated results**: wins, losses, average guesses, etc.
- **Dual return paths (perpetual loop)**
    - **Back to Menu**: always available → returns to MainActivity with statistics (and possibly GameState).
    - **Back to Game**: visible only if a GameState exists → restores directly into the unfinished game.

------

### 4. **GameController (Central Game Logic)**

- Owns the **rules of play**:
    - Random target generation (letter/number).
    - Guess validation.
    - Tracking of attempts.
- Ensures **every game ends in a recorded outcome**:
    - Win → recorded immediately.
    - Loss → recorded when max attempts are exceeded, or on **reset**.
- Provides **remaining guesses** info to UI for user feedback.

------

### 5. **Perpetual Intent Loop (Core Pattern)**

- **Every Activity → always passes back session state**:
    - `Statistics` (always updated).
    - `PlayerName` (to repopulate Main).
    - `Mode` (letters/numbers toggle restored).
    - `GameState` (only when mid-game → continue logic).
- **No dead ends**:
    - All navigation paths (Game ↔ Statistics ↔ Main) preserve context.
    - Only "New Player" action clears state and counts as a loss.
- This creates a **closed loop** where state is never lost between activities.

------

## Key Dynamic Management

1. **GameState**
    - Preserves current target, guess count, and player context.
    - Transferred via Intents when navigating away from gameplay.
    - Distinguishes between:
        - **Abandon/reset → counts as loss**.
        - **Temporary navigation (Stats) → resumes seamlessly**.
2. **Statistics**
    - Always kept alive through Intents.
    - All results (win/loss) are recorded immediately when game ends or resets.
    - Ensures **session-long tracking**, not just per-game.
3. **UI Adaptation**
    - Buttons shown/hidden depending on available state (`Continue Game`, `Back to Game`).
    - Input controls locked down after a win/loss to enforce proper flow.