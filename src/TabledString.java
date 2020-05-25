public class TabledString
{
	private int[] array;
	private int space;
	private String string;
	
	TabledString(int[] array,int space)
	{
		this.array=array;
		this.space=space;
		string="";
		
	}//end: TabledString(int[],int)
	
	public void addLineOf(char c)
	{
		int i,j;
		string+="\n";
		for(j=1;j <= space;j++) string+=c;
		for(i=0;i < array.length;i++)
		{
			for(j=1;j <= array[i];j++)
				string+=c;
			for(j=1;j <= space;j++) string+=c;
		}
		string+="\n";	
		
	}//end: void addLineOf(char)
	
	public void addRow()
	{
		string+="\n";
		
	}//end: void addRow()
	
	public void addSpace()
	{
		for(int i=1;i <= space;i++)
			if(i == space/2)
				string+="|";
			else
				string+=" ";
		
	}//end: void addSpace()
	
	public void addData(int colnum,String s)
	{
		int i=0;
		while(i < array[colnum-1])
		{
			if(i < s.length())
				string+=s.charAt(i);
			else
				string+=" ";
			i++;
		}
		addSpace();
		
	}//end: void addData(int,String)
	
	public void addString(String s)
	{
		string+=s;
		
	}//end: void addString(String)
	
	public String getString()
	{
		return string;
		
	}//end: String getString()
	
}//end: class TabledString
