package com.example.ide

import java.util.UUID

/**
 * Represents a node (File or Directory) in our virtual workspace filesystem.
 */
data class FileNode(
    val id: String = UUID.randomUUID().toString(),
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val parentPath: String,
    val isExpanded: Boolean = false,
    val content: String = ""
)

/**
 * Represents an open tab in our Code Editor.
 */
data class EditorTab(
    val fileId: String,
    val path: String,
    val name: String,
    val isDirty: Boolean = false
)

/**
 * Log levels for the Terminal/Build Console.
 */
enum class LogType {
    INFO,       // standard logs
    STDOUT,     // standard CLI output
    SUCCESS,    // green alerts (e.g. build succeeded)
    PROGRESS,   // status updates (e.g. downloads, assembling)
    WARNING,    // amber flags
    ERROR       // red errors of compiler
}

/**
 * A line item logged into the CLI console.
 */
data class TerminalLog(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val type: LogType,
    val timestamp: String
)

/**
 * Extensible developer plugin options that future contributors can add.
 */
data class DeveloperPlugin(
    val id: String,
    val name: String,
    val description: String,
    val enabled: Boolean = false,
    val creator: String,
    val category: String,
    val featuresAddedCount: Int
)

/**
 * Technical metrics of our Monorepo Termux bridge.
 */
data class BridgeDiagnostics(
    val socketChannel: String = "127.0.0.1:49152",
    val latencyMs: Long = 12,
    val isServerRunning: Boolean = true,
    val intentAction: String = "com.termux.tasker.RUN_COMMAND",
    val bytesSent: Long = 1024,
    val bytesReceived: Long = 4056
)
