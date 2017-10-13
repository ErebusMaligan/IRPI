package irpi.module.remote.data.macro;

import java.util.List;

import xml.XMLValues;

public interface MacroChild extends XMLValues {
	public List<MacroChild> getMacroChildren();
}
