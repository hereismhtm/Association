import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

class S_Itemset
{
    class Symb
    {
        int code;
        Symb next;
        Symb(int code)
        {
            this.code=code;
            next=null;
        }
    }//end: class Symb

    Symb head=null;
    Symb rp;

    void separate(int code,int items)
    {
        int c;
        Symb p=head;
        Symb pp=null;
        do
        {
            c=code%items;
            code/=items;
            if(p==null)
            {
                p=new Symb(c);
                if(head == null)
                {
                	head=p;
                }
                else
                {
                	pp.next=p;
                }
            }
            else
            {
                p.code=c;
            }
            pp=p;
            p=p.next;
        }while(code > 0);
        if(p != null) p.code=-1;
        rp=head;
    }//end: void separate(int,int)

    int read()
    {
        if(head==null) return -1;
        if(rp == null || rp.code == -1) return -1;
        int c=rp.code;
        rp=rp.next;
        return c;
    }//end: int read()

    void reset()
    {
        if(head != null) rp=head;
    }//end: void reset()

    int length()
    {
    	reset();
    	int i=0;
    	while(read() != (-1)) i++;
    	reset();
    	return i;
    }//end: void length()

}//end: class S_Itemset


@SuppressWarnings("serial")
public class apriori extends JFrame
{
	static class Rule
	{
		float conf;
		int antecedent;
		int consequent;
		int sup_a;
		int sup_c;
		int sup_ac;
		float liftRatio;
		Rule next;
	}//end: class Rule

	private static String dset;
    private static Rule r=null;
    private static String[] header;
    private static int trans=0;
    private static int items;
    private static float minsup;
    private static float minconf;
    private static byte k=1;

    private static Pineapple.Slice next(Pineapple.Slice sl,char mode)
    {
        int v=(mode=='C')? (k-1):(k);
        while(sl.next != null)
        {
            sl=sl.next;
            if(sl.data.k == v) return sl;
            if(sl.data.k == k) break;
        }
        return null;
    }//end: Pineapple.Slice next(Pineapple.Slice,char)

    private static int max(int code)
    {
        int c;
        do
        {
            c=code%items;
            code/=items;
        }while(code > 0);
        return c;
    }//end: int max(int)

    private static boolean candidate(Pineapple mem)
    {
        k++;
        Pineapple.Slice sl=mem.linker;
        while(sl != null && sl.data.k != k-1)
            sl=sl.next;
        if(sl == null) return false;

        Pineapple.Slice nsl;
        S_Itemset sobj=new S_Itemset();
        int max,c,code;
        boolean flag=false;

        do
        {
            max=max(sl.data.code);
            nsl=sl;
            while(true)
            {
                nsl=next(nsl,'C');
                if(nsl == null) break;
                else
                {
                    sobj.separate(nsl.data.code,items);
                    while((c=sobj.read()) != (-1))
                    {
                        if(c > max)
                        {
                            code=(sl.data.code)+(c*(int)Math.pow(items,k-1));
                            if(mem.search(code) == null)
                            {
                                mem.insert(code,k,0);
                                flag=true;
                            }
                        }
                    }
                }
            }
        }while((sl=next(sl,'C')) != null);
        return flag;
    }//end: boolean candidate(Pineapple)

    private static void prune(Pineapple mem)
    {
        S_Itemset sobj1=new S_Itemset();
        S_Itemset sobj2=new S_Itemset();
        int pow,i,j,c1,c2,code;
        Pineapple.Slice sl=mem.linker;

        while(sl.data.k != k)
            sl=sl.next;
        do
        {
            sobj1.separate(sl.data.code,items);
            pow=(int)Math.pow(2,k);
            for(i=0;i<pow;i++)                  // Check all subsets of k-1 for this sl
            {
                sobj2.separate(i,2);
                j=0;
                while((c2=sobj2.read()) != (-1))
                {
                    if(c2 == 1) j++;
                }
                if(j == k-1)
                {
                    j=0;
                    code=0;
                    sobj1.reset();
                    sobj2.reset();
                    while(true)
                    {
                        c1=sobj1.read();
                        c2=sobj2.read();
                        if(c2 == 1)
                        {
                            code+=c1*Math.pow(items,j++);
                            if(j == k-1) break;
                        }
                    }
                    if(mem.fetch(code).k == 0)
                    {
                    	sl.data.k=0;
                    	break;
                    }
                }
            }
        }while((sl=sl.next) != null);
    }//end: void prune(Pineapple)

    private static void supportK1(Pineapple mem)
    {
    	Pineapple.Slice sl=mem.linker;

        do
        {
            if(sl.data.support < minsup)
            {
                sl.data.k=0;
            }
        }while((sl=sl.next) != null);
    }//end: void supportK1(Pineapple)

