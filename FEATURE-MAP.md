

# Minimum Viable Product

- Glossary
  - ✓ Available
  - 🤔 Under Consideration
  - 🚧 Working with issues
  - 📅 Post MVP

## Agents

- Eclipse Native
  - 🤔 Code Pilot for Eclipse
- Terminal Driven (including Eclipse Terminal)
  - 🤔 Claude Code (need credits)
    - has an API that can be fed by our MCP primitives
  - 🤔 Aider (no MCP yet?)
  - 🤔 Goose (MCP support)
- Other
  - 🤔 Project Bob? (VS Code) (Roocode/Cline)
  - 🤔 Watsonx Code Assistant? (For Eclipse?, For VS Code?)

## Capabilities

Users can use capabilities to enable/disable a collection of Tool, Recourse, Templates and Prompts.  Tools and Resources can be added/removed dynamically, others require a server restart.

- Agentic Tools
  - Eclipse MCP Server (Single MCP Server, On/Off Toggle, HTTP Port)
    - Built-ins
    - System z
      - MVS
      - JES
      - USS
      - DB2 for z/OS
      - ...
  - Agent Integrations
    - Claude Code
    - Goose
    - Aider
      - TODO-style markers to trigger agent actions

## Preferences

- Agentic Tools
  - MCP Server
    - Platform (Builtin)
    - Db2 for z/OS
      - Runtime Options
  - Agent Integrations
    - Claude Code
    - Goose

## Templates

### CORE

### BUILTIN

- ✓ file://workspace/{project}{relative-path-to-file}
  - ✓ auto-completion
- 🤔 file:///{full-path-to-file}
- ✓ editor://eclipse/{editor-tab-name}
  - ✓ auto-completion
- 🤔 console://eclipse/{console-name}

### MVS

- ✓ mvs://{host}/{pds}/{member}
  - ✓ auto-completion
- 🤔 sequential data set, others?

### 🤔 USS

### 🤔 JES

### 🤔 DB2

- 🤔 Catalog Resources (Tables, Views)
- 🤔 Schema Summary
- 🤔 DDL

## Resources

### BUILT-IN

- 🚧 Editors
  - adds/removes editor://eclipse/{editor-tab-name} resources as editors open and close

## Tools

### BUILT IN

- ✓ public TextEditorSelection currentSelection()
  - Return the text selection of active Eclipse IDE text editor
- ✓ public Editors listEditors()
  - List open Eclipse IDE text editors
- ✓ public Consoles listConsoles()
  - List open Eclipse IDE consoles
- ✓ public Resources listProjects()
  - List open Eclipse IDE projects
- ✓ public Resources listChildResources
  - List child resources of an Eclipse workspace, project or folder
  - args
    - String resourceURI: URI of an eclipse project or folder
    - optional int depth: 0 for immediate children, 1 for children and grandchildren, 2 for infinite depth", required = false
- ✓ public String readResource()
  - Returns the contents of a file, editor, or console URI"
  - args
    - String uri: URI of an eclipse file, editor or console
- 🚧 public void applyPatch()
  - Apply a git unified diff format patch to ?workspace"
  - args
    - String patch: A unified diff format patch to to workspace root
    - String resourceURI: uri to apply the patch to
- ✓ public Editor openEditor
  - open an Eclipse IDE editor on a file and set an initial text selection
  - args
    - fileUri: URI file in the Eclipse workspace
    - optional int selectionOffset: offset of the selected text
    - optional int selectionLength: length of the selected text
- ✓ public void closeEditor()
  - close an Eclipse IDE editor  
  - args
    - String editorUri: URI of an Eclipse editor
- ✓ public boolean saveEditor()
  - save any changes in the editor to file
  - args
    - String editorUri: URI of an Eclipse editor
- 🚧 public boolean changeEditorText()
  - Make one or more changes to an Eclipse text editor
  - args
    - String editorURI: URI of an Eclipse editor
    - TextReplacement replacements: One or more text replacements to be applied in reverese offset order
- ✓ public Problems listProblems()
  - list Eclipse IDE compilation and configuration problems
  - args
    - String resourceURI: Eclipse workspace file URI or editor URI
    - optional String severity:One of ERROR, INFO or WARNING
- ✓ public Tasks listTasks()
  - list codebase locations containing TODO comments
  - args
    - String resourceURI: Eclipse workspace file URI or editor URI

### Db2 for z/OS

- 🚧 Run Query
  - 🚧 Preference page for connection selection
  - 🚧 Preference page for run options including always rollback
  - 🤔 Preference for read-only queries only
