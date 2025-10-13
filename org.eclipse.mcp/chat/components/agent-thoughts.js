/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 
class AgentThoughts extends DivTemplate {
	
	markdown;
	thougts;

	constructor() {
		super("agent-thought-chunk");
		this.thoughts = 0;
	}

	connectedCallback() {
        // Create and append children to the shadow root
		this.markdown = this.root.querySelector('span chunked-markdown');
		this.button = this.root.querySelector('span button');
		this.button.addEventListener("click", function() {
			this.classList.toggle("active");
			let content = this.nextElementSibling;
			if (content.style.maxHeight) {
				content.style.maxHeight = null;
			} else {
				content.style.maxHeight = content.scrollHeight + "px";
				content.style.display = "block";
			}
		});
	}

	addContentBlock(block) {
		this.markdown.addContentBlock(block);
		this.button.textContent = "Thoughts Processed (" + ++this.thoughts + ")";
	}
}
customElements.define("agent-thoughts", AgentThoughts);