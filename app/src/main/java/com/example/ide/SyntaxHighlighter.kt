package com.example.ide

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object SyntaxHighlighter {

    // Dark-themed premium VS Code IDE color scheme mapped to Immersive UI Design theme
    private val KeywordColor = Color(0xFFFFB4AB)     // Light coral/pink for keywords
    private val TypeColor = Color(0xFFD0BCFF)        // Soft violet/purple for Types
    private val WidgetColor = Color(0xFFD0BCFF)      // Soft violet/purple for Flutter Widgets
    private val StringColor = Color(0xFFC2EFAD)      // Light lime-mint green for String literals
    private val CommentColor = Color(0xFF8E9199)     // Soft slate-gray for Comments
    private val NumberColor = Color(0xFFD1E4FF)      // High-contrast ice-blue for numbers
    private val AnnotationColor = Color(0xFFFFB4AB)  // Light coral/pink highlights for Annotations
    private val IdentifierColor = Color(0xFFE2E2E6)  // High-contrast ivory for variables & general words
    private val OperatorColor = Color(0xFF8E9199)    // Ice-gray for punctuation, operators and syntax delimiters

    private val DartKeywords = setOf(
        "import", "export", "class", "extends", "implements", "with", "void", "int", "double",
        "String", "bool", "var", "final", "const", "return", "if", "else", "for", "while",
        "switch", "case", "default", "break", "continue", "this", "super", "new", "null",
        "true", "false", "async", "await", "yield", "get", "set", "as", "is", "in", "show", "hide"
    )

    private val FlutterWidgets = setOf(
        "Widget", "StatelessWidget", "StatefulWidget", "State", "BuildContext", "Container",
        "Scaffold", "AppBar", "Text", "Center", "Column", "Row", "FloatingActionButton",
        "TextStyle", "Color", "Icon", "Icons", "MaterialApp", "ThemeData", "ValueNotifier",
        "SizedBox", "Padding", "EdgeInsets", "VoidCallback", "MainAxisAlignment", "CrossAxisAlignment",
        "Theme", "Navigator", "MaterialPageRoute", "ElevatedButton", "TextField", "Card", "RunApp"
    )

    /**
     * Highlights code and returns an AnnotatedString with custom styled spans.
     */
    fun highlight(code: String): AnnotatedString {
        return buildAnnotatedString {
            var index = 0
            val len = code.length

            while (index < len) {
                // 1. Single-line comment check
                if (index + 1 < len && code[index] == '/' && code[index + 1] == '/') {
                    val endOfLine = code.indexOf('\n', index)
                    val endOfComment = if (endOfLine != -1) endOfLine else len
                    withStyle(style = SpanStyle(color = CommentColor)) {
                        append(code.substring(index, endOfComment))
                    }
                    index = endOfComment
                    continue
                }

                // 2. Multi-line comment check
                if (index + 1 < len && code[index] == '/' && code[index + 1] == '*') {
                    val endOfComment = code.indexOf("*/", index + 2)
                    val endIdx = if (endOfComment != -1) endOfComment + 2 else len
                    withStyle(style = SpanStyle(color = CommentColor)) {
                        append(code.substring(index, endIdx))
                    }
                    index = endIdx
                    continue
                }

                // 3. String literals check (single/double quotes)
                if (code[index] == '"' || code[index] == '\'') {
                    val quote = code[index]
                    var endStr = index + 1
                    var escaped = false
                    while (endStr < len) {
                        if (code[endStr] == '\\') {
                            escaped = !escaped
                        } else if (code[endStr] == quote && !escaped) {
                            endStr++
                            break
                        } else {
                            escaped = false
                        }
                        endStr++
                    }
                    withStyle(style = SpanStyle(color = StringColor)) {
                        append(code.substring(index, endStr.coerceAtMost(len)))
                    }
                    index = endStr
                    continue
                }

                // 4. Annotations start with '@'
                if (code[index] == '@') {
                    var endAnn = index + 1
                    while (endAnn < len && (code[endAnn].isLetterOrDigit() || code[endAnn] == '_')) {
                        endAnn++
                    }
                    withStyle(style = SpanStyle(color = AnnotationColor, fontWeight = FontWeight.Bold)) {
                        append(code.substring(index, endAnn))
                    }
                    index = endAnn
                    continue
                }

                // 5. Letter, underscore -> word matching (keywords, widgets, functions)
                if (code[index].isLetter() || code[index] == '_') {
                    var endWord = index + 1
                    while (endWord < len && (code[endWord].isLetterOrDigit() || code[endWord] == '_')) {
                        endWord++
                    }
                    val word = code.substring(index, endWord)
                    when {
                        DartKeywords.contains(word) -> {
                            withStyle(style = SpanStyle(color = KeywordColor, fontWeight = FontWeight.SemiBold)) {
                                append(word)
                            }
                        }
                        FlutterWidgets.contains(word) -> {
                            withStyle(style = SpanStyle(color = WidgetColor, fontWeight = FontWeight.SemiBold)) {
                                append(word)
                            }
                        }
                        word.first().isUpperCase() -> {
                            // Likely a custom Dart Class / Type
                            withStyle(style = SpanStyle(color = TypeColor)) {
                                append(word)
                            }
                        }
                        else -> {
                            append(word)
                        }
                    }
                    index = endWord
                    continue
                }

                // 6. Number check
                if (code[index].isDigit()) {
                    var endNum = index + 1
                    while (endNum < len && (code[endNum].isDigit() || code[endNum] == '.')) {
                        endNum++
                    }
                    withStyle(style = SpanStyle(color = NumberColor)) {
                        append(code.substring(index, endNum))
                    }
                    index = endNum
                    continue
                }

                // 7. General operators / symbols / characters
                val char = code[index]
                if ("{}()[]+-*/%=&|^<>!?,;.:".contains(char)) {
                    withStyle(style = SpanStyle(color = OperatorColor)) {
                        append(char)
                    }
                } else {
                    append(char)
                }
                index++
            }
        }
    }
}
