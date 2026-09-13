# Minimalist Dark Launcher Implementation Plan

This plan transforms the current retro Nokia-style launcher into a modern, minimalist dark-themed launcher as per the provided image.

## User Review Required

> [!IMPORTANT]
> The Nokia 7610 frame, tactile keypad, and LCD simulation will be removed and replaced with a full-screen modern UI.
> App icons will be converted to monochrome white to match the minimalist aesthetic.

## Proposed Changes

### UI Components

#### [MODIFY] [MainActivity.kt](file:///Users/januprasad/AndroidStudioProjects/retro-launcher/app/src/main/java/com/example/MainActivity.kt)
- Replace `RetroPhoneFrame` with `MinimalistLauncher`.
- Implement a full-screen layout with a dark background.
- Add a vertical day-of-the-week display (e.g., "IT'S FRIDAY").
- Implement a vertical list for apps with monochrome icons and white text.
- Add the stylized "wave" background texture using a `Box` and `Canvas` or image resource.

#### [NEW] [MinimalistLauncher.kt](file:///Users/januprasad/AndroidStudioProjects/retro-launcher/app/src/main/java/com/example/ui/components/MinimalistLauncher.kt)
- Create a new component for the minimalist UI to keep `MainActivity` clean.
- Include:
    - `VerticalDayDisplay`: Rotated text for the current day.
    - `AppListItem`: Composable for a single app entry with a monochrome icon.
    - `StylizedBackground`: A background component that draws the wave-like texture.

### Data & Logic

#### [MODIFY] [LauncherViewModel.kt](file:///Users/januprasad/AndroidStudioProjects/retro-launcher/app/src/main/java/com/example/viewmodel/LauncherViewModel.kt)
- Add a new `StateFlow` for the day of the week (e.g., "FRIDAY").
- Ensure `loadInstalledApps` provides all necessary info for the new list.

### Styling

#### [MODIFY] [Color.kt](file:///Users/januprasad/AndroidStudioProjects/retro-launcher/app/src/main/java/com/example/ui/theme/Color.kt)
- Define a pure black background color and high-contrast white for text.

## Verification Plan

### Manual Verification
- Deploy the app and verify:
    - The background is dark with the wave texture.
    - "IT'S FRIDAY" (or current day) is displayed vertically.
    - The app list displays icons in white monochrome.
    - Tapping an app launches it correctly.
    - Back button behavior (if any sub-screens are used, though this design is mostly one-screen).
