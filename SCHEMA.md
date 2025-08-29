```json
{
  "resourceTemplates": [
    {
      "name": "Eclipse IDE Text Editor",
      "uriTemplate": "eclipse://editor/{name}",
      "description": "Content of an Eclipse Text Editor",
      "mimeType": "text/plain"
    },
    {
      "name": "PDS Member",
      "uriTemplate": "file://mvs/{host}/{pds}/{member}",
      "description": "A file that is a member of a an IBM System z Multiple Virtual Storage(MVS) Partitioned Data Set (PDS)",
      "mimeType": "text/plain"
    },
    {
      "name": "Eclipse Workspace File",
      "uriTemplate": "file://workspace/{project}/{projectRelativePath}",
      "description": "Content of an file in an Eclipse workspace",
      "mimeType": "text/plain"
    }
  ],
  "resources": [
    "openEditors": [{
      "name": "ELAXFSQL.jcl",
      "uri": "eclipse://editor/ELAXFSQL.jcl",
      "description": "com.ibm.systemz.db2.test.samples/cobol/example1/ELAXFSQL.jcl",
      "mimeType": "text/plain"
    }]
  ],
  "tools": [
    {
      "name": "currentSelection",
      "description": "Return the active Eclipse IDE text editor and its selected text",
      "inputSchema": {
        "type": "object",
        "properties": {},
        "required": []
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "editor": {
            "type": "object",
            "properties": {
              "editor": {
                "$ref": "#/$defs/ResourceLink"
              },
              "file": {
                "$ref": "#/$defs/ResourceLink"
              },
              "isActive": {
                "type": "boolean"
              },
              "isDirty": {
                "type": "boolean"
              },
              "name": {
                "type": "string"
              }
            }
          },
          "textSelection": {
            "type": "object",
            "properties": {
              "endLine": {
                "type": "integer",
                "format": "int32"
              },
              "length": {
                "type": "integer",
                "format": "int32"
              },
              "offset": {
                "type": "integer",
                "format": "int32"
              },
              "startLine": {
                "type": "integer",
                "format": "int32"
              },
              "text": {
                "type": "string"
              }
            }
          }
        },
        "$schema": "https://json-schema.org/draft/2020-12/schema",
        "$defs": {
          "ResourceLink": {
            "type": "object",
            "properties": {
              "annotations": {
                "type": "object",
                "properties": {
                  "audience": {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "enum": [
                        "USER",
                        "ASSISTANT"
                      ]
                    }
                  },
                  "priority": {
                    "type": "number",
                    "format": "double"
                  }
                }
              },
              "description": {
                "type": "string"
              },
              "meta": {
                "type": "object"
              },
              "mimeType": {
                "type": "string"
              },
              "name": {
                "type": "string"
              },
              "size": {
                "type": "integer",
                "format": "int64"
              },
              "title": {
                "type": "string"
              },
              "uri": {
                "type": "string"
              }
            }
          }
        }
      },
      "annotations": {
        "title": "Currrent Selection",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "listEditors",
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
                "editor": {
                  "$ref": "#/$defs/ResourceLink"
                },
                "file": {
                  "$ref": "#/$defs/ResourceLink"
                },
                "isActive": {
                  "type": "boolean"
                },
                "isDirty": {
                  "type": "boolean"
                },
                "name": {
                  "type": "string"
                }
              }
            }
          }
        },
        "$schema": "https://json-schema.org/draft/2020-12/schema",
        "$defs": {
          "ResourceLink": {
            "type": "object",
            "properties": {
              "annotations": {
                "type": "object",
                "properties": {
                  "audience": {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "enum": [
                        "USER",
                        "ASSISTANT"
                      ]
                    }
                  },
                  "priority": {
                    "type": "number",
                    "format": "double"
                  }
                }
              },
              "description": {
                "type": "string"
              },
              "meta": {
                "type": "object"
              },
              "mimeType": {
                "type": "string"
              },
              "name": {
                "type": "string"
              },
              "size": {
                "type": "integer",
                "format": "int64"
              },
              "title": {
                "type": "string"
              },
              "uri": {
                "type": "string"
              }
            }
          }
        }
      },
      "annotations": {
        "title": "List Editors",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "listConsoles",
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
                  "type": "string"
                },
                "type": {
                  "type": "string"
                },
                "uri": {}
              }
            }
          }
        },
        "$schema": "https://json-schema.org/draft/2020-12/schema"
      },
      "annotations": {
        "title": "List Consoles",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "listProjects",
      "description": "List open Eclipse IDE projects",
      "inputSchema": {
        "type": "object",
        "properties": {},
        "required": []
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "depthSearched": {
            "type": "string",
            "enum": [
              "CHILDREN",
              "GRANDCHILDREN",
              "INFINITE"
            ]
          },
          "files": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "isFolder": {
                  "type": "boolean"
                },
                "name": {
                  "type": "string"
                },
                "uri": {
                  "type": "object",
                  "properties": {
                    "annotations": {
                      "type": "object",
                      "properties": {
                        "audience": {
                          "type": "array",
                          "items": {
                            "type": "string",
                            "enum": [
                              "USER",
                              "ASSISTANT"
                            ]
                          }
                        },
                        "priority": {
                          "type": "number",
                          "format": "double"
                        }
                      }
                    },
                    "description": {
                      "type": "string"
                    },
                    "meta": {
                      "type": "object"
                    },
                    "mimeType": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "size": {
                      "type": "integer",
                      "format": "int64"
                    },
                    "title": {
                      "type": "string"
                    },
                    "uri": {
                      "type": "string"
                    }
                  }
                }
              }
            }
          }
        },
        "$schema": "https://json-schema.org/draft/2020-12/schema"
      },
      "annotations": {
        "title": "List Projects",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "listChildResources",
      "description": "List child resources of an Eclipse workspace, project or folder URI",
      "inputSchema": {
        "type": "object",
        "properties": {
          "arg0": {
            "type": "string",
            "description": "URI of an eclipse project or folder"
          },
          "arg1": {
            "type": "string",
            "enum": [
              "CHILDREN",
              "GRANDCHILDREN",
              "INFINITE"
            ],
            "description": "CHILDREN, GRANDCHILDREN or INFINITE"
          }
        },
        "required": [
          "arg0"
        ]
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "depthSearched": {
            "type": "string",
            "enum": [
              "CHILDREN",
              "GRANDCHILDREN",
              "INFINITE"
            ]
          },
          "files": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "isFolder": {
                  "type": "boolean"
                },
                "name": {
                  "type": "string"
                },
                "uri": {
                  "type": "object",
                  "properties": {
                    "annotations": {
                      "type": "object",
                      "properties": {
                        "audience": {
                          "type": "array",
                          "items": {
                            "type": "string",
                            "enum": [
                              "USER",
                              "ASSISTANT"
                            ]
                          }
                        },
                        "priority": {
                          "type": "number",
                          "format": "double"
                        }
                      }
                    },
                    "description": {
                      "type": "string"
                    },
                    "meta": {
                      "type": "object"
                    },
                    "mimeType": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "size": {
                      "type": "integer",
                      "format": "int64"
                    },
                    "title": {
                      "type": "string"
                    },
                    "uri": {
                      "type": "string"
                    }
                  }
                }
              }
            }
          }
        },
        "$schema": "https://json-schema.org/draft/2020-12/schema"
      },
      "annotations": {
        "title": "List Child Resources",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "readResource",
      "description": "Returns the contents of an Eclipse workspace file, editor, or console URI",
      "inputSchema": {
        "type": "object",
        "properties": {
          "arg0": {
            "type": "string",
            "description": "URI of an eclipse file, editor or console"
          }
        },
        "required": [
          "arg0"
        ]
      },
      "annotations": {
        "title": "Read Resource",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "openEditor",
      "description": "open an Eclipse IDE editor on a file URI and set an initial text selection",
      "inputSchema": {
        "type": "object",
        "properties": {
          "arg0": {
            "type": "string",
            "description": "Eclipse workspace file uri"
          },
          "arg1": {
            "type": "integer",
            "format": "int32",
            "description": "offset of the text selection"
          },
          "arg2": {
            "type": "integer",
            "format": "int32",
            "description": "length of the text selection"
          }
        },
        "required": [
          "arg0"
        ]
      },
      "outputSchema": {
        "type": "object",
        "properties": {
          "editor": {
            "$ref": "#/$defs/ResourceLink"
          },
          "file": {
            "$ref": "#/$defs/ResourceLink"
          },
          "isActive": {
            "type": "boolean"
          },
          "isDirty": {
            "type": "boolean"
          },
          "name": {
            "type": "string"
          }
        },
        "$schema": "https://json-schema.org/draft/2020-12/schema",
        "$defs": {
          "ResourceLink": {
            "type": "object",
            "properties": {
              "annotations": {
                "type": "object",
                "properties": {
                  "audience": {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "enum": [
                        "USER",
                        "ASSISTANT"
                      ]
                    }
                  },
                  "priority": {
                    "type": "number",
                    "format": "double"
                  }
                }
              },
              "description": {
                "type": "string"
              },
              "meta": {
                "type": "object"
              },
              "mimeType": {
                "type": "string"
              },
              "name": {
                "type": "string"
              },
              "size": {
                "type": "integer",
                "format": "int64"
              },
              "title": {
                "type": "string"
              },
              "uri": {
                "type": "string"
              }
            }
          }
        }
      },
      "annotations": {
        "title": "Open Editor",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "closeEditor",
      "description": "close an Eclipse IDE editor",
      "inputSchema": {
        "type": "object",
        "properties": {
          "arg0": {
            "type": "string",
            "description": "URI of an open Eclipse editor"
          }
        },
        "required": [
          "arg0"
        ]
      },
      "annotations": {
        "title": "Close Editor",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "saveEditor",
      "description": "save the contents of a dirty Eclipse IDE editor to file",
      "inputSchema": {
        "type": "object",
        "properties": {
          "arg0": {
            "type": "string",
            "description": "URI of an open Eclipse editor"
          }
        },
        "required": [
          "arg0"
        ]
      },
      "annotations": {
        "title": "Save Editor",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "changeEditorText",
      "description": "Make one or more changes to an Eclipse text editor",
      "inputSchema": {
        "type": "object",
        "properties": {
          "arg0": {
            "type": "string",
            "description": "Open Eclipse editor URI"
          },
          "arg1": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "length": {
                  "type": "integer",
                  "format": "int32",
                  "description": "the length of text after the offset to remove"
                },
                "offset": {
                  "type": "integer",
                  "format": "int32",
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
          "arg0",
          "arg1"
        ]
      },
      "annotations": {
        "title": "Change Editor Text",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "listProblems",
      "description": "list Eclipse IDE compilation and configuration problems",
      "inputSchema": {
        "type": "object",
        "properties": {
          "arg0": {
            "type": "string",
            "description": "Eclipse workspace file or editor URI"
          },
          "arg1": {
            "type": "string",
            "description": "One of ERROR, INFO or WARNING. Default i"
          }
        },
        "required": [
          "arg0"
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
                  "format": "int32"
                },
                "charStart": {
                  "type": "integer",
                  "format": "int32"
                },
                "creationTime": {
                  "type": "integer",
                  "format": "int64"
                },
                "done": {
                  "type": "boolean"
                },
                "id": {
                  "type": "integer",
                  "format": "int64"
                },
                "lineNumber": {
                  "type": "integer",
                  "format": "int32"
                },
                "location": {
                  "type": "string"
                },
                "message": {
                  "type": "string"
                },
                "priority": {
                  "type": "string",
                  "enum": [
                    "HIGH",
                    "LOW",
                    "NORMAL"
                  ]
                },
                "resource_link": {
                  "type": "object",
                  "properties": {
                    "annotations": {
                      "type": "object",
                      "properties": {
                        "audience": {
                          "type": "array",
                          "items": {
                            "type": "string",
                            "enum": [
                              "USER",
                              "ASSISTANT"
                            ]
                          }
                        },
                        "priority": {
                          "type": "number",
                          "format": "double"
                        }
                      }
                    },
                    "description": {
                      "type": "string"
                    },
                    "meta": {
                      "type": "object"
                    },
                    "mimeType": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "size": {
                      "type": "integer",
                      "format": "int64"
                    },
                    "title": {
                      "type": "string"
                    },
                    "uri": {
                      "type": "string"
                    }
                  }
                },
                "severity": {
                  "type": "string",
                  "enum": [
                    "ERROR",
                    "INFO",
                    "WARNING"
                  ]
                },
                "type": {
                  "type": "string",
                  "enum": [
                    "Bookmark",
                    "Problem",
                    "Task",
                    "Text"
                  ]
                }
              }
            }
          }
        },
        "$schema": "https://json-schema.org/draft/2020-12/schema"
      },
      "annotations": {
        "title": "List Problems",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    },
    {
      "name": "listTasks",
      "description": "list codebase locations of tasks including TODO comments",
      "inputSchema": {
        "type": "object",
        "properties": {
          "arg0": {
            "type": "string",
            "description": "Eclipse workspace file or editor URI"
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
                  "format": "int32"
                },
                "charStart": {
                  "type": "integer",
                  "format": "int32"
                },
                "creationTime": {
                  "type": "integer",
                  "format": "int64"
                },
                "done": {
                  "type": "boolean"
                },
                "id": {
                  "type": "integer",
                  "format": "int64"
                },
                "lineNumber": {
                  "type": "integer",
                  "format": "int32"
                },
                "location": {
                  "type": "string"
                },
                "message": {
                  "type": "string"
                },
                "priority": {
                  "type": "string",
                  "enum": [
                    "HIGH",
                    "LOW",
                    "NORMAL"
                  ]
                },
                "resource_link": {
                  "type": "object",
                  "properties": {
                    "annotations": {
                      "type": "object",
                      "properties": {
                        "audience": {
                          "type": "array",
                          "items": {
                            "type": "string",
                            "enum": [
                              "USER",
                              "ASSISTANT"
                            ]
                          }
                        },
                        "priority": {
                          "type": "number",
                          "format": "double"
                        }
                      }
                    },
                    "description": {
                      "type": "string"
                    },
                    "meta": {
                      "type": "object"
                    },
                    "mimeType": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "size": {
                      "type": "integer",
                      "format": "int64"
                    },
                    "title": {
                      "type": "string"
                    },
                    "uri": {
                      "type": "string"
                    }
                  }
                },
                "severity": {
                  "type": "string",
                  "enum": [
                    "ERROR",
                    "INFO",
                    "WARNING"
                  ]
                },
                "type": {
                  "type": "string",
                  "enum": [
                    "Bookmark",
                    "Problem",
                    "Task",
                    "Text"
                  ]
                }
              }
            }
          }
        },
        "$schema": "https://json-schema.org/draft/2020-12/schema"
      },
      "annotations": {
        "title": "List Tasks",
        "readOnlyHint": false,
        "destructiveHint": true,
        "idempotentHint": false,
        "openWorldHint": true
      }
    }
  ]
}
```
