package com.example.ide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.BackHandler
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

// Visual Color Tokens for Slate Dark Theme
val SlateDarkBg = Color(0xFF0F1115)       // Brand deep slate background
val CodeEditorBg = Color(0xFF0F1115)      // Content area background harmonized
val SidebarBg = Color(0xFF1A1C1E)         // Clean layout bar background
val AccentCyan = Color(0xFFD1E4FF)        // High-contrast ice-blue text/accent color
val AccentPurple = Color(0xFFD0BCFF)      // Warm violet highlight
val CodeTextGray = Color(0xFF8E9199)      // Balanced gray secondary text
val PrimaryAccentGreen = Color(0xFF4CAF50) // High-contrast terminal green
val AmbientBorder = Color(0xFF333538)     // Hard visual separator line color

fun Modifier.swipeTrigger(
    onSwipeUp: (() -> Unit)? = null,
    onSwipeDown: (() -> Unit)? = null,
    onSwipeLeft: (() -> Unit)? = null,
    onSwipeRight: (() -> Unit)? = null,
    threshold: Float = 40f
): Modifier {
    return this.pointerInput(onSwipeUp, onSwipeDown, onSwipeLeft, onSwipeRight) {
        try {
            var totalX = 0f
            var totalY = 0f
            detectDragGestures(
                onDragStart = {
                    totalX = 0f
                    totalY = 0f
                },
                onDrag = { change, dragAmount ->
                    try {
                        change.consume()
                        totalX += dragAmount.x
                        totalY += dragAmount.y
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                onDragEnd = {
                    val absX = Math.abs(totalX)
                    val absY = Math.abs(totalY)
                    if (absX > absY) {
                        if (absX > threshold) {
                            if (totalX > 0) {
                                onSwipeRight?.invoke()
                            } else {
                                onSwipeLeft?.invoke()
                            }
                        }
                    } else {
                        if (absY > threshold) {
                            if (totalY > 0) {
                                onSwipeDown?.invoke()
                            } else {
                                onSwipeUp?.invoke()
                            }
                        }
                    }
                },
                onDragCancel = {
                    totalX = 0f
                    totalY = 0f
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun IDESetupStatusBar(isBuilding: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        color = Color(0xFF004977)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sync,
                        contentDescription = "Sync",
                        tint = Color(0xFFD1E4FF),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "Connected to Termux",
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD1E4FF),
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = "Errors",
                        tint = Color(0xFFD1E4FF),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "0",
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD1E4FF)
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = "Warnings",
                        tint = Color(0xFFD1E4FF),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = if (isBuilding) "1" else "2",
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD1E4FF)
                        )
                    )
                }
            }

            Text(
                text = "Ln 6, Col 12",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFD1E4FF),
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraIDEParentView(viewModel: IDEViewModel) {
    val fileSystem by viewModel.fileSystem.collectAsState()
    val openTabs by viewModel.openTabs.collectAsState()
    val selectedTabId by viewModel.selectedTabId.collectAsState()
    val terminalLogs by viewModel.terminalLogs.collectAsState()
    val isBuilding by viewModel.isBuilding.collectAsState()
    val bridgeDiagnostics by viewModel.bridgeDiagnostics.collectAsState()
    val simulatorPrefs by viewModel.simulatorUIPrefs.collectAsState()

    var activeSidebarMode by remember { mutableStateOf("explorer") } // "explorer", "architecture", "plugins"
    var isSidebarVisible by remember { mutableStateOf(false) } // Default to false (hidden) for maximum edit screen space!
    var isTerminalVisible by remember { mutableStateOf(true) } // Default to true (or terminal is open but can be closed)
    var isLivePreviewVisible by remember { mutableStateOf(false) } // Default to false (can be opened easily)
    var isEditorFocused by remember { mutableStateOf(false) }
    var showNewNodeDialog by remember { mutableStateOf<Pair<Boolean, String>?>(null) } // pair(isDirectory, parentPath)

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Combined BackHandler: closes sidebar or clears editor focus before exiting applet
    BackHandler(enabled = isSidebarVisible || isEditorFocused || isTerminalVisible || isLivePreviewVisible) {
        if (isSidebarVisible) {
            isSidebarVisible = false
        } else if (isEditorFocused) {
            focusManager.clearFocus()
            keyboardController?.hide()
        } else if (isTerminalVisible || isLivePreviewVisible) {
            isTerminalVisible = false
            isLivePreviewVisible = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { isSidebarVisible = !isSidebarVisible }
                    ) {
                        Icon(
                            imageVector = if (isSidebarVisible) Icons.Rounded.MenuOpen else Icons.Rounded.Menu,
                            contentDescription = "Toggle Sidebar Panel",
                            tint = Color(0xFFD1E4FF)
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF004977)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Terminal,
                                contentDescription = "Terminal core logo",
                                tint = Color(0xFFD1E4FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "flutter_bridge_core",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFE2E2E6),
                                    fontFamily = FontFamily.SansSerif
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AccountTree,
                                    contentDescription = "branch main",
                                    tint = Color(0xFF8E9199),
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "main",
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        color = Color(0xFF8E9199),
                                        fontFamily = FontFamily.SansSerif
                                    )
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Daemon service connection state dot
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isBuilding) Color.Yellow else PrimaryAccentGreen)
                        )
                        Text(
                            text = if (isBuilding) "COMPILING" else "TERMUX BRIDGE CONNECTED",
                            style = TextStyle(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isBuilding) Color.Yellow else PrimaryAccentGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.runTerminalCommand("flutter run --hot") },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Run compiler",
                            tint = Color(0xFFD1E4FF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SidebarBg,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            IDESetupStatusBar(
                isBuilding = isBuilding,
                modifier = Modifier.swipeTrigger(
                    onSwipeUp = {
                        isTerminalVisible = true
                        isLivePreviewVisible = true
                    }
                )
            )
        },
        containerColor = SlateDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main workspace with left sidebar tabs & viewport
            Row(
                modifier = Modifier
                    .weight(if (isTerminalVisible || isLivePreviewVisible) 1.25f else 1f)
                    .fillMaxWidth()
            ) {
                // VS Code Sidebar Icons Tab Selector
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(56.dp)
                        .background(SidebarBg)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SidebarIconButton(
                        imageVector = Icons.Rounded.FolderOpen,
                        label = "Files",
                        isActive = isSidebarVisible && activeSidebarMode == "explorer",
                        onClick = {
                            if (isSidebarVisible && activeSidebarMode == "explorer") {
                                isSidebarVisible = false
                            } else {
                                activeSidebarMode = "explorer"
                                isSidebarVisible = true
                            }
                        }
                    )
                    SidebarIconButton(
                        imageVector = Icons.Rounded.Hub,
                        label = "Termux",
                        isActive = isSidebarVisible && activeSidebarMode == "architecture",
                        onClick = {
                            if (isSidebarVisible && activeSidebarMode == "architecture") {
                                isSidebarVisible = false
                            } else {
                                activeSidebarMode = "architecture"
                                isSidebarVisible = true
                            }
                        }
                    )
                    SidebarIconButton(
                        imageVector = Icons.Rounded.Extension,
                        label = "Plugins",
                        isActive = isSidebarVisible && activeSidebarMode == "plugins",
                        onClick = {
                            if (isSidebarVisible && activeSidebarMode == "plugins") {
                                isSidebarVisible = false
                            } else {
                                activeSidebarMode = "plugins"
                                isSidebarVisible = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Shortcuts to toggle bottom sections dynamically
                    SidebarIconButton(
                        imageVector = Icons.Rounded.Terminal,
                        label = "Terminal",
                        isActive = isTerminalVisible,
                        onClick = { isTerminalVisible = !isTerminalVisible }
                    )
                    SidebarIconButton(
                        imageVector = Icons.Rounded.PlayCircle,
                        label = "Preview",
                        isActive = isLivePreviewVisible,
                        onClick = { isLivePreviewVisible = !isLivePreviewVisible }
                    )
                }

                // Vertical Divider
                Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(AmbientBorder))

                // Selected Sidebar Panel
                if (isSidebarVisible) {
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .background(SlateDarkBg)
                    ) {
                        when (activeSidebarMode) {
                            "explorer" -> WorkspaceExplorerPanel(
                                fileSystem = fileSystem,
                                onFileClick = { node ->
                                    viewModel.openFileInEditor(node)
                                    isSidebarVisible = false // Auto-close folder explorer panel when a file is tapped to give full screen to code!
                                },
                                onToggleFolder = { viewModel.toggleFolderExpanded(it) },
                                onCreateNode = { isDir, pPath -> showNewNodeDialog = Pair(isDir, pPath) },
                                onDeleteNode = { viewModel.deleteFileFromWorkspace(it) },
                                onClose = { isSidebarVisible = false }
                            )
                            "architecture" -> BridgeArchitecturePanel(viewModel)
                            "plugins" -> PluginsPanel(viewModel)
                        }
                    }

                    // Vertical Divider
                    Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(AmbientBorder))
                }

                // Active Tab Code Editor pane
                Box(
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight()
                ) {
                    val activeId = selectedTabId
                    if (activeId != null) {
                        val activeNode = fileSystem.find { it.id == activeId }
                        if (activeNode != null) {
                            CodeEditorWorkspaceView(
                                fileNode = activeNode,
                                openTabs = openTabs,
                                activeTabId = activeId,
                                onTabSelect = { id ->
                                    val target = fileSystem.find { it.id == id }
                                    if (target != null) viewModel.openFileInEditor(target)
                                },
                                onTabClose = { viewModel.closeTab(it) },
                                onContentChange = { viewModel.updateActiveFileContent(it) },
                                onSave = { viewModel.saveActiveFile() },
                                onFocusChanged = { isEditorFocused = it }
                            )
                        } else {
                            EmptyEditorContent()
                        }
                    } else {
                        EmptyEditorContent()
                    }
                }
            }

            if (isTerminalVisible || isLivePreviewVisible) {
                // Divider separating code block from terminal & emulator simulator bottom section
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AmbientBorder))

                // Horizontal dual pane bottom workspace: Left = Build Console Terminal, Right = Simulated Hot Reload Device
                Row(
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxWidth()
                ) {
                    if (isTerminalVisible) {
                        // Interactive Terminal Console (Runs CLI)
                        Box(
                            modifier = Modifier
                                .weight(if (isLivePreviewVisible) 1.25f else 1f)
                                .fillMaxHeight()
                                .background(CodeEditorBg)
                        ) {
                            TerminalConsolePanel(
                                logs = terminalLogs,
                                isBuilding = isBuilding,
                                onTriggerCommand = { viewModel.runTerminalCommand(it) },
                                onClear = { viewModel.clearTerminal() },
                                onSwipeDown = {
                                    isTerminalVisible = false
                                },
                                onSwipeLeft = {
                                    if (isTerminalVisible && !isLivePreviewVisible) {
                                        isTerminalVisible = false
                                        isLivePreviewVisible = true
                                    }
                                },
                                onSwipeRight = {
                                    if (isTerminalVisible && !isLivePreviewVisible) {
                                        isTerminalVisible = false
                                        isLivePreviewVisible = true
                                    }
                                }
                            )
                        }
                    }

                    if (isTerminalVisible && isLivePreviewVisible) {
                        // Vertical Divider
                        Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(AmbientBorder))
                    }

                    if (isLivePreviewVisible) {
                        // Live Simulator Preview Frame
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(SlateDarkBg)
                        ) {
                            LivePreviewSimulatorContainer(
                                prefs = simulatorPrefs,
                                isCompiling = isBuilding,
                                onHotReloadTrigger = { viewModel.runTerminalCommand("flutter run --hot") },
                                onSwipeDown = {
                                    isLivePreviewVisible = false
                                },
                                onSwipeLeft = {
                                    if (isLivePreviewVisible && !isTerminalVisible) {
                                        isLivePreviewVisible = false
                                        isTerminalVisible = true
                                    }
                                },
                                onSwipeRight = {
                                    if (isLivePreviewVisible && !isTerminalVisible) {
                                        isLivePreviewVisible = false
                                        isTerminalVisible = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Create Dialog node (File or Folder helper)
        showNewNodeDialog?.let { data ->
            val isDir = data.first
            val parentPath = data.second
            CreateNodeDialog(
                isDir = isDir,
                parentPath = parentPath,
                onDismiss = { showNewNodeDialog = null },
                onSubmit = { name ->
                    viewModel.createNewFile(name, parentPath, isDir)
                    showNewNodeDialog = null
                }
            )
        }
    }
}

// ============================
// SIDEBAR TABS WIDGETS
// ============================

@Composable
fun SidebarIconButton(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = label,
            tint = if (isActive) AccentCyan else Color(0xFF8E9199),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = TextStyle(
                fontSize = 8.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) AccentCyan else Color(0xFF8E9199),
                textAlign = TextAlign.Center
            )
        )
    }
}

