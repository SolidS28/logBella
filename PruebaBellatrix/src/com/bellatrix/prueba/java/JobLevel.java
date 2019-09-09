package com.bellatrix.prueba.java;
public enum JobLevel {

	MESSAGE("message", 1),
	ERROR("error", 2),
	WARNING("warning", 3);
	
	private final String text;
    private final int priority;
    
    private JobLevel(String text, int priority) {
    	this.text = text;
    	this.priority = priority;
    }

	public String getText() {
		return text;
	}

	public int getPriority() {
		return priority;
	}
}