    private static void support(Pineapple mem)
    {
        Pineapple.Slice fsl=mem.linker;
        while(fsl != null && fsl.data.k != k)
            fsl=fsl.next;
        if(fsl == null) return;
        Pineapple.Slice sl;
        String line;
        S_Itemset sobj=new S_Itemset();
        int c;
        boolean flag;

        try
        {
            BufferedReader dataset=new BufferedReader(new FileReader(dset));
            line=dataset.readLine();
            while(line != null)
            {
                sl=fsl;
                while(sl != null)
                {
                    flag=false;
                    sobj.separate(sl.data.code,items);
                    while((c=sobj.read()) != (-1))
                    {
                        if(line.charAt(c*2) == '0')
                        {
                            flag=true;
                            break;
                        }
                    }
                    if(!flag) sl.data.support++;
                    sl=next(sl,'S');
                }
                line=dataset.readLine();
            }
            dataset.close();
        }catch(IOException e){System.out.println(e.getMessage());System.exit(1);}

        sl=fsl;
        while(sl != null)
        {
            if(sl.data.support < minsup)
            {
                sl.data.k=0;
            }
            sl=next(sl,'S');
        }
    }//end: void support(Pineapple)

    private static void print(Pineapple mem)
    {
        Pineapple.Slice sl=mem.linker;
        S_Itemset sobj=new S_Itemset();
        String s;
        int c;
        boolean flag=false;
        while(sl != null)
        {
            if(sl.data.k == k)
            {
            	if(!flag) { System.out.println("F"+k+"\\\t"+k+"-itemsets\n"); flag=true; }
                sobj.separate(sl.data.code,items);
                s="";
                while((c=sobj.read()) != (-1))
                {
                    s+=(char)(c+97)+" ";
                }
                System.out.println("< "+s+">    code: "+sl.data.code+"    k: "+sl.data.k+"    support: "+sl.data.support);
            }
            if(sl.data.k == k+1) break;
            sl=sl.next;
        }
        if(flag) System.out.println();
    }//end: void print(Pineapple)

    private static void getRules(Pineapple mem,int para1,int para2)
    {
    	Pineapple.Slice sl=mem.linker;
    	S_Itemset sobj1=new S_Itemset();
        S_Itemset sobj2=new S_Itemset();
        S_Itemset sobj=new S_Itemset();
        int pow,i,c1,c2,sideL,powL,sideR,powR;
        float conf;
        boolean flag=false;
        Rule p;
        String s;

        while((sl=sl.next) != null)
        {
        	if(sl.data.k == 1) continue;

        	sobj1.separate(sl.data.code,items);
        	pow=(int)Math.pow(2,sl.data.k);
        	for(i=0;i<pow;i++)
        	{
        		if(i==0 || i==(pow-1)) continue;
        		sobj2.separate(i,2);
        		sideL=powL=sideR=powR=0;
        		sobj1.reset();
    			while((c1=sobj1.read()) != (-1))
    			{
    				c2=sobj2.read();
    				if(c2 != 1)		sideL+=c1*Math.pow(items,powL++);
    				else			sideR+=c1*Math.pow(items,powR++);
    			}

                conf=(float)mem.fetch(sl.data.code).support/(float)mem.fetch(sideL).support;
    			if(conf >= minconf)
    			{
    				flag=true;
    				p=new Rule();
    				p.conf=conf;
					p.antecedent=sideL;
					p.consequent=sideR;
					p.sup_a=mem.fetch(sideL).support;
					p.sup_c=mem.fetch(sideR).support;
					p.sup_ac=mem.fetch(sl.data.code).support;
					p.liftRatio=conf/((float)mem.fetch(sideR).support/trans);
					if(r == null)
						p.next=null;
					else
						p.next=r;
					r=p;

    				s="";
    				sobj.separate(sideL,items);
                    while((c1=sobj.read()) != (-1))
                    {
                        s+=(char)(c1+97)+" ";
                    }
                    s+="--> ";
                    sobj.separate(sideR,items);
                    while((c1=sobj.read()) != (-1))
                    {
                        s+=(char)(c1+97)+" ";
                    }
                    System.out.println("\t"+s+"\t\tconf= "+conf);
    			}
        	}
        }
        if(flag)	System.out.println("\nNo more rules can be generated.");
        else		System.out.println("No association rules.");
        System.out.println("--------------------------------------------------\n");

        sortRules();
        int[] colsize={5,7,28,28,10,10,12,10};
        TabledString report=new TabledString(colsize,3);
        report.addSpace();
        report.addData(1,"Rule#");
        report.addData(2,"Conf. %");
        report.addData(3,"Antecedent(a)");
        report.addData(4,"Consequent(c)");
        report.addData(5,"Support(a)");
        report.addData(6,"Support(c)");
        report.addData(7,"Support(aUc)");
        report.addData(8,"Lift Ratio");
        report.addLineOf('-'); report.addSpace();
        DecimalFormat df = new DecimalFormat("#.##");
        if(r == null) report.addString("No association rules.");
        p=r;
        i=0;
        while(p != null)
        {
        	i++;
        	if(p != r) { report.addRow(); report.addSpace(); }
        	report.addData(1,String.valueOf(i));
        	report.addData(2,df.format(p.conf*100));
        	sobj.separate(p.antecedent,items);
        	pow=sobj.length(); s="";
        	while((c1=sobj.read()) != (-1))
        	{
        		s+=header[c1];
        		if(--pow != 0)
        			s+=", ";
        	}
        	report.addData(3,s+" =>");
        	sobj.separate(p.consequent,items);
        	pow=sobj.length(); s="";
        	while((c1=sobj.read()) != (-1))
        	{
        		s+=header[c1];
        		if(--pow != 0)
        			s+=", ";
        	}
        	report.addData(4,s);
        	report.addData(5,String.valueOf(p.sup_a));
        	report.addData(6,String.valueOf(p.sup_c));
        	report.addData(7,String.valueOf(p.sup_ac));
        	report.addData(8,df.format(p.liftRatio));
        	p=p.next;
        }
        report.addLineOf('-');
        s="min support: "+df.format(minsup)+" = ("+para1+"%)\n";
        s+="min confidence: "+df.format(minconf)+" = ("+para2+"%)\n";
        s+="dataset file: "+dset+"\n";
        s+="transactions: "+trans+"\n";
        s+="items: "+items+"\n\n";
        s+=report.getString();
        s+="Click anywhere and press Ctrl+A then Ctrl+C, and paste the results on a file.";
        JOptionPane.showMessageDialog(null,new JTextArea(s),"Association Rules :",1);

    }//end: void getRules(Pineapple,int,int)

