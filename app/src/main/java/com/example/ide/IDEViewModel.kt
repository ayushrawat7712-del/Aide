package com.example.ide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Visual parsed properties of our simulated Flutter compiler preview frame
 */
data class SimulatorUIPrefs(
    val appBarTitle: String = "Aura App Runner",
    val titleText: String = "Welcome to Aura IDE!",
    val bodyText: String = "Edit this main.dart file to customize layouts or titles, and tap run to hot-reload in real-time!",
    val primaryColorName: String = "indigo",
    val accentIconName: String = "flash_on"
)

/**
 * Socket payload packet tracked for modular core communications.
 */
data class BridgePacket(
    val timestamp: String,
    val direction: String, // "IDE -> TERMUX" or "TERMUX -> IDE"
    val content: String,
    val protocol: String = "TCP/IPC"
)

class IDEViewModel : ViewModel() {

    // --- Virtual Filesystem Store ---
    private val _fileSystem = MutableStateFlow<List<FileNode>>(emptyList())
    val fileSystem = _fileSystem.asStateFlow()

    // --- Tab Management ---
    private val _openTabs = MutableStateFlow<List<EditorTab>>(emptyList())
    val openTabs = _openTabs.asStateFlow()

    private val _selectedTabId = MutableStateFlow<String?>(null)
    val selectedTabId = _selectedTabId.asStateFlow()

    // --- Terminal / Process Execution ---
    private val _terminalLogs = MutableStateFlow<List<TerminalLog>>(emptyList())
    val terminalLogs = _terminalLogs.asStateFlow()

    private val _isTermuxConnected = MutableStateFlow(true)
    val isTermuxConnected = _isTermuxConnected.asStateFlow()

    private val _isBuilding = MutableStateFlow(false)
    val isBuilding = _isBuilding.asStateFlow()

    private val _activeCommand = MutableStateFlow<String?>(null)
    val activeCommand = _activeCommand.asStateFlow()

    // --- Bridge & Plugins ---
    private val _bridgeDiagnostics = MutableStateFlow(BridgeDiagnostics())
    val bridgeDiagnostics = _bridgeDiagnostics.asStateFlow()

    private val _bridgePackets = MutableStateFlow<List<BridgePacket>>(emptyList())
    val bridgePackets = _bridgePackets.asStateFlow()

    private val _plugins = MutableStateFlow<List<DeveloperPlugin>>(emptyList())
    val plugins = _plugins.asStateFlow()

    // --- Flutter Emulator Live Visual Simulator State ---
    private val _simulatorUIPrefs = MutableStateFlow(SimulatorUIPrefs())
    val simulatorUIPrefs = _simulatorUIPrefs.asStateFlow()

    init {
        loadDefaultWorkspace()
        loadDefaultPlugins()
        addTerminalLog("SYSTEM: Aura IDE system initialized successfully.", LogType.INFO)
        addTerminalLog("BRIDGE: Connected to Termux Environment socket listener: ${bridgeDiagnostics.value.socketChannel}", LogType.INFO)
        addTerminalLog("Run 'flutter pub get' or click Run to boot compilation workspace.", LogType.PROGRESS)
    }

    private fun loadDefaultWorkspace() {
        val initialWorkspace = listOf(
            FileNode(path = "pubspec.yaml", name = "pubspec.yaml", isDirectory = false, parentPath = ""),
            FileNode(path = "README.md", name = "README.md", isDirectory = false, parentPath = ""),
            FileNode(path = "lib", name = "lib", isDirectory = true, parentPath = "", isExpanded = true),
            FileNode(
                path = "lib/main.dart",
                name = "main.dart",
                isDirectory = false,
                parentPath = "lib",
                content = """import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        primarySwatch: Colors.indigo,
      ),
      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Aura App Runner'),
        backgroundColor: Colors.indigoAccent,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.flash_on, size: 80, color: Colors.amber),
            const SizedBox(height: 24),
            const Text(
              'Welcome to Aura IDE!',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 32, vertical: 8),
              child: Text(
                'Edit this main.dart file to customize layouts or titles, and tap run to hot-reload in real-time!',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 14, color: Colors.grey),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
"""
            ),
            FileNode(path = "lib/widgets", name = "widgets", isDirectory = true, parentPath = "lib", isExpanded = false),
            FileNode(
                path = "lib/widgets/custom_card.dart",
                name = "custom_card.dart",
                isDirectory = false,
                parentPath = "lib/widgets",
                content = """import 'package:flutter/material.dart';

class CodeCard extends StatelessWidget {
  final String text;
  const CodeCard({required this.text, super.key});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Text(text),
      ),
    );
  }
}
"""
            ),
            FileNode(path = "android", name = "android", isDirectory = true, parentPath = "", isExpanded = false),
            FileNode(
                path = "android/build.gradle",
                name = "build.gradle",
                isDirectory = false,
                parentPath = "android",
                content = """// Mock native Android configuration for the shell wrapper script
buildscript {
    repositories {
        google()
        mavenCentral()
    }
}
"""
            )
        )
        _fileSystem.value = initialWorkspace

        // Auto open main.dart by default in tabs
        val mainDart = initialWorkspace.first { it.path == "lib/main.dart" }
        openFileInEditor(mainDart)
    }

