package simplefaces.components.autocomplete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import simplefaces.util.Util;

@FacesComponent("simplefaces.components.autocomplete.AutoComplete")
@ResourceDependencies({ @ResourceDependency(name = "js/simplefaces_20180604.js", target = "head"),
		@ResourceDependency(name = "css/simplefaces_20180604.css", target = "head") })
public class AutoComplete extends UIInput implements ClientBehaviorHolder {
	public AutoComplete() {
		super();
		setRendererType(null);
	}

	String completeMethod;
	String minQueryLength;
	private static List<String> eventNames = Arrays.asList("itemSelect");

	@Override
	public String getDefaultEventName() {
		return "itemSelect";
	}

	@Override
	public Collection<String> getEventNames() {
		return eventNames;
	}

	@Override
	public void decode(FacesContext context) {
		Map<String, String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
		String newValue = requestParameterMap.get(getClientId() + "_value");
		setSubmittedValue(newValue);
		fireAjaxEvents(context);
		setValid(true);
	}

	@Override
	public boolean visitTree(VisitContext context, VisitCallback callback) {
		return super.visitTree(context, callback);
	}

	@Override
	public void processDecodes(FacesContext context) {
		super.processDecodes(context);
	}

	public String getAjaxScript(String event) {
		List<ClientBehavior> clientBehaviourList = this.getClientBehaviors().get(event);
		if (clientBehaviourList != null && clientBehaviourList.size() > 0) {
			ClientBehaviorContext clientBehaviorContext = ClientBehaviorContext.createClientBehaviorContext(
					FacesContext.getCurrentInstance(), this, "click", getClientId(FacesContext.getCurrentInstance()),
					null);
			if (clientBehaviorContext != null)
				return clientBehaviourList.get(0).getScript(clientBehaviorContext);
		}
		return null;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		if (getAttributes().get("rendered") != null && !(Boolean) getAttributes().get("rendered")) {
			writer.close();
			return;
		}
		Object value = getValue();
		minQueryLength = (String) this.getAttributes().get("minQueryLength");
		completeMethod = getStateHelper().get("completeMethod")!=null?(String) ((ArrayList<?>) getStateHelper().get("completeMethod")).get(0):"";
		String var = (String) this.getAttributes().get("var");
		Util.setValue2ValueExpression(value, var);
		Object labelValue =null;
		if(getStateHelper().get("itemLabel")!=null)
			labelValue=Util.resolveExpression((String) ((ArrayList<?>) getStateHelper().get("itemLabel")).get(0));

		String idValue = null;
		if (getConverter() != null)
			idValue = getConverter().getAsString(context, this, value);
		else if (value != null)
			idValue = value.toString();

		if (context.getPartialViewContext() != null && context.getPartialViewContext().isPartialRequest()
				&& context.getPartialViewContext().getExecuteIds().contains(getClientId() + "_autocomplete")) {
			String newValue = context.getExternalContext().getRequestParameterMap()
					.get(getClientId() + "_autocomplete");
			List<?> result = (List<?>) Util.resolveExpression(
					completeMethod.replace("#", "").replace("{", "").replace("}", "") + "(\"" + newValue + "\")");
			encodeOptions(writer, context, var, result);
		} else {
			if ("true".equalsIgnoreCase((String) this.getAttributes().get("readonly"))) {
				encodeReadOnlyControl(writer, labelValue);
			} else {
				writer.startElement("div", this);
				writer.writeAttribute("id", getClientId() + "_parent", null);
				writer.writeAttribute("style", "position:relative", null);
				encodeAutocomplete(writer, Util.nvl(labelValue, idValue));
				encodeHidden(writer, Util.nvl(idValue, ""));
				encodeOptions(writer, context, var, new LinkedList<Object>());
				writer.endElement("div");
			}
			writer.close();
		}

	}

	protected void encodeReadOnlyControl(ResponseWriter writer, Object value) throws IOException {
		writer.startElement("input", null);
		writer.writeAttribute("type", "text", null);
		writer.writeAttribute("value", value, null);
		writer.writeAttribute("readonly", "readonly", null);
		writer.endElement("input");
	}

	protected void encodeAutocomplete(ResponseWriter writer, Object value) throws IOException {
		writer.startElement("input", this);
		writer.writeAttribute("type", "text", null);
		writer.writeAttribute("autocomplete", "off", null);
		writer.writeAttribute("class", Util.nvl(this.getAttributes().get("styleClass"), "") + " autocomplete", null);
		writer.writeAttribute("id", getClientId() + "_autocomplete", null);
		writer.writeAttribute("name", getClientId() + "_autocomplete", null);
		writer.writeAttribute("value", value, null);
		writer.writeAttribute("onfocusout", "hideOptions('" + getClientId() + "')", null);
		if (this.getAttributes().get("readonly") != null
				&& ("true".equalsIgnoreCase(this.getAttributes().get("readonly").toString())
						|| "readonly".equalsIgnoreCase(this.getAttributes().get("readonly").toString())))
			writer.writeAttribute("readonly", Util.nvl("readonly", ""), null);
		writer.writeAttribute("style", Util.nvl(this.getAttributes().get("style"), ""), null);
		writer.writeAttribute("size", Util.nvl(this.getAttributes().get("maxlength"), ""), null);
		writer.writeAttribute("maxLength", Util.nvl(this.getAttributes().get("maxlength"), ""), null);
		if (this.getAttributes().get("readonly") == null
				|| (!"true".equalsIgnoreCase(this.getAttributes().get("readonly").toString())
						&& !"readonly".equalsIgnoreCase(this.getAttributes().get("readonly").toString())))
			writer.writeAttribute("onkeyup",
					"sfAutoCompleteFire(this, event,'" + getClientId() + "'," + minQueryLength + ")", null);
		writeJSEventAttributes(writer);
		writer.endElement("input");
	}