// ============================
// 📁 WORKSPACE EXPLORER PANEL
// ============================

@Composable
fun WorkspaceExplorerPanel(
    fileSystem: List<FileNode>,
    onFileClick: (FileNode) -> Unit,
    onToggleFolder: (String) -> Unit,
    onCreateNode: (Boolean, String) -> Unit, // Boolean: isDirectory, String: parentPath
    onDeleteNode: (String) -> Unit,
    onClose: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .swipeTrigger(onSwipeLeft = onClose)
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        // Workspace Headings
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (onClose != null) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = "Close Panel",
                            tint = Color(0xFF8E9199),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = "FILES",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E9199),
                        letterSpacing = 1.5.sp
                    )
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { onCreateNode(false, "") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NoteAdd,
                        contentDescription = "New File",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = { onCreateNode(true, "") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CreateNewFolder,
                        contentDescription = "New Directory",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(AmbientBorder))
        Spacer(modifier = Modifier.height(8.dp))

        // Folders and items listed
        val rootNodes = fileSystem.filter { it.parentPath.isEmpty() }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(rootNodes) { node ->
                RenderFileSystemNode(
                    node = node,
                    allNodes = fileSystem,
                    depth = 0,
                    onFileClick = onFileClick,
                    onToggleFolder = onToggleFolder,
                    onCreateNode = onCreateNode,
                    onDeleteNode = onDeleteNode
                )
            }
        }
    }
}

