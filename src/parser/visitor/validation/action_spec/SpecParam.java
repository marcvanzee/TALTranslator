package parser.visitor.validation.action_spec;

import parser.visitor.validation.ValParam;

public class SpecParam extends ValParam
{
	public int implCount = 0;
	public boolean inOccurs = false;
	public boolean inPre = false;
	public boolean inPost = false;
	
	public SpecParam() {}
}
