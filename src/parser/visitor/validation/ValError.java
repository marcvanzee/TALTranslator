package parser.visitor.validation;

public class ValError
{
	public boolean error = false;	
	private String errorMessage = "";
	private String formula = "";
	
	public ValError()
	{
		this.error = false;
	}
	
	public void setError(String msg, String formula)
	{
		setError(msg);
		this.formula = formula;
	}
	
	public void setError(String msg)
	{
		this.error = true;
		this.errorMessage = msg;
	}
	
	public void setFormula(String f)
	{
		this.formula = f;
	}
	
	public String getMessage() {
		return this.errorMessage;
	}
	
	public String getFormula() {
		return formula;
	}
}
