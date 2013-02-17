public class Interper {
    static void interp(Stm s) {
        /* you write this part */
        interpStm(s,new Table("**Table-$-End**",0,(Table)null));
    }
    
    private static void printExpList(ExpList el,Table t){
        if(el instanceof PairExpList){
            Exp head = ((PairExpList)el).head;
            int result = interpExp(head,t).i;
            System.out.print(result+" ");
            printExpList(((PairExpList)el).tail,t);
        }
        else{
            Exp head = ((LastExpList)el).head;
            int result = interpExp(head,t).i;
            System.out.println(result);
        }
    } 

    private static Table interpStm(Stm s, Table t){
        Table updated;
        if(s instanceof CompoundStm)
            updated = interpStm(((CompoundStm)s).stm2,interpStm(((CompoundStm)s).stm1,t));
        else if (s instanceof AssignStm)
            updated = new Table(((AssignStm)s).id,(interpExp(((AssignStm)s).exp,t)).i,(interpExp(((AssignStm)s).exp,t)).t); 
        else if (s instanceof PrintStm){
            updated = t;
            printExpList(((PrintStm)s).exps,t);
        }
        else
            updated = t;
        return updated;
    }

        
    private static IntAndTable interpExp(Exp e, Table t) {
    	IntAndTable updated;
    	
    	if(e instanceof IdExp){
            try{
                updated = new IntAndTable(t.lookup(((IdExp)e).id),t);
    	    }
            catch(Exception err){
                updated = null;
                System.err.println(err.getMessage());
            }
        }
    	else if (e instanceof NumExp){
            updated = new IntAndTable(((NumExp)e).num,t);     
    	}
    	else if (e instanceof EseqExp){
    		updated = interpExp(((EseqExp)e).exp,interpStm(((EseqExp)e).stm,t));
    	}
    	else if (e instanceof OpExp){
            IntAndTable leftresult = interpExp(((OpExp)e).left,t);
            IntAndTable rightresult = interpExp(((OpExp)e).right,t);
            switch(((OpExp)e).oper){
                case 1:
                    updated = new IntAndTable(leftresult.i+rightresult.i,rightresult.t);
                    break;
                case 2:
                    updated = new IntAndTable(leftresult.i-rightresult.i,rightresult.t);
                    break;
                case 3:
                    updated = new IntAndTable(leftresult.i*rightresult.i,rightresult.t);
                    break;
                case 4:
                    updated = new IntAndTable(leftresult.i/rightresult.i,rightresult.t);
                    break;
                default:
                    updated = new IntAndTable(0,t);
                    break;
            }
    	}
    	else
    		updated = new IntAndTable(0,t);
        return updated;
    }
    
    private static int length(ExpList e, int count){
        if(e instanceof PairExpList){
            return length(((PairExpList)e).tail,count+1);
        }
        else
            return count+1;
    }

    private static int maxargs(Stm s) { 
        /* you write this part */
        if(s instanceof CompoundStm)
            return maxargs(((CompoundStm)s).stm1)>maxargs(((CompoundStm)s).stm2)?maxargs(((CompoundStm)s).stm1):maxargs(((CompoundStm)s).stm2);
        else if(!(s instanceof PrintStm))
            return 0;
        else
            return length(((PrintStm)s).exps,1); 
    }

    public static void main(String args[]) throws java.io.IOException {
        System.out.println(maxargs(prog.prog));
        interp(prog.prog);
    }
}

class Table {
    String id; int value; Table tail;
    Table(String i, int v, Table t) {id=i; value=v; tail=t;}
    
    int lookup(String key) throws Exception{
    	if(key.equals(id)){
    		return value;
    	}
    	else if (tail != null)
    		return tail.lookup(key);
    	else 
    		throw new Exception("Refering to a non existing variable.");
    }
}
    
class IntAndTable {int i; Table t; IntAndTable(int ii, Table tt) {i=ii; t=tt;}}
    

