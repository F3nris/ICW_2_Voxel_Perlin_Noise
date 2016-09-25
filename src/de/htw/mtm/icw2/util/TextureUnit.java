package de.htw.mtm.icw2.util;

public class TextureUnit {
	private int id;
	private int value;
	
	public TextureUnit (int id, int value) {
		this.setId(id); this.setValue(value);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
