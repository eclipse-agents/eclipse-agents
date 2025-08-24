{
  "resourceTemplates": [
    {
      "name": "Eclipse Editor",
      "uriTemplate": "eclipse://editor/{name}",
      "description": "Content of an Eclipse Text Editor",
      "mimeType": "text/plain",
      "annotations": {
        "audience": [],
        "priority": -1
      }
    },
    {
      "name": "Eclipse Workspace File",
      "uriTemplate": "file://workspace/{relative-path}",
      "description": "Content of an file in an Eclipse workspace",
      "mimeType": "text/plain",
      "annotations": {
        "audience": [],
        "priority": -1
      }
    }
  ],
  "tools": [
    {
      "name": "currentSelection",
      "title": "Currrent Selection",
      "description": "Return the text selection of active Eclipse IDE text editor",
      "inputSchema": {
        "type": "object",
        "properties": {},
        "required": []
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "editor": {
            "allOf": [
              {
                "type": "object",
                "properties": {
                  "buffer": {
                    "$ref": "#/$defs/ResourceLink",
                    "description": "The contents of the text editor"
                  },
                  "file": {
                    "$ref": "#/$defs/ResourceLink",
                    "description": "The file being edited containing the last saved changes"
                  },
                  "isActive": {
                    "type": "boolean",
                    "description": "Whether this is the editor has the user's focus"
                  },
                  "isDirty": {
                    "type": "boolean",
                    "description": "Whether text editor contains unsaved changes"
                  },
                  "name": {
                    "type": "string",
                    "description": "Title of this editor"
                  }
                },
                "description": "An Eclipse IDE text editor"
              },
              {
                "description": "Selected Text Editor"
              }
            ]
          },
          "textSelection": {
            "allOf": [
              {
                "type": "object",
                "properties": {
                  "endLine": {
                    "type": "integer",
                    "description": "line of the last character of the selected text"
                  },
                  "length": {
                    "type": "integer",
                    "description": "length of the text selection"
                  },
                  "offset": {
                    "type": "integer",
                    "description": "position of the first selected character"
                  },
                  "startLine": {
                    "type": "integer",
                    "description": "line of the offset of the selected text"
                  },
                  "text": {
                    "type": "string",
                    "description": "selected text"
                  }
                },
                "description": "Range of characters selected in a text editor"
              },
              {
                "description": "Selected text"
              }
            ]
          }
        },
        "$defs": {
          "ResourceLink": {
            "type": "object",
            "properties": {
              "_meta": {
                "type": "object"
              },
              "annotations": {
                "type": "object",
                "properties": {
                  "audience": {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "enum": [
                        "user",
                        "assistant"
                      ]
                    }
                  },
                  "priority": {
                    "type": "number"
                  }
                }
              },
              "description": {
                "type": "string"
              },
              "mimeType": {
                "type": "string"
              },
              "name": {
                "type": "string"
              },
              "size": {
                "type": "integer"
              },
              "title": {
                "type": "string"
              },
              "uri": {
                "type": "string"
              },
              "type": {
                "const": "resource_link"
              }
            },
            "required": [
              "type"
            ]
          }
        },
        "description": ""
      },
      "annotations": {
        "title": "Currrent Selection",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "listEditors",
      "title": "List Editors",
      "description": "List open Eclipse IDE text editors",
      "inputSchema": {
        "type": "object",
        "properties": {},
        "required": []
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "editors": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "buffer": {
                  "$ref": "#/$defs/ResourceLink",
                  "description": "The contents of the text editor"
                },
                "file": {
                  "$ref": "#/$defs/ResourceLink",
                  "description": "The file being edited containing the last saved changes"
                },
                "isActive": {
                  "type": "boolean",
                  "description": "Whether this is the editor has the user's focus"
                },
                "isDirty": {
                  "type": "boolean",
                  "description": "Whether text editor contains unsaved changes"
                },
                "name": {
                  "type": "string",
                  "description": "Title of this editor"
                }
              },
              "description": "An Eclipse IDE text editor"
            }
          }
        },
        "$defs": {
          "ResourceLink": {
            "type": "object",
            "properties": {
              "_meta": {
                "type": "object"
              },
              "annotations": {
                "type": "object",
                "properties": {
                  "audience": {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "enum": [
                        "user",
                        "assistant"
                      ]
                    }
                  },
                  "priority": {
                    "type": "number"
                  }
                }
              },
              "description": {
                "type": "string"
              },
              "mimeType": {
                "type": "string"
              },
              "name": {
                "type": "string"
              },
              "size": {
                "type": "integer"
              },
              "title": {
                "type": "string"
              },
              "uri": {
                "type": "string"
              },
              "type": {
                "const": "resource_link"
              }
            },
            "required": [
              "type"
            ]
          }
        },
        "description": "List of Eclipse IDE text editors"
      },
      "annotations": {
        "title": "List Editors",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "listConsoles",
      "title": "List Consoles",
      "description": "List open Eclipse IDE consoles",
      "inputSchema": {
        "type": "object",
        "properties": {},
        "required": []
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "consoles": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "name": {
                  "type": "string",
                  "description": "Console name"
                },
                "type": {
                  "type": "string"
                },
                "uri": {
                  "type": "string"
                }
              },
              "description": "An Eclipse IDE console"
            }
          }
        },
        "description": "List of Eclipse IDE consoles"
      },
      "annotations": {
        "title": "List Consoles",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "listProjects",
      "title": "List Projects",
      "description": "List open Eclipse IDE projects",
      "inputSchema": {
        "type": "object",
        "properties": {},
        "required": []
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "resources": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "workspace_uri": {
                  "type": "object",
                  "properties": {
                    "_meta": {
                      "type": "object"
                    },
                    "annotations": {
                      "type": "object",
                      "properties": {
                        "audience": {
                          "type": "array",
                          "items": {
                            "type": "string",
                            "enum": [
                              "user",
                              "assistant"
                            ]
                          }
                        },
                        "priority": {
                          "type": "number"
                        }
                      }
                    },
                    "description": {
                      "type": "string"
                    },
                    "mimeType": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "size": {
                      "type": "integer"
                    },
                    "title": {
                      "type": "string"
                    },
                    "uri": {
                      "type": "string"
                    },
                    "type": {
                      "const": "resource_link"
                    }
                  },
                  "required": [
                    "type"
                  ],
                  "description": "Relative path for resource within Eclipse workspace"
                }
              }
            }
          }
        },
        "description": "List of file and/or folder resources in the Eclipse workspace"
      },
      "annotations": {
        "title": "List Projects",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "listChildResources",
      "title": "List Child Resources",
      "description": "List child resources of an Eclipse project or folder",
      "inputSchema": {
        "type": "object",
        "properties": {
          "resourceURI": {
            "type": "string",
            "description": "URI of an eclipse project or folder"
          },
          "depth": {
            "type": "integer",
            "description": "0 for immediate children, 1 for children and grandchildren, 2 for infinite depth"
          }
        },
        "required": [
          "resourceURI"
        ]
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "resources": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "workspace_uri": {
                  "type": "object",
                  "properties": {
                    "_meta": {
                      "type": "object"
                    },
                    "annotations": {
                      "type": "object",
                      "properties": {
                        "audience": {
                          "type": "array",
                          "items": {
                            "type": "string",
                            "enum": [
                              "user",
                              "assistant"
                            ]
                          }
                        },
                        "priority": {
                          "type": "number"
                        }
                      }
                    },
                    "description": {
                      "type": "string"
                    },
                    "mimeType": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "size": {
                      "type": "integer"
                    },
                    "title": {
                      "type": "string"
                    },
                    "uri": {
                      "type": "string"
                    },
                    "type": {
                      "const": "resource_link"
                    }
                  },
                  "required": [
                    "type"
                  ],
                  "description": "Relative path for resource within Eclipse workspace"
                }
              }
            }
          }
        },
        "description": "List of file and/or folder resources in the Eclipse workspace"
      },
      "annotations": {
        "title": "List Child Resources",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "readResource",
      "title": "Read Resources",
      "description": "Returns the contents of a file, editor, or console URI",
      "inputSchema": {
        "type": "object",
        "properties": {
          "uri": {
            "type": "string",
            "description": "URI of an eclipse file, editor or console"
          }
        },
        "required": [
          "uri"
        ]
      },
      "annotations": {
        "title": "Read Resources",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "openEditor",
      "title": "openEditor",
      "description": "open an Eclipse IDE editor on a file and set an initial text selection",
      "inputSchema": {
        "type": "object",
        "properties": {
          "fileUri": {
            "type": "string",
            "description": "URI file in the Eclipse workspace"
          },
          "selectionOffset": {
            "type": "integer",
            "description": "offset of the selected text"
          },
          "selectionLength": {
            "type": "integer",
            "description": "length of the selected text"
          }
        },
        "required": [
          "fileUri"
        ]
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "buffer": {
            "$ref": "#/$defs/ResourceLink",
            "description": "The contents of the text editor"
          },
          "file": {
            "$ref": "#/$defs/ResourceLink",
            "description": "The file being edited containing the last saved changes"
          },
          "isActive": {
            "type": "boolean",
            "description": "Whether this is the editor has the user's focus"
          },
          "isDirty": {
            "type": "boolean",
            "description": "Whether text editor contains unsaved changes"
          },
          "name": {
            "type": "string",
            "description": "Title of this editor"
          }
        },
        "$defs": {
          "ResourceLink": {
            "type": "object",
            "properties": {
              "_meta": {
                "type": "object"
              },
              "annotations": {
                "type": "object",
                "properties": {
                  "audience": {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "enum": [
                        "user",
                        "assistant"
                      ]
                    }
                  },
                  "priority": {
                    "type": "number"
                  }
                }
              },
              "description": {
                "type": "string"
              },
              "mimeType": {
                "type": "string"
              },
              "name": {
                "type": "string"
              },
              "size": {
                "type": "integer"
              },
              "title": {
                "type": "string"
              },
              "uri": {
                "type": "string"
              },
              "type": {
                "const": "resource_link"
              }
            },
            "required": [
              "type"
            ]
          }
        },
        "description": "An Eclipse IDE text editor"
      },
      "annotations": {
        "title": "openEditor",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "closeEditor",
      "title": "closeEditor",
      "description": "close an Eclipse IDE editor",
      "inputSchema": {
        "type": "object",
        "properties": {
          "editorUri": {
            "type": "string",
            "description": "URI of an Eclipse editor"
          }
        },
        "required": [
          "editorUri"
        ]
      },
      "outputSchema": {
        "type": "object"
      },
      "annotations": {
        "title": "closeEditor",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "saveEditor",
      "title": "saveEditor",
      "description": "open an Eclipse IDE editor on a file and set an initial text selection",
      "inputSchema": {
        "type": "object",
        "properties": {
          "editorUri": {
            "type": "string",
            "description": "URI of an Eclipse editor"
          }
        },
        "required": [
          "editorUri"
        ]
      },
      "annotations": {
        "title": "saveEditor",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "changeEditorText",
      "title": "changeEditorText",
      "description": "Make one or more changes to an Eclipse text editor",
      "inputSchema": {
        "type": "object",
        "properties": {
          "editorURI": {
            "type": "string",
            "description": "URI of an Eclipse editor"
          },
          "replacements": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "length": {
                  "type": "integer",
                  "description": "the length of text after the offset to remove"
                },
                "offset": {
                  "type": "integer",
                  "description": "the character offset to insert the text"
                },
                "text": {
                  "type": "string",
                  "description": "the text to insert into editor"
                }
              },
              "description": "A single text replacement"
            },
            "description": "One or more text replacements to be applied in order"
          }
        },
        "required": [
          "editorURI",
          "replacements"
        ]
      },
      "annotations": {
        "title": "changeEditorText",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "listProblems",
      "title": "listProblems",
      "description": "list Eclipse IDE compilation and configuration problems",
      "inputSchema": {
        "type": "object",
        "properties": {
          "resourceURI": {
            "type": "string",
            "description": "Eclipse workspace file URI"
          },
          "severity": {
            "type": "string",
            "description": "One of ERROR, INFO or WARNING"
          }
        },
        "required": [
          "resourceURI"
        ]
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "problems": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "charEnd": {
                  "type": "integer",
                  "description": "An integer value indicating where a text marker ends. This attribute is zero-relative and exclusive."
                },
                "charStart": {
                  "type": "integer",
                  "description": "An integer value indicating where a text marker starts. This attribute is zero-relative and inclusive."
                },
                "creationTime": {
                  "type": "integer"
                },
                "done": {
                  "type": "boolean",
                  "description": "A boolean value indicating whether the marker"
                },
                "id": {
                  "type": "integer"
                },
                "lineNumber": {
                  "type": "integer",
                  "description": "An integer value indicating the line number for a text marker. This attribute is 1-relative"
                },
                "location": {
                  "type": "string",
                  "description": "The location is a human-readable (localized) string which can be used to distinguish between markers on a resource"
                },
                "message": {
                  "type": "string"
                },
                "resource_link": {
                  "type": "object",
                  "properties": {
                    "_meta": {
                      "type": "object"
                    },
                    "annotations": {
                      "type": "object",
                      "properties": {
                        "audience": {
                          "type": "array",
                          "items": {
                            "type": "string",
                            "enum": [
                              "user",
                              "assistant"
                            ]
                          }
                        },
                        "priority": {
                          "type": "number"
                        }
                      }
                    },
                    "description": {
                      "type": "string"
                    },
                    "mimeType": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "size": {
                      "type": "integer"
                    },
                    "title": {
                      "type": "string"
                    },
                    "uri": {
                      "type": "string"
                    },
                    "type": {
                      "const": "resource_link"
                    }
                  },
                  "required": [
                    "type"
                  ],
                  "description": "The associated file or editor"
                },
                "type": {
                  "type": "string"
                }
              }
            }
          }
        }
      },
      "annotations": {
        "title": "listProblems",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    },
    {
      "name": "listTasks",
      "title": "listTasks",
      "description": "list codebase locations containing TODO comments",
      "inputSchema": {
        "type": "object",
        "properties": {
          "resourceURI": {
            "type": "string",
            "description": "Eclipse workspace file URI"
          }
        },
        "required": []
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "tasks": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "charEnd": {
                  "type": "integer",
                  "description": "An integer value indicating where a text marker ends. This attribute is zero-relative and exclusive."
                },
                "charStart": {
                  "type": "integer",
                  "description": "An integer value indicating where a text marker starts. This attribute is zero-relative and inclusive."
                },
                "creationTime": {
                  "type": "integer"
                },
                "done": {
                  "type": "boolean",
                  "description": "A boolean value indicating whether the marker"
                },
                "id": {
                  "type": "integer"
                },
                "lineNumber": {
                  "type": "integer",
                  "description": "An integer value indicating the line number for a text marker. This attribute is 1-relative"
                },
                "location": {
                  "type": "string",
                  "description": "The location is a human-readable (localized) string which can be used to distinguish between markers on a resource"
                },
                "message": {
                  "type": "string"
                },
                "resource_link": {
                  "type": "object",
                  "properties": {
                    "_meta": {
                      "type": "object"
                    },
                    "annotations": {
                      "type": "object",
                      "properties": {
                        "audience": {
                          "type": "array",
                          "items": {
                            "type": "string",
                            "enum": [
                              "user",
                              "assistant"
                            ]
                          }
                        },
                        "priority": {
                          "type": "number"
                        }
                      }
                    },
                    "description": {
                      "type": "string"
                    },
                    "mimeType": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "size": {
                      "type": "integer"
                    },
                    "title": {
                      "type": "string"
                    },
                    "uri": {
                      "type": "string"
                    },
                    "type": {
                      "const": "resource_link"
                    }
                  },
                  "required": [
                    "type"
                  ],
                  "description": "The associated file or editor"
                },
                "type": {
                  "type": "string"
                }
              }
            }
          }
        }
      },
      "annotations": {
        "title": "listTasks",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": false,
        "returnDirect": false
      }
    }
  ]
}