@Composable
fun RenderFileSystemNode(
    node: FileNode,
    allNodes: List<FileNode>,
    depth: Int,
    onFileClick: (FileNode) -> Unit,
    onToggleFolder: (String) -> Unit,
    onCreateNode: (Boolean, String) -> Unit,
    onDeleteNode: (String) -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(if (isHovered) Color.White.copy(alpha = 0.03f) else Color.Transparent)
                .clickable {
                    if (node.isDirectory) {
                        onToggleFolder(node.id)
                    } else {
                        onFileClick(node)
                    }
                }
                .padding(start = (depth * 10).dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Expanded/Collapsed Icon
            if (node.isDirectory) {
                Icon(
                    imageVector = if (node.isExpanded) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "Expand folder",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }

            Spacer(modifier = Modifier.width(4.dp))

            // File Type Icon
            Icon(
                imageVector = if (node.isDirectory) {
                    if (node.isExpanded) Icons.Rounded.FolderOpen else Icons.Rounded.Folder
                } else {
                    if (node.name.endsWith(".yaml")) Icons.Rounded.SettingsApplications
                    else if (node.name.endsWith(".md")) Icons.Rounded.Article
                    else Icons.Rounded.InsertDriveFile
                },
                contentDescription = node.name,
                tint = if (node.isDirectory) Color(0xFFF1C40F) else AccentCyan.copy(alpha = 0.85f),
                modifier = Modifier.size(15.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Text tag of path name
            Text(
                text = node.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (node.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (node.isDirectory) Color.White else Color.LightGray
                )
            )

            // Direct actions hover row bar
            Row {
                if (node.isDirectory) {
                    IconButton(
                        onClick = { onCreateNode(false, node.path) },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NoteAdd,
                            contentDescription = "Add File in Folder",
                            tint = Color.LightGray.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                IconButton(
                    onClick = { onDeleteNode(node.id) },
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete from Workspace",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Expanded Folders Recursive Children rendering
        if (node.isDirectory && node.isExpanded) {
            val children = allNodes.filter { it.parentPath == node.path }
            children.forEach { baby ->
                RenderFileSystemNode(
                    node = baby,
                    allNodes = allNodes,
                    depth = depth + 1,
                    onFileClick = onFileClick,
                    onToggleFolder = onToggleFolder,
                    onCreateNode = onCreateNode,
                    onDeleteNode = onDeleteNode
                )
            }
        }
    }
}

// Dialog element helper to create new Nodes
@Composable
fun CreateNodeDialog(
    isDir: Boolean,
    parentPath: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SidebarBg),
            modifier = Modifier.width(280.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isDir) "Create New Directory" else "Create New Dart/Yaml File",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                if (parentPath.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Sub-dir inside: $parentPath/",
                        style = TextStyle(fontSize = 10.sp, color = AccentCyan)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    placeholder = {
                        Text(
                            text = if (isDir) "widgets" else "screen.dart",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f)
                    ),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = Color.LightGray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(fileName) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                    ) {
                        Text("CREATE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ============================
// ⚙️ ARCHITECTURE BRIDGE EXP AREA
// ============================

@Composable
fun BridgeArchitecturePanel(viewModel: IDEViewModel) {
    val packets by viewModel.bridgePackets.collectAsState()
    val diagnostics by viewModel.bridgeDiagnostics.collectAsState()
    var manualInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            text = "BRIDGE DIAGNOSTICS",
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Tech layer visual representations
        Card(
            colors = CardDefaults.cardColors(containerColor = SidebarBg.copy(alpha = 0.9f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "AURA IPC TUNNEL NETWORKS",
                    style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = AccentCyan)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("SOCKET PORT", style = TextStyle(fontSize = 8.sp, color = Color.Gray))
                        Text(diagnostics.socketChannel, style = TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.White))
                    }
                    Column {
                        Text("LATENCY TEST", style = TextStyle(fontSize = 8.sp, color = Color.Gray))
                        Text("${diagnostics.latencyMs} ms", style = TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = PrimaryAccentGreen))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TX PACKET BYTES", style = TextStyle(fontSize = 8.sp, color = Color.Gray))
                        Text("${diagnostics.bytesSent} B", style = TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.White))
                    }
                    Column {
                        Text("RX PACKET BYTES", style = TextStyle(fontSize = 8.sp, color = Color.Gray))
                        Text("${diagnostics.bytesReceived} B", style = TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.White))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "INTENT HANDSHAKE TESTER",
            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Intercom typing textbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = manualInput,
                onValueChange = { manualInput = it },
                placeholder = { Text("PING / STATUS / VERSION", style = TextStyle(fontSize = 10.sp, color = Color.Gray)) },
                modifier = Modifier.weight(1f).height(42.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.LightGray,
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.12f)
                ),
                textStyle = TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = {
                    if (manualInput.trim().isNotEmpty()) {
                        viewModel.sendManualBridgePacket(manualInput.trim())
                        manualInput = ""
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentCyan)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send packet",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "NETWORK PACKET STREAM",
            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Packet history log box streams
        Card(
            colors = CardDefaults.cardColors(containerColor = CodeEditorBg),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (packets.isEmpty()) {
                    item {
                        Text(
                            text = "Socket stream empty. Launch compiling processes or type command above to invoke TCP logs.",
                            style = TextStyle(fontSize = 9.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    items(packets) { pkg ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.02f))
                                .padding(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = pkg.direction,
                                    style = TextStyle(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (pkg.direction.contains("IDE")) AccentCyan else AccentPurple
                                    )
                                )
                                Text(
                                    text = pkg.timestamp,
                                    style = TextStyle(fontSize = 7.sp, color = Color.Gray)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = pkg.content,
                                style = TextStyle(
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.LightGray
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================
// 🔌 DYNAMIC PLUGINS LOADER PANEL
// ============================

@Composable
fun PluginsPanel(viewModel: IDEViewModel) {
    val plugins by viewModel.plugins.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            text = "PLUGINS STORE",
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                letterSpacing = 1.sp
            )
        )
        Text(
            text = "Extend IDE features with custom background helper streams.",
            style = TextStyle(fontSize = 8.sp, color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plugins) { plugin ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (plugin.enabled) SidebarBg else SidebarBg.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plugin.name,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (plugin.enabled) AccentCyan else Color.LightGray
                                    )
                                )
                                Text(
                                    text = "by ${plugin.creator} | Cat: ${plugin.category}",
                                    style = TextStyle(fontSize = 7.sp, color = Color.Gray)
                                )
                            }
                            Switch(
                                checked = plugin.enabled,
                                onCheckedChange = { viewModel.togglePlugin(plugin.id) },
                                modifier = Modifier.scale(0.7f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AccentCyan,
                                    checkedTrackColor = AccentCyan.copy(alpha = 0.3f),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.White.copy(alpha = 0.05f)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = plugin.description,
                            style = TextStyle(fontSize = 9.sp, color = Color.LightGray.copy(alpha = 0.8f))
                        )
                    }
                }
            }
        }
    }
}

// Helper Extension to scale down Switch removed. Scale extension imported directly from androidx.compose.ui.draw.scale

// ============================
// 🖊️ CODE EDITOR WORKSPACE VIEW
// ============================

@Composable
fun CodeEditorWorkspaceView(
    fileNode: FileNode,
    openTabs: List<EditorTab>,
    activeTabId: String,
    onTabSelect: (String) -> Unit,
    onTabClose: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CodeEditorBg)
    ) {
        // Tab Headers Container List matching modern VS Code tab designs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SidebarBg)
                .height(40.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            openTabs.forEach { tab ->
                val isActive = tab.fileId == activeTabId
                Box(
                    modifier = Modifier
                        .background(if (isActive) CodeEditorBg else SidebarBg)
                        .clickable { onTabSelect(tab.fileId) }
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Top item active blue indicator stroke line
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(AccentCyan)
                                .align(Alignment.TopCenter)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (tab.name.endsWith(".yaml")) Icons.Rounded.SettingsApplications else Icons.Rounded.Code,
                            contentDescription = "File indicator",
                            tint = if (isActive) PrimaryAccentGreen else Color.Gray,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (tab.isDirty) "${tab.name} •" else tab.name,
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isActive) AccentCyan else Color.White.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        IconButton(
                            onClick = { onTabClose(tab.fileId) },
                            modifier = Modifier.size(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close tab",
                                tint = if (isActive) Color.White.copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.4f),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(AmbientBorder))
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AmbientBorder))

        // Active File editor operations bar (File title + SAVE option button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(SidebarBg)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "workspace/${fileNode.path}",
                style = TextStyle(fontSize = 10.sp, color = Color(0xFF8E9199), fontFamily = FontFamily.Monospace),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                // Command palette Save button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF004977))
                        .clickable { onSave() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SAVE",
                        style = TextStyle(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD1E4FF),
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AmbientBorder))

        // Quick Input symbols Accessory Bar
        val quickKeys = listOf("{", "}", "(", ")", "[", "]", ";", ",", "=", "<", ">", "\"", "'")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(SidebarBg)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            quickKeys.forEach { symbol ->
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.04f), shape = RoundedCornerShape(4.dp))
                        .clickable {
                            val nextContent = fileNode.content + symbol
                            onContentChange(nextContent)
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol,
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AmbientBorder))

        // Code Editor text canvas pane
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Numbers listing
            val linesCount = fileNode.content.lines().size
            LazyColumn(
                modifier = Modifier
                    .width(36.dp)
                    .fillMaxHeight()
                    .background(SidebarBg)
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items((1..linesCount).toList()) { index ->
                    Text(
                        text = "$index",
                        style = TextStyle(
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (index == 6) Color(0xFFD1E4FF) else Color(0xFF8E9199),
                            fontWeight = if (index == 6) FontWeight.Bold else FontWeight.Normal
                        ),
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(AmbientBorder))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp)
            ) {
                // Customized Dart code tokenizer renderer
                val textFieldValue = remember(fileNode.id, fileNode.content) {
                    mutableStateOf(TextFieldValue(fileNode.content))
                }

                BasicTextField(
                    value = textFieldValue.value,
                    onValueChange = {
                        textFieldValue.value = it
                        onContentChange(it.text)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .onFocusChanged { state ->
                            onFocusChanged(state.isFocused)
                        },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFE2E2E6),
                        lineHeight = 15.sp
                    ),
                    visualTransformation = { text ->
                        androidx.compose.ui.text.input.TransformedText(
                            text = SyntaxHighlighter.highlight(text.text),
                            offsetMapping = androidx.compose.ui.text.input.OffsetMapping.Identity
                        )
                    },
                    cursorBrush = SolidColor(AccentCyan)
                )
            }
        }
    }
}