    private fun loadDefaultPlugins() {
        val initialPlugins = listOf(
            DeveloperPlugin("git_controller", "Git Version Controller", "Provides git log, push, commit and fetch directly inside the UI sidebars.", true, "FlutterDev Community", "VCS", 4),
            DeveloperPlugin("gemini_assistant", "Gemini Code Copilot", "Generates Dart code snippets, auto-completes state layouts and suggests UI corrections with local intent checks.", false, "Google Labs", "AI", 8),
            DeveloperPlugin("asset_optimizer", "Asset & SVG Prettier", "Lightweight vector compress script to optimize bundle sizes before APK compilations.", false, "Aura Core", "Assets", 2),
            DeveloperPlugin("docker_host", "Docker Web Deployer", "Pushes mock multi-architecture image streams to dynamic local networks automatically on successful runs.", false, "Termux Contributors", "Ops", 3)
        )
        _plugins.value = initialPlugins
    }

    // --- Log Helper ---
    private fun getCurrentTimeString(): String {
        return SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
    }

    private fun addTerminalLog(text: String, type: LogType) {
        _terminalLogs.update { current ->
            current + TerminalLog(text = text, type = type, timestamp = getCurrentTimeString())
        }
    }

    // --- Workspace Action handlers ---

    fun openFileInEditor(file: FileNode) {
        if (file.isDirectory) return
        val existing = _openTabs.value.find { it.fileId == file.id }
        if (existing == null) {
            val freshTab = EditorTab(fileId = file.id, path = file.path, name = file.name)
            _openTabs.update { it + freshTab }
        }
        _selectedTabId.value = file.id
    }

    fun closeTab(fileId: String) {
        val currentTabs = _openTabs.value
        val index = currentTabs.indexOfFirst { it.fileId == fileId }
        if (index == -1) return

        val updatedTabs = currentTabs.filterNot { it.fileId == fileId }
        _openTabs.value = updatedTabs

        if (_selectedTabId.value == fileId) {
            if (updatedTabs.isNotEmpty()) {
                val nextActive = updatedTabs.getOrElse(index) { updatedTabs.last() }
                _selectedTabId.value = nextActive.fileId
            } else {
                _selectedTabId.value = null
            }
        }
    }

    fun toggleFolderExpanded(fileId: String) {
        _fileSystem.update { currentList ->
            currentList.map { node ->
                if (node.id == fileId && node.isDirectory) {
                    node.copy(isExpanded = !node.isExpanded)
                } else {
                    node
                }
            }
        }
    }

    fun createNewFile(name: String, parentPath: String, isDirectory: Boolean) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return

        val path = if (parentPath.isEmpty()) cleanName else "$parentPath/$cleanName"

        // Avoid duplicates
        if (_fileSystem.value.any { it.path.equals(path, ignoreCase = true) }) {
            addTerminalLog("FS ERROR: File or directory at '$path' already exists.", LogType.ERROR)
            return
        }

        val freshNode = FileNode(
            path = path,
            name = cleanName,
            isDirectory = isDirectory,
            parentPath = parentPath,
            content = if (isDirectory) "" else "// Created $cleanName on ${getCurrentTimeString()}\n",
            isExpanded = isDirectory
        )

        _fileSystem.update { it + freshNode }