    private static void readHeader()
    {
    	String filename="";
    	for(int i=0;i<dset.length();i++)
    	{
    		if(dset.charAt(i) == '.') break;
    		filename+=dset.charAt(i);
    	}
    	filename+=".dh";

    	header=new String[items];
    	String line;
    	int i;
    	try
    	{
    		i=0;
    		BufferedReader file=new BufferedReader(new FileReader(filename));
    		while((line=file.readLine()) != null)
    		{
    			header[i++]=line;
    		}
    		file.close();
    	}catch(IOException e){System.out.println(e.getMessage());System.exit(1);}

    }//end: void readHeader()

    private static void sortRules()
    {
    	if(r == null) return;
        int flag;
        Rule prev,p1,p2;

        while(true)
        {
            flag=0;
            prev=null;
            p1=r;
            p2=r.next;

            while(p2!=null)
            {
                if(p1.liftRatio < p2.liftRatio)
                {
                    flag=1;
                    p1.next=p2.next;
                    p2.next=p1;
                        if(prev==null)
                        {
                            prev=p2;
                            r=p2;
                        }
                        else
                        {
                            prev.next=p2;
                            prev=p2;
                        }
                    p2=p1.next;
                }
                else
                {
                    prev=p1;
                    p1=p2;
                    p2=p2.next;
                }
            }
            if(flag==0) break;
        }

    }//end: void sortRules()

    private static void getAssociationRules(int para1,int para2,String para3)
    {
    	dset=para3;
        int i,j;
        String line;
        Pineapple mem=null;
        Pineapple.Itemset itemset;
        System.out.println("\nsupport: "+para1+"%");
        System.out.println("confidence: "+para2+"%");
        System.out.println("dataset: "+dset);

        try
        {
            BufferedReader dataset=new BufferedReader(new FileReader(dset));
            line=dataset.readLine();
            items=line.length()/2;
            readHeader();
            mem=new Pineapple(items);
            do
            {
                trans++;
                for(i=0,j=0;j<items;j++,i+=2)
                {
                    if(line.charAt(i) == '1')
                    {
                        itemset=mem.search(j);
                        if(itemset == null)
                            mem.insert(j,k,1); else itemset.support++;
                    }
                }
            }while((line=dataset.readLine()) != null);
            dataset.close();
        }catch(IOException e){System.out.println(e.getMessage());System.exit(1);}

        minsup=(float)trans/100*para1;
        supportK1(mem);

        System.out.println("trans:"+trans);
        System.out.println("Items: "+items);
        System.out.println("--------------------------------------------------\n");
        mem.sort();
        print(mem);

        while(candidate(mem))
        {
            if(k > 2) prune(mem);
            support(mem);
            print(mem);
        }
        System.out.println("No more itemsets can be generated.");
        System.out.println("--------------------------------------------------\n");

        minconf=(float)1/100*para2;
        getRules(mem,para1,para2);

    }//end: void getAssociationRules(int,int,String)

    public static void main(String[] args)
    {
        switch(args.length)
        {
            case 3:
            getAssociationRules(Integer.valueOf(args[0]),Integer.valueOf(args[1]),args[2]);
            System.exit(0);

            default:
            System.out.println("apriori [min_support] [min_confidence] [dataset_file.ds]");
        }
    }//end: main()

}//end: class apriori
