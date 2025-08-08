
# Resources
activeSelection: {
	file: {
		name: 'abc.txt'
		path: '/full/path'
		uri: 'file://path'
	},
	textSelection {
		offset: 10,
		length: 19,
		startLine: 1,
		endLine: 1,
		text: 'asdf'
	},
	isEmpty: false
}

# Functions

getSelection() returns {
	file: {
		name: 'abc.txt'
		path: '/full/path'
		uri: 'file://path'
	},
	textSelection {
		offset: 10,
		length: 19,
		startLine: 1,
		endLine: 1,
		text: 'asdf'
	},
	isEmpty: false
}

openEditor({
	uri: 
	selection: {
		offset: 10,
		length: 10
	}
}) returns void

listEditors() returns {
	name: 'tab name',
	path: '',
	uri: '',
}

listProjects returns { projectUris: [ uri ]}
listFolders(project) returns { folderUris: [ uri ]}
listFiles(folder)  returns { fileUris: [ uri ]}
readFile(uri)

listProblems(optional file url) returns {
	path:
	line:
	serverity:
	name:
	description:
	type:
}

updateEditor(url, offset, length, newText, review): {
	path:
	newContent:
}

#### Prompts

Tool Explanation
Coding Agent Stub

#### Resource Templating
- dynamic remote path exploration and content resolution
- resource templating
