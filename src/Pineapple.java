class Pineapple
{
    class Itemset     // 9 bytes
    {
        int code;
        byte k;
        int support;
        Itemset(int value1,byte value2,int value3)
        {
            code=value1;
            k=value2;
            support=value3;
        }
    }//end: class Itemset

    class Port      // 5 bytes
    {
        byte number;
        Slice path;
        Port next;
        Port(byte number,Slice path)
        {
            this.number=number;
            this.path=path;
        }
    }//end: class Port

    class Slice     // 6 bytes
    {
        Itemset data=null;
        Port ports=null;
        Slice next=null;
    }//end class Slice

    int csys;
    int count=0;
    Slice crust[];
    Slice linker=null;
    Slice tail=null;
    Pineapple(int csys)
    {
        crust= new Slice[csys];
        this.csys=csys;
        for(int i=0;i<csys;i++)
            crust[i]=null;
    }//end: Pineapple(int)

    void insert(int value1,byte value2,int value3)
    {
        int val=value1;
        Slice p2s; Port p2p;
        int way=val%csys; val/=csys;
        if(crust[way]==null)
        {
            p2s=new Slice();
            crust[way]=p2s;
        }
        else
        {
            p2s=crust[way];
        }

        while(val>0)
        {
            way=val%csys; val/=csys;
            p2p=p2s.ports;
            while(p2p!=null && p2p.number!=way)
            {
                p2p=p2p.next;
            }
            if(p2p==null)
            {
                p2p=new Port((byte)way,new Slice());
                p2p.next=p2s.ports;
                p2s.ports=p2p;
            }
            p2s=p2p.path;
        }
        p2s.data=new Itemset(value1,value2,value3);

        if(tail!=null)
            tail.next=p2s;
        tail=p2s;
        if(linker==null)
            linker=p2s;

        count++;

    }//end: void insert(int,byte,int)

    Itemset search(int val)
    {
        Slice p2s; Port p2p;
        int way=val%csys; val/=csys;
        if(crust[way]==null)
            return null;
            else
            p2s=crust[way];

        while(val>0)
        {
            way=val%csys; val/=csys;
            p2p=p2s.ports;
            while(p2p!=null && p2p.number!=way)
            {
                p2p=p2p.next;
            }
            if(p2p==null)
                return null;
                else
                p2s=p2p.path;
        }

        return p2s.data;
    }//end: Itemset search(int)

    Itemset fetch(int val)
    {
        Slice p2s; Port p2p;
        int way=val%csys; val/=csys;
        p2s=crust[way];

        while(val>0)
        {
            way=val%csys; val/=csys;
            p2p=p2s.ports;
            while(p2p.number!=way)
            {
                p2p=p2p.next;
            }
            p2s=p2p.path;
        }

        return p2s.data;
    }//end: Itemset fetch(int)

    void sort()
    {
        int flag;
        Slice prev,p1,p2;

        while(true)
        {
            flag=0;
            prev=null;
            p1=linker;
            p2=linker.next;

            while(p2!=null)
            {
                if(p1.data.code>p2.data.code)
                {
                    flag=1;
                    p1.next=p2.next;
                    p2.next=p1;
                        if(prev==null)
                        {
                            prev=p2;
                            linker=p2;
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

        p1=linker;
        while(p1.next!=null)
        {
            p1=p1.next;
        }
        tail=p1;

    }//end: void sort()

}//end: class Pineapple
