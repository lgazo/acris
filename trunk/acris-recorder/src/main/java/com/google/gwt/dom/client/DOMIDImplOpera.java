package com.google.gwt.dom.client;

public class DOMIDImplOpera extends DOMImplOpera {
	public native Element createElement(Document doc, String tag) /*-{
	    var e = doc.createElement(tag);
	    @sk.seges.acris.recorder.client.utils.DOMUtil::generateId(Lcom/google/gwt/dom/client/Element;)(e);
	  	return e;
	}-*/;
	
	public native InputElement createInputElement(Document doc, String type) /*-{
	    var e = doc.createElement("INPUT");
	  	e.type = type;
	    @sk.seges.acris.recorder.client.utils.DOMUtil::generateId(Lcom/google/gwt/dom/client/Element;)(e);
		return e;
	}-*/;
}
