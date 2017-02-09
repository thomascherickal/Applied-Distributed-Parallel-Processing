package MPI;

/** Interface that specifies the MPI operations over the DPPEJ
 *
 */

public interface MPInterface {
    void All_To_One_Reduction(int dest, double buffer[], String operation);
    void All_To_All_Reduction(double buffer[], String operation);
    void One_To_All_Broadcast(double message, double buffer[], int sourceid);
    void All_To_All_Broadcast(double message, double buffer[]);
    void All_Reduction(double buffer[], String operation);
    void One_To_All_Personal(int source,double messages[], double buffer[]);
    void All_To_One_Personal(int dest, double messages[], double buffer[]);
    void All_To_All_Personal(double messages[], double buffer[]);
    void Prefix_Sums(double values[], double sums[]);  
}

File MPImplementation.java
package MPI;


import com.ibm.dthreads.DThread;
import com.ibm.dthreads.MP.ObjectMP;


/**  Class that implements the MPI interface specified by interface MPI
 *@author Thomas
 *
 */

public class MPImplementation implements MPInterface
{
    //process id
    int id;
    
    //number of processes
    int n;
    
    //message passer
    ObjectMP mp;  
  
    
    //debugging viewer
    GUI g;
    /**  Operations common to all mpi functions
     *
     */
    public MPImplementation()
    {
        id = DThread.getContext().getIdentity(); 
        n =  DThread.getContext().getNumberOfThreads();
        mp = DThread.getContext().getMP();
       // g = new GUI("", id);
        
    }
    /**
      * All to One Reduce MPI function complexity: 2 n
      * Performs a specified operation on all Dthread'sbuffers and puts the result 
      * in the destination Dthread's buffer
      * @param dest Destination DThread
      * @param buffer Values buffer
      * @param operation String describing the type of operation performed
      * Can be "SUM", "PROD", "MIN", "MAX"
      */
     public void All_To_One_Reduction(int dest, double buffer[], String operation)
     {

         double result = 0;
         int i;


         if (operation.compareToIgnoreCase("Sum") == 0)
         {
             result = 0;
             for (i = 0; i < n; i++)
                 result += buffer[i];                 
         }
         else if (operation.compareToIgnoreCase("Prod") == 0)
         {
             result = 1;
             for (i = 0; i < n; i++)
                 result *= buffer[i];                 
         }
         else if (operation.compareToIgnoreCase("Min") == 0)
         {
             result = buffer[0];
             for (i = 0; i < n; i++)
                 result = buffer[i] < result ? buffer[i]  : result;                 
         }
         
         else if (operation.compareToIgnoreCase("Max") == 0)
         {
             result = buffer[0];
             for (i = 0; i < n; i++)
                 result = buffer[i] > result ? buffer[i]  : result;                 
         }
	   if (id != dest) mp.put(result, id, dest, "");
         mp.barrier();
         if (id == dest) {
             for (i = 0; i < n; i++)
		{
			if (i == id) {
				buffer[i] = result;
				continue;
			}
                 buffer[i] = Double.parseDouble(mp.get(i, "") + "");
             }
         }

         
     }
     
    /**
     * All to All Reduce MPI Operation
     * Performs a specified operation on corresponding elements 
     * of all DThread buffers and puts the result in each
     * buffer
     * @param buffer values passed
     * @param operation String describing the type of operation performed
     * Can be "SUM", "PROD", "MIN", "MAX"
     */
    public void All_To_All_Reduction(double buffer[], String operation)
    {
         double result = 0;
         int i;


         
         if (operation.compareToIgnoreCase("Sum") == 0)
         {
             result = 0;
             for (i = 0; i < n; i++)
                 result += buffer[i];                 
         }
         else if (operation.compareToIgnoreCase("Prod") == 0)
         {
             result = 1;
             for (i = 0; i < n; i++)
                 result *= buffer[i];                 
         }
         else if (operation.compareToIgnoreCase("Min") == 0)
         {
             result = buffer[0];
             for (i = 0; i < n; i++)
                 result = buffer[i] < result ? buffer[i]  : result;                 
         }
         
         else if (operation.compareToIgnoreCase("Max") == 0)
         {
             result = buffer[0];
             for (i = 0; i < n; i++)
                 result = buffer[i] > result ? buffer[i]  : result;                 
         }
         for (i = 0; i < n; i++)
         {
            if (i == id) continue; 
		mp.put(result, id, i,"");
             
         }
         mp.barrier();
         for (i = 0 ; i < n; i++)
         {
            if (i == id) {
			buffer[i] = result; 
			continue;
		}
		buffer[i] = Double.parseDouble(mp.get(i, "" ) + "");
         }
         
    }
    