        if (isDirectory) {
            // Auto expand parent directory to see change
            if (parentPath.isNotEmpty()) {
                _fileSystem.update { list ->
                    list.map { node ->
                        if (node.path == parentPath) node.copy(isExpanded = true) else node
                    }
                }
            }
            addTerminalLog("FS INFO: Directory created successfully: $path", LogType.INFO)
        } else {
            addTerminalLog("FS INFO: File created successfully: $path", LogType.INFO)
            openFileInEditor(freshNode)
        }
    }

    fun deleteFileFromWorkspace(fileId: String) {
        val fileToDelete = _fileSystem.value.find { it.id == fileId } ?: return
        
        // Don't delete lib/main.dart or pubspec.yaml to prevent breaking experience
        if (fileToDelete.path == "lib/main.dart" || fileToDelete.path == "pubspec.yaml") {
            addTerminalLog("FS WARNING: Deleting standard workspace file '${fileToDelete.path}' is protected to maintain compiling prototype state.", LogType.WARNING)
            return
        }

        // Close editor tab
        closeTab(fileId)

        // Delete node and clean recursive children if directory
        _fileSystem.update { current ->
            current.filterNot { node ->
                node.id == fileId || (fileToDelete.isDirectory && node.path.startsWith("${fileToDelete.path}/"))
            }
        }
        addTerminalLog("FS INFO: Deleted filesystem node: ${fileToDelete.path}", LogType.INFO)
    }

    fun updateActiveFileContent(content: String) {
        val activeId = _selectedTabId.value ?: return
        _fileSystem.update { currentFs ->
            currentFs.map { node ->
                if (node.id == activeId) {
                    node.copy(content = content)
                } else {
                    node
                }
            }
        }

        _openTabs.update { tabs ->
            tabs.map { tab ->
                if (tab.fileId == activeId) tab.copy(isDirty = true) else tab
            }
        }
    }

    fun saveActiveFile() {
        val activeId = _selectedTabId.value ?: return
        _openTabs.update { tabs ->
            tabs.map { tab ->
                if (tab.fileId == activeId) tab.copy(isDirty = false) else tab
            }
        }
        val fileNode = _fileSystem.value.find { it.id == activeId }
        if (fileNode != null) {
            addTerminalLog("FS SUCCESS: Content synchrony verified/written for: ${fileNode.path}", LogType.INFO)
            
            // If they edited primary Dart file, we automatically run a quick regex scanner to extract attributes!
            if (fileNode.path == "lib/main.dart") {
                parseAndUpdateSimulatorUIPrefs(fileNode.content)
            }
        }
    }

    // --- Plugin Toggler ---
    fun togglePlugin(pluginId: String) {
        _plugins.update { currentList ->
            currentList.map { plugin ->
                if (plugin.id == pluginId) {
                    val nextVal = !plugin.enabled
                    addTerminalLog("PLUGIN: ${plugin.name} toggled ${if (nextVal) "ON (Enabled)" else "OFF (Disabled)"}", LogType.INFO)
                    plugin.copy(enabled = nextVal)
                } else {
                    plugin
                }
            }
        }
    }

    // --- Simulated Active Socket Packets for Bridge View ---
    fun sendManualBridgePacket(text: String) {
        viewModelScope.launch {
            val idePacket = BridgePacket(
                timestamp = getCurrentTimeString(),
                direction = "IDE ➜ TERMUX",
                content = "SOCKET_WRITE: $text"
            )
            _bridgePackets.update { listOf(idePacket) + it }
            _bridgeDiagnostics.update { it.copy(bytesSent = it.bytesSent + text.length) }

            delay(200)

            val tmReply = when {
                text.contains("PING", ignoreCase = true) -> "PONG (ID: Termux-Daemon-v4)"
                text.contains("GET_VERSION", ignoreCase = true) -> "FLUTTER_CHANNEL_VERSION: Stable 3.19.4"
                text.contains("STATUS", ignoreCase = true) -> "DAEMON_UPTIME: 14500s | FLUTTER_RUN_ACTIVE: false"
                else -> "ACK: Received packet payload successfully."
            }

            val termuxPacket = BridgePacket(
                timestamp = getCurrentTimeString(),
                direction = "TERMUX ➜ IDE",
                content = "SOCKET_READ: $tmReply"
            )
            _bridgePackets.update { listOf(termuxPacket) + it }
            _bridgeDiagnostics.update { it.copy(
                bytesReceived = it.bytesReceived + tmReply.length,
                latencyMs = (8..18).random().toLong()
            ) }
        }
    }

    // --- Dart Regex Code Attribute Extractor ---
    private fun parseAndUpdateSimulatorUIPrefs(code: String) {
        try {
            // Find Appbar Title -> text inside Text('')
            val appBarRegex = Regex("""AppBar\([\s\S]*?title:\s*(?:const\s+)?Text\(\s*['"](.*?)['"]\s*\)""")
            val appBarMatch = appBarRegex.find(code)
            val extractedAppBar = appBarMatch?.groupValues?.get(1) ?: "Aura App Runner"

            // Look for Body/Center Main Text strings
            val bodyTitleRegex = Regex("""Welcome to (.*?)(?:['"]|!)""")
            val titleMatch = bodyTitleRegex.find(code)
            val extractedTitle = if (titleMatch != null) {
                "Welcome to ${titleMatch.groupValues[1]}!"
            } else {
                // Generic center Text extraction that falls inside Text('...') list
                val welcomeAlternativeRegex = Regex("""['"]Welcome to (.*?)['"]""")
                val alternativeMatch = welcomeAlternativeRegex.find(code)
                if (alternativeMatch != null) "Welcome to ${alternativeMatch.groupValues[1]}!" else "Welcome to Aura IDE!"
            }

            // Look for detail user message changes inside first multiline text or text starting with Edit this
            val detailsRegex = Regex("""Text\(\s*['"]((?:Edit this|Edit|Modify|Customize|Tap|Click)[\s\S]*?)['"]""")
            val detailsMatch = detailsRegex.find(code)
            val extractedMessage = detailsMatch?.groupValues?.get(1)?.replace("\n", " ")
                ?: "Edit this main.dart file to customize layouts or titles, and tap run to hot-reload in real-time!"

            // Color search swatch matcher (indigo, green, blue, dark, amber, teal etc)
            val colorList = listOf("indigo", "green", "blue", "teal", "amber", "red", "purple", "orange", "pink", "cyan")
            var selectedColor = "indigo"
            for (color in colorList) {
                if (code.contains("Colors.$color", ignoreCase = true)) {
                    selectedColor = color
                    break
                }
            }

            // Icon tracker
            val iconsList = listOf("flash_on", "favorite", "star", "home", "code", "settings", "android", "build", "face")
            var selectedIcon = "flash_on"
            for (icon in iconsList) {
                if (code.contains("Icons.$icon", ignoreCase = true) || code.contains("Icons.${icon.lowercase()}", ignoreCase = true)) {
                    selectedIcon = icon
                    break
                }
            }

            _simulatorUIPrefs.value = SimulatorUIPrefs(
                appBarTitle = extractedAppBar,
                titleText = extractedTitle,
                bodyText = extractedMessage,
                primaryColorName = selectedColor,
                accentIconName = selectedIcon
            )
        } catch (e: Exception) {
            // Suppress and maintain defaults if syntax has incomplete edits
        }
    }

    // --- Dynamic CLI Command Execution Pipeline (Simulation) ---

    fun runTerminalCommand(command: String) {
        if (_isBuilding.value) return
        _isBuilding.value = true
        _activeCommand.value = command

        viewModelScope.launch {
            addTerminalLog("[bash] \$ $command", LogType.INFO)

            when (command) {
                "flutter pub get" -> executePubGet()
                "flutter build apk" -> executeBuildApk()
                "flutter run --hot" -> executeFlutterRun()
                "flutter clean" -> executeClean()
                else -> {
                    delay(400)
                    addTerminalLog("bash: command not found: $command", LogType.ERROR)
                    _isBuilding.value = false
                    _activeCommand.value = null
                }
            }
        }
    }

    private suspend fun executePubGet() {
        addTerminalLog("Resolving dependencies in workspace...", LogType.PROGRESS)
        delay(600)
        
        // Scan pubspec.yaml if present
        val pubspec = _fileSystem.value.find { it.path == "pubspec.yaml"}
        addTerminalLog("Downloading package bundles from secure pub.dev hub mirror...", LogType.PROGRESS)
        delay(800)
        addTerminalLog("+ flutter_riverpod (v2.4.9) [1.2 MB]", LogType.STDOUT)
        addTerminalLog("+ google_fonts (v6.1.0) [0.4 MB]", LogType.STDOUT)
        addTerminalLog("+ cupertino_icons (v1.0.6) [0.1 MB]", LogType.STDOUT)
        addTerminalLog("+ path_provider (v2.1.2) [0.2 MB]", LogType.STDOUT)
        delay(600)
        
        addTerminalLog("Updating cached .dart_tool package path configuration...", LogType.PROGRESS)
        delay(400)
        addTerminalLog("Stdout: Got 4 dependencies parsed!", LogType.STDOUT)
        addTerminalLog("BUILD SUCCESS: flutter pub get completed in 2.4s.", LogType.SUCCESS)
        
        _isBuilding.value = false
        _activeCommand.value = null
    }

    private suspend fun executeClean() {
        addTerminalLog("Cleaning intermediate directories. Deleting .dart_tool/ and build/ folders...", LogType.PROGRESS)
        delay(800)
        addTerminalLog("Stdout: Cleaned build/ directory (removed 42.1 MB).", LogType.STDOUT)
        addTerminalLog("SUCCESS: Clean operation completed. Cache flushed.", LogType.SUCCESS)
        _isBuilding.value = false
        _activeCommand.value = null
    }

    private suspend fun executeBuildApk() {
        addTerminalLog("Initiating local release build command via intent tunnel.", LogType.PROGRESS)
        delay(500)
        addTerminalLog("Termux: Received command task (ID: flutter_compile). Triggering Flutter CLI Gradle task internally.", LogType.INFO)
        delay(600)
        addTerminalLog("Compiling app with SDK constraints: targetSdk 35 / minSdk 24...", LogType.PROGRESS)
        delay(800)
        addTerminalLog("Executing task: :app:compileReleaseDartSources (AOT compiler)...", LogType.STDOUT)
        delay(1200)
        addTerminalLog("Note: Dart AOT compilation targeting arm64-v8a successful.", LogType.STDOUT)
        delay(800)
        addTerminalLog("Executing task: :app:minifyReleaseWithR8 (Inlining assets)...", LogType.PROGRESS)
        delay(1000)
        addTerminalLog("Assembling APK layout container packages...", LogType.PROGRESS)
        delay(700)
        
        // Generate metrics
        val codeLength = _fileSystem.value.sumOf { it.content.length }
        val sizeMb = 12.4 + (codeLength % 500) / 100.0
        val roundedSize = String.format(Locale.US, "%.1f", sizeMb)

        addTerminalLog("---------------------------------------------------------", LogType.STDOUT)
        addTerminalLog("✓ Built build/app/outputs/flutter-apk/app-release.apk ($roundedSize MB).", LogType.SUCCESS)
        addTerminalLog("---------------------------------------------------------", LogType.STDOUT)
        addTerminalLog("APK BUILD SUCCESSFUL: Compile finished in 5.6s.", LogType.SUCCESS)

        _isBuilding.value = false
        _activeCommand.value = null
    }

    private suspend fun executeFlutterRun() {
        // Trigger save to make sure they get the latest
        saveActiveFile()
        
        addTerminalLog("Launching Dart developer daemon service wrapper...", LogType.PROGRESS)
        delay(500)
        
        // Connect event simulation
        _isTermuxConnected.value = true
        addTerminalLog("TCP Bridge: Local connection established with Termux background loop (port 49152).", LogType.INFO)
        delay(600)
        
        addTerminalLog("Compiling sources in JIT (Just-In-Time) hot reload mode...", LogType.PROGRESS)
        delay(700)
        addTerminalLog("Registering Observatory DevFS content provider on device...", LogType.STDOUT)
        delay(800)
        addTerminalLog("Syncing files to device: 3 files (lib/main.dart)...", LogType.PROGRESS)
        delay(600)
        
        val codeNode = _fileSystem.value.find { it.path == "lib/main.dart" }
        if (codeNode != null) {
            parseAndUpdateSimulatorUIPrefs(codeNode.content)
        }

        addTerminalLog("⚡ Hot reload injection compiled and synced in 384ms.", LogType.SUCCESS)
        addTerminalLog("Stdout: Dynamic layout render frames rendered successfully.", LogType.STDOUT)

        // Inject simulated connection packets to bridge logs
        val startupPacket = BridgePacket(
            timestamp = getCurrentTimeString(),
            direction = "IDE ➜ TERMUX",
            content = "CMD_EXEC: flutter run --machine -d emulator-5554"
        )
        val devFsPacket = BridgePacket(
            timestamp = getCurrentTimeString(),
            direction = "TERMUX ➜ IDE",
            content = "EVENT: DevFS setup complete and listening at WebSockets http://127.0.0.1:49152/obs/"
        )
        val hotReloadPacket = BridgePacket(
            timestamp = getCurrentTimeString(),
            direction = "IDE ➜ TERMUX",
            content = "HOT_RELOAD: Full payload update lib/main.dart size (${codeNode?.content?.length ?: 2048} B)"
        )
        val framePacket = BridgePacket(
            timestamp = getCurrentTimeString(),
            direction = "TERMUX ➜ IDE",
            content = "EVENT: Frame rendering completed on thread (UI: 12ms / Raster: 8ms)"
        )

        _bridgePackets.update { listOf(framePacket, hotReloadPacket, devFsPacket, startupPacket) + it }
        _bridgeDiagnostics.update { it.copy(
            bytesSent = it.bytesSent + 2048,
            bytesReceived = it.bytesReceived + 4096,
            latencyMs = 8
        ) }

        _isBuilding.value = false
        _activeCommand.value = null
    }

    fun clearTerminal() {
        _terminalLogs.value = emptyList()
        addTerminalLog("Console terminal screen flushed.", LogType.INFO)
    }
}
