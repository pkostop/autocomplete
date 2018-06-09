package simplefaces.util;

import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.sun.faces.el.ELContextImpl;

public class Util {

	public static Object nvl(Object _obj, Object alternative) {
		return _obj != null ? _obj.toString() : alternative;
	}

	public static String nvl(String _obj, String alternative) {
		return isNotEmpty(_obj) ? _obj : alternative;
	}

	public static boolean isEmpty(String value) {
		return value == null || "".equalsIgnoreCase(value.trim()) || "null".equalsIgnoreCase(value);
	}

	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	public static void setValue2ValueExpression(final Object value, String base) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ELContext elContext = facesContext.getELContext();
		elContext.getELResolver().setValue(elContext, null, base, value);		
	}

	public static Object resolveExpression(String expression) {

		Object result = null;
		try {
			if (!expression.startsWith("#{"))
				expression = "#{" + expression;
			if (!expression.endsWith("}"))
				expression = expression + "}";

			FacesContext context = FacesContext.getCurrentInstance();
			ExpressionFactory factory = context.getApplication().getExpressionFactory();

			ValueExpression valueExpression = factory.createValueExpression(context.getELContext(), expression,
					Object.class);
			result = valueExpression.getValue(context.getELContext());
		} catch (Exception ex) {
			System.out.println("Could not resolve expression " + expression);
		}
		return result;
	}

	public static void main(String[] args) {
		ELResolver elResolver = new CompositeELResolver();
		ELContext elContext = new ELContextImpl(elResolver);
		elResolver.setValue(elContext, null, "s", "123");
		ValueExpression valueExpression = ExpressionFactory.newInstance().createValueExpression(elContext, "#{s}",
				String.class);
		System.out.println(valueExpression.getValue(elContext));
	}
	
	public static String sanitize(String value){
		if(isEmpty(value))return value;
		return value.replace("'", "").replace("\"", "").replace("--", "").replace("&", "").replace("", "");
	}

}
