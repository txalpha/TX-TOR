# TX TOR — Premium Super Fast Browser

TX TOR is a production-oriented, ultra-fast web browser designed with an ultra-premium black/purple glassmorphism aesthetic for Android and Windows PC.

---

## 🌟 Key Architecture & Capabilities

### 1. Dual-Platform Architecture
- **Android Platform**:
  - Native Kotlin + Jetpack Compose
  - Hardware-accelerated Chromium WebView engine
  - Room SQLite local persistence (Bookmarks, History, Tabs, Downloads)
  - Async OkHttp streaming download engine with pause, resume (HTTP 206 Range), instantaneous speed, and ETA calculations
  - Material 3 dynamic glassmorphic theming with custom AMOLED black and neon violet/magenta gradients
- **Windows Platform (`windows/TxtorBrowser`)**:
  - C# and .NET 8 (WPF)
  - Microsoft Edge WebView2 runtime (Chromium engine)
  - Native asynchronous download service with progress observation
  - Desktop tabs, address omnibox, and keyboard shortcuts

---

## 🚀 Core Features

### 🌐 Fast Web Browsing Engine
- Full HTTP and HTTPS navigation with lock security indicator
- Real-time page loading progress bar with smooth easing
- Back, forward, reload, and stop controls
- Find-in-page text search with next/previous occurrence navigation
- Full-screen HTML5 video and media support
- Safe external link handling

### 📑 Advanced Multi-Tab Manager
- Independent navigation backstack and session states per tab
- Responsive tab switcher grid with active tab indicator
- Quick "+ New Tab" creation and "Close All"
- Private/Incognito mode tabs with non-persistent storage and disabled cookies

### 🖥 Desktop / PC Mode
- One-tap toggle between Mobile and Desktop viewport modes
- Changes User-Agent string to high-compatibility Linux/Windows desktop browser strings
- Enables `useWideViewPort` and `loadWithOverviewMode` for complete desktop rendering
- Configurable per-session and globally via Settings

### ⚡ Integrated Media Sniffer (Video & Audio Downloader)
- Detects genuine direct media streams (`.mp4`, `.webm`, `.m4v`, `.mp3`, `.m4a`, `.aac`, etc.)
- Inspects real Content-Length via OkHttp probes without faking file sizes
- Displays format badges, file size, and 1-tap download queueing
- **Ethical & Legal Compliance**: Transparently respects DRM protections, paywalls, and content owners' rights. Only authorizes direct streams without fabricating fake progress.

### 📥 Advanced Persistent Download Manager
- Filterable tabs: `All`, `Active`, `Completed`, `Paused`, `Failed`
- Real-time download speed (`KB/s` and `MB/s`) and dynamic ETA calculation
- Real pause and resume using HTTP `Range: bytes=X-` headers
- Storage space checks before queueing large files
- File opening directly via Android `FileProvider`
- Multi-threaded task execution persisted in Room database across application restarts

### 🛡 Privacy & Security
- Tracker and ad-blocking request heuristics
- One-tap "Clear Cookies & Web Storage"
- Optional "Clear Data On App Exit"
- Clear Security Notice: TX TOR is a high-speed private browser. It does not route through Tor onion proxies unless an external Tor proxy is configured.

---

## 🎨 UI/UX Design System (Cyber Glassmorphism)
- **Background**: AMOLED Black (`#06030D`)
- **Panels**: Frosted glass containers (`#B8140A28`) with gradient borders
- **Accents**: Neon Violet (`#A855F7`), Electric Purple (`#7C3AED`), Neon Magenta (`#D946EF`), Cyber Cyan (`#06B6D4`)
- **Contrast**: High contrast slate typography (`#F8FAFC`) meeting WCAG accessibility standards

---

## 🛠 Build Instructions

### Android Application
1. **Compilation & Verification**:
   ```bash
   gradle compileDebugKotlin
   ```
2. **Build Debug APK**:
   ```bash
   gradle :app:assembleDebug
   ```
   Outputs to `app/build/outputs/apk/debug/app-debug.apk`.
3. **Run Unit & Robolectric Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

### Windows Desktop Application (`windows/TxtorBrowser`)
1. **Prerequisites**: .NET 8 SDK and Microsoft Edge WebView2 Runtime.
2. **Build**:
   ```cmd
   cd windows\TxtorBrowser
   dotnet build -c Release
   ```
3. **Run**:
   ```cmd
   dotnet run -c Release
   ```
4. **Publish Self-Contained Single File**:
   ```cmd
   dotnet publish -c Release -r win-x64 --self-contained true -p:PublishSingleFile=true
   ```

---

## 🔒 Permissions & Privacy
- `android.permission.INTERNET`: Required for web page loading and file streaming.
- `android.permission.ACCESS_NETWORK_STATE`: Required for connectivity detection.
- `android.permission.POST_NOTIFICATIONS`: Optional for background download completion alerts.
- Downloaded files are stored in the app's external downloads directory (`context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)`), requiring no dangerous storage permissions.