	private static final String[] JS_EVENTS = new String[] { "onclick", "ondblclick", "onfocus", "onkeydown",
			"onkeypress", "onkeyup", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup", "onreset",
			"onselect", "onchange", "onblur" };

	protected void writeJSEventAttributes(ResponseWriter writer) throws IOException {
		for (String jsEvent : JS_EVENTS) {
			String eventMethodName = (String) getAttributes().get(jsEvent);
			if (Util.isNotEmpty(eventMethodName))
				writer.writeAttribute(jsEvent, eventMethodName, null);
		}
	}

	protected void encodeHidden(ResponseWriter writer, String value) throws IOException {
		writer.startElement("input", this);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", getClientId() + "_value", null);
		writer.writeAttribute("name", getClientId() + "_value", null);
		writer.writeAttribute("value", Util.nvl(value, ""), null);
		writer.endElement("input");
	}

	protected void encodeOptions(ResponseWriter writer, FacesContext context, String var, List<?> result)
			throws IOException {
		String ajaxScript = getAjaxScript("itemSelect");
		writer.startElement("ul", this);
		writer.writeAttribute("id", getClientId(), null);
		writer.writeAttribute("style", "display:none;" + Util.nvl(this.getAttributes().get("optionsStyle"), ""), null);
		writer.writeAttribute("class", "sf-autocomplete " + Util.nvl(this.getAttributes().get("optionsClass"), ""),
				null);
		if ("true".equalsIgnoreCase((String) this.getAttributes().get("noSelectionOption"))) {
			writer.startElement("li", this);
			writer.writeAttribute("onclick",
					"sfAutoCompleteItemSelected('" + getClientId() + "','" + " " + "','" + "null" + "');", null);
			writer.writeText("", null);
			writer.endElement("li");
		}
		if (result != null) {
			for (int i = 0; i < result.size(); i++) {
				Object _obj = result.get(i);
				Util.setValue2ValueExpression(_obj, var);
				Object label = getStateHelper().get("itemLabel")!=null?Util.resolveExpression((String) ((ArrayList<?>) getStateHelper().get("itemLabel")).get(0)):"";
				Object val = getStateHelper().get("itemValue")!=null?Util.resolveExpression((String) ((ArrayList<?>) getStateHelper().get("itemValue")).get(0)):"";
				String sval = getConverter() != null ? getConverter().getAsString(context, this, val) : val.toString();
				writer.startElement("li", this);
				String onClick = "sfAutoCompleteItemSelected('" + getClientId() + "','"
						+ (label != null ? Util.sanitize(label.toString()) : "") + "','" + sval + "');";
				onClick += !Util.isEmpty(ajaxScript) ? ajaxScript + ";" : "";
				onClick += (this.getAttributes().get("onchange") != null ? this.getAttributes().get("onchange") : "");
				writer.writeAttribute("onclick", onClick, null);
				writer.writeText(label, null);
				writer.endElement("li");
			}
		}
		writer.endElement("ul");
	}

	@Override
	public void setValueExpression(String name, ValueExpression binding) {
		if ("itemLabel".equalsIgnoreCase(name)) {
			getStateHelper().add("itemLabel", binding.getExpressionString());
		}
		if ("itemValue".equalsIgnoreCase(name)) {
			getStateHelper().add("itemValue", binding.getExpressionString());
		}
		if ("completeMethod".equalsIgnoreCase(name)) {
			getStateHelper().add("completeMethod", binding.getExpressionString());
		}
		super.setValueExpression(name, binding);
	}

	protected void fireAjaxEvents(FacesContext context) {
		Map<String, String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
		String _event = requestParameterMap.get("javax.faces.behavior.event");
		String sourceId = requestParameterMap.get("javax.faces.source");
		String[] events = new String[] { _event };
		if ("click".equalsIgnoreCase(_event)) {
			events = new String[] { _event, "itemSelect" };
		}
		for (String event : events) {
			if (getClientId().equalsIgnoreCase(sourceId)) {
				List<ClientBehavior> clientBehaviors = getClientBehaviors().get(event);
				if (clientBehaviors != null) {
					clientBehaviors.get(0).decode(context, this);
				}
			}
		}
	}

}
