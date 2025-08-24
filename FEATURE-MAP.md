

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
      - TODO style markers?

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

- ✓ readResource(uri)
  - Some agents will not read a resource without user intervention regardless of audience annotations, yet they will call tools
  - accepts uri for console, editor or file
- ✓ Consoles listConsoles()
- ✓ Editors listEditors()
- ✓ Problems listProblems(resourceURI) 
  - accepts uri for workspace, project, folder or 
  - TODO accept uri for editor
- ✓ Selection currentSelection()
- ✓ Resources listProjects()
- ✓ Resources listChildResources(resourceURI, depth)
- ✓ changeEditorText("editor://eclipse/{editor-tab-name}", TextChanges)
- ✓ void closeEditor("editor://eclipse/{editor-tab-name}")
- ✓ void openEditor(file://{absolute-or-workspace-relative-file-path")
- ✓ void saveEditor("editor://eclipse/{editor-tab-name}")

### Db2 for z/OS

- 🚧 Run Query
  - 🚧 Preference page for connection selection
  - 🚧 Preference page for run options including always rollback
  - 🤔 Preference for read-only queries only