@Composable
fun EmptyEditorContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CodeEditorBg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Code,
            contentDescription = "No Code File",
            tint = Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "No Active File Opened",
            style = TextStyle(fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = "Double tap a workspace file in sidebar to begin",
            style = TextStyle(fontSize = 9.sp, color = Color.Gray.copy(alpha = 0.7f))
        )
    }
}

// ============================
// 💻 BUILD CONSOLE TERMINAL
// ============================

@Composable
fun TerminalConsolePanel(
    logs: List<TerminalLog>,
    isBuilding: Boolean,
    onTriggerCommand: (String) -> Unit,
    onClear: () -> Unit,
    onSwipeDown: (() -> Unit)? = null,
    onSwipeLeft: (() -> Unit)? = null,
    onSwipeRight: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CodeEditorBg)
    ) {
        // Output Section Header matching Immersive UI html precisely
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF151719))
                .swipeTrigger(
                    onSwipeDown = onSwipeDown,
                    onSwipeLeft = onSwipeLeft,
                    onSwipeRight = onSwipeRight
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = "TERMUX OUTPUT",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD1E4FF),
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(modifier = Modifier.width(36.dp).height(2.dp).background(Color(0xFFD1E4FF)))
                }
                Text(
                    text = "DEBUG CONSOLE",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8E9199),
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF004977))
                        .clickable { onClear() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CLEAR",
                        style = TextStyle(
                            fontSize = 8.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFFD1E4FF),
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AmbientBorder))

        // CLI Automation Command triggers bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SidebarBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val buildOps = listOf(
                "flutter pub get" to "PUB GET",
                "flutter run --hot" to "HOT RUN",
                "flutter build apk" to "BUILD APK",
                "flutter clean" to "CLEAN"
            )
            buildOps.forEach { op ->
                Button(
                    onClick = { if (!isBuilding) onTriggerCommand(op.first) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (op.second.contains("RUN")) Color(0xFF004977) else SlateDarkBg,
                        contentColor = if (op.second.contains("RUN")) Color(0xFFD1E4FF) else Color(0xFFE2E2E6)
                    ),
                    modifier = Modifier
                        .height(26.dp)
                        .border(
                            width = 1.dp,
                            color = if (op.second.contains("RUN")) Color(0xFFD1E4FF).copy(alpha = 0.3f) else AmbientBorder,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    shape = RoundedCornerShape(4.dp),
                    enabled = !isBuilding
                ) {
                    Text(
                        text = op.second,
                        style = TextStyle(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AmbientBorder))

        // System output streams list
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            text = "Terminal logs ready. Click 'HOT RUN' or 'BUILD APK' pipeline automation triggers above.",
                            style = TextStyle(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF8E9199),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                } else {
                    items(logs) { log ->
                        val color = when (log.type) {
                            LogType.INFO -> AccentCyan.copy(alpha = 0.85f)
                            LogType.STDOUT -> Color(0xFFE2E2E6)
                            LogType.SUCCESS -> PrimaryAccentGreen
                            LogType.PROGRESS -> Color(0xFFD1E4FF)
                            LogType.WARNING -> Color(0xFFE67E22)
                            LogType.ERROR -> Color(0xFFFFB4AB)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "[${log.timestamp}] ",
                                style = TextStyle(
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF8E9199)
                                )
                            )
                            Text(
                                text = log.text,
                                style = TextStyle(
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = color,
                                    lineHeight = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================
// 📱 LIVE HOT RELOAD SIMULATOR PREVIEW
// ============================

@Composable
fun LivePreviewSimulatorContainer(
    prefs: SimulatorUIPrefs,
    isCompiling: Boolean,
    onHotReloadTrigger: () -> Unit,
    onSwipeDown: (() -> Unit)? = null,
    onSwipeLeft: (() -> Unit)? = null,
    onSwipeRight: (() -> Unit)? = null
) {
    // Pulse animation config during compiling/reloads
    val infiniteTransition = rememberInfiniteTransition(label = "compiler_pulse")
    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = keyframes { durationMillis = 1000 },
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .swipeTrigger(
                    onSwipeDown = onSwipeDown,
                    onSwipeLeft = onSwipeLeft,
                    onSwipeRight = onSwipeRight
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LIVE EMULATOR PREVIEW",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    letterSpacing = 0.5.sp
                )
            )

            IconButton(
                onClick = onHotReloadTrigger,
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FlashOn,
                    contentDescription = "Trigger compile reload hot",
                    tint = Color.Yellow,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Device wrapper canvas
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(2.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1C2229))
            ) {
                if (isCompiling) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.75f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .scale(scaleFactor),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = AccentCyan,
                                    radius = size.minDimension / 2,
                                    style = Stroke(width = 4f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Rounded.Sync,
                                contentDescription = "Compiling indicator",
                                tint = AccentCyan,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Assembling resources...",
                            style = TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AccentCyan)
                        )
                    }
                }

                // Emulator visual device content layout
                Column(modifier = Modifier.fillMaxSize()) {
                    // System Status Bar Simulator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("9:41", style = TextStyle(fontSize = 7.sp, fontWeight = FontWeight.SemiBold, color = Color.White))
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(imageVector = Icons.Rounded.Wifi, contentDescription = "wifi", tint = Color.White, modifier = Modifier.size(7.dp))
                            Icon(imageVector = Icons.Rounded.BatteryChargingFull, contentDescription = "battery", tint = Color.White, modifier = Modifier.size(7.dp))
                        }
                    }

                    // Simulated Flutter App Bar Frame Container
                    val parsedThemeColor = when (prefs.primaryColorName.lowercase()) {
                        "indigo" -> Color(0xFF3F51B5)
                        "green" -> Color(0xFF2ECC71)
                        "blue" -> Color(0xFF3498DB)
                        "teal" -> Color(0xFF1ABC9C)
                        "amber" -> Color(0xFFF1C40F)
                        "red" -> Color(0xFFE74C3C)
                        "purple" -> Color(0xFF9B59B6)
                        "orange" -> Color(0xFFE67E22)
                        "pink" -> Color(0xFFE91E63)
                        "cyan" -> Color(0xFF00BCD4)
                        else -> Color(0xFF3F51B5)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .background(parsedThemeColor),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Rounded.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(14.dp))
                        Text(
                            text = prefs.appBarTitle,
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Application View Body
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val parsedIcon = when (prefs.accentIconName.lowercase()) {
                            "flash_on" -> Icons.Rounded.FlashOn
                            "favorite" -> Icons.Rounded.Favorite
                            "star" -> Icons.Rounded.Star
                            "home" -> Icons.Rounded.Home
                            "code" -> Icons.Rounded.Code
                            "android" -> Icons.Default.Android
                            "build" -> Icons.Rounded.Build
                            "face" -> Icons.Rounded.Face
                            else -> Icons.Rounded.Smartphone
                        }
                        
                        Icon(
                            imageVector = parsedIcon,
                            contentDescription = "Simulated icon",
                            tint = if (prefs.accentIconName == "favorite") Color.Red else Color.Yellow,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = prefs.titleText,
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = prefs.bodyText,
                            style = TextStyle(fontSize = 8.sp, color = Color.LightGray, lineHeight = 11.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    // Floating action button inside emulator simulator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(parsedThemeColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = "sim_add", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