    /**
     * One to All Broadcast MPI function complexity: n
     * Broadcasts message to all DThreads
     * @param message value to broadcast
     * @param buffer return values in buffer[0]
     * @param sourceid source id of source DThread
     */
    public void One_To_All_Broadcast(double message, double buffer[], int sourceid )
    {
 
         int i;


         
         if (id == sourceid)
         {
             for (i = 0; i < n; i++) {
                 if (i == sourceid) continue;
                    mp.send(message, sourceid,i,  "");
             }
             
         }
         
         mp.barrier();
         buffer[0] = (sourceid == id) ? message :  Double.parseDouble(mp.receive(sourceid, "") + "");       
     }
    
    /**
     *  All to All Broadcast MPI function complexity: 2 n
     *  Each DThread broadcasts its own message to all DThreads
     *  and receives the result in buffer
     * @param message message to broadcast
     * @param buffer return value array
     */
    public void All_To_All_Broadcast(double message, double buffer[])
    {

         int i;

		//g = new GUI("", id);
         for (i = 0; i < n; i++)
	   {
		if (i == id) continue;
             mp.put(message,id, i, "");
	   }
         mp.barrier();
         for (i = 0; i < n; i++)
	   {
             buffer[i] = (i == id ? message : Double.parseDouble(mp.get(i, "") + ""));
		//g.addText(buffer[i] + "");
	   }	 
         
    }
    
    /**
     *  All Reduce MPI function complexity: 3 n
     *  Performs an all to one reduction followed by a 
     *  one to all broadcast of the results
     * @param buffer Value buffer
     * @param operation String describing the type of operation performed
     * Can be "SUM", "PROD", "MIN", "MAX"
     */
    public void All_Reduction(double buffer[], String operation)
    {
	  int root = 0;
         int i;
	
       All_To_One_Reduction(root, buffer, operation);
   if (id == root)
         {
             for (i = 0; i < n ; i++) {
			if (i == id) continue;
                  mp.put(buffer[i], id, i, "");
		}
         }
         mp.barrier();
	if (id == root) buffer[0] = buffer[id];
	else
	{
         buffer[0] = Double.parseDouble(mp.get(root, "") + "");
	}
    }
    
    /**
     *  One To All Personalized MPI function complexity : n
     *  Scatter operation. One DThread sends n messages to n DThreads
     * @param source source DThread
     * @param messages messages to scatter
     * @param buffer values returned in buffer[0]
     */
    public void One_To_All_Personal(int source,double messages[], double buffer[])
    {
		 


         int i;
         if (id == source)
         {
             for (i = 0; i < n ; i++) {
			if (i == id) continue;
                  mp.put(messages[i], id, i, "");
		}
         }
         mp.barrier();
	if (id == source) buffer[0] = messages[id];
	else
	{
         buffer[0] = Double.parseDouble(mp.get(source, "") + "");
	}
                  
    }
    
    /**
     *  All To One Personalized MPI function complexity: n
     *  Gather operation. Each DThread sends an individual message to one DThread
     * @param dest destination DThread
     * @param messages messages to gather
     * @param buffer return values array
     */
    public void All_To_One_Personal(int dest, double messages[], double buffer[])
    {

         int i;
		

         mp.put(messages[id], id, dest, "");
         mp.barrier();
         if (id == dest)
         {
             for (i = 0; i < n; i++) {
			if (i == id) {
				buffer[i] = messages[i];
				continue;
			}
                 buffer[i] = Double.parseDouble(mp.get(i, "") + "");
		}
         }
             
    }
    
    /**
     *  All To One Personalized MPI function complexity: n
     *  All Dthreads send individual and recieve individual messages from each DThread
     * @param messages messages to send
     * @param buffer Recieved values Buffer
     */
    
    public void All_To_All_Personal(double messages[], double buffer[])
    {
          
         int i;
        
         for (i = 0; i < n; i++)
         {
		if (i == id) continue;
             mp.put(messages[i], id, i, "");
         }
         mp.barrier();
         for (i = 0; i < n; i++)
         {
            if (id == i) buffer[i] = messages[id]; 
		else {
			buffer[i] = Double.parseDouble(mp.get(i, "") + "");
		}
         }
    }
    
    /**
     *  Prefix sums complexity:  2 n   
     *  Dthread i gets the Sum (0 to i) of Value at DThread i
     * @param values Values at each node
     * @param sums return sum value is in Sum[id]
     */
    public void Prefix_Sums(double values[], double sums[])
    {
          
         int i;
         for (i = id + 1; i < n; i++)
             mp.put(values[id], id, i, "");
         
         sums[id] = values[id];
         mp.barrier();
         
         for (i = 0; i < id; i++)
             sums[id] += Double.parseDouble(mp.get(i, "") + "");
                    
    }
}

